///usr/bin/env java --enable-preview --source 21 "$0" "$@"; exit $?
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Live-reload development server for teavm-react.
 *
 * Usage:  java DevServer.java [port]
 *         (default port: 8080)
 *
 * Features:
 *   - Serves the compiled webapp from teavm-react-demo/target/webapp
 *   - Watches all src/ directories for changes
 *   - Smart rebuild: only recompiles modules whose source actually changed
 *   - Fast path: HTML/CSS-only changes are copied directly (no Maven)
 *   - Uses Maven Daemon (mvnd) if available, falls back to mvn
 *   - Activates the 'dev' Maven profile (incremental TeaVM, no source maps)
 *   - Pushes reload events to the browser via SSE
 *   - Injects live-reload script into HTML responses automatically
 */
public class DevServer {

    // ── Configuration ──────────��───────────────────────────────────────
    static final String WEBAPP_DIR_REL   = "teavm-react-demo/target/webapp";
    static final String WEBAPP_SRC_REL   = "teavm-react-demo/src/main/webapp";
    static final long DEBOUNCE_MS        = 400;
    static final String SSE_PATH         = "/__dev/events";

    // Module source roots — order matters for dependency resolution
    static final String CORE_SRC   = "teavm-react-core/src";
    static final String KOTLIN_SRC = "teavm-react-kotlin/src";
    static final String DEMO_SRC   = "teavm-react-demo/src";

    // ── Live-reload script injected before </body> ─────────────────────
    static final String LIVE_RELOAD_SCRIPT = """
        <script>
        (function() {
            var retryDelay = 1000;
            function connect() {
                var es = new EventSource('/__dev/events');
                es.onmessage = function(e) {
                    if (e.data === 'reload') {
                        console.log('[dev] Reloading...');
                        location.reload();
                    } else if (e.data === 'compiling') {
                        if (!document.getElementById('__dev-overlay')) {
                            var d = document.createElement('div');
                            d.id = '__dev-overlay';
                            d.style.cssText = 'position:fixed;top:0;left:0;right:0;bottom:0;'
                                + 'background:rgba(0,0,0,0.35);z-index:99999;display:flex;'
                                + 'align-items:center;justify-content:center;';
                            d.innerHTML = '<div style="background:#222;color:#0f0;padding:24px 40px;'
                                + 'border-radius:10px;font:16px monospace">Recompiling...</div>';
                            document.body.appendChild(d);
                        }
                    }
                };
                es.onerror = function() {
                    es.close();
                    setTimeout(connect, retryDelay);
                };
                es.onopen = function() { retryDelay = 1000; };
            }
            connect();
        })();
        </script>
        """;

    // ── State ────────��────────────────────────────────��────────────────
    static Path projectDir;
    static Path webappDir;
    static Path webappSrcDir;
    static String mvnCmd; // "mvnd" or "mvn"
    static final List<HttpExchange> sseClients = new CopyOnWriteArrayList<>();
    static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    static volatile ScheduledFuture<?> pendingBuild;
    static volatile boolean building = false;
    static final AtomicLong buildGeneration = new AtomicLong(0);

    // Tracks which modules have dirty source since last build
    static volatile boolean coreChanged;
    static volatile boolean kotlinChanged;
    static volatile boolean demoJavaChanged;
    static volatile boolean demoStaticChanged; // HTML/CSS in webapp dir only

    // ── Main ─────────────���─────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].startsWith("-")) {
            System.out.println("Usage: java DevServer.java [port]");
            System.out.println("  Live-reload dev server for teavm-react (default port: 8080)");
            System.exit(0);
        }
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        projectDir  = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        webappDir   = projectDir.resolve(WEBAPP_DIR_REL);
        webappSrcDir = projectDir.resolve(WEBAPP_SRC_REL);

        // Detect Maven Daemon
        mvnCmd = detectMvnd();
        log("Using build tool: " + mvnCmd);

        if (!Files.isDirectory(webappDir)) {
            log("Webapp dir not found — running initial build...");
            runFullBuild();
        }
        if (!Files.isRegularFile(webappDir.resolve("js/classes.js"))) {
            log("ERROR: Build did not produce classes.js. Check Maven output above.");
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(SSE_PATH, DevServer::handleSSE);
        server.createContext("/",      DevServer::handleStatic);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        log("Dev server running at  http://localhost:" + port);
        log("Live-reload enabled. Watching for source changes...");

        startWatcher();
        Thread.currentThread().join();
    }

    // ── Maven Daemon detection ─────────────────────────────────────────
    static String detectMvnd() {
        try {
            Process p = new ProcessBuilder("mvnd", "--version")
                .redirectErrorStream(true).start();
            p.getInputStream().readAllBytes();
            if (p.waitFor() == 0) return "mvnd";
        } catch (Exception ignored) {}
        return "mvn";
    }

    // ── HTTP: static file serving with script injection ────────────────
    static void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        Path file = webappDir.resolve(path.substring(1)).normalize();
        if (!file.startsWith(webappDir) || !Files.isRegularFile(file)) {
            sendResponse(ex, 404, "text/plain", "Not found".getBytes());
            return;
        }

        String mime = guessMime(file.toString());
        byte[] body = Files.readAllBytes(file);

        if (mime.equals("text/html")) {
            String html = new String(body);
            int idx = html.lastIndexOf("</body>");
            if (idx >= 0) {
                html = html.substring(0, idx) + LIVE_RELOAD_SCRIPT + html.substring(idx);
            } else {
                html = html + LIVE_RELOAD_SCRIPT;
            }
            body = html.getBytes();
        }

        sendResponse(ex, 200, mime, body);
    }

    static void sendResponse(HttpExchange ex, int code, String mime, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", mime);
        ex.getResponseHeaders().set("Cache-Control", "no-cache, no-store");
        ex.sendResponseHeaders(code, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    static String guessMime(String path) {
        if (path.endsWith(".html"))  return "text/html";
        if (path.endsWith(".js"))    return "application/javascript";
        if (path.endsWith(".css"))   return "text/css";
        if (path.endsWith(".json"))  return "application/json";
        if (path.endsWith(".png"))   return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".svg"))   return "image/svg+xml";
        if (path.endsWith(".wasm"))  return "application/wasm";
        return "application/octet-stream";
    }

    // ── SSE: server-sent events for live reload ────────────────────────
    static void handleSSE(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "text/event-stream");
        ex.getResponseHeaders().set("Cache-Control", "no-cache");
        ex.getResponseHeaders().set("Connection", "keep-alive");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(200, 0);

        OutputStream os = ex.getResponseBody();
        os.write("data: connected\n\n".getBytes());
        os.flush();

        sseClients.add(ex);
        log("SSE client connected (" + sseClients.size() + " total)");
    }

    static void broadcastSSE(String data) {
        byte[] msg = ("data: " + data + "\n\n").getBytes();
        for (HttpExchange client : sseClients) {
            try {
                OutputStream os = client.getResponseBody();
                os.write(msg);
                os.flush();
            } catch (IOException e) {
                sseClients.remove(client);
            }
        }
    }

    // ── File watcher with module-aware change tracking ─────────────────
    static void startWatcher() {
        scheduler.submit(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                String[] watchDirs = { CORE_SRC, KOTLIN_SRC, DEMO_SRC };
                for (String dir : watchDirs) {
                    Path watchRoot = projectDir.resolve(dir);
                    if (!Files.isDirectory(watchRoot)) continue;
                    registerRecursive(watcher, watchRoot);
                }

                while (true) {
                    WatchKey key = watcher.take();
                    boolean relevant = false;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = ((Path) key.watchable()).resolve((Path) event.context());
                        String name = changed.getFileName().toString();
                        if (name.endsWith(".java") || name.endsWith(".kt") ||
                            name.endsWith(".html") || name.endsWith(".css")) {

                            String rel = projectDir.relativize(changed).toString();
                            log("Changed: " + rel);
                            relevant = true;
                            classifyChange(rel, name);
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE &&
                            Files.isDirectory(changed)) {
                            registerRecursive(watcher, changed);
                        }
                    }
                    key.reset();

                    if (relevant) scheduleBuild();
                }
            } catch (Exception e) {
                log("Watcher error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    static void classifyChange(String relPath, String fileName) {
        if (relPath.startsWith(CORE_SRC)) {
            coreChanged = true;
        } else if (relPath.startsWith(KOTLIN_SRC)) {
            kotlinChanged = true;
        } else if (relPath.startsWith(DEMO_SRC)) {
            if (fileName.endsWith(".html") || fileName.endsWith(".css")) {
                demoStaticChanged = true;
            } else {
                demoJavaChanged = true;
            }
        }
    }

    static void registerRecursive(WatchService watcher, Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // ── Build orchestration ────────────────────────────────────────────
    static synchronized void scheduleBuild() {
        if (pendingBuild != null && !pendingBuild.isDone()) {
            pendingBuild.cancel(false);
        }
        pendingBuild = scheduler.schedule(DevServer::runSmartBuild, DEBOUNCE_MS, TimeUnit.MILLISECONDS);
    }

    static void runSmartBuild() {
        if (building) {
            scheduleBuild();
            return;
        }
        building = true;
        long gen = buildGeneration.incrementAndGet();

        // Snapshot and reset dirty flags
        boolean needCore   = coreChanged;
        boolean needKotlin = kotlinChanged;
        boolean needDemo   = demoJavaChanged;
        boolean needStatic = demoStaticChanged;
        coreChanged = false;
        kotlinChanged = false;
        demoJavaChanged = false;
        demoStaticChanged = false;

        // Fast path: only static assets changed — just copy, no Maven needed
        if (!needCore && !needKotlin && !needDemo && needStatic) {
            log("──── Static-only change — fast copy (build #" + gen + ") ────");
            Instant start = Instant.now();
            boolean ok = copyStaticAssets();
            long ms = Duration.between(start, Instant.now()).toMillis();
            building = false;
            if (ok) {
                log("──── Fast copy done in " + ms + "ms ────");
                broadcastSSE("reload");
            } else {
                log("──── Fast copy failed, falling back to full rebuild ────");
                coreChanged = needCore; kotlinChanged = needKotlin;
                demoJavaChanged = true; demoStaticChanged = needStatic;
                scheduleBuild();
            }
            return;
        }

        broadcastSSE("compiling");
        Instant start = Instant.now();

        // Build the minimal set of Maven commands
        List<String> commands = buildCommandList(needCore, needKotlin, needDemo || needCore || needKotlin);
        log("──── Recompiling (build #" + gen + ") ────");

        boolean success = true;
        for (String cmd : commands) {
            if (!runCommand(cmd)) {
                success = false;
                break;
            }
        }

        // Also copy static assets if they changed alongside code
        if (success && needStatic) {
            copyStaticAssets();
        }

        long ms = Duration.between(start, Instant.now()).toMillis();
        building = false;

        if (success) {
            log("───�� Build #" + gen + " succeeded in " + ms + "ms ────");
            broadcastSSE("reload");
        } else {
            log("──── Build #" + gen + " FAILED after " + ms + "ms ────");
            broadcastSSE("error");
        }
    }

    /**
     * Builds the minimal set of Maven commands based on what changed.
     *
     * - core or kotlin changed → rebuild those modules, then re-run TeaVM on demo
     * - only demo Java changed → just re-run TeaVM on demo (skip core/kotlin)
     */
    static List<String> buildCommandList(boolean needCore, boolean needKotlin, boolean needDemo) {
        List<String> cmds = new ArrayList<>();
        String profile = " -Pdev";
        String parallel = " -T 1C";

        // Step 1: Rebuild library modules if needed
        if (needCore && needKotlin) {
            cmds.add(mvnCmd + " install -pl teavm-react-core,teavm-react-kotlin -DskipTests -q"
                      + parallel);
        } else if (needCore) {
            // Kotlin depends on core, so rebuild both
            cmds.add(mvnCmd + " install -pl teavm-react-core,teavm-react-kotlin -DskipTests -q"
                      + parallel);
        } else if (needKotlin) {
            cmds.add(mvnCmd + " install -pl teavm-react-kotlin -DskipTests -q");
        }

        // Step 2: Recompile demo with TeaVM (always needed when any code changed)
        if (needDemo) {
            cmds.add(mvnCmd + " process-classes -pl teavm-react-demo -q" + profile);
        }

        return cmds;
    }

    // ── Fast path: copy static assets without Maven ────────────────────
    static boolean copyStaticAssets() {
        try {
            if (!Files.isDirectory(webappSrcDir)) return false;
            Files.walkFileTree(webappSrcDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path rel = webappSrcDir.relativize(file);
                    Path dest = webappDir.resolve(rel);
                    Files.createDirectories(dest.getParent());
                    Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
            return true;
        } catch (IOException e) {
            log("Static copy error: " + e.getMessage());
            return false;
        }
    }

    // ── Full initial build ─���───────────────────────────────────────────
    static void runFullBuild() {
        runCommand(mvnCmd + " install -pl teavm-react-core,teavm-react-kotlin -DskipTests -q -T 1C");
        runCommand(mvnCmd + " process-classes -pl teavm-react-demo -q -Pdev");
    }

    // ── Command execution ───────────────────────────────────────��──────
    static boolean runCommand(String cmd) {
        try {
            log("$ " + cmd);
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd)
                .directory(projectDir.toFile())
                .redirectErrorStream(true);
            Process proc = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  " + line);
                }
            }

            int exit = proc.waitFor();
            if (exit != 0) {
                log("Command failed with exit code " + exit);
                return false;
            }
            return true;
        } catch (Exception e) {
            log("Build error: " + e.getMessage());
            return false;
        }
    }

    // ── Logging ────────────────────────────��───────────────────────────
    static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    static void log(String msg) {
        System.out.println("[" + LocalTime.now().format(TIME_FMT) + "] " + msg);
    }
}

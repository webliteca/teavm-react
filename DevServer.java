///usr/bin/env java --enable-preview --source 21 "$0" "$@"; exit $?
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
 *   - Auto-recompiles via Maven when source files change
 *   - Pushes reload events to the browser via SSE
 *   - Injects live-reload script into HTML responses automatically
 */
public class DevServer {

    // ── Configuration ──────────────────────────────────────────────────
    static final String WEBAPP_DIR_REL   = "teavm-react-demo/target/webapp";
    static final String[] WATCH_DIRS     = {
        "teavm-react-core/src",
        "teavm-react-kotlin/src",
        "teavm-react-demo/src"
    };
    static final String[] REBUILD_CMDS   = {
        // Only recompile the modules that changed, then re-run TeaVM
        "mvn install -pl teavm-react-core,teavm-react-kotlin -q -DskipTests",
        "mvn process-classes -pl teavm-react-demo -q"
    };
    static final long DEBOUNCE_MS        = 600;
    static final String SSE_PATH         = "/__dev/events";

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

    // ── State ──────────────────────────────────────────────────────────
    static Path projectDir;
    static Path webappDir;
    static final List<HttpExchange> sseClients = new CopyOnWriteArrayList<>();
    static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    static volatile ScheduledFuture<?> pendingBuild;
    static volatile boolean building = false;
    static final AtomicLong buildGeneration = new AtomicLong(0);

    // ── Main ───────────────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].startsWith("-")) {
            System.out.println("Usage: java DevServer.java [port]");
            System.out.println("  Live-reload dev server for teavm-react (default port: 8080)");
            System.exit(0);
        }
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        projectDir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        webappDir  = projectDir.resolve(WEBAPP_DIR_REL);

        if (!Files.isDirectory(webappDir)) {
            log("Webapp dir not found at " + webappDir);
            log("Running initial build...");
            runBuild();
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

        // Block main thread
        Thread.currentThread().join();
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

        // Inject live-reload script into HTML
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
        ex.sendResponseHeaders(200, 0); // chunked

        // Send initial heartbeat
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

    // ── File watcher ───────────────────────────────────────────────────
    static void startWatcher() {
        scheduler.submit(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                for (String dir : WATCH_DIRS) {
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
                            log("Changed: " + projectDir.relativize(changed));
                            relevant = true;
                        }
                        // Register new directories
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE &&
                            Files.isDirectory(changed)) {
                            registerRecursive(watcher, changed);
                        }
                    }
                    key.reset();

                    if (relevant) {
                        scheduleBuild();
                    }
                }
            } catch (Exception e) {
                log("Watcher error: " + e.getMessage());
                e.printStackTrace();
            }
        });
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
        pendingBuild = scheduler.schedule(DevServer::runBuildAsync, DEBOUNCE_MS, TimeUnit.MILLISECONDS);
    }

    static void runBuildAsync() {
        if (building) {
            // Another build already in progress; reschedule
            scheduleBuild();
            return;
        }
        building = true;
        long gen = buildGeneration.incrementAndGet();
        broadcastSSE("compiling");
        log("──── Recompiling (build #" + gen + ") ────");

        boolean success = runBuild();

        building = false;
        if (success) {
            log("──── Build #" + gen + " succeeded ────");
            broadcastSSE("reload");
        } else {
            log("──── Build #" + gen + " FAILED ────");
            broadcastSSE("error");
        }
    }

    static boolean runBuild() {
        for (String cmd : REBUILD_CMDS) {
            try {
                log("$ " + cmd);
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd)
                    .directory(projectDir.toFile())
                    .redirectErrorStream(true);
                Process proc = pb.start();

                // Stream output in real time
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
            } catch (Exception e) {
                log("Build error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    // ── Logging ────────────────────────────────────────────────────────
    static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    static void log(String msg) {
        System.out.println("[" + LocalTime.now().format(TIME_FMT) + "] " + msg);
    }
}

# TeaVM Core Essentials

Read this file when you need to know what Java standard library classes work under TeaVM, how to use TeaVM's built-in JSO types, how to configure the TeaVM Maven plugin, or why code that compiles with javac fails during TeaVM compilation. This supplements `teavm-interop.md` (which covers `@JSBody`, `@JSFunctor`, and `@JSProperty` in detail).

TeaVM version used by teavm-react: **0.13.1**

## Java Standard Library Support

TeaVM re-implements a subset of the Java standard library in JavaScript. Code that uses unsupported classes will fail during TeaVM compilation (not at runtime), with errors like `Method not found` or `Class not found`.

### Fully Supported

These work reliably and can be used freely in teavm-react components:

| Package | Classes / Notes |
|---------|----------------|
| `java.lang` | `String`, `StringBuilder`, `Math`, `Integer`, `Long`, `Float`, `Double`, `Boolean`, `Character`, `Byte`, `Short`, `Number`, `Comparable`, `Iterable`, `Runnable`, `Enum`, `System.arraycopy()`, `System.currentTimeMillis()`, exceptions (`RuntimeException`, `IllegalArgumentException`, `NullPointerException`, etc.) |
| `java.util` | `ArrayList`, `LinkedList`, `HashMap`, `HashSet`, `TreeMap`, `TreeSet`, `LinkedHashMap`, `LinkedHashSet`, `ArrayDeque`, `PriorityQueue`, `Collections`, `Arrays`, `Objects`, `Optional`, `OptionalInt`, `OptionalLong`, `OptionalDouble`, `Iterator`, `ListIterator`, `Comparator`, `Random`, `UUID`, `EnumMap`, `EnumSet`, `BitSet` |
| `java.util.function` | All functional interfaces: `Function`, `Consumer`, `Supplier`, `Predicate`, `BiFunction`, `BiConsumer`, `BiPredicate`, `UnaryOperator`, `BinaryOperator`, and primitive specializations (`IntFunction`, `IntConsumer`, etc.) |
| `java.util.stream` | `Stream`, `IntStream`, `LongStream`, `DoubleStream`, `Collectors` (most collectors work: `toList()`, `toSet()`, `toMap()`, `joining()`, `groupingBy()`, etc.) |
| `java.util.regex` | `Pattern`, `Matcher` (delegates to JavaScript's regex engine; most patterns work, but lookbehind and some Unicode categories may behave differently than HotSpot) |
| `java.io` | `Serializable` (marker only), `IOException`, `ByteArrayInputStream`, `ByteArrayOutputStream`, `StringReader`, `StringWriter`, `BufferedReader`, `BufferedWriter`, `PrintStream` (partial) |
| `java.math` | `BigInteger`, `BigDecimal` |
| `java.util.concurrent.atomic` | `AtomicInteger`, `AtomicLong`, `AtomicReference`, `AtomicBoolean` (single-threaded, but the API works) |

### Partially Supported

Use with caution. Some methods work, others throw at compile time or produce incorrect results:

| Package | What Works | What Doesn't |
|---------|------------|--------------|
| `java.time` | Basic `LocalDate`, `LocalTime`, `LocalDateTime`, `Instant`, `Duration`, `Period`, `ZoneOffset` creation and arithmetic | `DateTimeFormatter` (limited patterns), `ZonedDateTime` (limited zone support), zone database lookups |
| `java.text` | `DecimalFormat` (basic patterns) | `SimpleDateFormat` (use `java.time` or `@JSBody` with `Intl.DateTimeFormat`), `MessageFormat`, `Collator` |
| `java.util.concurrent` | `ConcurrentHashMap`, `CopyOnWriteArrayList`, atomic classes | `ExecutorService`, `ThreadPoolExecutor`, `CompletableFuture`, `ForkJoinPool`, `Semaphore`, `CountDownLatch` (no real threads exist) |

### Not Supported -- Will Fail at TeaVM Compile Time

| Category | Details |
|----------|---------|
| **Reflection** | `java.lang.reflect.*` -- `Class.forName()`, `Method.invoke()`, `Field.get()`, `Constructor.newInstance()`, `Proxy.newProxyInstance()`. This is the #1 reason third-party libraries fail. |
| **Networking** | `java.net.*` -- `URL`, `HttpURLConnection`, `Socket`. Use `@JSBody` with `fetch()` or `XMLHttpRequest` instead (see `teavm-interop.md`). |
| **Filesystem** | `java.io.File`, `java.io.FileInputStream`, `java.io.FileOutputStream`, `java.nio.file.*`. There is no filesystem in a browser. |
| **NIO channels** | `java.nio.channels.*` -- `SocketChannel`, `FileChannel`, `Selector`. |
| **JDBC** | `java.sql.*` -- no database drivers exist. Use `@JSBody` to call IndexedDB or a REST API. |
| **Crypto** | `javax.crypto.*`, most of `java.security.*`. Use `@JSBody` with the Web Crypto API (`window.crypto.subtle`). |
| **Desktop UI** | `java.awt.*`, `javax.swing.*`, `javafx.*` -- none available. |
| **Class loading** | `ClassLoader`, `ServiceLoader`, `java.lang.instrument.*`. |
| **Processes** | `ProcessBuilder`, `Runtime.exec()`. |

### Libraries That Will Never Work Under TeaVM

Do **not** suggest or use any of these. They all depend on reflection, class loading, or unsupported APIs:

- **JSON**: Jackson, Gson, Moshi, JSON-B -- parse JSON with `@JSBody` (`JSON.parse()`) instead
- **HTTP**: OkHttp, Apache HttpClient, Retrofit -- use `@JSBody` with `fetch()`
- **DI**: Spring, Guice, Dagger (at runtime; Dagger compile-time might partially work)
- **ORM**: Hibernate, JPA, JOOQ
- **Serialization**: Java Serialization (`ObjectOutputStream`), Kryo, Protocol Buffers (Java runtime)
- **Logging**: SLF4J, Log4j, Logback -- use `JsUtil.consoleLog()` or `@JSBody` with `console.*`
- **Testing**: JUnit, TestNG (run in JVM tests only, not in TeaVM-compiled code)

### Safe Alternatives for Common Tasks

| Task | Don't Use | Use Instead |
|------|-----------|-------------|
| Parse JSON | Jackson, Gson | `@JSBody(script = "return JSON.parse(str);")` returning `JSObject`, then `@JSProperty` getters |
| HTTP requests | OkHttp, HttpURLConnection | `@JSBody` with `fetch()` (see `teavm-interop.md` for complete pattern) |
| Log output | SLF4J, System.out.println | `JsUtil.consoleLog(msg)` or `JsUtil.consoleError(msg)` |
| Date formatting | SimpleDateFormat | `@JSBody` with `new Date(ms).toLocaleDateString()` or `Intl.DateTimeFormat` |
| Timer/interval | `java.util.Timer` | `JsUtil.setInterval(callback, ms)`, `JsUtil.setTimeout(callback, ms)` |
| Unique IDs | `UUID.randomUUID()` | Works in TeaVM -- `java.util.UUID` is supported. Or use `@JSBody` with `crypto.randomUUID()` |
| String formatting | `String.format()` | String concatenation or `StringBuilder`. `String.format()` has limited support in TeaVM. |

## org.teavm.jso Types Quick Reference

These are the core types from TeaVM's JavaScript Object (JSO) layer. Use them when interacting with JavaScript values that don't map to Java primitives or `String`.

### JSObject

Base interface for all JavaScript objects. Every TeaVM JS-interop interface extends this.

```java
import org.teavm.jso.JSObject;

// Your custom JS interfaces extend JSObject
public interface UserData extends JSObject {
    @JSProperty String getName();
    @JSProperty int getAge();
}
```

### JSArray

Typed JavaScript array. Use for passing arrays between Java and JS.

```java
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSString;

// Create
JSArray<JSString> arr = JSArray.create();
arr.push(JSString.valueOf("hello"));
arr.push(JSString.valueOf("world"));

// Read
int len = arr.getLength();
JSString first = arr.get(0);

// Convert from Java array
@JSBody(params = {"items"}, script = "return items;")
static native JSArray<JSObject> toJSArray(JSObject[] items);
```

### JSString, JSNumber, JSBoolean

Boxed JS primitives. Rarely needed since TeaVM auto-converts `String`, `int`/`double`, and `boolean` in `@JSBody` params and returns. Use when you need to store primitives in a `JSArray<T>` or pass them as `JSObject`.

```java
import org.teavm.jso.core.JSString;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSBoolean;

JSString str = JSString.valueOf("hello");
String javaStr = str.stringValue();

JSNumber num = JSNumber.valueOf(42);
double javaNum = num.doubleValue();

JSBoolean bool = JSBoolean.valueOf(true);
```

### JSMapLike

Object used as a string-keyed dictionary. Useful for dynamic property bags.

```java
import org.teavm.jso.core.JSMapLike;

@JSBody(script = "return {};")
static native JSMapLike<JSObject> createEmptyObject();

JSMapLike<JSString> map = ...;
map.set("key", JSString.valueOf("value"));
JSString val = map.get("key");
```

### JSArrayReader

Read-only view of a JS array. Returned by some TeaVM APIs. Same `get(i)` and `getLength()` methods as `JSArray` but no mutation.

### Type Casting Between JSO Types

TeaVM uses `JSObject` as the universal base. Cast with standard Java casts:

```java
JSObject obj = someApiCall();
JSArray<JSString> arr = (JSArray<JSString>) obj;
MyInterface typed = (MyInterface) obj;
```

These are unchecked casts (no `instanceof` in JS interop). If the underlying JS value doesn't match, you get undefined behavior, not a `ClassCastException`.

## Built-in Browser API Bindings

TeaVM ships typed bindings in `org.teavm.jso.*` packages. Use these instead of raw `@JSBody` when they exist.

### DOM (`org.teavm.jso.dom.html`)

```java
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

// Get the document
HTMLDocument doc = HTMLDocument.current();

// Query elements
HTMLElement el = doc.getElementById("myId");
String tag = el.getTagName();
el.setAttribute("class", "active");
String text = el.getInnerHTML();
el.setInnerHTML("<b>new content</b>");
```

Note: teavm-react users rarely touch the DOM directly. React manages the DOM. The main use is `HTMLDocument.current().getElementById("root")` in `main()`.

### Window and Navigator (`org.teavm.jso.browser`)

```java
import org.teavm.jso.browser.Window;
import org.teavm.jso.browser.Navigator;
import org.teavm.jso.browser.Location;

Window window = Window.current();
Location loc = window.getLocation();
String href = loc.getHref();
String hash = loc.getHash();

Navigator nav = window.getNavigator();
String userAgent = nav.getUserAgent();
```

### XMLHttpRequest (`org.teavm.jso.ajax`)

```java
import org.teavm.jso.ajax.XMLHttpRequest;

XMLHttpRequest xhr = XMLHttpRequest.create();
xhr.open("GET", "/api/data");
xhr.setOnReadyStateChange(function() {
    // Note: for teavm-react, prefer @JSBody with fetch() (see teavm-interop.md)
});
xhr.send();
```

Prefer `@JSBody` with `fetch()` over `XMLHttpRequest` for new code. The `fetch()` pattern in `teavm-interop.md` is cleaner and better suited to React's async model.

### Canvas (`org.teavm.jso.canvas`)

```java
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;

HTMLCanvasElement canvas = (HTMLCanvasElement) doc.getElementById("myCanvas");
CanvasRenderingContext2D ctx = canvas.getContext("2d");
ctx.setFillStyle("red");
ctx.fillRect(10, 10, 100, 50);
```

### WebSocket (`org.teavm.jso.websocket`)

```java
import org.teavm.jso.websocket.WebSocket;

WebSocket ws = WebSocket.create("wss://example.com/ws");
ws.onMessage(evt -> {
    String data = evt.getData().toString();
});
ws.onOpen(evt -> ws.send("hello"));
ws.onClose(evt -> { /* handle close */ });
```

### Typed Arrays (`org.teavm.jso.typedarrays`)

```java
import org.teavm.jso.typedarrays.Uint8Array;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Float32Array;

Uint8Array bytes = Uint8Array.create(256);
bytes.set(0, 42);
int val = bytes.get(0);
```

## TeaVM Maven Plugin Configuration

The `teavm-maven-plugin` compiles Java/Kotlin bytecode to JavaScript. It runs on class files produced by javac/kotlinc.

### Minimal Configuration

```xml
<plugin>
    <groupId>org.teavm</groupId>
    <artifactId>teavm-maven-plugin</artifactId>
    <version>0.13.1</version>
    <executions>
        <execution>
            <goals><goal>compile</goal></goals>
            <phase>process-classes</phase>
            <configuration>
                <mainClass>com.example.App</mainClass>
                <targetType>JAVASCRIPT</targetType>
                <targetDirectory>${project.build.directory}/webapp/js</targetDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### All Configuration Options

| Option | Values | Default | Notes |
|--------|--------|---------|-------|
| `mainClass` | Fully qualified class name | (required) | Must have `public static void main(String[])` |
| `targetType` | `JAVASCRIPT`, `WEBASSEMBLY`, `C` | `JAVASCRIPT` | Always use `JAVASCRIPT` for teavm-react |
| `targetDirectory` | Path | `${project.build.directory}/teavm` | Where `classes.js` is written |
| `minifying` | `true`/`false` | `true` | Minify output JS. Set `false` in dev for readability. |
| `debugInformationGenerated` | `true`/`false` | `false` | Generates `.teavmdbg` for IDE step-debugging |
| `sourceMapsGenerated` | `true`/`false` | `false` | Generates `.map` file for browser devtools |
| `incremental` | `true`/`false` | `false` | Faster recompilation in dev. May cache stale data; disable for prod. |
| `stopOnErrors` | `true`/`false` | `true` | Fail the build on errors. Keep `true`. |
| `optimizationLevel` | `SIMPLE`, `ADVANCED`, `FULL` | `SIMPLE` | `ADVANCED` or `FULL` for production -- smaller output, slower compile |
| `targetFileName` | String | `classes.js` | Output filename. The HTML shell `<script>` must match this. |
| `entryPointName` | String | `main` | JS function name. Called as `main([])` from HTML. |
| `properties` | Map | empty | Key-value pairs available via `System.getProperty()` at compile time |

### Dev vs Production Profiles

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <teavm.minifying>false</teavm.minifying>
            <teavm.sourceMaps>true</teavm.sourceMaps>
            <teavm.incremental>true</teavm.incremental>
            <teavm.optimization>SIMPLE</teavm.optimization>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <teavm.minifying>true</teavm.minifying>
            <teavm.sourceMaps>false</teavm.sourceMaps>
            <teavm.incremental>false</teavm.incremental>
            <teavm.optimization>FULL</teavm.optimization>
        </properties>
    </profile>
</profiles>
```

### Required Dependencies

Every module compiled by TeaVM needs these in `pom.xml`:

```xml
<dependency>
    <groupId>org.teavm</groupId>
    <artifactId>teavm-classlib</artifactId>
    <version>0.13.1</version>
</dependency>
<dependency>
    <groupId>org.teavm</groupId>
    <artifactId>teavm-jso</artifactId>
    <version>0.13.1</version>
</dependency>
<dependency>
    <groupId>org.teavm</groupId>
    <artifactId>teavm-jso-apis</artifactId>
    <version>0.13.1</version>
</dependency>
```

`teavm-jso` provides the annotation types (`@JSBody`, etc.). `teavm-jso-apis` provides built-in browser bindings (`HTMLDocument`, `Window`, etc.). `teavm-classlib` provides the Java standard library emulation.

## Common "Compiles in Java, Fails in TeaVM" Patterns

These pass `javac` but produce errors during the `teavm-maven-plugin:compile` phase:

### 1. Reflection

```java
// FAILS: TeaVM has no reflection support
Class<?> clazz = Class.forName("com.example.MyClass");
Object obj = clazz.getDeclaredConstructor().newInstance();
Method m = clazz.getMethod("doStuff");
m.invoke(obj);
```

Fix: use direct instantiation or a factory pattern. If you need dynamic dispatch, use an interface and a `switch` or `Map<String, Supplier<MyInterface>>`.

### 2. Unsupported I/O

```java
// FAILS: no filesystem
File f = new File("data.json");
String content = new String(Files.readAllBytes(f.toPath()));

// FIX: use fetch() for remote data, or embed as a constant
@JSBody(params = {"url", "cb"}, script =
    "fetch(url).then(function(r){return r.text();}).then(cb);")
static native void loadText(String url, TextCallback cb);
```

### 3. Thread-Based Concurrency

```java
// FAILS: no real threads
ExecutorService pool = Executors.newFixedThreadPool(4);
Future<String> result = pool.submit(() -> fetchData());
String data = result.get(); // blocks -- impossible in browser

// FIX: use callbacks or Kotlin coroutines (which map to JS async patterns)
```

### 4. String.format() Edge Cases

```java
// MAY FAIL or produce wrong output for complex format strings
String s = String.format("%.2f %tF", 3.14, date);

// FIX: use concatenation or StringBuilder
String s = Math.round(val * 100.0) / 100.0 + "";
```

### 5. Lambda Serialization

```java
// FAILS: serializable lambdas use reflection
Comparator<String> c = (Comparator<String> & Serializable) String::compareTo;

// FIX: just use a normal lambda
Comparator<String> c = String::compareTo;
```

### 6. Service Loader

```java
// FAILS: no META-INF/services support at runtime
ServiceLoader<MyPlugin> plugins = ServiceLoader.load(MyPlugin.class);

// FIX: explicit registration
List<MyPlugin> plugins = List.of(new PluginA(), new PluginB());
```

## Key Takeaways

1. **Stick to `java.util` collections, `java.util.function`, and `java.util.stream`** -- these are well-supported and cover most application logic.
2. **No reflection, no filesystem, no real networking, no threads.** Browser APIs are accessed through `@JSBody` and JSO interfaces.
3. **If a third-party library uses reflection, it won't work.** Check before suggesting any dependency.
4. **Use TeaVM's built-in browser bindings** (`org.teavm.jso.dom.html`, `org.teavm.jso.browser`, etc.) instead of writing raw `@JSBody` for common browser APIs.
5. **For JSON, HTTP, timers, and storage**, see the patterns in `teavm-interop.md`.
6. **When in doubt about stdlib support**, simplify -- plain collections, simple string ops, and direct JS interop via `@JSBody` always work.

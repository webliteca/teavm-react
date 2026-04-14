# TeaVM Interop Reference

Read this file when you need to call browser APIs directly, extend the teavm-react library, or understand how the Java-to-JavaScript bridge works. Most users writing components do not need any of this -- the library wraps React fully.

## When You Need Interop

- Calling `fetch()`, `localStorage`, `WebSocket`, or other browser APIs
- Calling `event.preventDefault()` or `event.stopPropagation()`
- Creating custom React hooks that delegate to JS
- Integrating third-party JS libraries

## @JSBody -- Embedding JavaScript

`@JSBody` lets you write inline JavaScript in a Java method. The method must be `static native`.

### Basic Syntax

```java
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

@JSBody(params = {"msg"}, script = "console.log(msg);")
public static native void log(String msg);

@JSBody(params = {"a", "b"}, script = "return a + b;")
public static native int add(int a, int b);

@JSBody(params = {}, script = "return window.location.href;")
public static native String currentUrl();
```

### Parameter Mapping

| Java Type | JS Type | Notes |
|-----------|---------|-------|
| `int` | `number` | Integer values |
| `double` | `number` | Floating point |
| `boolean` | `boolean` | |
| `String` | `string` | Automatic conversion |
| `JSObject` | `object` | Passed by reference |
| `JSObject[]` | `Array` | |

### No ES6 Rule

TeaVM's JavaScript parser does **not** support ES6+ syntax in `@JSBody` scripts. This is the most common interop mistake.

#### What Breaks

```java
// BROKEN: Arrow function
@JSBody(params = {"arr"}, script = "return arr.map(x => x * 2);")

// BROKEN: Template literal
@JSBody(params = {"name"}, script = "return `Hello ${name}`;")

// BROKEN: Spread operator
@JSBody(params = {"type", "props", "children"},
    script = "return React.createElement(type, props, ...children);")

// BROKEN: let/const
@JSBody(params = {}, script = "let x = 5; return x;")

// BROKEN: Destructuring
@JSBody(params = {"obj"}, script = "var {a, b} = obj; return a;")
```

#### The Workaround -- ES5 Only

```java
// Use function() instead of =>
@JSBody(params = {"arr"},
    script = "return arr.map(function(x) { return x * 2; });")

// Use string concatenation instead of template literals
@JSBody(params = {"name"},
    script = "return 'Hello ' + name;")

// Use .apply/.concat instead of spread
@JSBody(params = {"type", "props", "children"},
    script = "return React.createElement.apply(null, [type, props].concat(children));")

// Use var instead of let/const
@JSBody(params = {}, script = "var x = 5; return x;")
```

### Multi-Statement Scripts

Use string concatenation for readability:

```java
@JSBody(params = {"url"}, script =
    "var xhr = new XMLHttpRequest();" +
    "xhr.open('GET', url, false);" +
    "xhr.send();" +
    "return xhr.responseText;")
public static native String syncGet(String url);
```

### Void Methods

Omit `return` for void methods:

```java
// Note: preventDefault() and stopPropagation() are now available directly
// on SyntheticEvent, so you rarely need @JSBody for these:
//   e.preventDefault();
//   e.stopPropagation();
// But the pattern works for any void JS operation:
@JSBody(params = {"event"}, script = "event.preventDefault();")
public static native void preventDefault(JSObject event);
```

## @JSFunctor -- Passing Java Lambdas as JS Functions

`@JSFunctor` marks a single-method interface so TeaVM passes instances as raw JavaScript functions instead of wrapped Java objects.

### How It Works

```java
@JSFunctor
public interface EventHandler extends JSObject {
    void handleEvent(SyntheticEvent event);
}
```

When you pass an `EventHandler` lambda through a `@JSBody`-annotated method or a dedicated setter like `React.setOnClick()`, TeaVM generates a plain JS function. Without `@JSFunctor`, TeaVM wraps the object in a Java-style proxy that React cannot call.

### The Event Handler Trap

This is why you must use dedicated setters for event handlers:

```java
// CORRECT: React.setOnClick uses @JSBody and expects a @JSFunctor
React.setOnClick(props, e -> doSomething());

// WRONG: setProperty wraps the handler in a Java object
React.setProperty(props, "onClick", (EventHandler) e -> doSomething());
// Result: onClick receives [object Object], not a callable function
```

### Existing @JSFunctor Interfaces

| Interface | Method | Used By |
|-----------|--------|---------|
| `EventHandler` | `handleEvent(SyntheticEvent)` | onClick, onMouseDown/Up/Enter/Leave |
| `ChangeEventHandler` | `handleEvent(ChangeEvent)` | onChange |
| `KeyboardEventHandler` | `handleEvent(KeyboardEvent)` | onKeyDown, onKeyUp |
| `FocusEventHandler` | `handleEvent(FocusEvent)` | onFocus, onBlur |
| `SubmitEventHandler` | `handleEvent(SubmitEvent)` | onSubmit |
| `EffectCallback` | `run()` | useEffect |
| `VoidCallback` | (no explicit method) | cleanup functions, setInterval/setTimeout |
| `MemoFactory` | `create()` | useMemo |
| `RenderFunction` | (render method) | wrapComponent |

### Creating Your Own @JSFunctor

```java
@JSFunctor
public interface StringCallback extends JSObject {
    void call(String value);
}

// Use in @JSBody
@JSBody(params = {"callback"}, script = "callback('hello from JS');")
public static native void callWithString(StringCallback callback);

// Usage
callWithString(value -> JsUtil.consoleLog("Received: " + value));
```

## @JSProperty -- Interface Property Access

`@JSProperty` maps getter/setter methods on a `JSObject` interface to JavaScript property access. Used for typed event objects.

```java
public interface KeyboardEvent extends SyntheticEvent {
    @JSProperty String getKey();       // reads event.key
    @JSProperty boolean getCtrlKey();  // reads event.ctrlKey
    // getTarget() inherited from SyntheticEvent → returns EventTarget
}
```

Naming convention: `getXyz()` reads property `xyz`, `setXyz(value)` writes it. TeaVM strips the `get`/`set` prefix and lowercases the first letter.

### Defining Custom JS Object Interfaces

```java
public interface GeoPosition extends JSObject {
    @JSProperty double getLatitude();
    @JSProperty double getLongitude();
    @JSProperty double getAccuracy();
}
```

## JsUtil Helper Methods

`ca.weblite.teavmreact.core.JsUtil` provides commonly needed browser APIs:

```java
// Timers
int id = JsUtil.setInterval(callback, 1000);  // returns interval ID
JsUtil.clearInterval(id);
int id = JsUtil.setTimeout(callback, 500);    // returns timeout ID
JsUtil.clearTimeout(id);

// Console
JsUtil.consoleLog("message");     // console.log(string)
JsUtil.consoleLog(jsObject);      // console.log(object)
JsUtil.consoleError("error msg"); // console.error(string)

// Alert
JsUtil.alert("Hello!");           // window.alert()
```

`VoidCallback` is the callback type for timers:

```java
VoidCallback tick = () -> count.updateInt(n -> n + 1);
int id = JsUtil.setInterval(tick, 1000);
```

## Complete Example: Wrapping Browser fetch()

This pattern is used in the photostream app's `HttpClient` class.

### Step 1: Define Callback Interfaces

```java
@JSFunctor
public interface ResponseCallback extends JSObject {
    void onResponse(JSObject response);
}

@JSFunctor
public interface ErrorCallback extends JSObject {
    void onError(JSObject error);
}

@JSFunctor
public interface TextCallback extends JSObject {
    void onText(String text);
}
```

### Step 2: Write @JSBody Bridge Methods

```java
@JSBody(params = {"url", "onSuccess", "onError"}, script =
    "fetch(url)" +
    ".then(function(r) { return r.text(); })" +
    ".then(function(text) { onSuccess(text); })" +
    ".catch(function(err) { onError(err); });")
private static native void fetchText(
    String url, TextCallback onSuccess, ErrorCallback onError);
```

Note the ES5 `function()` syntax -- not arrow functions.

### Step 3: Create a Java-Friendly Wrapper

```java
public class HttpClient {
    public static void get(String url,
                           TextCallback onSuccess,
                           ErrorCallback onError) {
        fetchText(url, onSuccess, onError);
    }
}
```

### Step 4: Use in a Component

```java
static ReactElement dataLoader(JSObject props) {
    StateHandle<String> data = Hooks.useState("Loading...");
    StateHandle<String> error = Hooks.useState("");

    Hooks.useEffect(() -> {
        HttpClient.get(
            "https://api.example.com/data",
            text -> data.setString(text),
            err -> error.setString("Failed to load")
        );
        return null;
    }, Hooks.deps());

    if (!error.getString().isEmpty()) {
        return p("Error: " + error.getString());
    }
    return pre(data.getString());
}
```

## Working with localStorage

```java
@JSBody(params = {"key", "value"},
    script = "localStorage.setItem(key, value);")
public static native void setItem(String key, String value);

@JSBody(params = {"key"},
    script = "var v = localStorage.getItem(key); return v !== null ? v : '';")
public static native String getItem(String key);

@JSBody(params = {"key"}, script = "localStorage.removeItem(key);")
public static native void removeItem(String key);
```

## Key Takeaways

1. **Most users never write @JSBody.** The library wraps React 18 completely.
2. **Always use ES5 syntax** in @JSBody scripts -- no arrows, no template literals, no spread, no let/const, no destructuring.
3. **Always use @JSFunctor** on callback interfaces that cross the Java-JS boundary.
4. **Use dedicated setters** (like `React.setOnClick`) for event handlers -- never `React.setProperty` with a callback value.
5. **TeaVM has no reflection.** You cannot use Jackson, Gson, or any reflection-based serialization. Parse JSON manually with @JSBody.

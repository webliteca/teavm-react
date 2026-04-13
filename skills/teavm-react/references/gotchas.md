# Gotchas Reference

Read this file to avoid the most common mistakes when building teavm-react applications. Each gotcha includes the symptom, cause, and fix.

## 1. Rules of Hooks -- Conditional Hook Calls

**Symptom:** Component renders correctly at first, then state gets mixed up or React throws "Rendered more hooks than during the previous render."

**Cause:** Hooks called inside `if`, `for`, `switch`, or early-return blocks.

```java
// WRONG: hook inside conditional
static ReactElement myComp(JSObject props) {
    StateHandle<Boolean> loggedIn = Hooks.useState(false);
    if (loggedIn.getBool()) {
        StateHandle<String> name = Hooks.useState(""); // BREAKS
    }
    // ...
}
```

**Fix:** Always call all hooks at the top level, unconditionally, in the same order.

```java
// CORRECT: all hooks at top, use values conditionally
static ReactElement myComp(JSObject props) {
    StateHandle<Boolean> loggedIn = Hooks.useState(false);
    StateHandle<String> name = Hooks.useState("");
    // Use loggedIn.getBool() in the return, not around hooks
}
```

In `ReactView`, declare hooks as **field initializers** -- they naturally run in order:

```java
public class MyView extends ReactView {
    private final StateHandle<Boolean> loggedIn = Hooks.useState(false);
    private final StateHandle<String> name = Hooks.useState("");
    // ...
}
```

## 2. No ES6 in @JSBody

**Symptom:** Maven build fails with a TeaVM parse error in a `@JSBody` script.

**Cause:** Used arrow functions (`=>`), template literals, spread (`...`), `let`/`const`, or destructuring.

```java
// BROKEN
@JSBody(params = {"arr"}, script = "return arr.map(x => x * 2);")
```

**Fix:** Use ES5 syntax only.

```java
// FIXED
@JSBody(params = {"arr"}, script = "return arr.map(function(x) { return x * 2; });")
```

See `references/teavm-interop.md` for a complete list of what breaks and the workarounds.

## 3. Event Handlers via setProperty

**Symptom:** Click handler or onChange never fires. No error in console.

**Cause:** Passed an event handler through `React.setProperty()` instead of a dedicated setter.

```java
// WRONG -- handler is wrapped as a Java object, not a JS function
React.setProperty(props, "onClick", (EventHandler) e -> doSomething());
```

**Fix:** Use `React.setOnClick()` or the builder's `.onClick()` method.

```java
// CORRECT
React.setOnClick(props, e -> doSomething());
// Or with builder
button("Click").onClick(e -> doSomething()).build();
```

## 4. HTML Shell Script Order

**Symptom:** "React is not defined" error in the browser console.

**Cause:** `classes.js` loaded before the React CDN scripts.

**Fix:** In your HTML, React scripts must come first:

```html
<!-- 1. React CDN -->
<script src="https://unpkg.com/react@18/umd/react.development.js" crossorigin></script>
<script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js" crossorigin></script>
<!-- 2. Div for mounting -->
<div id="root"></div>
<!-- 3. TeaVM output -->
<script src="js/classes.js"></script>
<!-- 4. Entry point -->
<script>main([]);</script>
```

## 5. TeaVM Limited Stdlib

**Symptom:** `ClassNotFoundException` or `NoSuchMethodError` at TeaVM compile time for common Java classes.

**Cause:** Tried to use classes not supported by TeaVM: `java.nio` channels, `java.net`, reflection, or libraries that depend on them (Jackson, Gson, OkHttp, Retrofit).

**Fix:** Use TeaVM JSO (`@JSBody`) for browser APIs like `fetch`, `XMLHttpRequest`, `localStorage`. For JSON, parse manually with `@JSBody`. See `references/teavm-interop.md`.

## 6. Stale Closures with Direct State Access

**Symptom:** Counter increments by 1 no matter how fast you click, or state updates seem to be lost.

**Cause:** Used `getInt()` inside a closure that captured a stale value.

```java
// WRONG -- captures current value, stale in rapid clicks
button("Inc").onClick(e -> count.setInt(count.getInt() + 1)).build();
```

**Fix:** Use functional updaters.

```java
// CORRECT -- always uses the latest value
button("Inc").onClick(e -> count.updateInt(n -> n + 1)).build();
```

In Kotlin DSL, `count++` works correctly because the delegate calls `setInt` synchronously. For complex updates, use the delegate's underlying `updateInt`.

## 7. ReactView Subclasses Re-instantiated Every Render

**Symptom:** Plain mutable fields on a `ReactView` subclass reset to their initial values on every render.

**Cause:** The `ViewFactory` creates a new instance of the `ReactView` subclass on each render. State must come from hooks.

```java
// WRONG -- resets to 0 every render
public class Bad extends ReactView {
    private int clickCount = 0;
    @Override protected ReactElement render() {
        return button("Clicks: " + clickCount)
            .onClick(e -> clickCount++).build(); // never advances past 1
    }
}
```

**Fix:** Use `Hooks.useState` as a field initializer.

```java
// CORRECT
public class Good extends ReactView {
    private final StateHandle<Integer> clickCount = Hooks.useState(0);
    @Override protected ReactElement render() {
        return button("Clicks: " + clickCount.getInt())
            .onClick(e -> clickCount.updateInt(n -> n + 1)).build();
    }
}
```

## 8. Mixing APIs in One Component

**Symptom:** Confusing type errors, unexpected nesting, or elements not rendering.

**Cause:** Combined `Html.div(...)` (Functional), `Div.create()` (Builder DSL), and/or Kotlin `div { }` in the same component.

**Fix:** Pick one approach per file. The four approaches produce `ReactElement` in different ways and should not be interleaved.

## 9. Using java.util.Timer Instead of JsUtil.setInterval

**Symptom:** Timer never fires, or `UnsupportedOperationException` at runtime.

**Cause:** `java.util.Timer` uses threads, which are not available in the browser. TeaVM's classlib does not fully support `java.util.Timer`.

**Fix:** Use `JsUtil.setInterval` or `JsUtil.setTimeout`.

```java
// WRONG
new java.util.Timer().schedule(new TimerTask() { ... }, 1000);

// CORRECT
int id = JsUtil.setInterval(() -> { /* tick */ }, 1000);
// Later: JsUtil.clearInterval(id);
```

## 10. Forgetting .build() on ElementBuilder

**Symptom:** Compile error ("expected ReactElement, got ElementBuilder") or element silently missing from output.

**Cause:** `Html.button()`, `Html.input()`, `Html.a()`, `Html.textarea()`, `Html.select()`, and `Html.img()` return `ElementBuilder`, not `ReactElement`. You must call `.build()` to finalize.

```java
// WRONG -- returns ElementBuilder, not ReactElement
return div(button("Click me"));  // compile error

// CORRECT
return div(button("Click me").build());

// With children
return div(button("Click me").build(child1, child2));
```

The same applies to `DomBuilder` subclasses -- always call `.build()` to produce the `ReactElement`.

## 11. Using == Instead of .equals() for String State

**Symptom:** String comparison on state values always returns false, even when the strings look identical.

**Cause:** `getString()` may return different `String` object references across renders. Using `==` compares references, not content.

```java
// WRONG
if (theme.getString() == "dark") { ... }

// CORRECT
if ("dark".equals(theme.getString())) { ... }
```

In Kotlin, `==` calls `.equals()` automatically, so this is only a Java issue.

## 12. Missing key Prop in List Rendering

**Symptom:** List items re-render incorrectly when added/removed/reordered. Focus jumps, animations break, or state leaks between items.

**Cause:** React needs a stable `key` prop to track list items across renders.

```java
// WRONG -- no keys
items.stream().map(item -> li(item.name)).toArray(ReactElement[]::new);

// CORRECT -- unique key per item
items.stream().map(item ->
    Li.create().key(item.id).text(item.name).build()
).toArray(ReactElement[]::new);
```

In Kotlin, `li` accepts a `key` parameter:

```kotlin
ul {
    items.forEach { item ->
        li(key = item.id) { +item.name }
    }
}
```

## 13. Using System.out.println for Debugging

**Symptom:** No output visible anywhere, or output goes to the Java console but not the browser.

**Cause:** TeaVM may not map `System.out.println` to `console.log` in all configurations.

**Fix:** Use `JsUtil.consoleLog()` for browser console output.

```java
// Instead of
System.out.println("Debug: " + value);

// Use
JsUtil.consoleLog("Debug: " + value);
```

## 14. Forgetting to Install Parent POM

**Symptom:** `mvn process-classes -pl my-module` fails with "Could not find artifact ca.weblite:teavm-react-parent" or unresolved `${teavm.version}`.

**Cause:** Child modules inherit from the parent POM. Maven needs the parent installed in the local repo first.

**Fix:**

```bash
mvn install -N   # install parent POM only (non-recursive)
mvn process-classes -pl my-module
```

## 15. textarea and select Return ElementBuilder

**Symptom:** Trying to use `Html.textarea()` or `Html.select()` as a `ReactElement` directly causes a compile error.

**Cause:** Like `button()` and `input()`, these methods return `ElementBuilder` and require `.build()`.

```java
// WRONG
return div(textarea());

// CORRECT
return div(
    textarea().value(text.getString())
        .onChange(e -> text.setString(e.getTarget().getValue()))
        .rows(5)
        .build()
);
```

## 16. collectAsState Called Outside Render

**Symptom:** "Rendered more hooks than during the previous render" or hooks order violation.

**Cause:** `Flow.collectAsState()` calls `Hooks.useState` and `Hooks.useEffect` internally. It must be called during render (inside the `fc { }` body), not inside `effect` or `launchedEffect`.

```kotlin
// WRONG -- inside effect
val MyComp = fc("MyComp") {
    effectOnce {
        val seconds by myFlow.collectAsState(0)  // BREAKS
    }
    // ...
}

// CORRECT -- in fc body
val MyComp = fc("MyComp") {
    val seconds by myFlow.collectAsState(0)  // hooks called during render
    div { +"Seconds: $seconds" }
}
```

## 17. StringListStateDelegate and Null Characters

**Symptom:** List items are merged or split unexpectedly when using `stateList`.

**Cause:** `StringListStateDelegate` uses a null character (`\0`) as an internal separator via `encodeStringList` / `decodeStringList`. Strings containing `\0` corrupt the encoding.

**Fix:** Never store strings containing the null character (`\0`) in a `stateList`. This is extremely rare in practice but worth knowing.

## 18. Using Removed JSObject-Based Methods

**Symptom:** `cannot find symbol` for `StateHandle.get()`, `StateHandle.set()`, `RefHandle.raw()`, `RefHandle.getCurrent()`, `RefHandle.setCurrent()`, `ReactContext.jsContext()`, `ReactContext.provider()`, or `Hooks.useState(JSObject)`.

**Cause:** These generic JSObject-based methods were removed to reduce abstraction leakage. The library now provides typed methods only.

**Fix:** Use the typed equivalents:

```java
// StateHandle: use typed getters/setters
count.getInt();       // instead of (int) count.get()
count.setInt(5);      // instead of count.set(React.intToJS(5))

// RefHandle: use typed getters/setters
ref.getCurrentString();       // instead of React.jsToString(ref.getCurrent())
ref.setCurrentInt(42);        // instead of ref.setCurrent(React.intToJS(42))

// ReactContext: use typed create/provide/use
ReactContext.create("light"); // instead of ReactContext.create(React.stringToJS("light"))
ctx.useString();              // instead of React.jsToString(Hooks.useContext(ctx.jsContext()))
ctx.provide("dark", child);   // instead of ctx.provide(React.stringToJS("dark"), child)
```

In Kotlin, use `refInt()`/`refString()` instead of `ref()`, and typed state delegates instead of `state(null as JSObject?)`.

## 19. Forgetting to Wrap Component for Rendering

**Symptom:** Passing a raw render function to `ReactDOM.createRoot().render()` produces an error or blank page.

**Cause:** React expects a component (JSObject) or ReactElement, not a raw Java method reference.

```java
// WRONG
root.render(App::render);  // raw method reference

// CORRECT -- wrap first, then create element
JSObject App = React.wrapComponent(AppClass::render, "App");
root.render(Html.component(App));
```

In Kotlin:

```kotlin
val App = fc("App") { /* ... */ }
root.render(component(App))
```

# Hooks Reference

Read this file when you need the exact signature, behavior, or usage pattern for any React hook in teavm-react. Covers all hooks in the `Hooks` class (Java) and their Kotlin DSL equivalents.

## Rules of Hooks

These rules are identical to JavaScript React and are enforced at runtime:

1. Call hooks at the **top level** of render functions only -- never inside `if`, `for`, `switch`, or nested lambdas.
2. Call hooks in the **same order** every render.
3. In `ReactView` subclasses, declare hooks as **field initializers** to guarantee ordering.

Violating these rules causes React to mix up state between hooks, producing silent corruption.

## useState

Creates a reactive state variable. When the value changes via `set*`, the component re-renders.

### Signatures (Java)

```java
StateHandle<Integer>  Hooks.useState(int initial)
StateHandle<String>   Hooks.useState(String initial)
StateHandle<Boolean>  Hooks.useState(boolean initial)
StateHandle<Double>   Hooks.useState(double initial)
```

### StateHandle Methods

| Method | Description |
|--------|-------------|
| `getInt()` | Get as `int` |
| `getString()` | Get as `String` |
| `getBool()` | Get as `boolean` |
| `getDouble()` | Get as `double` |
| `setInt(int value)` | Set int value |
| `setString(String value)` | Set String value |
| `setBool(boolean value)` | Set boolean value |
| `setDouble(double value)` | Set double value |
| `updateInt(IntUpdater updater)` | Functional update for int: `n -> n + 1` |
| `updateString(StringUpdater updater)` | Functional update for String |

### Java Example

```java
static ReactElement counter(JSObject props) {
    StateHandle<Integer> count = Hooks.useState(0);
    StateHandle<String> name = Hooks.useState("World");

    return div(
        h2("Count: " + count.getInt()),
        button("Increment").onClick(e -> count.updateInt(n -> n + 1)).build(),
        button("Reset").onClick(e -> count.setInt(0)).build(),
        p("Hello, " + name.getString())
    );
}
```

### Functional Updates

Always use `updateInt` / `updateString` when the new value depends on the previous one. Using `getInt()` inside an event handler captures a stale closure:

```java
// CORRECT -- uses previous value safely
count.updateInt(n -> n + 1);

// WRONG -- stale closure risk in async/batched updates
count.setInt(count.getInt() + 1);
```

### Kotlin DSL

In the Kotlin DSL, state is accessed via property delegates:

```kotlin
val Counter = fc("Counter") {
    var count by state(0)           // IntStateDelegate
    var name by state("World")      // StringStateDelegate
    var visible by state(true)      // BooleanStateDelegate
    var opacity by state(1.0)       // DoubleStateDelegate

    div {
        h2 { +"Count: $count" }
        button { +"Increment"; onClick { count++ } }
    }
}
```

For lists of strings, use `stateList`:

```kotlin
var items by stateList("apple", "banana")
// items is List<String>, assignment replaces the whole list
```

## useEffect

Runs side effects after render. Three variations based on dependency handling.

### Signatures (Java)

```java
Hooks.useEffect(EffectCallback effect)                    // runs every render
Hooks.useEffect(EffectCallback effect, JSObject[] deps)   // runs when deps change
```

`EffectCallback` is a `@JSFunctor` interface:

```java
@JSFunctor
public interface EffectCallback extends JSObject {
    VoidCallback run();  // return cleanup function, or null
}
```

### Variation 1: Run Every Render (no deps)

```java
Hooks.useEffect(() -> {
    JsUtil.consoleLog("Rendered");
    return null;  // no cleanup
});
```

### Variation 2: Run Once on Mount (empty deps)

```java
Hooks.useEffect(() -> {
    JsUtil.consoleLog("Mounted");
    int id = JsUtil.setInterval(() -> { /* tick */ }, 1000);
    return () -> JsUtil.clearInterval(id);  // cleanup on unmount
}, Hooks.deps());  // empty array = mount only
```

### Variation 3: Run When Dependencies Change

```java
StateHandle<String> query = Hooks.useState("");

Hooks.useEffect(() -> {
    JsUtil.consoleLog("Query changed to: " + query.getString());
    return null;
}, Hooks.deps(React.stringToJS(query.getString())));
```

### Cleanup

Return a `VoidCallback` from the effect to run cleanup before re-running or on unmount. Return `null` if no cleanup is needed.

### Kotlin DSL

```kotlin
// Run every render
effect {
    JsUtil.consoleLog("Rendered")
}

// Run once on mount
effectOnce {
    val id = JsUtil.setInterval({ /* tick */ }, 1000)
    onCleanup { JsUtil.clearInterval(id) }
}

// Run when dependencies change
effect(query) {
    JsUtil.consoleLog("Query changed: $query")
}
```

The Kotlin `effect` block provides a `CoroutineScope` and `onCleanup` for registering teardown logic.

## useRef

Creates a mutable ref that persists across renders without triggering re-render on change. Two uses: DOM refs and mutable instance variables.

### Signatures (Java)

```java
RefHandle Hooks.useRef(JSObject initial)
RefHandle Hooks.useRefInt(int initial)
RefHandle Hooks.useRefString(String initial)
```

### RefHandle Methods

| Method | Description |
|--------|-------------|
| `getCurrentString()` | Get current as `String` |
| `getCurrentInt()` | Get current as `int` |
| `getCurrentBool()` | Get current as `boolean` |
| `getCurrentDouble()` | Get current as `double` |
| `setCurrentString(String value)` | Set current as `String` |
| `setCurrentInt(int value)` | Set current as `int` |
| `setCurrentBool(boolean value)` | Set current as `boolean` |
| `setCurrentDouble(double value)` | Set current as `double` |

### Java Example -- Mutable Counter (No Re-render)

```java
RefHandle renderCount = Hooks.useRefInt(0);

Hooks.useEffect(() -> {
    renderCount.setCurrentInt(renderCount.getCurrentInt() + 1);
    return null;
});
```

### Kotlin DSL

```kotlin
var intervalId by refInt(0)
var nameRef by refString("")

effectOnce {
    intervalId = JsUtil.setInterval({ /* tick */ }, 1000)
    onCleanup { JsUtil.clearInterval(intervalId) }
}
```

## useMemo

Memoizes an expensive computation. Only recalculates when dependencies change.

### Signature (Java)

```java
JSObject Hooks.useMemo(MemoFactory factory, JSObject[] deps)
```

`MemoFactory` is a `@JSFunctor` interface:

```java
@JSFunctor
public interface MemoFactory extends JSObject {
    JSObject create();
}
```

### Java Example

```java
StateHandle<String> filter = Hooks.useState("");

JSObject filtered = Hooks.useMemo(
    () -> computeExpensiveFilter(items, filter.getString()),
    Hooks.deps(React.stringToJS(filter.getString()))
);
```

### Kotlin DSL

```kotlin
var filter by state("")
val filtered = memo(filter) {
    items.filter { it.contains(filter) }
}
```

## useCallback

Memoizes a callback reference. Prevents child components from re-rendering when the parent re-renders, if the child is wrapped with `React.memo()`.

### Signature (Java)

```java
JSObject Hooks.useCallback(JSObject callback, JSObject[] deps)
```

### Java Example

```java
JSObject handleClick = Hooks.useCallback(
    (EventHandler) e -> count.updateInt(n -> n + 1),
    Hooks.deps()  // stable reference, never changes
);
```

## useReducer

Manages complex state with a reducer function, similar to Redux.

### Signature (Java)

```java
JSObject[] Hooks.useReducer(JSObject reducer, JSObject initialState)
// Returns [state, dispatch]
```

The reducer is a raw JS function `(state, action) => newState`. Define it with `@JSBody`:

```java
@JSBody(params = {}, script =
    "return function(state, action) {" +
    "  switch(action.type) {" +
    "    case 'increment': return {count: state.count + 1};" +
    "    case 'decrement': return {count: state.count - 1};" +
    "    default: return state;" +
    "  }" +
    "};")
private static native JSObject reducer();

@JSBody(params = {}, script = "return {count: 0};")
private static native JSObject initialState();

// In render:
JSObject[] result = Hooks.useReducer(reducer(), initialState());
JSObject state = result[0];    // current state
JSObject dispatch = result[1]; // dispatch function
```

## useContext

Reads the current value from a React context. See `references/context.md` for full context API.

### Java: Typed Methods on ReactContext

`ReactContext` provides typed consumption methods directly — no raw `JSObject` needed:

```java
ReactContext themeCtx = ReactContext.create("light");

// In a component render:
String theme = themeCtx.useString();
int count = countCtx.useInt();
boolean auth = authCtx.useBool();
```

### Kotlin DSL

```kotlin
val ThemeContext = createStringContext("light")

val ThemedButton = fc("ThemedButton") {
    val theme = useContext(ThemeContext)
    // theme is a String, no casting needed
}
```

## deps() Helper

Creates dependency arrays for `useEffect`, `useMemo`, and `useCallback`.

```java
Hooks.deps()                          // empty array (mount-only)
Hooks.deps(React.stringToJS("foo"))   // single dep
Hooks.deps(jsVal1, jsVal2)            // multiple deps
```

All dependency values must be `JSObject`. Use `React.stringToJS()`, `React.intToJS()`, `React.boolToJS()` to convert primitives.

In Kotlin DSL, `effect(dep1, dep2) { ... }` handles conversion automatically via `depsToJsArray()`.

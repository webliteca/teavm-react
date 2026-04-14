---
name: teavm-react
description: "Use this skill whenever the user is building, debugging, or extending a teavm-react application — even if they only mention writing React components in Java or Kotlin, or compiling JVM code to React via TeaVM. Triggers on: teavm-react, React in Java, React in Kotlin, TeaVM React, compile Java to React, Java React component, Kotlin React DSL, Java web frontend with React, TeaVM web app."
---

# teavm-react

Write React 18 applications in Java or Kotlin, compiled to JavaScript via TeaVM, with full type safety and zero JavaScript authoring. The library is a thin binding over real React 18 — hooks, reconciliation, fragments, memo, and context all work identically to JavaScript React. Only the syntax changes.

## Compilation Pipeline

```
Java/Kotlin source → javac/kotlinc → TeaVM compiler → classes.js → browser
                                                         ↑
                                          React 18 loaded from CDN (separate <script>)
```

The entry point is `public static void main(String[] args)`. The HTML shell loads React 18 from CDN, then `classes.js`, then calls `main([])`.

## Pick an Approach

teavm-react offers four APIs. Pick **one per project** (never mix in the same file).

| Approach | Best for | Entry point |
|----------|----------|-------------|
| **Kotlin DSL** | New projects with Kotlin available. Most ergonomic. | `fc("Name") { ... }` |
| **Java Functional** | Default Java choice. React-familiar. | `React.wrapComponent(App::render, "Name")` |
| **Java Builder DSL** | Java devs who prefer fluent chains over static imports. | `Div.create().child(...).build()` |
| **Java Class-based** | Swing/JavaFX developers. Familiar OOP structure. | `extends ReactView`, override `render()` |

Bias toward Kotlin DSL when Kotlin is available. For Java, default to Functional with `Html.*` static imports. See `references/approach-selection.md` for the full decision tree.

## Quickstart — Same Counter in Four APIs

### Kotlin DSL
```kotlin
val Counter = fc("Counter") {
    var count by state(0)
    div {
        h2 { +"Count: $count" }
        button { +"Increment"; onClick { count++ } }
    }
}
```

### Java Functional
```java
import static ca.weblite.teavmreact.html.Html.*;

static ReactElement counter(JSObject props) {
    StateHandle<Integer> count = Hooks.useState(0);
    return div(
        h2("Count: " + count.getInt()),
        button("Increment").onClick(e -> count.updateInt(n -> n + 1)).build()
    );
}
```

### Java Builder DSL
```java
import ca.weblite.teavmreact.html.DomBuilder.*;

static ReactElement counter(JSObject props) {
    StateHandle<Integer> count = Hooks.useState(0);
    return Div.create()
        .child(H2.create().text("Count: " + count.getInt()).build())
        .child(Button.create().text("Increment")
            .onClick(e -> count.updateInt(n -> n + 1)).build())
        .build();
}
```

### Java Class-based
```java
public class CounterView extends ReactView {
    private final StateHandle<Integer> count = Hooks.useState(0);

    @Override protected ReactElement render() {
        return Html.div(
            Html.h2("Count: " + count.getInt()),
            Html.button("Increment").onClick(e -> count.updateInt(n -> n + 1)).build()
        );
    }
}
// Render: ReactView.view(CounterView::new, "CounterView")
```

## Critical Gotchas

These are the highest-impact mistakes. Internalize them before writing any code.

### 1. Rules of Hooks apply identically to React

Hooks (`useState`, `useEffect`, `useRef`, etc.) must be called:
- At the **top level** of the render function / `fc {}` body / `ReactView.render()`, never inside conditionals or loops
- In the **same order** every render

In `ReactView`, declare hooks as **field initializers** — this naturally satisfies the ordering rule:
```java
private final StateHandle<Integer> count = Hooks.useState(0); // OK
```

### 2. Don't mix APIs in one component

Each file should use exactly one approach. Never combine `Html.div(...)` with `Div.create()` or Kotlin `div { }` in the same component. The photostream app (a real-world teavm-react app) shows how to use `Html.*` functional DSL consistently across an entire application.

### 3. No ES6 in `@JSBody`

TeaVM's `@JSBody` does not support spread (`...`), arrow functions (`=>`), or template literals. Use `.apply/.concat` and ES5 function expressions:
```java
// WRONG: React.createElement(type, props, ...children)
// RIGHT:
@JSBody(params = {"type", "props", "children"}, script =
    "return React.createElement.apply(null, [type, props].concat(children));")
```
This only matters when **extending the library**. Users writing components don't author `@JSBody` directly.

### 4. Event handlers use dedicated setters

Use `.onClick(handler)` on the builder, or `React.setOnClick(props, handler)` at the low level. **Never** use `React.setProperty(props, "onClick", handler)` — it wraps the function in a Java object instead of passing the raw JS function, breaking the handler silently at runtime.

### 5. HTML shell must load React 18 before classes.js

```html
<script src="https://unpkg.com/react@18/umd/react.development.js" crossorigin></script>
<script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js" crossorigin></script>
<div id="root"></div>
<script src="js/classes.js"></script>
<script>main([]);</script>
```

Entry point is `main([])`, not `ReactDOM.render()` from JS. See `references/build-and-deploy.md` for the full template and production CDN URLs.

### 6. TeaVM has a limited stdlib

No reflection, no `java.nio` channels, no `java.net`, limited concurrency. Do **not** suggest Jackson, OkHttp, Retrofit, Gson, or any reflection-based library — they won't compile under TeaVM. Use TeaVM JSO (`@JSBody`) for browser APIs like `fetch`, `XMLHttpRequest`, `localStorage`.

### 7. Functional state updates avoid stale closures

When the new value depends on the previous one, use the functional updater:
```java
count.updateInt(n -> n + 1);       // CORRECT
count.setInt(count.getInt() + 1);  // WRONG — stale closure risk
```
In Kotlin DSL, direct assignment (`count++`) works because the delegate calls `setInt` immediately, but for complex updates use the delegate's `update()` method.

### 8. ReactView subclasses are re-instantiated every render

State must come from hooks (field initializers calling `Hooks.useState(...)`), never from plain mutable fields. Plain fields reset on every render because the class is reconstructed.

## Fetching Data

TeaVM has no `java.net` — you cannot use OkHttp, Retrofit, or `HttpURLConnection`. The library provides a built-in `Fetch` class that wraps the browser's `fetch()` API with a clean Java interface (no `JSObject` in the public API).

### Java — Using Fetch

```java
import ca.weblite.teavmreact.core.Fetch;

static ReactElement dataLoader(JSObject props) {
    StateHandle<String> data = Hooks.useState("Loading...");
    StateHandle<String> error = Hooks.useState("");

    Hooks.useEffect(() -> {
        Fetch.get("https://api.example.com/items",
            (body, status) -> data.setString(body),
            msg -> error.setString(msg));
        return null;
    }, Hooks.deps());  // empty deps = run once on mount

    if (!error.getString().isEmpty()) {
        return p("Error: " + error.getString());
    }
    return pre(data.getString());
}
```

POST, PUT, PATCH, and DELETE are also available:

```java
Fetch.post("https://api.example.com/items",
    "{\"name\":\"New Item\"}", "application/json",
    (body, status) -> { /* handle response */ },
    msg -> { /* handle error */ });

Fetch.delete("https://api.example.com/items/1",
    (body, status) -> { /* handle response */ },
    msg -> { /* handle error */ });
```

The callback receives both the response body (`String`) and the HTTP status code (`int`). The error callback is invoked only on network-level failures (DNS, CORS, connection refused) — HTTP error statuses like 404 or 500 still arrive through the success callback so you can inspect the status code.

### Kotlin — Suspend Functions

The Kotlin module provides suspend wrappers that return a `FetchResponse`:

```kotlin
import ca.weblite.teavmreact.kotlin.*

val ItemList = fc("ItemList") {
    var items by state("Loading...")
    var error by state("")

    launchedEffect {
        try {
            val response = fetchText("https://api.example.com/items")
            if (response.ok) {           // status in 200..299
                items = response.body
            } else {
                error = "HTTP ${response.status}"
            }
        } catch (e: FetchException) {
            error = e.message ?: "Network error"
        }
    }

    div {
        show(error.isNotEmpty()) { p { +"Error: $error" } }
        show(error.isEmpty()) { pre { +items } }
    }
}
```

Available suspend functions: `fetchText()`, `postText()`, `putText()`, `patchText()`, `deleteText()`, and `fetchRequest()` for arbitrary methods. POST/PUT/PATCH default to `application/json` content type.

See `references/teavm-interop.md` for writing custom `@JSBody` wrappers when you need lower-level control (custom headers, streaming, etc.).

## Reference Files

Load these on demand for deeper guidance:

| File | When to read |
|------|-------------|
| `references/api-signatures.md` | Look up exact method names and parameter types before writing any API call |
| `references/approach-selection.md` | Help the user choose between the four APIs |
| `references/java-functional.md` | Building components with `Html.*` static imports |
| `references/java-builder-dsl.md` | Building components with `DomBuilder` fluent chains |
| `references/java-class-based.md` | Building components with `ReactView` subclasses |
| `references/kotlin-dsl.md` | Building components with `fc {}` and the Kotlin HTML builder |
| `references/hooks.md` | useState, useEffect, useRef, useMemo, useCallback, useReducer, useContext |
| `references/events.md` | Event handler types, ChangeEvent, KeyboardEvent, MouseEvent |
| `references/context.md` | Java ReactContext and Kotlin TypedContext |
| `references/styling.md` | Inline styles, StyleBuilder (Kotlin), css() shorthand |
| `references/coroutines-and-flow.md` | effect(), launchedEffect(), collectAsState(), produceState() |
| `references/teavm-interop.md` | @JSBody, @JSFunctor, @JSProperty, calling browser APIs |
| `references/build-and-deploy.md` | Maven commands, dev.sh, run.sh, HTML shell, production builds |
| `references/pom-templates.md` | Copy-pasteable pom.xml for Java-only, Java+Kotlin, and production |
| `references/gotchas.md` | Full list of gotchas and troubleshooting |

## Examples

Copy working starter code from `assets/examples/` rather than synthesizing from scratch:

| Example | Approach | What it demonstrates |
|---------|----------|---------------------|
| `java-functional-counter/` | Java Functional | Minimal counter with useState, onClick |
| `java-builder-form/` | Java Builder DSL | Form with controlled inputs, onChange |
| `java-class-stopwatch/` | Java Class-based | ReactView with onMount/onUnmount lifecycle |
| `kotlin-fc-counter/` | Kotlin DSL | fc(), state delegation, HTML builder |
| `kotlin-flow-clock/` | Kotlin DSL | Flow.collectAsState + coroutines |
| `shared-html-shell/` | All | Canonical index.html template |

## Real-World Reference

The **photostream** app ([webliteca/photostream](https://github.com/webliteca/photostream)) is a production teavm-react application demonstrating hash-based routing, Firebase Auth integration, REST API calls via `@JSBody`/`@JSFunctor`, and multi-page component composition — all using the Java Functional approach with `Html.*` static imports.

## Key Imports

For quick reference, the most common imports by approach:

### Java Functional / Builder / Class-based
```java
import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.StateHandle;
import ca.weblite.teavmreact.html.Style;
import org.teavm.jso.JSObject;                           // for wrapComponent return type
import org.teavm.jso.dom.html.HTMLDocument;
import static ca.weblite.teavmreact.html.Html.*;      // functional DSL
import ca.weblite.teavmreact.html.DomBuilder.*;        // builder DSL
import ca.weblite.teavmreact.component.ReactView;      // class-based
```

### Kotlin DSL
```kotlin
import ca.weblite.teavmreact.core.ReactDOM
import ca.weblite.teavmreact.kotlin.*
import org.teavm.jso.dom.html.HTMLDocument
```

## Version Info

- Library: 0.1.0-SNAPSHOT
- TeaVM: 0.13.1
- React: 18 (CDN)
- JDK: 21 (build-time), Java 11 source/target
- Kotlin: 1.9.25
- Coroutines: 1.8.1

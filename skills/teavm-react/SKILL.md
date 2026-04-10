---
name: teavm-react
description: "Guidance for building React 18 web applications in Java and Kotlin using teavm-react, including component creation, hooks, event handling, HTML DSL usage, and TeaVM compilation patterns."
---

# teavm-react

Java/Kotlin library for writing React 18 web applications compiled to JavaScript via TeaVM. Provides type-safe React bindings including components, hooks, context, refs, and event handling.

**Version**: 0.1.0-SNAPSHOT

## Architecture

Three Maven modules:
- **teavm-react-core** -- Java bindings to React 18 via TeaVM JSBody annotations
- **teavm-react-kotlin** -- Idiomatic Kotlin wrapper with delegated properties, coroutines, and DSL builders
- **teavm-react-demo** -- Kitchen-sink demo application

Compilation flow: Java/Kotlin source -> javac/kotlinc -> class files -> TeaVM compiler -> classes.js -> runs with React 18 from CDN in browser.

## Quickstart

### Java (Core)

```java
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.html.Html;

// Functional component with useState
RenderFunction Counter = () -> {
    var count = Hooks.useState(0);
    return Html.div()
        .child(Html.h1().text("Count: " + count.get()))
        .child(Html.button()
            .text("Increment")
            .onClick(e -> count.set(count.get() + 1)))
        .build();
};

// Render to DOM
ReactDOM.createRoot("root").render(React.createElement(Counter));
```

### Kotlin (DSL)

```kotlin
import ca.weblite.teavmreact.kotlin.*

val Counter = component {
    var count by useState(0)
    div {
        h1 { +"Count: $count" }
        button {
            +"Increment"
            onClick { count++ }
        }
    }
}
```

## Key APIs

### Core (`ca.weblite.teavmreact.core`)
- `React` -- createElement, memo, createContext
- `ReactDOM` -- createRoot, render
- `ReactElement` -- element wrapper
- `ReactContext` -- context API
- `ReactRoot` -- root container

### Hooks (`ca.weblite.teavmreact.hooks`)
- `Hooks.useState(initial)` -- returns `StateHandle<T>` with get/set
- `Hooks.useEffect(callback, deps)` -- side effects
- `Hooks.useContext(context)` -- consume context
- `Hooks.useRef(initial)` -- returns `RefHandle<T>`
- `Hooks.useMemo(factory, deps)` -- memoized values
- `Hooks.useCallback(callback, deps)` -- memoized callbacks

### HTML DSL (`ca.weblite.teavmreact.html`)
- `Html.div()`, `Html.span()`, `Html.button()`, etc. -- fluent builder pattern
- `.text(str)` -- text content
- `.child(element)` -- nested elements
- `.onClick(handler)`, `.onChange(handler)` -- event handlers
- `.style(key, value)` -- inline styles
- `.className(name)` -- CSS classes
- `.build()` -- produces ReactElement

### Events (`ca.weblite.teavmreact.events`)
- `MouseEvent`, `KeyboardEvent`, `ChangeEvent`
- Type-safe handler interfaces: `EventHandler`, `ChangeEventHandler`, `SubmitEventHandler`, `FocusEventHandler`

### Component base class (`ca.weblite.teavmreact.component`)
- `ReactView` -- abstract class for class-based components

## Build Commands

```bash
# Build everything
mvn clean install

# Build and run demo
./run.sh [port]    # default port 8080

# Run unit tests
mvn test -pl teavm-react-core,teavm-react-kotlin

# Compile TeaVM output only
mvn process-classes -pl teavm-react-demo
```

## Prerequisites

- JDK 21 (source/target compatibility is Java 11)
- Maven 3.8+
- TeaVM 0.13.1

## Code Conventions

- Java 11 language level
- TeaVM JSBody annotations for JS interop -- no runtime reflection
- Builder pattern for HTML elements in Java
- Lambda-with-receiver DSL in Kotlin
- Package: `ca.weblite.teavmreact`

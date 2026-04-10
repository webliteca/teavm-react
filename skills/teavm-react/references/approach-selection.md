# Approach Selection Guide

Read this when starting a new teavm-react component or project to pick the right API surface. There are four approaches; each uses the same React 18 runtime and hooks under the hood. The choice is purely about coding style.

## Decision Tree

```
Is Kotlin available in your project?
|
+-- YES --> Use Kotlin DSL
|           Reason: most concise syntax, property delegates for state,
|           lambda-with-receiver HTML builders, coroutine support.
|           Import: import ca.weblite.teavmreact.kotlin.*
|
+-- NO (Java only)
    |
    Are you porting from Swing/JavaFX or prefer class-per-component?
    |
    +-- YES --> Use Java Class-Based (ReactView)
    |           Reason: familiar OOP model, state as fields, lifecycle
    |           methods (onMount/onUnmount) instead of raw useEffect.
    |           Import: import ca.weblite.teavmreact.component.ReactView
    |
    +-- NO
        |
        Do you prefer static method calls or fluent builder chains?
        |
        +-- Static methods (React-like) --> Use Java Functional
        |   Reason: closest to idiomatic React, concise for simple UIs,
        |   reads like JSX with static imports.
        |   Import: import static ca.weblite.teavmreact.html.Html.*
        |
        +-- Fluent chains --> Use Java Builder DSL
            Reason: discoverable via autocomplete, good for complex
            forms, no static imports needed.
            Import: import ca.weblite.teavmreact.html.DomBuilder.*
```

## Quick Comparison

| Feature              | Java Functional         | Java Builder DSL          | Java Class-Based         | Kotlin DSL              |
|----------------------|------------------------|--------------------------|--------------------------|-------------------------|
| Component definition | `React.wrapComponent`  | `React.wrapComponent`    | `extends ReactView`      | `fc("Name") { ... }`   |
| HTML elements        | `div(...)`, `p(...)`   | `Div.create().child()`   | `div(...)` (from Html)   | `div { ... }`           |
| State declaration    | `Hooks.useState(0)`    | `Hooks.useState(0)`      | field initializer        | `var x by state(0)`     |
| Event handlers       | `.onClick(e -> ...)`   | `.onClick(e -> ...)`     | `.onClick(e -> ...)`     | `onClick { ... }`       |
| Effects              | `Hooks.useEffect(...)` | `Hooks.useEffect(...)`   | `onMount()`/`onUnmount()`| `effect { ... }`       |
| Boilerplate          | Low                    | Medium                   | Medium                   | Lowest                  |
| Learning curve       | Low (React devs)       | Low (Java devs)          | Low (Swing/FX devs)      | Low (Kotlin devs)       |

## Mixing Approaches

All four approaches produce `ReactElement` and `JSObject` component references. You can mix them freely:

```java
// Render a Builder DSL component inside a Functional component
div(
    h1("Title"),
    Div.create().className("sidebar").child(P.create().text("Built with Builder DSL")).build(),
    ReactView.view(MyView::new, "MyView")
);
```

## File References

- Java Functional: see `java-functional.md`
- Java Builder DSL: see `java-builder-dsl.md`
- Java Class-Based: see `java-class-based.md`
- Kotlin DSL: see `kotlin-dsl.md`

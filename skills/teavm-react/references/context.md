# Context Reference

Read this file when you need to share state across components without prop drilling. Covers Java `ReactContext`, Kotlin `TypedContext`, providing values, and consuming with typed methods.

## Java: ReactContext

### Creating a Context

```java
import ca.weblite.teavmreact.core.ReactContext;

// No default value
ReactContext themeCtx = ReactContext.create();

// Typed default values
ReactContext themeCtx = ReactContext.create("light");     // String
ReactContext countCtx = ReactContext.create(0);           // int
ReactContext authCtx  = ReactContext.create(false);       // boolean
```

### ReactContext Methods

| Method | Return Type | Description |
|--------|------------|-------------|
| `create()` | `ReactContext` | Create context without default |
| `create(String defaultValue)` | `ReactContext` | Create context with String default |
| `create(int defaultValue)` | `ReactContext` | Create context with int default |
| `create(boolean defaultValue)` | `ReactContext` | Create context with boolean default |
| `useString()` | `String` | Read context value as String (during render) |
| `useInt()` | `int` | Read context value as int (during render) |
| `useBool()` | `boolean` | Read context value as boolean (during render) |
| `provide(String value, ReactElement... children)` | `ReactElement` | Wrap children with String provider |
| `provide(int value, ReactElement... children)` | `ReactElement` | Wrap children with int provider |
| `provide(boolean value, ReactElement... children)` | `ReactElement` | Wrap children with boolean provider |

### Providing a Value (Java)

```java
static final ReactContext ThemeCtx = ReactContext.create("light");

static ReactElement app(JSObject props) {
    StateHandle<String> theme = Hooks.useState("light");

    return ThemeCtx.provide(
        theme.getString(),
        Html.component(ThemedButton),
        Html.component(ThemedPanel)
    );
}
```

### Consuming with useString / useInt / useBool (Java)

```java
static ReactElement themedButton(JSObject props) {
    String theme = ThemeCtx.useString();

    return button("Themed Button")
        .style(Style.create()
            .backgroundColor("dark".equals(theme) ? "#333" : "#fff")
            .color("dark".equals(theme) ? "#fff" : "#333"))
        .build();
}
```

### Nested Providers (Java)

Inner providers override outer ones for their subtree:

```java
return ThemeCtx.provide("light",
    Html.div(
        Html.component(LightChild),            // sees "light"
        ThemeCtx.provide("dark",
            Html.component(DarkChild)           // sees "dark"
        )
    )
);
```

## Kotlin: TypedContext

The Kotlin DSL provides type-safe context wrappers that eliminate manual conversion.

### Creating Typed Contexts

```kotlin
import ca.weblite.teavmreact.kotlin.*

val ThemeContext = createStringContext("light")     // TypedContext<String>
val CountContext = createIntContext(0)              // TypedContext<Int>
val AuthContext  = createBoolContext(false)         // TypedContext<Boolean>
```

### TypedContext Factory Functions

| Function | Result Type | Default Type |
|----------|------------|-------------|
| `createStringContext(default)` | `TypedContext<String>` | `String` |
| `createIntContext(default)` | `TypedContext<Int>` | `Int` |
| `createBoolContext(default)` | `TypedContext<Boolean>` | `Boolean` |

### Providing Values (Kotlin)

`TypedContext.provide(value) { children }` returns a `ReactElement`:

```kotlin
val App = fc("App") {
    var theme by state("light")

    +ThemeContext.provide(theme) {
        div {
            button {
                +"Toggle Theme"
                onClick { theme = if (theme == "light") "dark" else "light" }
            }
            +ThemedContent
        }
    }
}
```

### Consuming with useContext (Kotlin)

`ComponentScope.useContext(ctx)` returns the typed value directly -- no casting:

```kotlin
val ThemedContent = fc("ThemedContent") {
    val theme: String = useContext(ThemeContext)  // returns String directly

    div {
        style {
            backgroundColor = if (theme == "dark") "#333" else "#fff"
            color = if (theme == "dark") "#fff" else "#333"
            padding = "20px"
        }
        p { +"Current theme: $theme" }
    }
}
```

The generic `useContext<T>(TypedContext<T>)` works for all typed contexts -- String, Int, and Boolean.

## Complete Theme-Switching Example

### Java Version

```java
import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import ca.weblite.teavmreact.html.Style;

public class ThemeApp {
    static final ReactContext ThemeCtx = ReactContext.create("light");

    static final JSObject ThemeToggle =
        React.wrapComponent(ThemeApp::renderToggle, "ThemeToggle");

    static final JSObject ThemedCard =
        React.wrapComponent(ThemeApp::renderCard, "ThemedCard");

    static ReactElement renderCard(JSObject props) {
        String theme = ThemeCtx.useString();
        boolean dark = "dark".equals(theme);

        return div(
            h3("Themed Card"),
            p("The current theme is: " + theme)
        );
    }

    static ReactElement renderApp(JSObject props) {
        StateHandle<String> theme = Hooks.useState("light");

        return div(
            button("Toggle").onClick(e ->
                theme.updateString(t -> "light".equals(t) ? "dark" : "light")
            ).build(),
            ThemeCtx.provide(
                theme.getString(),
                component(ThemedCard)
            )
        );
    }
}
```

### Kotlin Version

```kotlin
import ca.weblite.teavmreact.kotlin.*

val ThemeContext = createStringContext("light")

val ThemedCard = fc("ThemedCard") {
    val theme = useContext(ThemeContext)
    val dark = theme == "dark"

    div {
        style {
            backgroundColor = if (dark) "#1a1a2e" else "#ffffff"
            color = if (dark) "#e0e0e0" else "#333333"
            padding = "20px"
            borderRadius = "8px"
        }
        h3 { +"Themed Card" }
        p { +"Current theme: $theme" }
    }
}

val App = fc("App") {
    var theme by state("light")

    +ThemeContext.provide(theme) {
        button {
            +"Toggle Theme"
            onClick { theme = if (theme == "light") "dark" else "light" }
        }
        +ThemedCard
    }
}
```

## Context Gotchas

1. **Context value identity matters.** If you create a new object on every render, all consumers re-render. Use `useMemo` or stable references for complex values.
2. **Declare context objects at module/file level**, not inside render functions. Creating a context inside a render function creates a new context on every render.
3. **Java uses typed methods.** Use the matching `create(String)` / `provide(String, ...)` / `useString()` for String contexts, and similarly for `int` and `boolean`.

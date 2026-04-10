# Context Reference

Read this file when you need to share state across components without prop drilling. Covers Java `ReactContext`, Kotlin `TypedContext`, providing values, and consuming with `useContext`.

## Java: ReactContext

### Creating a Context

```java
import ca.weblite.teavmreact.core.ReactContext;
import ca.weblite.teavmreact.core.React;

// No default value
ReactContext themeCtx = ReactContext.create();

// With default value
ReactContext themeCtx = ReactContext.create(React.stringToJS("light"));
```

### ReactContext Methods

| Method | Return Type | Description |
|--------|------------|-------------|
| `create()` | `ReactContext` | Create context without default |
| `create(JSObject defaultValue)` | `ReactContext` | Create context with default |
| `jsContext()` | `JSObject` | Get raw JS context object (for `useContext`) |
| `provider()` | `JSObject` | Get the Provider component |
| `provide(JSObject value, ReactElement... children)` | `ReactElement` | Wrap children with a Provider |

### Providing a Value (Java)

```java
static final ReactContext ThemeCtx = ReactContext.create(React.stringToJS("light"));

static ReactElement app(JSObject props) {
    StateHandle<String> theme = Hooks.useState("light");

    return ThemeCtx.provide(
        React.stringToJS(theme.getString()),
        Html.component(ThemedButton),
        Html.component(ThemedPanel)
    );
}
```

### Consuming with useContext (Java)

```java
static ReactElement themedButton(JSObject props) {
    JSObject themeVal = Hooks.useContext(ThemeCtx.jsContext());
    String theme = React.jsToString(themeVal);

    JSObject style = React.createObject();
    React.setProperty(style, "backgroundColor", theme.equals("dark") ? "#333" : "#fff");
    React.setProperty(style, "color", theme.equals("dark") ? "#fff" : "#333");

    return button("Themed Button").style(style).build();
}
```

### Nested Providers (Java)

Inner providers override outer ones for their subtree:

```java
return ThemeCtx.provide(React.stringToJS("light"),
    Html.div(
        Html.component(LightChild),            // sees "light"
        ThemeCtx.provide(React.stringToJS("dark"),
            Html.component(DarkChild)           // sees "dark"
        )
    )
);
```

## Kotlin: TypedContext

The Kotlin DSL provides type-safe context wrappers that eliminate manual `JSObject` conversion.

### Creating Typed Contexts

```kotlin
import ca.weblite.teavmreact.kotlin.*

val ThemeContext = createStringContext("light")     // TypedContext<String>
val CountContext = createIntContext(0)              // TypedContext<Int>
val AuthContext  = createBoolContext(false)         // TypedContext<Boolean>
val DataContext  = createContext(null as JSObject?) // TypedContext<JSObject?>
```

### TypedContext Factory Functions

| Function | Result Type | Default Type |
|----------|------------|-------------|
| `createStringContext(default)` | `TypedContext<String>` | `String` |
| `createIntContext(default)` | `TypedContext<Int>` | `Int` |
| `createBoolContext(default)` | `TypedContext<Boolean>` | `Boolean` |
| `createContext(default)` | `TypedContext<JSObject?>` | `JSObject?` |

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

### useContext Overloads (Kotlin)

| Call | Return Type |
|------|------------|
| `useContext(TypedContext<String>)` | `String` |
| `useContext(TypedContext<Int>)` | `Int` |
| `useContext(TypedContext<Boolean>)` | `Boolean` |
| `useContext(TypedContext<JSObject?>)` | `JSObject?` |

## Complete Theme-Switching Example

### Java Version

```java
import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;

public class ThemeApp {
    static final ReactContext ThemeCtx =
        ReactContext.create(React.stringToJS("light"));

    static final JSObject ThemeToggle =
        React.wrapComponent(ThemeApp::renderToggle, "ThemeToggle");

    static final JSObject ThemedCard =
        React.wrapComponent(ThemeApp::renderCard, "ThemedCard");

    static ReactElement renderToggle(JSObject props) {
        // This component needs a way to change the theme.
        // Typically you'd pass the setter via another context or props.
        return button("Toggle Theme").onClick(e -> {
            // toggle logic via context or callback prop
        }).build();
    }

    static ReactElement renderCard(JSObject props) {
        JSObject raw = Hooks.useContext(ThemeCtx.jsContext());
        String theme = React.jsToString(raw);
        boolean dark = "dark".equals(theme);

        JSObject style = React.createObject();
        React.setProperty(style, "backgroundColor", dark ? "#1a1a2e" : "#ffffff");
        React.setProperty(style, "color", dark ? "#e0e0e0" : "#333333");
        React.setProperty(style, "padding", "20px");
        React.setProperty(style, "borderRadius", "8px");

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
                React.stringToJS(theme.getString()),
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

1. **Context value identity matters.** If you create a new `JSObject` on every render, all consumers re-render. Use `useMemo` or stable references for object values.
2. **Declare context objects at module/file level**, not inside render functions. Creating a context inside a render function creates a new context on every render.
3. **Java requires manual JSObject conversion.** Always use `React.stringToJS()` / `React.jsToString()` when providing/consuming string values. Kotlin's `TypedContext` handles this automatically.

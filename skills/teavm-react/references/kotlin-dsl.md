# Kotlin DSL Approach

Read this when building teavm-react components in Kotlin. This is the most concise approach: state via property delegates, HTML via lambda-with-receiver builders, and coroutine support for effects. All examples assume:

```kotlin
import ca.weblite.teavmreact.kotlin.*
```

This single import covers `fc`, `component`, `state`, `ref`, `effect`, `div`, `span`, `button`, `input`, `HtmlBuilder`, `StyleBuilder`, `fragment`, and all other DSL functions.

## Defining Components with fc()

```kotlin
val Greeting = fc("Greeting") {
    div {
        h1 { +"Hello World" }
        p { +"Built with teavm-react Kotlin DSL" }
    }
}
```

`fc(name) { ... }` returns a `JSObject` component reference. The lambda receiver is `ComponentScope`, which provides access to state, refs, effects, props, and HTML building.

## Rendering Components

### At the App Root

```kotlin
import ca.weblite.teavmreact.core.ReactDOM
import org.teavm.jso.dom.html.HTMLDocument

fun main() {
    val root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    )
    root.render(component(App))
}
```

### As Children

Inside an `HtmlBuilder` block, use the unary `+` operator:

```kotlin
val App = fc("App") {
    div {
        +Greeting           // render component with no props
        +AnotherComponent   // renders via unaryPlus on JSObject
    }
}
```

Or use the `component()` function for explicit rendering with props:

```kotlin
+component(MyComp)
+component(MyComp, propsObj)
```

## State Delegates

Declare state with `var x by state(initial)`. The delegate handles React's `useState` hook:

```kotlin
val Counter = fc("Counter") {
    var count by state(0)          // Int
    var name by state("")          // String
    var active by state(false)     // Boolean
    var price by state(9.99)       // Double
    var items by stateList("a", "b")  // List<String>

    div {
        p { +"Count: $count" }
        button {
            +"Increment"
            onClick { count++ }     // triggers re-render
        }
    }
}
```

Supported types:
- `state(Int)` -- `IntStateDelegate`
- `state(String)` -- `StringStateDelegate`
- `state(Boolean)` -- `BooleanStateDelegate`
- `state(Double)` -- `DoubleStateDelegate`
- `state(JSObject?)` -- `JsObjectStateDelegate`
- `stateList(vararg String)` or `stateList(List<String>)` -- `StringListStateDelegate`

Reading and writing is transparent via Kotlin property syntax.

## HTML Builder DSL

Nest elements using lambda-with-receiver blocks. Every HTML element function takes a `HtmlBuilder.() -> Unit` lambda:

```kotlin
div {
    className("container")
    h1 { +"Page Title" }
    p { +"Some paragraph text" }
    ul {
        li { +"Item 1" }
        li { +"Item 2" }
        li { +"Item 3" }
    }
}
```

### Available Elements

**Layout:** `div`, `span`, `section`, `article`, `aside`, `header`, `footer`, `main`, `nav`

**Headings:** `h1`, `h2`, `h3`, `h4`, `h5`, `h6`

**Text:** `p`, `pre`, `code`, `blockquote`, `em`, `strong`, `small`, `mark`, `sub`, `sup`, `label`

**Lists:** `ul`, `ol`, `li` (with optional `key` parameter), `dl`, `dt`, `dd`

**Table:** `table`, `thead`, `tbody`, `tfoot`, `tr` (with optional `key` parameter), `th`, `td`, `caption`

**Form:** `form`, `fieldset`, `legend`, `button`, `input` (with optional `type` parameter), `textarea`, `select`, `option`

**Link/Media:** `a`, `img`, `figure`, `figcaption`, `video`, `audio`, `source`

**Misc:** `details`, `summary`, `hr()`, `br()`

All elements (except `hr` and `br`) take a builder lambda.

### Top-Level vs Nested

Top-level functions (`div { }`, `span { }`, etc.) return `ReactElement` and are used as component return values. Inside an `HtmlBuilder` block, the same function names add children to the parent.

## Unary Plus Operators

The `+` operator is overloaded for adding content inside builders:

```kotlin
div {
    +"text node"                    // String -> text node
    +someReactElement               // ReactElement -> child
    +SomeComponent                  // JSObject -> component child
    +listOfReactElements            // List<ReactElement> -> splice children
}
```

## Event Handlers

```kotlin
// Click
button {
    +"Click me"
    onClick { e -> /* JSObject event */ }
}

// Change (typed ChangeEvent)
input("text") {
    value(currentText)
    onChange { e ->
        val newVal = e.target.value     // ChangeEvent.getTarget().getValue()
    }
}

// Keyboard (typed KeyboardEvent)
input("text") {
    onKeyDown { e ->
        if (e.key == "Enter") { /* submit */ }
    }
}

// Focus/Blur
input("text") {
    onFocus { focused = true }
    onBlur { focused = false }
}

// Submit
form {
    onSubmit { e -> /* handle submit */ }
}

// Mouse
div {
    onMouseDown { e -> /* ... */ }
    onMouseUp { e -> /* ... */ }
    onMouseEnter { e -> /* ... */ }
    onMouseLeave { e -> /* ... */ }
}
```

## Controlled Inputs

```kotlin
var text by state("")

input("text") {
    value(text)
    onChange { text = it.target.value }
    placeholder("Type here...")
}
```

Checkbox:

```kotlin
var checked by state(false)

input("checkbox") {
    checked(checked)
    onChange { checked = it.target.checked }
}
```

The `input` function takes an optional type parameter (defaults to `"text"`):

```kotlin
input("email") { /* ... */ }
input("password") { /* ... */ }
input("number") { /* ... */ }
```

## Attributes

Set attributes inside any builder block:

```kotlin
div {
    className("container main")
    id("app-root")
    key("unique-key")
    title("Tooltip text")
    tabIndex(0)
    role("button")
    draggable(true)
    hidden(false)
}

input("text") {
    value("current")
    placeholder("hint")
    disabled(false)
    readOnly(true)
    name("field-name")
    htmlFor("label-id")
    autoFocus(true)
    maxLength(100)
    minLength(1)
    rows(4)      // for textarea
    cols(50)     // for textarea
}

a {
    href("https://example.com")
    target("_blank")
    +"Link text"
}

img {
    src("/photo.jpg")
    alt("Description")
}

// Generic property
div {
    prop("data-testid", "my-div")
    prop("aria-label", "Section")
}
```

## List Rendering

Use Kotlin's iteration inside builders:

```kotlin
val items = listOf("Apple", "Banana", "Cherry")

ul {
    for (item in items) {
        li(key = item) { +item }
    }
}
```

Or with `forEachIndexed`:

```kotlin
ul {
    items.forEachIndexed { i, item ->
        li(key = i) { +item }
    }
}
```

The `li` and `tr` functions accept an optional `key` parameter for React reconciliation.

## Conditional Rendering

### show() Helper

```kotlin
div {
    show(isLoggedIn) {
        p { +"Welcome back!" }
        button { +"Logout"; onClick { logout() } }
    }
}
```

`show(condition) { ... }` renders children only when condition is true.

### Standard Kotlin Conditionals

```kotlin
div {
    if (count > 0) {
        p { +"Count: $count" }
    } else {
        p { +"Nothing yet" }
    }
}
```

## Style Builder

Type-safe inline styles via `style { }`:

```kotlin
div {
    style {
        backgroundColor = "#282c34"
        color = "white"
        padding = "20px"
        display = "flex"
        flexDirection = "column"
        gap = "8px"
        borderRadius = "8px"
        fontSize = "16px"
        fontWeight = "bold"
    }
    +"Styled content"
}
```

Available properties: `display`, `position`, `top/right/bottom/left`, `zIndex`, `overflow`, `flexDirection`, `flexWrap`, `justifyContent`, `alignItems`, `alignContent`, `alignSelf`, `flex`, `flexGrow/Shrink/Basis`, `gap`, `rowGap`, `columnGap`, `order`, `gridTemplateColumns/Rows`, `gridColumn/Row/Gap/Area/Template`, `width`, `height`, `min/maxWidth`, `min/maxHeight`, `margin` (+ top/right/bottom/left), `padding` (+ top/right/bottom/left), `boxSizing`, `border` (+ top/right/bottom/left), `borderRadius/Color/Style/Width`, `outline`, `color`, `backgroundColor`, `background`, `backgroundImage/Size/Position/Repeat`, `opacity`, `fontSize`, `fontWeight`, `fontFamily`, `fontStyle`, `lineHeight`, `letterSpacing`, `textAlign`, `textDecoration`, `textTransform`, `whiteSpace`, `wordBreak/Wrap`, `textOverflow`, `boxShadow`, `textShadow`, `transform`, `transition`, `animation`, `cursor`, `pointerEvents`, `userSelect`, `filter`, `visibility`.

Custom properties: `property("WebkitOverflowScrolling", "touch")`.

### css() Shorthand

Parse a CSS string into a style object:

```kotlin
div {
    css("background-color: red; padding: 10px; border-radius: 4px")
    +"Quick styled div"
}
```

Properties are automatically converted from kebab-case to camelCase.

## Ref Delegates

```kotlin
var inputRef by ref()                    // JSObject? ref
var countRef by refInt(0)                // Int ref
var nameRef by refString("")             // String ref
```

Read/write transparently:

```kotlin
countRef++                    // update ref (no re-render)
println(countRef)             // read current value
```

Pass to DOM elements:

```kotlin
input("text") {
    ref(inputRef.handle.raw())   // pass underlying ref object
}
```

## Effects

### effect() -- useEffect wrapper

```kotlin
// Run after every render (no deps)
effect {
    JsUtil.consoleLog("rendered")
    onCleanup { JsUtil.consoleLog("cleanup") }
}

// Run only on mount (empty deps)
effectOnce {
    JsUtil.consoleLog("mounted")
    onCleanup { JsUtil.consoleLog("unmounted") }
}

// Run when specific values change
effect(count, filter) {
    JsUtil.consoleLog("count or filter changed")
}
```

### CoroutineScope in effects

Effects implement `CoroutineScope`, so you can launch coroutines:

```kotlin
effect {
    val job = launch {
        while (isActive) {
            delay(1000)
            ticks++
        }
    }
    onCleanup { job.cancel() }
}
```

### launchedEffect() -- Compose-inspired

Automatically launches and cancels a coroutine:

```kotlin
launchedEffect {
    val data = api.fetchItems()   // suspend call
    items = data
}

// With dependencies -- re-launches when deps change
launchedEffect(userId) {
    profile = api.fetchUser(userId)
}
```

## Memo

Memoize computed values:

```kotlin
val filtered = memo(items, searchTerm) {
    items.filter { it.contains(searchTerm) }
}
```

## Props Access

Read props passed to a component:

```kotlin
val UserCard = fc("UserCard") {
    val name = propString("name")
    val age = propInt("age")
    val active = propBool("active")
    val data = propObj("data")

    div {
        h2 { +name }
        p { +"Age: $age" }
        show(active) { span { +"Active" } }
    }
}
```

Pass props when rendering:

```kotlin
val props = React.createObject()
React.setProperty(props, "name", "Alice")
React.setProperty(props, "age", 30)
+component(UserCard, props)
```

Or use the `render` extension with `PropsBuilder`:

```kotlin
div {
    render(UserCard) {
        "name"("Alice")
        "age"(30)
        "active"(true)
    }
}
```

## Fragments

Group elements without an extra DOM node:

```kotlin
fragment {
    h1 { +"Title" }
    p { +"Paragraph 1" }
    p { +"Paragraph 2" }
}
```

## Tables

```kotlin
table {
    thead {
        tr {
            th { +"Name" }
            th { +"Email" }
            th { +"Actions" }
        }
    }
    tbody {
        for (user in users) {
            tr(key = user.id) {
                td { +user.name }
                td { +user.email }
                td {
                    button {
                        +"Delete"
                        onClick { removeUser(user.id) }
                    }
                }
            }
        }
    }
}
```

## Complete Example: Todo List with Add/Remove

```kotlin
import ca.weblite.teavmreact.kotlin.*
import ca.weblite.teavmreact.core.ReactDOM
import org.teavm.jso.dom.html.HTMLDocument

val TodoApp = fc("TodoApp") {
    var input by state("")
    var todos by stateList<String>()
    var nextId by state(0)
    var filter by state("all")  // "all", "active", "done"
    var doneIds by state("")    // comma-separated done IDs

    val doneSet = if (doneIds.isEmpty()) emptySet()
                  else doneIds.split(",").toSet()

    div {
        h1 { +"Todo List" }
        p { +"${todos.size} item(s), ${doneSet.size} completed" }

        // Add form
        div {
            className("add-form")
            input("text") {
                value(input)
                onChange { input = it.target.value }
                onKeyDown { e ->
                    if (e.key == "Enter" && input.isNotBlank()) {
                        todos = todos + "$nextId:$input"
                        nextId++
                        input = ""
                    }
                }
                placeholder("What needs to be done?")
            }
            button {
                +"Add"
                disabled(input.isBlank())
                onClick {
                    todos = todos + "$nextId:$input"
                    nextId++
                    input = ""
                }
            }
        }

        // Filter buttons
        div {
            className("filters")
            for (f in listOf("all", "active", "done")) {
                button {
                    +f.replaceFirstChar { it.uppercase() }
                    className(if (f == filter) "active" else "")
                    onClick { filter = f }
                }
            }
        }

        // Todo list
        ul {
            for (todo in todos) {
                val id = todo.substringBefore(":")
                val text = todo.substringAfter(":")
                val isDone = id in doneSet
                val visible = when (filter) {
                    "active" -> !isDone
                    "done" -> isDone
                    else -> true
                }
                show(visible) {
                    li(key = id) {
                        style {
                            textDecoration = if (isDone) "line-through" else "none"
                            display = "flex"
                            gap = "8px"
                            alignItems = "center"
                        }
                        input("checkbox") {
                            checked(isDone)
                            onChange {
                                doneIds = if (isDone) {
                                    doneSet.filter { it != id }.joinToString(",")
                                } else {
                                    if (doneIds.isEmpty()) id else "$doneIds,$id"
                                }
                            }
                        }
                        span { +text }
                        button {
                            +"x"
                            onClick {
                                todos = todos.filter { it != todo }
                                doneIds = doneSet.filter { it != id }.joinToString(",")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    )
    root.render(component(TodoApp))
}
```

## Key Kotlin DSL Advantages Over Java Approaches

1. `var x by state(0)` instead of `Hooks.useState(0)` + `.getInt()`/`.setInt()`
2. `div { h1 { +"text" } }` instead of `div(h1("text"))`
3. `onClick { ... }` instead of `.onClick(e -> { ... }).build()`
4. `effect { onCleanup { ... } }` instead of `Hooks.useEffect(() -> { return () -> ...; })`
5. `show(cond) { ... }` for conditional rendering
6. `style { padding = "10px" }` instead of manual `React.createObject()` + `setProperty`
7. `stateList()` for `List<String>` state without manual serialization
8. Coroutine support in effects via `launch { }` and `launchedEffect { }`

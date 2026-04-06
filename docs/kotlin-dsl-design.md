# Kotlin DSL Design for teavm-react

## Overview

This document describes the design of `teavm-react-kotlin`, a new module that wraps
`teavm-react-core` with an idiomatic Kotlin API. The goal is to provide a developer
experience that feels native to Kotlin developers, leveraging:

- **Delegated properties** for React state and refs
- **Type-safe builders** (lambda-with-receiver + `@DslMarker`) for HTML
- **Coroutines** for effects, async data fetching, and `Flow`-based reactive state
- **Extension functions** for composable, reusable UI fragments
- **Reified generics** for type-safe context and props
- **Operator overloading** for natural element composition
- **Data classes** with named/default arguments for props

### Module Structure

```
teavm-react-kotlin/
  src/main/kotlin/ca/weblite/teavmreact/kotlin/
    dsl/          — HtmlBuilder, @HtmlDsl, element functions
    state/        — StateDelegate, RefDelegate, state(), ref()
    component/    — fc(), ComponentScope
    hooks/        — effect(), launchedEffect(), memo(), context helpers
    coroutines/   — ReactCoroutineScope, collectAsState, produceState
    context/      — TypedContext, createContext<T>, provide()
    style/        — StyleBuilder, CSS property types
    props/        — Props bridge, data class support
    util/         — Operator extensions, helper functions
```

---

## 1. Delegated Properties for State

The single biggest DX win. Kotlin's property delegation makes React state feel like
native mutable variables — no getters, setters, or update callbacks.

### Java (current)

```java
var count = Hooks.useState(0);
int value = count.getInt();          // read
count.setInt(5);                     // write
count.updateInt(c -> c + 1);         // functional update
```

### Kotlin DSL

```kotlin
var count by state(0)
val value = count        // read — just use the variable
count = 5                // write — just assign
count++                  // increment — natural operators
```

### Implementation

```kotlin
class StateDelegate<T>(initial: T) : ReadWriteProperty<Any?, T> {
    private val handle: StateHandle<*> = when (initial) {
        is Int     -> Hooks.useState(initial)
        is String  -> Hooks.useState(initial)
        is Boolean -> Hooks.useState(initial)
        is Double  -> Hooks.useState(initial)
        else       -> Hooks.useState(initial as JSObject)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = when (handle.get()) {
        // Type-safe unwrapping based on the original type
        is Int     -> handle.int as T
        is String  -> handle.string as T
        is Boolean -> handle.bool as T
        is Double  -> handle.double as T
        else       -> handle.get() as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        when (value) {
            is Int     -> handle.setInt(value)
            is String  -> handle.setString(value)
            is Boolean -> handle.setBool(value)
            is Double  -> handle.setDouble(value)
            else       -> handle.set(value as JSObject)
        }
    }
}

fun <T> state(initial: T): StateDelegate<T> = StateDelegate(initial)
```

### Collection State

For lists and maps, we provide specialized delegates that bridge Kotlin collections
to the underlying JS representation:

```kotlin
var todos by stateList("Buy milk", "Write code")
// Type: MutableList<String>-like behavior via delegate

todos = todos + "New item"              // immutable update (React-friendly)
todos = todos.filter { it != "Buy milk" }  // filter
```

---

## 2. Type-Safe HTML Builder DSL

Replace `.child()` chains with Kotlin's lambda-with-receiver pattern, matching the
conventions established by kotlinx.html and Compose HTML.

### Java (current — Builder approach)

```java
Div.create()
    .child(H1.create().text("Todo List"))
    .child(Ul.create()
        .child(Li.create().text("Item 1"))
        .child(Li.create().text("Item 2")))
    .child(Button.create().text("Add").onClick(e -> { /* ... */ }))
    .build()
```

### Kotlin DSL

```kotlin
div {
    h1 { +"Todo List" }
    ul {
        li { +"Item 1" }
        li { +"Item 2" }
    }
    button {
        +"Add"
        onClick { /* ... */ }
    }
}
```

### Implementation

```kotlin
@DslMarker
annotation class HtmlDsl

@HtmlDsl
open class HtmlBuilder(private val tag: String) {
    protected val children = mutableListOf<ReactElement>()
    protected val props: JSObject = React.createObject()

    /** Text node via unaryPlus — idiomatic Kotlin HTML DSL convention */
    operator fun String.unaryPlus() {
        children.add(Html.text(this))
    }

    /** Render a child component */
    operator fun JSObject.unaryPlus() {
        children.add(Html.component(this))
    }

    /** Render a child ReactElement */
    operator fun ReactElement.unaryPlus() {
        children.add(this)
    }

    // --- Attributes ---
    fun className(value: String) { React.setProperty(props, "className", value) }
    fun id(value: String) { React.setProperty(props, "id", value) }
    fun key(value: String) { React.setProperty(props, "key", value) }
    fun key(value: Int) { React.setProperty(props, "key", value) }

    // --- Event handlers ---
    fun onClick(handler: (JSObject) -> Unit) { React.setOnClick(props, handler) }
    fun onChange(handler: (ChangeEvent) -> Unit) { React.setOnChange(props, handler) }
    fun onKeyDown(handler: (KeyboardEvent) -> Unit) { React.setOnKeyDown(props, handler) }
    fun onSubmit(handler: (JSObject) -> Unit) { React.setOnSubmit(props, handler) }
    // ... other events

    // --- Child element functions ---
    fun div(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("div").apply(block).build())
    }
    fun span(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("span").apply(block).build())
    }
    fun p(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("p").apply(block).build())
    }
    fun h1(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("h1").apply(block).build())
    }
    fun h2(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("h2").apply(block).build())
    }
    // ... h3-h6, ul, ol, li, table, thead, tbody, tr, td, th, etc.

    fun button(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("button").apply(block).build())
    }
    fun input(type: String = "text", block: InputBuilder.() -> Unit) {
        children.add(InputBuilder(type).apply(block).build())
    }
    fun form(block: HtmlBuilder.() -> Unit) {
        children.add(HtmlBuilder("form").apply(block).build())
    }
    fun a(block: AnchorBuilder.() -> Unit) {
        children.add(AnchorBuilder().apply(block).build())
    }
    fun img(block: ImgBuilder.() -> Unit) {
        children.add(ImgBuilder().apply(block).build())
    }

    // --- Keyed elements ---
    fun li(key: Any? = null, block: HtmlBuilder.() -> Unit) {
        val builder = HtmlBuilder("li")
        key?.let { builder.key(it.toString()) }
        children.add(builder.apply(block).build())
    }

    fun build(): ReactElement =
        React.createElement(tag, props, children.toTypedArray())
}

/** Specialized builder for input elements */
class InputBuilder(type: String) : HtmlBuilder("input") {
    init { React.setProperty(props, "type", type) }
    fun value(v: String) { React.setProperty(props, "value", v) }
    fun placeholder(v: String) { React.setProperty(props, "placeholder", v) }
    fun disabled(v: Boolean) { React.setProperty(props, "disabled", v) }
    fun checked(v: Boolean) { React.setProperty(props, "checked", v) }
}

/** Specialized builder for anchor elements */
class AnchorBuilder : HtmlBuilder("a") {
    fun href(v: String) { React.setProperty(props, "href", v) }
    fun target(v: String) { React.setProperty(props, "target", v) }
}

/** Specialized builder for img elements */
class ImgBuilder : HtmlBuilder("img") {
    fun src(v: String) { React.setProperty(props, "src", v) }
    fun alt(v: String) { React.setProperty(props, "alt", v) }
}

// --- Top-level entry points ---
fun div(block: HtmlBuilder.() -> Unit): ReactElement =
    HtmlBuilder("div").apply(block).build()

fun span(block: HtmlBuilder.() -> Unit): ReactElement =
    HtmlBuilder("span").apply(block).build()

// ... all other top-level element functions
```

### @DslMarker Prevents Scope Leaking

The `@HtmlDsl` annotation prevents accidental access to outer builder scopes:

```kotlin
div {
    ul {
        li {
            // className() here applies to <li>, NOT <ul> or <div>
            // Trying to call ul { } here would be a compile error
            className("item")
            +"Text"
        }
    }
}
```

---

## 3. Component Definition with `fc { }`

A concise way to define function components that wraps `React.wrapComponent`.

### Java (current)

```java
static final JSObject counter = React.wrapComponent(props -> {
    var count = Hooks.useState(0);
    return Html.div(
        Html.p("Count: " + count.getInt()),
        Html.button("+").onClick(e -> count.updateInt(c -> c + 1)).build()
    );
}, "Counter");
```

### Kotlin DSL

```kotlin
val Counter = fc("Counter") {
    var count by state(0)

    div {
        p { +"Count: $count" }       // String templates!
        button {
            +"+"
            onClick { count++ }      // Direct mutation via delegate
        }
    }
}
```

### Implementation

```kotlin
class ComponentScope(val props: JSObject) : CoroutineScope {
    // Coroutine scope tied to component lifecycle (see Section 5)
    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Main

    fun <T> state(initial: T): StateDelegate<T> = StateDelegate(initial)
    fun <T : Any> ref(initial: T? = null): RefDelegate<T> = RefDelegate(initial)

    // Hooks (see Section 5 for coroutine-based hooks)
    fun effect(vararg deps: Any?, block: EffectScope.() -> Unit) { /* ... */ }
    fun <T> memo(vararg deps: Any?, compute: () -> T): T { /* ... */ }
    fun <T> context(ctx: TypedContext<T>): T { /* ... */ }
}

fun fc(name: String = "", render: ComponentScope.() -> ReactElement): JSObject {
    return React.wrapComponent(
        RenderFunction { props -> ComponentScope(props).render() },
        name
    )
}
```

### Typed Props with Data Classes

```kotlin
data class GreetingProps(
    val name: String,
    val age: Int = 0,
    val onGreet: (() -> Unit)? = null
)

val Greeting = fc<GreetingProps>("Greeting") { props ->
    div {
        h1 { +"Hello, ${props.name}!" }
        if (props.age > 0) {
            p { +"Age: ${props.age}" }
        }
        props.onGreet?.let { callback ->
            button {
                +"Greet"
                onClick { callback() }
            }
        }
    }
}

// Usage — named arguments with defaults
+Greeting(name = "Alice", age = 30)
+Greeting(name = "Bob")  // age defaults to 0, onGreet defaults to null
```

### Implementation of typed `fc<P>`

```kotlin
inline fun <reified P : Any> fc(
    name: String = "",
    crossinline render: ComponentScope.(P) -> ReactElement
): TypedComponent<P> {
    val wrapped = React.wrapComponent(
        RenderFunction { jsProps ->
            val props = jsPropsToKotlin<P>(jsProps)  // deserialize
            ComponentScope(jsProps).render(props)
        },
        name
    )
    return TypedComponent(wrapped)
}

class TypedComponent<P : Any>(private val jsComponent: JSObject) {
    /** Invoke with typed props — enables Greeting(name = "Alice") syntax */
    operator fun invoke(props: P): ReactElement {
        val jsProps = kotlinPropsToJs(props)  // serialize
        return React.createElement(jsComponent, jsProps)
    }
}
```

---

## 4. Coroutine-Based Effects

**This is the centerpiece of the Kotlin API.** Since TeaVM supports coroutines,
we can offer structured concurrency for effects, async data fetching, and reactive
streams — bringing Jetpack Compose-level ergonomics to React.

### 4a. `launchedEffect` — Compose-inspired

A coroutine that launches on mount (or when dependencies change) and is
automatically cancelled on unmount via structured concurrency.

```kotlin
val DataLoader = fc("DataLoader") {
    var data by state<List<Item>>(emptyList())
    var loading by state(true)
    var error by state<String?>(null)

    // Launches a coroutine — auto-cancels on unmount
    launchedEffect {
        try {
            data = api.fetchItems()     // suspend fun!
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }

    div {
        when {
            loading -> p { +"Loading..." }
            error != null -> p { className("error"); +"Error: $error" }
            else -> ul {
                for (item in data) {
                    li(key = item.id) { +item.name }
                }
            }
        }
    }
}
```

#### With dependencies — re-launches when deps change

```kotlin
val UserProfile = fc("UserProfile") {
    val userId = prop<String>("userId")
    var profile by state<User?>(null)

    // Re-fetches whenever userId changes
    launchedEffect(userId) {
        profile = null                // reset
        profile = api.fetchUser(userId)  // suspend
    }

    div {
        if (profile == null) {
            p { +"Loading user $userId..." }
        } else {
            h2 { +profile!!.name }
            p { +profile!!.email }
        }
    }
}
```

### Implementation

```kotlin
fun ComponentScope.launchedEffect(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    // Convert keys to JS dependency array
    val deps = if (keys.isEmpty()) null else keys.toJsDeps()

    Hooks.useEffect({
        // Launch coroutine in component's scope
        val job = this@ComponentScope.launch { block() }
        // Return cleanup that cancels the coroutine
        VoidCallback { job.cancel() }
    }, deps)
}
```

### 4b. `effect` with Coroutine-Aware Cleanup

For effects that need explicit setup/teardown with coroutine support:

```kotlin
val Timer = fc("Timer") {
    var ticks by state(0)

    effect {
        // Launch a coroutine within the effect
        val job = launch {
            while (isActive) {
                delay(1000)
                ticks++
            }
        }

        onCleanup {
            job.cancel()
        }
    }

    p { +"Ticks: $ticks" }
}
```

### Implementation

```kotlin
class EffectScope(private val scope: CoroutineScope) : CoroutineScope by scope {
    internal var cleanupFn: (() -> Unit)? = null

    fun onCleanup(block: () -> Unit) {
        cleanupFn = block
    }
}

fun ComponentScope.effect(
    vararg keys: Any?,
    block: EffectScope.() -> Unit
) {
    val deps = if (keys.isEmpty()) null else keys.toJsDeps()

    Hooks.useEffect({
        val effectScope = EffectScope(this@ComponentScope)
        effectScope.block()
        VoidCallback { effectScope.cleanupFn?.invoke() }
    }, deps)
}
```

### 4c. `Flow.collectAsState` — Reactive Streams as State

The most powerful pattern: turn any Kotlin `Flow` into a delegated state property
that automatically collects on mount and cancels on unmount.

```kotlin
val LiveTimer = fc("LiveTimer") {
    // Flow that emits every second, collected into state
    val seconds by flow {
        var t = 0
        while (true) {
            emit(t++)
            delay(1000)
        }
    }.collectAsState(initial = 0)

    p { +"Elapsed: ${seconds}s" }
}
```

#### With external flows (e.g., WebSocket, event bus)

```kotlin
val LiveChat = fc("LiveChat") {
    val messages by chatService.messagesFlow
        .collectAsState(initial = emptyList())

    ul {
        for (msg in messages) {
            li(key = msg.id) {
                strong { +msg.author }
                +" ${msg.text}"
            }
        }
    }
}
```

### Implementation

```kotlin
fun <T> Flow<T>.collectAsState(initial: T): StateDelegate<T> {
    val delegate = state(initial)

    launchedEffect {
        this@collectAsState.collect { value ->
            // Update React state on each emission
            delegate.set(value)
        }
    }

    return delegate
}
```

### 4d. Suspending Event Handlers

Handle async operations in event handlers without callback nesting:

```kotlin
val SaveButton = fc("SaveButton") {
    var saving by state(false)
    var message by state("")

    button {
        +if (saving) "Saving..." else "Save"
        disabled(saving)

        onClickAsync {
            saving = true
            try {
                val result = api.save(data)   // suspend call
                message = "Saved: ${result.id}"
            } catch (e: Exception) {
                message = "Error: ${e.message}"
            } finally {
                saving = false
            }
        }
    }

    if (message.isNotEmpty()) {
        p { +message }
    }
}
```

### Implementation

```kotlin
fun HtmlBuilder.onClickAsync(handler: suspend CoroutineScope.() -> Unit) {
    onClick {
        componentScope.launch { handler() }
    }
}
```

### 4e. `produceState` — Compose-Inspired Async State

A convenience that combines state creation and async initialization:

```kotlin
val userProfile = produceState<UserProfile?>(null, userId) {
    value = api.fetchUser(userId)   // suspend — assigns to state when complete
}

// userProfile is immediately null, then updates when fetch completes
```

### Implementation

```kotlin
fun <T> ComponentScope.produceState(
    initialValue: T,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit
): ReadOnlyProperty<Any?, T> {
    val stateDelegate = state(initialValue)

    launchedEffect(*keys) {
        val scope = ProduceStateScope(stateDelegate)
        scope.producer()
    }

    return stateDelegate
}

class ProduceStateScope<T>(private val delegate: StateDelegate<T>) {
    var value: T
        get() = delegate.getValue(null, /* ... */)
        set(v) = delegate.setValue(null, /* ... */, v)
}
```

---

## 5. Context API with Reified Generics

Type-safe context without casting, using Kotlin's reified inline functions.

### Java (current)

```java
ReactContext themeContext = ReactContext.create();
// Set: themeContext.provide(React.stringToJS("dark"), children...)
// Get: String theme = React.jsToString(Hooks.useContext(themeContext.jsContext()));
```

### Kotlin DSL

```kotlin
// Create — type is inferred
val ThemeContext = createContext("light")     // TypedContext<String>
val UserContext = createContext<User?>(null)  // TypedContext<User?>

// Provide — type-safe value
ThemeContext.provide("dark") {
    +App
}

// Consume — no cast needed
val theme: String = useContext(ThemeContext)
```

### Implementation

```kotlin
class TypedContext<T>(
    internal val reactContext: ReactContext,
    private val serialize: (T) -> JSObject,
    private val deserialize: (JSObject) -> T
) {
    fun provide(value: T, block: HtmlBuilder.() -> Unit): ReactElement {
        val children = HtmlBuilder("fragment").apply(block).build()
        return reactContext.provide(serialize(value), children)
    }
}

inline fun <reified T> createContext(defaultValue: T): TypedContext<T> {
    val serializer = jsSerializer<T>()
    val deserializer = jsDeserializer<T>()
    val reactCtx = ReactContext.create(serializer(defaultValue))
    return TypedContext(reactCtx, serializer, deserializer)
}

fun <T> ComponentScope.useContext(ctx: TypedContext<T>): T {
    val jsValue = Hooks.useContext(ctx.reactContext.jsContext())
    return ctx.deserialize(jsValue)
}
```

---

## 6. Style DSL

Type-safe inline styles as a nested builder instead of raw JSObject manipulation.

```kotlin
div {
    style {
        backgroundColor = "#282c34"
        color = "white"
        padding = "20px"
        display = Display.Flex
        justifyContent = JustifyContent.Center
        alignItems = AlignItems.Center
        gap = "12px"
        borderRadius = "8px"
    }
    +"Styled content"
}
```

### Implementation

```kotlin
@HtmlDsl
class StyleBuilder {
    private val styleObj = React.createObject()

    var backgroundColor: String by styleProperty("backgroundColor")
    var color: String by styleProperty("color")
    var padding: String by styleProperty("padding")
    var margin: String by styleProperty("margin")
    var border: String by styleProperty("border")
    var borderRadius: String by styleProperty("borderRadius")
    var fontSize: String by styleProperty("fontSize")
    var fontWeight: String by styleProperty("fontWeight")
    var gap: String by styleProperty("gap")
    var width: String by styleProperty("width")
    var height: String by styleProperty("height")
    // ... all CSS properties

    var display: Display by enumStyleProperty("display")
    var justifyContent: JustifyContent by enumStyleProperty("justifyContent")
    var alignItems: AlignItems by enumStyleProperty("alignItems")
    var flexDirection: FlexDirection by enumStyleProperty("flexDirection")
    // ... enum-typed properties

    internal fun build(): JSObject = styleObj

    private fun styleProperty(name: String) = object : ReadWriteProperty<StyleBuilder, String> {
        override fun getValue(thisRef: StyleBuilder, property: KProperty<*>) = ""
        override fun setValue(thisRef: StyleBuilder, property: KProperty<*>, value: String) {
            React.setProperty(styleObj, name, value)
        }
    }
}

enum class Display(val value: String) {
    Flex("flex"), Grid("grid"), Block("block"),
    InlineBlock("inline-block"), None("none")
}

// Add to HtmlBuilder:
fun HtmlBuilder.style(block: StyleBuilder.() -> Unit) {
    val style = StyleBuilder().apply(block).build()
    React.setProperty(props, "style", style)
}
```

### Quick inline style shorthand

```kotlin
// For simple one-off styles
div {
    css("background: red; padding: 10px")
    +"Quick styled"
}
```

---

## 7. Extension Functions for Reusable Components

Kotlin extension functions on `HtmlBuilder` create composable UI fragments
without any registration, wrapping, or overhead — they're just functions.

```kotlin
// Define a reusable card component
fun HtmlBuilder.card(
    title: String,
    elevation: Int = 1,
    block: HtmlBuilder.() -> Unit
) {
    div {
        className("card")
        style {
            borderRadius = "8px"
            boxShadow = "0 ${elevation * 2}px ${elevation * 4}px rgba(0,0,0,0.1)"
            padding = "16px"
        }
        if (title.isNotEmpty()) {
            h3 {
                style { marginTop = "0" }
                +title
            }
        }
        block()
    }
}

// Define a reusable labeled input
fun HtmlBuilder.labeledInput(
    label: String,
    value: String,
    type: String = "text",
    onChange: (ChangeEvent) -> Unit
) {
    div {
        className("form-group")
        label { +label }
        input(type) {
            value(value)
            onChange(onChange)
        }
    }
}

// Usage — compose naturally
val SignupForm = fc("SignupForm") {
    var name by state("")
    var email by state("")

    card("Sign Up", elevation = 2) {
        form {
            onSubmit { e -> e.preventDefault() }
            labeledInput("Name", name) { name = it.target.value }
            labeledInput("Email", email, type = "email") { email = it.target.value }
            button { +"Submit" }
        }
    }
}
```

---

## 8. Operator Overloading and Element Composition

### Rendering child components with `+`

```kotlin
div {
    +Counter                          // render a component
    +Greeting(name = "Alice")         // render with typed props
    +listOf(item1, item2, item3)      // splice a list of elements
}
```

### Conditional rendering helpers

```kotlin
div {
    // Natural Kotlin — just use if/when
    if (isLoggedIn) {
        +Dashboard
    } else {
        +LoginForm
    }

    // when expression
    when (status) {
        Status.LOADING -> p { +"Loading..." }
        Status.ERROR   -> p { className("error"); +"Failed" }
        Status.SUCCESS -> +DataView
    }

    // Convenience helper for show/hide
    show(items.isNotEmpty()) {
        ul {
            for (item in items) {
                li(key = item.id) { +item.name }
            }
        }
    }
}
```

### Implementation

```kotlin
// In HtmlBuilder:

/** Render a component (JSObject) as a child */
operator fun JSObject.unaryPlus() {
    children.add(Html.component(this))
}

/** Render a ReactElement as a child */
operator fun ReactElement.unaryPlus() {
    children.add(this)
}

/** Splice a list of elements as children */
operator fun List<ReactElement>.unaryPlus() {
    children.addAll(this)
}

/** Conditional rendering helper */
fun HtmlBuilder.show(condition: Boolean, block: HtmlBuilder.() -> Unit) {
    if (condition) block()
}
```

---

## 9. Ref Delegation

Refs as delegated properties, matching the state delegation pattern:

```kotlin
val FocusInput = fc("FocusInput") {
    var inputRef by ref<HTMLInputElement>()

    div {
        input("text") {
            ref(inputRef)
            placeholder("Type here...")
        }
        button {
            +"Focus"
            onClick { inputRef?.focus() }
        }
    }
}
```

### Implementation

```kotlin
class RefDelegate<T>(initial: T?) : ReadWriteProperty<Any?, T?> {
    internal val handle: RefHandle = when (initial) {
        null    -> Hooks.useRef(null)
        is Int  -> Hooks.useRefInt(initial)
        else    -> Hooks.useRef(initial as JSObject)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? =
        handle.current as? T

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        handle.setCurrent(value as? JSObject)
    }
}

fun <T : Any> ComponentScope.ref(initial: T? = null): RefDelegate<T> =
    RefDelegate(initial)
```

---

## 10. Memo and Callback Hooks

```kotlin
val ExpensiveComponent = fc("ExpensiveComponent") {
    val items = prop<List<String>>("items")
    val filter = prop<String>("filter")

    // Memoized computation — only recalculates when deps change
    val filtered = memo(items, filter) {
        items.filter { it.contains(filter, ignoreCase = true) }
    }

    // Memoized callback — stable reference across renders
    val handleClick = callback(filtered) {
        println("Filtered: ${filtered.size} items")
    }

    ul {
        for (item in filtered) {
            li { +item }
        }
    }
}
```

---

## 11. Routing DSL (Future)

```kotlin
val App = fc("App") {
    router {
        route("/") { HomePage() }
        route("/about") { AboutPage() }
        route("/users/:id") { params ->
            UserPage(userId = params["id"]!!)
        }
        notFound { NotFoundPage() }
    }
}
```

---

## 12. Full Example Application

A complete app showcasing all features working together:

```kotlin
// --- Typed Context ---
val ThemeContext = createContext("light")

// --- Reusable UI extensions ---
fun HtmlBuilder.card(title: String, block: HtmlBuilder.() -> Unit) {
    val theme = useContext(ThemeContext)
    div {
        className("card card-$theme")
        style {
            borderRadius = "8px"
            padding = "16px"
            backgroundColor = if (theme == "dark") "#1e1e1e" else "#ffffff"
            color = if (theme == "dark") "#e0e0e0" else "#333333"
        }
        h3 { +title }
        block()
    }
}

// --- Counter component ---
val Counter = fc("Counter") {
    var count by state(0)
    var step by state(1)

    card("Counter") {
        p { +"Count: $count (step: $step)" }
        div {
            className("button-group")
            button { +"-"; onClick { count -= step } }
            button { +"+"; onClick { count += step } }
            button { +"Reset"; onClick { count = 0 } }
        }
        input("number") {
            value("$step")
            onChange { step = it.target.value.toIntOrNull() ?: 1 }
        }
    }
}

// --- Live timer using Flow ---
val LiveTimer = fc("LiveTimer") {
    val seconds by flow {
        var t = 0
        while (true) {
            emit(t++)
            delay(1000)
        }
    }.collectAsState(initial = 0)

    card("Live Timer") {
        p { +"Elapsed: ${seconds}s" }
        p { +"Minutes: ${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}" }
    }
}

// --- Async data loader ---
data class User(val id: Int, val name: String, val email: String)

val UserList = fc("UserList") {
    var users by state<List<User>>(emptyList())
    var loading by state(true)
    var error by state<String?>(null)

    launchedEffect {
        try {
            users = api.fetchUsers()
            loading = false
        } catch (e: Exception) {
            error = e.message
            loading = false
        }
    }

    card("Users") {
        when {
            loading -> p { +"Loading users..." }
            error != null -> p { className("error"); +"Error: $error" }
            else -> {
                ul {
                    for (user in users) {
                        li(key = user.id) {
                            strong { +user.name }
                            +" — ${user.email}"
                        }
                    }
                }
                p { className("muted"); +"${users.size} users loaded" }
            }
        }
    }
}

// --- Todo app ---
val TodoApp = fc("TodoApp") {
    var todos by state(listOf("Buy milk", "Write Kotlin DSL"))
    var input by state("")

    card("Todo List") {
        form {
            onSubmit { e ->
                e.preventDefault()
                if (input.isNotBlank()) {
                    todos = todos + input.trim()
                    input = ""
                }
            }
            div {
                className("input-row")
                input("text") {
                    placeholder("Add todo...")
                    value(input)
                    onChange { input = it.target.value }
                }
                button { +"Add" }
            }
        }

        if (todos.isEmpty()) {
            p { className("muted"); +"Nothing to do!" }
        } else {
            ul {
                for ((i, todo) in todos.withIndex()) {
                    li(key = i) {
                        +todo
                        button {
                            className("delete")
                            +"\u00d7"
                            onClick {
                                todos = todos.filterIndexed { idx, _ -> idx != i }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- App root ---
val App = fc("App") {
    var darkMode by state(false)

    ThemeContext.provide(if (darkMode) "dark" else "light") {
        div {
            className(if (darkMode) "app dark" else "app")
            header {
                h1 { +"Kotlin React App" }
                button {
                    +(if (darkMode) "Light Mode" else "Dark Mode")
                    onClick { darkMode = !darkMode }
                }
            }
            div {
                className("grid")
                +Counter
                +LiveTimer
                +UserList
                +TodoApp
            }
        }
    }
}

// --- Entry point ---
fun main() {
    val root = ReactDOM.createRoot(document.getElementById("root")!!)
    root.render(component(App))
}
```

---

## 13. Comparison Table

| Feature | Java API | Kotlin DSL |
|---|---|---|
| **State read** | `count.getInt()` | `count` |
| **State write** | `count.setInt(5)` | `count = 5` |
| **State update** | `count.updateInt(c -> c + 1)` | `count++` |
| **Children** | `.child(H1.create().text("Hi"))` | `h1 { +"Hi" }` |
| **Events** | `.onClick(e -> { ... })` | `onClick { ... }` |
| **Async events** | Manual callback chains | `onClickAsync { api.save() }` |
| **Conditionals** | Ternary / if-else with createElement | `if (x) { p { +"yes" } }` |
| **Lists** | `mapToElements(list, ...)` | `for (item in list) { li { +item } }` |
| **String formatting** | `"Count: " + count.getInt()` | `"Count: $count"` |
| **Props** | Raw `JSObject` | Data classes + named args |
| **Context create** | `ReactContext.create()` | `createContext<String>("default")` |
| **Context consume** | `React.jsToString(Hooks.useContext(...))` | `useContext(ThemeCtx)` — typed |
| **Composition** | New builder class or static method | Extension functions |
| **Component def** | `React.wrapComponent(props -> {...}, "Name")` | `fc("Name") { ... }` |
| **Effects** | `Hooks.useEffect(() -> { ... })` | `effect { ... }` |
| **Async effects** | Manual timer/callback management | `launchedEffect { delay(1000) }` |
| **Data fetching** | Callback-based with manual state | `launchedEffect { data = api.fetch() }` |
| **Reactive streams** | Not available | `flow { emit(x) }.collectAsState(0)` |
| **Suspending handlers** | Not available | `onClickAsync { api.save() }` |
| **Styles** | `React.setProperty(props, "style", obj)` | `style { padding = "20px" }` |
| **Refs** | `Hooks.useRef(null)` / `.getCurrent()` | `var ref by ref<T>()` / `ref?.focus()` |

---

## 14. TeaVM Compatibility Notes

All features in this design compile to standard JVM bytecode that TeaVM can transpile:

- **Delegated properties** — compiled to getter/setter methods; no reflection needed
- **Lambda with receiver** — compiled to regular lambdas with extra parameter; no special runtime
- **Extension functions** — compiled to static methods; zero overhead
- **Reified generics** — inlined at compile time; no runtime reflection
- **Operator overloading** — compiled to regular method calls
- **@DslMarker** — compile-time only annotation; no runtime effect
- **Coroutines** — TeaVM supports `kotlinx.coroutines`; state machine transformation
  happens at the Kotlin compiler level, producing standard bytecode
- **Flow** — built on coroutines; same compatibility story
- **Data classes** — compiled to regular classes with `equals`/`hashCode`/`copy`

### Dependencies

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>${coroutines.version}</version>
</dependency>
```

The Kotlin module would use the `kotlin-maven-plugin` alongside the existing
`teavm-maven-plugin`, compiling Kotlin to JVM bytecode first, then letting
TeaVM transpile it to JavaScript.

---

## 15. Implementation Priorities

Recommended implementation order, from highest to lowest impact:

1. **`state()` delegation** — Immediate, massive DX improvement
2. **HTML builder DSL** — The visual transformation that makes code readable
3. **`fc()` component definition** — Clean component API
4. **`effect()` and `launchedEffect()`** — Coroutine-based lifecycle
5. **`Flow.collectAsState()`** — Reactive state from streams
6. **Typed context** — Eliminates JSObject casting
7. **Style DSL** — Nice-to-have polish
8. **Typed props** — Data class bridge
9. **`onClickAsync`** — Suspending event handlers
10. **`produceState`** — Convenience API
11. **Routing DSL** — Future scope

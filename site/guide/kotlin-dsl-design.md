# Kotlin DSL Design for teavm-react

## Overview

This document specifies the design for **teavm-react-kotlin**, a new module that wraps the existing `teavm-react-core` Java library with an idiomatic Kotlin API. The goal is to provide a developer experience comparable to Jetpack Compose or kotlinx.html while targeting the browser via TeaVM compilation to JavaScript.

The existing Java core provides three API surfaces:

1. **Functional hooks** -- `Hooks.useState()`, `Hooks.useEffect()`, etc. returning `StateHandle<T>` / `RefHandle` wrappers around raw JS arrays.
2. **Builder DSL** -- `DomBuilder.Div.create().child(...).onClick(...).build()` chaining pattern.
3. **Class-based ReactView** -- extend `ReactView`, override `render()`, use hooks in field initializers.

The Kotlin module will not replace the Java core. It will depend on `teavm-react-core` and provide extension functions, inline wrappers, and DSL builders that delegate to the existing `React`, `Hooks`, `Html`, `ElementBuilder`, `DomBuilder`, `StateHandle`, `RefHandle`, and `ReactContext` classes.

### Key Kotlin features leveraged

- **DSL builders** with lambda-with-receiver and `@DslMarker`
- **Delegated properties** (`by state(0)`, `by ref(null)`)
- **Coroutines** (TeaVM supports Kotlin coroutines) for effects, data fetching, and reactive streams
- **Extension functions** for composable component extraction
- **Operator overloading** (`+"text"`, `+component`)
- **Reified generics** for type-safe context and props bridging
- **Data classes** for typed props with default values

---

## 1. Delegated Properties for State

### Problem

The Java API requires calling `Hooks.useState(0)` which returns a `StateHandle<Integer>`. Reading the value requires `count.getInt()` and writing requires `count.setInt(5)`. This is verbose and error-prone -- calling `getString()` on an int handle silently coerces via JS.

### Kotlin design

```kotlin
// Usage inside a component
val Counter = fc("Counter") {
    var count by state(0)          // Int state, initial value 0
    var name by state("Alice")     // String state
    var active by state(true)      // Boolean state
    var price by state(9.99)       // Double state
    var items by state<List<String>>(emptyList())  // Generic state

    div {
        h1 { +"Count: $count" }
        button {
            +"Increment"
            onClick { count++ }    // Direct assignment triggers re-render
        }
    }
}
```

### Implementation sketch

```kotlin
package ca.weblite.teavmreact.kotlin.state

import ca.weblite.teavmreact.hooks.Hooks
import ca.weblite.teavmreact.hooks.StateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegated property backed by React's useState hook.
 * The type parameter determines which StateHandle getter/setter pair is used.
 */
class StateDelegate<T>(private val handle: StateHandle<*>, private val type: StateType) :
    ReadWriteProperty<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = when (type) {
        StateType.INT -> handle.int as T
        StateType.STRING -> handle.string as T
        StateType.BOOL -> handle.bool as T
        StateType.DOUBLE -> handle.double as T
        StateType.OBJECT -> handle.get() as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = when (type) {
        StateType.INT -> handle.setInt(value as Int)
        StateType.STRING -> handle.setString(value as String)
        StateType.BOOL -> handle.setBool(value as Boolean)
        StateType.DOUBLE -> handle.setDouble(value as Double)
        StateType.OBJECT -> handle.set(value as org.teavm.jso.JSObject)
    }
}

enum class StateType { INT, STRING, BOOL, DOUBLE, OBJECT }

// --- Factory functions called inside ComponentScope ---

fun ComponentScope.state(initial: Int): StateDelegate<Int> =
    StateDelegate(Hooks.useState(initial), StateType.INT)

fun ComponentScope.state(initial: String): StateDelegate<String> =
    StateDelegate(Hooks.useState(initial), StateType.STRING)

fun ComponentScope.state(initial: Boolean): StateDelegate<Boolean> =
    StateDelegate(Hooks.useState(initial), StateType.BOOL)

fun ComponentScope.state(initial: Double): StateDelegate<Double> =
    StateDelegate(Hooks.useState(initial), StateType.DOUBLE)

inline fun <reified T> ComponentScope.state(initial: T): StateDelegate<T> =
    StateDelegate(Hooks.useState(toJSObject(initial)), StateType.OBJECT)
```

---

## 2. Type-Safe HTML Builder DSL

### Problem

The Java `Html` class uses varargs children (`div(child1, child2)`) and the `DomBuilder` uses `.child()` chaining. Neither provides the nesting clarity of HTML structure, and adding attributes requires breaking out to `ElementBuilder`.

### Kotlin design

```kotlin
@DslMarker
annotation class HtmlDsl

@HtmlDsl
class HtmlBuilder {
    internal val children = mutableListOf<ReactElement>()
    internal val props: JSObject = React.createObject()

    // --- Text nodes via unaryPlus ---
    operator fun String.unaryPlus() {
        children.add(Html.text(this))
    }

    // --- Render a ReactElement child ---
    operator fun ReactElement.unaryPlus() {
        children.add(this)
    }

    // --- Render a list of elements ---
    operator fun List<ReactElement>.unaryPlus() {
        children.addAll(this)
    }

    // --- Attribute setters ---
    fun className(value: String) { React.setProperty(props, "className", value) }
    fun id(value: String) { React.setProperty(props, "id", value) }
    fun key(value: String) { React.setProperty(props, "key", value) }
    fun key(value: Int) { React.setProperty(props, "key", value) }

    // --- Style sub-builder ---
    fun style(block: StyleBuilder.() -> Unit) {
        val styleObj = StyleBuilder().apply(block).build()
        React.setProperty(props, "style", styleObj)
    }

    // --- Event handlers (use React.setOn* to preserve JS function refs) ---
    fun onClick(handler: EventHandler) { React.setOnClick(props, handler) }
    fun onChange(handler: ChangeEventHandler) { React.setOnChange(props, handler) }
    fun onKeyDown(handler: KeyboardEventHandler) { React.setOnKeyDown(props, handler) }
    fun onKeyUp(handler: KeyboardEventHandler) { React.setOnKeyUp(props, handler) }
    fun onFocus(handler: FocusEventHandler) { React.setOnFocus(props, handler) }
    fun onBlur(handler: FocusEventHandler) { React.setOnBlur(props, handler) }
    fun onSubmit(handler: SubmitEventHandler) { React.setOnSubmit(props, handler) }

    // --- Form attributes ---
    fun value(v: String) { React.setProperty(props, "value", v) }
    fun placeholder(v: String) { React.setProperty(props, "placeholder", v) }
    fun disabled(v: Boolean) { React.setProperty(props, "disabled", v) }
    fun checked(v: Boolean) { React.setProperty(props, "checked", v) }
    fun type(v: String) { React.setProperty(props, "type", v) }
    fun href(v: String) { React.setProperty(props, "href", v) }
    fun src(v: String) { React.setProperty(props, "src", v) }
    fun alt(v: String) { React.setProperty(props, "alt", v) }
    fun name(v: String) { React.setProperty(props, "name", v) }
    fun htmlFor(v: String) { React.setProperty(props, "htmlFor", v) }

    // --- Ref attachment ---
    fun ref(refDelegate: RefDelegate<*>) {
        React.setProperty(props, "ref", refDelegate.raw())
    }

    // --- Generic prop ---
    fun prop(name: String, value: String) { React.setProperty(props, name, value) }
    fun prop(name: String, value: Int) { React.setProperty(props, name, value) }
    fun prop(name: String, value: Boolean) { React.setProperty(props, name, value) }
    fun prop(name: String, value: JSObject) { React.setProperty(props, name, value) }

    internal fun build(tag: String): ReactElement {
        if (children.isEmpty()) {
            return React.createElement(tag, props)
        }
        return React.createElement(tag, props, children.toTypedArray() as Array<JSObject>)
    }
}
```

### Element functions

```kotlin
// Each HTML element is a top-level function usable inside any HtmlBuilder scope.
// The optional key parameter is set on the props before building.

inline fun HtmlBuilder.div(key: String? = null, block: HtmlBuilder.() -> Unit = {}) {
    val builder = HtmlBuilder().apply(block)
    key?.let { React.setProperty(builder.props, "key", it) }
    children.add(builder.build("div"))
}

inline fun HtmlBuilder.span(key: String? = null, block: HtmlBuilder.() -> Unit = {}) {
    val builder = HtmlBuilder().apply(block)
    key?.let { React.setProperty(builder.props, "key", it) }
    children.add(builder.build("span"))
}

inline fun HtmlBuilder.h1(key: String? = null, block: HtmlBuilder.() -> Unit = {}) {
    val builder = HtmlBuilder().apply(block)
    key?.let { React.setProperty(builder.props, "key", it) }
    children.add(builder.build("h1"))
}

// ... same pattern for h2, h3, h4, h5, h6, p, pre, code, blockquote,
//     em, strong, small, ul, ol, li, dl, dt, dd, table, thead, tbody,
//     tfoot, tr, th, td, caption, form, fieldset, legend, label,
//     section, article, aside, header, footer, main, nav, figure,
//     figcaption, details, summary, button, input, textarea, select, a, img

// Void elements (no children)
inline fun HtmlBuilder.hr() { children.add(Html.hr()) }
inline fun HtmlBuilder.br() { children.add(Html.br()) }
```

### Usage

```kotlin
div {
    className("container")
    h1 { +"Hello World" }
    p {
        +"This is a "
        strong { +"bold" }
        +" paragraph."
    }
    ul {
        li(key = "a") { +"Item A" }
        li(key = "b") { +"Item B" }
        li(key = "c") { +"Item C" }
    }
}
```

---

## 3. Component Definition with `fc { }`

### Problem

Java component definition requires `React.wrapComponent((props) -> { ... }, "Name")` and manually dealing with `JSObject` props. There is no scope object providing hooks in a discoverable way.

### Kotlin design

```kotlin
/**
 * Define a functional component. The lambda receiver is a ComponentScope,
 * which provides access to state(), effect(), memo(), ref(), context(), etc.
 *
 * The lambda must return a ReactElement (the component's render output).
 */
fun fc(name: String, render: ComponentScope.() -> ReactElement): FunctionComponent {
    val jsComponent = React.wrapComponent(
        { props ->
            val scope = ComponentScope(props)
            scope.render()
        },
        name
    )
    return FunctionComponent(jsComponent, name)
}

/**
 * Typed variant: the component receives typed props.
 */
inline fun <reified P : Any> fc(
    name: String,
    crossinline render: ComponentScope.(P) -> ReactElement
): TypedFunctionComponent<P> {
    val jsComponent = React.wrapComponent(
        { props ->
            val scope = ComponentScope(props)
            val typedProps = propsFromJS<P>(props)
            scope.render(typedProps)
        },
        name
    )
    return TypedFunctionComponent(jsComponent, name)
}

class FunctionComponent(internal val jsComponent: JSObject, val name: String) {
    /** Render this component with no props. */
    operator fun invoke(): ReactElement = React.createElement(jsComponent, null)

    /** Render with raw JS props. */
    operator fun invoke(props: JSObject): ReactElement =
        React.createElement(jsComponent, props)
}
```

### ComponentScope

```kotlin
@HtmlDsl
class ComponentScope(val rawProps: JSObject) {
    // State delegates (from Section 1)
    fun state(initial: Int) = StateDelegate<Int>(Hooks.useState(initial), StateType.INT)
    fun state(initial: String) = StateDelegate<String>(Hooks.useState(initial), StateType.STRING)
    fun state(initial: Boolean) = StateDelegate<Boolean>(Hooks.useState(initial), StateType.BOOL)
    fun state(initial: Double) = StateDelegate<Double>(Hooks.useState(initial), StateType.DOUBLE)
    inline fun <reified T> state(initial: T) = StateDelegate<T>(
        Hooks.useState(toJSObject(initial)), StateType.OBJECT
    )

    // Ref delegates (from Section 11)
    inline fun <reified T : JSObject> ref(initial: T?) = RefDelegate<T>(
        Hooks.useRef(initial)
    )

    // Effect (from Section 5)
    fun effect(deps: Array<JSObject>? = null, block: EffectScope.() -> Unit) { ... }
    fun launchedEffect(vararg deps: Any?, block: suspend CoroutineScope.() -> Unit) { ... }

    // Memo
    fun <T : JSObject> memo(vararg deps: JSObject, factory: () -> T): T =
        Hooks.useMemo({ factory() }, deps) as T

    // Context (from Section 6)
    inline fun <reified T> useContext(ctx: TypedContext<T>): T = ctx.use()

    // HTML builder entry point -- creates the root element tree
    fun html(block: HtmlBuilder.() -> Unit): ReactElement {
        val builder = HtmlBuilder().apply(block)
        // If single child, return it; otherwise wrap in Fragment
        return when (builder.children.size) {
            0 -> React.createElement("span", null) // empty
            1 -> builder.children[0]
            else -> React.createElement(
                React.fragment(), null,
                builder.children.toTypedArray() as Array<JSObject>
            )
        }
    }
}
```

### Usage

```kotlin
val Counter = fc("Counter") {
    var count by state(0)

    html {
        div {
            h1 { +"Count: $count" }
            button {
                +"Increment"
                onClick { count++ }
            }
            button {
                +"Reset"
                onClick { count = 0 }
            }
        }
    }
}

// Render in the app
val App = fc("App") {
    html {
        div {
            +Counter()  // invoke with no props
        }
    }
}
```

---

## 4. Props via Data Classes

### Problem

Java props are untyped `JSObject` instances. Properties must be read with `React.jsToString(props)` or similar, and there is no compile-time safety, no default values, and no named-argument invocation.

### Kotlin design

```kotlin
// Define props as a data class
data class GreetingProps(
    val name: String = "World",
    val age: Int = 0,
    val showAge: Boolean = true
)

// Define a typed component
val Greeting = fc<GreetingProps>("Greeting") { props ->
    html {
        div {
            h1 { +"Hello, ${props.name}!" }
            if (props.showAge) {
                p { +"Age: ${props.age}" }
            }
        }
    }
}

// Invoke with named arguments -- type-safe at call site
val App = fc("App") {
    html {
        div {
            +Greeting(name = "Alice", age = 30)
            +Greeting(name = "Bob", showAge = false)
            +Greeting()  // uses defaults: name="World", age=0, showAge=true
        }
    }
}
```

### TypedFunctionComponent with operator invoke

```kotlin
class TypedFunctionComponent<P : Any>(
    internal val jsComponent: JSObject,
    val name: String
) {
    /** Invoke with no props (uses data class defaults). */
    operator fun invoke(): ReactElement =
        React.createElement(jsComponent, null)

    /** Invoke with raw JS props. */
    operator fun invoke(props: JSObject): ReactElement =
        React.createElement(jsComponent, props)
}

/**
 * For each typed component, generate an invoke overload that accepts
 * the data class constructor parameters as named arguments.
 *
 * This is achieved via an inline function with reified type that
 * converts the data class to a JSObject via property iteration.
 */
inline fun <reified P : Any> TypedFunctionComponent<P>.invoke(
    builder: () -> P
): ReactElement {
    val props = builder()
    val jsProps = propsToJS(props)
    return React.createElement(jsComponent, jsProps)
}

// --- Props bridge ---

/**
 * Converts a data class instance to a JSObject by iterating its
 * declared properties via reflection (Kotlin reflection is available
 * in TeaVM for data classes compiled to JS).
 */
inline fun <reified P : Any> propsToJS(props: P): JSObject {
    val obj = React.createObject()
    for (prop in P::class.members.filterIsInstance<kotlin.reflect.KProperty1<P, *>>()) {
        val value = prop.get(props)
        when (value) {
            is String -> React.setProperty(obj, prop.name, value)
            is Int -> React.setProperty(obj, prop.name, value)
            is Boolean -> React.setProperty(obj, prop.name, value)
            is Double -> React.setProperty(obj, prop.name, value)
            is JSObject -> React.setProperty(obj, prop.name, value)
            null -> { /* skip nulls */ }
        }
    }
    return obj
}

/**
 * Converts a JSObject back to a data class instance.
 * Used internally when a typed component receives raw JS props.
 */
inline fun <reified P : Any> propsFromJS(jsProps: JSObject): P {
    // Implementation uses the data class primary constructor,
    // reading each parameter name from the JSObject.
    val constructor = P::class.constructors.first()
    val args = constructor.parameters.associateWith { param ->
        when (param.type.classifier) {
            String::class -> React.jsToString(getJSProperty(jsProps, param.name!!))
            Int::class -> React.jsToInt(getJSProperty(jsProps, param.name!!))
            Boolean::class -> React.jsToBool(getJSProperty(jsProps, param.name!!))
            else -> getJSProperty(jsProps, param.name!!)
        }
    }
    return constructor.callBy(args)
}
```

### Call-site ergonomics

Because `TypedFunctionComponent` has `operator fun invoke`, components are called like functions:

```kotlin
// These are all equivalent:
+Greeting(name = "Alice", age = 30)
+Greeting.invoke { GreetingProps(name = "Alice", age = 30) }
+Greeting(GreetingProps(name = "Alice", age = 30).toJS())
```

The first form is syntactic sugar generated by a compiler plugin or achieved via the invoke overload that accepts the data class constructor parameters directly. If a compiler plugin is not feasible, the `invoke { }` lambda form is the primary API.

---

## 5. Coroutine-Based Effects

This is the centerpiece of the Kotlin DSL. TeaVM supports Kotlin coroutines, which opens up structured concurrency for component lifecycle management, async data fetching, and reactive streams.

### 5a. LaunchedEffect (Compose-inspired)

A suspending block that runs in a `CoroutineScope` tied to the component lifecycle. Automatically cancelled on unmount via structured concurrency.

```kotlin
val DataLoader = fc("DataLoader") {
    var data by state<List<Item>>(emptyList())
    var loading by state(true)
    var error by state<String?>(null)

    // Runs once on mount (no deps), cancels on unmount
    launchedEffect {
        try {
            val result = fetchItems()   // suspend fun -- non-blocking
            data = result
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    html {
        div {
            if (loading) {
                p { +"Loading..." }
            } else if (error != null) {
                p { +"Error: $error" }
            } else {
                ul {
                    items(data) { item ->
                        li { +item.name }
                    }
                }
            }
        }
    }
}
```

With dependencies -- re-launches when `url` changes:

```kotlin
val RemoteData = fc("RemoteData") {
    var url by state("/api/items")
    var data by state<String>("")

    // Re-runs whenever url changes. Previous coroutine is cancelled.
    launchedEffect(url) {
        data = httpGet(url)   // suspend fun
    }

    html {
        div {
            input {
                value(url)
                onChange { url = it.value }
            }
            pre { +data }
        }
    }
}
```

### Implementation

```kotlin
/**
 * Launches a coroutine on mount (or when deps change). The previous
 * coroutine is cancelled before re-launching. On unmount, the coroutine
 * is cancelled via structured concurrency.
 */
fun ComponentScope.launchedEffect(
    vararg deps: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    val jsDeps = if (deps.isEmpty()) Hooks.deps() else deps.map { toJSDep(it) }.toTypedArray()

    Hooks.useEffect({
        val scope = ReactCoroutineScope()
        scope.launch { block() }

        // Return cleanup function: cancel all coroutines in this scope
        return@useEffect VoidCallback { scope.cancel() }
    }, jsDeps)
}

/**
 * CoroutineScope tied to a React component's lifecycle.
 * Uses Dispatchers.Main (maps to setTimeout/requestAnimationFrame in TeaVM).
 */
class ReactCoroutineScope : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    fun cancel() {
        job.cancel()
    }
}
```

### 5b. Coroutine-aware effect with cleanup

For effects that need explicit cleanup beyond coroutine cancellation:

```kotlin
val TickerComponent = fc("Ticker") {
    var tick by state(0)

    effect(deps = Hooks.deps()) {
        val job = launch {
            while (isActive) {
                delay(1000)
                tick++
            }
        }

        onCleanup {
            job.cancel()
        }
    }

    html {
        p { +"Tick: $tick" }
    }
}
```

### Implementation

```kotlin
class EffectScope : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main
    private var cleanupFn: (() -> Unit)? = null

    fun onCleanup(block: () -> Unit) {
        cleanupFn = block
    }

    internal fun cleanup() {
        cleanupFn?.invoke()
        job.cancel()
    }
}

fun ComponentScope.effect(
    deps: Array<JSObject>? = null,
    block: EffectScope.() -> Unit
) {
    val effectCallback = EffectCallback {
        val scope = EffectScope()
        scope.block()
        return@EffectCallback VoidCallback { scope.cleanup() }
    }

    if (deps != null) {
        Hooks.useEffect(effectCallback, deps)
    } else {
        Hooks.useEffect(effectCallback)
    }
}
```

### 5c. Flow-based State (reactive streams)

Collect a Kotlin `Flow` as component state. Collection starts on mount and is cancelled on unmount.

```kotlin
val Timer = fc("Timer") {
    // Flow that emits every second, collected as delegated state
    val seconds by flow {
        var t = 0
        while (true) {
            emit(t++)
            delay(1000)
        }
    }.collectAsState(initial = 0)

    html {
        p { +"Elapsed: ${seconds}s" }
    }
}
```

A more practical example with data streams:

```kotlin
val LiveFeed = fc("LiveFeed") {
    val messages by webSocketFlow("wss://feed.example.com")
        .collectAsState(initial = emptyList<Message>())

    html {
        ul {
            items(messages) { msg ->
                li { +"${msg.user}: ${msg.text}" }
            }
        }
    }
}
```

### Implementation

```kotlin
/**
 * Wraps Flow collection into a React state delegate.
 * Starts collection in a LaunchedEffect (mount), cancels on unmount.
 */
class FlowStateCollector<T>(
    private val flow: Flow<T>,
    private val initial: T
) {
    fun collectAsState(scope: ComponentScope): StateDelegate<T> {
        val delegate = scope.state(initial)

        scope.launchedEffect {
            flow.collect { value ->
                // Update React state on each emission
                delegate.setValue(null, TODO_PROP, value)
            }
        }

        return delegate
    }
}

fun <T> Flow<T>.collectAsState(initial: T): FlowStateProvider<T> =
    FlowStateProvider(this, initial)

class FlowStateProvider<T>(
    internal val flow: Flow<T>,
    internal val initial: T
)

// Extension on ComponentScope so `by` delegation works
operator fun <T> FlowStateProvider<T>.provideDelegate(
    thisRef: ComponentScope,
    property: KProperty<*>
): StateDelegate<T> {
    val stateDelegate = thisRef.state(initial)
    thisRef.launchedEffect {
        flow.collect { value ->
            // Triggers React re-render
            stateDelegate.setValue(null, property, value)
        }
    }
    return stateDelegate
}
```

### 5d. Suspending Event Handlers

Event handlers that need to perform async work (API calls, etc.):

```kotlin
val SaveForm = fc("SaveForm") {
    var data by state("")
    var saving by state(false)
    var message by state("")

    html {
        div {
            input {
                value(data)
                onChange { data = it.value }
            }
            button {
                +"Save"
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
            p { +message }
        }
    }
}
```

### Implementation

```kotlin
/**
 * Async click handler. Launches a coroutine in the component's scope.
 * Multiple clicks can be handled concurrently or debounced as configured.
 */
fun HtmlBuilder.onClickAsync(
    block: suspend CoroutineScope.() -> Unit
) {
    val scope = ReactCoroutineScope()  // Created per-render, but Job is stable via ref
    onClick {
        scope.launch { block() }
    }
}

// Same pattern for other events:
fun HtmlBuilder.onSubmitAsync(block: suspend CoroutineScope.() -> Unit) { ... }
fun HtmlBuilder.onChangeAsync(block: suspend CoroutineScope.(ChangeEvent) -> Unit) { ... }
```

### 5e. produceState (Compose-inspired)

A convenience that combines state declaration and async population:

```kotlin
val UserProfile = fc("UserProfile") {
    val userId by state("user-123")

    // produceState: declares state + launches coroutine to populate it
    val profile by produceState<UserProfile?>(null, userId) {
        value = api.fetchUser(userId)  // suspend call
    }

    html {
        div {
            if (profile == null) {
                p { +"Loading profile..." }
            } else {
                h1 { +profile!!.name }
                p { +profile!!.email }
            }
        }
    }
}
```

### Implementation

```kotlin
class ProduceStateScope<T>(
    private val stateDelegate: StateDelegate<T>
) {
    var value: T
        get() = stateDelegate.getValue(null, TODO_PROP)
        set(v) = stateDelegate.setValue(null, TODO_PROP, v)
}

fun <T> ComponentScope.produceState(
    initial: T,
    vararg deps: Any?,
    producer: suspend ProduceStateScope<T>.() -> Unit
): StateDelegate<T> {
    val delegate = state(initial)
    val scope = ProduceStateScope(delegate)

    launchedEffect(*deps) {
        scope.producer()
    }

    return delegate
}
```

---

## 6. Context API with Reified Generics

### Problem

Java's `ReactContext` wraps a raw `JSObject`. Values must be manually cast after `Hooks.useContext()`. There is no type safety.

### Kotlin design

```kotlin
// Create a typed context with a default value
val ThemeCtx = createContext("light")     // TypedContext<String>
val UserCtx = createContext<User?>(null)  // TypedContext<User?>
val LocaleCtx = createContext("en-US")    // TypedContext<String>

// Provide values to children
val App = fc("App") {
    html {
        ThemeCtx.provide("dark") {
            UserCtx.provide(currentUser) {
                +MainContent()
            }
        }
    }
}

// Consume in a child component -- fully typed, no casting
val ThemedButton = fc("ThemedButton") {
    val theme = useContext(ThemeCtx)      // type is String
    val user = useContext(UserCtx)        // type is User?
    val locale = useContext(LocaleCtx)    // type is String

    html {
        button {
            className(if (theme == "dark") "btn-dark" else "btn-light")
            +"${user?.name ?: "Guest"} ($locale)"
        }
    }
}
```

### Implementation

```kotlin
class TypedContext<T>(
    internal val reactContext: ReactContext,
    private val fromJS: (JSObject) -> T,
    private val toJS: (T) -> JSObject
) {
    /**
     * Provider DSL: wraps children with this context value.
     */
    fun HtmlBuilder.provide(value: T, block: HtmlBuilder.() -> Unit) {
        val jsValue = toJS(value)
        val childBuilder = HtmlBuilder().apply(block)
        val childElements = childBuilder.children.toTypedArray() as Array<JSObject>
        val element = reactContext.provide(jsValue, *childBuilder.children.toTypedArray())
        children.add(element)
    }

    /**
     * Read the current context value inside a ComponentScope.
     */
    fun use(): T {
        val raw = Hooks.useContext(reactContext.jsContext())
        return fromJS(raw)
    }
}

// --- Factory functions using reified generics ---

inline fun <reified T> createContext(defaultValue: T): TypedContext<T> {
    val jsDefault = toJSObject(defaultValue)
    val ctx = ReactContext.create(jsDefault)

    return TypedContext(
        reactContext = ctx,
        fromJS = { js -> fromJSObject<T>(js) },
        toJS = { value -> toJSObject(value) }
    )
}

// ComponentScope extension for ergonomic consumption
inline fun <reified T> ComponentScope.useContext(ctx: TypedContext<T>): T = ctx.use()
```

---

## 7. Style DSL

### Problem

Java styles require manually constructing a `JSObject` and calling `React.setProperty()` for each CSS property. No type safety, no autocompletion.

### Kotlin design

```kotlin
val StyledComponent = fc("StyledComponent") {
    html {
        div {
            style {
                backgroundColor = "#f0f0f0"
                padding = "20px"
                borderRadius = "8px"
                display = Display.FLEX
                flexDirection = FlexDirection.COLUMN
                gap = "12px"
            }
            h1 {
                style { color = "#333"; fontSize = "24px" }
                +"Styled heading"
            }
        }
    }
}
```

Shorthand for quick inline styles:

```kotlin
div {
    css("background: red; padding: 10px; color: white")
    +"Quick styled div"
}
```

### Implementation

```kotlin
class StyleBuilder {
    private val obj: JSObject = React.createObject()

    // --- Layout ---
    var display: Display? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "display", it.value) } }
    var flexDirection: FlexDirection? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "flexDirection", it.value) } }
    var justifyContent: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "justifyContent", it) } }
    var alignItems: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "alignItems", it) } }
    var flexWrap: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "flexWrap", it) } }
    var gap: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "gap", it) } }

    // --- Sizing ---
    var width: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "width", it) } }
    var height: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "height", it) } }
    var minWidth: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "minWidth", it) } }
    var maxWidth: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "maxWidth", it) } }

    // --- Spacing ---
    var margin: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "margin", it) } }
    var padding: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "padding", it) } }
    var marginTop: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "marginTop", it) } }
    var marginBottom: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "marginBottom", it) } }
    var paddingTop: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "paddingTop", it) } }
    var paddingBottom: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "paddingBottom", it) } }

    // --- Colors ---
    var color: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "color", it) } }
    var backgroundColor: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "backgroundColor", it) } }

    // --- Typography ---
    var fontSize: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "fontSize", it) } }
    var fontWeight: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "fontWeight", it) } }
    var textAlign: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "textAlign", it) } }
    var lineHeight: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "lineHeight", it) } }

    // --- Border ---
    var border: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "border", it) } }
    var borderRadius: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "borderRadius", it) } }

    // --- Position ---
    var position: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "position", it) } }
    var top: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "top", it) } }
    var left: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "left", it) } }
    var right: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "right", it) } }
    var bottom: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "bottom", it) } }

    // --- Other ---
    var overflow: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "overflow", it) } }
    var cursor: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "cursor", it) } }
    var opacity: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "opacity", it) } }
    var transition: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "transition", it) } }
    var boxShadow: String? = null
        set(value) { field = value; value?.let { React.setProperty(obj, "boxShadow", it) } }

    // --- Generic fallback for properties not listed above ---
    fun set(property: String, value: String) {
        React.setProperty(obj, property, value)
    }

    fun build(): JSObject = obj
}

// --- Type-safe enums for common values ---

enum class Display(val value: String) {
    FLEX("flex"), BLOCK("block"), INLINE("inline"),
    INLINE_BLOCK("inline-block"), GRID("grid"), NONE("none"),
    INLINE_FLEX("inline-flex")
}

enum class FlexDirection(val value: String) {
    ROW("row"), COLUMN("column"),
    ROW_REVERSE("row-reverse"), COLUMN_REVERSE("column-reverse")
}

// --- Shorthand CSS parser ---

fun HtmlBuilder.css(cssString: String) {
    val styleObj = React.createObject()
    cssString.split(";").map { it.trim() }.filter { it.isNotEmpty() }.forEach { decl ->
        val (prop, value) = decl.split(":").map { it.trim() }
        val camelProp = prop.replace(Regex("-([a-z])")) { it.groupValues[1].uppercase() }
        React.setProperty(styleObj, camelProp, value)
    }
    React.setProperty(props, "style", styleObj)
}
```

---

## 8. Conditional Rendering and Lists

### Problem

Java requires ternary-like patterns and manual array construction for lists. Conditional rendering is awkward with `condition ? element : null` patterns that do not translate cleanly to Java.

### Kotlin design

Natural Kotlin control flow works inside the DSL:

```kotlin
val Dashboard = fc("Dashboard") {
    var loggedIn by state(true)
    var items by state(listOf("Alpha", "Bravo", "Charlie"))
    var selectedTab by state("home")

    html {
        div {
            // --- if/else ---
            if (loggedIn) {
                h1 { +"Welcome back!" }
            } else {
                h1 { +"Please log in" }
            }

            // --- when ---
            when (selectedTab) {
                "home" -> div { +"Home content" }
                "settings" -> div { +"Settings content" }
                "profile" -> div { +"Profile content" }
            }

            // --- for loop with keys ---
            ul {
                for ((index, item) in items.withIndex()) {
                    li(key = item) { +"$index: $item" }
                }
            }

            // --- items() helper for keyed lists ---
            ul {
                items(items, key = { it }) { item ->
                    li { +item }
                }
            }

            // --- show() convenience ---
            show(loggedIn) {
                p { +"This is only visible when logged in" }
            }
        }
    }
}
```

### Implementation

```kotlin
/**
 * Renders a keyed list of items. Each item gets a React key derived
 * from the keySelector function.
 */
inline fun <T> HtmlBuilder.items(
    list: List<T>,
    key: (T) -> Any = { it.hashCode() },
    crossinline render: HtmlBuilder.(T) -> Unit
) {
    for (item in list) {
        val itemKey = key(item).toString()
        val builder = HtmlBuilder().apply { render(item) }
        React.setProperty(builder.props, "key", itemKey)
        // Wrap in a fragment with key if multiple children,
        // or set key on the single child
        if (builder.children.size == 1) {
            children.add(builder.children[0])  // key already set on props
        } else {
            val fragment = React.createElement(
                React.fragment(),
                builder.props,
                builder.children.toTypedArray() as Array<JSObject>
            )
            children.add(fragment)
        }
    }
}

/**
 * Conditionally renders content.
 */
inline fun HtmlBuilder.show(condition: Boolean, block: HtmlBuilder.() -> Unit) {
    if (condition) {
        block()
    }
}

/**
 * Conditionally renders content with an else branch.
 */
inline fun HtmlBuilder.showOrElse(
    condition: Boolean,
    block: HtmlBuilder.() -> Unit,
    elseBlock: HtmlBuilder.() -> Unit
) {
    if (condition) block() else elseBlock()
}
```

---

## 9. Extension Functions for Reusable Components

### Problem

Java requires creating a full `ReactView` subclass or `React.wrapComponent()` call even for small reusable UI fragments. There is no lightweight composition mechanism.

### Kotlin design

Extension functions on `HtmlBuilder` serve as lightweight composable "components" with zero overhead -- no React component boundary, no hook rules, just code reuse:

```kotlin
// --- Define a reusable card component ---
fun HtmlBuilder.card(title: String, block: HtmlBuilder.() -> Unit) {
    div {
        className("card")
        style {
            border = "1px solid #ddd"
            borderRadius = "8px"
            padding = "16px"
            marginBottom = "12px"
        }
        h3 {
            style { marginTop = "0" }
            +title
        }
        div {
            className("card-body")
            block()  // Render caller's children here
        }
    }
}

// --- Define a reusable badge ---
fun HtmlBuilder.badge(text: String, color: String = "#007bff") {
    span {
        style {
            backgroundColor = color
            this.color = "white"
            padding = "2px 8px"
            borderRadius = "12px"
            fontSize = "12px"
        }
        +text
    }
}

// --- Define a labeled input ---
fun HtmlBuilder.labeledInput(
    label: String,
    value: String,
    onChange: ChangeEventHandler
) {
    div {
        className("form-group")
        label { +label }
        input {
            type("text")
            value(value)
            onChange(onChange)
        }
    }
}

// --- Usage: compose naturally ---
val UserCard = fc("UserCard") {
    var name by state("Alice")

    html {
        card("User Profile") {
            p { +"Name: $name" }
            badge("Active", "#28a745")
            labeledInput("Change name:", name) { e ->
                name = e.value
            }
        }
    }
}
```

These compose freely because they are just functions -- no registration, no wrapping, no component boundary overhead.

---

## 10. Operator Overloading

### Design

```kotlin
// --- unaryPlus on String: creates a text node ---
operator fun String.unaryPlus() {
    children.add(Html.text(this))
}

// --- unaryPlus on ReactElement: adds as child ---
operator fun ReactElement.unaryPlus() {
    children.add(this)
}

// --- unaryPlus on List<ReactElement>: splices all as children ---
operator fun List<ReactElement>.unaryPlus() {
    children.addAll(this)
}

// --- unaryPlus on FunctionComponent: renders with no props ---
operator fun FunctionComponent.unaryPlus() {
    children.add(this())
}
```

### Usage

```kotlin
val App = fc("App") {
    html {
        div {
            +"Hello World"                        // String -> text node
            +Counter()                            // Component invocation
            +Greeting(name = "Alice")             // Typed component
            +listOf(itemA, itemB, itemC)          // Splice a list
            +Html.hr()                            // Raw ReactElement
        }
    }
}
```

---

## 11. Ref Delegation

### Problem

Java refs require `Hooks.useRef(null)` returning a `RefHandle`, then `refHandle.getCurrent()` / `refHandle.setCurrent()`. Passing to elements requires `refHandle.raw()`.

### Kotlin design

```kotlin
val FocusInput = fc("FocusInput") {
    var inputRef by ref<HTMLInputElement>(null)

    html {
        div {
            input {
                type("text")
                placeholder("Type here...")
                ref(inputRef)   // Attach ref to element
            }
            button {
                +"Focus Input"
                onClick {
                    inputRef?.focus()  // Direct access, null-safe
                }
            }
        }
    }
}
```

### Implementation

```kotlin
class RefDelegate<T : JSObject?>(
    private val handle: RefHandle
) : ReadWriteProperty<Any?, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? =
        handle.current as? T

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value != null) {
            handle.setCurrent(value)
        }
    }

    /** Returns the raw JS ref object for passing to element props. */
    fun raw(): JSObject = handle.raw()
}

fun <T : JSObject> ComponentScope.ref(initial: T? = null): RefDelegate<T> =
    RefDelegate(Hooks.useRef(initial))
```

---

## 12. Routing DSL (future)

A routing layer can be built on top of the component system. This is a future module (`teavm-react-kotlin-router`) but the DSL shape is designed now for consistency.

```kotlin
val App = fc("App") {
    html {
        router {
            route("/") {
                +HomePage()
            }
            route("/about") {
                +AboutPage()
            }
            route("/users/:id") { params ->
                +UserPage(userId = params["id"]!!)
            }
            route("/settings") {
                +SettingsPage()
            }
            notFound {
                h1 { +"404 - Page Not Found" }
            }
        }
    }
}
```

### Implementation sketch

```kotlin
@HtmlDsl
class RouterBuilder {
    internal val routes = mutableListOf<RouteDefinition>()
    internal var notFoundHandler: (HtmlBuilder.() -> Unit)? = null

    fun route(path: String, render: HtmlBuilder.(Map<String, String>) -> Unit) {
        routes.add(RouteDefinition(path, render))
    }

    fun route(path: String, render: HtmlBuilder.() -> Unit) {
        routes.add(RouteDefinition(path) { _ -> render() })
    }

    fun notFound(render: HtmlBuilder.() -> Unit) {
        notFoundHandler = render
    }
}

data class RouteDefinition(
    val path: String,
    val render: HtmlBuilder.(Map<String, String>) -> Unit
)

fun HtmlBuilder.router(block: RouterBuilder.() -> Unit) {
    val routerBuilder = RouterBuilder().apply(block)
    // Implementation would use window.location and History API
    // to match current path against routes and render the matching one.
    // This wraps a React component that uses useState for the current path
    // and useEffect to listen for popstate events.
}
```

---

## 13. Full Example Application

A complete application demonstrating all features working together:

```kotlin
package ca.weblite.teavmreact.kotlin.demo

import ca.weblite.teavmreact.kotlin.component.*
import ca.weblite.teavmreact.kotlin.dsl.*
import ca.weblite.teavmreact.kotlin.state.*
import ca.weblite.teavmreact.kotlin.hooks.*
import ca.weblite.teavmreact.kotlin.coroutines.*
import ca.weblite.teavmreact.kotlin.context.*
import ca.weblite.teavmreact.kotlin.style.*
import ca.weblite.teavmreact.core.ReactDOM
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

// ---- Data classes for typed props ----

data class TodoItemProps(
    val text: String,
    val completed: Boolean = false,
    val onToggle: (() -> Unit)? = null,
    val onDelete: (() -> Unit)? = null
)

data class Todo(val id: Int, val text: String, val completed: Boolean = false)

// ---- Typed context ----

val ThemeCtx = createContext("light")

// ---- Extension function component: reusable card ----

fun HtmlBuilder.card(title: String, block: HtmlBuilder.() -> Unit) {
    div {
        style {
            border = "1px solid #ddd"
            borderRadius = "8px"
            padding = "16px"
            marginBottom = "16px"
            boxShadow = "0 2px 4px rgba(0,0,0,0.1)"
        }
        h3 {
            style { marginTop = "0"; color = "#333" }
            +title
        }
        block()
    }
}

// ---- Typed component with props ----

val TodoItem = fc<TodoItemProps>("TodoItem") { props ->
    val theme = useContext(ThemeCtx)
    val isDark = theme == "dark"

    html {
        li {
            style {
                display = Display.FLEX
                justifyContent = "space-between"
                alignItems = "center"
                padding = "8px 12px"
                backgroundColor = if (isDark) "#333" else "#fff"
                color = if (isDark) "#eee" else "#333"
                borderBottom = "1px solid ${if (isDark) "#555" else "#eee"}"
            }
            span {
                style {
                    textDecoration = if (props.completed) "line-through" else "none"
                    opacity = if (props.completed) "0.6" else "1"
                    cursor = "pointer"
                }
                onClick { props.onToggle?.invoke() }
                +props.text
            }
            button {
                style {
                    backgroundColor = "#dc3545"
                    color = "white"
                    border = "none"
                    borderRadius = "4px"
                    padding = "4px 8px"
                    cursor = "pointer"
                }
                +"Delete"
                onClick { props.onDelete?.invoke() }
            }
        }
    }
}

// ---- Flow-based timer component ----

val TimerWidget = fc("TimerWidget") {
    val seconds by flow {
        var t = 0
        while (true) {
            emit(t++)
            delay(1000)
        }
    }.collectAsState(initial = 0)

    val theme = useContext(ThemeCtx)

    html {
        div {
            style {
                padding = "8px 16px"
                backgroundColor = if (theme == "dark") "#444" else "#f8f9fa"
                borderRadius = "4px"
                fontFamily = "monospace"
                fontSize = "14px"
            }
            +"Uptime: ${seconds / 60}m ${seconds % 60}s"
        }
    }
}

// ---- Main app with coroutine data fetching ----

val App = fc("App") {
    var theme by state("light")
    var todos by state<List<Todo>>(emptyList())
    var newTodoText by state("")
    var nextId by state(1)
    var loading by state(true)
    var filter by state("all")  // "all", "active", "completed"

    // Coroutine-based data fetching on mount
    launchedEffect {
        try {
            val initialTodos = fetchTodos()  // suspend fun
            todos = initialTodos
            nextId = (initialTodos.maxOfOrNull { it.id } ?: 0) + 1
        } catch (e: Exception) {
            // Handle error -- in production, set an error state
        } finally {
            loading = false
        }
    }

    // Derived values
    val filteredTodos = when (filter) {
        "active" -> todos.filter { !it.completed }
        "completed" -> todos.filter { it.completed }
        else -> todos
    }
    val activeCount = todos.count { !it.completed }

    html {
        ThemeCtx.provide(theme) {
            div {
                style {
                    maxWidth = "600px"
                    margin = "0 auto"
                    padding = "20px"
                    fontFamily = "-apple-system, BlinkMacSystemFont, sans-serif"
                    backgroundColor = if (theme == "dark") "#222" else "#fff"
                    color = if (theme == "dark") "#eee" else "#333"
                    minHeight = "100vh"
                }

                // Header with theme toggle
                div {
                    style {
                        display = Display.FLEX
                        justifyContent = "space-between"
                        alignItems = "center"
                        marginBottom = "20px"
                    }
                    h1 {
                        style { margin = "0" }
                        +"Todo App"
                    }
                    button {
                        +"Toggle ${if (theme == "light") "Dark" else "Light"} Mode"
                        onClick {
                            theme = if (theme == "light") "dark" else "light"
                        }
                    }
                }

                // Timer widget
                +TimerWidget()

                // Add todo form
                card("Add Todo") {
                    div {
                        style { display = Display.FLEX; gap = "8px" }
                        input {
                            type("text")
                            value(newTodoText)
                            placeholder("What needs to be done?")
                            onChange { newTodoText = it.value }
                            onKeyDown { e ->
                                if (e.key == "Enter" && newTodoText.isNotBlank()) {
                                    todos = todos + Todo(nextId, newTodoText)
                                    nextId++
                                    newTodoText = ""
                                }
                            }
                        }
                        button {
                            +"Add"
                            disabled(newTodoText.isBlank())
                            onClick {
                                if (newTodoText.isNotBlank()) {
                                    todos = todos + Todo(nextId, newTodoText)
                                    nextId++
                                    newTodoText = ""
                                }
                            }
                        }
                    }
                }

                // Filter tabs
                div {
                    style {
                        display = Display.FLEX
                        gap = "8px"
                        marginBottom = "12px"
                    }
                    for (tab in listOf("all", "active", "completed")) {
                        button {
                            +tab.replaceFirstChar { it.uppercase() }
                            style {
                                fontWeight = if (filter == tab) "bold" else "normal"
                                textDecoration = if (filter == tab) "underline" else "none"
                            }
                            onClick { filter = tab }
                        }
                    }
                    span {
                        style { marginLeft = "auto" }
                        +"$activeCount items left"
                    }
                }

                // Todo list
                show(loading) {
                    p { +"Loading todos..." }
                }

                show(!loading) {
                    ul {
                        style { listStyle = "none"; padding = "0"; margin = "0" }
                        items(filteredTodos, key = { it.id }) { todo ->
                            +TodoItem(
                                text = todo.text,
                                completed = todo.completed,
                                onToggle = {
                                    todos = todos.map {
                                        if (it.id == todo.id) it.copy(completed = !it.completed)
                                        else it
                                    }
                                },
                                onDelete = {
                                    todos = todos.filter { it.id != todo.id }
                                }
                            )
                        }
                    }
                }

                // Save button with async handler
                show(!loading && todos.isNotEmpty()) {
                    button {
                        style {
                            marginTop = "16px"
                            padding = "8px 16px"
                            backgroundColor = "#28a745"
                            color = "white"
                            border = "none"
                            borderRadius = "4px"
                            cursor = "pointer"
                        }
                        +"Save All"
                        onClickAsync {
                            saveTodos(todos)  // suspend fun
                        }
                    }
                }
            }
        }
    }
}

// ---- Suspend functions (would be implemented with fetch API) ----

suspend fun fetchTodos(): List<Todo> {
    delay(500)  // Simulate network
    return listOf(
        Todo(1, "Learn teavm-react"),
        Todo(2, "Build Kotlin DSL"),
        Todo(3, "Write tests")
    )
}

suspend fun saveTodos(todos: List<Todo>) {
    delay(300)  // Simulate save
}

// ---- Entry point ----

fun main() {
    ReactDOM.renderToId(App(), "root")
}
```

---

## 14. Module Structure

```
teavm-react-kotlin/
  pom.xml                          -- Maven module depending on teavm-react-core
  src/main/kotlin/ca/weblite/teavmreact/kotlin/
    dsl/
      HtmlDsl.kt                   -- @HtmlDsl DslMarker annotation
      HtmlBuilder.kt               -- HtmlBuilder class, text/element unaryPlus
      Elements.kt                  -- div(), span(), h1(), p(), button(), etc.
      VoidElements.kt              -- hr(), br()
    state/
      StateDelegate.kt             -- ReadWriteProperty for React state
      StateType.kt                 -- Enum: INT, STRING, BOOL, DOUBLE, OBJECT
    component/
      FunctionComponent.kt         -- fc(), FunctionComponent, operator invoke
      TypedFunctionComponent.kt    -- fc<P>(), TypedFunctionComponent<P>
      ComponentScope.kt            -- Receiver class with state/effect/memo/ref/context
    hooks/
      EffectDsl.kt                 -- effect(), EffectScope, onCleanup
      MemoDsl.kt                   -- memo() with typed return
      CallbackDsl.kt               -- callback() for memoized lambdas
    coroutines/
      ReactCoroutineScope.kt       -- CoroutineScope tied to component lifecycle
      LaunchedEffect.kt            -- launchedEffect() with structured concurrency
      FlowCollector.kt             -- Flow<T>.collectAsState(), FlowStateProvider
      ProduceState.kt              -- produceState(), ProduceStateScope
      AsyncHandlers.kt             -- onClickAsync(), onSubmitAsync(), etc.
    context/
      TypedContext.kt              -- TypedContext<T> wrapper around ReactContext
      ContextFactory.kt            -- createContext<T>() reified factory
      ProviderDsl.kt               -- TypedContext<T>.provide() extension
    style/
      StyleBuilder.kt              -- CSS property setters, build() -> JSObject
      CssEnums.kt                  -- Display, FlexDirection, Position, etc.
      CssShorthand.kt              -- css() string parser
    props/
      PropsBridge.kt               -- propsToJS(), propsFromJS() inline functions
      PropsMarker.kt               -- Optional annotation for props data classes
    ref/
      RefDelegate.kt               -- ReadWriteProperty for React refs
    util/
      JSConversions.kt             -- toJSObject(), fromJSObject(), toJSDep()
      OperatorExtensions.kt        -- unaryPlus overloads for JSObject, List
      ListHelpers.kt               -- items(), show(), showOrElse()
```

---

## 15. Comparison Table

| Feature | Java API | Kotlin DSL |
|---|---|---|
| **State declaration** | `StateHandle<Integer> count = Hooks.useState(0);` | `var count by state(0)` |
| **State read** | `count.getInt()` | `count` |
| **State write** | `count.setInt(5)` | `count = 5` |
| **Children** | `div(child1, child2)` or `.child(x).child(y)` | `div { +child1; +child2 }` |
| **Text nodes** | `Html.text("hello")` or `div("hello")` | `+"hello"` |
| **Event handlers** | `.onClick(e -> { ... }).build()` | `onClick { ... }` |
| **Async events** | Callback hell or manual promise handling | `onClickAsync { val r = api.call() }` |
| **Conditionals** | `condition ? elementA : elementB` (awkward in Java) | `if (cond) { elementA } else { elementB }` |
| **Lists** | `Html.mapToElements(list, item -> li(item))` | `items(list) { li { +it } }` or `for` loop |
| **String interpolation** | `"Count: " + count.getInt()` | `"Count: $count"` |
| **Props** | `React.createObject()` + `setProperty()` per field | `data class Props(...)` + named args |
| **Context create** | `ReactContext.create(jsValue)` | `createContext<String>("light")` |
| **Context read** | `(String) Hooks.useContext(ctx)` (manual cast) | `val theme = useContext(ThemeCtx)` (typed) |
| **Context provide** | `ctx.provide(jsValue, child1, child2)` | `ThemeCtx.provide("dark") { ... }` |
| **Composition** | New class or `React.wrapComponent()` | Extension function on `HtmlBuilder` |
| **Component def** | `React.wrapComponent(props -> { ... }, "Name")` | `val C = fc("Name") { ... }` |
| **Effects** | `Hooks.useEffect(() -> { ... return cleanup; })` | `effect { ... onCleanup { } }` |
| **Async effects** | Not supported (must use JS Promise interop) | `launchedEffect { val x = fetchData() }` |
| **Data fetching** | Manual XHR/fetch + callback nesting | `launchedEffect { data = api.get() }` |
| **Reactive streams** | Not available | `val x by flow { emit(v) }.collectAsState(init)` |
| **Refs** | `RefHandle ref = Hooks.useRef(null); ref.getCurrent()` | `var ref by ref<T>(null); ref?.focus()` |
| **Styles** | `React.createObject()` + `setProperty()` per prop | `style { backgroundColor = "#fff" }` |
| **Memo** | `Hooks.useMemo(factory, deps)` (returns JSObject) | `val x = memo(deps) { compute() }` (typed) |

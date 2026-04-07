package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.React
import ca.weblite.teavmreact.core.ReactContext
import ca.weblite.teavmreact.core.ReactElement
import ca.weblite.teavmreact.hooks.Hooks
import org.teavm.jso.JSObject

/**
 * Type-safe wrapper around React context. Avoids JSObject casting.
 *
 * ```
 * val ThemeContext = createContext("light")
 *
 * // Provider
 * ThemeContext.provide("dark") {
 *     +App
 * }
 *
 * // Consumer
 * val theme: String = useContext(ThemeContext)
 * ```
 */
class TypedContext<T> @PublishedApi internal constructor(
    @PublishedApi internal val reactContext: ReactContext,
    @PublishedApi internal val serialize: (T) -> JSObject,
    @PublishedApi internal val deserialize: (JSObject) -> T
) {
    /**
     * Wrap children with this context's Provider, supplying the given value.
     */
    fun provide(value: T, block: HtmlBuilder.() -> Unit): ReactElement {
        val builder = HtmlBuilder("__provider__")
        builder.block()
        val jsValue = serialize(value)
        return reactContext.provide(jsValue, *builder.children.toTypedArray())
    }
}

// ========================================================================
// Factory functions
// ========================================================================

/** Create a typed String context */
fun createStringContext(defaultValue: String): TypedContext<String> {
    val ctx = ReactContext.create(React.stringToJS(defaultValue))
    return TypedContext(
        ctx,
        serialize = { React.stringToJS(it) },
        deserialize = { React.jsToString(it) }
    )
}

/** Create a typed Int context */
fun createIntContext(defaultValue: Int): TypedContext<Int> {
    val ctx = ReactContext.create(React.intToJS(defaultValue))
    return TypedContext(
        ctx,
        serialize = { React.intToJS(it) },
        deserialize = { React.jsToInt(it) }
    )
}

/** Create a typed Boolean context */
fun createBoolContext(defaultValue: Boolean): TypedContext<Boolean> {
    val ctx = ReactContext.create(React.boolToJS(defaultValue))
    return TypedContext(
        ctx,
        serialize = { React.boolToJS(it) },
        deserialize = { React.jsToBool(it) }
    )
}

/** Create a typed JSObject context */
fun createContext(defaultValue: JSObject?): TypedContext<JSObject?> {
    val ctx = if (defaultValue != null) {
        ReactContext.create(defaultValue)
    } else {
        ReactContext.create()
    }
    return TypedContext(
        ctx,
        serialize = { it ?: React.createObject() },
        deserialize = { it }
    )
}

// ========================================================================
// useContext — typed consumer
// ========================================================================

/** Read a typed String context value */
fun ComponentScope.useContext(ctx: TypedContext<String>): String {
    val jsValue = Hooks.useContext(ctx.reactContext.jsContext())
    return ctx.deserialize(jsValue)
}

/** Read a typed Int context value */
fun ComponentScope.useContext(ctx: TypedContext<Int>): Int {
    val jsValue = Hooks.useContext(ctx.reactContext.jsContext())
    return ctx.deserialize(jsValue)
}

/** Read a typed Boolean context value */
fun ComponentScope.useContext(ctx: TypedContext<Boolean>): Boolean {
    val jsValue = Hooks.useContext(ctx.reactContext.jsContext())
    return ctx.deserialize(jsValue)
}

/** Read a typed JSObject context value */
fun ComponentScope.useContext(ctx: TypedContext<JSObject?>): JSObject? {
    return Hooks.useContext(ctx.reactContext.jsContext())
}

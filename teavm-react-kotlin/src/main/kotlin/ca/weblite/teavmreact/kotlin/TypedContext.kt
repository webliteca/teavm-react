package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.ReactContext
import ca.weblite.teavmreact.core.ReactElement

/**
 * Type-safe wrapper around React context. Avoids JSObject casting.
 *
 * ```
 * val ThemeContext = createStringContext("light")
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
    @PublishedApi internal val provideImpl: (ReactContext, T, Array<ReactElement>) -> ReactElement,
    @PublishedApi internal val useImpl: (ReactContext) -> T
) {
    /**
     * Wrap children with this context's Provider, supplying the given value.
     */
    fun provide(value: T, block: HtmlBuilder.() -> Unit): ReactElement {
        val builder = HtmlBuilder("__provider__")
        builder.block()
        return provideImpl(reactContext, value, builder.children.toTypedArray())
    }
}

// ========================================================================
// Factory functions
// ========================================================================

/** Create a typed String context */
fun createStringContext(defaultValue: String): TypedContext<String> {
    val ctx = ReactContext.create(defaultValue)
    return TypedContext(
        ctx,
        provideImpl = { rc, value, children -> rc.provide(value, *children) },
        useImpl = { rc -> rc.useString() }
    )
}

/** Create a typed Int context */
fun createIntContext(defaultValue: Int): TypedContext<Int> {
    val ctx = ReactContext.create(defaultValue)
    return TypedContext(
        ctx,
        provideImpl = { rc, value, children -> rc.provide(value, *children) },
        useImpl = { rc -> rc.useInt() }
    )
}

/** Create a typed Boolean context */
fun createBoolContext(defaultValue: Boolean): TypedContext<Boolean> {
    val ctx = ReactContext.create(defaultValue)
    return TypedContext(
        ctx,
        provideImpl = { rc, value, children -> rc.provide(value, *children) },
        useImpl = { rc -> rc.useBool() }
    )
}

// ========================================================================
// useContext — typed consumer
// ========================================================================

/** Read a typed context value during render. */
fun <T> ComponentScope.useContext(ctx: TypedContext<T>): T {
    return ctx.useImpl(ctx.reactContext)
}

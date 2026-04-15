package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.React
import ca.weblite.teavmreact.core.ReactElement
import ca.weblite.teavmreact.core.RenderFunction
import ca.weblite.teavmreact.hooks.Hooks
import org.teavm.jso.JSObject
import kotlin.properties.ReadWriteProperty

/**
 * Scope receiver for function components defined with [fc].
 * Provides access to hooks (state, ref, effect, memo, context) and
 * the component's props.
 *
 * Usage:
 * ```
 * val Counter = fc("Counter") {
 *     var count by state(0)
 *     div {
 *         p { +"Count: $count" }
 *         button { +"Inc"; onClick { count++ } }
 *     }
 * }
 * ```
 */
@HtmlDsl
class ComponentScope(@PublishedApi internal val props: JSObject) {

    // ====================================================================
    // State hooks — delegate to top-level state() functions
    // ====================================================================

    fun state(initial: Int): IntStateDelegate = ca.weblite.teavmreact.kotlin.state(initial)
    fun state(initial: String): StringStateDelegate = ca.weblite.teavmreact.kotlin.state(initial)
    fun state(initial: Boolean): BooleanStateDelegate = ca.weblite.teavmreact.kotlin.state(initial)
    fun state(initial: Double): DoubleStateDelegate = ca.weblite.teavmreact.kotlin.state(initial)
    fun stateList(vararg initial: String): StringListStateDelegate = ca.weblite.teavmreact.kotlin.stateList(*initial)
    fun stateList(initial: List<String> = emptyList()): StringListStateDelegate = ca.weblite.teavmreact.kotlin.stateList(initial)

    // ====================================================================
    // Ref hooks
    // ====================================================================

    fun refInt(initial: Int = 0): IntRefDelegate = ca.weblite.teavmreact.kotlin.refInt(initial)
    fun refString(initial: String = ""): StringRefDelegate = ca.weblite.teavmreact.kotlin.refString(initial)

    // ====================================================================
    // Props access
    // ====================================================================

    /** Read a string prop by name */
    fun propString(name: String): String = React.jsToString(React.getProperty(props, name))

    /** Read an int prop by name */
    fun propInt(name: String): Int = React.jsToInt(React.getProperty(props, name))

    /** Read a boolean prop by name */
    fun propBool(name: String): Boolean = React.jsToBool(React.getProperty(props, name))

    /** Read a raw JSObject prop by name */
    fun propObj(name: String): JSObject = React.getProperty(props, name)

    // ====================================================================
    // Memo hook
    // ====================================================================

    /**
     * Memoize a computed value. Only recalculates when deps change.
     *
     * ```
     * val filtered = memo(items, filter) {
     *     items.filter { it.contains(filter) }
     * }
     * ```
     */
    fun <T> memo(vararg deps: Any?, compute: () -> T): T {
        val jsDeps = depsToJsArray(*deps)
        @Suppress("UNCHECKED_CAST")
        val result = Hooks.useMemo(Hooks.MemoFactory { compute() as JSObject }, jsDeps)
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

}

// ========================================================================
// fc() — define a function component
// ========================================================================

/**
 * Define a React function component with the Kotlin DSL.
 *
 * ```
 * val MyComponent = fc("MyComponent") {
 *     var count by state(0)
 *     div {
 *         p { +"Count: $count" }
 *         button { +"Inc"; onClick { count++ } }
 *     }
 * }
 * ```
 *
 * The returned JSObject can be rendered with `+MyComponent` inside a builder,
 * or passed to [component] for top-level rendering.
 */
fun fc(name: String = "", render: ComponentScope.() -> ReactElement): JSObject {
    return React.wrapComponent(
        RenderFunction { props -> ComponentScope(props).render() },
        name.ifEmpty { "KotlinComponent" }
    )
}

/**
 * Wrap a JSObject component reference into a ReactElement for rendering.
 * Useful at the app root:
 * ```
 * root.render(component(App))
 * ```
 */
fun component(comp: JSObject): ReactElement = React.createElement(comp, null as JSObject?)

/**
 * Wrap a JSObject component with props.
 */
fun component(comp: JSObject, props: JSObject): ReactElement = React.createElement(comp, props)

package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.React
import ca.weblite.teavmreact.hooks.Hooks
import org.teavm.jso.JSObject

// ========================================================================
// Pure functions — no JS runtime dependency, fully unit-testable
// ========================================================================

/**
 * Convert a kebab-case CSS property name to camelCase.
 * E.g. "background-color" -> "backgroundColor", "font-size" -> "fontSize"
 */
fun kebabToCamelCase(kebab: String): String {
    return kebab.replace(Regex("-([a-z])")) { it.groupValues[1].uppercase() }
}

/**
 * Parse a CSS style string into a list of (camelCaseProp, value) pairs.
 * E.g. "background-color: red; padding: 10px" -> [("backgroundColor","red"), ("padding","10px")]
 */
fun parseCssString(styleString: String): List<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    for (pair in styleString.split(";")) {
        val trimmed = pair.trim()
        if (trimmed.isEmpty()) continue
        val colonIdx = trimmed.indexOf(':')
        if (colonIdx <= 0) continue
        val rawProp = trimmed.substring(0, colonIdx).trim()
        val value = trimmed.substring(colonIdx + 1).trim()
        result.add(kebabToCamelCase(rawProp) to value)
    }
    return result
}

/**
 * Encode a list of strings into a single string using a null-char separator.
 * Used internally by [StringListStateDelegate].
 */
fun encodeStringList(items: List<String>): String = items.joinToString(STRING_LIST_SEPARATOR)

/**
 * Decode a null-char-separated string back into a list.
 * Returns empty list for empty input.
 */
fun decodeStringList(encoded: String): List<String> =
    if (encoded.isEmpty()) emptyList() else encoded.split(STRING_LIST_SEPARATOR)

internal const val STRING_LIST_SEPARATOR = "\u0000"

// ========================================================================
// JS-dependent utilities
// ========================================================================

/**
 * Convert vararg dependency values to a JSObject[] array for useEffect deps.
 * Handles Int, String, Boolean, Double, and JSObject values.
 */
fun depsToJsArray(vararg keys: Any?): Array<JSObject> {
    if (keys.isEmpty()) return Hooks.deps()
    return Array(keys.size) { i ->
        val key = keys[i]
        when (key) {
            null -> React.stringToJS("null")
            is Int -> React.intToJS(key)
            is String -> React.stringToJS(key)
            is Boolean -> React.boolToJS(key)
            is Double -> React.intToJS(key.toInt())
            is JSObject -> key
            else -> React.stringToJS(key.toString())
        }
    }
}

/**
 * Convenience extension to render a component with no props inside a builder.
 * Equivalent to `+component` using the unaryPlus operator.
 */
fun HtmlBuilder.render(comp: JSObject) {
    child(React.createElement(comp, null as JSObject?))
}

/**
 * Render a component with props built via a lambda.
 */
fun HtmlBuilder.render(comp: JSObject, propsBlock: PropsBuilder.() -> Unit) {
    val propsObj = PropsBuilder().apply(propsBlock).build()
    child(React.createElement(comp, propsObj))
}

/**
 * Simple props builder for passing ad-hoc props to components.
 */
class PropsBuilder {
    private val obj: JSObject = React.createObject()

    fun set(name: String, value: String) { React.setProperty(obj, name, value) }
    fun set(name: String, value: Int) { React.setProperty(obj, name, value) }
    fun set(name: String, value: Boolean) { React.setProperty(obj, name, value) }
    fun set(name: String, value: Double) { React.setProperty(obj, name, value) }
    fun set(name: String, value: JSObject) { React.setProperty(obj, name, value) }

    operator fun String.invoke(value: String) { set(this, value) }
    operator fun String.invoke(value: Int) { set(this, value) }
    operator fun String.invoke(value: Boolean) { set(this, value) }

    internal fun build(): JSObject = obj
}

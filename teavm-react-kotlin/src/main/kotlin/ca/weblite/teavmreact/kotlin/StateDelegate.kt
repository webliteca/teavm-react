package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.hooks.Hooks
import ca.weblite.teavmreact.hooks.StateHandle
import org.teavm.jso.JSObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegated property for React state. Wraps StateHandle with typed
 * getter/setter so you can write:
 *
 * ```
 * var count by state(0)
 * count++          // triggers React re-render
 * println(count)   // reads current state
 * ```
 *
 * Type-specific overloads avoid boxing and ensure correct JS coercion.
 */

// ========================================================================
// Int state
// ========================================================================

class IntStateDelegate(initial: Int) : ReadWriteProperty<Any?, Int> {
    private val handle: StateHandle<Int> = Hooks.useState(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = handle.int

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        handle.setInt(value)
    }

    /** Functional update: prev => new */
    fun update(updater: StateHandle.IntUpdater) {
        handle.updateInt(updater)
    }
}

fun state(initial: Int): IntStateDelegate = IntStateDelegate(initial)

// ========================================================================
// String state
// ========================================================================

class StringStateDelegate(initial: String) : ReadWriteProperty<Any?, String> {
    private val handle: StateHandle<String> = Hooks.useState(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): String = handle.string

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        handle.setString(value)
    }

    fun update(updater: StateHandle.StringUpdater) {
        handle.updateString(updater)
    }
}

fun state(initial: String): StringStateDelegate = StringStateDelegate(initial)

// ========================================================================
// Boolean state
// ========================================================================

class BooleanStateDelegate(initial: Boolean) : ReadWriteProperty<Any?, Boolean> {
    private val handle: StateHandle<Boolean> = Hooks.useState(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = handle.bool

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        handle.setBool(value)
    }
}

fun state(initial: Boolean): BooleanStateDelegate = BooleanStateDelegate(initial)

// ========================================================================
// Double state
// ========================================================================

class DoubleStateDelegate(initial: Double) : ReadWriteProperty<Any?, Double> {
    private val handle: StateHandle<Double> = Hooks.useState(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Double = handle.double

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        handle.setDouble(value)
    }
}

fun state(initial: Double): DoubleStateDelegate = DoubleStateDelegate(initial)

// ========================================================================
// JSObject state (generic fallback for complex objects)
// ========================================================================

class JsObjectStateDelegate(initial: JSObject?) : ReadWriteProperty<Any?, JSObject?> {
    private val handle: StateHandle<JSObject> = if (initial != null) {
        Hooks.useState(initial)
    } else {
        Hooks.useState(null as JSObject?)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): JSObject? = handle.get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: JSObject?) {
        handle.set(value as JSObject)
    }
}

fun state(initial: JSObject?): JsObjectStateDelegate = JsObjectStateDelegate(initial)

// ========================================================================
// List<String> state — stored as JSON string in JS, exposed as List
// ========================================================================

class StringListStateDelegate(
    initial: List<String>
) : ReadWriteProperty<Any?, List<String>> {
    // Store as a comma-delimited string internally (matching existing pattern)
    // Empty list = empty string, items separated by \u0000 (null char) to avoid conflicts
    private val handle: StateHandle<String> = Hooks.useState(initial.joinToString(SEPARATOR))

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<String> {
        val raw = handle.string
        return if (raw.isEmpty()) emptyList() else raw.split(SEPARATOR)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<String>) {
        handle.setString(value.joinToString(SEPARATOR))
    }

    companion object {
        private const val SEPARATOR = "\u0000"
    }
}

fun stateList(vararg initial: String): StringListStateDelegate =
    StringListStateDelegate(initial.toList())

fun stateList(initial: List<String> = emptyList()): StringListStateDelegate =
    StringListStateDelegate(initial)

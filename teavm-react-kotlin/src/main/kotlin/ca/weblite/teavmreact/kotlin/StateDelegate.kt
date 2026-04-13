package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.hooks.Hooks
import ca.weblite.teavmreact.hooks.StateHandle
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
// List<String> state — stored as JSON string in JS, exposed as List
// ========================================================================

class StringListStateDelegate(
    initial: List<String>
) : ReadWriteProperty<Any?, List<String>> {
    private val handle: StateHandle<String> = Hooks.useState(encodeStringList(initial))

    override fun getValue(thisRef: Any?, property: KProperty<*>): List<String> {
        return decodeStringList(handle.string)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<String>) {
        handle.setString(encodeStringList(value))
    }
}

fun stateList(vararg initial: String): StringListStateDelegate =
    StringListStateDelegate(initial.toList())

fun stateList(initial: List<String> = emptyList()): StringListStateDelegate =
    StringListStateDelegate(initial)

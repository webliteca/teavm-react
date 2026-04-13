package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.hooks.Hooks
import ca.weblite.teavmreact.hooks.RefHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Int ref delegate — stores an int in the ref.
 */
class IntRefDelegate(initial: Int) : ReadWriteProperty<Any?, Int> {
    val handle: RefHandle = Hooks.useRefInt(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = handle.currentInt

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        handle.setCurrentInt(value)
    }
}

fun refInt(initial: Int = 0): IntRefDelegate = IntRefDelegate(initial)

/**
 * String ref delegate.
 */
class StringRefDelegate(initial: String) : ReadWriteProperty<Any?, String> {
    val handle: RefHandle = Hooks.useRefString(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): String = handle.currentString

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        handle.setCurrentString(value)
    }
}

fun refString(initial: String = ""): StringRefDelegate = StringRefDelegate(initial)

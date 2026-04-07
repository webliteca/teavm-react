package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.hooks.Hooks
import ca.weblite.teavmreact.hooks.RefHandle
import org.teavm.jso.JSObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegated property for React refs. Wraps RefHandle so you can write:
 *
 * ```
 * var inputRef by ref<JSObject>()
 * // later:
 * inputRef  // gets ref.current
 * ```
 */
class RefDelegate(initial: JSObject? = null) : ReadWriteProperty<Any?, JSObject?> {
    val handle: RefHandle = if (initial != null) {
        Hooks.useRef(initial)
    } else {
        Hooks.useRef(null as JSObject?)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): JSObject? {
        return handle.current
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: JSObject?) {
        handle.setCurrent(value)
    }
}

/**
 * Create a ref delegate for use with `by`:
 * ```
 * var myRef by ref()
 * ```
 */
fun ref(initial: JSObject? = null): RefDelegate = RefDelegate(initial)

/**
 * Int ref delegate — stores an int in the ref.
 */
class IntRefDelegate(initial: Int) : ReadWriteProperty<Any?, Int> {
    val handle: RefHandle = Hooks.useRefInt(initial)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = handle.currentInt

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        // RefHandle doesn't have setCurrentInt, so we use the JS bridge
        handle.setCurrent(ca.weblite.teavmreact.core.React.intToJS(value))
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
        handle.setCurrent(ca.weblite.teavmreact.core.React.stringToJS(value))
    }
}

fun refString(initial: String = ""): StringRefDelegate = StringRefDelegate(initial)

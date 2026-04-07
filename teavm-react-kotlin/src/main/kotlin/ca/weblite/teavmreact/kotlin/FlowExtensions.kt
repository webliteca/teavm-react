package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.VoidCallback
import ca.weblite.teavmreact.hooks.EffectCallback
import ca.weblite.teavmreact.hooks.Hooks
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Collect a [Flow] into React state. The flow starts collecting on mount
 * and is automatically cancelled on unmount.
 *
 * ```
 * val seconds by flow {
 *     var t = 0
 *     while (true) {
 *         emit(t++)
 *         delay(1000)
 *     }
 * }.collectAsState(initial = 0)
 * ```
 */
fun Flow<Int>.collectAsState(initial: Int): CollectedIntState {
    return CollectedIntState(this, initial)
}

class CollectedIntState(
    private val flow: Flow<Int>,
    initial: Int
) : ReadOnlyProperty<Any?, Int> {
    private val delegate = IntStateDelegate(initial)

    init {
        // Set up effect to collect the flow
        Hooks.useEffect(EffectCallback {
            val job = SupervisorJob()
            val scope = CoroutineScope(job + JsDispatcher)
            scope.launch {
                flow.collect { value ->
                    delegate.setValue(null, DUMMY_PROP, value)
                }
            }
            VoidCallback { job.cancel() }
        }, Hooks.deps())
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return delegate.getValue(thisRef, property)
    }
}

fun Flow<String>.collectAsState(initial: String): CollectedStringState {
    return CollectedStringState(this, initial)
}

class CollectedStringState(
    private val flow: Flow<String>,
    initial: String
) : ReadOnlyProperty<Any?, String> {
    private val delegate = StringStateDelegate(initial)

    init {
        Hooks.useEffect(EffectCallback {
            val job = SupervisorJob()
            val scope = CoroutineScope(job + JsDispatcher)
            scope.launch {
                flow.collect { value ->
                    delegate.setValue(null, DUMMY_PROP, value)
                }
            }
            VoidCallback { job.cancel() }
        }, Hooks.deps())
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return delegate.getValue(thisRef, property)
    }
}

fun Flow<Boolean>.collectAsState(initial: Boolean): CollectedBooleanState {
    return CollectedBooleanState(this, initial)
}

class CollectedBooleanState(
    private val flow: Flow<Boolean>,
    initial: Boolean
) : ReadOnlyProperty<Any?, Boolean> {
    private val delegate = BooleanStateDelegate(initial)

    init {
        Hooks.useEffect(EffectCallback {
            val job = SupervisorJob()
            val scope = CoroutineScope(job + JsDispatcher)
            scope.launch {
                flow.collect { value ->
                    delegate.setValue(null, DUMMY_PROP, value)
                }
            }
            VoidCallback { job.cancel() }
        }, Hooks.deps())
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return delegate.getValue(thisRef, property)
    }
}

fun Flow<Double>.collectAsState(initial: Double): CollectedDoubleState {
    return CollectedDoubleState(this, initial)
}

class CollectedDoubleState(
    private val flow: Flow<Double>,
    initial: Double
) : ReadOnlyProperty<Any?, Double> {
    private val delegate = DoubleStateDelegate(initial)

    init {
        Hooks.useEffect(EffectCallback {
            val job = SupervisorJob()
            val scope = CoroutineScope(job + JsDispatcher)
            scope.launch {
                flow.collect { value ->
                    delegate.setValue(null, DUMMY_PROP, value)
                }
            }
            VoidCallback { job.cancel() }
        }, Hooks.deps())
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return delegate.getValue(thisRef, property)
    }
}

// ========================================================================
// produceState — Compose-inspired async state producer
// ========================================================================

/**
 * Create state that is asynchronously produced by a coroutine.
 *
 * ```
 * val profile by produceState<String>("loading", userId) {
 *     value = api.fetchUser(userId).name
 * }
 * ```
 */
fun ComponentScope.produceState(
    initialValue: Int,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<Int>.() -> Unit
): ReadOnlyProperty<Any?, Int> {
    val delegate = state(initialValue)
    val deps = if (keys.isEmpty()) Hooks.deps() else depsToJsArray(*keys)
    Hooks.useEffect(EffectCallback {
        val job = SupervisorJob()
        val scope = CoroutineScope(job + JsDispatcher)
        scope.launch {
            val produceScope = ProduceStateScope<Int>(
                getter = { delegate.getValue(null, DUMMY_PROP) },
                setter = { delegate.setValue(null, DUMMY_PROP, it) }
            )
            produceScope.producer()
        }
        VoidCallback { job.cancel() }
    }, deps)
    return delegate
}

fun ComponentScope.produceState(
    initialValue: String,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<String>.() -> Unit
): ReadOnlyProperty<Any?, String> {
    val delegate = state(initialValue)
    val deps = if (keys.isEmpty()) Hooks.deps() else depsToJsArray(*keys)
    Hooks.useEffect(EffectCallback {
        val job = SupervisorJob()
        val scope = CoroutineScope(job + JsDispatcher)
        scope.launch {
            val produceScope = ProduceStateScope<String>(
                getter = { delegate.getValue(null, DUMMY_PROP) },
                setter = { delegate.setValue(null, DUMMY_PROP, it) }
            )
            produceScope.producer()
        }
        VoidCallback { job.cancel() }
    }, deps)
    return delegate
}

class ProduceStateScope<T>(
    private val getter: () -> T,
    private val setter: (T) -> Unit
) {
    var value: T
        get() = getter()
        set(v) = setter(v)
}

// Dummy KProperty for internal use with delegates
@PublishedApi
internal val DUMMY_PROP: KProperty<*> by lazy {
    // Use a minimal implementation that delegates need
    object : KProperty<Any?> {
        override val annotations: List<Annotation> = emptyList()
        override val isAbstract: Boolean = false
        override val isConst: Boolean = false
        override val isFinal: Boolean = true
        override val isLateinit: Boolean = false
        override val isOpen: Boolean = false
        override val isSuspend: Boolean = false
        override val name: String = "dummy"
        override val parameters: List<kotlin.reflect.KParameter> = emptyList()
        override val returnType: kotlin.reflect.KType get() = throw UnsupportedOperationException()
        override val typeParameters: List<kotlin.reflect.KTypeParameter> = emptyList()
        override val visibility: kotlin.reflect.KVisibility? = null
        override val getter: KProperty.Getter<Any?> get() = throw UnsupportedOperationException()
        override fun call(vararg args: Any?): Any? = throw UnsupportedOperationException()
        override fun callBy(args: Map<kotlin.reflect.KParameter, Any?>): Any? = throw UnsupportedOperationException()
    }
}

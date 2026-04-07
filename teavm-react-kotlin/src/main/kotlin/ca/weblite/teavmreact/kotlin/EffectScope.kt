package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.VoidCallback
import ca.weblite.teavmreact.hooks.EffectCallback
import ca.weblite.teavmreact.hooks.Hooks
import kotlinx.coroutines.*
import org.teavm.jso.JSObject
import kotlin.coroutines.CoroutineContext

/**
 * Scope for [effect] blocks. Provides [onCleanup] for registering
 * teardown logic and implements [CoroutineScope] for launching
 * coroutines within the effect.
 */
@HtmlDsl
class EffectScope(private val parentJob: Job) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = parentJob + JsDispatcher

    internal var cleanupFn: (() -> Unit)? = null

    /**
     * Register a cleanup function that runs when the component unmounts
     * or before the effect re-runs.
     */
    fun onCleanup(block: () -> Unit) {
        cleanupFn = block
    }
}

// ========================================================================
// effect() — useEffect wrapper with CoroutineScope
// ========================================================================

/**
 * Run a side effect. Wraps React's useEffect with a [CoroutineScope]
 * for launching coroutines.
 *
 * ```
 * effect {
 *     val job = launch {
 *         while (isActive) {
 *             delay(1000)
 *             ticks++
 *         }
 *     }
 *     onCleanup { job.cancel() }
 * }
 * ```
 *
 * Pass dependency values to re-run when they change:
 * ```
 * effect(userId) {
 *     // re-runs when userId changes
 * }
 * ```
 *
 * Pass no arguments to run after every render, or pass an empty
 * list to run only on mount.
 */
fun ComponentScope.effect(
    vararg keys: Any?,
    block: EffectScope.() -> Unit
) {
    val deps = if (keys.isEmpty()) null else depsToJsArray(*keys)

    val callback = EffectCallback {
        val job = SupervisorJob()
        val scope = EffectScope(job)
        scope.block()
        VoidCallback {
            scope.cleanupFn?.invoke()
            job.cancel()
        }
    }

    if (deps != null) {
        Hooks.useEffect(callback, deps)
    } else {
        Hooks.useEffect(callback)
    }
}

/**
 * Run an effect only once on mount (empty dependency array).
 */
fun ComponentScope.effectOnce(block: EffectScope.() -> Unit) {
    val callback = EffectCallback {
        val job = SupervisorJob()
        val scope = EffectScope(job)
        scope.block()
        VoidCallback {
            scope.cleanupFn?.invoke()
            job.cancel()
        }
    }
    Hooks.useEffect(callback, Hooks.deps())
}

// ========================================================================
// launchedEffect() — Compose-inspired coroutine effect
// ========================================================================

/**
 * Launch a coroutine that runs when the component mounts (or when
 * dependencies change). The coroutine is automatically cancelled
 * when the component unmounts or before re-running.
 *
 * ```
 * launchedEffect {
 *     val data = api.fetchItems()  // suspend call
 *     items = data
 * }
 * ```
 *
 * With dependencies — re-launches when deps change:
 * ```
 * launchedEffect(userId) {
 *     profile = api.fetchUser(userId)
 * }
 * ```
 */
fun ComponentScope.launchedEffect(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    val deps = if (keys.isEmpty()) Hooks.deps() else depsToJsArray(*keys)

    Hooks.useEffect(EffectCallback {
        val job = SupervisorJob()
        val scope = CoroutineScope(job + JsDispatcher)
        scope.launch { block() }
        VoidCallback { job.cancel() }
    }, deps)
}

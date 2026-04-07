package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.JsUtil
import ca.weblite.teavmreact.core.VoidCallback
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine dispatcher that runs continuations on the JS event loop
 * via setTimeout(fn, 0). This is the appropriate dispatcher for
 * TeaVM-compiled code running in the browser.
 */
object JsDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        JsUtil.setTimeout(VoidCallback { block.run() }, 0)
    }
}

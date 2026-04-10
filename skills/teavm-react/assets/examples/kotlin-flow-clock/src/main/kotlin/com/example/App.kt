package com.example

import ca.weblite.teavmreact.core.ReactDOM
import ca.weblite.teavmreact.kotlin.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.teavm.jso.dom.html.HTMLDocument

// Clock component using Flow.collectAsState to bridge coroutines into React state.
fun main() {
    // A cold flow that emits an incrementing second count every 1000ms.
    val tickerFlow = flow {
        var seconds = 0
        while (true) {
            emit(seconds)
            delay(1000)
            seconds++
        }
    }

    val Clock = fc("Clock") {
        // Collect the flow into React state — re-renders on each emission.
        val elapsed by tickerFlow.collectAsState(initial = 0)

        // Format as hh:mm:ss
        val hours = elapsed / 3600
        val minutes = (elapsed % 3600) / 60
        val secs = elapsed % 60
        val display = "%02d:%02d:%02d".format(hours, minutes, secs)

        div {
            h1 { +"Elapsed Time" }
            h2 {
                className("clock-display")
                +display
            }
            p { +"Seconds since page load: $elapsed" }
        }
    }

    // Mount the component.
    val root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    )
    root.render(component(Clock))
}

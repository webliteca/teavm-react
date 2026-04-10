package com.example

import ca.weblite.teavmreact.core.ReactDOM
import ca.weblite.teavmreact.kotlin.*
import org.teavm.jso.dom.html.HTMLDocument

// Kotlin counter using the fc() DSL and delegated state.
fun main() {
    // Define a functional component with the Kotlin DSL.
    val Counter = fc("Counter") {
        // Delegated state — reads and writes like a normal variable.
        var count by state(0)

        div {
            h2 { +"Count: $count" }
            button {
                +"+"
                onClick { count++ }
            }
            button {
                +"-"
                onClick { count-- }
            }
            button {
                +"Reset"
                onClick { count = 0 }
            }
        }
    }

    // Mount the component into the DOM.
    val root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    )
    root.render(component(Counter))
}

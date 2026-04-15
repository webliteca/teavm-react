package ${package}

import ca.weblite.teavmreact.core.ReactDOM
import ca.weblite.teavmreact.kotlin.*
import org.teavm.jso.dom.html.HTMLDocument

fun main() {
    val App = fc("App") {
        div {
            h1 { +"Hello, teavm-react!" }
            p { +"Your app is running. Edit App.kt and rebuild." }
        }
    }

    val root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    )
    root.render(component(App))
}

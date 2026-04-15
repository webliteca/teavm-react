package ${package};

import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;

import static ca.weblite.teavmreact.html.Html.*;

public class App {

    public static void main(String[] args) {
        var root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(component(App::render, "App"));
    }

    static ReactElement render(JSObject props) {
        return div(
            h1("Hello, teavm-react!"),
            p("Your app is running. Edit App.java and rebuild.")
        );
    }
}

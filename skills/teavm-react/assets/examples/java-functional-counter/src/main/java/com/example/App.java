package com.example;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.StateHandle;
import org.teavm.jso.dom.html.HTMLDocument;

import static ca.weblite.teavmreact.html.Html.*;

// Minimal functional counter demonstrating useState and static Html imports.
public class App {

    // Define the Counter component using a RenderFunction lambda.
    private static ReactElement Counter(org.teavm.jso.JSObject props) {
        StateHandle<Integer> count = Hooks.useState(0);

        return div(
            h1("Counter"),
            p("Count: " + count.getInt()),
            // Increment button
            button("+1").onClick(e -> count.updateInt(n -> n + 1)).build(),
            // Decrement button
            button("-1").onClick(e -> count.updateInt(n -> n - 1)).build(),
            // Reset button
            button("Reset").onClick(e -> count.setInt(0)).build()
        );
    }

    public static void main(String[] args) {
        // Wrap the render function as a named React component.
        var counter = React.wrapComponent(App::Counter, "Counter");

        // Mount into the #root element.
        var root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(component(counter));
    }
}

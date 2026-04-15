package ca.weblite.teavmreact.docs.layout;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.Router;
import ca.weblite.teavmreact.html.DomBuilder;
import ca.weblite.teavmreact.html.DomBuilder.*;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;

public class Sidebar {

    public static ReactElement render(JSObject props, boolean open, Runnable onClose) {
        String currentPath = Router.ROUTE_CTX.useString();

        return Nav.create().className("sidebar" + (open ? " open" : ""))
            .child(sidebarSection("Learn",
                link("Quick Start", "learn/quick-start", currentPath, onClose),
                link("Installation", "learn/installation", currentPath, onClose),
                link("Thinking in teavm-react", "learn/thinking", currentPath, onClose),
                link("Your First Component", "learn/first-component", currentPath, onClose),
                link("Passing Props", "learn/props", currentPath, onClose),
                link("Conditional Rendering", "learn/conditional-rendering", currentPath, onClose),
                link("Rendering Lists", "learn/rendering-lists", currentPath, onClose),
                link("Responding to Events", "learn/events", currentPath, onClose),
                link("State", "learn/state", currentPath, onClose),
                link("Reducers", "learn/reducers", currentPath, onClose),
                link("Context", "learn/context", currentPath, onClose),
                link("Refs", "learn/refs", currentPath, onClose),
                link("Effects", "learn/effects", currentPath, onClose)
            ))
            .child(sidebarSection("Reference",
                link("Hooks Overview", "reference/hooks-overview", currentPath, onClose),
                link("useState", "reference/use-state", currentPath, onClose),
                link("useEffect", "reference/use-effect", currentPath, onClose),
                link("useRef", "reference/use-ref", currentPath, onClose),
                link("useContext", "reference/use-context", currentPath, onClose),
                link("HTML DSL", "reference/html-dsl", currentPath, onClose),
                link("Components", "reference/components", currentPath, onClose),
                link("Events", "reference/events", currentPath, onClose)
            ))
            .build();
    }

    private static ReactElement sidebarSection(String title, ReactElement... links) {
        DomBuilder section = Div.create().className("sidebar-section")
            .child(P.create().className("sidebar-section-title").text(title));
        for (ReactElement link : links) {
            section.child(link);
        }
        return section.build();
    }

    private static ReactElement link(String label, String path, String currentPath, Runnable onClose) {
        boolean active = currentPath.equals(path);
        return A.create()
            .href("#/" + path)
            .className("sidebar-link" + (active ? " active" : ""))
            .onClick(e -> onClose.run())
            .text(label)
            .build();
    }
}

package ca.weblite.teavmreact.docs.layout;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.Router;
import ca.weblite.teavmreact.html.DomBuilder.*;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;

public class Header {

    public static ReactElement render(JSObject props, Runnable onToggleSidebar) {
        String currentPath = Router.ROUTE_CTX.useString();
        boolean isLearn = currentPath.startsWith("learn");
        boolean isReference = currentPath.startsWith("reference");

        return Div.create().className("header")
            .child(button("\u2630")
                .className("hamburger")
                .onClick(e -> onToggleSidebar.run())
                .build())
            .child(A.create().href("#/").className("header-logo").text("teavm-react"))
            .child(Nav.create().className("header-nav")
                .child(A.create().href("#/learn/quick-start")
                    .className("header-nav-link" + (isLearn ? " active" : ""))
                    .text("Learn"))
                .child(A.create().href("#/reference/hooks-overview")
                    .className("header-nav-link" + (isReference ? " active" : ""))
                    .text("Reference")))
            .child(A.create().href("https://github.com/webliteca/teavm-react")
                .className("header-github")
                .prop("target", "_blank")
                .text("GitHub"))
            .build();
    }
}

package ca.weblite.teavmreact.docs.components;

import ca.weblite.teavmreact.core.RenderFunction;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.html.DomBuilder.*;

import static ca.weblite.teavmreact.html.Html.*;

public class LiveDemo {

    public static ReactElement create(RenderFunction demo) {
        return Div.create().className("live-demo")
            .child(Div.create().className("live-demo-label").text("Live Example"))
            .child(Div.create().className("live-demo-content")
                .child(component(demo, "Demo")))
            .build();
    }
}

package com.teavm.react.demo;

import com.teavm.react.component.ReactView;
import com.teavm.react.core.React;
import com.teavm.react.core.ReactDOM;
import com.teavm.react.core.ReactElement;
import com.teavm.react.core.RenderFunction;
import com.teavm.react.core.VoidCallback;
import com.teavm.react.hooks.Hooks;
import com.teavm.react.hooks.StateHandle;
import com.teavm.react.html.DomBuilder;
import com.teavm.react.html.DomBuilder.*;
import com.teavm.react.html.ElementBuilder;
import com.teavm.react.html.Html;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;

import static com.teavm.react.html.Html.*;

/**
 * Demo application showcasing all three approaches side-by-side.
 */
public class App {

    public static void main(String[] args) {
        var root = ReactDOM.createRoot(
                HTMLDocument.current().getElementById("root"));

        // The main app component wraps everything
        JSObject appComponent = React.wrapComponent(App::renderApp, "App");
        root.render(React.createElement(appComponent, null));
    }

    /**
     * Main app render function — showcases all three approaches.
     */
    static ReactElement renderApp(JSObject props) {
        return div(
            h1("teavm-react — Feasibility POC"),
            p("Three approaches to writing React components in Java:"),

            // ===== APPROACH A: Functional (React-familiar) =====
            sectionWithTitle("Approach A — Functional (React-familiar)",
                Html.component(counterFunctional),
                Html.component(timerFunctional),
                Html.component(textInputFunctional)
            ),

            // ===== APPROACH B: Builder DSL (Java-idiomatic) =====
            sectionWithTitle("Approach B — Builder DSL (Java-idiomatic)",
                Html.component(counterBuilder),
                Html.component(listBuilder)
            ),

            // ===== APPROACH C: Class-based (Swing/JavaFX-familiar) =====
            sectionWithTitle("Approach C — Class-based (Swing/JavaFX-familiar)",
                ReactView.view(CounterView::new, "CounterView"),
                ReactView.view(TimerView::new, "TimerView"),
                ReactView.view(TextInputView::new, "TextInputView")
            )
        );
    }

    // =====================================================================
    // Helper: Section with title
    // =====================================================================

    static ReactElement sectionWithTitle(String title, ReactElement... children) {
        ReactElement[] allChildren = new ReactElement[children.length + 2];
        allChildren[0] = h2(title);
        allChildren[1] = hr();
        System.arraycopy(children, 0, allChildren, 2, children.length);
        return section(allChildren);
    }

    static ReactElement hr() {
        return React.createElement("hr", null);
    }

    // =====================================================================
    // APPROACH A: Functional components
    // =====================================================================

    /**
     * Counter — functional style (mirrors React hooks pattern).
     */
    static final JSObject counterFunctional = React.wrapComponent(props -> {
        var count = Hooks.useState(0);

        return div(
            h3("Counter (Functional)"),
            p("Count: " + count.getInt()),
            button("Increment").onClick(e -> count.updateInt(c -> c + 1)).build(),
            button(" Reset").onClick(e -> count.setInt(0)).build()
        );
    }, "CounterFunctional");

    /**
     * Timer — functional style with useEffect and cleanup.
     */
    static final JSObject timerFunctional = React.wrapComponent(props -> {
        var seconds = Hooks.useState(0);
        var running = Hooks.useState(true);

        Hooks.useEffect(() -> {
            if (!running.get().equals(Boolean.TRUE)) return null;
            int interval = setInterval(() -> seconds.updateInt(s -> s + 1), 1000);
            return () -> clearInterval(interval);
        });

        return div(
            h3("Timer (Functional)"),
            p("Elapsed: " + seconds.getInt() + "s"),
            button(running.get().equals(Boolean.TRUE) ? "Pause" : "Resume")
                .onClick(e -> running.set(!running.get().equals(Boolean.TRUE)))
                .build()
        );
    }, "TimerFunctional");

    /**
     * Text input — functional style with controlled input.
     */
    static final JSObject textInputFunctional = React.wrapComponent(props -> {
        var value = Hooks.useState("");

        return div(
            h3("Text Input (Functional)"),
            input("text")
                .value(value.getString())
                .onChange(e -> value.setString(e.getTarget().getValue()))
                .placeholder("Type something...")
                .build(),
            p("You typed: " + value.getString())
        );
    }, "TextInputFunctional");

    // =====================================================================
    // APPROACH B: Builder DSL components
    // =====================================================================

    /**
     * Counter — builder DSL style.
     */
    static final JSObject counterBuilder = React.wrapComponent(props -> {
        var count = Hooks.useState(0);

        return Div.create()
            .child(H3.create().text("Counter (Builder DSL)"))
            .child(P.create().text("Count: " + count.getInt()))
            .child(Button.create().text("Increment")
                .onClick(e -> count.updateInt(c -> c + 1)))
            .child(Button.create().text(" Reset")
                .onClick(e -> count.setInt(0)))
            .build();
    }, "CounterBuilder");

    /**
     * List rendering — builder DSL style with forEach.
     */
    static final JSObject listBuilder = React.wrapComponent(props -> {
        String[] names = {"Apple", "Banana", "Cherry", "Date"};

        DomBuilder ul = Ul.create();
        for (int i = 0; i < names.length; i++) {
            ul.child(Li.create().key(i).text(names[i]));
        }

        return Div.create()
            .child(H3.create().text("List (Builder DSL)"))
            .child(ul)
            .build();
    }, "ListBuilder");

    // =====================================================================
    // APPROACH C: Class-based components
    // =====================================================================

    /**
     * Counter — class-based style (Swing/JavaFX familiar).
     */
    static class CounterView extends ReactView {
        private final StateHandle<Integer> count = Hooks.useState(0);

        @Override
        protected ReactElement render() {
            return div(
                h3("Counter (Class-based)"),
                p("Count: " + count.getInt()),
                button("Increment").onClick(e -> count.updateInt(c -> c + 1)).build(),
                button(" Reset").onClick(e -> count.setInt(0)).build()
            );
        }
    }

    /**
     * Timer — class-based style with lifecycle methods.
     */
    static class TimerView extends ReactView {
        private final StateHandle<Integer> seconds = Hooks.useState(0);
        private int intervalId = -1;

        @Override
        protected void onMount() {
            intervalId = setInterval(() -> seconds.updateInt(s -> s + 1), 1000);
        }

        @Override
        protected void onUnmount() {
            if (intervalId >= 0) {
                clearInterval(intervalId);
            }
        }

        @Override
        protected ReactElement render() {
            return div(
                h3("Timer (Class-based)"),
                p("Elapsed: " + seconds.getInt() + "s")
            );
        }
    }

    /**
     * Text input — class-based style.
     */
    static class TextInputView extends ReactView {
        private final StateHandle<String> value = Hooks.useState("");

        @Override
        protected ReactElement render() {
            return div(
                h3("Text Input (Class-based)"),
                input("text")
                    .value(value.getString())
                    .onChange(e -> value.setString(e.getTarget().getValue()))
                    .placeholder("Type something...")
                    .build(),
                p("You typed: " + value.getString())
            );
        }
    }

    // =====================================================================
    // JS timer helpers (TeaVM's Window.setInterval returns void, we need int)
    // =====================================================================

    @JSBody(params = {"callback", "ms"}, script = "return setInterval(callback, ms);")
    static native int setInterval(VoidCallback callback, int ms);

    @JSBody(params = {"id"}, script = "clearInterval(id);")
    static native void clearInterval(int id);
}

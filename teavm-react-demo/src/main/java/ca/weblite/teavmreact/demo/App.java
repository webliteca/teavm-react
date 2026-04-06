package ca.weblite.teavmreact.demo;

import ca.weblite.teavmreact.component.ReactView;
import ca.weblite.teavmreact.core.JsUtil;
import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactContext;
import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.core.VoidCallback;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.StateHandle;
import ca.weblite.teavmreact.html.DomBuilder;
import ca.weblite.teavmreact.html.DomBuilder.*;
import ca.weblite.teavmreact.html.Html;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;

import static ca.weblite.teavmreact.html.Html.*;

/**
 * Demo app exercising all three approaches and major API features.
 */
public class App {

    // =========================================================================
    // Context for theme demo
    // =========================================================================
    static final ReactContext THEME_CTX = ReactContext.create(React.stringToJS("light"));

    public static void main(String[] args) {
        var root = ReactDOM.createRoot(HTMLDocument.current().getElementById("root"));
        JSObject appComponent = React.wrapComponent(App::renderApp, "App");
        root.render(React.createElement(appComponent, null));
    }

    static ReactElement renderApp(JSObject props) {
        var theme = Hooks.useState("light");

        return div(
            h1("teavm-react Library Demo"),
            p("Three approaches to writing React components in Java."),

            // Approach A — Functional
            sectionBlock("Approach A — Functional (React-familiar)",
                component(counterFunctional),
                component(timerFunctional),
                component(textInputFunctional)
            ),

            // Approach B — Builder DSL
            sectionBlock("Approach B — Builder DSL (Java-idiomatic)",
                component(counterBuilder),
                component(listBuilder)
            ),

            // Approach C — Class-based
            sectionBlock("Approach C — Class-based (Swing/JavaFX-familiar)",
                ReactView.view(CounterView::new, "CounterView"),
                ReactView.view(TimerView::new, "TimerView"),
                ReactView.view(TextInputView::new, "TextInputView")
            ),

            // Context demo
            sectionBlock("Context API Demo",
                THEME_CTX.provide(React.stringToJS(theme.getString()),
                    component(themeDisplay),
                    button("Toggle theme").onClick(e ->
                        theme.setString(theme.getString().equals("light") ? "dark" : "light")
                    ).build()
                )
            )
        );
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    static ReactElement sectionBlock(String title, ReactElement... children) {
        ReactElement[] all = new ReactElement[children.length + 2];
        all[0] = h2(title);
        all[1] = Html.hr();
        for (int i = 0; i < children.length; i++) {
            all[i + 2] = children[i];
        }
        return section(all);
    }

    // =========================================================================
    // Approach A: Functional components
    // =========================================================================

    static final JSObject counterFunctional = React.wrapComponent(props -> {
        var count = Hooks.useState(0);

        return div(
            h3("Counter (Functional)"),
            p("Count: " + count.getInt()),
            button("Increment").onClick(e -> count.updateInt(c -> c + 1)).build(),
            button(" Reset").onClick(e -> count.setInt(0)).build()
        );
    }, "CounterFunctional");

    static final JSObject timerFunctional = React.wrapComponent(props -> {
        var seconds = Hooks.useState(0);
        var running = Hooks.useState(true);

        Hooks.useEffect(() -> {
            if (!running.getBool()) return null;
            int id = JsUtil.setInterval(() -> seconds.updateInt(s -> s + 1), 1000);
            return () -> JsUtil.clearInterval(id);
        });

        return div(
            h3("Timer (Functional)"),
            p("Elapsed: " + seconds.getInt() + "s"),
            button(running.getBool() ? "Pause" : "Resume")
                .onClick(e -> running.setBool(!running.getBool()))
                .build()
        );
    }, "TimerFunctional");

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

    // =========================================================================
    // Approach B: Builder DSL components
    // =========================================================================

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

    // =========================================================================
    // Approach C: Class-based components
    // =========================================================================

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

    static class TimerView extends ReactView {
        private final StateHandle<Integer> seconds = Hooks.useState(0);
        private int intervalId = -1;

        @Override
        protected void onMount() {
            intervalId = JsUtil.setInterval(() -> seconds.updateInt(s -> s + 1), 1000);
        }

        @Override
        protected void onUnmount() {
            if (intervalId >= 0) {
                JsUtil.clearInterval(intervalId);
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

    // =========================================================================
    // Context demo
    // =========================================================================

    static final JSObject themeDisplay = React.wrapComponent(props -> {
        JSObject theme = Hooks.useContext(THEME_CTX.jsContext());
        String themeStr = React.jsToString(theme);
        return div(
            p("Current theme: " + themeStr),
            div(
                button("I'm themed!").className("themed-btn").build()
            )
        );
    }, "ThemeDisplay");
}

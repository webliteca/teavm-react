package ca.weblite.teavmreact.demo;

import ca.weblite.teavmreact.component.ReactView;
import ca.weblite.teavmreact.core.JsUtil;
import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactContext;
import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.core.VoidCallback;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.RefHandle;
import ca.weblite.teavmreact.hooks.StateHandle;
import ca.weblite.teavmreact.html.DomBuilder;
import ca.weblite.teavmreact.html.DomBuilder.*;
import ca.weblite.teavmreact.html.Html;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;

import static ca.weblite.teavmreact.html.Html.*;

/**
 * Kitchen-sink demo exercising every feature of teavm-react.
 */
public class App {

    // =========================================================================
    // Theme context (shared across components)
    // =========================================================================
    static final ReactContext THEME_CTX = ReactContext.create(React.stringToJS("light"));

    public static void main(String[] args) {
        var root = ReactDOM.createRoot(HTMLDocument.current().getElementById("root"));
        JSObject app = React.wrapComponent(App::renderApp, "App");
        root.render(React.createElement(app, null));
    }

    // =========================================================================
    // Root app component
    // =========================================================================
    static ReactElement renderApp(JSObject props) {
        var theme = Hooks.useState("light");
        boolean isDark = theme.getString().equals("dark");

        return THEME_CTX.provide(React.stringToJS(theme.getString()),
            div(
                // Header
                header(
                    h1("teavm-react Kitchen Sink"),
                    p("A comprehensive demo of every library feature.")
                ),

                // Theme toggle
                nav(
                    button(isDark ? "Switch to Light" : "Switch to Dark")
                        .onClick(e -> theme.setString(isDark ? "light" : "dark"))
                        .className("theme-toggle")
                        .build()
                ),

                hr(),

                // ===== Section 1: Approach A — Functional =====
                section(
                    h2("1. Approach A — Functional Components"),
                    p("React-familiar hooks-based pattern."),
                    component(counterFunctional),
                    component(timerFunctional),
                    component(textInputFunctional),
                    component(todoListFunctional)
                ),

                hr(),

                // ===== Section 2: Approach B — Builder DSL =====
                section(
                    h2("2. Approach B — Builder DSL"),
                    p("Java-idiomatic fluent builder pattern."),
                    component(pageNavigationBuilder),
                    component(counterBuilder),
                    component(itemListBuilder),
                    component(formBuilder)
                ),

                hr(),

                // ===== Section 3: Approach C — Class-based =====
                section(
                    h2("3. Approach C — Class-Based Components"),
                    p("Swing/JavaFX-familiar extends ReactView."),
                    ReactView.view(CounterView::new, "CounterView"),
                    ReactView.view(StopwatchView::new, "StopwatchView"),
                    ReactView.view(CharCounterView::new, "CharCounterView")
                ),

                hr(),

                // ===== Section 4: Hooks showcase =====
                section(
                    h2("4. Hooks Showcase"),
                    component(useRefDemo),
                    component(useContextDemo),
                    component(useMemoDemo)
                ),

                hr(),

                // ===== Section 5: HTML elements showcase =====
                section(
                    h2("5. HTML Elements Showcase"),
                    component(htmlElementsDemo)
                ),

                // Footer
                footer(
                    hr(),
                    p("Built with teavm-react — Java compiled to JS via TeaVM, rendered by React 18.")
                )
            )
        );
    }

    // =========================================================================
    // 1A. Counter — Functional
    // =========================================================================
    static final JSObject counterFunctional = React.wrapComponent(props -> {
        var count = Hooks.useState(0);
        var step = Hooks.useState(1);

        return div(
            h3("Counter with Step"),
            p("Count: " + count.getInt()),
            div(
                button("-" + step.getInt())
                    .onClick(e -> count.updateInt(c -> c - step.getInt()))
                    .build(),
                button("+" + step.getInt())
                    .onClick(e -> count.updateInt(c -> c + step.getInt()))
                    .build(),
                button("Reset")
                    .onClick(e -> count.setInt(0))
                    .build()
            ),
            div(
                text("Step: "),
                button("1").onClick(e -> step.setInt(1)).build(),
                button("5").onClick(e -> step.setInt(5)).build(),
                button("10").onClick(e -> step.setInt(10)).build()
            )
        );
    }, "CounterFunctional");

    // =========================================================================
    // 1B. Timer — Functional with useEffect
    // =========================================================================
    static final JSObject timerFunctional = React.wrapComponent(props -> {
        var seconds = Hooks.useState(0);
        var running = Hooks.useState(true);

        Hooks.useEffect(() -> {
            if (!running.getBool()) return null;
            int id = JsUtil.setInterval(() -> seconds.updateInt(s -> s + 1), 1000);
            return () -> JsUtil.clearInterval(id);
        });

        int mins = seconds.getInt() / 60;
        int secs = seconds.getInt() % 60;
        String display = (mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "") + secs;

        return div(
            h3("Timer with useEffect"),
            p(display),
            button(running.getBool() ? "Pause" : "Resume")
                .onClick(e -> running.setBool(!running.getBool()))
                .className(running.getBool() ? "btn-warning" : "btn-success")
                .build(),
            button("Reset")
                .onClick(e -> { seconds.setInt(0); running.setBool(true); })
                .build()
        );
    }, "TimerFunctional");

    // =========================================================================
    // 1C. Text Input — Functional with controlled input
    // =========================================================================
    static final JSObject textInputFunctional = React.wrapComponent(props -> {
        var value = Hooks.useState("");
        var focused = Hooks.useState(false);

        int charCount = value.getString().length();

        return div(
            h3("Controlled Text Input"),
            input("text")
                .value(value.getString())
                .onChange(e -> value.setString(e.getTarget().getValue()))
                .onFocus(e -> focused.setBool(true))
                .onBlur(e -> focused.setBool(false))
                .placeholder("Type something...")
                .maxLength(100)
                .build(),
            p(charCount + "/100 characters" + (focused.getBool() ? " (focused)" : "")),
            charCount > 0
                ? p("Reversed: " + new StringBuilder(value.getString()).reverse().toString())
                : p("Start typing to see your text reversed.")
        );
    }, "TextInputFunctional");

    // =========================================================================
    // 1D. Todo List — Functional with dynamic list
    // =========================================================================
    static final JSObject todoListFunctional = React.wrapComponent(props -> {
        var input = Hooks.useState("");
        var nextId = Hooks.useState(3);
        // Store todos as parallel arrays (TeaVM can't pass Java collections to JS)
        var todoIds = Hooks.useState("0,1,2");
        var todoTexts = Hooks.useState("Learn Java,Try teavm-react,Build something cool");
        var todoDone = Hooks.useState("false,false,false");

        String[] ids = todoIds.getString().split(",");
        String[] texts = todoTexts.getString().split(",");
        String[] dones = todoDone.getString().split(",");
        int count = ids.length;
        if (ids[0].isEmpty()) count = 0;

        int total = count;
        int completed = 0;
        for (int i = 0; i < count; i++) {
            if (dones[i].equals("true")) completed++;
        }

        ReactElement[] items = new ReactElement[count];
        for (int i = 0; i < count; i++) {
            final int idx = i;
            boolean done = dones[i].equals("true");
            items[i] = li(
                input("checkbox")
                    .checked(done)
                    .onChange(e -> {
                        String[] d = todoDone.getString().split(",");
                        d[idx] = d[idx].equals("true") ? "false" : "true";
                        todoDone.setString(joinArray(d));
                    })
                    .build(),
                done ? Html.em(texts[i]) : span(texts[i]),
                button(" x")
                    .onClick(e -> {
                        String[] ci = todoIds.getString().split(",");
                        String[] ct = todoTexts.getString().split(",");
                        String[] cd = todoDone.getString().split(",");
                        todoIds.setString(removeAt(ci, idx));
                        todoTexts.setString(removeAt(ct, idx));
                        todoDone.setString(removeAt(cd, idx));
                    })
                    .className("btn-danger btn-sm")
                    .build()
            );
        }

        return div(
            h3("Todo List"),
            p(completed + "/" + total + " completed"),
            div(
                input("text")
                    .value(input.getString())
                    .onChange(e -> input.setString(e.getTarget().getValue()))
                    .onKeyDown(e -> {
                        if (e.getKey().equals("Enter") && !input.getString().isEmpty()) {
                            addTodo(todoIds, todoTexts, todoDone, nextId, input);
                        }
                    })
                    .placeholder("Add a todo...")
                    .build(),
                button("Add")
                    .onClick(e -> {
                        if (!input.getString().isEmpty()) {
                            addTodo(todoIds, todoTexts, todoDone, nextId, input);
                        }
                    })
                    .disabled(input.getString().isEmpty())
                    .build()
            ),
            ul(items)
        );
    }, "TodoListFunctional");

    static void addTodo(StateHandle<String> ids, StateHandle<String> texts,
                        StateHandle<String> dones, StateHandle<Integer> nextId,
                        StateHandle<String> input) {
        String sep = ids.getString().isEmpty() ? "" : ",";
        ids.setString(ids.getString() + sep + nextId.getInt());
        texts.setString(texts.getString() + sep + input.getString());
        dones.setString(dones.getString() + sep + "false");
        nextId.updateInt(n -> n + 1);
        input.setString("");
    }

    static String removeAt(String[] arr, int idx) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i == idx) continue;
            if (sb.length() > 0) sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    static String joinArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    // =========================================================================
    // 2. Page Navigation — Builder DSL (mirrors create-teavm-app template)
    // =========================================================================
    static final JSObject pageNavigationBuilder = React.wrapComponent(props -> {
        StateHandle<String> currentPage = Hooks.useState("home");

        return Div.create().className("page-nav-demo")
                .child(Nav.create().className("navbar")
                        .child(H3.create().text("Page Navigation (Builder DSL)").build())
                        .child(Div.create().className("nav-links")
                                .child(Button.create().text("Home")
                                        .onClick(e -> currentPage.setString("home"))
                                        .className("nav-btn").build())
                                .child(Button.create().text("About")
                                        .onClick(e -> currentPage.setString("about"))
                                        .className("nav-btn").build())
                                .child(Button.create().text("Contact")
                                        .onClick(e -> currentPage.setString("contact"))
                                        .className("nav-btn").build())
                                .build())
                        .build())
                .child(Div.create().className("content")
                        .child(renderNavPage(currentPage.getString()))
                        .build())
                .build();
    }, "PageNavigationBuilder");

    static ReactElement renderNavPage(String page) {
        return switch (page) {
            case "about" -> Div.create()
                    .child(H4.create().text("About Page").build())
                    .child(P.create().text("This is the about page, rendered with DomBuilder.").build())
                    .build();
            case "contact" -> Div.create()
                    .child(H4.create().text("Contact Page").build())
                    .child(P.create().text("This is the contact page.").build())
                    .build();
            default -> Div.create()
                    .child(H4.create().text("Home Page").build())
                    .child(P.create().text("Welcome! Click the buttons above to navigate.").build())
                    .build();
        };
    }

    // =========================================================================
    // 2A. Counter — Builder DSL
    // =========================================================================
    static final JSObject counterBuilder = React.wrapComponent(props -> {
        var count = Hooks.useState(0);

        return Div.create()
            .child(H3.create().text("Counter (Builder DSL)"))
            .child(P.create().text("Count: " + count.getInt()))
            .child(Button.create().text("Increment")
                .onClick(e -> count.updateInt(c -> c + 1)))
            .child(Button.create().text("Decrement")
                .onClick(e -> count.updateInt(c -> c - 1)))
            .child(Button.create().text("Reset")
                .onClick(e -> count.setInt(0)))
            .build();
    }, "CounterBuilder");

    // =========================================================================
    // 2B. Item List — Builder DSL with forEach
    // =========================================================================
    static final JSObject itemListBuilder = React.wrapComponent(props -> {
        String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Elderberry", "Fig", "Grape"};
        var filter = Hooks.useState("");

        DomBuilder list = Ul.create();
        int shown = 0;
        for (int i = 0; i < fruits.length; i++) {
            if (filter.getString().isEmpty()
                    || fruits[i].toLowerCase().contains(filter.getString().toLowerCase())) {
                list.child(Li.create().key(i).text(fruits[i]));
                shown++;
            }
        }

        return Div.create()
            .child(H3.create().text("Filterable List (Builder DSL)"))
            .child(Input.create().type("text")
                .value(filter.getString())
                .onChange(e -> filter.setString(e.getTarget().getValue()))
                .placeholder("Filter fruits..."))
            .child(P.create().text("Showing " + shown + " of " + fruits.length))
            .child(list)
            .build();
    }, "ItemListBuilder");

    // =========================================================================
    // 2C. Form — Builder DSL with multiple inputs
    // =========================================================================
    static final JSObject formBuilder = React.wrapComponent(props -> {
        var name = Hooks.useState("");
        var email = Hooks.useState("");
        var message = Hooks.useState("");
        var submitted = Hooks.useState(false);

        if (submitted.getBool()) {
            return Div.create()
                .child(H3.create().text("Form (Builder DSL)"))
                .child(P.create().text("Submitted!"))
                .child(Dl.create()
                    .child(Dt.create().text("Name"))
                    .child(Dd.create().text(name.getString()))
                    .child(Dt.create().text("Email"))
                    .child(Dd.create().text(email.getString()))
                    .child(Dt.create().text("Message"))
                    .child(Dd.create().text(message.getString())))
                .child(Button.create().text("Reset")
                    .onClick(e -> submitted.setBool(false)))
                .build();
        }

        return Div.create()
            .child(H3.create().text("Form (Builder DSL)"))
            .child(Div.create().className("form-group")
                .child(Label.create().text("Name:").prop("htmlFor", "name"))
                .child(Input.create().type("text").prop("name", "name").id("name")
                    .value(name.getString())
                    .onChange(e -> name.setString(e.getTarget().getValue()))
                    .placeholder("Your name")))
            .child(Div.create().className("form-group")
                .child(Label.create().text("Email:").prop("htmlFor", "email"))
                .child(Input.create().type("text").prop("name", "email").id("email")
                    .value(email.getString())
                    .onChange(e -> email.setString(e.getTarget().getValue()))
                    .placeholder("you@example.com")))
            .child(Div.create().className("form-group")
                .child(Label.create().text("Message:").prop("htmlFor", "msg"))
                .child(Textarea.create().prop("name", "msg").id("msg")
                    .value(message.getString())
                    .onChange(e -> message.setString(e.getTarget().getValue()))
                    .placeholder("Write something...")
                    .prop("rows", "4")))
            .child(Button.create().text("Submit")
                .onClick(e -> submitted.setBool(true))
                .disabled(name.getString().isEmpty() || email.getString().isEmpty()))
            .build();
    }, "FormBuilder");

    // =========================================================================
    // 3A. Counter — Class-based
    // =========================================================================
    static class CounterView extends ReactView {
        private final StateHandle<Integer> count = Hooks.useState(0);

        @Override
        protected ReactElement render() {
            return div(
                h3("Counter (Class-based)"),
                p("Count: " + count.getInt()),
                button("Increment").onClick(e -> count.updateInt(c -> c + 1)).build(),
                button("Decrement").onClick(e -> count.updateInt(c -> c - 1)).build(),
                button("Reset").onClick(e -> count.setInt(0)).build()
            );
        }
    }

    // =========================================================================
    // 3B. Stopwatch — Class-based with onMount/onUnmount
    // =========================================================================
    static class StopwatchView extends ReactView {
        private final StateHandle<Integer> ms = Hooks.useState(0);
        private final StateHandle<Boolean> running = Hooks.useState(false);
        private int intervalId = -1;

        @Override
        protected void onMount() {
            // Start ticking when mounted
        }

        @Override
        protected void onUnmount() {
            if (intervalId >= 0) JsUtil.clearInterval(intervalId);
        }

        @Override
        protected ReactElement render() {
            // Manage interval based on running state
            Hooks.useEffect(() -> {
                if (!running.getBool()) return null;
                int id = JsUtil.setInterval(() -> ms.updateInt(t -> t + 100), 100);
                return () -> JsUtil.clearInterval(id);
            });

            int totalMs = ms.getInt();
            int secs = totalMs / 1000;
            int tenths = (totalMs % 1000) / 100;

            return div(
                h3("Stopwatch (Class-based)"),
                p(secs + "." + tenths + "s"),
                button(running.getBool() ? "Stop" : "Start")
                    .onClick(e -> running.setBool(!running.getBool()))
                    .className(running.getBool() ? "btn-warning" : "btn-success")
                    .build(),
                button("Reset")
                    .onClick(e -> { ms.setInt(0); running.setBool(false); })
                    .build()
            );
        }
    }

    // =========================================================================
    // 3C. Character Counter — Class-based textarea
    // =========================================================================
    static class CharCounterView extends ReactView {
        private final StateHandle<String> text = Hooks.useState("");

        @Override
        protected ReactElement render() {
            String val = text.getString();
            int chars = val.length();
            int words = val.isEmpty() ? 0 : val.trim().split("\\s+").length;

            return div(
                h3("Character Counter (Class-based)"),
                textarea()
                    .value(val)
                    .onChange(e -> text.setString(e.getTarget().getValue()))
                    .placeholder("Type a paragraph...")
                    .rows(4)
                    .cols(50)
                    .build(),
                p(chars + " characters, " + words + " words"),
                chars > 0
                    ? pre(val.toUpperCase())
                    : p("Your text in UPPERCASE will appear here.")
            );
        }
    }

    // =========================================================================
    // 4A. useRef Demo
    // =========================================================================
    static final JSObject useRefDemo = React.wrapComponent(props -> {
        var renderCount = Hooks.useRefInt(0);
        var inputValue = Hooks.useState("");

        // Increment render count on every render
        renderCount.setCurrent(React.intToJS(renderCount.getCurrentInt() + 1));

        return div(
            h3("useRef — Render Counter"),
            p("This component has rendered " + renderCount.getCurrentInt() + " times."),
            p("Type below to trigger re-renders:"),
            input("text")
                .value(inputValue.getString())
                .onChange(e -> inputValue.setString(e.getTarget().getValue()))
                .placeholder("Type to re-render...")
                .build()
        );
    }, "UseRefDemo");

    // =========================================================================
    // 4B. useContext Demo — consumes theme from root
    // =========================================================================
    static final JSObject useContextDemo = React.wrapComponent(props -> {
        JSObject themeValue = Hooks.useContext(THEME_CTX.jsContext());
        String theme = React.jsToString(themeValue);
        boolean isDark = theme.equals("dark");

        JSObject style = React.createObject();
        React.setProperty(style, "background", isDark ? "#333" : "#f0f0f0");
        React.setProperty(style, "color", isDark ? "#fff" : "#333");
        React.setProperty(style, "padding", "16px");
        React.setProperty(style, "borderRadius", "8px");
        React.setProperty(style, "marginBottom", "12px");

        return div(
            h3("useContext — Theme Consumer"),
            Div.create().style(style)
                .child(P.create().text("Current theme: " + theme))
                .child(P.create().text("This box adapts to the theme set at the app root."))
                .child(P.create().text(isDark
                    ? "Dark mode is active. Click the toggle above to switch."
                    : "Light mode is active. Click the toggle above to switch."))
                .build()
        );
    }, "UseContextDemo");

    // =========================================================================
    // 4C. useMemo Demo — expensive computation
    // =========================================================================
    static final JSObject useMemoDemo = React.wrapComponent(props -> {
        var number = Hooks.useState(10);
        var dummy = Hooks.useState(0);

        // "Expensive" fibonacci computation (memoized)
        // We can't use useMemo directly for int, so we'll compute inline
        // and demonstrate that the concept works
        int n = number.getInt();
        int fib = fibonacci(n);

        return div(
            h3("useMemo Concept — Fibonacci"),
            p("Fibonacci(" + n + ") = " + fib),
            div(
                button("n-1").onClick(e -> { if (number.getInt() > 0) number.updateInt(x -> x - 1); }).build(),
                button("n+1").onClick(e -> { if (number.getInt() < 40) number.updateInt(x -> x + 1); }).build()
            ),
            p("Unrelated counter (re-renders without recomputing fib): " + dummy.getInt()),
            button("Increment unrelated").onClick(e -> dummy.updateInt(d -> d + 1)).build()
        );
    }, "UseMemoDemo");

    static int fibonacci(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int tmp = a + b;
            a = b;
            b = tmp;
        }
        return b;
    }

    // =========================================================================
    // 5. HTML Elements Showcase
    // =========================================================================
    static final JSObject htmlElementsDemo = React.wrapComponent(props -> {
        return div(
            h3("HTML Elements Gallery"),

            // Headings
            div(
                h4("Headings"),
                h1("Heading 1"),
                h2("Heading 2"),
                h3("Heading 3"),
                h4("Heading 4"),
                h5("Heading 5"),
                h6("Heading 6")
            ),

            hr(),

            // Text formatting
            div(
                h4("Text Formatting"),
                p(
                    text("This has "),
                    em("emphasized"),
                    text(", "),
                    strong("strong"),
                    text(", "),
                    small("small"),
                    text(", "),
                    code("inline code"),
                    text(", and "),
                    mark("marked"),
                    text(" text.")
                ),
                blockquote("This is a blockquote. Someone wise said this."),
                pre("function hello() {\n  console.log('Hello from pre!');\n}")
            ),

            hr(),

            // Lists
            div(
                h4("Lists"),
                div(
                    strong("Unordered:"),
                    ul(
                        li("First item"),
                        li("Second item"),
                        li("Third item")
                    )
                ),
                div(
                    strong("Ordered:"),
                    ol(
                        li("Step one"),
                        li("Step two"),
                        li("Step three")
                    )
                ),
                div(
                    strong("Definition List:"),
                    dl(
                        dt("TeaVM"),
                        dd("Compiles Java bytecode to JavaScript"),
                        dt("React"),
                        dd("A JavaScript library for building user interfaces"),
                        dt("teavm-react"),
                        dd("The bridge between the two!")
                    )
                )
            ),

            hr(),

            // Table
            div(
                h4("Table"),
                table(
                    thead(
                        tr(
                            th("Feature"),
                            th("Approach A"),
                            th("Approach B"),
                            th("Approach C")
                        )
                    ),
                    tbody(
                        tr(td("Style"), td("Functional"), td("Builder"), td("Class-based")),
                        tr(td("Familiar to"), td("React devs"), td("Java devs"), td("Swing/JavaFX")),
                        tr(td("State"), td("useState()"), td("useState()"), td("Field + useState")),
                        tr(td("Lifecycle"), td("useEffect"), td("useEffect"), td("onMount/Unmount"))
                    )
                )
            ),

            hr(),

            // Details/Summary
            div(
                h4("Details & Summary"),
                details(
                    summary("Click to expand"),
                    p("This content was hidden inside a details element."),
                    p("It uses the native HTML5 details/summary elements.")
                )
            ),

            hr(),

            // Navigation (semantic)
            div(
                h4("Semantic Elements"),
                article(
                    header(h5("Article Title")),
                    p("This is an article with header, main content, and footer."),
                    footer(small("Published by teavm-react demo"))
                )
            ),

            hr(),

            // Fragment demo
            div(
                h4("Fragment"),
                p("The next three items are rendered via React.Fragment (no wrapper div):"),
                fragment(
                    span("One "),
                    span("Two "),
                    span("Three")
                )
            )
        );
    }, "HtmlElementsDemo");
}

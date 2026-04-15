package ca.weblite.teavmreact.docs.pages.reference;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Reference page for component creation patterns in teavm-react.
 */
public class ComponentsPage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("Components Reference"),
            p(fragment(
                text("Components are the building blocks of a teavm-react application. "),
                text("There are three ways to define components: functional components "),
                text("via "),
                code("component()"),
                text(", the builder DSL pattern, and class-based components via "),
                code("ReactView"),
                text(".")
            )),

            // ── Functional Components ──
            h2("Functional Components"),
            p(fragment(
                text("Functional components are the primary way to build UI in teavm-react. "),
                text("A functional component is a static method that takes a "),
                code("JSObject"),
                text(" props argument and returns a "),
                code("ReactElement"),
                text(".")
            )),

            h3("Step 1: Define the Render Function"),
            CodeTabs.create(
                """
                    public class Greeting {
                        public static ReactElement render(JSObject props) {
                            return div(
                                h2("Hello, World!"),
                                p("Welcome to teavm-react.")
                            );
                        }
                    }""",
                """
                    fun renderGreeting(props: JSObject): ReactElement {
                        return div(
                            h2("Hello, World!"),
                            p("Welcome to teavm-react.")
                        )
                    }"""
            ),

            h3("Step 2: Wrap with component()"),
            p(fragment(
                text("Use "),
                code("component(RenderFunction, \"Name\")"),
                text(" to wrap the static method as a React component. "),
                text("The second argument is the display name used in React DevTools.")
            )),
            CodeTabs.create(
                """
                    // In a parent component
                    return div(
                        component(Greeting::render, "Greeting"),
                        component(Footer::render, "Footer")
                    );""",
                """
                    return div(
                        component(::renderGreeting, "Greeting"),
                        component(::renderFooter, "Footer")
                    )"""
            ),

            h3("Step 3: Use in a Parent"),
            p("The wrapped component behaves like any other React component and can be placed anywhere in the element tree:"),
            CodeBlock.create("""
                public class App {
                    public static ReactElement render(JSObject props) {
                        return El.div("app",

                            component(Header::render, "Header"),
                            main(
                                component(Greeting::render, "Greeting"),
                                component(Content::render, "Content")
                            ),
                            component(Footer::render, "Footer")
                        );
                    }
                }""", "java"),

            // Using hooks
            h3("Using Hooks in Functional Components"),
            p("Functional components can use any hook at the top level of the render function:"),
            CodeBlock.create("""
                public class Counter {
                    public static ReactElement render(JSObject props) {
                        var count = Hooks.useState(0);
                        var label = Hooks.useState("Clicks");

                        return div(
                            h3(label.getString() + ": " + count.getInt()),
                            button("Increment")
                                .onClick(e -> count.updateInt(c -> c + 1))
                                .build()
                        );
                    }
                }""", "java"),

            // ── Builder DSL Pattern ──
            h2("Builder DSL Pattern"),
            p(fragment(
                text("For components with many configuration options, you can create "),
                text("a builder that collects parameters before producing the final "),
                code("ReactElement"),
                text(". This is useful for reusable UI components that accept many options.")
            )),
            CodeTabs.create(
                """
                    public class Card {
                        private String title;
                        private String body;
                        private String variant = "default";

                        public static Card create() {
                            return new Card();
                        }

                        public Card title(String title) {
                            this.title = title;
                            return this;
                        }

                        public Card body(String body) {
                            this.body = body;
                            return this;
                        }

                        public Card variant(String variant) {
                            this.variant = variant;
                            return this;
                        }

                        public ReactElement build() {
                            return div(
                                h3(title),
                                p(body)
                            ).className("card card-" + variant).build();
                        }
                    }

                    // Usage:
                    Card.create()
                        .title("Welcome")
                        .body("This is a card component.")
                        .variant("primary")
                        .build()""",
                """
                    class Card {
                        var title = ""
                        var body = ""
                        var variant = "default"

                        companion object {
                            fun create() = Card()
                        }

                        fun title(t: String) = apply { title = t }
                        fun body(b: String) = apply { body = b }
                        fun variant(v: String) = apply { variant = v }

                        fun build(): ReactElement = El.div("card card-$variant",

                            h3(title),
                            p(body)
                        )
                    }

                    // Usage:
                    Card.create()
                        .title("Welcome")
                        .body("This is a card component.")
                        .variant("primary")
                        .build()"""
            ),

            // ── Class-Based Components ──
            h2("Class-Based Components (ReactView)"),
            p(fragment(
                text("For components that need lifecycle methods or more complex state "),
                text("management, extend "),
                code("ReactView"),
                text(". This is similar to React class components.")
            )),

            h3("ReactView Methods"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(
                        td(code("render()")),
                        td("Required. Returns the ReactElement tree for this component.")
                    ),
                    tr(
                        td(code("onMount()")),
                        td("Called after the component is inserted into the DOM (componentDidMount).")
                    ),
                    tr(
                        td(code("onUnmount()")),
                        td("Called before the component is removed from the DOM (componentWillUnmount).")
                    )
                )
            ),

            h3("Defining a Class-Based Component"),
            CodeTabs.create(
                """
                    public class Clock extends ReactView {

                        @Override
                        public ReactElement render() {
                            return div(
                                h2("Clock Component"),
                                p("Current time: " + getTime())
                            );
                        }

                        @Override
                        public void onMount() {
                            System.out.println("Clock mounted");
                            // Start timer, add listeners, etc.
                        }

                        @Override
                        public void onUnmount() {
                            System.out.println("Clock unmounted");
                            // Clean up timer, remove listeners, etc.
                        }
                    }""",
                """
                    class Clock : ReactView() {

                        override fun render(): ReactElement {
                            return div(
                                h2("Clock Component"),
                                p("Current time: ${getTime()}")
                            )
                        }

                        override fun onMount() {
                            println("Clock mounted")
                        }

                        override fun onUnmount() {
                            println("Clock unmounted")
                        }
                    }"""
            ),

            h3("Wrapping a Class-Based Component"),
            p(fragment(
                text("Use "),
                code("ReactView.view(Constructor, \"Name\")"),
                text(" to wrap a ReactView subclass for use in the element tree:")
            )),
            CodeBlock.create("""
                // In a parent component
                return div(
                    ReactView.view(Clock::new, "Clock"),
                    ReactView.view(Dashboard::new, "Dashboard")
                );""", "java"),

            Callout.note("Functional vs Class-Based",
                p(fragment(
                    text("Prefer functional components with hooks for most use cases. "),
                    text("Class-based components via "),
                    code("ReactView"),
                    text(" are provided for cases where lifecycle methods are more "),
                    text("natural, but hooks can handle all the same scenarios.")
                ))
            ),

            // ── Utility Functions ──
            h2("Utility Functions"),

            h3("fragment()"),
            p(fragment(
                text("Groups multiple children without adding an extra DOM node. "),
                text("Equivalent to React's "),
                code("<React.Fragment>"),
                text(":")
            )),
            CodeBlock.create("""
                return fragment(
                    h1("Title"),
                    p("First paragraph"),
                    p("Second paragraph")
                );""", "java"),

            h3("text()"),
            p(fragment(
                text("Creates a raw text node. Useful when you need to mix text "),
                text("with other elements inside a "),
                code("fragment()"),
                text(" or "),
                code("p()"),
                text(":")
            )),
            CodeBlock.create("""
                p(fragment(
                    text("Click "),
                    a("here").href("#/page").build(),
                    text(" to continue.")
                ))""", "java"),

            h3("mapToElements()"),
            p(fragment(
                text("Converts a collection to an array of "),
                code("ReactElement"),
                text(" for rendering lists. Use with "),
                code(".key()"),
                text(" on each child for efficient list diffing:")
            )),
            CodeTabs.create(
                """
                    String[] items = {"Apple", "Banana", "Cherry"};

                    return ul(
                        mapToElements(items, (item, index) ->
                            li(item).key(String.valueOf(index)).build()
                        )
                    );""",
                """
                    val items = arrayOf("Apple", "Banana", "Cherry")

                    return ul(
                        mapToElements(items) { item, index ->
                            li(item).key(index.toString()).build()
                        }
                    )"""
            ),

            Callout.pitfall("Keys in Lists",
                p(fragment(
                    text("Always provide a unique "),
                    code("key"),
                    text(" when rendering lists of elements. Using the array index "),
                    text("as a key works for static lists but can cause issues with "),
                    text("dynamic lists where items are added, removed, or reordered. "),
                    text("Prefer a stable unique identifier when available.")
                ))
            ),

            // See also
            h2("See Also"),
            ul(
                li(a("HTML DSL -- Building Element Trees").href("#/reference/html-dsl").build()),
                li(a("Events -- Handling User Interactions").href("#/reference/events").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }
}

package ca.weblite.teavmreact.docs.pages.reference;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Full API reference page for useContext and ReactContext.
 */
public class UseContextPage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("useContext & ReactContext"),
            p(fragment(
                code("useContext"),
                text(" lets a component read and subscribe to a React context. "),
                text("In teavm-react, contexts are created and consumed through the "),
                code("ReactContext"),
                text(" class, which provides typed helpers for common value types.")
            )),

            // ReactContext.create() overloads
            h2("Creating a Context"),
            p(fragment(
                text("Use "),
                code("ReactContext.create()"),
                text(" to create a context with a default value:")
            )),
            CodeBlock.create("""
                // String context with default value
                ReactContext themeCtx = ReactContext.create("light");

                // Integer context with default value
                ReactContext countCtx = ReactContext.create(0);

                // Boolean context with default value
                ReactContext authCtx = ReactContext.create(false);

                // No default (null)
                ReactContext ctx = ReactContext.create();""", "java"),

            El.table("api-table",

                thead(
                    tr(
                        th("Factory Method"),
                        th("Default Value Type"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(
                        td(code("ReactContext.create()")),
                        td("none (null)"),
                        td("Creates a context with no default value.")
                    ),
                    tr(
                        td(code("ReactContext.create(String)")),
                        td(code("String")),
                        td("Creates a context with a string default.")
                    ),
                    tr(
                        td(code("ReactContext.create(int)")),
                        td(code("int")),
                        td("Creates a context with an integer default.")
                    ),
                    tr(
                        td(code("ReactContext.create(boolean)")),
                        td(code("boolean")),
                        td("Creates a context with a boolean default.")
                    )
                )
            ),

            // Providing values
            h2("Providing Context Values"),
            p(fragment(
                text("Wrap a subtree with "),
                code("context.provide(value, children...)"),
                text(" to make a value available to all descendants:")
            )),
            CodeTabs.create(
                """
                    static final ReactContext THEME =
                        ReactContext.create("light");

                    public static ReactElement render(JSObject props) {
                        var theme = Hooks.useState("light");

                        return THEME.provide(theme.getString(),
                            div(
                                button("Toggle Theme")
                                    .onClick(e -> theme.setString(
                                        theme.getString().equals("light")
                                            ? "dark" : "light"))
                                    .build(),
                                component(ChildComponent::render,
                                    "ChildComponent")
                            )
                        );
                    }""",
                """
                    val THEME = ReactContext.create("light")

                    fun render(props: JSObject): ReactElement {
                        val theme = Hooks.useState("light")

                        return THEME.provide(theme.string,
                            div(
                                button("Toggle Theme")
                                    .onClick {
                                        theme.string = if (theme.string == "light")
                                            "dark" else "light"
                                    }
                                    .build(),
                                component(ChildComponent::render,
                                    "ChildComponent")
                            )
                        )
                    }"""
            ),

            // Consuming values
            h2("Consuming Context Values"),
            p("There are two ways to read a context value: using the typed helpers on ReactContext, or using Hooks.useContext directly."),

            h3("Typed Helpers (Recommended)"),
            p(fragment(
                text("ReactContext provides typed read methods that are the preferred "),
                text("way to consume context in teavm-react:")
            )),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("Return Type"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(
                        td(code("context.useString()")),
                        td(code("String")),
                        td("Reads the context value as a string.")
                    ),
                    tr(
                        td(code("context.useInt()")),
                        td(code("int")),
                        td("Reads the context value as an integer.")
                    ),
                    tr(
                        td(code("context.useBool()")),
                        td(code("boolean")),
                        td("Reads the context value as a boolean.")
                    ),
                    tr(
                        td(code("context.useDouble()")),
                        td(code("double")),
                        td("Reads the context value as a double.")
                    )
                )
            ),

            CodeBlock.create("""
                // In a child component
                public static ReactElement render(JSObject props) {
                    String theme = THEME.useString();

                    return div(
                        p("Current theme: " + theme)
                    ).className("theme-" + theme).build();
                }""", "java"),

            h3("Hooks.useContext (Low-Level)"),
            p(fragment(
                text("You can also use "),
                code("Hooks.useContext(context.raw())"),
                text(" directly, which returns a raw "),
                code("JSObject"),
                text(". You will need to cast or extract values manually:")
            )),
            CodeBlock.create("""
                JSObject value = Hooks.useContext(myContext.raw());""", "java"),

            // Full theme example
            h2("Complete Example: Theme Switching"),
            p("A full example showing context creation, providing, and consuming across multiple components:"),
            CodeTabs.create(
                """
                    // ThemeContext.java
                    public class ThemeContext {
                        public static final ReactContext THEME =
                            ReactContext.create("light");
                    }

                    // App.java
                    public class App {
                        public static ReactElement render(JSObject props) {
                            var theme = Hooks.useState("light");

                            return ThemeContext.THEME.provide(
                                theme.getString(),
                                div(
                                    component(ThemeToggle::render,
                                        "ThemeToggle"),
                                    component(ThemedCard::render,
                                        "ThemedCard")
                                )
                            );
                        }
                    }

                    // ThemeToggle.java
                    public class ThemeToggle {
                        public static ReactElement render(JSObject props) {
                            String theme = ThemeContext.THEME.useString();
                            return button("Theme: " + theme)
                                .className("theme-toggle")
                                .build();
                        }
                    }

                    // ThemedCard.java
                    public class ThemedCard {
                        public static ReactElement render(JSObject props) {
                            String theme = ThemeContext.THEME.useString();
                            boolean isDark = theme.equals("dark");

                            return div(
                                h3("Themed Card"),
                                p("This card adapts to the current theme.")
                            ).className(isDark
                                ? "card card-dark"
                                : "card card-light").build();
                        }
                    }""",
                """
                    // ThemeContext.kt
                    object ThemeContext {
                        val THEME = ReactContext.create("light")
                    }

                    // App.kt
                    fun renderApp(props: JSObject): ReactElement {
                        val theme = Hooks.useState("light")

                        return ThemeContext.THEME.provide(
                            theme.string,
                            div(
                                component(::renderToggle, "Toggle"),
                                component(::renderCard, "Card")
                            )
                        )
                    }

                    // ThemeToggle.kt
                    fun renderToggle(props: JSObject): ReactElement {
                        val theme = ThemeContext.THEME.useString()
                        return button("Theme: $theme")
                            .className("theme-toggle")
                            .build()
                    }

                    // ThemedCard.kt
                    fun renderCard(props: JSObject): ReactElement {
                        val theme = ThemeContext.THEME.useString()
                        val isDark = theme == "dark"

                        return div(
                            h3("Themed Card"),
                            p("This card adapts to the current theme.")
                        ).className(
                            if (isDark) "card card-dark"
                            else "card card-light"
                        ).build()
                    }"""
            ),

            // Nested providers
            h2("Nested Providers"),
            p("You can nest providers to override context values for a subtree:"),
            CodeBlock.create("""
                return THEME.provide("light",
                    div(
                        component(Header::render, "Header"),  // sees "light"
                        THEME.provide("dark",
                            div(
                                component(Sidebar::render,
                                    "Sidebar")  // sees "dark"
                            )
                        ),
                        component(Footer::render, "Footer")   // sees "light"
                    )
                );""", "java"),

            Callout.note("Context and Re-renders",
                p("When a context value changes, all components that consume that context will re-render. Keep context values as narrow as possible to avoid unnecessary re-renders.")
            ),

            Callout.pitfall("Missing Provider",
                p(fragment(
                    text("If a component calls "),
                    code("useString()"),
                    text(" on a context but there is no matching Provider above it "),
                    text("in the tree, it will receive the default value passed to "),
                    code("ReactContext.create()"),
                    text(". If no default was set, the value will be null.")
                ))
            ),

            // See also
            h2("See Also"),
            ul(
                li(a("useState -- Local Component State").href("#/reference/use-state").build()),
                li(a("Components -- Creating Components").href("#/reference/components").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }
}

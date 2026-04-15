package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactContext;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.html.Style;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Documentation page: Context.
 */
public class ContextPage {

    // Context for the live demo
    private static final ReactContext THEME_CTX = ReactContext.create("light");

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("Context"),
            p("Usually, you pass information from a parent component to a child " +
              "component via props. But passing props can become verbose and " +
              "inconvenient when you need to pass them through many intermediate " +
              "components, or when many components need the same information. " +
              "Context lets a parent component provide data to the entire subtree " +
              "below it."),

            // Section 1: The problem
            h2("The Problem: Prop Drilling"),
            p("Imagine you have a theme setting that many components need to read. " +
              "Without context, you would have to pass the theme through every " +
              "intermediate component, even ones that don't use it themselves:"),

            CodeBlock.create(
                """
                // Without context: prop drilling
                App -> Layout(theme) -> Sidebar(theme) -> Button(theme)
                                     -> Content(theme) -> Card(theme)
                                                       -> Header(theme)

                // Every component in the chain must accept and forward
                // the "theme" prop, even if it doesn't use it.""",
                "java"
            ),

            p("Context solves this by letting any component in the tree read the " +
              "value directly, without passing it through props."),

            // Section 2: Creating context
            h2("Creating a Context with ReactContext.create()"),
            p(
                text("Use "),
                code("ReactContext.create()"),
                text(" to create a context object. You can provide a typed default " +
                     "value that will be used when no Provider is found above a component.")
            ),

            CodeTabs.create(
                """
                import ca.weblite.teavmreact.core.ReactContext;

                // Create a context with a String default value
                public static final ReactContext THEME_CTX =
                    ReactContext.create("light");

                // Create a context with an int default
                public static final ReactContext FONT_SIZE_CTX =
                    ReactContext.create(16);

                // Create a context with a boolean default
                public static final ReactContext DARK_MODE_CTX =
                    ReactContext.create(false);""",
                """
                import ca.weblite.teavmreact.core.ReactContext

                // Typed contexts with defaults
                val ThemeCtx = ReactContext.create("light")
                val FontSizeCtx = ReactContext.create(16)
                val DarkModeCtx = ReactContext.create(false)"""
            ),

            Callout.note("Context objects are static",
                p("Create context objects as static fields (or top-level vals in " +
                  "Kotlin). They should be created once and shared across the " +
                  "application. Do not create contexts inside render functions.")
            ),

            // Section 3: Providing context values
            h2("Providing Context Values"),
            p(
                text("Wrap part of your component tree with "),
                code("context.provide(value, ...children)"),
                text(" to make a value available to all descendants.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var theme = Hooks.useState("light");

                    // All children inside provide() can read this value
                    return THEME_CTX.provide(theme.getString(),
                        div(
                            h1("My App"),
                            button(
                                "Toggle to "
                                + (theme.getString().equals("light")
                                    ? "dark" : "light")
                            )
                            .onClick(e -> theme.setString(
                                theme.getString().equals("light")
                                    ? "dark" : "light"))
                            .build(),
                            // Deeply nested children can read THEME_CTX
                            component(Toolbar::render, "Toolbar"),
                            component(Content::render, "Content")
                        )
                    );
                }""",
                """
                val App = component("App") {
                    var theme by useState("light")

                    ThemeCtx.provide(theme) {
                        div {
                            h1("My App")
                            button("Toggle to ${'$'}{if (theme == "light") "dark" else "light"}") {
                                onClick {
                                    theme = if (theme == "light") "dark" else "light"
                                }
                            }
                            +Toolbar
                            +Content
                        }
                    }
                }"""
            ),

            // Section 4: Consuming context
            h2("Consuming Context Values"),
            p(
                text("Inside any component that is a descendant of a Provider, call "),
                code("useString()"),
                text(", "),
                code("useInt()"),
                text(", or "),
                code("useBool()"),
                text(" on the context object to read the current value.")
            ),

            CodeTabs.create(
                """
                public class ThemedButton {
                    public static ReactElement render(JSObject props) {
                        // Read the current theme from context
                        String theme = THEME_CTX.useString();

                        boolean isDark = theme.equals("dark");
                        String className = isDark
                            ? "btn btn-dark" : "btn btn-light";

                        return button("I'm themed!")
                            .className(className)
                            .build();
                    }
                }

                // For int context:
                // int fontSize = FONT_SIZE_CTX.useInt();

                // For boolean context:
                // boolean darkMode = DARK_MODE_CTX.useBool();""",
                """
                val ThemedButton = component("ThemedButton") {
                    val theme = ThemeCtx.useString()
                    val isDark = theme == "dark"

                    button("I'm themed!") {
                        className = if (isDark) "btn btn-dark" else "btn btn-light"
                    }
                }"""
            ),

            Callout.deepDive("Context and re-rendering",
                p("When a context value changes, React re-renders every component " +
                  "that consumes that context. This happens automatically -- you " +
                  "don't need to do anything special. If performance is a concern, " +
                  "you can split your contexts (one for theme, one for user, etc.) " +
                  "so that components only re-render when the specific context " +
                  "they consume changes.")
            ),

            // Section 5: Live demo
            h2("Live Demo: Theme Toggle"),
            p("Click the toggle button to switch between light and dark themes. " +
              "The themed panel below reads the theme from context."),

            LiveDemo.create(ContextPage::themeDemo),

            CodeBlock.create(
                """
                private static final ReactContext THEME_CTX =
                    ReactContext.create("light");

                private static ReactElement themeDemo(JSObject props) {
                    var theme = Hooks.useState("light");
                    boolean isDark = theme.getString().equals("dark");

                    return THEME_CTX.provide(theme.getString(),
                        div(
                            button(isDark
                                    ? "Switch to Light"
                                    : "Switch to Dark")
                                .onClick(e -> theme.setString(
                                    isDark ? "light" : "dark"))
                                .build(),
                            // This component reads theme from context:
                            component(
                                ContextPage::themedPanel, "ThemedPanel")
                        )
                    );
                }

                private static ReactElement themedPanel(JSObject props) {
                    String theme = THEME_CTX.useString();
                    boolean isDark = theme.equals("dark");

                    Style style = Style.create()
                        .padding("20px")
                        .borderRadius("8px")
                        .backgroundColor(isDark ? "#1a1a2e" : "#f0f0f5")
                        .color(isDark ? "#e0e0e0" : "#333");

                    return div(
                        h3("Themed Panel"),
                        p("Current theme: " + theme),
                        p("This panel reads the theme from context, "
                          + "not from props!")
                    ).style(style).build();
                }""",
                "java"
            ),

            // Recap
            h2("Recap"),
            ul(
                li("Context solves prop drilling by providing values to an entire subtree."),
                li(
                    text("Create contexts with "),
                    code("ReactContext.create(defaultValue)"),
                    text(" as static fields.")
                ),
                li(
                    text("Provide values with "),
                    code("context.provide(value, ...children)"),
                    text(".")
                ),
                li(
                    text("Consume values with "),
                    code("context.useString()"),
                    text(", "),
                    code("context.useInt()"),
                    text(", or "),
                    code("context.useBool()"),
                    text(".")
                ),
                li("Changing a context value re-renders all consumers automatically.")
            )
        );
    }

    private static ReactElement themeDemo(JSObject props) {
        var theme = Hooks.useState("light");
        boolean isDark = theme.getString().equals("dark");

        return THEME_CTX.provide(theme.getString(),
            div(
                button(isDark ? "Switch to Light" : "Switch to Dark")
                    .className("demo-btn")
                    .onClick(e -> theme.setString(isDark ? "light" : "dark"))
                    .build(),
                component(ContextPage::themedPanel, "ThemedPanel")
            )
        );
    }

    private static ReactElement themedPanel(JSObject props) {
        String theme = THEME_CTX.useString();
        boolean isDark = theme.equals("dark");

        Style style = Style.create()
            .padding("20px")
            .marginTop("12px")
            .borderRadius("8px")
            .border("1px solid " + (isDark ? "#333" : "#ddd"))
            .backgroundColor(isDark ? "#1a1a2e" : "#f0f0f5")
            .color(isDark ? "#e0e0e0" : "#333")
            .transition("all 0.3s ease");

        return El.styledDiv(style,
            h3("Themed Panel"),
            p("Current theme: " + theme),
            p("This panel reads the theme from context, not from props!")
        );
    }
}

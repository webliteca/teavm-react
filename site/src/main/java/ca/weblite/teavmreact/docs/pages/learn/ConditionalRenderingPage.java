package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Documentation page: Conditional Rendering.
 */
public class ConditionalRenderingPage {

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("Conditional Rendering"),
            p("Your components will often need to display different things " +
              "depending on different conditions. In teavm-react, you use standard " +
              "Java control flow -- if statements, ternary operators, and short-circuit " +
              "evaluation -- to conditionally render elements."),

            // Section 1: Ternary operator
            h2("Ternary Operator"),
            p("The simplest form of conditional rendering uses Java's ternary " +
              "operator to choose between two elements inline."),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var loggedIn = Hooks.useState(false);

                    return div(
                        loggedIn.getBool()
                            ? h1("Welcome back!")
                            : h1("Please sign in."),
                        button(loggedIn.getBool() ? "Log out" : "Log in")
                            .onClick(e -> loggedIn.setBool(!loggedIn.getBool()))
                            .build()
                    );
                }""",
                """
                val LoginBanner = component("LoginBanner") {
                    var loggedIn by useState(false)

                    div {
                        if (loggedIn) h1("Welcome back!")
                        else h1("Please sign in.")

                        button(if (loggedIn) "Log out" else "Log in") {
                            onClick { loggedIn = !loggedIn }
                        }
                    }
                }"""
            ),

            LiveDemo.create(ConditionalRenderingPage::ternaryDemo),

            // Section 2: if/else for complex logic
            h2("Using if/else for Complex Logic"),
            p("When you have more than two branches, or when the conditional " +
              "logic is more complex, use regular if/else statements. Build " +
              "the element in a local variable, then include it in the tree."),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var status = Hooks.useState("pending");
                    String current = status.getString();

                    ReactElement statusIcon;
                    if (current.equals("success")) {
                        statusIcon = span("Done");
                    } else if (current.equals("error")) {
                        statusIcon = span("Failed");
                    } else {
                        statusIcon = span("Loading...");
                    }

                    return div(
                        h2("Order Status"),
                        statusIcon,
                        div(
                            button("Success")
                                .onClick(e -> status.setString("success"))
                                .build(),
                            button("Error")
                                .onClick(e -> status.setString("error"))
                                .build(),
                            button("Pending")
                                .onClick(e -> status.setString("pending"))
                                .build()
                        )
                    );
                }""",
                """
                val StatusPage = component("StatusPage") {
                    var status by useState("pending")

                    div {
                        h2("Order Status")
                        when (status) {
                            "success" -> span("Done")
                            "error" -> span("Failed")
                            else -> span("Loading...")
                        }
                        div {
                            button("Success") { onClick { status = "success" } }
                            button("Error") { onClick { status = "error" } }
                            button("Pending") { onClick { status = "pending" } }
                        }
                    }
                }"""
            ),

            // Section 3: Returning nothing
            h2("Conditionally Returning Nothing (null)"),
            p(
                text("Sometimes you want to render nothing at all. In React, returning "),
                code("null"),
                text(" from a component (or using null in place of an element) tells React "),
                text("to skip rendering that part of the tree.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var showWarning = Hooks.useState(false);

                    return div(
                        // Conditionally include the warning
                        showWarning.getBool()
                            ? p("Warning: this action cannot be undone!")
                            : null,
                        button(showWarning.getBool()
                                ? "Hide Warning" : "Show Warning")
                            .onClick(e -> showWarning.setBool(
                                !showWarning.getBool()))
                            .build()
                    );
                }""",
                """
                val WarningDemo = component("WarningDemo") {
                    var showWarning by useState(false)

                    div {
                        if (showWarning) {
                            p("Warning: this action cannot be undone!")
                        }
                        button(
                            if (showWarning) "Hide Warning"
                            else "Show Warning"
                        ) {
                            onClick { showWarning = !showWarning }
                        }
                    }
                }"""
            ),

            Callout.note("null is safe",
                p("React simply skips null children. You can safely include " +
                  "null in your element tree without causing errors. This makes " +
                  "the ternary pattern (condition ? element : null) a concise way " +
                  "to conditionally show or hide content.")
            ),

            // Section 4: Kotlin DSL show()
            h2("Kotlin DSL: show()"),
            p("The Kotlin DSL provides a show() helper that conditionally " +
              "includes a block of elements. This is more idiomatic in Kotlin than " +
              "using ternary or null."),

            CodeTabs.create(
                """
                // Java doesn't have a direct equivalent of show().
                // Use the ternary pattern instead:
                public static ReactElement render(JSObject props) {
                    var isAdmin = Hooks.useState(false);

                    return div(
                        h1("Dashboard"),
                        isAdmin.getBool()
                            ? El.div("admin-panel",

                                h2("Admin Panel"),
                                p("Manage users and settings here.")
                              )
                            : null
                    );
                }""",
                """
                val Dashboard = component("Dashboard") {
                    var isAdmin by useState(false)

                    div {
                        h1("Dashboard")
                        show(isAdmin) {
                            div {
                                className = "admin-panel"
                                h2("Admin Panel")
                                p("Manage users and settings here.")
                            }
                        }
                    }
                }"""
            ),

            Callout.deepDive("How conditional rendering works in the virtual DOM",
                p("When a condition changes from true to false (or vice versa), " +
                  "React diffs the new virtual DOM tree against the previous one. " +
                  "If an element was present and is now null, React removes the " +
                  "corresponding DOM node. If an element was null and now exists, " +
                  "React creates and inserts it. This is why keys matter when elements " +
                  "change type -- React needs to know whether to update or replace.")
            ),

            // Recap
            h2("Recap"),
            ul(
                li(
                    text("Use the ternary operator ("),
                    code("condition ? a : b"),
                    text(") for simple two-way choices.")
                ),
                li("Use if/else statements when logic is more complex."),
                li(
                    text("Return "),
                    code("null"),
                    text(" to render nothing.")
                ),
                li(
                    text("In Kotlin, use "),
                    code("show(condition)"),
                    text(" for clean conditional blocks.")
                )
            )
        );
    }

    private static ReactElement ternaryDemo(JSObject props) {
        var loggedIn = Hooks.useState(false);

        return div(
            loggedIn.getBool()
                ? h1("Welcome back!")
                : h1("Please sign in."),
            button(loggedIn.getBool() ? "Log out" : "Log in")
                .className("demo-btn")
                .onClick(e -> loggedIn.setBool(!loggedIn.getBool()))
                .build()
        );
    }
}

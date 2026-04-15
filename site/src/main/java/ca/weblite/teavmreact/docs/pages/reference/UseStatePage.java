package ca.weblite.teavmreact.docs.pages.reference;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Full API reference page for the useState hook.
 */
public class UseStatePage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("useState"),
            p(fragment(
                code("useState"),
                text(" adds local state to a functional component. It returns a "),
                code("StateHandle"),
                text(" that provides typed getters and setters for the state value.")
            )),

            // Method signatures
            h2("Method Signatures"),
            CodeBlock.create("""
                // Integer state
                StateHandle state = Hooks.useState(int initialValue);

                // String state
                StateHandle state = Hooks.useState(String initialValue);

                // Boolean state
                StateHandle state = Hooks.useState(boolean initialValue);

                // Double state
                StateHandle state = Hooks.useState(double initialValue);""", "java"),

            p(fragment(
                text("Each overload infers the type from the initial value. The returned "),
                code("StateHandle"),
                text(" provides type-specific accessors to read and update the value.")
            )),

            // StateHandle methods table
            h2("StateHandle Methods"),
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
                        td(code("getInt()")),
                        td(code("int")),
                        td("Returns the current integer value.")
                    ),
                    tr(
                        td(code("setInt(int value)")),
                        td(code("void")),
                        td("Sets the state to a new integer value and triggers a re-render.")
                    ),
                    tr(
                        td(code("updateInt(IntUnaryOperator fn)")),
                        td(code("void")),
                        td("Updates state using a function that receives the previous value.")
                    ),
                    tr(
                        td(code("getString()")),
                        td(code("String")),
                        td("Returns the current string value.")
                    ),
                    tr(
                        td(code("setString(String value)")),
                        td(code("void")),
                        td("Sets the state to a new string value and triggers a re-render.")
                    ),
                    tr(
                        td(code("getBool()")),
                        td(code("boolean")),
                        td("Returns the current boolean value.")
                    ),
                    tr(
                        td(code("setBool(boolean value)")),
                        td(code("void")),
                        td("Sets the state to a new boolean value and triggers a re-render.")
                    ),
                    tr(
                        td(code("getDouble()")),
                        td(code("double")),
                        td("Returns the current double value.")
                    ),
                    tr(
                        td(code("setDouble(double value)")),
                        td(code("void")),
                        td("Sets the state to a new double value and triggers a re-render.")
                    )
                )
            ),

            // Usage examples
            h2("Usage Examples"),

            h3("Integer State"),
            CodeTabs.create(
                """
                    var count = Hooks.useState(0);

                    return div(
                        p("Count: " + count.getInt()),
                        button("Increment")
                            .onClick(e -> count.setInt(count.getInt() + 1))
                            .build()
                    );""",
                """
                    val count = Hooks.useState(0)

                    return div(
                        p("Count: ${count.int}"),
                        button("Increment")
                            .onClick { count.int = count.int + 1 }
                            .build()
                    )"""
            ),

            h3("String State"),
            CodeTabs.create(
                """
                    var name = Hooks.useState("");

                    return div(
                        input("text")
                            .value(name.getString())
                            .onChange(e -> name.setString(
                                e.getTarget().getValue()))
                            .placeholder("Enter your name")
                            .build(),
                        p("Hello, " + name.getString() + "!")
                    );""",
                """
                    val name = Hooks.useState("")

                    return div(
                        input("text")
                            .value(name.string)
                            .onChange { name.string = it.target.value }
                            .placeholder("Enter your name")
                            .build(),
                        p("Hello, ${name.string}!")
                    )"""
            ),

            h3("Boolean State"),
            CodeTabs.create(
                """
                    var visible = Hooks.useState(true);

                    return div(
                        button(visible.getBool() ? "Hide" : "Show")
                            .onClick(e -> visible.setBool(!visible.getBool()))
                            .build(),
                        visible.getBool()
                            ? p("This content is visible")
                            : text("")
                    );""",
                """
                    val visible = Hooks.useState(true)

                    return div(
                        button(if (visible.bool) "Hide" else "Show")
                            .onClick { visible.bool = !visible.bool }
                            .build(),
                        if (visible.bool) p("This content is visible")
                        else text("")
                    )"""
            ),

            // Functional updates pitfall
            Callout.pitfall("Use Functional Updates for State Based on Previous Value",
                p(fragment(
                    text("When updating state based on the previous value, always use "),
                    code("updateInt()"),
                    text(" instead of "),
                    code("setInt()"),
                    text(". Calling "),
                    code("setInt(count.getInt() + 1)"),
                    text(" inside an async callback or event handler may use a stale value.")
                )),
                CodeBlock.create("""
                    // WRONG: may use stale value in async contexts
                    count.setInt(count.getInt() + 1);

                    // CORRECT: always uses the latest value
                    count.updateInt(prev -> prev + 1);""", "java")
            ),

            // Multiple state variables
            h2("Multiple State Variables"),
            p("You can call useState multiple times to manage independent pieces of state:"),
            CodeBlock.create("""
                var name = Hooks.useState("");
                var age = Hooks.useState(0);
                var agreed = Hooks.useState(false);

                return div(
                    input("text")
                        .value(name.getString())
                        .onChange(e -> name.setString(
                            e.getTarget().getValue()))
                        .build(),
                    p("Name: " + name.getString()),
                    p("Age: " + age.getInt()),
                    p("Agreed: " + agreed.getBool())
                );""", "java"),

            Callout.note("State Identity",
                p("Each call to useState is independent. React identifies state by the order in which hooks are called, so always call hooks in the same order on every render.")
            ),

            // Live demo
            h2("Live Demo: Counter"),
            p("An interactive counter demonstrating useState with integer state and functional updates:"),
            LiveDemo.create(UseStatePage::counterDemo),

            // See also
            h2("See Also"),
            ul(
                li(a("useEffect -- Reacting to State Changes").href("#/reference/use-effect").build()),
                li(a("useRef -- Values That Don't Trigger Re-renders").href("#/reference/use-ref").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }

    private static ReactElement counterDemo(JSObject props) {
        var count = Hooks.useState(0);

        return El.div("demo-counter",

            p("Count: " + count.getInt()),
            El.div("demo-btn-group",

                button("- 1")
                    .onClick(e -> count.updateInt(c -> c - 1))
                    .className("demo-btn")
                    .build(),
                button("Reset")
                    .onClick(e -> count.setInt(0))
                    .className("demo-btn")
                    .build(),
                button("+ 1")
                    .onClick(e -> count.updateInt(c -> c + 1))
                    .className("demo-btn")
                    .build()
            )
        );
    }
}

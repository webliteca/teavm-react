package ca.weblite.teavmreact.docs.pages.learn;

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
 * Documentation page: State -- A Component's Memory.
 */
public class StatePage {

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("State: A Component's Memory"),
            p("Components often need to change what's on the screen as a result " +
              "of an interaction. Typing into a form should update the input field, " +
              "clicking a button should change which content is displayed. Components " +
              "need to \"remember\" things: the current input value, the current " +
              "count, whether a panel is open. In React, this kind of " +
              "component-specific memory is called state."),

            // Section 1: Why you need state
            h2("Why Local Variables Aren't Enough"),
            p("Regular Java local variables don't survive between renders. " +
              "Every time React calls your render function, it starts from scratch. " +
              "Changes to local variables won't trigger a re-render, and even if " +
              "they did, the value would be reset on the next call."),

            CodeTabs.create(
                """
                // THIS DOESN'T WORK!
                public static ReactElement render(JSObject props) {
                    int count = 0; // resets to 0 every render

                    return div(
                        p("Count: " + count),
                        button("Increment")
                            .onClick(e -> {
                                // count++; // can't modify local variable
                                // Even if we could, React wouldn't re-render
                            })
                            .build()
                    );
                }""",
                """
                // THIS DOESN'T WORK!
                val BrokenCounter = component("BrokenCounter") {
                    var count = 0 // resets to 0 every render

                    div {
                        p("Count: ${'$'}count")
                        button("Increment") {
                            onClick {
                                count++ // won't trigger re-render
                            }
                        }
                    }
                }"""
            ),

            p("To update a component, you need two things:"),
            ol(
                li("A way to retain data between renders."),
                li("A way to trigger React to re-render the component with new data.")
            ),
            p(
                text("The "),
                code("Hooks.useState()"),
                text(" hook provides both.")
            ),

            // Section 2: useState
            h2("Using useState"),
            p(
                text("The "),
                code("Hooks.useState()"),
                text(" hook takes an initial value and returns a "),
                code("StateHandle"),
                text(" that provides getters and setters for the state value. " +
                     "teavm-react provides typed overloads for the common Java primitives.")
            ),

            CodeTabs.create(
                """
                import ca.weblite.teavmreact.hooks.Hooks;

                public static ReactElement render(JSObject props) {
                    // int state
                    var count = Hooks.useState(0);

                    // String state
                    var name = Hooks.useState("World");

                    // boolean state
                    var visible = Hooks.useState(true);

                    // double state
                    var price = Hooks.useState(9.99);

                    return div(
                        p("Count: " + count.getInt()),
                        p("Name: " + name.getString()),
                        p("Visible: " + visible.getBool()),
                        p("Price: " + price.getDouble())
                    );
                }""",
                """
                val Demo = component("Demo") {
                    // Kotlin delegates provide direct property access
                    var count by useState(0)
                    var name by useState("World")
                    var visible by useState(true)
                    var price by useState(9.99)

                    div {
                        p("Count: ${'$'}count")
                        p("Name: ${'$'}name")
                        p("Visible: ${'$'}visible")
                        p("Price: ${'$'}price")
                    }
                }"""
            ),

            // Section 3: StateHandle API
            h2("The StateHandle API"),
            p(
                text("The "),
                code("StateHandle"),
                text(" returned by "),
                code("Hooks.useState()"),
                text(" provides typed getters and setters:")
            ),

            El.table("docs-table",

                thead(
                    tr(
                        th("Method"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(
                        td(code("getInt()")),
                        td("Returns the current state as an int.")
                    ),
                    tr(
                        td(code("setInt(int value)")),
                        td("Sets the state to a new int value and triggers a re-render.")
                    ),
                    tr(
                        td(code("getString()")),
                        td("Returns the current state as a String.")
                    ),
                    tr(
                        td(code("setString(String value)")),
                        td("Sets the state to a new String value.")
                    ),
                    tr(
                        td(code("getBool()")),
                        td("Returns the current state as a boolean.")
                    ),
                    tr(
                        td(code("setBool(boolean value)")),
                        td("Sets the state to a new boolean value.")
                    ),
                    tr(
                        td(code("getDouble()")),
                        td("Returns the current state as a double.")
                    ),
                    tr(
                        td(code("setDouble(double value)")),
                        td("Sets the state to a new double value.")
                    ),
                    tr(
                        td(code("updateInt(IntUpdater)")),
                        td("Functional update: receives previous value, returns new value.")
                    ),
                    tr(
                        td(code("updateString(StringUpdater)")),
                        td("Functional update for String state.")
                    )
                )
            ),

            // Section 4: Functional updates
            h2("Functional Updates"),
            p(
                text("When your next state depends on the previous state, use "),
                code("updateInt(prev -> prev + 1)"),
                text(" instead of "),
                code("setInt(count.getInt() + 1)"),
                text(". This ensures you always work with the latest value, " +
                     "even when multiple updates are batched together.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var count = Hooks.useState(0);

                    return div(
                        p("Count: " + count.getInt()),
                        button("+1")
                            .onClick(e -> {
                                // SAFE: uses functional update
                                count.updateInt(n -> n + 1);
                            })
                            .build(),
                        button("+3 (batched)")
                            .onClick(e -> {
                                // All three updates use the latest value
                                count.updateInt(n -> n + 1);
                                count.updateInt(n -> n + 1);
                                count.updateInt(n -> n + 1);
                            })
                            .build()
                    );
                }""",
                """
                val Counter = component("Counter") {
                    var count by useState(0)

                    div {
                        p("Count: ${'$'}count")
                        button("+1") {
                            onClick { count++ }
                        }
                        button("+3 (batched)") {
                            onClick {
                                // Kotlin delegates handle batching
                                count += 3
                            }
                        }
                    }
                }"""
            ),

            Callout.pitfall("Avoid reading state right after setting it",
                p(
                    text("After calling "),
                    code("count.setInt(5)"),
                    text(", the value of "),
                    code("count.getInt()"),
                    text(" is still the old value within the same event handler. " +
                         "The new value is only available on the next render. " +
                         "Use functional updates ("),
                    code("updateInt"),
                    text(") when you need to chain state changes.")
                )
            ),

            // Section 5: Multiple state variables
            h2("Multiple State Variables"),
            p("A single component can hold as many state variables as you need. " +
              "Each call to useState is independent."),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var name = Hooks.useState("");
                    var age = Hooks.useState(0);
                    var agreed = Hooks.useState(false);

                    return div(
                        div(
                            text("Name: "),
                            input("text")
                                .value(name.getString())
                                .onChange(e -> name.setString(
                                    e.getTarget().getValue()))
                                .build()
                        ),
                        div(
                            text("Age: "),
                            input("number")
                                .value(String.valueOf(age.getInt()))
                                .onChange(e -> {
                                    String val = e.getTarget().getValue();
                                    try {
                                        age.setInt(Integer.parseInt(val));
                                    } catch (NumberFormatException ex) {
                                        // ignore invalid input
                                    }
                                })
                                .build()
                        ),
                        p("Name: " + name.getString()
                            + ", Age: " + age.getInt()
                            + ", Agreed: " + agreed.getBool())
                    );
                }""",
                """
                val SignupForm = component("SignupForm") {
                    var name by useState("")
                    var age by useState(0)
                    var agreed by useState(false)

                    div {
                        input("text") {
                            value = name
                            onChange { name = it.target.value }
                        }
                        input("number") {
                            value = age.toString()
                            onChange {
                                age = it.target.value.toIntOrNull() ?: 0
                            }
                        }
                        p("Name: ${'$'}name, Age: ${'$'}age, Agreed: ${'$'}agreed")
                    }
                }"""
            ),

            // Section 6: State is isolated
            h2("State Is Isolated Per Component Instance"),
            p("Each instance of a component has its own independent state. " +
              "Rendering the same component twice creates two separate state " +
              "containers that don't affect each other."),

            CodeTabs.create(
                """
                // Each Counter instance has its own count
                public class App {
                    public static ReactElement render(JSObject props) {
                        return div(
                            h2("Two independent counters:"),
                            component(Counter::render, "Counter"),
                            component(Counter::render, "Counter")
                        );
                    }
                }

                public class Counter {
                    public static ReactElement render(JSObject props) {
                        var count = Hooks.useState(0);
                        return div(
                            p("Count: " + count.getInt()),
                            button("+1")
                                .onClick(e ->
                                    count.updateInt(n -> n + 1))
                                .build()
                        );
                    }
                }""",
                """
                val Counter = component("Counter") {
                    var count by useState(0)
                    div {
                        p("Count: ${'$'}count")
                        button("+1") { onClick { count++ } }
                    }
                }

                val App = component("App") {
                    div {
                        h2("Two independent counters:")
                        +Counter  // instance 1
                        +Counter  // instance 2, separate state
                    }
                }"""
            ),

            Callout.note("State lives in React, not in Java",
                p("State is managed by React's internal fiber tree, not by Java " +
                  "object fields. This is why useState must be called in the same " +
                  "order on every render -- React identifies each piece of state " +
                  "by its position in the hook call sequence.")
            ),

            // Section 7: Live demo
            h2("Live Demo: Counter with Configurable Step"),
            p("Try changing the step size and clicking the buttons."),

            LiveDemo.create(StatePage::counterDemo),

            CodeBlock.create(
                """
                private static ReactElement counterDemo(JSObject props) {
                    var count = Hooks.useState(0);
                    var step = Hooks.useState(1);

                    return div(
                        div(
                            text("Step: "),
                            input("number")
                                .value(String.valueOf(step.getInt()))
                                .onChange(e -> {
                                    try {
                                        step.setInt(Integer.parseInt(
                                            e.getTarget().getValue()));
                                    } catch (NumberFormatException ex) {}
                                })
                                .build()
                        ),
                        p("Count: " + count.getInt()),
                        button("+ Step")
                            .onClick(e -> {
                                int s = step.getInt();
                                count.updateInt(n -> n + s);
                            })
                            .build(),
                        button("- Step")
                            .onClick(e -> {
                                int s = step.getInt();
                                count.updateInt(n -> n - s);
                            })
                            .build(),
                        button("Reset")
                            .onClick(e -> count.setInt(0))
                            .build()
                    );
                }""",
                "java"
            ),

            // Recap
            h2("Recap"),
            ul(
                li("Local variables don't persist between renders -- use state."),
                li(
                    text("Call "),
                    code("Hooks.useState(initialValue)"),
                    text(" to declare a state variable.")
                ),
                li(
                    text("The "),
                    code("StateHandle"),
                    text(" provides typed getters ("),
                    code("getInt()"),
                    text(") and setters ("),
                    code("setInt()"),
                    text(").")
                ),
                li(
                    text("Use "),
                    code("updateInt(prev -> ...)"),
                    text(" for state that depends on the previous value.")
                ),
                li("You can have multiple state variables in one component."),
                li("State is isolated per component instance.")
            )
        );
    }

    private static ReactElement counterDemo(JSObject props) {
        var count = Hooks.useState(0);
        var step = Hooks.useState(1);

        return div(
            El.div("demo-row",

                text("Step: "),
                input("number")
                    .value(String.valueOf(step.getInt()))
                    .className("demo-input demo-input-small")
                    .onChange(e -> {
                        String val = e.getTarget().getValue();
                        try {
                            step.setInt(Integer.parseInt(val));
                        } catch (NumberFormatException ex) {
                            // ignore
                        }
                    })
                    .build()
            ),
            p(
                text("Count: "),
                strong(String.valueOf(count.getInt()))
            ),
            El.div("demo-row",

                button("+ Step")
                    .className("demo-btn")
                    .onClick(e -> {
                        int s = step.getInt();
                        count.updateInt(n -> n + s);
                    })
                    .build(),
                button("- Step")
                    .className("demo-btn")
                    .onClick(e -> {
                        int s = step.getInt();
                        count.updateInt(n -> n - s);
                    })
                    .build(),
                button("Reset")
                    .className("demo-btn demo-btn-secondary")
                    .onClick(e -> count.setInt(0))
                    .build()
            )
        );
    }
}

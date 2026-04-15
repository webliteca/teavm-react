package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSObject;

import ca.weblite.teavmreact.html.DomBuilder.*;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Documentation page: Responding to Events.
 */
public class EventsPage {

    public static ReactElement render(JSObject props) {
        return Div.create().className("docs-page")
            .child(h1("Responding to Events"))
            .child(p("teavm-react lets you add event handlers to your elements. " +
              "Event handlers are Java lambda expressions that run in response " +
              "to user interactions like clicks, typing, and key presses."))

            // Section 1: onClick
            .child(h2("onClick Handler"))
            .child(p(
                text("The "),
                code("onClick"),
                text(" handler is the most common event. It fires when the user " +
                     "clicks (or taps) an element. The handler receives a "),
                code("SyntheticEvent"),
                text(".")
            ))

            .child(CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    return button("Click me!")
                        .onClick(e -> {
                            JsUtil.alert("Button was clicked!");
                        })
                        .build();
                }""",
                """
                val ClickDemo = component("ClickDemo") {
                    button("Click me!") {
                        onClick { alert("Button was clicked!") }
                    }
                }"""
            ))

            .child(Callout.note("Event handler syntax",
                p(
                    text("Notice that you pass a lambda to "),
                    code(".onClick()"),
                    text(", not the result of calling a function. "),
                    text("The lambda is called by React when the event occurs. "),
                    text("The parameter "),
                    code("e"),
                    text(" is the synthetic event object.")
                )
            ))

            // Section 2: onChange for text inputs
            .child(h2("onChange for Text Inputs"))
            .child(p(
                text("The "),
                code("onChange"),
                text(" handler fires when the value of an input element changes. " +
                     "It receives a "),
                code("ChangeEvent"),
                text(". Use "),
                code("e.getTarget().getValue()"),
                text(" to read the current input value.")
            ))

            .child(CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var name = Hooks.useState("");

                    return div(
                        input("text")
                            .placeholder("Enter your name")
                            .value(name.getString())
                            .onChange(e -> {
                                String value = e.getTarget().getValue();
                                name.setString(value);
                            })
                            .build(),
                        p("Hello, " + name.getString() + "!")
                    );
                }""",
                """
                val NameInput = component("NameInput") {
                    var name by useState("")

                    div {
                        input("text") {
                            placeholder = "Enter your name"
                            value = name
                            onChange { name = it.target.value }
                        }
                        p("Hello, ${'$'}name!")
                    }
                }"""
            ))

            // Section 3: onKeyDown / onKeyUp
            .child(h2("Keyboard Events: onKeyDown / onKeyUp"))
            .child(p(
                text("The "),
                code("onKeyDown"),
                text(" and "),
                code("onKeyUp"),
                text(" handlers fire when the user presses or releases a key. " +
                     "They receive a "),
                code("KeyboardEvent"),
                text(" with methods like "),
                code("getKey()"),
                text(", "),
                code("getCode()"),
                text(", and modifier flags.")
            ))

            .child(CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var lastKey = Hooks.useState("(none)");

                    return div(
                        input("text")
                            .placeholder("Press any key...")
                            .onKeyDown(e -> {
                                lastKey.setString(e.getKey());
                            })
                            .build(),
                        p("Last key pressed: " + lastKey.getString())
                    );
                }""",
                """
                val KeyDemo = component("KeyDemo") {
                    var lastKey by useState("(none)")

                    div {
                        input("text") {
                            placeholder = "Press any key..."
                            onKeyDown { lastKey = it.key }
                        }
                        p("Last key pressed: ${'$'}lastKey")
                    }
                }"""
            ))

            // Section 4: Event object
            .child(h2("The Event Object"))
            .child(p("All event handlers receive a typed event object. Here are the " +
              "most commonly used methods:"))

            .child(Table.create().className("docs-table")
                .child(thead(
                    tr(
                        th("Method"),
                        th("Available On"),
                        th("Description")
                    )
                ))
                .child(tbody(
                    tr(
                        td(code("getTarget()")),
                        td("All events"),
                        td("Returns the DOM element that fired the event.")
                    ),
                    tr(
                        td(code("getTarget().getValue()")),
                        td("Change events"),
                        td("Returns the current value of an input element.")
                    ),
                    tr(
                        td(code("getTarget().getChecked()")),
                        td("Change events"),
                        td("Returns whether a checkbox is checked.")
                    ),
                    tr(
                        td(code("getKey()")),
                        td("Keyboard events"),
                        td("Returns the key value (e.g. \"Enter\", \"a\").")
                    ),
                    tr(
                        td(code("getCode()")),
                        td("Keyboard events"),
                        td("Returns the physical key code (e.g. \"KeyA\").")
                    ),
                    tr(
                        td(code("getCtrlKey()")),
                        td("Keyboard events"),
                        td("True if the Ctrl key was held during the event.")
                    ),
                    tr(
                        td(code("getShiftKey()")),
                        td("Keyboard events"),
                        td("True if the Shift key was held during the event.")
                    )
                ))
                .build())

            // Section 5: preventDefault and stopPropagation
            .child(h2("Preventing Default and Stopping Propagation"))
            .child(p("Sometimes you need to prevent the browser's default behavior " +
              "(like form submission) or stop the event from bubbling up the tree."))

            .child(CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var message = Hooks.useState("");

                    return form(
                        input("text")
                            .placeholder("Type something")
                            .value(message.getString())
                            .onChange(e -> message.setString(
                                e.getTarget().getValue()))
                            .build(),
                        button("Submit")
                            .onClick(e -> {
                                // Prevent form submission (page reload)
                                e.preventDefault();
                                JsUtil.alert("Submitted: "
                                    + message.getString());
                            })
                            .build()
                    );
                }

                // To stop event propagation:
                // button("Inner")
                //     .onClick(e -> {
                //         e.stopPropagation();
                //         // parent onClick will NOT fire
                //     })
                //     .build()""",
                """
                val FormDemo = component("FormDemo") {
                    var message by useState("")

                    form {
                        onSubmit { e ->
                            e.preventDefault()
                            alert("Submitted: ${'$'}message")
                        }
                        input("text") {
                            placeholder = "Type something"
                            value = message
                            onChange { message = it.target.value }
                        }
                        button("Submit")
                    }
                }"""
            ))

            .child(Callout.pitfall("Don't call event methods outside the handler",
                p(
                    text("Methods like "),
                    code("preventDefault()"),
                    text(" and "),
                    code("stopPropagation()"),
                    text(" must be called synchronously inside the event handler. " +
                         "React pools synthetic events and resets them after the " +
                         "handler returns.")
                )
            ))

            // Section 6: Live demo
            .child(h2("Live Demo: Input Echo with Key Events"))
            .child(p("Type in the input below to see both the text value and the last key pressed."))

            .child(LiveDemo.create(EventsPage::inputEchoDemo))

            .child(CodeBlock.create(
                """
                private static ReactElement inputEchoDemo(JSObject props) {
                    var text = Hooks.useState("");
                    var lastKey = Hooks.useState("(none)");
                    var keyCount = Hooks.useState(0);

                    return div(
                        input("text")
                            .placeholder("Start typing...")
                            .value(text.getString())
                            .onChange(e -> text.setString(
                                e.getTarget().getValue()))
                            .onKeyDown(e -> {
                                lastKey.setString(e.getKey());
                                keyCount.updateInt(n -> n + 1);
                            })
                            .build(),
                        p("Text: " + text.getString()),
                        p("Last key: " + lastKey.getString()),
                        p("Total keystrokes: " + keyCount.getInt())
                    );
                }""",
                "java"
            ))

            // Recap
            .child(h2("Recap"))
            .child(ul(
                li(
                    text("Use "),
                    code(".onClick(e -> ...)"),
                    text(" for click handlers.")
                ),
                li(
                    text("Use "),
                    code(".onChange(e -> ...)"),
                    text(" for input value changes; read values with "),
                    code("e.getTarget().getValue()"),
                    text(".")
                ),
                li(
                    text("Use "),
                    code(".onKeyDown(e -> ...)"),
                    text(" for keyboard events; read the key with "),
                    code("e.getKey()"),
                    text(".")
                ),
                li(
                    text("Call "),
                    code("e.preventDefault()"),
                    text(" to stop browser defaults (like form submission).")
                ),
                li(
                    text("Call "),
                    code("e.stopPropagation()"),
                    text(" to stop the event from bubbling to parent handlers.")
                )
            ))
            .build();
    }

    private static ReactElement inputEchoDemo(JSObject props) {
        var text = Hooks.useState("");
        var lastKey = Hooks.useState("(none)");
        var keyCount = Hooks.useState(0);

        return div(
            input("text")
                .placeholder("Start typing...")
                .value(text.getString())
                .className("demo-input")
                .onChange(e -> text.setString(e.getTarget().getValue()))
                .onKeyDown(e -> {
                    lastKey.setString(e.getKey());
                    keyCount.updateInt(n -> n + 1);
                })
                .build(),
            El.div("demo-output",

                p(
                    strong("Text: "),
                    text(text.getString())
                ),
                p(
                    strong("Last key: "),
                    code(lastKey.getString())
                ),
                p(
                    strong("Total keystrokes: "),
                    text(String.valueOf(keyCount.getInt()))
                )
            )
        );
    }
}

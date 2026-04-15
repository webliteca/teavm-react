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
 * Comprehensive reference page for the teavm-react event system.
 */
public class EventsReferencePage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("Events Reference"),
            p(fragment(
                text("teavm-react provides type-safe event handler interfaces that "),
                text("mirror React's synthetic event system. Each handler interface "),
                text("wraps the corresponding browser event with convenient accessors.")
            )),

            // ── Handler Interfaces Overview ──
            h2("Event Handler Interfaces"),
            El.table("api-table",

                thead(
                    tr(
                        th("Interface"),
                        th("Used With"),
                        th("ElementBuilder Method"),
                        th("Event Object")
                    )
                ),
                tbody(
                    tr(
                        td(code("EventHandler")),
                        td("click, mousedown, mouseup, mouseover, mouseout"),
                        td(code(".onClick(handler)")),
                        td(code("SyntheticEvent"))
                    ),
                    tr(
                        td(code("ChangeEventHandler")),
                        td("input change"),
                        td(code(".onChange(handler)")),
                        td(code("ChangeEvent"))
                    ),
                    tr(
                        td(code("KeyboardEventHandler")),
                        td("keydown, keyup, keypress"),
                        td(code(".onKeyDown(handler)")),
                        td(code("KeyboardEvent"))
                    ),
                    tr(
                        td(code("FocusEventHandler")),
                        td("focus, blur"),
                        td(code(".onFocus(handler)")),
                        td(code("FocusEvent"))
                    ),
                    tr(
                        td(code("SubmitEventHandler")),
                        td("form submit"),
                        td(code(".onSubmit(handler)")),
                        td(code("SubmitEvent"))
                    )
                )
            ),

            // ── SyntheticEvent Base ──
            h2("SyntheticEvent (Base)"),
            p(fragment(
                text("All event objects extend "),
                code("SyntheticEvent"),
                text(", which provides the following common properties:")
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
                        td(code("getTarget()")),
                        td(code("EventTarget")),
                        td("The DOM element that triggered the event.")
                    ),
                    tr(
                        td(code("preventDefault()")),
                        td(code("void")),
                        td("Prevents the browser's default action for this event.")
                    ),
                    tr(
                        td(code("stopPropagation()")),
                        td(code("void")),
                        td("Stops the event from propagating to parent elements.")
                    ),
                    tr(
                        td(code("getType()")),
                        td(code("String")),
                        td("The event type string (e.g., \"click\", \"keydown\").")
                    )
                )
            ),

            // ── EventHandler (Click / Mouse) ──
            h2("EventHandler (Click Events)"),
            p(fragment(
                text("The "),
                code("EventHandler"),
                text(" interface is used for click and general mouse events. "),
                text("It is a functional interface taking a "),
                code("SyntheticEvent"),
                text(".")
            )),
            CodeBlock.create("""
                @FunctionalInterface
                public interface EventHandler extends JSObject {
                    void handleEvent(SyntheticEvent e);
                }""", "java"),

            h3("Example: Click Handler"),
            CodeTabs.create(
                """
                    var count = Hooks.useState(0);

                    return button("Clicked " + count.getInt() + " times")
                        .onClick(e -> count.updateInt(c -> c + 1))
                        .build();""",
                """
                    val count = Hooks.useState(0)

                    return button("Clicked ${count.int} times")
                        .onClick { count.updateInt { c -> c + 1 } }
                        .build()"""
            ),

            // ── MouseEvent Details ──
            h2("MouseEvent Details"),
            p("When handling click or mouse events, the SyntheticEvent provides mouse-specific information:"),
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
                        td(code("getClientX()")),
                        td(code("int")),
                        td("Horizontal coordinate relative to the viewport.")
                    ),
                    tr(
                        td(code("getClientY()")),
                        td(code("int")),
                        td("Vertical coordinate relative to the viewport.")
                    ),
                    tr(
                        td(code("getPageX()")),
                        td(code("int")),
                        td("Horizontal coordinate relative to the document.")
                    ),
                    tr(
                        td(code("getPageY()")),
                        td(code("int")),
                        td("Vertical coordinate relative to the document.")
                    ),
                    tr(
                        td(code("getButton()")),
                        td(code("int")),
                        td("Which mouse button was pressed (0 = left, 1 = middle, 2 = right).")
                    ),
                    tr(
                        td(code("getCtrlKey()")),
                        td(code("boolean")),
                        td("Whether the Ctrl key was held during the event.")
                    ),
                    tr(
                        td(code("getShiftKey()")),
                        td(code("boolean")),
                        td("Whether the Shift key was held during the event.")
                    ),
                    tr(
                        td(code("getAltKey()")),
                        td(code("boolean")),
                        td("Whether the Alt key was held during the event.")
                    ),
                    tr(
                        td(code("getMetaKey()")),
                        td(code("boolean")),
                        td("Whether the Meta (Cmd/Win) key was held during the event.")
                    )
                )
            ),

            h3("Example: Mouse Position"),
            CodeBlock.create("""
                var x = Hooks.useState(0);
                var y = Hooks.useState(0);

                return div(
                    p("Mouse: " + x.getInt() + ", " + y.getInt())
                ).className("mouse-tracker")
                    .onClick(e -> {
                        x.setInt(e.getClientX());
                        y.setInt(e.getClientY());
                    }).build();""", "java"),

            // ── ChangeEvent ──
            h2("ChangeEvent Details"),
            p(fragment(
                text("The "),
                code("ChangeEventHandler"),
                text(" is used with "),
                code(".onChange()"),
                text(" on input elements. The event target provides access to "),
                text("the input's current value.")
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
                        td(code("e.getTarget().getValue()")),
                        td(code("String")),
                        td("Current text value of the input, select, or textarea.")
                    ),
                    tr(
                        td(code("e.getTarget().getChecked()")),
                        td(code("boolean")),
                        td("Whether a checkbox or radio input is checked.")
                    )
                )
            ),

            h3("Example: Text Input"),
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
                        p("Hello, " + name.getString())
                    );""",
                """
                    val name = Hooks.useState("")

                    return div(
                        input("text")
                            .value(name.string)
                            .onChange { name.string = it.target.value }
                            .placeholder("Enter your name")
                            .build(),
                        p("Hello, ${name.string}")
                    )"""
            ),

            h3("Example: Checkbox"),
            CodeBlock.create("""
                var agreed = Hooks.useState(false);

                return div(
                    input("checkbox")
                        .checked(agreed.getBool())
                        .onChange(e -> agreed.setBool(
                            e.getTarget().getChecked()))
                        .build(),
                    text(" I agree to the terms"),
                    p(agreed.getBool()
                        ? "Thank you for agreeing!"
                        : "Please check the box above.")
                );""", "java"),

            // ── KeyboardEvent ──
            h2("KeyboardEvent Details"),
            p(fragment(
                text("The "),
                code("KeyboardEventHandler"),
                text(" is used with "),
                code(".onKeyDown()"),
                text(" and "),
                code(".onKeyUp()"),
                text(". It provides information about which key was pressed.")
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
                        td(code("getKey()")),
                        td(code("String")),
                        td("The key value (e.g., \"Enter\", \"a\", \"ArrowUp\").")
                    ),
                    tr(
                        td(code("getCode()")),
                        td(code("String")),
                        td("The physical key code (e.g., \"KeyA\", \"Enter\", \"ArrowUp\").")
                    ),
                    tr(
                        td(code("getCtrlKey()")),
                        td(code("boolean")),
                        td("Whether Ctrl was held.")
                    ),
                    tr(
                        td(code("getShiftKey()")),
                        td(code("boolean")),
                        td("Whether Shift was held.")
                    ),
                    tr(
                        td(code("getAltKey()")),
                        td(code("boolean")),
                        td("Whether Alt was held.")
                    ),
                    tr(
                        td(code("getMetaKey()")),
                        td(code("boolean")),
                        td("Whether Meta (Cmd/Win) was held.")
                    ),
                    tr(
                        td(code("getRepeat()")),
                        td(code("boolean")),
                        td("Whether the key is being held down (auto-repeat).")
                    )
                )
            ),

            h3("Example: Key Detection"),
            CodeTabs.create(
                """
                    var lastKey = Hooks.useState("(none)");

                    return div(
                        input("text")
                            .placeholder("Press a key...")
                            .onKeyDown(e -> {
                                String info = e.getKey();
                                if (e.getCtrlKey()) info = "Ctrl+" + info;
                                if (e.getShiftKey()) info = "Shift+" + info;
                                if (e.getAltKey()) info = "Alt+" + info;
                                lastKey.setString(info);
                            })
                            .build(),
                        p("Last key: " + lastKey.getString())
                    );""",
                """
                    val lastKey = Hooks.useState("(none)")

                    return div(
                        input("text")
                            .placeholder("Press a key...")
                            .onKeyDown { e ->
                                var info = e.key
                                if (e.ctrlKey) info = "Ctrl+$info"
                                if (e.shiftKey) info = "Shift+$info"
                                if (e.altKey) info = "Alt+$info"
                                lastKey.string = info
                            }
                            .build(),
                        p("Last key: ${lastKey.string}")
                    )"""
            ),

            h3("Example: Enter Key to Submit"),
            CodeBlock.create("""
                var text = Hooks.useState("");
                var submitted = Hooks.useState("");

                return div(
                    input("text")
                        .value(text.getString())
                        .onChange(e -> text.setString(
                            e.getTarget().getValue()))
                        .onKeyDown(e -> {
                            if (e.getKey().equals("Enter")) {
                                submitted.setString(text.getString());
                                text.setString("");
                            }
                        })
                        .placeholder("Type and press Enter")
                        .build(),
                    p("Submitted: " + submitted.getString())
                );""", "java"),

            // ── FocusEvent ──
            h2("FocusEvent Details"),
            p(fragment(
                text("The "),
                code("FocusEventHandler"),
                text(" is used with "),
                code(".onFocus()"),
                text(" and "),
                code(".onBlur()"),
                text(" to detect when an element gains or loses focus.")
            )),

            h3("Example: Focus Tracking"),
            CodeBlock.create("""
                var focused = Hooks.useState(false);

                return input("text")
                    .className(focused.getBool()
                        ? "input-focused" : "input-normal")
                    .onFocus(e -> focused.setBool(true))
                    .onBlur(e -> focused.setBool(false))
                    .placeholder(focused.getBool()
                        ? "Typing..." : "Click to focus")
                    .build();""", "java"),

            // ── SubmitEvent ──
            h2("SubmitEvent Details"),
            p(fragment(
                text("The "),
                code("SubmitEventHandler"),
                text(" is used with "),
                code(".onSubmit()"),
                text(" on form elements. Always call "),
                code("e.preventDefault()"),
                text(" to prevent the default form submission:")
            )),
            CodeBlock.create("""
                var name = Hooks.useState("");

                return form(
                    input("text")
                        .value(name.getString())
                        .onChange(e -> name.setString(
                            e.getTarget().getValue()))
                        .build(),
                    button("Submit").build()
                ).onSubmit(e -> {
                    e.preventDefault();
                    System.out.println("Submitted: " + name.getString());
                }).build();""", "java"),

            // ── Preventing Defaults ──
            h2("Preventing Default Behavior"),
            p("Use preventDefault() and stopPropagation() to control event behavior:"),
            CodeBlock.create("""
                // Prevent link navigation
                a("Click me").href("#")
                    .onClick(e -> {
                        e.preventDefault();
                        // Handle click without navigation
                    })
                    .build();

                // Prevent event bubbling
                div(
                    button("Inner")
                        .onClick(e -> {
                            e.stopPropagation();
                            // Only this handler runs, not the div's
                        })
                        .build()
                ).onClick(e -> {
                    // This will NOT fire if inner button is clicked
                }).build();""", "java"),

            // Common patterns
            Callout.note("Event Handler Patterns",
                p(fragment(
                    text("Event handlers in teavm-react are Java lambda expressions. "),
                    text("They have access to component state via closures, so you can "),
                    text("read and update state directly within the handler. For expensive "),
                    text("operations, consider using "),
                    code("Hooks.useCallback()"),
                    text(" to memoize the handler.")
                ))
            ),

            // Live demo
            h2("Live Demo: Event Playground"),
            p("Try clicking, typing, and pressing keys to see events in action:"),
            LiveDemo.create(EventsReferencePage::eventPlaygroundDemo),

            // See also
            h2("See Also"),
            ul(
                li(a("HTML DSL -- Element Attributes and Handlers").href("#/reference/html-dsl").build()),
                li(a("useState -- Managing State from Event Handlers").href("#/reference/use-state").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }

    private static ReactElement eventPlaygroundDemo(JSObject props) {
        var clicks = Hooks.useState(0);
        var lastKey = Hooks.useState("(none)");
        var inputValue = Hooks.useState("");
        var isFocused = Hooks.useState(false);

        return El.div("demo-events",

            h3("Event Playground"),

            El.div("demo-section",

                strong("Click counter: "),
                text(String.valueOf(clicks.getInt())),
                text(" "),
                button("Click me")
                    .onClick(e -> clicks.updateInt(c -> c + 1))
                    .className("demo-btn")
                    .build(),
                button("Reset")
                    .onClick(e -> clicks.setInt(0))
                    .className("demo-btn")
                    .build()
            ),

            El.div("demo-section",

                strong("Keyboard: "),
                input("text")
                    .value(inputValue.getString())
                    .onChange(e -> inputValue.setString(
                        e.getTarget().getValue()))
                    .onKeyDown(e -> {
                        String info = e.getKey();
                        if (e.getCtrlKey()) info = "Ctrl+" + info;
                        if (e.getShiftKey()) info = "Shift+" + info;
                        lastKey.setString(info);
                    })
                    .onFocus(e -> isFocused.setBool(true))
                    .onBlur(e -> isFocused.setBool(false))
                    .className(isFocused.getBool()
                        ? "demo-input focused" : "demo-input")
                    .placeholder("Type here...")
                    .build(),
                p("Last key: " + lastKey.getString()),
                p("Input value: " + inputValue.getString()),
                p("Focused: " + isFocused.getBool())
            )
        );
    }
}

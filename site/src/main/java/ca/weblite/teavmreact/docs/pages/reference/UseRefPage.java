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
 * Full API reference page for the useRef hook.
 */
public class UseRefPage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("useRef"),
            p(fragment(
                code("useRef"),
                text(" creates a mutable reference that persists across renders. "),
                text("Unlike state, changing a ref does "),
                em("not"),
                text(" trigger a re-render. Refs are useful for storing values you "),
                text("need to read later without causing updates, or for holding "),
                text("references to DOM elements.")
            )),

            // Method signatures
            h2("Method Signatures"),
            CodeBlock.create("""
                // Generic ref (for DOM elements or JSObjects)
                RefHandle ref = Hooks.useRef(JSObject initialValue);

                // Integer ref
                RefHandle ref = Hooks.useRefInt(int initialValue);

                // String ref
                RefHandle ref = Hooks.useRefString(String initialValue);""", "java"),

            // RefHandle methods table
            h2("RefHandle Methods"),
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
                        td(code("getCurrentInt()")),
                        td(code("int")),
                        td("Returns the current integer value of the ref.")
                    ),
                    tr(
                        td(code("setCurrentInt(int value)")),
                        td(code("void")),
                        td("Sets the ref to a new integer value without re-rendering.")
                    ),
                    tr(
                        td(code("getCurrentString()")),
                        td(code("String")),
                        td("Returns the current string value of the ref.")
                    ),
                    tr(
                        td(code("setCurrentString(String value)")),
                        td(code("void")),
                        td("Sets the ref to a new string value without re-rendering.")
                    ),
                    tr(
                        td(code("rawRef()")),
                        td(code("JSObject")),
                        td("Returns the underlying React ref object. Pass this to an element's ref prop to get a DOM reference.")
                    )
                )
            ),

            // Usage: storing values
            h2("Storing Mutable Values"),
            p(fragment(
                text("Use refs to store values that should persist across renders "),
                text("but should not trigger re-renders when changed. Common examples "),
                text("include timer IDs, previous values, and render counters.")
            )),
            CodeTabs.create(
                """
                    var renderCount = Hooks.useRefInt(0);

                    // Increment on every render (no re-render triggered)
                    renderCount.setCurrentInt(
                        renderCount.getCurrentInt() + 1);

                    return p("This component rendered "
                        + renderCount.getCurrentInt() + " times");""",
                """
                    val renderCount = Hooks.useRefInt(0)

                    renderCount.setCurrentInt(
                        renderCount.currentInt + 1)

                    return p("This component rendered " +
                        "${renderCount.currentInt} times")"""
            ),

            // Usage: DOM references
            h2("Referencing DOM Elements"),
            p(fragment(
                text("Pass "),
                code("ref.rawRef()"),
                text(" to an element's "),
                code("ref"),
                text(" prop to get a reference to the underlying DOM node. "),
                text("You can then read or manipulate the element in effects.")
            )),
            CodeTabs.create(
                """
                    var inputRef = Hooks.useRef(null);

                    return div(
                        input("text")
                            .prop("ref", inputRef.rawRef())
                            .placeholder("Type here...")
                            .build(),
                        button("Focus Input")
                            .onClick(e -> {
                                // Access the DOM element
                                JSObject el = inputRef.rawRef();
                                focusElement(el);
                            })
                            .build()
                    );""",
                """
                    val inputRef = Hooks.useRef(null)

                    return div(
                        input("text")
                            .prop("ref", inputRef.rawRef())
                            .placeholder("Type here...")
                            .build(),
                        button("Focus Input")
                            .onClick {
                                val el = inputRef.rawRef()
                                focusElement(el)
                            }
                            .build()
                    )"""
            ),

            // Timer ID example
            h2("Storing Timer IDs"),
            p("Refs are ideal for storing interval or timeout IDs that you need to clear later:"),
            CodeBlock.create("""
                var intervalRef = Hooks.useRefInt(0);
                var count = Hooks.useState(0);

                Hooks.useEffectOnMount(() -> {
                    intervalRef.setCurrentInt(
                        setInterval(() -> {
                            count.updateInt(c -> c + 1);
                        }, 1000)
                    );
                    return () -> clearInterval(
                        intervalRef.getCurrentInt());
                });""", "java"),

            // Tracking previous value
            h2("Tracking Previous Values"),
            p("A common pattern is using a ref to remember the previous value of a piece of state:"),
            CodeBlock.create("""
                var count = Hooks.useState(0);
                var prevCount = Hooks.useRefInt(0);

                Hooks.useEffect(() -> {
                    prevCount.setCurrentInt(count.getInt());
                    return null;
                });

                return p("Now: " + count.getInt()
                    + ", Before: " + prevCount.getCurrentInt());""", "java"),

            // Pitfall
            Callout.pitfall("Do Not Read or Write Refs During Rendering",
                p(fragment(
                    text("Avoid reading or writing ref values during the render phase "),
                    text("(outside of effects and event handlers), except for lazy "),
                    text("initialization. Changing a ref during render can lead to "),
                    text("unpredictable behavior because React may re-render your "),
                    text("component multiple times.")
                ))
            ),

            Callout.note("Ref vs State",
                p(fragment(
                    text("Use "),
                    code("useState"),
                    text(" when the UI should update in response to value changes. "),
                    text("Use "),
                    code("useRef"),
                    text(" when you need to persist a value without triggering re-renders.")
                ))
            ),

            // Live demo
            h2("Live Demo: Render Counter"),
            p("This demo tracks how many times the component has rendered using a ref, and lets you force re-renders by updating state:"),
            LiveDemo.create(UseRefPage::renderCounterDemo),

            // See also
            h2("See Also"),
            ul(
                li(a("useState -- State That Triggers Re-renders").href("#/reference/use-state").build()),
                li(a("useEffect -- Running Side Effects").href("#/reference/use-effect").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }

    private static ReactElement renderCounterDemo(JSObject props) {
        var renderCount = Hooks.useRefInt(0);
        var forceUpdate = Hooks.useState(0);

        renderCount.setCurrentInt(renderCount.getCurrentInt() + 1);

        return El.div("demo-ref",

            p("Render count: " + renderCount.getCurrentInt()),
            p("State value: " + forceUpdate.getInt()),
            button("Force Re-render")
                .onClick(e -> forceUpdate.updateInt(v -> v + 1))
                .className("demo-btn")
                .build()
        );
    }
}

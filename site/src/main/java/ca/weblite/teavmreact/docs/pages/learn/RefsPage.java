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
 * Documentation page: Refs.
 */
public class RefsPage {

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("Refs"),
            p("When you want a component to \"remember\" some information, but " +
              "you don't want that information to trigger new renders, you can " +
              "use a ref. Refs are like a secret pocket on your component that " +
              "React doesn't watch."),

            // Section 1: What refs are
            h2("What Refs Are"),
            p("A ref is a mutable value that persists across renders, similar to " +
              "state. The key difference is that changing a ref does NOT trigger " +
              "a re-render. This makes refs useful for:"),
            ul(
                li("Storing values that you need to read later but that don't affect the UI."),
                li("Keeping track of interval/timeout IDs for cleanup."),
                li("Counting how many times a component has rendered."),
                li("Storing a reference to a DOM element.")
            ),

            // Section 2: useRef API
            h2("useRef(), useRefInt(), useRefString()"),
            p(
                text("teavm-react provides typed ref hooks. Each returns a "),
                code("RefHandle"),
                text(" with typed getters and setters.")
            ),

            CodeTabs.create(
                """
                import ca.weblite.teavmreact.hooks.Hooks;
                import ca.weblite.teavmreact.hooks.RefHandle;

                public static ReactElement render(JSObject props) {
                    // Ref for a JSObject (e.g., a DOM element)
                    RefHandle domRef = Hooks.useRef(null);

                    // Ref for an int value
                    RefHandle countRef = Hooks.useRefInt(0);

                    // Ref for a String value
                    RefHandle nameRef = Hooks.useRefString("initial");

                    return div(
                        p("Ref values don't trigger re-renders!")
                    );
                }""",
                """
                val RefDemo = component("RefDemo") {
                    // Kotlin delegates for refs
                    val domRef = useRef(null)
                    var count by useRefInt(0)
                    var name by useRefString("initial")

                    div {
                        p("Ref values don't trigger re-renders!")
                    }
                }"""
            ),

            // Section 3: RefHandle API
            h2("The RefHandle API"),
            p(
                text("The "),
                code("RefHandle"),
                text(" object wraps React's ref and provides typed access:")
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
                        td(code("getCurrentInt()")),
                        td("Returns the current ref value as an int.")
                    ),
                    tr(
                        td(code("setCurrentInt(int value)")),
                        td("Sets the ref value to an int (no re-render).")
                    ),
                    tr(
                        td(code("getCurrentString()")),
                        td("Returns the current ref value as a String.")
                    ),
                    tr(
                        td(code("setCurrentString(String value)")),
                        td("Sets the ref value to a String.")
                    ),
                    tr(
                        td(code("getCurrentBool()")),
                        td("Returns the current ref value as a boolean.")
                    ),
                    tr(
                        td(code("setCurrentBool(boolean value)")),
                        td("Sets the ref value to a boolean.")
                    ),
                    tr(
                        td(code("getCurrentDouble()")),
                        td("Returns the current ref value as a double.")
                    ),
                    tr(
                        td(code("setCurrentDouble(double value)")),
                        td("Sets the ref value to a double.")
                    ),
                    tr(
                        td(code("rawRef()")),
                        td("Returns the underlying JS ref object (for passing to DOM elements).")
                    )
                )
            ),

            Callout.note("Refs vs State",
                p(
                    text("State ("),
                    code("useState"),
                    text("): changing it triggers a re-render. Use for values that affect the UI."),
                    br(),
                    text("Refs ("),
                    code("useRef"),
                    text("): changing it does NOT trigger a re-render. Use for values that " +
                         "are read in event handlers or effects but don't directly affect rendering.")
                )
            ),

            // Section 4: Counting renders example
            h2("Example: Counting Renders"),
            p("A common use case for refs is counting how many times a component " +
              "has rendered. Since changing a ref doesn't cause a re-render, you " +
              "can increment it in a useEffect without creating an infinite loop."),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var name = Hooks.useState("");
                    var renderCount = Hooks.useRefInt(0);

                    // Increment render count on every render
                    Hooks.useEffect(() -> {
                        renderCount.setCurrentInt(
                            renderCount.getCurrentInt() + 1);
                        return null; // no cleanup needed
                    });

                    return div(
                        input("text")
                            .placeholder("Type to trigger re-renders")
                            .value(name.getString())
                            .onChange(e -> name.setString(
                                e.getTarget().getValue()))
                            .build(),
                        p("Name: " + name.getString()),
                        p("This component has rendered "
                            + renderCount.getCurrentInt()
                            + " times")
                    );
                }""",
                """
                val RenderCounter = component("RenderCounter") {
                    var name by useState("")
                    var renderCount by useRefInt(0)

                    useEffect {
                        renderCount++
                    }

                    div {
                        input("text") {
                            placeholder = "Type to trigger re-renders"
                            value = name
                            onChange { name = it.target.value }
                        }
                        p("Name: ${'$'}name")
                        p("This component has rendered ${'$'}renderCount times")
                    }
                }"""
            ),

            Callout.pitfall("Don't read/write refs during rendering",
                p("Avoid reading or writing refs during the render phase " +
                  "(outside of effects and event handlers). If you read a ref " +
                  "during render, the displayed value may be stale since changing " +
                  "a ref doesn't cause React to re-render. Read refs in event handlers " +
                  "or effects instead.")
            ),

            // Section 5: Live demo
            h2("Live Demo: Render Counter"),
            p("Type in the input below. The render count increments each time " +
              "the component re-renders, but updating the ref itself does not " +
              "cause additional renders."),

            LiveDemo.create(RefsPage::renderCounterDemo),

            CodeBlock.create(
                """
                private static ReactElement renderCounterDemo(JSObject props) {
                    var name = Hooks.useState("");
                    var renderCount = Hooks.useRefInt(0);

                    Hooks.useEffect(() -> {
                        renderCount.setCurrentInt(
                            renderCount.getCurrentInt() + 1);
                        return null;
                    });

                    return div(
                        input("text")
                            .placeholder("Type something...")
                            .value(name.getString())
                            .onChange(e -> name.setString(
                                e.getTarget().getValue()))
                            .build(),
                        p("Value: " + name.getString()),
                        p("Render count: "
                            + renderCount.getCurrentInt())
                    );
                }""",
                "java"
            ),

            // Recap
            h2("Recap"),
            ul(
                li("Refs are mutable values that persist across renders without triggering re-renders."),
                li(
                    text("Use "),
                    code("Hooks.useRef(null)"),
                    text(", "),
                    code("Hooks.useRefInt(0)"),
                    text(", or "),
                    code("Hooks.useRefString(\"\")"),
                    text(" to create refs.")
                ),
                li(
                    text("The "),
                    code("RefHandle"),
                    text(" provides typed access: "),
                    code("getCurrentInt()"),
                    text(" / "),
                    code("setCurrentInt()"),
                    text(", etc.")
                ),
                li("Use refs for timer IDs, render counts, and DOM element references."),
                li("Don't read or write refs during the render phase -- use effects or event handlers.")
            )
        );
    }

    private static ReactElement renderCounterDemo(JSObject props) {
        var name = Hooks.useState("");
        var renderCount = Hooks.useRefInt(0);

        Hooks.useEffect(() -> {
            renderCount.setCurrentInt(renderCount.getCurrentInt() + 1);
            return null;
        });

        return div(
            input("text")
                .placeholder("Type something...")
                .value(name.getString())
                .className("demo-input")
                .onChange(e -> name.setString(e.getTarget().getValue()))
                .build(),
            El.div("demo-output",

                p(
                    strong("Value: "),
                    text(name.getString())
                ),
                p(
                    strong("Render count: "),
                    text(String.valueOf(renderCount.getCurrentInt()))
                )
            )
        );
    }
}

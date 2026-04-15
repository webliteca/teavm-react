package ca.weblite.teavmreact.docs.pages.reference;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.html.DomBuilder.*;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Overview page listing all available hooks with brief descriptions
 * and links to their detail pages.
 */
public class HooksOverviewPage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("Hooks API Reference"),
            p(fragment(
                text("Hooks let you use React features from functional components in teavm-react. "),
                text("All hooks are accessed via the "),
                code("Hooks"),
                text(" class and must be called at the top level of a render function.")
            )),

            Callout.note("Rules of Hooks",
                p("Always call hooks at the top level of your render function, never inside loops, conditions, or nested functions. This ensures hooks are called in the same order on every render.")
            ),

            // Summary table
            h2("Available Hooks"),
            Table.create().className("api-table")
                .child(thead(
                    tr(
                        th("Hook"),
                        th("Purpose"),
                        th("Example")
                    )
                ))
                .child(tbody(
                    // useState
                    tr(
                        td(a("useState").href("#/reference/use-state").build()),
                        td("Adds local state to a component. Supports int, String, boolean, and double values."),
                        td(code("Hooks.useState(0)"))
                    ),
                    // useEffect
                    tr(
                        td(a("useEffect").href("#/reference/use-effect").build()),
                        td("Runs side effects after render. Supports cleanup functions and dependency arrays."),
                        td(code("Hooks.useEffect(() -> { ... return null; })"))
                    ),
                    // useEffectOnMount
                    tr(
                        td(a("useEffectOnMount").href("#/reference/use-effect").build()),
                        td("Runs a side effect only once when the component mounts."),
                        td(code("Hooks.useEffectOnMount(() -> { ... return null; })"))
                    ),
                    // useRef
                    tr(
                        td(a("useRef").href("#/reference/use-ref").build()),
                        td("Creates a mutable ref that persists across renders without triggering re-renders."),
                        td(code("Hooks.useRefInt(0)"))
                    ),
                    // useContext
                    tr(
                        td(a("useContext").href("#/reference/use-context").build()),
                        td("Reads a value from a ReactContext. Prefer the typed helpers on ReactContext itself."),
                        td(code("Hooks.useContext(ctx)"))
                    ),
                    // useMemo
                    tr(
                        td("useMemo"),
                        td("Memoizes an expensive computation so it only recalculates when dependencies change."),
                        td(code("Hooks.useMemo(factory, deps)"))
                    ),
                    // useCallback
                    tr(
                        td("useCallback"),
                        td("Memoizes a callback reference to avoid unnecessary child re-renders."),
                        td(code("Hooks.useCallback(cb, deps)"))
                    ),
                    // useReducer
                    tr(
                        td("useReducer"),
                        td("Manages complex state with a reducer function. Returns [state, dispatch]."),
                        td(code("Hooks.useReducer(reducer, initial)"))
                    )
                ))
                .build(),

            // deps helper
            h2("Dependency Arrays"),
            p(fragment(
                text("Several hooks accept a dependency array to control when they re-run. Use the "),
                code("Hooks.deps()"),
                text(" helper to create these arrays.")
            )),
            CodeBlock.create("""
                // Empty deps -- run only on mount
                Hooks.useEffect(effect, Hooks.deps());

                // With dependencies -- re-run when values change
                Hooks.useEffect(effect, Hooks.deps(someValue, anotherValue));""", "java"),

            // Quick start
            h2("Quick Example"),
            p("Here is a minimal component using useState and useEffect together:"),
            CodeBlock.create("""
                public class Timer {
                    public static ReactElement render(JSObject props) {
                        var seconds = Hooks.useState(0);

                        Hooks.useEffectOnMount(() -> {
                            int id = setInterval(() -> {
                                seconds.updateInt(s -> s + 1);
                            }, 1000);
                            return () -> clearInterval(id);
                        });

                        return div(
                            h2("Elapsed: " + seconds.getInt() + "s")
                        );
                    }
                }""", "java"),

            // Navigation links
            h2("Detailed References"),
            ul(
                li(a("useState -- State Management").href("#/reference/use-state").build()),
                li(a("useEffect -- Side Effects").href("#/reference/use-effect").build()),
                li(a("useRef -- Mutable References").href("#/reference/use-ref").build()),
                li(a("useContext -- Context Consumption").href("#/reference/use-context").build()),
                li(a("HTML DSL -- Building Element Trees").href("#/reference/html-dsl").build()),
                li(a("Components -- Defining Components").href("#/reference/components").build()),
                li(a("Events -- Event Handling").href("#/reference/events").build())
            )
        );
    }
}

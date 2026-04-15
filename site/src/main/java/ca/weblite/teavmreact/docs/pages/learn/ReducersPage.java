package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Documentation page: Reducers.
 */
public class ReducersPage {

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("Reducers"),
            p("As your components grow more complex, managing multiple pieces " +
              "of related state with several useState calls can become unwieldy. " +
              "A reducer lets you consolidate all state update logic into a single " +
              "function, making state transitions explicit and easier to reason about."),

            // Section 1: When to use useReducer
            h2("When to Use useReducer vs useState"),
            p("Use useState when:"),
            ul(
                li("State is simple (a single number, string, or boolean)."),
                li("State transitions are straightforward (set to a new value)."),
                li("There are only one or two state variables.")
            ),
            p("Use useReducer when:"),
            ul(
                li("You have multiple related state values that change together."),
                li("State transitions depend on the previous state in complex ways."),
                li("You want to centralize update logic in one place."),
                li("Actions are semantic (INCREMENT, RESET) rather than raw value assignments.")
            ),

            Callout.note("Reducers are familiar",
                p("If you have used Redux or Java's switch-based command patterns, " +
                  "reducers will feel natural. A reducer is just a pure function: " +
                  "(state, action) -> newState.")
            ),

            // Section 2: Writing a reducer with @JSBody
            h2("Writing a Reducer Function"),
            p(
                text("In teavm-react, the reducer is a JavaScript function defined with "),
                code("@JSBody"),
                text(". It receives the current state and an action object, and returns " +
                     "the new state. The action's "),
                code("type"),
                text(" field determines which transition to apply.")
            ),

            CodeTabs.create(
                """
                import org.teavm.jso.JSBody;
                import org.teavm.jso.JSObject;

                public class CounterReducer {

                    // Define the reducer function in JS
                    @JSBody(script = \"\"\"
                        return function(state, action) {
                            switch (action.type) {
                                case 'increment':
                                    return { count: state.count + 1 };
                                case 'decrement':
                                    return { count: state.count - 1 };
                                case 'reset':
                                    return { count: 0 };
                                default:
                                    return state;
                            }
                        };
                    \"\"\")
                    private static native JSObject reducer();

                    // Helper to create the initial state
                    @JSBody(params = {"count"}, script =
                        "return { count: count };")
                    private static native JSObject initialState(int count);

                    // Helper to create action objects
                    @JSBody(params = {"type"}, script =
                        "return { type: type };")
                    private static native JSObject action(String type);
                }""",
                """
                // In Kotlin, you can define reducers inline:
                val counterReducer = jsReducer { state, action ->
                    when (action.string("type")) {
                        "increment" -> jsObject { "count" to state.int("count") + 1 }
                        "decrement" -> jsObject { "count" to state.int("count") - 1 }
                        "reset" -> jsObject { "count" to 0 }
                        else -> state
                    }
                }"""
            ),

            // Section 3: Dispatching actions
            h2("Dispatching Actions"),
            p(
                text("Call "),
                code("Hooks.useReducer(reducer, initialState)"),
                text(" to get back a two-element array: the current state and a " +
                     "dispatch function. Call the dispatch function with an action " +
                     "object to trigger a state transition.")
            ),

            CodeTabs.create(
                """
                import ca.weblite.teavmreact.hooks.Hooks;
                import ca.weblite.teavmreact.core.React;

                public static ReactElement render(JSObject props) {
                    // useReducer returns [state, dispatch]
                    JSObject[] result = Hooks.useReducer(
                        reducer(),
                        initialState(0)
                    );
                    JSObject state = result[0];
                    JSObject dispatch = result[1];

                    // Read state
                    int count = React.jsToInt(
                        React.getProperty(state, "count")
                    );

                    return div(
                        p("Count: " + count),
                        button("+1")
                            .onClick(e -> dispatchAction(
                                dispatch, action("increment")))
                            .build(),
                        button("-1")
                            .onClick(e -> dispatchAction(
                                dispatch, action("decrement")))
                            .build(),
                        button("Reset")
                            .onClick(e -> dispatchAction(
                                dispatch, action("reset")))
                            .build()
                    );
                }

                @JSBody(params = {"dispatch", "action"},
                    script = "dispatch(action);")
                private static native void dispatchAction(
                    JSObject dispatch, JSObject action);""",
                """
                val CounterApp = component("CounterApp") {
                    val (state, dispatch) = useReducer(
                        counterReducer,
                        jsObject { "count" to 0 }
                    )

                    val count = state.int("count")

                    div {
                        p("Count: ${'$'}count")
                        button("+1") {
                            onClick { dispatch(jsObject { "type" to "increment" }) }
                        }
                        button("-1") {
                            onClick { dispatch(jsObject { "type" to "decrement" }) }
                        }
                        button("Reset") {
                            onClick { dispatch(jsObject { "type" to "reset" }) }
                        }
                    }
                }"""
            ),

            Callout.deepDive("Why define reducers with @JSBody?",
                p("TeaVM compiles Java to JavaScript, but the useReducer hook " +
                  "expects a plain JS function. Using @JSBody lets you write the " +
                  "reducer directly in JavaScript, which React can call without " +
                  "any TeaVM wrapper overhead. The reducer runs in pure JS space, " +
                  "making it fast and compatible with React's internal batching.")
            ),

            // Section 4: Full counter example
            h2("Complete Example: Counter with Actions"),
            p("Here is the full counter component using useReducer with " +
              "increment, decrement, and reset actions."),

            LiveDemo.create(ReducersPage::counterReducerDemo),

            CodeBlock.create(
                """
                public class ReducerCounterDemo {

                    @JSBody(script = \"\"\"
                        return function(state, action) {
                            switch (action.type) {
                                case 'increment':
                                    return { count: state.count + 1 };
                                case 'decrement':
                                    return { count: state.count - 1 };
                                case 'reset':
                                    return { count: 0 };
                                default:
                                    return state;
                            }
                        };
                    \"\"\")
                    private static native JSObject reducer();

                    @JSBody(params = {"n"}, script = "return { count: n };")
                    private static native JSObject initState(int n);

                    @JSBody(params = {"t"}, script = "return { type: t };")
                    private static native JSObject act(String t);

                    @JSBody(params = {"dispatch", "action"},
                        script = "dispatch(action);")
                    private static native void dispatch(
                        JSObject dispatch, JSObject action);

                    @JSBody(params = {"state", "key"},
                        script = "return state[key]|0;")
                    private static native int readInt(
                        JSObject state, String key);

                    public static ReactElement render(JSObject props) {
                        JSObject[] r = Hooks.useReducer(
                            reducer(), initState(0));
                        int count = readInt(r[0], "count");

                        return div(
                            h2("Reducer Counter"),
                            p("Count: " + count),
                            button("+1").onClick(e ->
                                dispatch(r[1], act("increment"))).build(),
                            button("-1").onClick(e ->
                                dispatch(r[1], act("decrement"))).build(),
                            button("Reset").onClick(e ->
                                dispatch(r[1], act("reset"))).build()
                        );
                    }
                }""",
                "java"
            ),

            Callout.pitfall("Reducers must be pure",
                p("A reducer function must not have side effects. It should not " +
                  "modify the existing state object, make API calls, or interact " +
                  "with the DOM. Always return a new state object. Side effects " +
                  "belong in event handlers or useEffect.")
            ),

            // Recap
            h2("Recap"),
            ul(
                li("Use useReducer when state transitions are complex or related."),
                li("Define the reducer function with @JSBody as a pure JS function."),
                li(
                    text("Call "),
                    code("Hooks.useReducer(reducer, initialState)"),
                    text(" to get [state, dispatch].")
                ),
                li("Dispatch action objects with a type field to trigger transitions."),
                li("Reducers must be pure: no side effects, always return new state.")
            )
        );
    }

    // --- Live demo reducer implementation ---

    @JSBody(script =
        "return function(state, action) {" +
        "  switch (action.type) {" +
        "    case 'increment': return { count: state.count + 1 };" +
        "    case 'decrement': return { count: state.count - 1 };" +
        "    case 'reset': return { count: 0 };" +
        "    default: return state;" +
        "  }" +
        "};")
    private static native JSObject demoReducer();

    @JSBody(params = {"n"}, script = "return { count: n };")
    private static native JSObject demoInitState(int n);

    @JSBody(params = {"t"}, script = "return { type: t };")
    private static native JSObject demoAction(String t);

    @JSBody(params = {"dispatch", "action"}, script = "dispatch(action);")
    private static native void demoDispatch(JSObject dispatch, JSObject action);

    @JSBody(params = {"state", "key"}, script = "return state[key]|0;")
    private static native int demoReadInt(JSObject state, String key);

    private static ReactElement counterReducerDemo(JSObject props) {
        JSObject[] result = Hooks.useReducer(demoReducer(), demoInitState(0));
        int count = demoReadInt(result[0], "count");

        return div(
            p(
                text("Count: "),
                strong(String.valueOf(count))
            ),
            El.div("demo-row",

                button("+1")
                    .className("demo-btn")
                    .onClick(e -> demoDispatch(result[1], demoAction("increment")))
                    .build(),
                button("-1")
                    .className("demo-btn")
                    .onClick(e -> demoDispatch(result[1], demoAction("decrement")))
                    .build(),
                button("Reset")
                    .className("demo-btn demo-btn-secondary")
                    .onClick(e -> demoDispatch(result[1], demoAction("reset")))
                    .build()
            )
        );
    }
}

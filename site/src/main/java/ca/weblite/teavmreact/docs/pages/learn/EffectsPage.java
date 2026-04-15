package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.JsUtil;
import ca.weblite.teavmreact.core.React;
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
 * Documentation page: Effects.
 */
public class EffectsPage {

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("Effects"),
            p("Some components need to synchronize with external systems. For " +
              "example, you might want to start a timer, fetch data from a " +
              "server, or update the document title after rendering. Effects " +
              "let you run code after React has updated the DOM."),

            // Section 1: What effects are
            h2("What Effects Are"),
            p("Effects are side effects that run after React renders your " +
              "component. Unlike event handlers (which run in response to user " +
              "actions), effects run because rendering itself requires " +
              "synchronization with something outside React."),
            p("Common use cases for effects:"),
            ul(
                li("Setting up timers (setInterval, setTimeout)."),
                li("Subscribing to external data sources."),
                li("Updating the document title."),
                li("Measuring DOM elements after layout."),
                li("Integrating with third-party libraries.")
            ),

            // Section 2: useEffect (every render)
            h2("useEffect() -- Runs After Every Render"),
            p(
                text("The simplest form of "),
                code("Hooks.useEffect()"),
                text(" runs your effect function after every render, including " +
                     "the first one.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var count = Hooks.useState(0);

                    // Runs after every render
                    Hooks.useEffect(() -> {
                        JsUtil.consoleLog(
                            "Component rendered! Count: "
                            + count.getInt());
                        return null; // no cleanup needed
                    });

                    return div(
                        p("Count: " + count.getInt()),
                        button("+1")
                            .onClick(e ->
                                count.updateInt(n -> n + 1))
                            .build()
                    );
                }""",
                """
                val LoggingCounter = component("LoggingCounter") {
                    var count by useState(0)

                    useEffect {
                        console.log("Component rendered! Count: ${'$'}count")
                    }

                    div {
                        p("Count: ${'$'}count")
                        button("+1") { onClick { count++ } }
                    }
                }"""
            ),

            Callout.pitfall("Avoid infinite loops",
                p(
                    text("If your effect updates state with no dependency array, it will "),
                    text("cause an infinite render loop: render -> effect -> setState -> "),
                    text("render -> effect -> ... Always ask yourself: does this effect "),
                    text("really need to run after "),
                    em("every"),
                    text(" render?")
                )
            ),

            // Section 3: useEffectOnMount
            h2("useEffectOnMount() -- Runs Once"),
            p(
                text("Use "),
                code("Hooks.useEffectOnMount()"),
                text(" when you want your effect to run only once, when the " +
                     "component first appears on screen. This is equivalent to " +
                     "useEffect with an empty dependency array ("),
                code("[]"),
                text(") in JavaScript.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var data = Hooks.useState("Loading...");

                    // Runs once on mount
                    Hooks.useEffectOnMount(() -> {
                        // Simulate fetching data
                        JsUtil.setTimeout(() -> {
                            data.setString("Data loaded!");
                        }, 1000);

                        return null; // or return a cleanup function
                    });

                    return p(data.getString());
                }""",
                """
                val DataLoader = component("DataLoader") {
                    var data by useState("Loading...")

                    useEffectOnMount {
                        setTimeout(1000) {
                            data = "Data loaded!"
                        }
                    }

                    p(data)
                }"""
            ),

            // Section 4: useEffect with deps
            h2("useEffect with Dependencies"),
            p(
                text("Pass a dependency array to "),
                code("Hooks.useEffect()"),
                text(" to control when the effect re-runs. The effect will only " +
                     "re-run when one of the values in the dependency array changes.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var roomId = Hooks.useState("general");

                    // Re-runs only when roomId changes
                    Hooks.useEffect(() -> {
                        JsUtil.consoleLog(
                            "Connecting to room: "
                            + roomId.getString());
                        // Simulate connecting to a chat room

                        return () -> {
                            // Cleanup: disconnect when roomId changes
                            JsUtil.consoleLog(
                                "Disconnecting from room: "
                                + roomId.getString());
                        };
                    }, Hooks.deps(
                        React.stringToJS(roomId.getString())
                    ));

                    return div(
                        p("Current room: " + roomId.getString()),
                        button("General")
                            .onClick(e ->
                                roomId.setString("general"))
                            .build(),
                        button("Random")
                            .onClick(e ->
                                roomId.setString("random"))
                            .build()
                    );
                }""",
                """
                val ChatRoom = component("ChatRoom") {
                    var roomId by useState("general")

                    useEffect(roomId) {
                        console.log("Connecting to room: ${'$'}roomId")

                        onCleanup {
                            console.log("Disconnecting from: ${'$'}roomId")
                        }
                    }

                    div {
                        p("Current room: ${'$'}roomId")
                        button("General") { onClick { roomId = "general" } }
                        button("Random") { onClick { roomId = "random" } }
                    }
                }"""
            ),

            // Section 5: Cleanup functions
            h2("Cleanup Functions"),
            p(
                text("Effects can return a cleanup function (a "),
                code("VoidCallback"),
                text("). React calls it before re-running the effect and when the " +
                     "component unmounts. This is essential for preventing memory " +
                     "leaks with timers, subscriptions, and event listeners.")
            ),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    var ticks = Hooks.useState(0);

                    Hooks.useEffectOnMount(() -> {
                        // Start a timer
                        int id = JsUtil.setInterval(() -> {
                            ticks.updateInt(n -> n + 1);
                        }, 1000);

                        // Return cleanup function
                        return () -> {
                            JsUtil.clearInterval(id);
                        };
                    });

                    return p("Ticks: " + ticks.getInt());
                }""",
                """
                val Timer = component("Timer") {
                    var ticks by useState(0)

                    useEffectOnMount {
                        val id = setInterval(1000) {
                            ticks++
                        }

                        onCleanup { clearInterval(id) }
                    }

                    p("Ticks: ${'$'}ticks")
                }"""
            ),

            Callout.note("Return null if no cleanup is needed",
                p(
                    text("The "),
                    code("EffectCallback"),
                    text(" functional interface requires returning a "),
                    code("VoidCallback"),
                    text(". If your effect doesn't need cleanup, return "),
                    code("null"),
                    text(". In Kotlin, the DSL handles this automatically.")
                )
            ),

            // Section 6: deps() helper
            h2("The deps() Helper"),
            p(
                text("Use "),
                code("Hooks.deps()"),
                text(" to build dependency arrays for useEffect. Pass JS values " +
                     "created with "),
                code("React.stringToJS()"),
                text(", "),
                code("React.intToJS()"),
                text(", or "),
                code("React.boolToJS()"),
                text(".")
            ),

            CodeTabs.create(
                """
                // Empty deps = run once on mount
                Hooks.useEffect(effect, Hooks.deps());

                // Single dependency
                Hooks.useEffect(effect, Hooks.deps(
                    React.stringToJS(roomId.getString())
                ));

                // Multiple dependencies
                Hooks.useEffect(effect, Hooks.deps(
                    React.stringToJS(name.getString()),
                    React.intToJS(count.getInt())
                ));""",
                """
                // In Kotlin, deps are handled by the DSL:
                useEffect(roomId) { /* runs when roomId changes */ }
                useEffect(name, count) { /* runs when either changes */ }
                useEffectOnMount { /* runs once */ }"""
            ),

            // Section 7: Live demo
            h2("Live Demo: Stopwatch"),
            p("Click Start to begin the stopwatch, Stop to pause it, and " +
              "Reset to return to zero. The timer uses useEffectOnMount with " +
              "a ref to store the interval ID."),

            LiveDemo.create(EffectsPage::stopwatchDemo),

            CodeBlock.create(
                """
                private static ReactElement stopwatchDemo(JSObject props) {
                    var elapsed = Hooks.useState(0);
                    var running = Hooks.useState(false);
                    var intervalRef = Hooks.useRefInt(0);

                    // Effect that starts/stops the timer based on running
                    Hooks.useEffect(() -> {
                        if (running.getBool()) {
                            int id = JsUtil.setInterval(() -> {
                                elapsed.updateInt(n -> n + 1);
                            }, 100); // 100ms ticks for 0.1s precision
                            intervalRef.setCurrentInt(id);

                            return () -> JsUtil.clearInterval(id);
                        }
                        return null;
                    }, Hooks.deps(
                        React.boolToJS(running.getBool())
                    ));

                    int total = elapsed.getInt();
                    int secs = total / 10;
                    int tenths = total % 10;

                    return div(
                        p(secs + "." + tenths + "s"),
                        button(running.getBool() ? "Stop" : "Start")
                            .onClick(e -> running.setBool(
                                !running.getBool()))
                            .build(),
                        button("Reset")
                            .onClick(e -> {
                                running.setBool(false);
                                elapsed.setInt(0);
                            })
                            .build()
                    );
                }""",
                "java"
            ),

            // Recap
            h2("Recap"),
            ul(
                li(
                    text("Effects run after rendering. Use "),
                    code("Hooks.useEffect()"),
                    text(" for side effects.")
                ),
                li(
                    text("Use "),
                    code("Hooks.useEffectOnMount()"),
                    text(" for effects that should run only once (on mount).")
                ),
                li(
                    text("Pass a dependency array to "),
                    code("Hooks.useEffect(effect, deps)"),
                    text(" to control when it re-runs.")
                ),
                li("Return a cleanup function to clean up timers, subscriptions, etc."),
                li(
                    text("Use "),
                    code("Hooks.deps()"),
                    text(" to build dependency arrays from Java values.")
                ),
                li("Return null from the effect callback when no cleanup is needed.")
            )
        );
    }

    private static ReactElement stopwatchDemo(JSObject props) {
        var elapsed = Hooks.useState(0);
        var running = Hooks.useState(false);
        var intervalRef = Hooks.useRefInt(0);

        Hooks.useEffect(() -> {
            if (running.getBool()) {
                int id = JsUtil.setInterval(() -> {
                    elapsed.updateInt(n -> n + 1);
                }, 100);
                intervalRef.setCurrentInt(id);

                return () -> JsUtil.clearInterval(id);
            }
            return null;
        }, Hooks.deps(React.boolToJS(running.getBool())));

        int total = elapsed.getInt();
        int secs = total / 10;
        int tenths = total % 10;
        String display = secs + "." + tenths + "s";

        return div(
            El.p("demo-timer-display", display),
            El.div("demo-row",

                button(running.getBool() ? "Stop" : "Start")
                    .className("demo-btn")
                    .onClick(e -> running.setBool(!running.getBool()))
                    .build(),
                button("Reset")
                    .className("demo-btn demo-btn-secondary")
                    .onClick(e -> {
                        running.setBool(false);
                        elapsed.setInt(0);
                    })
                    .build()
            )
        );
    }
}

package ca.weblite.teavmreact.docs.pages.reference;

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
 * Full API reference page for the useEffect hook and its variants.
 */
public class UseEffectPage {

    @JSBody(params = {"callback", "ms"}, script = "return setInterval(callback, ms);")
    private static native int setInterval(Runnable callback, int ms);

    @JSBody(params = {"id"}, script = "clearInterval(id);")
    private static native void clearInterval(int id);

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("useEffect"),
            p(fragment(
                code("useEffect"),
                text(" lets you synchronize a component with external systems. "),
                text("It runs a side-effect function after React commits changes to the DOM.")
            )),

            // Three variants
            h2("Variants"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("When It Runs"),
                        th("Use Case")
                    )
                ),
                tbody(
                    tr(
                        td(code("useEffect(callback)")),
                        td("After every render"),
                        td("Syncing with external state on every update")
                    ),
                    tr(
                        td(code("useEffectOnMount(callback)")),
                        td("Once, after initial mount"),
                        td("Subscriptions, timers, fetching data on load")
                    ),
                    tr(
                        td(code("useEffect(callback, deps)")),
                        td("When dependencies change"),
                        td("Reacting to specific state or prop changes")
                    )
                )
            ),

            // Method signatures
            h2("Method Signatures"),
            CodeBlock.create("""
                // Runs after every render
                Hooks.useEffect(EffectCallback callback);

                // Runs only on mount (empty dependency array)
                Hooks.useEffectOnMount(EffectCallback callback);

                // Runs when any dependency changes
                Hooks.useEffect(EffectCallback callback, JSObject[] deps);""", "java"),

            p(fragment(
                text("The "),
                code("EffectCallback"),
                text(" is a functional interface that returns either a cleanup "),
                code("Runnable"),
                text(" or "),
                code("null"),
                text(":")
            )),
            CodeBlock.create("""
                @FunctionalInterface
                public interface EffectCallback {
                    Runnable run();
                }""", "java"),

            // Cleanup pattern
            h2("Cleanup Pattern"),
            p(fragment(
                text("Return a "),
                code("Runnable"),
                text(" from your effect to clean up resources. React calls the cleanup "),
                text("function before the component unmounts and before re-running the "),
                text("effect if dependencies changed.")
            )),
            CodeTabs.create(
                """
                    Hooks.useEffectOnMount(() -> {
                        // Set up a subscription
                        int timerId = setInterval(() -> {
                            System.out.println("tick");
                        }, 1000);

                        // Return cleanup function
                        return () -> clearInterval(timerId);
                    });""",
                """
                    Hooks.useEffectOnMount {
                        // Set up a subscription
                        val timerId = setInterval({ println("tick") }, 1000)

                        // Return cleanup function
                        return@useEffectOnMount { clearInterval(timerId) }
                    }"""
            ),

            p(fragment(
                text("If your effect does not need cleanup, return "),
                code("null"),
                text(":")
            )),
            CodeBlock.create("""
                Hooks.useEffect(() -> {
                    System.out.println("Component rendered");
                    return null;  // No cleanup needed
                });""", "java"),

            // Dependency arrays
            h2("Dependency Arrays with deps()"),
            p(fragment(
                text("Use "),
                code("Hooks.deps()"),
                text(" to create dependency arrays. The effect re-runs only when a "),
                text("dependency value changes between renders.")
            )),
            CodeTabs.create(
                """
                    var userId = Hooks.useState(1);

                    // Re-runs whenever userId changes
                    Hooks.useEffect(() -> {
                        System.out.println("Fetching user " + userId.getInt());
                        // ... fetch user data ...
                        return null;
                    }, Hooks.deps(userId.getInt()));

                    // Empty deps -- runs only on mount
                    Hooks.useEffect(() -> {
                        System.out.println("Mounted");
                        return null;
                    }, Hooks.deps());""",
                """
                    val userId = Hooks.useState(1)

                    // Re-runs whenever userId changes
                    Hooks.useEffect({
                        println("Fetching user ${userId.int}")
                        null
                    }, Hooks.deps(userId.int))

                    // Empty deps -- runs only on mount
                    Hooks.useEffect({
                        println("Mounted")
                        null
                    }, Hooks.deps())"""
            ),

            Callout.note("deps() vs useEffectOnMount()",
                p(fragment(
                    text("Calling "),
                    code("Hooks.useEffect(callback, Hooks.deps())"),
                    text(" with an empty array is equivalent to "),
                    code("Hooks.useEffectOnMount(callback)"),
                    text(". Prefer "),
                    code("useEffectOnMount"),
                    text(" for readability when you only need mount behavior.")
                ))
            ),

            // Common patterns
            h2("Common Patterns"),

            h3("Timer"),
            CodeBlock.create("""
                Hooks.useEffectOnMount(() -> {
                    int id = setInterval(() -> {
                        seconds.updateInt(s -> s + 1);
                    }, 1000);
                    return () -> clearInterval(id);
                });""", "java"),

            h3("Event Listener"),
            CodeBlock.create("""
                Hooks.useEffectOnMount(() -> {
                    EventListener listener = e -> {
                        System.out.println("Window resized");
                    };
                    Window.addEventListener("resize", listener);
                    return () -> Window.removeEventListener(
                        "resize", listener);
                });""", "java"),

            h3("Reacting to State Changes"),
            CodeBlock.create("""
                var query = Hooks.useState("");
                var results = Hooks.useState("");

                Hooks.useEffect(() -> {
                    if (query.getString().length() > 2) {
                        // Perform search
                        results.setString(
                            "Results for: " + query.getString());
                    }
                    return null;
                }, Hooks.deps(query.getString()));""", "java"),

            // Pitfalls
            Callout.pitfall("Avoid Infinite Loops",
                p(fragment(
                    text("Do not call "),
                    code("setState"),
                    text(" unconditionally inside "),
                    code("useEffect"),
                    text(" without dependencies. This creates an infinite render loop "),
                    text("because setting state triggers a re-render, which re-runs the "),
                    text("effect, which sets state again.")
                )),
                CodeBlock.create("""
                    // WRONG: infinite loop!
                    Hooks.useEffect(() -> {
                        count.setInt(count.getInt() + 1);
                        return null;
                    });

                    // CORRECT: use dependencies to limit re-runs
                    Hooks.useEffect(() -> {
                        derived.setString("Value: " + count.getInt());
                        return null;
                    }, Hooks.deps(count.getInt()));""", "java")
            ),

            // Live demo
            h2("Live Demo: Timer"),
            p("A timer that starts on mount and cleans up on unmount:"),
            LiveDemo.create(UseEffectPage::timerDemo),

            // See also
            h2("See Also"),
            ul(
                li(a("useState -- Managing State").href("#/reference/use-state").build()),
                li(a("useRef -- Persisting Values Without Re-renders").href("#/reference/use-ref").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }

    private static ReactElement timerDemo(JSObject props) {
        var seconds = Hooks.useState(0);
        var running = Hooks.useState(true);

        Hooks.useEffect(() -> {
            if (!running.getBool()) {
                return null;
            }
            int id = setInterval(() -> {
                seconds.updateInt(s -> s + 1);
            }, 1000);
            return () -> clearInterval(id);
        }, Hooks.deps());

        return El.div("demo-timer",

            p("Elapsed: " + seconds.getInt() + " seconds"),
            El.div("demo-btn-group",

                button(running.getBool() ? "Pause" : "Resume")
                    .onClick(e -> running.setBool(!running.getBool()))
                    .className("demo-btn")
                    .build(),
                button("Reset")
                    .onClick(e -> {
                        seconds.setInt(0);
                        running.setBool(true);
                    })
                    .className("demo-btn")
                    .build()
            )
        );
    }
}

# Java Class-Based Approach (ReactView)

Read this when building teavm-react components as classes that extend `ReactView`. This approach is familiar to Swing and JavaFX developers: state lives in fields, lifecycle hooks are override methods, and each component is a separate class. All examples assume:

```java
import ca.weblite.teavmreact.component.ReactView;
import ca.weblite.teavmreact.hooks.*;
import ca.weblite.teavmreact.core.*;
import static ca.weblite.teavmreact.html.Html.*;
import org.teavm.jso.JSObject;
```

## Core Pattern

Extend `ReactView`, override `render()`, declare state as field initializers:

```java
public class Counter extends ReactView {
    private final StateHandle<Integer> count = Hooks.useState(0);

    @Override
    protected ReactElement render() {
        return div(
            h2("Count: " + count.getInt()),
            button("Increment").onClick(e -> count.updateInt(c -> c + 1)).build()
        );
    }
}
```

## Critical: Re-instantiation on Every Render

**The class is re-instantiated on every render.** This is how hooks work: field initializers call `Hooks.useState()` in consistent order, mapping each call to the corresponding React hook slot. This means:

- State MUST be declared as field initializers (not in constructors or methods)
- Field declaration ORDER must be stable (never conditional)
- Don't store non-hook mutable state in fields -- it will be lost on re-render
- The class is effectively a structured way to organize a functional component

```java
// CORRECT: hooks in field initializers, consistent order
public class MyView extends ReactView {
    private final StateHandle<String> name = Hooks.useState("");
    private final StateHandle<Integer> age = Hooks.useState(0);
    // ...
}

// WRONG: conditional hook call -- violates Rules of Hooks
public class BadView extends ReactView {
    private final StateHandle<String> name = Hooks.useState("");
    private StateHandle<Integer> age;
    { if (someCondition) age = Hooks.useState(0); } // NEVER DO THIS
}
```

## Lifecycle: onMount and onUnmount

Override `onMount()` and `onUnmount()` instead of using `useEffect` with empty deps:

```java
public class TimerView extends ReactView {
    private final StateHandle<Integer> seconds = Hooks.useState(0);

    @Override
    protected void onMount() {
        JsUtil.consoleLog("Component mounted");
    }

    @Override
    protected void onUnmount() {
        JsUtil.consoleLog("Component unmounting");
    }

    @Override
    protected ReactElement render() {
        return div(p("Seconds: " + seconds.getInt()));
    }
}
```

Under the hood, `onMount`/`onUnmount` are wired via a single `useEffect` with empty deps. The effect calls `onMount()` when mounted and returns `onUnmount` as the cleanup function.

## Using useEffect in render()

For effects that depend on state or need to re-run, call `Hooks.useEffect()` inside `render()`:

```java
public class PollingView extends ReactView {
    private final StateHandle<Integer> count = Hooks.useState(0);
    private final StateHandle<Boolean> running = Hooks.useState(true);

    @Override
    protected ReactElement render() {
        // This effect re-runs when `running` changes
        Hooks.useEffect(() -> {
            if (!running.getBool()) return null;
            int id = JsUtil.setInterval(() -> count.updateInt(c -> c + 1), 1000);
            return () -> JsUtil.clearInterval(id);
        });

        return div(
            p("Count: " + count.getInt()),
            button(running.getBool() ? "Stop" : "Start")
                .onClick(e -> running.setBool(!running.getBool()))
                .build()
        );
    }
}
```

## Using Refs

Declare refs as field initializers:

```java
public class TrackingView extends ReactView {
    private final RefHandle renderCount = Hooks.useRefInt(0);

    @Override
    protected ReactElement render() {
        Hooks.useEffect(() -> {
            renderCount.setCurrentInt(renderCount.getCurrentInt() + 1);
            return null;
        });
        return div(
            p("This component has rendered " + renderCount.getCurrentInt() + " times")
        );
    }
}
```

## Rendering ReactView Components

### As an Element (inline)

```java
ReactView.view(Counter::new, "Counter")
```

This creates the component and renders it in one call. The result is a `ReactElement`.

### Inside Other Components

```java
// Inside a functional component
JSObject App = React.wrapComponent(props -> {
    return div(
        h1("My App"),
        ReactView.view(Counter::new, "Counter"),
        ReactView.view(StopwatchView::new, "Stopwatch")
    );
}, "App");

// Inside another ReactView
public class Dashboard extends ReactView {
    @Override
    protected ReactElement render() {
        return div(
            h1("Dashboard"),
            ReactView.view(Counter::new, "Counter"),
            ReactView.view(StopwatchView::new, "Stopwatch")
        );
    }
}
```

## ViewFactory Interface

`ReactView.view()` accepts a `ViewFactory`:

```java
@FunctionalInterface
public interface ViewFactory {
    ReactView create();
}
```

Use method references (`MyView::new`) or lambdas (`() -> new MyView()`).

## Using Any HTML Approach Inside render()

The `render()` method returns `ReactElement`. You can use any approach:

```java
// Functional Html (recommended for readability)
@Override
protected ReactElement render() {
    return div(h1("Hello"), p("World"));
}

// Builder DSL
@Override
protected ReactElement render() {
    return Div.create()
        .child(H1.create().text("Hello"))
        .child(P.create().text("World"))
        .build();
}
```

## Event Handling

Same event APIs as the Functional approach:

```java
public class FormView extends ReactView {
    private final StateHandle<String> value = Hooks.useState("");

    @Override
    protected ReactElement render() {
        return div(
            input("text")
                .value(value.getString())
                .onChange(e -> value.setString(e.getTarget().getValue()))
                .onKeyDown(e -> {
                    if (e.getKey().equals("Enter")) {
                        JsUtil.consoleLog("Submitted: " + value.getString());
                    }
                })
                .build(),
            p("Current: " + value.getString())
        );
    }
}
```

## Complete Example: Stopwatch with Start/Stop/Reset

```java
import ca.weblite.teavmreact.component.ReactView;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import static ca.weblite.teavmreact.html.Html.*;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;

public class StopwatchApp {

    static class Stopwatch extends ReactView {
        private final StateHandle<Integer> elapsed = Hooks.useState(0);
        private final StateHandle<Boolean> running = Hooks.useState(false);

        @Override
        protected void onMount() {
            JsUtil.consoleLog("Stopwatch mounted");
        }

        @Override
        protected void onUnmount() {
            JsUtil.consoleLog("Stopwatch unmounting");
        }

        @Override
        protected ReactElement render() {
            // Timer effect -- re-runs whenever render happens
            Hooks.useEffect(() -> {
                if (!running.getBool()) return null;
                int id = JsUtil.setInterval(
                    () -> elapsed.updateInt(t -> t + 100), 100
                );
                return () -> JsUtil.clearInterval(id);
            });

            int totalMs = elapsed.getInt();
            int minutes = totalMs / 60000;
            int seconds = (totalMs % 60000) / 1000;
            int tenths = (totalMs % 1000) / 100;
            String display = String.format("%02d:%02d.%d", minutes, seconds, tenths);

            boolean isRunning = running.getBool();

            return div(
                h1("Stopwatch"),
                p(display),
                div(
                    button(isRunning ? "Stop" : "Start")
                        .onClick(e -> running.setBool(!isRunning))
                        .className(isRunning ? "btn-warning" : "btn-success")
                        .build(),
                    button("Reset")
                        .onClick(e -> {
                            elapsed.setInt(0);
                            running.setBool(false);
                        })
                        .disabled(totalMs == 0 && !isRunning)
                        .build()
                ),
                totalMs > 0 ? div(
                    h3("Splits"),
                    p("Total: " + totalMs + "ms"),
                    p(minutes + " min, " + seconds + " sec")
                ) : p("Press Start to begin timing.")
            );
        }
    }

    public static void main(String[] args) {
        ReactRoot root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(ReactView.view(Stopwatch::new, "Stopwatch"));
    }
}
```

## When to Choose Class-Based

- Porting Swing/JavaFX code and want familiar structure
- Prefer state as named fields rather than local variables
- Want `onMount`/`onUnmount` instead of raw `useEffect`
- Each component is a clear, self-contained unit in its own class

## When NOT to Choose Class-Based

- Small utility components (lambdas are shorter)
- Heavy hook usage beyond `useState` -- you'll still call `Hooks.*` directly
- If you forget the re-instantiation rule and try to store mutable instance state

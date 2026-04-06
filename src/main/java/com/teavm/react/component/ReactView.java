package com.teavm.react.component;

import com.teavm.react.core.React;
import com.teavm.react.core.ReactElement;
import com.teavm.react.core.RenderFunction;
import com.teavm.react.core.VoidCallback;
import com.teavm.react.hooks.Hooks;
import com.teavm.react.hooks.StateHandle;
import org.teavm.jso.JSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Approach C: Class-based component model familiar to Swing/JavaFX developers.
 *
 * Extend this class and override render() to create a component.
 * Declare state as fields using state() — they are backed by React hooks.
 *
 * Example:
 * <pre>
 *   public class Counter extends ReactView {
 *       private final StateHandle<Integer> count = useState(0);
 *
 *       protected ReactElement render() {
 *           return Html.div(
 *               Html.h2("Count: " + count.getInt()),
 *               Html.button("Increment").onClick(e -> count.setInt(count.getInt() + 1)).build()
 *           );
 *       }
 *   }
 * </pre>
 *
 * Under the hood, each ReactView subclass is compiled into a React functional
 * component. The class is instantiated on every render (like React class
 * components' render() method being called each time), and state() calls
 * map to React.useState() calls in the correct order.
 */
public abstract class ReactView {

    /**
     * Override this to return the component's element tree.
     * Called on every render, just like React's render() method.
     */
    protected abstract ReactElement render();

    /**
     * Called once when the component is first mounted.
     * Override to set up timers, subscriptions, etc.
     * Similar to Swing's addNotify() or JavaFX's initialize().
     */
    protected void onMount() {}

    /**
     * Called when the component is about to be removed from the DOM.
     * Override to clean up timers, subscriptions, etc.
     * Similar to Swing's removeNotify().
     */
    protected void onUnmount() {}

    /**
     * Create a React component from this ReactView class.
     * Returns a JSObject that can be passed to React.createElement().
     */
    public static JSObject toComponent(ViewFactory factory) {
        return toComponent(factory, "ReactView");
    }

    /**
     * Create a named React component from this ReactView class.
     */
    public static JSObject toComponent(ViewFactory factory, String displayName) {
        RenderFunction renderFn = (props) -> {
            ReactView view = factory.create();
            // Set up lifecycle hooks
            boolean hasLifecycle = hasLifecycleMethods(view);
            if (hasLifecycle) {
                Hooks.useEffect(() -> {
                    view.onMount();
                    return view::onUnmount;
                }, Hooks.deps());
            }
            return view.render();
        };
        return React.wrapComponent(renderFn, displayName);
    }

    /**
     * Convenience method to create a component and immediately create an element.
     */
    public static ReactElement view(ViewFactory factory) {
        return React.createElement(toComponent(factory), null);
    }

    /**
     * Convenience method to create a component element with a display name.
     */
    public static ReactElement view(ViewFactory factory, String displayName) {
        return React.createElement(toComponent(factory, displayName), null);
    }

    private static boolean hasLifecycleMethods(ReactView view) {
        // Always install lifecycle hook — the no-op defaults are harmless
        // and this avoids reflection which TeaVM doesn't support
        return true;
    }

    /**
     * Factory interface for creating ReactView instances.
     * Use lambda syntax: view(Counter::new)
     */
    @FunctionalInterface
    public interface ViewFactory {
        ReactView create();
    }
}

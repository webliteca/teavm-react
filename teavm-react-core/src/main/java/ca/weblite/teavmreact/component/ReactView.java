package ca.weblite.teavmreact.component;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.core.RenderFunction;
import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSObject;

/**
 * Class-based component model familiar to Swing/JavaFX developers.
 *
 * Extend this class and override {@link #render()} to create a component.
 * Use {@link Hooks#useState(int)} etc. to declare state in field initializers.
 *
 * <pre>
 *   public class Counter extends ReactView {
 *       private final StateHandle&lt;Integer&gt; count = Hooks.useState(0);
 *
 *       protected ReactElement render() {
 *           return Html.div(
 *               Html.h2("Count: " + count.getInt()),
 *               Html.button("Increment").onClick(e -> count.updateInt(c -> c + 1)).build()
 *           );
 *       }
 *   }
 * </pre>
 *
 * Under the hood, each ReactView subclass becomes a React functional component.
 * The class is instantiated on every render so that state() calls map to
 * React hooks in consistent order.
 */
public abstract class ReactView {

    /**
     * Override to return the component's element tree.
     */
    protected abstract ReactElement render();

    /**
     * Called once when the component is mounted. Override to set up resources.
     */
    protected void onMount() {}

    /**
     * Called when the component is about to unmount. Override to clean up.
     */
    protected void onUnmount() {}

    /**
     * Create and render a component element.
     */
    public static ReactElement view(ViewFactory factory, String displayName) {
        return React.createElement(toComponent(factory, displayName), null);
    }

    /**
     * Create and render a component element with default name.
     */
    public static ReactElement view(ViewFactory factory) {
        return React.createElement(toComponent(factory, "ReactView"), null);
    }

    // ---- Package-private: component wrapping ----

    static JSObject toComponent(ViewFactory factory, String displayName) {
        RenderFunction renderFn = (props) -> {
            ReactView view = factory.create();
            Hooks.useEffect(() -> {
                view.onMount();
                return view::onUnmount;
            }, Hooks.deps());
            return view.render();
        };
        return React.wrapComponent(renderFn, displayName);
    }

    @FunctionalInterface
    public interface ViewFactory {
        ReactView create();
    }
}

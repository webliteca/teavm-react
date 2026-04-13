package ca.weblite.teavmreact.core;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Wrapper for a React context object. Use with Hooks.useContext() and Html.provider().
 */
public class ReactContext {

    private final JSObject jsContext;

    private ReactContext(JSObject jsContext) {
        this.jsContext = jsContext;
    }

    /**
     * Create a new React context with no default value.
     */
    public static ReactContext create() {
        return new ReactContext(React.createContext());
    }

    /**
     * Create a new React context with a default value.
     */
    public static ReactContext create(JSObject defaultValue) {
        return new ReactContext(React.createContext(defaultValue));
    }

    /**
     * Get the underlying JS context object (for passing to createElement).
     */
    public JSObject jsContext() {
        return jsContext;
    }

    /**
     * Get the Provider component for this context.
     */
    public JSObject provider() {
        return getProvider(jsContext);
    }

    /**
     * Create a Provider element that wraps children with this context value.
     */
    public ReactElement provide(JSObject value, ReactElement... children) {
        JSObject props = React.createObject();
        React.setProperty(props, "value", value);
        JSObject all = React.createArray();
        for (int i = 0; i < children.length; i++) {
            React.arrayPush(all, children[i]);
        }
        return React.createElementFromArray(provider(), props, all);
    }

    @JSBody(params = {"ctx"}, script = "return ctx.Provider;")
    private static native JSObject getProvider(JSObject ctx);
}

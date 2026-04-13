package ca.weblite.teavmreact.core;

import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Wrapper for a React context object. Use with {@link #useString()},
 * {@link #useInt()}, {@link #useBool()} to consume values, and
 * {@link #provide(String, ReactElement...)} to provide them.
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
        return new ReactContext(createContextJS());
    }

    /**
     * Create a new React context with a String default value.
     */
    public static ReactContext create(String defaultValue) {
        return new ReactContext(createContextString(defaultValue));
    }

    /**
     * Create a new React context with an int default value.
     */
    public static ReactContext create(int defaultValue) {
        return new ReactContext(createContextInt(defaultValue));
    }

    /**
     * Create a new React context with a boolean default value.
     */
    public static ReactContext create(boolean defaultValue) {
        return new ReactContext(createContextBool(defaultValue));
    }

    // ---- Typed context consumption ----

    /**
     * Read the current context value as a String.
     * Must be called during a component render.
     */
    public String useString() {
        return useContextString(jsContext);
    }

    /**
     * Read the current context value as an int.
     * Must be called during a component render.
     */
    public int useInt() {
        return useContextInt(jsContext);
    }

    /**
     * Read the current context value as a boolean.
     * Must be called during a component render.
     */
    public boolean useBool() {
        return useContextBool(jsContext);
    }

    // ---- Typed providers ----

    /**
     * Create a Provider element with a String value.
     */
    public ReactElement provide(String value, ReactElement... children) {
        JSObject props = React.createObject();
        setProviderValue(props, value);
        return createProviderElement(props, children);
    }

    /**
     * Create a Provider element with an int value.
     */
    public ReactElement provide(int value, ReactElement... children) {
        JSObject props = React.createObject();
        setProviderValueInt(props, value);
        return createProviderElement(props, children);
    }

    /**
     * Create a Provider element with a boolean value.
     */
    public ReactElement provide(boolean value, ReactElement... children) {
        JSObject props = React.createObject();
        setProviderValueBool(props, value);
        return createProviderElement(props, children);
    }

    // ---- Package-private: raw context for advanced use ----

    JSObject rawContext() {
        return jsContext;
    }

    // ---- Private helpers ----

    private ReactElement createProviderElement(JSObject props, ReactElement[] children) {
        JSObject provider = getProvider(jsContext);
        JSObject all = React.createArray();
        for (int i = 0; i < children.length; i++) {
            React.arrayPush(all, children[i]);
        }
        return React.createElementFromArray(provider, props, all);
    }

    // ---- Private JS bridge methods ----

    @JSBody(script = "return React.createContext(undefined);")
    private static native JSObject createContextJS();

    @JSBody(params = {"defaultValue"}, script = "return React.createContext(defaultValue);")
    private static native JSObject createContextString(String defaultValue);

    @JSBody(params = {"defaultValue"}, script = "return React.createContext(defaultValue);")
    private static native JSObject createContextInt(int defaultValue);

    @JSBody(params = {"defaultValue"}, script = "return React.createContext(defaultValue);")
    private static native JSObject createContextBool(boolean defaultValue);

    @JSBody(params = {"ctx"}, script = "return ctx.Provider;")
    private static native JSObject getProvider(JSObject ctx);

    @JSBody(params = {"ctx"}, script = "return '' + React.useContext(ctx);")
    private static native String useContextString(JSObject ctx);

    @JSBody(params = {"ctx"}, script = "return React.useContext(ctx)|0;")
    private static native int useContextInt(JSObject ctx);

    @JSBody(params = {"ctx"}, script = "return !!React.useContext(ctx);")
    private static native boolean useContextBool(JSObject ctx);

    @JSBody(params = {"props", "value"}, script = "props['value'] = value;")
    private static native void setProviderValue(JSObject props, String value);

    @JSBody(params = {"props", "value"}, script = "props['value'] = value;")
    private static native void setProviderValueInt(JSObject props, int value);

    @JSBody(params = {"props", "value"}, script = "props['value'] = value;")
    private static native void setProviderValueBool(JSObject props, boolean value);
}

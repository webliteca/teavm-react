package ca.weblite.teavmreact.core;

import ca.weblite.teavmreact.events.ChangeEventHandler;
import ca.weblite.teavmreact.events.EventHandler;
import ca.weblite.teavmreact.events.FocusEventHandler;
import ca.weblite.teavmreact.events.KeyboardEventHandler;
import ca.weblite.teavmreact.events.SubmitEventHandler;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Core bindings to the React global object.
 * React must be loaded via a script tag before the TeaVM-compiled JS runs.
 *
 * <p>All methods use {@code @JSBody} annotations for direct JS interop.
 * Event handler setters use dedicated methods with functor parameters
 * to ensure the raw JS function is passed (not a Java wrapper object).
 */
public final class React {

    private React() {}

    // -----------------------------------------------------------------------
    // createElement
    // -----------------------------------------------------------------------

    @JSBody(params = {"type", "props"}, script =
            "return React.createElement(type, props);")
    public static native ReactElement createElement(String type, JSObject props);

    /**
     * createElement with varargs children.
     * Uses .apply/.concat instead of ES6 spread (unsupported by TeaVM @JSBody).
     */
    @JSBody(params = {"type", "props", "children"}, script =
            "return React.createElement.apply(null, [type, props].concat(Array.prototype.slice.call(children)));")
    public static native ReactElement createElement(String type, JSObject props, JSObject[] children);

    @JSBody(params = {"type", "props"}, script =
            "return React.createElement(type, props);")
    public static native ReactElement createElement(JSObject type, JSObject props);

    /**
     * createElement for component types with varargs children.
     * Uses .apply/.concat instead of ES6 spread (unsupported by TeaVM @JSBody).
     */
    @JSBody(params = {"type", "props", "children"}, script =
            "return React.createElement.apply(null, [type, props].concat(Array.prototype.slice.call(children)));")
    public static native ReactElement createElement(JSObject type, JSObject props, JSObject[] children);

    @JSBody(params = {"type", "props", "child"}, script =
            "return React.createElement(type, props, child);")
    public static native ReactElement createElement(String type, JSObject props, JSObject child);

    @JSBody(params = {"type", "props", "text"}, script =
            "return React.createElement(type, props, text);")
    public static native ReactElement createElementWithText(String type, JSObject props, String text);

    // -----------------------------------------------------------------------
    // Component wrapping
    // -----------------------------------------------------------------------

    /**
     * Wraps a Java {@link RenderFunction} as a React component function.
     * The returned JSObject can be passed to createElement as a component type.
     * Display name defaults to 'JavaComponent'.
     */
    @JSBody(params = {"renderFn"}, script =
            "var comp = function(props) { return renderFn(props); }; " +
            "comp.displayName = 'JavaComponent'; " +
            "return comp;")
    public static native JSObject wrapComponent(RenderFunction renderFn);

    /**
     * Wraps a Java {@link RenderFunction} with a custom display name
     * for React DevTools.
     */
    @JSBody(params = {"renderFn", "name"}, script =
            "var comp = function(props) { return renderFn(props); }; " +
            "comp.displayName = name; " +
            "return comp;")
    public static native JSObject wrapComponent(RenderFunction renderFn, String name);

    // -----------------------------------------------------------------------
    // Object / property utilities
    // -----------------------------------------------------------------------

    @JSBody(script = "return {};")
    public static native JSObject createObject();

    @JSBody(params = {"obj", "key"}, script = "return obj[key];")
    public static native JSObject getProperty(JSObject obj, String key);

    @JSBody(params = {"obj", "key", "value"}, script = "obj[key] = value;")
    public static native void setProperty(JSObject obj, String key, JSObject value);

    @JSBody(params = {"obj", "key", "value"}, script = "obj[key] = value;")
    public static native void setProperty(JSObject obj, String key, String value);

    @JSBody(params = {"obj", "key", "value"}, script = "obj[key] = value;")
    public static native void setProperty(JSObject obj, String key, int value);

    @JSBody(params = {"obj", "key", "value"}, script = "obj[key] = value;")
    public static native void setProperty(JSObject obj, String key, boolean value);

    @JSBody(params = {"obj", "key", "value"}, script = "obj[key] = value;")
    public static native void setProperty(JSObject obj, String key, double value);

    // -----------------------------------------------------------------------
    // Event handler setters
    //
    // CRITICAL: These must use @JSBody with the functor as a direct parameter.
    // Using a generic setProperty that casts to JSObject would wrap the functor
    // in a Java object instead of passing the raw JS function.
    // -----------------------------------------------------------------------

    @JSBody(params = {"obj", "handler"}, script = "obj['onClick'] = handler;")
    public static native void setOnClick(JSObject obj, EventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onChange'] = handler;")
    public static native void setOnChange(JSObject obj, ChangeEventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onKeyDown'] = handler;")
    public static native void setOnKeyDown(JSObject obj, KeyboardEventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onKeyUp'] = handler;")
    public static native void setOnKeyUp(JSObject obj, KeyboardEventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onFocus'] = handler;")
    public static native void setOnFocus(JSObject obj, FocusEventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onBlur'] = handler;")
    public static native void setOnBlur(JSObject obj, FocusEventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onSubmit'] = handler;")
    public static native void setOnSubmit(JSObject obj, SubmitEventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onMouseDown'] = handler;")
    public static native void setOnMouseDown(JSObject obj, EventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onMouseUp'] = handler;")
    public static native void setOnMouseUp(JSObject obj, EventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onMouseEnter'] = handler;")
    public static native void setOnMouseEnter(JSObject obj, EventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onMouseLeave'] = handler;")
    public static native void setOnMouseLeave(JSObject obj, EventHandler handler);

    // -----------------------------------------------------------------------
    // JS array utilities (for building children arrays natively in JS,
    // avoiding TeaVM JSObject[] marshaling issues with ArrayList-sourced elements)
    // -----------------------------------------------------------------------

    @JSBody(script = "return [];")
    public static native JSObject createArray();

    @JSBody(params = {"arr", "element"}, script = "arr.push(element);")
    public static native void arrayPush(JSObject arr, JSObject element);

    /**
     * createElement variant that accepts a native JS array of children (as JSObject).
     * This avoids passing a Java JSObject[] through the @JSBody boundary.
     */
    @JSBody(params = {"type", "props", "childrenArray"}, script =
            "return React.createElement.apply(null, [type, props].concat(childrenArray));")
    public static native ReactElement createElementFromArray(String type, JSObject props, JSObject childrenArray);

    /**
     * createElement variant for component types that accepts a native JS array of children.
     */
    @JSBody(params = {"type", "props", "childrenArray"}, script =
            "return React.createElement.apply(null, [type, props].concat(childrenArray));")
    public static native ReactElement createElementFromArray(JSObject type, JSObject props, JSObject childrenArray);

    // -----------------------------------------------------------------------
    // Context API
    // -----------------------------------------------------------------------

    @JSBody(script = "return React.createContext(undefined);")
    public static native JSObject createContext();

    @JSBody(params = {"defaultValue"}, script = "return React.createContext(defaultValue);")
    public static native JSObject createContext(JSObject defaultValue);

    // -----------------------------------------------------------------------
    // Memo
    // -----------------------------------------------------------------------

    @JSBody(params = {"component"}, script = "return React.memo(component);")
    public static native JSObject memo(JSObject component);

    // -----------------------------------------------------------------------
    // Fragment
    // -----------------------------------------------------------------------

    @JSBody(script = "return React.Fragment;")
    public static native JSObject fragment();

    // -----------------------------------------------------------------------
    // Type conversion utilities
    // -----------------------------------------------------------------------

    @JSBody(params = {"s"}, script = "return s;")
    public static native JSObject stringToJS(String s);

    @JSBody(params = {"n"}, script = "return n;")
    public static native JSObject intToJS(int n);

    @JSBody(params = {"b"}, script = "return b;")
    public static native JSObject boolToJS(boolean b);

    @JSBody(params = {"obj"}, script = "return '' + obj;")
    public static native String jsToString(JSObject obj);

    @JSBody(params = {"obj"}, script = "return obj|0;")
    public static native int jsToInt(JSObject obj);

    @JSBody(params = {"obj"}, script = "return !!obj;")
    public static native boolean jsToBool(JSObject obj);
}

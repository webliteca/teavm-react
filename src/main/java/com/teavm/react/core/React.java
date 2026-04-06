package com.teavm.react.core;

import com.teavm.react.events.ChangeEventHandler;
import com.teavm.react.events.EventHandler;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Core bindings to the React global object.
 * React must be loaded via a script tag before the TeaVM-compiled JS runs.
 */
public final class React {

    private React() {}

    // --- createElement ---

    @JSBody(params = {"type", "props"}, script =
            "return React.createElement(type, props);")
    public static native ReactElement createElement(String type, JSObject props);

    @JSBody(params = {"type", "props", "children"}, script =
            "return React.createElement.apply(null, [type, props].concat(Array.prototype.slice.call(children)));")
    public static native ReactElement createElement(String type, JSObject props, JSObject[] children);

    @JSBody(params = {"type", "props"}, script =
            "return React.createElement(type, props);")
    public static native ReactElement createElement(JSObject type, JSObject props);

    @JSBody(params = {"type", "props", "children"}, script =
            "return React.createElement.apply(null, [type, props].concat(Array.prototype.slice.call(children)));")
    public static native ReactElement createElement(JSObject type, JSObject props, JSObject[] children);

    // createElement with a single child (common case)
    @JSBody(params = {"type", "props", "child"}, script =
            "return React.createElement(type, props, child);")
    public static native ReactElement createElement(String type, JSObject props, JSObject child);

    // createElement for text content
    @JSBody(params = {"type", "props", "text"}, script =
            "return React.createElement(type, props, text);")
    public static native ReactElement createElementWithText(String type, JSObject props, String text);

    // --- Component wrapping ---

    /**
     * Wraps a Java RenderFunction as a React component function.
     * This is the critical bridge: the returned JSObject can be passed
     * to createElement as a component type.
     */
    @JSBody(params = {"renderFn"}, script =
            "var comp = function(props) { return renderFn(props); }; " +
            "comp.displayName = 'JavaComponent'; " +
            "return comp;")
    public static native JSObject wrapComponent(RenderFunction renderFn);

    /**
     * Wraps a Java RenderFunction with a display name for React DevTools.
     */
    @JSBody(params = {"renderFn", "name"}, script =
            "var comp = function(props) { return renderFn(props); }; " +
            "comp.displayName = name; " +
            "return comp;")
    public static native JSObject wrapComponent(RenderFunction renderFn, String name);

    // --- Utility: create plain JS objects for props ---

    @JSBody(script = "return {};")
    public static native JSObject createObject();

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

    // --- Utility: wrap Java string as JSObject ---

    @JSBody(params = {"s"}, script = "return s;")
    public static native JSObject stringToJS(String s);

    // --- Utility: wrap int as JSObject ---

    @JSBody(params = {"n"}, script = "return n;")
    public static native JSObject intToJS(int n);

    // --- Event handler setters (must use @JSBody to preserve functor as raw JS function) ---

    @JSBody(params = {"obj", "handler"}, script = "obj['onClick'] = handler;")
    public static native void setOnClick(JSObject obj, EventHandler handler);

    @JSBody(params = {"obj", "handler"}, script = "obj['onChange'] = handler;")
    public static native void setOnChange(JSObject obj, ChangeEventHandler handler);

    // --- Fragment ---

    @JSBody(script = "return React.Fragment;")
    public static native JSObject fragment();

    // --- Create JS array ---

    @JSBody(script = "return [];")
    public static native JSObject[] emptyArray();

    @JSBody(params = {"arr", "item"}, script = "arr.push(item);")
    public static native void arrayPush(JSObject[] arr, JSObject item);
}

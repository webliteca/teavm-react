package com.teavm.react.hooks;

import com.teavm.react.core.VoidCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Java bindings for React Hooks.
 * These MUST be called inside a component's render function (same rules as React hooks).
 */
public final class Hooks {

    private Hooks() {}

    // --- useState ---

    /**
     * useState for int values.
     */
    public static StateHandle<Integer> useState(int initialValue) {
        return new StateHandle<>(useStateRawInt(initialValue));
    }

    /**
     * useState for String values.
     */
    public static StateHandle<String> useState(String initialValue) {
        return new StateHandle<>(useStateRawString(initialValue));
    }

    /**
     * useState for boolean values.
     */
    public static StateHandle<Boolean> useState(boolean initialValue) {
        return new StateHandle<>(useStateRawBool(initialValue));
    }

    /**
     * useState for any JSObject value.
     */
    public static StateHandle<JSObject> useState(JSObject initialValue) {
        return new StateHandle<>(useStateRawObj(initialValue));
    }

    // --- useEffect ---

    /**
     * useEffect with no dependency array (runs after every render).
     */
    public static void useEffect(EffectCallback effect) {
        useEffectRaw(effect);
    }

    /**
     * useEffect with a dependency array.
     * Pass an empty array via deps() for mount-only effects.
     */
    public static void useEffect(EffectCallback effect, JSObject[] deps) {
        useEffectRawWithDeps(effect, deps);
    }

    /**
     * Create an empty dependency array (for mount-only effects).
     */
    public static JSObject[] deps() {
        return createEmptyJSArray();
    }

    /**
     * Create a dependency array with items.
     */
    public static JSObject[] deps(JSObject... items) {
        return items;
    }

    // --- useRef ---

    /**
     * useRef returning a mutable ref object.
     */
    @JSBody(params = {"initial"}, script = "return React.useRef(initial);")
    public static native JSObject useRef(JSObject initial);

    @JSBody(params = {"initial"}, script = "return React.useRef(initial);")
    public static native JSObject useRefInt(int initial);

    // --- useMemo ---

    @JSBody(params = {"factory", "deps"}, script =
            "return React.useMemo(factory, deps);")
    public static native JSObject useMemo(VoidCallback factory, JSObject[] deps);

    // --- useCallback ---

    @JSBody(params = {"callback", "deps"}, script =
            "return React.useCallback(callback, deps);")
    public static native JSObject useCallback(JSObject callback, JSObject[] deps);

    // --- Raw JS bindings ---

    @JSBody(params = {"initial"}, script = "return React.useState(initial);")
    private static native JSObject[] useStateRawInt(int initial);

    @JSBody(params = {"initial"}, script = "return React.useState(initial);")
    private static native JSObject[] useStateRawString(String initial);

    @JSBody(params = {"initial"}, script = "return React.useState(initial);")
    private static native JSObject[] useStateRawBool(boolean initial);

    @JSBody(params = {"initial"}, script = "return React.useState(initial);")
    private static native JSObject[] useStateRawObj(JSObject initial);

    @JSBody(params = {"effect"}, script =
            "React.useEffect(function() { var cleanup = effect(); return cleanup || undefined; });")
    private static native void useEffectRaw(EffectCallback effect);

    @JSBody(params = {"effect", "deps"}, script =
            "React.useEffect(function() { var cleanup = effect(); return cleanup || undefined; }, deps);")
    private static native void useEffectRawWithDeps(EffectCallback effect, JSObject[] deps);

    @JSBody(script = "return [];")
    private static native JSObject[] createEmptyJSArray();
}

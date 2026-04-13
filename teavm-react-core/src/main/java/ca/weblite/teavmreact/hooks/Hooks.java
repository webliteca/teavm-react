package ca.weblite.teavmreact.hooks;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Static methods wrapping React hooks.
 *
 * <p>All methods in this class MUST be called during a component render
 * (i.e., inside a {@link ca.weblite.teavmreact.core.RenderFunction}).
 * Calling them outside a render will violate React's Rules of Hooks.</p>
 */
public final class Hooks {

    private Hooks() {}

    // ====================================================================
    // useState
    // ====================================================================

    /**
     * React.useState for int values.
     */
    public static StateHandle<Integer> useState(int initial) {
        return new StateHandle<>(useStateInt(initial));
    }

    /**
     * React.useState for String values.
     */
    public static StateHandle<String> useState(String initial) {
        return new StateHandle<>(useStateString(initial));
    }

    /**
     * React.useState for boolean values.
     */
    public static StateHandle<Boolean> useState(boolean initial) {
        return new StateHandle<>(useStateBool(initial));
    }

    /**
     * React.useState for double values.
     */
    public static StateHandle<Double> useState(double initial) {
        return new StateHandle<>(useStateDouble(initial));
    }

    // ====================================================================
    // useEffect
    // ====================================================================

    /**
     * useEffect with no dependency array (runs after every render).
     */
    public static void useEffect(EffectCallback effect) {
        useEffectNoDeps(effect);
    }

    /**
     * useEffect that runs only once on mount (equivalent to useEffect with []).
     */
    public static void useEffectOnMount(EffectCallback effect) {
        useEffectWithDeps(effect, emptyDeps());
    }

    /**
     * useEffect with a dependency array.
     * Pass {@link #deps()} for an empty array (run once on mount).
     */
    public static void useEffect(EffectCallback effect, JSObject[] deps) {
        useEffectWithDeps(effect, deps);
    }

    // ====================================================================
    // useRef
    // ====================================================================

    /**
     * React.useRef for an arbitrary JS object.
     */
    public static RefHandle useRef(JSObject initial) {
        return new RefHandle(useRefObj(initial));
    }

    /**
     * React.useRef for an int initial value.
     */
    public static RefHandle useRefInt(int initial) {
        return new RefHandle(useRefIntJS(initial));
    }

    /**
     * React.useRef for a String initial value.
     */
    public static RefHandle useRefString(String initial) {
        return new RefHandle(useRefStringJS(initial));
    }

    // ====================================================================
    // useMemo / useCallback
    // ====================================================================

    /**
     * React.useMemo — memoize a computed value.
     */
    public static JSObject useMemo(MemoFactory factory, JSObject[] deps) {
        return useMemoJS(factory, deps);
    }

    /**
     * React.useCallback — memoize a callback reference.
     */
    public static JSObject useCallback(JSObject callback, JSObject[] deps) {
        return useCallbackJS(callback, deps);
    }

    // ====================================================================
    // useReducer
    // ====================================================================

    /**
     * React.useReducer — returns a two-element array [state, dispatch].
     * The reducer is a JS function (state, action) => newState.
     */
    public static JSObject[] useReducer(JSObject reducer, JSObject initialState) {
        return useReducerJS(reducer, initialState);
    }

    // ====================================================================
    // useContext
    // ====================================================================

    /**
     * React.useContext — reads the current value from a React context object.
     */
    public static JSObject useContext(JSObject context) {
        return useContextJS(context);
    }

    // ====================================================================
    // deps helpers
    // ====================================================================

    /**
     * Returns an empty JS array, for use as an empty dependency list
     * (equivalent to [] in JS — causes the effect to run only on mount).
     */
    public static JSObject[] deps() {
        return emptyDeps();
    }

    /**
     * Wraps the given items into a JS array for use as a dependency list.
     */
    public static JSObject[] deps(JSObject... items) {
        return items;
    }

    // ====================================================================
    // @JSFunctor for useMemo
    // ====================================================================

    /**
     * Factory function for useMemo.
     */
    @JSFunctor
    public interface MemoFactory extends JSObject {
        JSObject create();
    }

    // ====================================================================
    // Private native JS bridge methods
    // ====================================================================

    // --- useState ---

    @JSBody(params = {"initial"}, script =
        "var result = React.useState(initial);" +
        "return result;")
    private static native JSObject[] useStateInt(int initial);

    @JSBody(params = {"initial"}, script =
        "var result = React.useState(initial);" +
        "return result;")
    private static native JSObject[] useStateString(String initial);

    @JSBody(params = {"initial"}, script =
        "var result = React.useState(initial);" +
        "return result;")
    private static native JSObject[] useStateBool(boolean initial);

    @JSBody(params = {"initial"}, script =
        "var result = React.useState(initial);" +
        "return result;")
    private static native JSObject[] useStateDouble(double initial);

    @JSBody(params = {"initial"}, script =
        "var result = React.useState(initial);" +
        "return result;")
    private static native JSObject[] useStateObj(JSObject initial);

    // --- useEffect ---

    @JSBody(params = {"effect"}, script =
        "React.useEffect(function() {" +
        "  var cleanup = effect();" +
        "  return cleanup ? function() { cleanup(); } : undefined;" +
        "});")
    private static native void useEffectNoDeps(EffectCallback effect);

    @JSBody(params = {"effect", "deps"}, script =
        "React.useEffect(function() {" +
        "  var cleanup = effect();" +
        "  return cleanup ? function() { cleanup(); } : undefined;" +
        "}, deps);")
    private static native void useEffectWithDeps(EffectCallback effect, JSObject[] deps);

    // --- useRef ---

    @JSBody(params = {"initial"}, script = "return React.useRef(initial);")
    private static native JSObject useRefObj(JSObject initial);

    @JSBody(params = {"initial"}, script = "return React.useRef(initial);")
    private static native JSObject useRefIntJS(int initial);

    @JSBody(params = {"initial"}, script = "return React.useRef(initial);")
    private static native JSObject useRefStringJS(String initial);

    // --- useMemo / useCallback ---

    @JSBody(params = {"factory", "deps"}, script =
        "return React.useMemo(function() { return factory(); }, deps);")
    private static native JSObject useMemoJS(MemoFactory factory, JSObject[] deps);

    @JSBody(params = {"callback", "deps"}, script =
        "return React.useCallback(callback, deps);")
    private static native JSObject useCallbackJS(JSObject callback, JSObject[] deps);

    // --- useReducer ---

    @JSBody(params = {"reducer", "initialState"}, script =
        "var result = React.useReducer(reducer, initialState);" +
        "return result;")
    private static native JSObject[] useReducerJS(JSObject reducer, JSObject initialState);

    // --- useContext ---

    @JSBody(params = {"context"}, script = "return React.useContext(context);")
    private static native JSObject useContextJS(JSObject context);

    // --- deps ---

    @JSBody(params = {}, script = "return [];")
    private static native JSObject[] emptyDeps();
}

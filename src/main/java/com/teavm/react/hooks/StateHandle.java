package com.teavm.react.hooks;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Java-friendly wrapper around React's useState return value.
 * Instead of [value, setter] destructuring, provides .get() and .set() methods.
 *
 * This pattern is deliberately similar to JavaFX's Property<T> for familiarity.
 */
public class StateHandle<T> {
    private final JSObject[] hookResult;

    StateHandle(JSObject[] hookResult) {
        this.hookResult = hookResult;
    }

    /**
     * Get the current state value.
     */
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) unwrap(hookResult[0]);
    }

    /**
     * Get the current state value as a String.
     */
    public String getString() {
        return unwrapString(hookResult[0]);
    }

    /**
     * Get the current state value as an int.
     */
    public int getInt() {
        return unwrapInt(hookResult[0]);
    }

    /**
     * Set state to a new value. Triggers a React re-render.
     */
    public void set(T value) {
        callSetter(hookResult[1], wrap(value));
    }

    /**
     * Set state to a new int value.
     */
    public void setInt(int value) {
        callSetterInt(hookResult[1], value);
    }

    /**
     * Set state to a new String value.
     */
    public void setString(String value) {
        callSetterString(hookResult[1], value);
    }

    /**
     * Update state using a function (like React's functional updater).
     */
    public void update(StateUpdater<T> updater) {
        callSetterWithUpdater(hookResult[1], updater);
    }

    /**
     * Update int state using a function.
     */
    public void updateInt(IntStateUpdater updater) {
        callSetterWithIntUpdater(hookResult[1], updater);
    }

    // --- JS bridge methods ---

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetter(JSObject setter, JSObject value);

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetterInt(JSObject setter, int value);

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetterString(JSObject setter, String value);

    @JSBody(params = {"setter", "updater"}, script = "setter(function(prev) { return updater(prev); });")
    private static native void callSetterWithUpdater(JSObject setter, StateUpdater<?> updater);

    @JSBody(params = {"setter", "updater"}, script = "setter(function(prev) { return updater(prev); });")
    private static native void callSetterWithIntUpdater(JSObject setter, IntStateUpdater updater);

    @JSBody(params = {"value"}, script = "return value;")
    private static native Object unwrap(JSObject value);

    @JSBody(params = {"value"}, script = "return '' + value;")
    private static native String unwrapString(JSObject value);

    @JSBody(params = {"value"}, script = "return value|0;")
    private static native int unwrapInt(JSObject value);

    @JSBody(params = {"value"}, script = "return value;")
    private static native JSObject wrap(Object value);

    /**
     * Functional updater interface for state updates.
     */
    @JSFunctor
    public interface StateUpdater<T> extends JSObject {
        T update(T previousValue);
    }

    /**
     * Specialized int updater to avoid boxing.
     */
    @JSFunctor
    public interface IntStateUpdater extends JSObject {
        int update(int previousValue);
    }
}

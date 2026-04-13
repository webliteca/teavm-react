package ca.weblite.teavmreact.hooks;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Java-friendly wrapper for React's useState return value [value, setter].
 *
 * <p>React hooks return JS primitives, not Java objects. Use the typed
 * getters ({@link #getString()}, {@link #getInt()}, {@link #getBool()},
 * {@link #getDouble()}) to extract values correctly via JS coercion
 * rather than Java casts.</p>
 *
 * @param <T> the logical Java type of the state value (for documentation only)
 */
public class StateHandle<T> {

    private final JSObject[] hookResult;

    /**
     * Package-private constructor. Use {@link Hooks#useState} to create instances.
     *
     * @param hookResult the two-element JS array [value, setter] returned by React.useState
     */
    StateHandle(JSObject[] hookResult) {
        this.hookResult = hookResult;
    }

    // ---- Getters ----

    /**
     * Returns the current state as a String.
     */
    public String getString() {
        return unwrapString(hookResult[0]);
    }

    /**
     * Returns the current state as an int (using JS bitwise-or coercion).
     */
    public int getInt() {
        return unwrapInt(hookResult[0]);
    }

    /**
     * Returns the current state as a boolean (using JS double-bang coercion).
     */
    public boolean getBool() {
        return unwrapBool(hookResult[0]);
    }

    /**
     * Returns the current state as a double (using JS unary-plus coercion).
     */
    public double getDouble() {
        return unwrapDouble(hookResult[0]);
    }

    // ---- Setters ----

    /**
     * Sets the state to an int value.
     */
    public void setInt(int value) {
        callSetterInt(hookResult[1], value);
    }

    /**
     * Sets the state to a String value.
     */
    public void setString(String value) {
        callSetterString(hookResult[1], value);
    }

    /**
     * Sets the state to a boolean value.
     */
    public void setBool(boolean value) {
        callSetterBool(hookResult[1], value);
    }

    /**
     * Sets the state to a double value.
     */
    public void setDouble(double value) {
        callSetterDouble(hookResult[1], value);
    }

    // ---- Functional updates ----

    /**
     * Performs a functional update for int state: setter(prev =&gt; updater(prev)).
     */
    public void updateInt(IntUpdater updater) {
        callSetterWithIntUpdater(hookResult[1], updater);
    }

    /**
     * Performs a functional update for String state: setter(prev =&gt; updater(prev)).
     */
    public void updateString(StringUpdater updater) {
        callSetterWithStringUpdater(hookResult[1], updater);
    }

    // ---- @JSFunctor interfaces for functional updates ----

    /**
     * Functional updater for int state.
     */
    @JSFunctor
    public interface IntUpdater extends JSObject {
        int update(int prev);
    }

    /**
     * Functional updater for String state.
     */
    @JSFunctor
    public interface StringUpdater extends JSObject {
        String update(String prev);
    }

    // ---- Private JS bridge methods ----

    @JSBody(params = {"value"}, script = "return '' + value;")
    private static native String unwrapString(JSObject value);

    @JSBody(params = {"value"}, script = "return value|0;")
    private static native int unwrapInt(JSObject value);

    @JSBody(params = {"value"}, script = "return !!value;")
    private static native boolean unwrapBool(JSObject value);

    @JSBody(params = {"value"}, script = "return +value;")
    private static native double unwrapDouble(JSObject value);

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetterInt(JSObject setter, int value);

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetterString(JSObject setter, String value);

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetterBool(JSObject setter, boolean value);

    @JSBody(params = {"setter", "value"}, script = "setter(value);")
    private static native void callSetterDouble(JSObject setter, double value);

    @JSBody(params = {"setter", "updater"}, script = "setter(function(prev) { return updater(prev|0); });")
    private static native void callSetterWithIntUpdater(JSObject setter, IntUpdater updater);

    @JSBody(params = {"setter", "updater"}, script = "setter(function(prev) { return updater('' + prev); });")
    private static native void callSetterWithStringUpdater(JSObject setter, StringUpdater updater);
}

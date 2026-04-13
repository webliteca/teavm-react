package ca.weblite.teavmreact.hooks;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Java-friendly wrapper around a React ref object ({current: value}).
 * Use typed getters and setters to avoid working with raw JSObject.
 */
public class RefHandle {

    private final JSObject ref;

    RefHandle(JSObject ref) {
        this.ref = ref;
    }

    // ---- Typed getters ----

    /**
     * Get the current value as a String.
     */
    public String getCurrentString() {
        return getCurrentString(ref);
    }

    /**
     * Get the current value as an int.
     */
    public int getCurrentInt() {
        return getCurrentInt(ref);
    }

    /**
     * Get the current value as a boolean.
     */
    public boolean getCurrentBool() {
        return getCurrentBool(ref);
    }

    /**
     * Get the current value as a double.
     */
    public double getCurrentDouble() {
        return getCurrentDouble(ref);
    }

    // ---- Typed setters ----

    /**
     * Set the current value to a String.
     */
    public void setCurrentString(String value) {
        setCurrentString(ref, value);
    }

    /**
     * Set the current value to an int.
     */
    public void setCurrentInt(int value) {
        setCurrentInt(ref, value);
    }

    /**
     * Set the current value to a boolean.
     */
    public void setCurrentBool(boolean value) {
        setCurrentBool(ref, value);
    }

    /**
     * Set the current value to a double.
     */
    public void setCurrentDouble(double value) {
        setCurrentDouble(ref, value);
    }

    // ---- Package-private: raw ref for passing to React elements ----

    JSObject rawRef() {
        return ref;
    }

    // ---- Private JS bridge methods ----

    @JSBody(params = {"ref"}, script = "return '' + ref.current;")
    private static native String getCurrentString(JSObject ref);

    @JSBody(params = {"ref"}, script = "return ref.current|0;")
    private static native int getCurrentInt(JSObject ref);

    @JSBody(params = {"ref"}, script = "return !!ref.current;")
    private static native boolean getCurrentBool(JSObject ref);

    @JSBody(params = {"ref"}, script = "return +ref.current;")
    private static native double getCurrentDouble(JSObject ref);

    @JSBody(params = {"ref", "value"}, script = "ref.current = value;")
    private static native void setCurrentString(JSObject ref, String value);

    @JSBody(params = {"ref", "value"}, script = "ref.current = value;")
    private static native void setCurrentInt(JSObject ref, int value);

    @JSBody(params = {"ref", "value"}, script = "ref.current = value;")
    private static native void setCurrentBool(JSObject ref, boolean value);

    @JSBody(params = {"ref", "value"}, script = "ref.current = value;")
    private static native void setCurrentDouble(JSObject ref, double value);
}

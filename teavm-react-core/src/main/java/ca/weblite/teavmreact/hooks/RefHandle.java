package ca.weblite.teavmreact.hooks;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Java-friendly wrapper around a React ref object ({current: value}).
 */
public class RefHandle {

    private final JSObject ref;

    RefHandle(JSObject ref) {
        this.ref = ref;
    }

    /**
     * Returns the underlying JS ref object (for passing to React elements).
     */
    public JSObject raw() {
        return ref;
    }

    /**
     * Get the current value of the ref.
     */
    public JSObject getCurrent() {
        return getCurrent(ref);
    }

    /**
     * Set the current value of the ref.
     */
    public void setCurrent(JSObject value) {
        setCurrent(ref, value);
    }

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

    @JSBody(params = {"ref"}, script = "return ref.current;")
    private static native JSObject getCurrent(JSObject ref);

    @JSBody(params = {"ref", "value"}, script = "ref.current = value;")
    private static native void setCurrent(JSObject ref, JSObject value);

    @JSBody(params = {"ref"}, script = "return '' + ref.current;")
    private static native String getCurrentString(JSObject ref);

    @JSBody(params = {"ref"}, script = "return ref.current|0;")
    private static native int getCurrentInt(JSObject ref);
}

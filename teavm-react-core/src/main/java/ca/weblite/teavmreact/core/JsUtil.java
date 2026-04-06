package ca.weblite.teavmreact.core;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Low-level JS utility methods.
 */
public final class JsUtil {

    private JsUtil() {}

    @JSBody(params = {"callback", "ms"}, script = "return setInterval(callback, ms);")
    public static native int setInterval(VoidCallback callback, int ms);

    @JSBody(params = {"id"}, script = "clearInterval(id);")
    public static native void clearInterval(int id);

    @JSBody(params = {"callback", "ms"}, script = "return setTimeout(callback, ms);")
    public static native int setTimeout(VoidCallback callback, int ms);

    @JSBody(params = {"id"}, script = "clearTimeout(id);")
    public static native void clearTimeout(int id);

    @JSBody(params = {"msg"}, script = "console.log(msg);")
    public static native void consoleLog(String msg);

    @JSBody(params = {"obj"}, script = "console.log(obj);")
    public static native void consoleLog(JSObject obj);

    @JSBody(params = {"msg"}, script = "console.error(msg);")
    public static native void consoleError(String msg);

    @JSBody(params = {"msg"}, script = "alert(msg);")
    public static native void alert(String msg);
}

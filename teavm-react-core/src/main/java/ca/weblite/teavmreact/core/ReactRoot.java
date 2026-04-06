package ca.weblite.teavmreact.core;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

/**
 * React root created by ReactDOM.createRoot().
 */
public interface ReactRoot extends JSObject {
    @JSMethod
    void render(ReactElement element);

    @JSMethod
    void unmount();
}

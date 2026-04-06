package com.teavm.react.core;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSMethod;

/**
 * Represents a React root created by ReactDOM.createRoot().
 */
public interface ReactRoot extends JSObject {
    @JSMethod
    void render(ReactElement element);

    @JSMethod
    void unmount();
}

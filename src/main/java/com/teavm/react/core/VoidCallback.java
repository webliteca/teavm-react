package com.teavm.react.core;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * A no-argument callback. Used for useEffect cleanup functions, event handlers, etc.
 */
@JSFunctor
public interface VoidCallback extends JSObject {
    void call();
}

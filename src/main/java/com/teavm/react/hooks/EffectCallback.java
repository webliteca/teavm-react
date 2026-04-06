package com.teavm.react.hooks;

import com.teavm.react.core.VoidCallback;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Callback for useEffect. Returns an optional cleanup function.
 */
@JSFunctor
public interface EffectCallback extends JSObject {
    /**
     * Execute the effect. Return a cleanup function, or null if no cleanup is needed.
     */
    VoidCallback run();
}

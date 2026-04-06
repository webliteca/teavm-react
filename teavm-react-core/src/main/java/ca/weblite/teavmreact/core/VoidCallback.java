package ca.weblite.teavmreact.core;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * A no-argument callback. Used for useEffect cleanup, timers, etc.
 */
@JSFunctor
public interface VoidCallback extends JSObject {
    void call();
}

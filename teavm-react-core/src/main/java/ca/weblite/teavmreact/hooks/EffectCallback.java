package ca.weblite.teavmreact.hooks;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

import ca.weblite.teavmreact.core.VoidCallback;

/**
 * Callback for useEffect. Returns an optional cleanup function.
 * If no cleanup is needed, return null.
 */
@JSFunctor
public interface EffectCallback extends JSObject {
    VoidCallback run();
}

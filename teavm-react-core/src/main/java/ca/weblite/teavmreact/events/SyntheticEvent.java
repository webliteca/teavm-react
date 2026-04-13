package ca.weblite.teavmreact.events;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * Base interface for all React synthetic events. Provides access to
 * the event target and common event methods without exposing raw JSObject.
 */
public interface SyntheticEvent extends JSObject {
    @JSProperty
    EventTarget getTarget();

    @JSProperty
    String getType();

    @JSProperty
    boolean getBubbles();

    @JSBody(script = "this.preventDefault();")
    void preventDefault();

    @JSBody(script = "this.stopPropagation();")
    void stopPropagation();
}

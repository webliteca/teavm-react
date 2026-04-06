package com.teavm.react.events;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Generic event handler functor. Maps to a JavaScript function(event) callback.
 */
@JSFunctor
public interface EventHandler extends JSObject {
    void handleEvent(JSObject event);
}

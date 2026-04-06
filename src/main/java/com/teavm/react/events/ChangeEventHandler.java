package com.teavm.react.events;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Typed event handler specifically for change events.
 */
@JSFunctor
public interface ChangeEventHandler extends JSObject {
    void handleEvent(ChangeEvent event);
}

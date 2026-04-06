package com.teavm.react.events;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * React synthetic mouse event overlay.
 */
public interface MouseEvent extends JSObject {
    @JSProperty
    double getClientX();

    @JSProperty
    double getClientY();

    @JSProperty
    int getButton();

    @JSProperty
    JSObject getTarget();
}

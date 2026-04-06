package com.teavm.react.events;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * React synthetic change event overlay.
 */
public interface ChangeEvent extends JSObject {
    @JSProperty
    InputTarget getTarget();

    /**
     * Overlay for the event target element (typically an input).
     */
    interface InputTarget extends JSObject {
        @JSProperty
        String getValue();

        @JSProperty
        boolean getChecked();
    }
}

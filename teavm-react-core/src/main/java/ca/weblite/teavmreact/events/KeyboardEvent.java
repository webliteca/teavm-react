package ca.weblite.teavmreact.events;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface KeyboardEvent extends JSObject {
    @JSProperty
    String getKey();

    @JSProperty
    String getCode();

    @JSProperty
    boolean getAltKey();

    @JSProperty
    boolean getCtrlKey();

    @JSProperty
    boolean getMetaKey();

    @JSProperty
    boolean getShiftKey();

    @JSProperty
    boolean getRepeat();

    @JSProperty
    JSObject getTarget();
}

package ca.weblite.teavmreact.events;

import org.teavm.jso.JSProperty;

public interface KeyboardEvent extends SyntheticEvent {
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
}

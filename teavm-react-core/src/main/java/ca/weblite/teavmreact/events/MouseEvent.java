package ca.weblite.teavmreact.events;

import org.teavm.jso.JSProperty;

public interface MouseEvent extends SyntheticEvent {
    @JSProperty
    double getClientX();

    @JSProperty
    double getClientY();

    @JSProperty
    double getPageX();

    @JSProperty
    double getPageY();

    @JSProperty
    int getButton();

    @JSProperty
    boolean getAltKey();

    @JSProperty
    boolean getCtrlKey();

    @JSProperty
    boolean getMetaKey();

    @JSProperty
    boolean getShiftKey();
}

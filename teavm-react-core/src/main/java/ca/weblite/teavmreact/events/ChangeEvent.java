package ca.weblite.teavmreact.events;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface ChangeEvent extends JSObject {
    @JSProperty
    InputTarget getTarget();

    interface InputTarget extends JSObject {
        @JSProperty
        String getValue();

        @JSProperty
        boolean getChecked();

        @JSProperty
        String getType();
    }
}

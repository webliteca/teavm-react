package ca.weblite.teavmreact.events;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface ChangeEventHandler extends JSObject {
    void handleEvent(ChangeEvent event);
}

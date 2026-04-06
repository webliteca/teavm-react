package ca.weblite.teavmreact.events;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface KeyboardEventHandler extends JSObject {
    void handleEvent(KeyboardEvent event);
}

package ca.weblite.teavmreact.events;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface EventHandler extends JSObject {
    void handleEvent(JSObject event);
}

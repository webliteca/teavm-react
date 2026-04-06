package ca.weblite.teavmreact.events;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

@JSFunctor
public interface SubmitEventHandler extends JSObject {
    void handleEvent(JSObject event);
}

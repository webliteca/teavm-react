package ca.weblite.teavmreact.core;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLElement;

/**
 * Bindings to ReactDOM (React 18+ createRoot API).
 */
public final class ReactDOM {

    private ReactDOM() {}

    @JSBody(params = {"element"}, script = "return ReactDOM.createRoot(element);")
    public static native ReactRoot createRoot(HTMLElement element);
}

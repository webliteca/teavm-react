package com.teavm.react.core;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLElement;

/**
 * Bindings to ReactDOM (React 18+ createRoot API).
 * ReactDOM must be loaded via a script tag before the TeaVM-compiled JS runs.
 */
public final class ReactDOM {

    private ReactDOM() {}

    @JSBody(params = {"element"}, script =
            "return ReactDOM.createRoot(element);")
    public static native ReactRoot createRoot(HTMLElement element);
}

package com.teavm.react.core;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * A React functional component: a JavaScript function that takes props and returns a ReactElement.
 * This is the @JSFunctor that bridges Java lambdas to React component functions.
 */
@JSFunctor
public interface RenderFunction extends JSObject {
    ReactElement render(JSObject props);
}

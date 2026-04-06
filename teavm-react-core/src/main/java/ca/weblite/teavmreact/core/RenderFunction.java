package ca.weblite.teavmreact.core;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * A React functional component: a JS function taking props and returning a ReactElement.
 */
@JSFunctor
public interface RenderFunction extends JSObject {
    ReactElement render(JSObject props);
}

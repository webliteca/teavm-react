package ca.weblite.teavmreact.events;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * Typed interface for DOM event targets. Provides access to common
 * target element properties without exposing raw JSObject.
 */
public interface EventTarget extends JSObject {
    @JSProperty
    String getValue();

    @JSProperty
    boolean getChecked();

    @JSProperty
    String getType();

    @JSProperty
    String getId();

    @JSProperty
    String getTagName();

    @JSProperty
    String getClassName();
}

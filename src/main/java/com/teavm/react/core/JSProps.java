package com.teavm.react.core;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * Base interface for React component props.
 * Implemented as a JSO overlay type that maps directly to a JavaScript object.
 */
public interface JSProps extends JSObject {
    @JSProperty
    ReactNode getChildren();

    @JSProperty
    void setChildren(ReactNode children);

    @JSProperty
    String getKey();

    @JSProperty
    void setKey(String key);
}

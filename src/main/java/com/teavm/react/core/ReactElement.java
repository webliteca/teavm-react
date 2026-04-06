package com.teavm.react.core;

import org.teavm.jso.JSObject;

/**
 * Opaque wrapper for a React element (the return value of React.createElement).
 * Not meant to be constructed directly — returned by React.createElement and Html DSL methods.
 */
public interface ReactElement extends JSObject {
}

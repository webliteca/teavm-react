package com.teavm.react.html;

import com.teavm.react.core.React;
import com.teavm.react.core.ReactElement;
import com.teavm.react.events.ChangeEventHandler;
import com.teavm.react.events.EventHandler;
import org.teavm.jso.JSObject;

/**
 * Builder for HTML elements that need props (button, input, a, etc.).
 * Supports method chaining for setting props before building the element.
 *
 * Used by both Approach A (functional) for interactive elements
 * and internally by Approach B (builder DSL).
 */
public class ElementBuilder {
    private final String tag;
    private final String textContent;
    private final JSObject props;

    ElementBuilder(String tag, String textContent) {
        this.tag = tag;
        this.textContent = textContent;
        this.props = React.createObject();
    }

    // --- Common props ---

    public ElementBuilder className(String className) {
        React.setProperty(props, "className", className);
        return this;
    }

    public ElementBuilder id(String id) {
        React.setProperty(props, "id", id);
        return this;
    }

    public ElementBuilder key(String key) {
        React.setProperty(props, "key", key);
        return this;
    }

    public ElementBuilder key(int key) {
        React.setProperty(props, "key", "" + key);
        return this;
    }

    // --- Event handlers ---

    public ElementBuilder onClick(EventHandler handler) {
        React.setOnClick(props, handler);
        return this;
    }

    public ElementBuilder onChange(ChangeEventHandler handler) {
        React.setOnChange(props, handler);
        return this;
    }

    // --- Input-specific props ---

    public ElementBuilder value(String value) {
        React.setProperty(props, "value", value);
        return this;
    }

    public ElementBuilder placeholder(String placeholder) {
        React.setProperty(props, "placeholder", placeholder);
        return this;
    }

    public ElementBuilder disabled(boolean disabled) {
        React.setProperty(props, "disabled", disabled);
        return this;
    }

    // --- Link-specific props ---

    public ElementBuilder href(String href) {
        React.setProperty(props, "href", href);
        return this;
    }

    // --- Image-specific props ---

    public ElementBuilder src(String src) {
        React.setProperty(props, "src", src);
        return this;
    }

    public ElementBuilder alt(String alt) {
        React.setProperty(props, "alt", alt);
        return this;
    }

    // --- Style ---

    public ElementBuilder style(JSObject style) {
        React.setProperty(props, "style", style);
        return this;
    }

    // --- Generic prop setter ---

    public ElementBuilder prop(String name, String value) {
        React.setProperty(props, name, value);
        return this;
    }

    public ElementBuilder prop(String name, int value) {
        React.setProperty(props, name, value);
        return this;
    }

    public ElementBuilder prop(String name, boolean value) {
        React.setProperty(props, name, value);
        return this;
    }

    public ElementBuilder prop(String name, JSObject value) {
        React.setProperty(props, name, value);
        return this;
    }

    // --- Build (implicit via casting or explicit) ---

    /**
     * Build the ReactElement. Called implicitly when the builder is used
     * where a ReactElement is expected.
     */
    public ReactElement build() {
        if (textContent != null) {
            return React.createElementWithText(tag, props, textContent);
        } else {
            return React.createElement(tag, props);
        }
    }

    /**
     * Build with child elements.
     */
    public ReactElement build(ReactElement... children) {
        JSObject[] arr = new JSObject[children.length];
        System.arraycopy(children, 0, arr, 0, children.length);
        return React.createElement(tag, props, arr);
    }

    /**
     * Implicit conversion to ReactElement for use in varargs contexts.
     * Unfortunately Java doesn't support implicit conversions, so this
     * needs to be called explicitly. See the static helper in Html.
     */
    public static ReactElement[] toElements(Object... items) {
        ReactElement[] result = new ReactElement[items.length];
        for (int i = 0; i < items.length; i++) {
            Object item = items[i];
            if (item instanceof ReactElement) {
                result[i] = (ReactElement) item;
            } else if (item instanceof ElementBuilder) {
                result[i] = ((ElementBuilder) item).build();
            } else if (item instanceof String) {
                result[i] = Html.text((String) item);
            } else {
                result[i] = Html.text("" + item);
            }
        }
        return result;
    }
}

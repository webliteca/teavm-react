package ca.weblite.teavmreact.html;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.events.ChangeEventHandler;
import ca.weblite.teavmreact.events.EventHandler;
import ca.weblite.teavmreact.events.FocusEventHandler;
import ca.weblite.teavmreact.events.KeyboardEventHandler;
import ca.weblite.teavmreact.events.SubmitEventHandler;
import org.teavm.jso.JSObject;

/**
 * Builder for interactive HTML elements that need props such as event handlers,
 * values, and attributes. Used by both the functional DSL ({@link Html}) and
 * the fluent builder ({@link DomBuilder}) approaches.
 *
 * <p>Event handlers are set via dedicated {@code React.setOnClick} /
 * {@code React.setOnChange} methods (NOT via {@code React.setProperty} cast to
 * JSObject) to ensure the raw JS function reference is preserved.
 */
public final class ElementBuilder {

    private final String tag;
    private final String textContent;
    private final JSObject props;

    ElementBuilder(String tag, String textContent) {
        this.tag = tag;
        this.textContent = textContent;
        this.props = React.createObject();
    }

    // -----------------------------------------------------------------------
    // Common attributes
    // -----------------------------------------------------------------------

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
        React.setProperty(props, "key", key);
        return this;
    }

    // -----------------------------------------------------------------------
    // Event handlers — MUST use React.setOn* (not setProperty with cast)
    // -----------------------------------------------------------------------

    public ElementBuilder onClick(EventHandler handler) {
        React.setOnClick(props, handler);
        return this;
    }

    public ElementBuilder onChange(ChangeEventHandler handler) {
        React.setOnChange(props, handler);
        return this;
    }

    public ElementBuilder onKeyDown(KeyboardEventHandler handler) {
        React.setOnKeyDown(props, handler);
        return this;
    }

    public ElementBuilder onKeyUp(KeyboardEventHandler handler) {
        React.setOnKeyUp(props, handler);
        return this;
    }

    public ElementBuilder onFocus(FocusEventHandler handler) {
        React.setOnFocus(props, handler);
        return this;
    }

    public ElementBuilder onBlur(FocusEventHandler handler) {
        React.setOnBlur(props, handler);
        return this;
    }

    public ElementBuilder onSubmit(SubmitEventHandler handler) {
        React.setOnSubmit(props, handler);
        return this;
    }

    public ElementBuilder onMouseDown(EventHandler handler) {
        React.setOnMouseDown(props, handler);
        return this;
    }

    public ElementBuilder onMouseUp(EventHandler handler) {
        React.setOnMouseUp(props, handler);
        return this;
    }

    public ElementBuilder onMouseEnter(EventHandler handler) {
        React.setOnMouseEnter(props, handler);
        return this;
    }

    public ElementBuilder onMouseLeave(EventHandler handler) {
        React.setOnMouseLeave(props, handler);
        return this;
    }

    // -----------------------------------------------------------------------
    // Form / input attributes
    // -----------------------------------------------------------------------

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

    public ElementBuilder checked(boolean checked) {
        React.setProperty(props, "checked", checked);
        return this;
    }

    public ElementBuilder readOnly(boolean readOnly) {
        React.setProperty(props, "readOnly", readOnly);
        return this;
    }

    // -----------------------------------------------------------------------
    // Link / media attributes
    // -----------------------------------------------------------------------

    public ElementBuilder href(String href) {
        React.setProperty(props, "href", href);
        return this;
    }

    public ElementBuilder src(String src) {
        React.setProperty(props, "src", src);
        return this;
    }

    public ElementBuilder alt(String alt) {
        React.setProperty(props, "alt", alt);
        return this;
    }

    public ElementBuilder target(String target) {
        React.setProperty(props, "target", target);
        return this;
    }

    // -----------------------------------------------------------------------
    // Misc attributes
    // -----------------------------------------------------------------------

    public ElementBuilder type(String type) {
        React.setProperty(props, "type", type);
        return this;
    }

    public ElementBuilder name(String name) {
        React.setProperty(props, "name", name);
        return this;
    }

    public ElementBuilder htmlFor(String htmlFor) {
        React.setProperty(props, "htmlFor", htmlFor);
        return this;
    }

    public ElementBuilder tabIndex(int tabIndex) {
        React.setProperty(props, "tabIndex", tabIndex);
        return this;
    }

    public ElementBuilder rows(int rows) {
        React.setProperty(props, "rows", rows);
        return this;
    }

    public ElementBuilder cols(int cols) {
        React.setProperty(props, "cols", cols);
        return this;
    }

    public ElementBuilder maxLength(int maxLength) {
        React.setProperty(props, "maxLength", maxLength);
        return this;
    }

    public ElementBuilder minLength(int minLength) {
        React.setProperty(props, "minLength", minLength);
        return this;
    }

    // -----------------------------------------------------------------------
    // Style
    // -----------------------------------------------------------------------

    public ElementBuilder style(JSObject style) {
        React.setProperty(props, "style", style);
        return this;
    }

    // -----------------------------------------------------------------------
    // Generic property setters
    // -----------------------------------------------------------------------

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

    // -----------------------------------------------------------------------
    // Build
    // -----------------------------------------------------------------------

    /**
     * Builds the ReactElement with no additional children.
     * If textContent was provided it is rendered as the sole child.
     */
    public ReactElement build() {
        if (textContent != null) {
            return React.createElementWithText(tag, props, textContent);
        }
        return React.createElement(tag, props);
    }

    /**
     * Builds the ReactElement with the given children appended after any
     * text content.
     */
    public ReactElement build(ReactElement... children) {
        if (children == null || children.length == 0) {
            return build();
        }
        JSObject jsChildren = React.createArray();
        for (int i = 0; i < children.length; i++) {
            React.arrayPush(jsChildren, children[i]);
        }
        return React.createElementFromArray(tag, props, jsChildren);
    }
}

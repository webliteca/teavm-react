package com.teavm.react.html;

import com.teavm.react.core.React;
import com.teavm.react.core.ReactElement;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import java.util.List;
import java.util.function.Function;

/**
 * Approach A: Functional HTML DSL.
 *
 * Usage:
 *   import static com.teavm.react.html.Html.*;
 *   div(h1("Hello"), p("World"))
 */
public final class Html {

    private Html() {}

    // --- Element creation with text content ---

    public static ReactElement div(String text) {
        return React.createElementWithText("div", null, text);
    }

    public static ReactElement h1(String text) {
        return React.createElementWithText("h1", null, text);
    }

    public static ReactElement h2(String text) {
        return React.createElementWithText("h2", null, text);
    }

    public static ReactElement h3(String text) {
        return React.createElementWithText("h3", null, text);
    }

    public static ReactElement p(String text) {
        return React.createElementWithText("p", null, text);
    }

    public static ReactElement span(String text) {
        return React.createElementWithText("span", null, text);
    }

    public static ReactElement li(String text) {
        return React.createElementWithText("li", null, text);
    }

    // --- Element creation with children ---

    public static ReactElement div(ReactElement... children) {
        return React.createElement("div", null, toJSArray(children));
    }

    public static ReactElement h1(ReactElement... children) {
        return React.createElement("h1", null, toJSArray(children));
    }

    public static ReactElement h2(ReactElement... children) {
        return React.createElement("h2", null, toJSArray(children));
    }

    public static ReactElement h3(ReactElement... children) {
        return React.createElement("h3", null, toJSArray(children));
    }

    public static ReactElement p(ReactElement... children) {
        return React.createElement("p", null, toJSArray(children));
    }

    public static ReactElement span(ReactElement... children) {
        return React.createElement("span", null, toJSArray(children));
    }

    public static ReactElement ul(ReactElement... children) {
        return React.createElement("ul", null, toJSArray(children));
    }

    public static ReactElement ol(ReactElement... children) {
        return React.createElement("ol", null, toJSArray(children));
    }

    public static ReactElement li(ReactElement... children) {
        return React.createElement("li", null, toJSArray(children));
    }

    public static ReactElement section(ReactElement... children) {
        return React.createElement("section", null, toJSArray(children));
    }

    public static ReactElement header(ReactElement... children) {
        return React.createElement("header", null, toJSArray(children));
    }

    public static ReactElement footer(ReactElement... children) {
        return React.createElement("footer", null, toJSArray(children));
    }

    public static ReactElement nav(ReactElement... children) {
        return React.createElement("nav", null, toJSArray(children));
    }

    public static ReactElement main(ReactElement... children) {
        return React.createElement("main", null, toJSArray(children));
    }

    // --- Element creation with props ---

    public static ReactElement div(JSObject props, ReactElement... children) {
        return React.createElement("div", props, toJSArray(children));
    }

    public static ReactElement div(JSObject props, String text) {
        return React.createElementWithText("div", props, text);
    }

    public static ReactElement span(JSObject props, String text) {
        return React.createElementWithText("span", props, text);
    }

    public static ReactElement p(JSObject props, String text) {
        return React.createElementWithText("p", props, text);
    }

    // --- Interactive elements returning builders (for chaining onClick, etc.) ---

    public static ElementBuilder button(String text) {
        return new ElementBuilder("button", text);
    }

    public static ElementBuilder input(String type) {
        ElementBuilder builder = new ElementBuilder("input", null);
        builder.prop("type", type);
        return builder;
    }

    public static ElementBuilder a(String text) {
        return new ElementBuilder("a", text);
    }

    // --- Component rendering ---

    /**
     * Render a component (JSObject wrapping a RenderFunction).
     */
    public static ReactElement component(JSObject component) {
        return React.createElement(component, null);
    }

    public static ReactElement component(JSObject component, JSObject props) {
        return React.createElement(component, props);
    }

    // --- List helpers ---

    /**
     * Map a Java list to an array of ReactElements.
     * Similar to items.map(fn) in JSX.
     */
    public static <T> ReactElement[] mapToElements(List<T> items, Function<T, ReactElement> mapper) {
        return items.stream().map(mapper).toArray(ReactElement[]::new);
    }

    // --- Text node ---

    @JSBody(params = {"text"}, script = "return text;")
    public static native ReactElement text(String text);

    // --- Internal helpers ---

    private static JSObject[] toJSArray(ReactElement[] elements) {
        JSObject[] arr = new JSObject[elements.length];
        System.arraycopy(elements, 0, arr, 0, elements.length);
        return arr;
    }
}

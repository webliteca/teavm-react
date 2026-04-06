package ca.weblite.teavmreact.html;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactElement;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import java.util.List;
import java.util.function.Function;

/**
 * Functional DSL for building React element trees from Java.
 *
 * <p>Every standard HTML element has two static factory methods:
 * <ul>
 *   <li>{@code div("text")} — creates an element with text content</li>
 *   <li>{@code div(child1, child2)} — creates an element with children</li>
 * </ul>
 *
 * <p>Interactive elements (button, input, a, textarea, select, img) return an
 * {@link ElementBuilder} for chaining props such as onClick, value, etc.
 *
 * <p>Usage example:
 * <pre>
 * import static ca.weblite.teavmreact.html.Html.*;
 *
 * div(
 *     h1("Hello World"),
 *     button("Click me").onClick(e -&gt; { ... }).build()
 * );
 * </pre>
 */
public final class Html {

    private Html() {}

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static JSObject[] toJSArray(ReactElement[] elements) {
        JSObject[] arr = new JSObject[elements.length];
        for (int i = 0; i < elements.length; i++) {
            arr[i] = elements[i];
        }
        return arr;
    }

    // -----------------------------------------------------------------------
    // text() — returns a plain text node
    // -----------------------------------------------------------------------

    @JSBody(params = {"s"}, script = "return s;")
    public static native ReactElement text(String s);

    // -----------------------------------------------------------------------
    // Layout elements
    // -----------------------------------------------------------------------

    public static ReactElement div(String text) {
        return React.createElementWithText("div", null, text);
    }

    public static ReactElement div(ReactElement... children) {
        return React.createElement("div", null, toJSArray(children));
    }

    public static ReactElement span(String text) {
        return React.createElementWithText("span", null, text);
    }

    public static ReactElement span(ReactElement... children) {
        return React.createElement("span", null, toJSArray(children));
    }

    public static ReactElement section(String text) {
        return React.createElementWithText("section", null, text);
    }

    public static ReactElement section(ReactElement... children) {
        return React.createElement("section", null, toJSArray(children));
    }

    public static ReactElement article(String text) {
        return React.createElementWithText("article", null, text);
    }

    public static ReactElement article(ReactElement... children) {
        return React.createElement("article", null, toJSArray(children));
    }

    public static ReactElement aside(String text) {
        return React.createElementWithText("aside", null, text);
    }

    public static ReactElement aside(ReactElement... children) {
        return React.createElement("aside", null, toJSArray(children));
    }

    public static ReactElement header(String text) {
        return React.createElementWithText("header", null, text);
    }

    public static ReactElement header(ReactElement... children) {
        return React.createElement("header", null, toJSArray(children));
    }

    public static ReactElement footer(String text) {
        return React.createElementWithText("footer", null, text);
    }

    public static ReactElement footer(ReactElement... children) {
        return React.createElement("footer", null, toJSArray(children));
    }

    public static ReactElement main(String text) {
        return React.createElementWithText("main", null, text);
    }

    public static ReactElement main(ReactElement... children) {
        return React.createElement("main", null, toJSArray(children));
    }

    public static ReactElement nav(String text) {
        return React.createElementWithText("nav", null, text);
    }

    public static ReactElement nav(ReactElement... children) {
        return React.createElement("nav", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Headings
    // -----------------------------------------------------------------------

    public static ReactElement h1(String text) {
        return React.createElementWithText("h1", null, text);
    }

    public static ReactElement h1(ReactElement... children) {
        return React.createElement("h1", null, toJSArray(children));
    }

    public static ReactElement h2(String text) {
        return React.createElementWithText("h2", null, text);
    }

    public static ReactElement h2(ReactElement... children) {
        return React.createElement("h2", null, toJSArray(children));
    }

    public static ReactElement h3(String text) {
        return React.createElementWithText("h3", null, text);
    }

    public static ReactElement h3(ReactElement... children) {
        return React.createElement("h3", null, toJSArray(children));
    }

    public static ReactElement h4(String text) {
        return React.createElementWithText("h4", null, text);
    }

    public static ReactElement h4(ReactElement... children) {
        return React.createElement("h4", null, toJSArray(children));
    }

    public static ReactElement h5(String text) {
        return React.createElementWithText("h5", null, text);
    }

    public static ReactElement h5(ReactElement... children) {
        return React.createElement("h5", null, toJSArray(children));
    }

    public static ReactElement h6(String text) {
        return React.createElementWithText("h6", null, text);
    }

    public static ReactElement h6(ReactElement... children) {
        return React.createElement("h6", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Text elements
    // -----------------------------------------------------------------------

    public static ReactElement p(String text) {
        return React.createElementWithText("p", null, text);
    }

    public static ReactElement p(ReactElement... children) {
        return React.createElement("p", null, toJSArray(children));
    }

    public static ReactElement pre(String text) {
        return React.createElementWithText("pre", null, text);
    }

    public static ReactElement pre(ReactElement... children) {
        return React.createElement("pre", null, toJSArray(children));
    }

    public static ReactElement code(String text) {
        return React.createElementWithText("code", null, text);
    }

    public static ReactElement code(ReactElement... children) {
        return React.createElement("code", null, toJSArray(children));
    }

    public static ReactElement blockquote(String text) {
        return React.createElementWithText("blockquote", null, text);
    }

    public static ReactElement blockquote(ReactElement... children) {
        return React.createElement("blockquote", null, toJSArray(children));
    }

    public static ReactElement em(String text) {
        return React.createElementWithText("em", null, text);
    }

    public static ReactElement em(ReactElement... children) {
        return React.createElement("em", null, toJSArray(children));
    }

    public static ReactElement strong(String text) {
        return React.createElementWithText("strong", null, text);
    }

    public static ReactElement strong(ReactElement... children) {
        return React.createElement("strong", null, toJSArray(children));
    }

    public static ReactElement small(String text) {
        return React.createElementWithText("small", null, text);
    }

    public static ReactElement small(ReactElement... children) {
        return React.createElement("small", null, toJSArray(children));
    }

    public static ReactElement sub(String text) {
        return React.createElementWithText("sub", null, text);
    }

    public static ReactElement sub(ReactElement... children) {
        return React.createElement("sub", null, toJSArray(children));
    }

    public static ReactElement sup(String text) {
        return React.createElementWithText("sup", null, text);
    }

    public static ReactElement sup(ReactElement... children) {
        return React.createElement("sup", null, toJSArray(children));
    }

    public static ReactElement mark(String text) {
        return React.createElementWithText("mark", null, text);
    }

    public static ReactElement mark(ReactElement... children) {
        return React.createElement("mark", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Lists
    // -----------------------------------------------------------------------

    public static ReactElement ul(String text) {
        return React.createElementWithText("ul", null, text);
    }

    public static ReactElement ul(ReactElement... children) {
        return React.createElement("ul", null, toJSArray(children));
    }

    public static ReactElement ol(String text) {
        return React.createElementWithText("ol", null, text);
    }

    public static ReactElement ol(ReactElement... children) {
        return React.createElement("ol", null, toJSArray(children));
    }

    public static ReactElement li(String text) {
        return React.createElementWithText("li", null, text);
    }

    public static ReactElement li(ReactElement... children) {
        return React.createElement("li", null, toJSArray(children));
    }

    public static ReactElement dl(String text) {
        return React.createElementWithText("dl", null, text);
    }

    public static ReactElement dl(ReactElement... children) {
        return React.createElement("dl", null, toJSArray(children));
    }

    public static ReactElement dt(String text) {
        return React.createElementWithText("dt", null, text);
    }

    public static ReactElement dt(ReactElement... children) {
        return React.createElement("dt", null, toJSArray(children));
    }

    public static ReactElement dd(String text) {
        return React.createElementWithText("dd", null, text);
    }

    public static ReactElement dd(ReactElement... children) {
        return React.createElement("dd", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Table
    // -----------------------------------------------------------------------

    public static ReactElement table(String text) {
        return React.createElementWithText("table", null, text);
    }

    public static ReactElement table(ReactElement... children) {
        return React.createElement("table", null, toJSArray(children));
    }

    public static ReactElement thead(String text) {
        return React.createElementWithText("thead", null, text);
    }

    public static ReactElement thead(ReactElement... children) {
        return React.createElement("thead", null, toJSArray(children));
    }

    public static ReactElement tbody(String text) {
        return React.createElementWithText("tbody", null, text);
    }

    public static ReactElement tbody(ReactElement... children) {
        return React.createElement("tbody", null, toJSArray(children));
    }

    public static ReactElement tfoot(String text) {
        return React.createElementWithText("tfoot", null, text);
    }

    public static ReactElement tfoot(ReactElement... children) {
        return React.createElement("tfoot", null, toJSArray(children));
    }

    public static ReactElement tr(String text) {
        return React.createElementWithText("tr", null, text);
    }

    public static ReactElement tr(ReactElement... children) {
        return React.createElement("tr", null, toJSArray(children));
    }

    public static ReactElement th(String text) {
        return React.createElementWithText("th", null, text);
    }

    public static ReactElement th(ReactElement... children) {
        return React.createElement("th", null, toJSArray(children));
    }

    public static ReactElement td(String text) {
        return React.createElementWithText("td", null, text);
    }

    public static ReactElement td(ReactElement... children) {
        return React.createElement("td", null, toJSArray(children));
    }

    public static ReactElement caption(String text) {
        return React.createElementWithText("caption", null, text);
    }

    public static ReactElement caption(ReactElement... children) {
        return React.createElement("caption", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Form (non-interactive containers)
    // -----------------------------------------------------------------------

    public static ReactElement form(String text) {
        return React.createElementWithText("form", null, text);
    }

    public static ReactElement form(ReactElement... children) {
        return React.createElement("form", null, toJSArray(children));
    }

    public static ReactElement fieldset(String text) {
        return React.createElementWithText("fieldset", null, text);
    }

    public static ReactElement fieldset(ReactElement... children) {
        return React.createElement("fieldset", null, toJSArray(children));
    }

    public static ReactElement legend(String text) {
        return React.createElementWithText("legend", null, text);
    }

    public static ReactElement legend(ReactElement... children) {
        return React.createElement("legend", null, toJSArray(children));
    }

    public static ReactElement label(String text) {
        return React.createElementWithText("label", null, text);
    }

    public static ReactElement label(ReactElement... children) {
        return React.createElement("label", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Media
    // -----------------------------------------------------------------------

    public static ReactElement figure(String text) {
        return React.createElementWithText("figure", null, text);
    }

    public static ReactElement figure(ReactElement... children) {
        return React.createElement("figure", null, toJSArray(children));
    }

    public static ReactElement figcaption(String text) {
        return React.createElementWithText("figcaption", null, text);
    }

    public static ReactElement figcaption(ReactElement... children) {
        return React.createElement("figcaption", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Void elements (no children)
    // -----------------------------------------------------------------------

    public static ReactElement hr() {
        return React.createElement("hr", null);
    }

    public static ReactElement br() {
        return React.createElement("br", null);
    }

    // -----------------------------------------------------------------------
    // Details / Summary
    // -----------------------------------------------------------------------

    public static ReactElement details(String text) {
        return React.createElementWithText("details", null, text);
    }

    public static ReactElement details(ReactElement... children) {
        return React.createElement("details", null, toJSArray(children));
    }

    public static ReactElement summary(String text) {
        return React.createElementWithText("summary", null, text);
    }

    public static ReactElement summary(ReactElement... children) {
        return React.createElement("summary", null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Interactive elements — return ElementBuilder for chaining
    // -----------------------------------------------------------------------

    /**
     * Creates a button element builder with the given text content.
     */
    public static ElementBuilder button(String text) {
        return new ElementBuilder("button", text);
    }

    /**
     * Creates an input element builder with the given type attribute.
     */
    public static ElementBuilder input(String type) {
        return new ElementBuilder("input", null).type(type);
    }

    /**
     * Creates an anchor element builder with the given text content.
     */
    public static ElementBuilder a(String text) {
        return new ElementBuilder("a", text);
    }

    /**
     * Creates a textarea element builder.
     */
    public static ElementBuilder textarea() {
        return new ElementBuilder("textarea", null);
    }

    /**
     * Creates a select element builder.
     */
    public static ElementBuilder select() {
        return new ElementBuilder("select", null);
    }

    /**
     * Creates an img element builder (chain with .src() and .alt()).
     */
    public static ElementBuilder img() {
        return new ElementBuilder("img", null);
    }

    // -----------------------------------------------------------------------
    // Component helpers
    // -----------------------------------------------------------------------

    /**
     * Renders a React component with no props.
     */
    public static ReactElement component(JSObject component) {
        return React.createElement(component, null);
    }

    /**
     * Renders a React component with the given props.
     */
    public static ReactElement component(JSObject component, JSObject props) {
        return React.createElement(component, props);
    }

    // -----------------------------------------------------------------------
    // Fragment
    // -----------------------------------------------------------------------

    /**
     * Wraps children in a React.Fragment (no extra DOM node).
     */
    public static ReactElement fragment(ReactElement... children) {
        return React.createElement(React.fragment(), null, toJSArray(children));
    }

    // -----------------------------------------------------------------------
    // Collection mapping
    // -----------------------------------------------------------------------

    /**
     * Maps a list of items to an array of ReactElements.
     * Useful for rendering lists: {@code mapToElements(items, item -> li(item.name()))}.
     */
    public static <T> ReactElement[] mapToElements(List<T> list, Function<T, ReactElement> mapper) {
        ReactElement[] result = new ReactElement[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = mapper.apply(list.get(i));
        }
        return result;
    }
}

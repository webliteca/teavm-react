package com.teavm.react.html;

import com.teavm.react.core.React;
import com.teavm.react.core.ReactElement;
import com.teavm.react.events.ChangeEventHandler;
import com.teavm.react.events.EventHandler;
import org.teavm.jso.JSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Approach B: Fluent builder DSL for HTML elements.
 *
 * Usage:
 *   Div.create()
 *       .className("card")
 *       .child(H1.create().text("Title"))
 *       .child(P.create().text("Body"))
 *       .build()
 *
 * This style is familiar to Swing/JavaFX developers who are used to
 * building component trees imperatively.
 */
public class DomBuilder {
    private final String tag;
    private String textContent;
    private final JSObject props;
    private final List<ReactElement> children = new ArrayList<>();

    protected DomBuilder(String tag) {
        this.tag = tag;
        this.props = React.createObject();
    }

    // --- Content ---

    public DomBuilder text(String text) {
        this.textContent = text;
        return this;
    }

    // --- Children ---

    public DomBuilder child(ReactElement child) {
        children.add(child);
        return this;
    }

    public DomBuilder child(DomBuilder childBuilder) {
        children.add(childBuilder.build());
        return this;
    }

    public DomBuilder child(ElementBuilder childBuilder) {
        children.add(childBuilder.build());
        return this;
    }

    /**
     * Iterate over a list, mapping each item to a child element.
     * Similar to Jetpack Compose's `items()` or React's `.map()`.
     */
    public <T> DomBuilder forEach(List<T> items, Function<T, DomBuilder> mapper) {
        for (T item : items) {
            children.add(mapper.apply(item).build());
        }
        return this;
    }

    /**
     * forEach variant that accepts ReactElement results.
     */
    public <T> DomBuilder forEachElement(List<T> items, Function<T, ReactElement> mapper) {
        for (T item : items) {
            children.add(mapper.apply(item));
        }
        return this;
    }

    // --- Props ---

    public DomBuilder className(String className) {
        React.setProperty(props, "className", className);
        return this;
    }

    public DomBuilder id(String id) {
        React.setProperty(props, "id", id);
        return this;
    }

    public DomBuilder key(String key) {
        React.setProperty(props, "key", key);
        return this;
    }

    public DomBuilder key(int key) {
        React.setProperty(props, "key", "" + key);
        return this;
    }

    public DomBuilder style(JSObject style) {
        React.setProperty(props, "style", style);
        return this;
    }

    // --- Events ---

    public DomBuilder onClick(EventHandler handler) {
        React.setOnClick(props, handler);
        return this;
    }

    public DomBuilder onChange(ChangeEventHandler handler) {
        React.setOnChange(props, handler);
        return this;
    }

    // --- Input-specific ---

    public DomBuilder type(String type) {
        React.setProperty(props, "type", type);
        return this;
    }

    public DomBuilder value(String value) {
        React.setProperty(props, "value", value);
        return this;
    }

    public DomBuilder placeholder(String placeholder) {
        React.setProperty(props, "placeholder", placeholder);
        return this;
    }

    public DomBuilder disabled(boolean disabled) {
        React.setProperty(props, "disabled", disabled);
        return this;
    }

    // --- Link / image ---

    public DomBuilder href(String href) {
        React.setProperty(props, "href", href);
        return this;
    }

    public DomBuilder src(String src) {
        React.setProperty(props, "src", src);
        return this;
    }

    public DomBuilder alt(String alt) {
        React.setProperty(props, "alt", alt);
        return this;
    }

    // --- Generic prop ---

    public DomBuilder prop(String name, String value) {
        React.setProperty(props, name, value);
        return this;
    }

    public DomBuilder prop(String name, JSObject value) {
        React.setProperty(props, name, value);
        return this;
    }

    // --- Build ---

    public ReactElement build() {
        if (textContent != null && children.isEmpty()) {
            return React.createElementWithText(tag, props, textContent);
        } else if (children.isEmpty()) {
            return React.createElement(tag, props);
        } else {
            if (textContent != null) {
                // Prepend text as first child
                List<JSObject> all = new ArrayList<>();
                all.add(Html.text(textContent));
                all.addAll(children);
                return React.createElement(tag, props, all.toArray(new JSObject[0]));
            }
            return React.createElement(tag, props, children.toArray(new JSObject[0]));
        }
    }

    // --- Named factory subclasses for readability ---

    public static class Div extends DomBuilder {
        private Div() { super("div"); }
        public static Div create() { return new Div(); }
    }

    public static class H1 extends DomBuilder {
        private H1() { super("h1"); }
        public static H1 create() { return new H1(); }
    }

    public static class H2 extends DomBuilder {
        private H2() { super("h2"); }
        public static H2 create() { return new H2(); }
    }

    public static class H3 extends DomBuilder {
        private H3() { super("h3"); }
        public static H3 create() { return new H3(); }
    }

    public static class P extends DomBuilder {
        private P() { super("p"); }
        public static P create() { return new P(); }
    }

    public static class Span extends DomBuilder {
        private Span() { super("span"); }
        public static Span create() { return new Span(); }
    }

    public static class Ul extends DomBuilder {
        private Ul() { super("ul"); }
        public static Ul create() { return new Ul(); }
    }

    public static class Ol extends DomBuilder {
        private Ol() { super("ol"); }
        public static Ol create() { return new Ol(); }
    }

    public static class Li extends DomBuilder {
        private Li() { super("li"); }
        public static Li create() { return new Li(); }
    }

    public static class Button extends DomBuilder {
        private Button() { super("button"); }
        public static Button create() { return new Button(); }
    }

    public static class Input extends DomBuilder {
        private Input() { super("input"); }
        public static Input create() { return new Input(); }
        public static Input text() { Input i = new Input(); i.type("text"); return i; }
        public static Input password() { Input i = new Input(); i.type("password"); return i; }
        public static Input checkbox() { Input i = new Input(); i.type("checkbox"); return i; }
    }

    public static class A extends DomBuilder {
        private A() { super("a"); }
        public static A create() { return new A(); }
    }

    public static class Section extends DomBuilder {
        private Section() { super("section"); }
        public static Section create() { return new Section(); }
    }

    public static class Header extends DomBuilder {
        private Header() { super("header"); }
        public static Header create() { return new Header(); }
    }

    public static class Hr extends DomBuilder {
        private Hr() { super("hr"); }
        public static Hr create() { return new Hr(); }
    }
}

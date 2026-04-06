package ca.weblite.teavmreact.html;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.events.ChangeEventHandler;
import ca.weblite.teavmreact.events.EventHandler;
import ca.weblite.teavmreact.events.FocusEventHandler;
import ca.weblite.teavmreact.events.KeyboardEventHandler;
import ca.weblite.teavmreact.events.SubmitEventHandler;
import org.teavm.jso.JSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Fluent builder for constructing React element trees using a {@code .child()}
 * chaining pattern. Each HTML element type is represented by an inner static
 * subclass with a {@code create()} factory method.
 *
 * <p>Usage example:
 * <pre>
 * Div.create()
 *     .className("container")
 *     .child(H1.create().text("Hello World").build())
 *     .child(Button.create()
 *         .text("Click me")
 *         .onClick(e -&gt; { ... })
 *         .build())
 *     .build();
 * </pre>
 *
 * <p>Event handlers are set via dedicated {@code React.setOnClick} /
 * {@code React.setOnChange} methods (NOT via {@code React.setProperty} cast to
 * JSObject) to ensure the raw JS function reference is preserved.
 */
public class DomBuilder {

    private final String tag;
    private String textContent;
    private JSObject props;
    private List<ReactElement> children;

    protected DomBuilder(String tag) {
        this.tag = tag;
    }

    private JSObject ensureProps() {
        if (props == null) {
            props = React.createObject();
        }
        return props;
    }

    private List<ReactElement> ensureChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    // -----------------------------------------------------------------------
    // Text content
    // -----------------------------------------------------------------------

    public DomBuilder text(String text) {
        this.textContent = text;
        return this;
    }

    // -----------------------------------------------------------------------
    // Child methods
    // -----------------------------------------------------------------------

    public DomBuilder child(ReactElement element) {
        ensureChildren().add(element);
        return this;
    }

    public DomBuilder child(DomBuilder builder) {
        ensureChildren().add(builder.build());
        return this;
    }

    public DomBuilder child(ElementBuilder builder) {
        ensureChildren().add(builder.build());
        return this;
    }

    /**
     * Maps each item in the list to a DomBuilder and adds the built elements
     * as children.
     */
    public <T> DomBuilder forEach(List<T> list, Function<T, DomBuilder> mapper) {
        for (T item : list) {
            ensureChildren().add(mapper.apply(item).build());
        }
        return this;
    }

    /**
     * Maps each item in the list to a ReactElement and adds them as children.
     */
    public <T> DomBuilder forEachElement(List<T> list, Function<T, ReactElement> mapper) {
        for (T item : list) {
            ensureChildren().add(mapper.apply(item));
        }
        return this;
    }

    // -----------------------------------------------------------------------
    // Common attributes
    // -----------------------------------------------------------------------

    public DomBuilder className(String className) {
        React.setProperty(ensureProps(), "className", className);
        return this;
    }

    public DomBuilder id(String id) {
        React.setProperty(ensureProps(), "id", id);
        return this;
    }

    public DomBuilder key(String key) {
        React.setProperty(ensureProps(), "key", key);
        return this;
    }

    public DomBuilder key(int key) {
        React.setProperty(ensureProps(), "key", key);
        return this;
    }

    public DomBuilder style(JSObject style) {
        React.setProperty(ensureProps(), "style", style);
        return this;
    }

    // -----------------------------------------------------------------------
    // Event handlers — MUST use React.setOn* (not setProperty with cast)
    // -----------------------------------------------------------------------

    public DomBuilder onClick(EventHandler handler) {
        React.setOnClick(ensureProps(), handler);
        return this;
    }

    public DomBuilder onChange(ChangeEventHandler handler) {
        React.setOnChange(ensureProps(), handler);
        return this;
    }

    public DomBuilder onKeyDown(KeyboardEventHandler handler) {
        React.setOnKeyDown(ensureProps(), handler);
        return this;
    }

    public DomBuilder onFocus(FocusEventHandler handler) {
        React.setOnFocus(ensureProps(), handler);
        return this;
    }

    public DomBuilder onBlur(FocusEventHandler handler) {
        React.setOnBlur(ensureProps(), handler);
        return this;
    }

    public DomBuilder onSubmit(SubmitEventHandler handler) {
        React.setOnSubmit(ensureProps(), handler);
        return this;
    }

    // -----------------------------------------------------------------------
    // Form / input attributes
    // -----------------------------------------------------------------------

    public DomBuilder value(String value) {
        React.setProperty(ensureProps(), "value", value);
        return this;
    }

    public DomBuilder placeholder(String placeholder) {
        React.setProperty(ensureProps(), "placeholder", placeholder);
        return this;
    }

    public DomBuilder disabled(boolean disabled) {
        React.setProperty(ensureProps(), "disabled", disabled);
        return this;
    }

    public DomBuilder type(String type) {
        React.setProperty(ensureProps(), "type", type);
        return this;
    }

    // -----------------------------------------------------------------------
    // Link / media attributes
    // -----------------------------------------------------------------------

    public DomBuilder href(String href) {
        React.setProperty(ensureProps(), "href", href);
        return this;
    }

    public DomBuilder src(String src) {
        React.setProperty(ensureProps(), "src", src);
        return this;
    }

    public DomBuilder alt(String alt) {
        React.setProperty(ensureProps(), "alt", alt);
        return this;
    }

    // -----------------------------------------------------------------------
    // Generic property setters
    // -----------------------------------------------------------------------

    public DomBuilder prop(String name, String value) {
        React.setProperty(ensureProps(), name, value);
        return this;
    }

    public DomBuilder prop(String name, JSObject value) {
        React.setProperty(ensureProps(), name, value);
        return this;
    }

    // -----------------------------------------------------------------------
    // Build
    // -----------------------------------------------------------------------

    /**
     * Builds the ReactElement. If text content was set and no children were
     * added, the text is rendered as the sole child. If children were added,
     * they are rendered (text content is ignored when children are present).
     */
    public ReactElement build() {
        if (children != null && !children.isEmpty()) {
            JSObject[] jsChildren = new JSObject[children.size()];
            for (int i = 0; i < children.size(); i++) {
                jsChildren[i] = children.get(i);
            }
            return React.createElement(tag, props, jsChildren);
        }
        if (textContent != null) {
            return React.createElementWithText(tag, props, textContent);
        }
        return React.createElement(tag, props);
    }

    // =======================================================================
    // Inner static subclasses — one per HTML element type
    // =======================================================================

    // -----------------------------------------------------------------------
    // Layout elements
    // -----------------------------------------------------------------------

    public static final class Div extends DomBuilder {
        private Div() { super("div"); }
        public static Div create() { return new Div(); }
    }

    public static final class Span extends DomBuilder {
        private Span() { super("span"); }
        public static Span create() { return new Span(); }
    }

    public static final class Section extends DomBuilder {
        private Section() { super("section"); }
        public static Section create() { return new Section(); }
    }

    public static final class Article extends DomBuilder {
        private Article() { super("article"); }
        public static Article create() { return new Article(); }
    }

    public static final class Aside extends DomBuilder {
        private Aside() { super("aside"); }
        public static Aside create() { return new Aside(); }
    }

    public static final class Header extends DomBuilder {
        private Header() { super("header"); }
        public static Header create() { return new Header(); }
    }

    public static final class Footer extends DomBuilder {
        private Footer() { super("footer"); }
        public static Footer create() { return new Footer(); }
    }

    public static final class Main extends DomBuilder {
        private Main() { super("main"); }
        public static Main create() { return new Main(); }
    }

    public static final class Nav extends DomBuilder {
        private Nav() { super("nav"); }
        public static Nav create() { return new Nav(); }
    }

    // -----------------------------------------------------------------------
    // Headings
    // -----------------------------------------------------------------------

    public static final class H1 extends DomBuilder {
        private H1() { super("h1"); }
        public static H1 create() { return new H1(); }
    }

    public static final class H2 extends DomBuilder {
        private H2() { super("h2"); }
        public static H2 create() { return new H2(); }
    }

    public static final class H3 extends DomBuilder {
        private H3() { super("h3"); }
        public static H3 create() { return new H3(); }
    }

    public static final class H4 extends DomBuilder {
        private H4() { super("h4"); }
        public static H4 create() { return new H4(); }
    }

    public static final class H5 extends DomBuilder {
        private H5() { super("h5"); }
        public static H5 create() { return new H5(); }
    }

    public static final class H6 extends DomBuilder {
        private H6() { super("h6"); }
        public static H6 create() { return new H6(); }
    }

    // -----------------------------------------------------------------------
    // Text elements
    // -----------------------------------------------------------------------

    public static final class P extends DomBuilder {
        private P() { super("p"); }
        public static P create() { return new P(); }
    }

    public static final class Pre extends DomBuilder {
        private Pre() { super("pre"); }
        public static Pre create() { return new Pre(); }
    }

    public static final class Code extends DomBuilder {
        private Code() { super("code"); }
        public static Code create() { return new Code(); }
    }

    public static final class Blockquote extends DomBuilder {
        private Blockquote() { super("blockquote"); }
        public static Blockquote create() { return new Blockquote(); }
    }

    public static final class Em extends DomBuilder {
        private Em() { super("em"); }
        public static Em create() { return new Em(); }
    }

    public static final class Strong extends DomBuilder {
        private Strong() { super("strong"); }
        public static Strong create() { return new Strong(); }
    }

    public static final class Small extends DomBuilder {
        private Small() { super("small"); }
        public static Small create() { return new Small(); }
    }

    // -----------------------------------------------------------------------
    // Lists
    // -----------------------------------------------------------------------

    public static final class Ul extends DomBuilder {
        private Ul() { super("ul"); }
        public static Ul create() { return new Ul(); }
    }

    public static final class Ol extends DomBuilder {
        private Ol() { super("ol"); }
        public static Ol create() { return new Ol(); }
    }

    public static final class Li extends DomBuilder {
        private Li() { super("li"); }
        public static Li create() { return new Li(); }
    }

    public static final class Dl extends DomBuilder {
        private Dl() { super("dl"); }
        public static Dl create() { return new Dl(); }
    }

    public static final class Dt extends DomBuilder {
        private Dt() { super("dt"); }
        public static Dt create() { return new Dt(); }
    }

    public static final class Dd extends DomBuilder {
        private Dd() { super("dd"); }
        public static Dd create() { return new Dd(); }
    }

    // -----------------------------------------------------------------------
    // Table
    // -----------------------------------------------------------------------

    public static final class Table extends DomBuilder {
        private Table() { super("table"); }
        public static Table create() { return new Table(); }
    }

    public static final class Thead extends DomBuilder {
        private Thead() { super("thead"); }
        public static Thead create() { return new Thead(); }
    }

    public static final class Tbody extends DomBuilder {
        private Tbody() { super("tbody"); }
        public static Tbody create() { return new Tbody(); }
    }

    public static final class Tfoot extends DomBuilder {
        private Tfoot() { super("tfoot"); }
        public static Tfoot create() { return new Tfoot(); }
    }

    public static final class Tr extends DomBuilder {
        private Tr() { super("tr"); }
        public static Tr create() { return new Tr(); }
    }

    public static final class Th extends DomBuilder {
        private Th() { super("th"); }
        public static Th create() { return new Th(); }
    }

    public static final class Td extends DomBuilder {
        private Td() { super("td"); }
        public static Td create() { return new Td(); }
    }

    public static final class Caption extends DomBuilder {
        private Caption() { super("caption"); }
        public static Caption create() { return new Caption(); }
    }

    // -----------------------------------------------------------------------
    // Form elements
    // -----------------------------------------------------------------------

    public static final class Form extends DomBuilder {
        private Form() { super("form"); }
        public static Form create() { return new Form(); }
    }

    public static final class Fieldset extends DomBuilder {
        private Fieldset() { super("fieldset"); }
        public static Fieldset create() { return new Fieldset(); }
    }

    public static final class Legend extends DomBuilder {
        private Legend() { super("legend"); }
        public static Legend create() { return new Legend(); }
    }

    public static final class Label extends DomBuilder {
        private Label() { super("label"); }
        public static Label create() { return new Label(); }
    }

    // -----------------------------------------------------------------------
    // Interactive elements
    // -----------------------------------------------------------------------

    public static final class Button extends DomBuilder {
        private Button() { super("button"); }
        public static Button create() { return new Button(); }
    }

    public static final class Input extends DomBuilder {
        private Input() { super("input"); }
        public static Input create() { return new Input(); }

        /** Creates a text input. */
        public static Input text() {
            Input input = new Input();
            input.type("text");
            return input;
        }

        /** Creates a password input. */
        public static Input password() {
            Input input = new Input();
            input.type("password");
            return input;
        }

        /** Creates a checkbox input. */
        public static Input checkbox() {
            Input input = new Input();
            input.type("checkbox");
            return input;
        }

        /** Creates a number input. */
        public static Input number() {
            Input input = new Input();
            input.type("number");
            return input;
        }
    }

    public static final class Select extends DomBuilder {
        private Select() { super("select"); }
        public static Select create() { return new Select(); }
    }

    public static final class Textarea extends DomBuilder {
        private Textarea() { super("textarea"); }
        public static Textarea create() { return new Textarea(); }
    }

    // -----------------------------------------------------------------------
    // Link / media
    // -----------------------------------------------------------------------

    public static final class A extends DomBuilder {
        private A() { super("a"); }
        public static A create() { return new A(); }
    }

    public static final class Img extends DomBuilder {
        private Img() { super("img"); }
        public static Img create() { return new Img(); }
    }

    public static final class Figure extends DomBuilder {
        private Figure() { super("figure"); }
        public static Figure create() { return new Figure(); }
    }

    public static final class Figcaption extends DomBuilder {
        private Figcaption() { super("figcaption"); }
        public static Figcaption create() { return new Figcaption(); }
    }

    // -----------------------------------------------------------------------
    // Void / misc elements
    // -----------------------------------------------------------------------

    public static final class Hr extends DomBuilder {
        private Hr() { super("hr"); }
        public static Hr create() { return new Hr(); }
    }

    public static final class Br extends DomBuilder {
        private Br() { super("br"); }
        public static Br create() { return new Br(); }
    }

    public static final class Details extends DomBuilder {
        private Details() { super("details"); }
        public static Details create() { return new Details(); }
    }

    public static final class Summary extends DomBuilder {
        private Summary() { super("summary"); }
        public static Summary create() { return new Summary(); }
    }
}

# API Signatures Reference

> Read this file to find the exact class name, method name, and parameter types
> for any teavm-react API. This prevents hallucinating methods that don't exist.
> Grouped by package. Kotlin DSL functions are at the end.

## ca.weblite.teavmreact.core

### JsUtil

    public final class JsUtil {

-     public static native int setInterval(VoidCallback callback, int ms)
-     public static native void clearInterval(int id)
-     public static native int setTimeout(VoidCallback callback, int ms)
-     public static native void clearTimeout(int id)
-     public static native void consoleLog(String msg)
-     public static native void consoleLog(JSObject obj)
-     public static native void consoleError(String msg)
-     public static native void alert(String msg)

### React

    public final class React {

-     public static native ReactElement createElement(String type, JSObject props)
-     public static native ReactElement createElement(String type, JSObject props, JSObject[] children)
-     public static native ReactElement createElement(JSObject type, JSObject props)
-     public static native ReactElement createElement(JSObject type, JSObject props, JSObject[] children)
-     public static native ReactElement createElement(String type, JSObject props, JSObject child)
-     public static native ReactElement createElementWithText(String type, JSObject props, String text)
-     public static native JSObject wrapComponent(RenderFunction renderFn)
-     public static native JSObject wrapComponent(RenderFunction renderFn, String name)
-     public static native JSObject createObject()
-     public static native JSObject getProperty(JSObject obj, String key)
-     public static native void setProperty(JSObject obj, String key, JSObject value)
-     public static native void setProperty(JSObject obj, String key, String value)
-     public static native void setProperty(JSObject obj, String key, int value)
-     public static native void setProperty(JSObject obj, String key, boolean value)
-     public static native void setProperty(JSObject obj, String key, double value)
-     public static native void setOnClick(JSObject obj, EventHandler handler)
-     public static native void setOnChange(JSObject obj, ChangeEventHandler handler)
-     public static native void setOnKeyDown(JSObject obj, KeyboardEventHandler handler)
-     public static native void setOnKeyUp(JSObject obj, KeyboardEventHandler handler)
-     public static native void setOnFocus(JSObject obj, FocusEventHandler handler)
-     public static native void setOnBlur(JSObject obj, FocusEventHandler handler)
-     public static native void setOnSubmit(JSObject obj, SubmitEventHandler handler)
-     public static native void setOnMouseDown(JSObject obj, EventHandler handler)
-     public static native void setOnMouseUp(JSObject obj, EventHandler handler)
-     public static native void setOnMouseEnter(JSObject obj, EventHandler handler)
-     public static native void setOnMouseLeave(JSObject obj, EventHandler handler)
-     public static native JSObject createArray()
-     public static native void arrayPush(JSObject arr, JSObject element)
-     public static native ReactElement createElementFromArray(String type, JSObject props, JSObject childrenArray)
-     public static native ReactElement createElementFromArray(JSObject type, JSObject props, JSObject childrenArray)
-     public static native JSObject createContext()
-     public static native JSObject createContext(JSObject defaultValue)
-     public static native JSObject memo(JSObject component)
-     public static native JSObject fragment()
-     public static native JSObject stringToJS(String s)
-     public static native JSObject intToJS(int n)
-     public static native JSObject boolToJS(boolean b)
-     public static native String jsToString(JSObject obj)
-     public static native int jsToInt(JSObject obj)
-     public static native boolean jsToBool(JSObject obj)

### Fetch

    public final class Fetch {

-     public static void get(String url, Callback onSuccess, ErrorCallback onError)
-     public static void post(String url, String body, String contentType, Callback onSuccess, ErrorCallback onError)
-     public static void put(String url, String body, String contentType, Callback onSuccess, ErrorCallback onError)
-     public static void patch(String url, String body, String contentType, Callback onSuccess, ErrorCallback onError)
-     public static void delete(String url, Callback onSuccess, ErrorCallback onError)
-     public static void request(String method, String url, Callback onSuccess, ErrorCallback onError)
-     public static void request(String method, String url, String body, String contentType, Callback onSuccess, ErrorCallback onError)

    // Inner callback interfaces (plain Java, no JSObject)

    @FunctionalInterface
    public interface Callback {
-       void onResponse(String body, int status)
    }

    @FunctionalInterface
    public interface ErrorCallback {
-       void onError(String message)
    }

### ReactContext

    public class ReactContext {

-     public static ReactContext create()
-     public static ReactContext create(String defaultValue)
-     public static ReactContext create(int defaultValue)
-     public static ReactContext create(boolean defaultValue)
-     public String useString()
-     public int useInt()
-     public boolean useBool()
-     public ReactElement provide(String value, ReactElement... children)
-     public ReactElement provide(int value, ReactElement... children)
-     public ReactElement provide(boolean value, ReactElement... children)

### ReactDOM

    public final class ReactDOM {

-     public static native ReactRoot createRoot(HTMLElement element)

### ReactElement

    public interface ReactElement extends JSObject {


### ReactNode

    public interface ReactNode extends JSObject {


### ReactRoot

    public interface ReactRoot extends JSObject {


### RenderFunction

    public interface RenderFunction extends JSObject {


### VoidCallback

    public interface VoidCallback extends JSObject {


## ca.weblite.teavmreact.hooks

### EffectCallback

    public interface EffectCallback extends JSObject {


### Hooks

    public final class Hooks {

-     public static StateHandle<Integer> useState(int initial)
-     public static StateHandle<String> useState(String initial)
-     public static StateHandle<Boolean> useState(boolean initial)
-     public static StateHandle<Double> useState(double initial)
-     public static void useEffect(EffectCallback effect)
-     public static void useEffectOnMount(EffectCallback effect)
-     public static void useEffect(EffectCallback effect, JSObject[] deps)
-     public static RefHandle useRef(JSObject initial)
-     public static RefHandle useRefInt(int initial)
-     public static RefHandle useRefString(String initial)
-     public static JSObject useMemo(MemoFactory factory, JSObject[] deps)
-     public static JSObject useCallback(JSObject callback, JSObject[] deps)
-     public static JSObject[] useReducer(JSObject reducer, JSObject initialState)
-     public static JSObject useContext(JSObject context)
-     public static JSObject[] deps()
-     public static JSObject[] deps(JSObject... items)

### RefHandle

    public class RefHandle {

-     public String getCurrentString()
-     public int getCurrentInt()
-     public boolean getCurrentBool()
-     public double getCurrentDouble()
-     public void setCurrentString(String value)
-     public void setCurrentInt(int value)
-     public void setCurrentBool(boolean value)
-     public void setCurrentDouble(double value)

### StateHandle

    public class StateHandle<T> {

-     public String getString()
-     public int getInt()
-     public boolean getBool()
-     public double getDouble()
-     public void setInt(int value)
-     public void setString(String value)
-     public void setBool(boolean value)
-     public void setDouble(double value)
-     public void updateInt(IntUpdater updater)
-     public void updateString(StringUpdater updater)

## ca.weblite.teavmreact.events

### ChangeEvent

    public interface ChangeEvent extends SyntheticEvent {


### ChangeEventHandler

    public interface ChangeEventHandler extends JSObject {


### EventHandler

    public interface EventHandler extends JSObject {


### EventTarget

    public interface EventTarget extends JSObject {


### FocusEvent

    public interface FocusEvent extends SyntheticEvent {


### FocusEventHandler

    public interface FocusEventHandler extends JSObject {


### KeyboardEvent

    public interface KeyboardEvent extends SyntheticEvent {


### KeyboardEventHandler

    public interface KeyboardEventHandler extends JSObject {


### MouseEvent

    public interface MouseEvent extends SyntheticEvent {


### SubmitEvent

    public interface SubmitEvent extends SyntheticEvent {


### SubmitEventHandler

    public interface SubmitEventHandler extends JSObject {


### SyntheticEvent

    public interface SyntheticEvent extends JSObject {


## ca.weblite.teavmreact.html

### DomBuilder

    public class DomBuilder {

-     protected DomBuilder(String tag)
-     public DomBuilder text(String text)
-     public DomBuilder child(ReactElement element)
-     public DomBuilder child(DomBuilder builder)
-     public DomBuilder child(ElementBuilder builder)
-     public <T> DomBuilder forEach(List<T> list, Function<T, DomBuilder> mapper)
-     public <T> DomBuilder forEachElement(List<T> list, Function<T, ReactElement> mapper)
-     public DomBuilder className(String className)
-     public DomBuilder id(String id)
-     public DomBuilder key(String key)
-     public DomBuilder key(int key)
-     public DomBuilder style(Style style)
-     public DomBuilder onClick(EventHandler handler)
-     public DomBuilder onChange(ChangeEventHandler handler)
-     public DomBuilder onKeyDown(KeyboardEventHandler handler)
-     public DomBuilder onFocus(FocusEventHandler handler)
-     public DomBuilder onBlur(FocusEventHandler handler)
-     public DomBuilder onSubmit(SubmitEventHandler handler)
-     public DomBuilder value(String value)
-     public DomBuilder placeholder(String placeholder)
-     public DomBuilder disabled(boolean disabled)
-     public DomBuilder type(String type)
-     public DomBuilder href(String href)
-     public DomBuilder src(String src)
-     public DomBuilder alt(String alt)
-     public DomBuilder prop(String name, String value)
-     public DomBuilder prop(String name, int value)
-     public DomBuilder prop(String name, boolean value)
-     public DomBuilder prop(String name, double value)
-     public ReactElement build()
-     public static final class Div extends DomBuilder
-         public static Div create() { return new Div(); }
-     public static final class Span extends DomBuilder
-         public static Span create() { return new Span(); }
-     public static final class Section extends DomBuilder
-         public static Section create() { return new Section(); }
-     public static final class Article extends DomBuilder
-         public static Article create() { return new Article(); }
-     public static final class Aside extends DomBuilder
-         public static Aside create() { return new Aside(); }
-     public static final class Header extends DomBuilder
-         public static Header create() { return new Header(); }
-     public static final class Footer extends DomBuilder
-         public static Footer create() { return new Footer(); }
-     public static final class Main extends DomBuilder
-         public static Main create() { return new Main(); }
-     public static final class Nav extends DomBuilder
-         public static Nav create() { return new Nav(); }
-     public static final class H1 extends DomBuilder
-         public static H1 create() { return new H1(); }
-     public static final class H2 extends DomBuilder
-         public static H2 create() { return new H2(); }
-     public static final class H3 extends DomBuilder
-         public static H3 create() { return new H3(); }
-     public static final class H4 extends DomBuilder
-         public static H4 create() { return new H4(); }
-     public static final class H5 extends DomBuilder
-         public static H5 create() { return new H5(); }
-     public static final class H6 extends DomBuilder
-         public static H6 create() { return new H6(); }
-     public static final class P extends DomBuilder
-         public static P create() { return new P(); }
-     public static final class Pre extends DomBuilder
-         public static Pre create() { return new Pre(); }
-     public static final class Code extends DomBuilder
-         public static Code create() { return new Code(); }
-     public static final class Blockquote extends DomBuilder
-         public static Blockquote create() { return new Blockquote(); }
-     public static final class Em extends DomBuilder
-         public static Em create() { return new Em(); }
-     public static final class Strong extends DomBuilder
-         public static Strong create() { return new Strong(); }
-     public static final class Small extends DomBuilder
-         public static Small create() { return new Small(); }
-     public static final class Ul extends DomBuilder
-         public static Ul create() { return new Ul(); }
-     public static final class Ol extends DomBuilder
-         public static Ol create() { return new Ol(); }
-     public static final class Li extends DomBuilder
-         public static Li create() { return new Li(); }
-     public static final class Dl extends DomBuilder
-         public static Dl create() { return new Dl(); }
-     public static final class Dt extends DomBuilder
-         public static Dt create() { return new Dt(); }
-     public static final class Dd extends DomBuilder
-         public static Dd create() { return new Dd(); }
-     public static final class Table extends DomBuilder
-         public static Table create() { return new Table(); }
-     public static final class Thead extends DomBuilder
-         public static Thead create() { return new Thead(); }
-     public static final class Tbody extends DomBuilder
-         public static Tbody create() { return new Tbody(); }
-     public static final class Tfoot extends DomBuilder
-         public static Tfoot create() { return new Tfoot(); }
-     public static final class Tr extends DomBuilder
-         public static Tr create() { return new Tr(); }
-     public static final class Th extends DomBuilder
-         public static Th create() { return new Th(); }
-     public static final class Td extends DomBuilder
-         public static Td create() { return new Td(); }
-     public static final class Caption extends DomBuilder
-         public static Caption create() { return new Caption(); }
-     public static final class Form extends DomBuilder
-         public static Form create() { return new Form(); }
-     public static final class Fieldset extends DomBuilder
-         public static Fieldset create() { return new Fieldset(); }
-     public static final class Legend extends DomBuilder
-         public static Legend create() { return new Legend(); }
-     public static final class Label extends DomBuilder
-         public static Label create() { return new Label(); }
-     public static final class Button extends DomBuilder
-         public static Button create() { return new Button(); }
-     public static final class Input extends DomBuilder
-         public static Input create() { return new Input(); }
-         public static Input text()
-         public static Input password()
-         public static Input checkbox()
-         public static Input number()
-     public static final class Select extends DomBuilder
-         public static Select create() { return new Select(); }
-     public static final class Textarea extends DomBuilder
-         public static Textarea create() { return new Textarea(); }
-     public static final class A extends DomBuilder
-         public static A create() { return new A(); }
-     public static final class Img extends DomBuilder
-         public static Img create() { return new Img(); }
-     public static final class Figure extends DomBuilder
-         public static Figure create() { return new Figure(); }
-     public static final class Figcaption extends DomBuilder
-         public static Figcaption create() { return new Figcaption(); }
-     public static final class Hr extends DomBuilder
-         public static Hr create() { return new Hr(); }
-     public static final class Br extends DomBuilder
-         public static Br create() { return new Br(); }
-     public static final class Details extends DomBuilder
-         public static Details create() { return new Details(); }
-     public static final class Summary extends DomBuilder
-         public static Summary create() { return new Summary(); }

### ElementBuilder

    public final class ElementBuilder {

-     public ElementBuilder className(String className)
-     public ElementBuilder id(String id)
-     public ElementBuilder key(String key)
-     public ElementBuilder key(int key)
-     public ElementBuilder onClick(EventHandler handler)
-     public ElementBuilder onChange(ChangeEventHandler handler)
-     public ElementBuilder onKeyDown(KeyboardEventHandler handler)
-     public ElementBuilder onKeyUp(KeyboardEventHandler handler)
-     public ElementBuilder onFocus(FocusEventHandler handler)
-     public ElementBuilder onBlur(FocusEventHandler handler)
-     public ElementBuilder onSubmit(SubmitEventHandler handler)
-     public ElementBuilder onMouseDown(EventHandler handler)
-     public ElementBuilder onMouseUp(EventHandler handler)
-     public ElementBuilder onMouseEnter(EventHandler handler)
-     public ElementBuilder onMouseLeave(EventHandler handler)
-     public ElementBuilder value(String value)
-     public ElementBuilder placeholder(String placeholder)
-     public ElementBuilder disabled(boolean disabled)
-     public ElementBuilder checked(boolean checked)
-     public ElementBuilder readOnly(boolean readOnly)
-     public ElementBuilder href(String href)
-     public ElementBuilder src(String src)
-     public ElementBuilder alt(String alt)
-     public ElementBuilder target(String target)
-     public ElementBuilder type(String type)
-     public ElementBuilder name(String name)
-     public ElementBuilder htmlFor(String htmlFor)
-     public ElementBuilder tabIndex(int tabIndex)
-     public ElementBuilder rows(int rows)
-     public ElementBuilder cols(int cols)
-     public ElementBuilder maxLength(int maxLength)
-     public ElementBuilder minLength(int minLength)
-     public ElementBuilder style(Style style)
-     public ElementBuilder prop(String name, String value)
-     public ElementBuilder prop(String name, int value)
-     public ElementBuilder prop(String name, boolean value)
-     public ElementBuilder prop(String name, double value)
-     public ReactElement build()
-     public ReactElement build(ReactElement... children)

### Html

    public final class Html {

-     public static native ReactElement text(String s)
-     public static ReactElement div(String text)
-     public static ReactElement div(ReactElement... children)
-     public static ReactElement span(String text)
-     public static ReactElement span(ReactElement... children)
-     public static ReactElement section(String text)
-     public static ReactElement section(ReactElement... children)
-     public static ReactElement article(String text)
-     public static ReactElement article(ReactElement... children)
-     public static ReactElement aside(String text)
-     public static ReactElement aside(ReactElement... children)
-     public static ReactElement header(String text)
-     public static ReactElement header(ReactElement... children)
-     public static ReactElement footer(String text)
-     public static ReactElement footer(ReactElement... children)
-     public static ReactElement main(String text)
-     public static ReactElement main(ReactElement... children)
-     public static ReactElement nav(String text)
-     public static ReactElement nav(ReactElement... children)
-     public static ReactElement h1(String text)
-     public static ReactElement h1(ReactElement... children)
-     public static ReactElement h2(String text)
-     public static ReactElement h2(ReactElement... children)
-     public static ReactElement h3(String text)
-     public static ReactElement h3(ReactElement... children)
-     public static ReactElement h4(String text)
-     public static ReactElement h4(ReactElement... children)
-     public static ReactElement h5(String text)
-     public static ReactElement h5(ReactElement... children)
-     public static ReactElement h6(String text)
-     public static ReactElement h6(ReactElement... children)
-     public static ReactElement p(String text)
-     public static ReactElement p(ReactElement... children)
-     public static ReactElement pre(String text)
-     public static ReactElement pre(ReactElement... children)
-     public static ReactElement code(String text)
-     public static ReactElement code(ReactElement... children)
-     public static ReactElement blockquote(String text)
-     public static ReactElement blockquote(ReactElement... children)
-     public static ReactElement em(String text)
-     public static ReactElement em(ReactElement... children)
-     public static ReactElement strong(String text)
-     public static ReactElement strong(ReactElement... children)
-     public static ReactElement small(String text)
-     public static ReactElement small(ReactElement... children)
-     public static ReactElement sub(String text)
-     public static ReactElement sub(ReactElement... children)
-     public static ReactElement sup(String text)
-     public static ReactElement sup(ReactElement... children)
-     public static ReactElement mark(String text)
-     public static ReactElement mark(ReactElement... children)
-     public static ReactElement ul(String text)
-     public static ReactElement ul(ReactElement... children)
-     public static ReactElement ol(String text)
-     public static ReactElement ol(ReactElement... children)
-     public static ReactElement li(String text)
-     public static ReactElement li(ReactElement... children)
-     public static ReactElement dl(String text)
-     public static ReactElement dl(ReactElement... children)
-     public static ReactElement dt(String text)
-     public static ReactElement dt(ReactElement... children)
-     public static ReactElement dd(String text)
-     public static ReactElement dd(ReactElement... children)
-     public static ReactElement table(String text)
-     public static ReactElement table(ReactElement... children)
-     public static ReactElement thead(String text)
-     public static ReactElement thead(ReactElement... children)
-     public static ReactElement tbody(String text)
-     public static ReactElement tbody(ReactElement... children)
-     public static ReactElement tfoot(String text)
-     public static ReactElement tfoot(ReactElement... children)
-     public static ReactElement tr(String text)
-     public static ReactElement tr(ReactElement... children)
-     public static ReactElement th(String text)
-     public static ReactElement th(ReactElement... children)
-     public static ReactElement td(String text)
-     public static ReactElement td(ReactElement... children)
-     public static ReactElement caption(String text)
-     public static ReactElement caption(ReactElement... children)
-     public static ReactElement form(String text)
-     public static ReactElement form(ReactElement... children)
-     public static ReactElement fieldset(String text)
-     public static ReactElement fieldset(ReactElement... children)
-     public static ReactElement legend(String text)
-     public static ReactElement legend(ReactElement... children)
-     public static ReactElement label(String text)
-     public static ReactElement label(ReactElement... children)
-     public static ReactElement figure(String text)
-     public static ReactElement figure(ReactElement... children)
-     public static ReactElement figcaption(String text)
-     public static ReactElement figcaption(ReactElement... children)
-     public static ReactElement hr()
-     public static ReactElement br()
-     public static ReactElement details(String text)
-     public static ReactElement details(ReactElement... children)
-     public static ReactElement summary(String text)
-     public static ReactElement summary(ReactElement... children)
-     public static ElementBuilder button(String text)
-     public static ElementBuilder input(String type)
-     public static ElementBuilder a(String text)
-     public static ElementBuilder textarea()
-     public static ElementBuilder select()
-     public static ElementBuilder img()
-     public static ReactElement component(RenderFunction fn)
-     public static ReactElement component(RenderFunction fn, String displayName)
-     public static ReactElement component(JSObject wrappedComponent)
-     public static ReactElement fragment(ReactElement... children)
-     public static <T> ReactElement[] mapToElements(List<T> list, Function<T, ReactElement> mapper)

### Style

    public class Style {

-     public static Style create()
-     public Style set(String property, String value)
-     public Style set(String property, int value)
-     public Style set(String property, double value)
-     public Style background(String value) { return set("background", value); }
-     public Style backgroundColor(String value) { return set("backgroundColor", value); }
-     public Style color(String value) { return set("color", value); }
-     public Style opacity(double value) { return set("opacity", value); }
-     public Style padding(String value) { return set("padding", value); }
-     public Style paddingTop(String value) { return set("paddingTop", value); }
-     public Style paddingRight(String value) { return set("paddingRight", value); }
-     public Style paddingBottom(String value) { return set("paddingBottom", value); }
-     public Style paddingLeft(String value) { return set("paddingLeft", value); }
-     public Style margin(String value) { return set("margin", value); }
-     public Style marginTop(String value) { return set("marginTop", value); }
-     public Style marginRight(String value) { return set("marginRight", value); }
-     public Style marginBottom(String value) { return set("marginBottom", value); }
-     public Style marginLeft(String value) { return set("marginLeft", value); }
-     public Style border(String value) { return set("border", value); }
-     public Style borderRadius(String value) { return set("borderRadius", value); }
-     public Style borderColor(String value) { return set("borderColor", value); }
-     public Style width(String value) { return set("width", value); }
-     public Style height(String value) { return set("height", value); }
-     public Style minWidth(String value) { return set("minWidth", value); }
-     public Style minHeight(String value) { return set("minHeight", value); }
-     public Style maxWidth(String value) { return set("maxWidth", value); }
-     public Style maxHeight(String value) { return set("maxHeight", value); }
-     public Style display(String value) { return set("display", value); }
-     public Style position(String value) { return set("position", value); }
-     public Style top(String value) { return set("top", value); }
-     public Style right(String value) { return set("right", value); }
-     public Style bottom(String value) { return set("bottom", value); }
-     public Style left(String value) { return set("left", value); }
-     public Style zIndex(int value) { return set("zIndex", value); }
-     public Style flexDirection(String value) { return set("flexDirection", value); }
-     public Style justifyContent(String value) { return set("justifyContent", value); }
-     public Style alignItems(String value) { return set("alignItems", value); }
-     public Style flexWrap(String value) { return set("flexWrap", value); }
-     public Style flex(String value) { return set("flex", value); }
-     public Style gap(String value) { return set("gap", value); }
-     public Style fontSize(String value) { return set("fontSize", value); }
-     public Style fontWeight(String value) { return set("fontWeight", value); }
-     public Style fontFamily(String value) { return set("fontFamily", value); }
-     public Style textAlign(String value) { return set("textAlign", value); }
-     public Style textDecoration(String value) { return set("textDecoration", value); }
-     public Style lineHeight(String value) { return set("lineHeight", value); }
-     public Style letterSpacing(String value) { return set("letterSpacing", value); }
-     public Style overflow(String value) { return set("overflow", value); }
-     public Style overflowX(String value) { return set("overflowX", value); }
-     public Style overflowY(String value) { return set("overflowY", value); }
-     public Style cursor(String value) { return set("cursor", value); }
-     public Style transition(String value) { return set("transition", value); }
-     public Style transform(String value) { return set("transform", value); }
-     public Style boxShadow(String value) { return set("boxShadow", value); }

## ca.weblite.teavmreact.component

### ReactView

    public abstract class ReactView {

-     protected abstract ReactElement render()
-     protected void onMount() {}
-     protected void onUnmount() {}
-     public static ReactElement view(ViewFactory factory, String displayName)
-     public static ReactElement view(ViewFactory factory)

## ca.weblite.teavmreact.kotlin

### ComponentScope

- class ComponentScope(@PublishedApi internal val props: JSObject) {
- fun fc(name: String = "", render: ComponentScope.() -> ReactElement): JSObject {
- fun component(comp: JSObject): ReactElement = React.createElement(comp, null as JSObject?)
- fun component(comp: JSObject, props: JSObject): ReactElement = React.createElement(comp, props)

### EffectScope

- class EffectScope(private val parentJob: Job) : CoroutineScope {
- fun ComponentScope.effect(
- fun ComponentScope.effectOnce(block: EffectScope.() -> Unit) {
- fun ComponentScope.launchedEffect(
- fun ComponentScope.effect(
- fun ComponentScope.effectOnce(block: EffectScope.() -> Unit) {
- fun ComponentScope.launchedEffect(

### FlowExtensions

- class CollectedIntState(
- class CollectedStringState(
- class CollectedBooleanState(
- class CollectedDoubleState(
- class ProduceStateScope<T>(
- fun Flow<Int>.collectAsState(initial: Int): CollectedIntState {
- fun Flow<String>.collectAsState(initial: String): CollectedStringState {
- fun Flow<Boolean>.collectAsState(initial: Boolean): CollectedBooleanState {
- fun Flow<Double>.collectAsState(initial: Double): CollectedDoubleState {
- fun ComponentScope.produceState(
- fun ComponentScope.produceState(
- fun Flow<Int>.collectAsState(initial: Int): CollectedIntState {
- fun Flow<String>.collectAsState(initial: String): CollectedStringState {
- fun Flow<Boolean>.collectAsState(initial: Boolean): CollectedBooleanState {
- fun Flow<Double>.collectAsState(initial: Double): CollectedDoubleState {
- fun ComponentScope.produceState(
- fun ComponentScope.produceState(

### HtmlBuilder

- open class HtmlBuilder(@PublishedApi internal val tag: String) {
- fun div(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("div", block)
- fun span(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("span", block)
- fun section(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("section", block)
- fun article(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("article", block)
- fun aside(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("aside", block)
- fun header(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("header", block)
- fun footer(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("footer", block)
- fun main(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("main", block)
- fun nav(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("nav", block)
- fun h1(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h1", block)
- fun h2(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h2", block)
- fun h3(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h3", block)
- fun h4(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h4", block)
- fun h5(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h5", block)
- fun h6(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h6", block)
- fun p(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("p", block)
- fun pre(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("pre", block)
- fun code(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("code", block)
- fun blockquote(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("blockquote", block)
- fun em(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("em", block)
- fun strong(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("strong", block)
- fun ul(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("ul", block)
- fun ol(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("ol", block)
- fun table(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("table", block)
- fun form(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("form", block)
- fun button(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("button", block)
- fun a(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("a", block)
- fun img(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("img", block)
- fun fragment(block: HtmlBuilder.() -> Unit): ReactElement {

### FetchExtensions

- data class FetchResponse(val body: String, val status: Int) { val ok: Boolean }
- class FetchException(message: String) : Exception(message)
- suspend fun fetchText(url: String): FetchResponse
- suspend fun postText(url: String, body: String, contentType: String = "application/json"): FetchResponse
- suspend fun putText(url: String, body: String, contentType: String = "application/json"): FetchResponse
- suspend fun patchText(url: String, body: String, contentType: String = "application/json"): FetchResponse
- suspend fun deleteText(url: String): FetchResponse
- suspend fun fetchRequest(method: String, url: String, body: String? = null, contentType: String? = null): FetchResponse

### HtmlDsl


### JsDispatcher


### RefDelegate

- class IntRefDelegate(initial: Int) : ReadWriteProperty<Any?, Int> {
- class StringRefDelegate(initial: String) : ReadWriteProperty<Any?, String> {
- fun refInt(initial: Int = 0): IntRefDelegate = IntRefDelegate(initial)
- fun refString(initial: String = ""): StringRefDelegate = StringRefDelegate(initial)

### StateDelegate

- class IntStateDelegate(initial: Int) : ReadWriteProperty<Any?, Int> {
- class StringStateDelegate(initial: String) : ReadWriteProperty<Any?, String> {
- class BooleanStateDelegate(initial: Boolean) : ReadWriteProperty<Any?, Boolean> {
- class DoubleStateDelegate(initial: Double) : ReadWriteProperty<Any?, Double> {
- class StringListStateDelegate(
- fun state(initial: Int): IntStateDelegate = IntStateDelegate(initial)
- fun state(initial: String): StringStateDelegate = StringStateDelegate(initial)
- fun state(initial: Boolean): BooleanStateDelegate = BooleanStateDelegate(initial)
- fun state(initial: Double): DoubleStateDelegate = DoubleStateDelegate(initial)
- fun stateList(vararg initial: String): StringListStateDelegate =
- fun stateList(initial: List<String> = emptyList()): StringListStateDelegate =

### StyleBuilder

- class StyleBuilder {

### TypedContext

- class TypedContext<T> @PublishedApi internal constructor(
- fun createStringContext(defaultValue: String): TypedContext<String> {
- fun createIntContext(defaultValue: Int): TypedContext<Int> {
- fun createBoolContext(defaultValue: Boolean): TypedContext<Boolean> {
- fun <T> ComponentScope.useContext(ctx: TypedContext<T>): T {

### Util

- class PropsBuilder {
- fun kebabToCamelCase(kebab: String): String {
- fun parseCssString(styleString: String): List<Pair<String, String>> {
- fun encodeStringList(items: List<String>): String = items.joinToString(STRING_LIST_SEPARATOR)
- fun decodeStringList(encoded: String): List<String> =
- fun depsToJsArray(vararg keys: Any?): Array<JSObject> {
- fun HtmlBuilder.render(comp: JSObject) {
- fun HtmlBuilder.render(comp: JSObject, propsBlock: PropsBuilder.() -> Unit) {
- fun HtmlBuilder.render(comp: JSObject) {
- fun HtmlBuilder.render(comp: JSObject, propsBlock: PropsBuilder.() -> Unit) {

---
*Generated by scripts/generate-api-signatures.sh*

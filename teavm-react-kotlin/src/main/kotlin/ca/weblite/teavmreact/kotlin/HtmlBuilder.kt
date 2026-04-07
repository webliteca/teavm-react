package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.React
import ca.weblite.teavmreact.core.ReactElement
import ca.weblite.teavmreact.events.*
import ca.weblite.teavmreact.html.Html
import org.teavm.jso.JSObject

/**
 * Core DSL builder for constructing React element trees using
 * Kotlin's lambda-with-receiver pattern.
 *
 * Usage:
 * ```
 * div {
 *     h1 { +"Hello World" }
 *     p { +"Some text" }
 *     button {
 *         +"Click me"
 *         onClick { println("clicked") }
 *     }
 * }
 * ```
 */
@HtmlDsl
open class HtmlBuilder(@PublishedApi internal val tag: String) {
    @PublishedApi
    internal val children = mutableListOf<ReactElement>()

    @PublishedApi
    internal val props: JSObject = React.createObject()

    // ====================================================================
    // Text and element operators
    // ====================================================================

    /** Add a text node — idiomatic Kotlin HTML DSL convention: +"text" */
    operator fun String.unaryPlus() {
        children.add(Html.text(this))
    }

    /** Add a child ReactElement */
    operator fun ReactElement.unaryPlus() {
        children.add(this)
    }

    /** Render a wrapped component (JSObject from fc()) as a child */
    operator fun JSObject.unaryPlus() {
        children.add(Html.component(this))
    }

    /** Splice a list of ReactElements as children */
    operator fun List<ReactElement>.unaryPlus() {
        children.addAll(this)
    }

    /** Add a raw child element */
    fun child(element: ReactElement) {
        children.add(element)
    }

    // ====================================================================
    // Common attributes
    // ====================================================================

    fun className(value: String) { React.setProperty(props, "className", value) }
    fun id(value: String) { React.setProperty(props, "id", value) }
    fun key(value: String) { React.setProperty(props, "key", value) }
    fun key(value: Int) { React.setProperty(props, "key", value) }
    fun title(value: String) { React.setProperty(props, "title", value) }
    fun tabIndex(value: Int) { React.setProperty(props, "tabIndex", value) }
    fun role(value: String) { React.setProperty(props, "role", value) }
    fun draggable(value: Boolean) { React.setProperty(props, "draggable", value) }
    fun hidden(value: Boolean) { React.setProperty(props, "hidden", value) }

    /** Set an arbitrary prop by name */
    fun prop(name: String, value: String) { React.setProperty(props, name, value) }
    fun prop(name: String, value: Int) { React.setProperty(props, name, value) }
    fun prop(name: String, value: Boolean) { React.setProperty(props, name, value) }
    fun prop(name: String, value: Double) { React.setProperty(props, name, value) }
    fun prop(name: String, value: JSObject) { React.setProperty(props, name, value) }

    // ====================================================================
    // Style DSL
    // ====================================================================

    /** Type-safe inline styles via builder */
    fun style(block: StyleBuilder.() -> Unit) {
        val style = StyleBuilder().apply(block).build()
        React.setProperty(props, "style", style)
    }

    /** Quick CSS string shorthand (parsed into a style object) */
    fun css(styleString: String) {
        val obj = React.createObject()
        for ((camelProp, value) in parseCssString(styleString)) {
            React.setProperty(obj, camelProp, value)
        }
        React.setProperty(props, "style", obj)
    }

    // ====================================================================
    // Event handlers
    // ====================================================================

    fun onClick(handler: EventHandler) { React.setOnClick(props, handler) }
    fun onChange(handler: ChangeEventHandler) { React.setOnChange(props, handler) }
    fun onKeyDown(handler: KeyboardEventHandler) { React.setOnKeyDown(props, handler) }
    fun onKeyUp(handler: KeyboardEventHandler) { React.setOnKeyUp(props, handler) }
    fun onFocus(handler: FocusEventHandler) { React.setOnFocus(props, handler) }
    fun onBlur(handler: FocusEventHandler) { React.setOnBlur(props, handler) }
    fun onSubmit(handler: SubmitEventHandler) { React.setOnSubmit(props, handler) }
    fun onMouseDown(handler: EventHandler) { React.setOnMouseDown(props, handler) }
    fun onMouseUp(handler: EventHandler) { React.setOnMouseUp(props, handler) }
    fun onMouseEnter(handler: EventHandler) { React.setOnMouseEnter(props, handler) }
    fun onMouseLeave(handler: EventHandler) { React.setOnMouseLeave(props, handler) }

    // ====================================================================
    // Form-related attributes
    // ====================================================================

    fun value(v: String) { React.setProperty(props, "value", v) }
    fun defaultValue(v: String) { React.setProperty(props, "defaultValue", v) }
    fun placeholder(v: String) { React.setProperty(props, "placeholder", v) }
    fun disabled(v: Boolean) { React.setProperty(props, "disabled", v) }
    fun readOnly(v: Boolean) { React.setProperty(props, "readOnly", v) }
    fun checked(v: Boolean) { React.setProperty(props, "checked", v) }
    fun type(v: String) { React.setProperty(props, "type", v) }
    fun name(v: String) { React.setProperty(props, "name", v) }
    fun htmlFor(v: String) { React.setProperty(props, "htmlFor", v) }
    fun autoFocus(v: Boolean) { React.setProperty(props, "autoFocus", v) }
    fun maxLength(v: Int) { React.setProperty(props, "maxLength", v) }
    fun minLength(v: Int) { React.setProperty(props, "minLength", v) }
    fun rows(v: Int) { React.setProperty(props, "rows", v) }
    fun cols(v: Int) { React.setProperty(props, "cols", v) }

    // ====================================================================
    // Link / media attributes
    // ====================================================================

    fun href(v: String) { React.setProperty(props, "href", v) }
    fun src(v: String) { React.setProperty(props, "src", v) }
    fun alt(v: String) { React.setProperty(props, "alt", v) }
    fun target(v: String) { React.setProperty(props, "target", v) }

    // ====================================================================
    // Ref support
    // ====================================================================

    fun ref(handle: org.teavm.jso.JSObject) { React.setProperty(props, "ref", handle) }

    // ====================================================================
    // Conditional rendering helper
    // ====================================================================

    /** Render children only when condition is true */
    fun show(condition: Boolean, block: HtmlBuilder.() -> Unit) {
        if (condition) block()
    }

    // ====================================================================
    // Child element builders — Layout
    // ====================================================================

    fun div(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("div", block))
    }

    fun span(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("span", block))
    }

    fun section(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("section", block))
    }

    fun article(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("article", block))
    }

    fun aside(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("aside", block))
    }

    fun header(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("header", block))
    }

    fun footer(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("footer", block))
    }

    fun main(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("main", block))
    }

    fun nav(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("nav", block))
    }

    // ====================================================================
    // Headings
    // ====================================================================

    fun h1(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("h1", block))
    }

    fun h2(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("h2", block))
    }

    fun h3(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("h3", block))
    }

    fun h4(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("h4", block))
    }

    fun h5(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("h5", block))
    }

    fun h6(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("h6", block))
    }

    // ====================================================================
    // Text elements
    // ====================================================================

    fun p(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("p", block))
    }

    fun pre(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("pre", block))
    }

    fun code(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("code", block))
    }

    fun blockquote(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("blockquote", block))
    }

    fun em(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("em", block))
    }

    fun strong(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("strong", block))
    }

    fun small(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("small", block))
    }

    fun mark(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("mark", block))
    }

    fun sub(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("sub", block))
    }

    fun sup(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("sup", block))
    }

    fun label(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("label", block))
    }

    // ====================================================================
    // List elements
    // ====================================================================

    fun ul(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("ul", block))
    }

    fun ol(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("ol", block))
    }

    fun li(key: Any? = null, block: HtmlBuilder.() -> Unit) {
        val builder = HtmlBuilder("li")
        key?.let { React.setProperty(builder.props, "key", it.toString()) }
        builder.block()
        children.add(builder.build())
    }

    fun dl(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("dl", block))
    }

    fun dt(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("dt", block))
    }

    fun dd(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("dd", block))
    }

    // ====================================================================
    // Table elements
    // ====================================================================

    fun table(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("table", block))
    }

    fun thead(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("thead", block))
    }

    fun tbody(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("tbody", block))
    }

    fun tfoot(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("tfoot", block))
    }

    fun tr(key: Any? = null, block: HtmlBuilder.() -> Unit) {
        val builder = HtmlBuilder("tr")
        key?.let { React.setProperty(builder.props, "key", it.toString()) }
        builder.block()
        children.add(builder.build())
    }

    fun th(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("th", block))
    }

    fun td(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("td", block))
    }

    fun caption(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("caption", block))
    }

    // ====================================================================
    // Form elements
    // ====================================================================

    fun form(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("form", block))
    }

    fun fieldset(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("fieldset", block))
    }

    fun legend(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("legend", block))
    }

    fun button(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("button", block))
    }

    fun input(type: String = "text", block: HtmlBuilder.() -> Unit = {}) {
        val builder = HtmlBuilder("input")
        React.setProperty(builder.props, "type", type)
        builder.block()
        children.add(builder.build())
    }

    fun textarea(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("textarea", block))
    }

    fun select(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("select", block))
    }

    fun option(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("option", block))
    }

    // ====================================================================
    // Link / media elements
    // ====================================================================

    fun a(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("a", block))
    }

    fun img(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("img", block))
    }

    fun figure(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("figure", block))
    }

    fun figcaption(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("figcaption", block))
    }

    fun video(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("video", block))
    }

    fun audio(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("audio", block))
    }

    fun source(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("source", block))
    }

    // ====================================================================
    // Details / Summary
    // ====================================================================

    fun details(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("details", block))
    }

    fun summary(block: HtmlBuilder.() -> Unit) {
        children.add(buildElement("summary", block))
    }

    // ====================================================================
    // Void elements
    // ====================================================================

    fun hr() {
        children.add(React.createElement("hr", null as JSObject?))
    }

    fun br() {
        children.add(React.createElement("br", null as JSObject?))
    }

    // ====================================================================
    // Build
    // ====================================================================

    fun build(): ReactElement {
        return if (children.isEmpty()) {
            React.createElement(tag, props)
        } else {
            React.createElement(tag, props, children.toTypedArray() as Array<JSObject>)
        }
    }

    @PublishedApi
    internal companion object {
        fun buildElement(tag: String, block: HtmlBuilder.() -> Unit): ReactElement {
            return HtmlBuilder(tag).apply(block).build()
        }
    }
}

// ========================================================================
// Top-level element functions — entry points for the DSL
// ========================================================================

// --- Layout ---
fun div(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("div", block)
fun span(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("span", block)
fun section(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("section", block)
fun article(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("article", block)
fun aside(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("aside", block)
fun header(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("header", block)
fun footer(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("footer", block)
fun main(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("main", block)
fun nav(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("nav", block)

// --- Headings ---
fun h1(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h1", block)
fun h2(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h2", block)
fun h3(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h3", block)
fun h4(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h4", block)
fun h5(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h5", block)
fun h6(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("h6", block)

// --- Text ---
fun p(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("p", block)
fun pre(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("pre", block)
fun code(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("code", block)
fun blockquote(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("blockquote", block)
fun em(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("em", block)
fun strong(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("strong", block)

// --- Lists ---
fun ul(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("ul", block)
fun ol(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("ol", block)

// --- Table ---
fun table(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("table", block)

// --- Form ---
fun form(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("form", block)
fun button(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("button", block)

// --- Other ---
fun a(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("a", block)
fun img(block: HtmlBuilder.() -> Unit): ReactElement = HtmlBuilder.buildElement("img", block)

// --- Fragment ---
fun fragment(block: HtmlBuilder.() -> Unit): ReactElement {
    val builder = HtmlBuilder("fragment")
    builder.block()
    val fragmentType = React.fragment()
    return React.createElement(fragmentType, null, builder.children.toTypedArray() as Array<JSObject>)
}

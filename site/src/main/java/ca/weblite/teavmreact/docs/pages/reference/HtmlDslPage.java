package ca.weblite.teavmreact.docs.pages.reference;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Comprehensive reference page for the HTML DSL: Html.*, ElementBuilder,
 * DomBuilder, and Style classes.
 */
public class HtmlDslPage {

    public static ReactElement render(JSObject props) {
        return El.div("page-content",

            h1("HTML DSL Reference"),
            p(fragment(
                text("The "),
                code("Html"),
                text(" class provides static factory methods for creating React elements "),
                text("that correspond to standard HTML tags. Combined with "),
                code("ElementBuilder"),
                text(" for fluent attribute configuration and "),
                code("Style"),
                text(" for inline styles, this DSL lets you build full UI trees in pure Java.")
            )),

            // ── Layout Elements ──
            h2("Layout Elements"),
            p("Container elements that define page structure. Each accepts child elements as varargs:"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("HTML Tag"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code("div(children...)")), td(code("<div>")), td("Generic block container.")),
                    tr(td(code("span(children...)")), td(code("<span>")), td("Inline container.")),
                    tr(td(code("section(children...)")), td(code("<section>")), td("Thematic section.")),
                    tr(td(code("article(children...)")), td(code("<article>")), td("Self-contained content.")),
                    tr(td(code("header(children...)")), td(code("<header>")), td("Introductory content or nav group.")),
                    tr(td(code("footer(children...)")), td(code("<footer>")), td("Footer content.")),
                    tr(td(code("main(children...)")), td(code("<main>")), td("Dominant content of the body.")),
                    tr(td(code("nav(children...)")), td(code("<nav>")), td("Navigation links."))
                )
            ),

            CodeBlock.create("""
                div(
                    header(
                        nav(
                            a("Home").href("#/").build(),
                            a("About").href("#/about").build()
                        )
                    ),
                    main(
                        section(
                            h1("Welcome"),
                            p("Main content goes here.")
                        )
                    ),
                    footer(
                        p("Copyright 2025")
                    )
                )""", "java"),

            // ── Text Elements ──
            h2("Text Elements"),
            p("Elements for headings, paragraphs, and inline text formatting:"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("HTML Tag"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code("h1(text)")), td(code("<h1>")), td("Top-level heading.")),
                    tr(td(code("h2(text)")), td(code("<h2>")), td("Second-level heading.")),
                    tr(td(code("h3(text)")), td(code("<h3>")), td("Third-level heading.")),
                    tr(td(code("h4(text)")), td(code("<h4>")), td("Fourth-level heading.")),
                    tr(td(code("p(text)")), td(code("<p>")), td("Paragraph. Also accepts children via p(children...).")),
                    tr(td(code("p(children...)")), td(code("<p>")), td("Paragraph with mixed content.")),
                    tr(td(code("text(string)")), td("(text node)"), td("Raw text node. Use inside fragment() or as a child.")),
                    tr(td(code("strong(text)")), td(code("<strong>")), td("Strong emphasis (bold).")),
                    tr(td(code("em(text)")), td(code("<em>")), td("Emphasis (italic).")),
                    tr(td(code("code(text)")), td(code("<code>")), td("Inline code.")),
                    tr(td(code("mark(text)")), td(code("<mark>")), td("Highlighted text.")),
                    tr(td(code("small(text)")), td(code("<small>")), td("Side comment or small print.")),
                    tr(td(code("blockquote(children...)")), td(code("<blockquote>")), td("Block quotation."))
                )
            ),

            CodeBlock.create("""
                p(fragment(
                    text("This is "),
                    strong("important"),
                    text(" and this is "),
                    em("emphasized"),
                    text(". See "),
                    code("example()"),
                    text(" for details.")
                ))""", "java"),

            // ── List Elements ──
            h2("List Elements"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("HTML Tag"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code("ul(children...)")), td(code("<ul>")), td("Unordered list.")),
                    tr(td(code("ol(children...)")), td(code("<ol>")), td("Ordered list.")),
                    tr(td(code("li(children...)")), td(code("<li>")), td("List item. Also accepts li(text)."))
                )
            ),

            CodeBlock.create("""
                ul(
                    li("First item"),
                    li("Second item"),
                    li(fragment(
                        text("Third item with "),
                        a("a link").href("#/somewhere").build()
                    ))
                )""", "java"),

            // ── Table Elements ──
            h2("Table Elements"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("HTML Tag"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code("table(children...)")), td(code("<table>")), td("Table container. Returns ElementBuilder.")),
                    tr(td(code("thead(children...)")), td(code("<thead>")), td("Table header group.")),
                    tr(td(code("tbody(children...)")), td(code("<tbody>")), td("Table body group.")),
                    tr(td(code("tr(children...)")), td(code("<tr>")), td("Table row.")),
                    tr(td(code("th(text)")), td(code("<th>")), td("Table header cell.")),
                    tr(td(code("td(text)")), td(code("<td>")), td("Table data cell. Also accepts td(children...)."))
                )
            ),

            CodeBlock.create("""
                El.table("data-table",

                    thead(
                        tr(
                            th("Name"),
                            th("Age")
                        )
                    ),
                    tbody(
                        tr(td("Alice"), td("30")),
                        tr(td("Bob"), td("25"))
                    )
                )""", "java"),

            // ── Form Elements ──
            h2("Form Elements"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("HTML Tag"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code("input(type)")), td(code("<input>")), td("Input element. Returns ElementBuilder for chaining.")),
                    tr(td(code("button(label)")), td(code("<button>")), td("Button element. Returns ElementBuilder for chaining.")),
                    tr(td(code("a(text)")), td(code("<a>")), td("Anchor element. Use .href() and .target() to configure."))
                )
            ),

            CodeBlock.create("""
                div(
                    input("text")
                        .value(name.getString())
                        .placeholder("Enter name")
                        .onChange(e -> name.setString(
                            e.getTarget().getValue()))
                        .build(),
                    input("checkbox")
                        .checked(agreed.getBool())
                        .onChange(e -> agreed.setBool(
                            e.getTarget().getChecked()))
                        .build(),
                    button("Submit")
                        .onClick(e -> handleSubmit())
                        .disabled(name.getString().isEmpty())
                        .build(),
                    a("Documentation").href("#/docs")
                        .target("_blank").build()
                )""", "java"),

            // ── Other Elements ──
            h2("Other Elements"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("HTML Tag"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code("hr()")), td(code("<hr>")), td("Horizontal rule.")),
                    tr(td(code("br()")), td(code("<br>")), td("Line break.")),
                    tr(td(code("pre(children...)")), td(code("<pre>")), td("Preformatted text.")),
                    tr(td(code("details(children...)")), td(code("<details>")), td("Disclosure widget.")),
                    tr(td(code("summary(children...)")), td(code("<summary>")), td("Summary for a details element.")),
                    tr(td(code("fragment(children...)")), td("(fragment)"), td("Groups children without adding a DOM node.")),
                    tr(td(code("component(fn, name)")), td("(component)"), td("Wraps a RenderFunction as a named React component."))
                )
            ),

            // ── ElementBuilder ──
            h2("ElementBuilder Fluent API"),
            p(fragment(
                text("Methods like "),
                code("a()"),
                text(", "),
                code("button()"),
                text(", "),
                code("input()"),
                text(", and container methods return an "),
                code("ElementBuilder"),
                text(" that supports fluent attribute configuration. Call "),
                code(".build()"),
                text(" to finalize the element.")
            )),

            h3("Identity and Structure"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code(".className(String)")), td("Sets the CSS class name.")),
                    tr(td(code(".id(String)")), td("Sets the element ID.")),
                    tr(td(code(".key(String)")), td("Sets the React key for list rendering.")),
                    tr(td(code(".style(JSObject)")), td("Sets inline styles via a Style object.")),
                    tr(td(code(".prop(String, JSObject)")), td("Sets an arbitrary prop (e.g., ref, data-*).")),
                    tr(td(code(".build()")), td("Finalizes the element with no additional children.")),
                    tr(td(code(".build(children...)")), td("Finalizes the element with additional children."))
                )
            ),

            h3("Event Handlers"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("Handler Type"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code(".onClick(handler)")), td(code("EventHandler")), td("Click event.")),
                    tr(td(code(".onChange(handler)")), td(code("ChangeEventHandler")), td("Input value change.")),
                    tr(td(code(".onKeyDown(handler)")), td(code("KeyboardEventHandler")), td("Key press.")),
                    tr(td(code(".onKeyUp(handler)")), td(code("KeyboardEventHandler")), td("Key release.")),
                    tr(td(code(".onFocus(handler)")), td(code("FocusEventHandler")), td("Element focused.")),
                    tr(td(code(".onBlur(handler)")), td(code("FocusEventHandler")), td("Element lost focus."))
                )
            ),

            h3("Input and Link Attributes"),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code(".value(String)")), td("Sets the input value (controlled component).")),
                    tr(td(code(".placeholder(String)")), td("Sets placeholder text.")),
                    tr(td(code(".disabled(boolean)")), td("Disables the element.")),
                    tr(td(code(".checked(boolean)")), td("Sets checkbox/radio checked state.")),
                    tr(td(code(".type(String)")), td("Sets the input type.")),
                    tr(td(code(".href(String)")), td("Sets the link URL.")),
                    tr(td(code(".src(String)")), td("Sets the image source.")),
                    tr(td(code(".alt(String)")), td("Sets the image alt text.")),
                    tr(td(code(".target(String)")), td("Sets the link target (e.g., \"_blank\")."))
                )
            ),

            // ── DomBuilder ──
            h2("DomBuilder Pattern"),
            p(fragment(
                text("For more complex element construction, teavm-react provides "),
                code("DomBuilder"),
                text(" inner classes such as "),
                code("Div"),
                text(", "),
                code("Span"),
                text(", "),
                code("P"),
                text(", "),
                code("Button"),
                text(", and "),
                code("Input"),
                text(". These use a builder pattern with "),
                code(".create()"),
                text(", method chaining, and "),
                code(".build()"),
                text(".")
            )),
            CodeTabs.create(
                """
                    // DomBuilder pattern
                    DomBuilder.Div.create()
                        .className("card")
                        .child(
                            DomBuilder.P.create()
                                .text("Hello from DomBuilder")
                                .className("card-text")
                                .build()
                        )
                        .child(
                            DomBuilder.Button.create()
                                .text("Click Me")
                                .className("card-btn")
                                .build()
                        )
                        .build();""",
                """
                    // DomBuilder pattern
                    DomBuilder.Div.create()
                        .className("card")
                        .child(
                            DomBuilder.P.create()
                                .text("Hello from DomBuilder")
                                .className("card-text")
                                .build()
                        )
                        .child(
                            DomBuilder.Button.create()
                                .text("Click Me")
                                .className("card-btn")
                                .build()
                        )
                        .build()"""
            ),

            Callout.note("When to Use DomBuilder vs Html.*",
                p(fragment(
                    text("The "),
                    code("Html.*"),
                    text(" static methods are concise and recommended for most use cases. "),
                    text("Use "),
                    code("DomBuilder"),
                    text(" when you need to conditionally add children or build elements "),
                    text("in a loop without creating intermediate arrays.")
                ))
            ),

            // ── Style ──
            h2("Style Class"),
            p(fragment(
                text("The "),
                code("Style"),
                text(" class creates inline style objects. Use "),
                code("Style.create()"),
                text(" to start a builder chain:")
            )),
            El.table("api-table",

                thead(
                    tr(
                        th("Method"),
                        th("CSS Property"),
                        th("Description")
                    )
                ),
                tbody(
                    tr(td(code(".padding(String)")), td(code("padding")), td("Sets padding (e.g., \"8px\", \"1rem\").")),
                    tr(td(code(".margin(String)")), td(code("margin")), td("Sets margin.")),
                    tr(td(code(".background(String)")), td(code("background")), td("Sets background color or image.")),
                    tr(td(code(".color(String)")), td(code("color")), td("Sets text color.")),
                    tr(td(code(".display(String)")), td(code("display")), td("Sets display mode (flex, grid, block, etc.).")),
                    tr(td(code(".flexDirection(String)")), td(code("flexDirection")), td("Sets flex direction.")),
                    tr(td(code(".gap(String)")), td(code("gap")), td("Sets gap between flex/grid children.")),
                    tr(td(code(".borderRadius(String)")), td(code("borderRadius")), td("Sets border radius.")),
                    tr(td(code(".set(String, String)")), td("(any)"), td("Sets an arbitrary CSS property by name."))
                )
            ),

            CodeBlock.create("""
                import ca.weblite.teavmreact.html.Style;

                JSObject cardStyle = Style.create()
                    .padding("16px")
                    .margin("8px")
                    .background("#f5f5f5")
                    .borderRadius("8px")
                    .display("flex")
                    .flexDirection("column")
                    .gap("12px")
                    .set("boxShadow", "0 2px 4px rgba(0,0,0,0.1)")
                    .build();

                return div(
                    h3("Styled Card"),
                    p("Content with inline styles.")
                ).style(cardStyle).build();""", "java"),

            // ── Combining It All ──
            h2("Putting It All Together"),
            p("Here is a complete example combining layout, text, lists, forms, and styles:"),
            CodeBlock.create("""
                public static ReactElement render(JSObject props) {
                    var name = Hooks.useState("");
                    var items = new String[]{"Apple", "Banana", "Cherry"};

                    JSObject headerStyle = Style.create()
                        .padding("16px")
                        .background("#1a73e8")
                        .color("white")
                        .build();

                    return El.div("app-container",

                        header(
                            h1("My App")
                        ).style(headerStyle).build(),

                        main(
                            section(
                                h2("Input"),
                                input("text")
                                    .value(name.getString())
                                    .placeholder("Your name")
                                    .onChange(e -> name.setString(
                                        e.getTarget().getValue()))
                                    .build(),
                                p("Hello, " + name.getString() + "!")
                            ),

                            section(
                                h2("Fruit List"),
                                ul(
                                    li(items[0]),
                                    li(items[1]),
                                    li(items[2])
                                )
                            ),

                            section(
                                h2("Data Table"),
                                El.table("api-table",

                                    thead(tr(th("Fruit"), th("Color"))),
                                    tbody(
                                        tr(td("Apple"), td("Red")),
                                        tr(td("Banana"), td("Yellow"))
                                    )
                                )
                            )
                        )
                    );
                }""", "java"),

            // See also
            h2("See Also"),
            ul(
                li(a("Components -- Building Reusable Components").href("#/reference/components").build()),
                li(a("Events -- Handling User Interactions").href("#/reference/events").build()),
                li(a("Hooks Overview").href("#/reference/hooks").build())
            )
        );
    }
}

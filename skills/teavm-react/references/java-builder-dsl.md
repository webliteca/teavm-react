# Java Builder DSL Approach

Read this when building teavm-react components using the `DomBuilder` fluent chaining API. This approach is ideal for Java developers who prefer autocomplete-driven discovery and explicit builder patterns over static imports. All examples assume:

```java
import ca.weblite.teavmreact.html.DomBuilder.*;
import ca.weblite.teavmreact.html.DomBuilder;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import org.teavm.jso.JSObject;
```

## Core Pattern

Every HTML element has a corresponding inner class in `DomBuilder`. Create one with `.create()`, chain properties, and finish with `.build()`:

```java
Div.create()
    .className("container")
    .child(H1.create().text("Hello World").build())
    .child(P.create().text("Built with DomBuilder.").build())
    .build()
```

## Defining Components

Components are still defined with `React.wrapComponent()`. The builder DSL is used inside the render function:

```java
JSObject MyComponent = React.wrapComponent(props -> {
    return Div.create()
        .child(H1.create().text("My Component").build())
        .build();
}, "MyComponent");
```

## Available Builder Classes

**Layout:** `Div`, `Span`, `Section`, `Article`, `Aside`, `Header`, `Footer`, `Main`, `Nav`

**Headings:** `H1`, `H2`, `H3`, `H4`, `H5`, `H6`

**Text:** `P`, `Pre`, `Code`, `Blockquote`, `Em`, `Strong`, `Small`

**Lists:** `Ul`, `Ol`, `Li`, `Dl`, `Dt`, `Dd`

**Table:** `Table`, `Thead`, `Tbody`, `Tfoot`, `Tr`, `Th`, `Td`, `Caption`

**Form:** `Form`, `Fieldset`, `Legend`, `Label`, `Button`, `Input`, `Select`, `Textarea`

**Link/Media:** `A`, `Img`, `Figure`, `Figcaption`

**Misc:** `Hr`, `Br`, `Details`, `Summary`

Each has a `create()` static factory returning the typed builder.

## Chaining Methods

All builders inherit these from `DomBuilder`:

### Content
- `.text(String)` -- set text content
- `.child(ReactElement)` -- add a child element
- `.child(DomBuilder)` -- add a child builder (auto-builds)
- `.child(ElementBuilder)` -- add a child ElementBuilder (auto-builds)

### Iteration
- `.forEach(List<T>, Function<T, DomBuilder>)` -- map list items to DomBuilder children
- `.forEachElement(List<T>, Function<T, ReactElement>)` -- map list items to ReactElement children

### Attributes
- `.className(String)` -- CSS class
- `.id(String)` -- element ID
- `.key(String)` / `.key(int)` -- React key
- `.style(JSObject)` -- inline style object

### Event Handlers
- `.onClick(EventHandler)` -- click handler (`e -> { ... }`)
- `.onChange(ChangeEventHandler)` -- change handler (`e -> { e.getTarget().getValue() }`)
- `.onKeyDown(KeyboardEventHandler)` -- keyboard handler (`e -> { e.getKey() }`)
- `.onFocus(FocusEventHandler)` -- focus handler
- `.onBlur(FocusEventHandler)` -- blur handler
- `.onSubmit(SubmitEventHandler)` -- form submit handler

### Form Attributes
- `.value(String)` -- input value
- `.placeholder(String)` -- placeholder text
- `.disabled(boolean)` -- disabled state
- `.type(String)` -- input type

### Link/Media
- `.href(String)`, `.src(String)`, `.alt(String)`

### Generic
- `.prop(String name, String value)` -- arbitrary string prop
- `.prop(String name, JSObject value)` -- arbitrary JSObject prop

### Build
- `.build()` -- finalize and return `ReactElement`

## Input Convenience Methods

The `Input` class has shortcut factories:

```java
Input.text()       // <input type="text">
Input.password()   // <input type="password">
Input.checkbox()   // <input type="checkbox">
Input.number()     // <input type="number">
```

These are equivalent to `Input.create().type("text")` etc., but shorter:

```java
Input.text()
    .value(name.getString())
    .onChange(e -> name.setString(e.getTarget().getValue()))
    .placeholder("Your name")
    .build()
```

## State and Hooks

State management is identical to the Functional approach -- use `Hooks.useState()`:

```java
var count = Hooks.useState(0);
var name = Hooks.useState("");
var active = Hooks.useState(false);
```

Read with `.getInt()`, `.getString()`, `.getBool()`. Set with `.setInt()`, `.setString()`, `.setBool()`. Functional update with `.updateInt(prev -> prev + 1)`.

## Controlled Inputs

Text input:

```java
var email = Hooks.useState("");

Input.text()
    .value(email.getString())
    .onChange(e -> email.setString(e.getTarget().getValue()))
    .placeholder("you@example.com")
```

Checkbox:

```java
var agreed = Hooks.useState(false);

Input.checkbox()
    .onChange(e -> agreed.setBool(e.getTarget().getChecked()))
    .prop("checked", agreed.getBool() ? "true" : "")
```

Textarea:

```java
var message = Hooks.useState("");

Textarea.create()
    .value(message.getString())
    .onChange(e -> message.setString(e.getTarget().getValue()))
    .placeholder("Write something...")
    .prop("rows", "4")
```

## List Rendering with forEach

### DomBuilder mapper (forEach)

```java
List<String> fruits = List.of("Apple", "Banana", "Cherry");

Ul.create()
    .forEach(fruits, fruit ->
        Li.create().key(fruit).text(fruit))
    .build()
```

### ReactElement mapper (forEachElement)

```java
Ul.create()
    .forEachElement(fruits, fruit ->
        Html.li(fruit))  // mix with functional Html if desired
    .build()
```

### Manual Loop

```java
DomBuilder list = Ul.create();
for (int i = 0; i < items.length; i++) {
    list.child(Li.create().key(i).text(items[i]));
}
list.build();
```

## Nesting Builders

Child builders are auto-built when passed to `.child()`:

```java
Div.create()
    .child(Header.create()
        .child(H1.create().text("Title")))    // auto-built
    .child(Main.create()
        .child(P.create().text("Content")))   // auto-built
    .child(Footer.create()
        .child(Small.create().text("Footer")))
    .build()
```

## Tables

```java
Table.create()
    .child(Thead.create()
        .child(Tr.create()
            .child(Th.create().text("Name"))
            .child(Th.create().text("Email"))))
    .child(Tbody.create()
        .forEach(users, user ->
            Tr.create().key(user.id())
                .child(Td.create().text(user.name()))
                .child(Td.create().text(user.email()))))
    .build()
```

## Inline Styles

Build a style JSObject and pass it:

```java
JSObject style = React.createObject();
React.setProperty(style, "backgroundColor", "#f0f0f0");
React.setProperty(style, "padding", "16px");
React.setProperty(style, "borderRadius", "8px");

Div.create()
    .style(style)
    .child(P.create().text("Styled box"))
    .build()
```

## Effects

Same hooks API as the Functional approach:

```java
// Mount-only effect
Hooks.useEffect(() -> {
    JsUtil.consoleLog("mounted");
    return () -> JsUtil.consoleLog("unmounted");
}, Hooks.deps());

// Timer
Hooks.useEffect(() -> {
    int id = JsUtil.setInterval(() -> seconds.updateInt(s -> s + 1), 1000);
    return () -> JsUtil.clearInterval(id);
}, Hooks.deps());
```

## Mixing with Other Approaches

DomBuilder produces `ReactElement`, so it interoperates with Functional and Class-based:

```java
// Add a functional Html element as a child
Div.create()
    .child(Html.h1("Title from Html"))         // ReactElement
    .child(P.create().text("From Builder"))    // DomBuilder
    .child(ReactView.view(MyView::new, "X"))   // Class-based
    .build()
```

## Complete Example: Contact Form

```java
import ca.weblite.teavmreact.html.DomBuilder.*;
import ca.weblite.teavmreact.html.DomBuilder;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;

public class ContactForm {
    static final JSObject App = React.wrapComponent(props -> {
        var name = Hooks.useState("");
        var email = Hooks.useState("");
        var subject = Hooks.useState("");
        var message = Hooks.useState("");
        var submitted = Hooks.useState(false);

        if (submitted.getBool()) {
            return Div.create().className("container")
                .child(H2.create().text("Thank you!"))
                .child(P.create().text("We received your message, " + name.getString() + "."))
                .child(Dl.create()
                    .child(Dt.create().text("Subject"))
                    .child(Dd.create().text(subject.getString()))
                    .child(Dt.create().text("Message"))
                    .child(Dd.create().text(message.getString())))
                .child(Button.create().text("Send another")
                    .onClick(e -> {
                        name.setString("");
                        email.setString("");
                        subject.setString("");
                        message.setString("");
                        submitted.setBool(false);
                    }))
                .build();
        }

        boolean valid = !name.getString().isEmpty()
                     && !email.getString().isEmpty()
                     && !message.getString().isEmpty();

        return Div.create().className("container")
            .child(H2.create().text("Contact Us"))
            .child(Form.create()
                .onSubmit(e -> { if (valid) submitted.setBool(true); })
                .child(Div.create().className("form-group")
                    .child(Label.create().text("Name").prop("htmlFor", "name"))
                    .child(Input.text().id("name")
                        .value(name.getString())
                        .onChange(e -> name.setString(e.getTarget().getValue()))
                        .placeholder("Your full name")))
                .child(Div.create().className("form-group")
                    .child(Label.create().text("Email").prop("htmlFor", "email"))
                    .child(Input.create().type("email").id("email")
                        .value(email.getString())
                        .onChange(e -> email.setString(e.getTarget().getValue()))
                        .placeholder("you@example.com")))
                .child(Div.create().className("form-group")
                    .child(Label.create().text("Subject").prop("htmlFor", "subj"))
                    .child(Input.text().id("subj")
                        .value(subject.getString())
                        .onChange(e -> subject.setString(e.getTarget().getValue()))
                        .placeholder("What is this about?")))
                .child(Div.create().className("form-group")
                    .child(Label.create().text("Message").prop("htmlFor", "msg"))
                    .child(Textarea.create().id("msg")
                        .value(message.getString())
                        .onChange(e -> message.setString(e.getTarget().getValue()))
                        .placeholder("Your message...")
                        .prop("rows", "5")))
                .child(Button.create().text("Send Message")
                    .type("submit")
                    .disabled(!valid)))
            .build();
    }, "ContactForm");

    public static void main(String[] args) {
        ReactRoot root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(React.createElement(App, null));
    }
}
```

## Key Differences from Functional Approach

1. Import `DomBuilder.*` instead of `static Html.*`
2. Elements are `ClassName.create()` not `methodName()`
3. Text content via `.text()` not constructor argument
4. Children added one-by-one via `.child()` not varargs
5. All builders must end with `.build()` (except when passed to `.child(DomBuilder)`)
6. `forEach` / `forEachElement` for list iteration instead of `mapToElements`
7. Interactive elements (Button, Input, etc.) are also DomBuilder subclasses, not ElementBuilder -- they share the same chaining API as other elements

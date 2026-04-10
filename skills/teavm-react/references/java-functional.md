# Java Functional Approach

Read this when building teavm-react components using static imports from `Html` and direct hook calls. This is the closest approach to idiomatic React with JSX. All examples assume:

```java
import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import org.teavm.jso.JSObject;
```

## Defining Components

Use `React.wrapComponent()` to turn a lambda into a React component:

```java
JSObject MyComponent = React.wrapComponent(props -> {
    return div(
        h1("Hello World"),
        p("This is a functional component.")
    );
}, "MyComponent");
```

The second argument sets the display name in React DevTools.

## Rendering Components

Mount at the application root:

```java
import org.teavm.jso.dom.html.HTMLDocument;

public static void main(String[] args) {
    ReactRoot root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    );
    root.render(React.createElement(MyComponent, null));
}
```

Render a sub-component inside another component:

```java
// No props
component(MyComponent)

// With props
JSObject props = React.createObject();
React.setProperty(props, "title", "Hello");
component(MyComponent, props)
```

## HTML Elements

Non-interactive elements have two overloads:

```java
div("text content")              // text child
div(child1, child2, child3)      // ReactElement children
```

Available: `div`, `span`, `section`, `article`, `aside`, `header`, `footer`, `main`, `nav`, `h1`-`h6`, `p`, `pre`, `code`, `blockquote`, `em`, `strong`, `small`, `sub`, `sup`, `mark`, `ul`, `ol`, `li`, `dl`, `dt`, `dd`, `table`, `thead`, `tbody`, `tfoot`, `tr`, `th`, `td`, `caption`, `form`, `fieldset`, `legend`, `label`, `figure`, `figcaption`, `details`, `summary`.

Void elements: `hr()`, `br()`.

Text nodes: `text("raw text")`.

## Interactive Elements (ElementBuilder)

`button`, `input`, `a`, `textarea`, `select`, `img` return an `ElementBuilder`. Chain props, then call `.build()`:

```java
button("Click me")
    .onClick(e -> { /* handle click */ })
    .className("btn-primary")
    .disabled(false)
    .build()

input("text")
    .value(currentValue)
    .onChange(e -> state.setString(e.getTarget().getValue()))
    .placeholder("Enter text...")
    .maxLength(100)
    .build()

a("Visit site")
    .href("https://example.com")
    .target("_blank")
    .build()

textarea()
    .value(text)
    .onChange(e -> state.setString(e.getTarget().getValue()))
    .rows(4)
    .cols(50)
    .build()

img().src("/logo.png").alt("Logo").build()
```

**ElementBuilder chaining methods:** `.className()`, `.id()`, `.key(String)`, `.key(int)`, `.onClick()`, `.onChange()`, `.onKeyDown()`, `.onKeyUp()`, `.onFocus()`, `.onBlur()`, `.onSubmit()`, `.onMouseDown()`, `.onMouseUp()`, `.onMouseEnter()`, `.onMouseLeave()`, `.value()`, `.placeholder()`, `.disabled()`, `.checked()`, `.readOnly()`, `.href()`, `.src()`, `.alt()`, `.target()`, `.type()`, `.name()`, `.htmlFor()`, `.tabIndex()`, `.rows()`, `.cols()`, `.maxLength()`, `.minLength()`, `.style(JSObject)`, `.prop(name, value)`.

`.build()` returns `ReactElement`. `.build(ReactElement... children)` adds children.

## State with useState

`Hooks.useState()` returns a `StateHandle<T>`:

```java
var count = Hooks.useState(0);         // StateHandle<Integer>
var name  = Hooks.useState("");        // StateHandle<String>
var on    = Hooks.useState(false);     // StateHandle<Boolean>
var price = Hooks.useState(9.99);      // StateHandle<Double>
var obj   = Hooks.useState(jsObject);  // StateHandle<JSObject>
```

### Reading State

```java
count.getInt()       // int
name.getString()     // String
on.getBool()         // boolean
price.getDouble()    // double
obj.get()            // raw JSObject
```

### Setting State

```java
count.setInt(42);
name.setString("Alice");
on.setBool(true);
price.setDouble(19.99);
```

### Functional Updates

Use when the new value depends on the previous:

```java
count.updateInt(prev -> prev + 1);
name.updateString(prev -> prev.toUpperCase());
```

## Effects with useEffect

```java
// Run after every render (no deps)
Hooks.useEffect(() -> {
    JsUtil.consoleLog("rendered");
    return null; // no cleanup
});

// Run once on mount (empty deps)
Hooks.useEffect(() -> {
    JsUtil.consoleLog("mounted");
    return () -> JsUtil.consoleLog("unmounted"); // cleanup
}, Hooks.deps());

// Run when specific values change
Hooks.useEffect(() -> {
    JsUtil.consoleLog("count changed to " + count.getInt());
    return null;
}, Hooks.deps(React.intToJS(count.getInt())));
```

`EffectCallback` must return a `VoidCallback` (for cleanup) or `null`.

### Timer Pattern

```java
Hooks.useEffect(() -> {
    int id = JsUtil.setInterval(() -> count.updateInt(c -> c + 1), 1000);
    return () -> JsUtil.clearInterval(id);
}, Hooks.deps());
```

## Refs

```java
RefHandle ref = Hooks.useRef(null);          // JSObject ref
RefHandle intRef = Hooks.useRefInt(0);       // int ref
RefHandle strRef = Hooks.useRefString("");   // String ref

// Read
ref.getCurrent();           // JSObject
intRef.getCurrentInt();     // int
strRef.getCurrentString();  // String

// Write
ref.setCurrent(someJsObj);

// Pass to DOM element via props
input("text").prop("ref", ref.raw()).build()
```

## Event Handling

### Click Events

```java
button("Click").onClick(e -> {
    // e is JSObject (the raw React SyntheticEvent)
}).build()
```

### Change Events (ChangeEventHandler)

```java
input("text").onChange(e -> {
    String val = e.getTarget().getValue();     // input value
    boolean checked = e.getTarget().getChecked(); // checkbox
    String type = e.getTarget().getType();     // input type
}).build()
```

### Keyboard Events (KeyboardEventHandler)

```java
input("text").onKeyDown(e -> {
    String key = e.getKey();       // "Enter", "Escape", etc.
    String code = e.getCode();     // "KeyA", "ArrowUp", etc.
    boolean ctrl = e.getCtrlKey();
    boolean shift = e.getShiftKey();
    boolean alt = e.getAltKey();
    boolean meta = e.getMetaKey();
    boolean repeat = e.getRepeat();
}).build()
```

### Focus Events (FocusEventHandler)

```java
input("text")
    .onFocus(e -> focused.setBool(true))
    .onBlur(e -> focused.setBool(false))
    .build()
```

### Submit Events (SubmitEventHandler)

Set on a `form` element using the low-level API:

```java
JSObject formProps = React.createObject();
React.setOnSubmit(formProps, e -> {
    // handle submission
});
React.createElement("form", formProps, children);
```

## Controlled Inputs

Standard React pattern -- bind value + onChange:

```java
var email = Hooks.useState("");

input("email")
    .value(email.getString())
    .onChange(e -> email.setString(e.getTarget().getValue()))
    .placeholder("you@example.com")
    .build()
```

Checkbox:

```java
var agreed = Hooks.useState(false);

input("checkbox")
    .checked(agreed.getBool())
    .onChange(e -> agreed.setBool(e.getTarget().getChecked()))
    .build()
```

## List Rendering

### Using mapToElements

```java
List<String> items = List.of("Apple", "Banana", "Cherry");

ul(mapToElements(items, item -> li(item)))
```

### Manual Array Construction

```java
ReactElement[] rows = new ReactElement[data.length];
for (int i = 0; i < data.length; i++) {
    rows[i] = tr(td(data[i].name()), td(data[i].value()));
}
table(tbody(rows));
```

## Fragments

Wrap multiple elements without an extra DOM node:

```java
fragment(
    h1("Title"),
    p("Paragraph 1"),
    p("Paragraph 2")
)
```

## Inline Styles

Build a style object manually:

```java
JSObject style = React.createObject();
React.setProperty(style, "backgroundColor", "#282c34");
React.setProperty(style, "color", "white");
React.setProperty(style, "padding", "20px");

// Pass to an ElementBuilder
button("Styled").style(style).build()
```

## Context

```java
// Create (typically as a static field)
static final ReactContext THEME = ReactContext.create(React.stringToJS("light"));

// Provide a value to children
THEME.provide(React.stringToJS(currentTheme),
    component(ChildA),
    component(ChildB)
);

// Consume in a child component
JSObject themeVal = Hooks.useContext(THEME.jsContext());
String theme = React.jsToString(themeVal);
```

## Memoization

Wrap a component with `React.memo()` to skip re-renders when props are unchanged:

```java
JSObject MemoizedComp = React.memo(React.wrapComponent(props -> {
    return div(p("I only re-render when props change"));
}, "MemoizedComp"));
```

## Complete Example: Todo List App

```java
import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;
import java.util.ArrayList;
import java.util.List;

public class TodoApp {
    static final JSObject App = React.wrapComponent(props -> {
        var input = Hooks.useState("");
        var items = Hooks.useState("");       // comma-separated storage
        var nextId = Hooks.useState(0);

        List<String> todoList = new ArrayList<>();
        if (!items.getString().isEmpty()) {
            for (String s : items.getString().split("\n")) todoList.add(s);
        }

        return div(
            h1("Todo List"),
            p(todoList.size() + " item(s)"),
            div(
                input("text")
                    .value(input.getString())
                    .onChange(e -> input.setString(e.getTarget().getValue()))
                    .onKeyDown(e -> {
                        if (e.getKey().equals("Enter") && !input.getString().isEmpty()) {
                            addItem(items, input);
                        }
                    })
                    .placeholder("What needs to be done?")
                    .build(),
                button("Add")
                    .onClick(e -> addItem(items, input))
                    .disabled(input.getString().isEmpty())
                    .build()
            ),
            ul(mapToElements(todoList, item -> {
                String id = item.split(":")[0];
                String text = item.substring(item.indexOf(':') + 1);
                return li(
                    span(text),
                    button(" x")
                        .onClick(e -> removeItem(items, id))
                        .className("btn-danger")
                        .build()
                );
            }))
        );
    }, "TodoApp");

    static void addItem(StateHandle<String> items, StateHandle<String> input) {
        String current = items.getString();
        String entry = System.identityHashCode(input) + ":" + input.getString();
        items.setString(current.isEmpty() ? entry : current + "\n" + entry);
        input.setString("");
    }

    static void removeItem(StateHandle<String> items, String id) {
        StringBuilder sb = new StringBuilder();
        for (String line : items.getString().split("\n")) {
            if (!line.startsWith(id + ":")) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(line);
            }
        }
        items.setString(sb.toString());
    }

    public static void main(String[] args) {
        ReactRoot root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(component(App));
    }
}
```

## JsUtil Utilities

```java
int id = JsUtil.setInterval(() -> { /* tick */ }, 1000);
JsUtil.clearInterval(id);

int tid = JsUtil.setTimeout(() -> { /* delayed */ }, 500);
JsUtil.clearTimeout(tid);

JsUtil.consoleLog("debug message");
JsUtil.consoleLog(someJsObject);
JsUtil.consoleError("error message");
JsUtil.alert("popup");
```

# Events Reference

Read this file when handling user interactions -- clicks, input changes, keyboard events, form submissions, focus, or mouse events. Covers all event handler interfaces, event object types, and their properties.

## Event Handler Interfaces

All handlers are in `ca.weblite.teavmreact.events`. Every handler interface is annotated `@JSFunctor` and extends `JSObject`.

| Interface | Method Signature | Event Object |
|-----------|-----------------|--------------|
| `EventHandler` | `handleEvent(SyntheticEvent event)` | `SyntheticEvent` |
| `ChangeEventHandler` | `handleEvent(ChangeEvent event)` | `ChangeEvent` |
| `KeyboardEventHandler` | `handleEvent(KeyboardEvent event)` | `KeyboardEvent` |
| `FocusEventHandler` | `handleEvent(FocusEvent event)` | `FocusEvent` |
| `SubmitEventHandler` | `handleEvent(SubmitEvent event)` | `SubmitEvent` |

All event types extend `SyntheticEvent`, which provides `getTarget()`, `getType()`, `getBubbles()`, `preventDefault()`, and `stopPropagation()`. The `getTarget()` method returns an `EventTarget` with typed properties like `getValue()`, `getChecked()`, `getId()`, `getTagName()`, and `getClassName()`.

`MouseEvent` extends `SyntheticEvent` and adds mouse-specific properties. Mouse event handlers use `EventHandler`, so cast the `SyntheticEvent` to `MouseEvent` when needed.

## Mapping Handlers to React Props

| React Prop | Handler Type | Setter (Java) | Builder Method |
|------------|-------------|---------------|----------------|
| `onClick` | `EventHandler` | `React.setOnClick(props, h)` | `.onClick(h)` |
| `onChange` | `ChangeEventHandler` | `React.setOnChange(props, h)` | `.onChange(h)` |
| `onKeyDown` | `KeyboardEventHandler` | `React.setOnKeyDown(props, h)` | `.onKeyDown(h)` |
| `onKeyUp` | `KeyboardEventHandler` | `React.setOnKeyUp(props, h)` | `.onKeyUp(h)` |
| `onFocus` | `FocusEventHandler` | `React.setOnFocus(props, h)` | `.onFocus(h)` |
| `onBlur` | `FocusEventHandler` | `React.setOnBlur(props, h)` | `.onBlur(h)` |
| `onSubmit` | `SubmitEventHandler` | `React.setOnSubmit(props, h)` | `.onSubmit(h)` |
| `onMouseDown` | `EventHandler` | `React.setOnMouseDown(props, h)` | `.onMouseDown(h)` |
| `onMouseUp` | `EventHandler` | `React.setOnMouseUp(props, h)` | `.onMouseUp(h)` |
| `onMouseEnter` | `EventHandler` | `React.setOnMouseEnter(props, h)` | `.onMouseEnter(h)` |
| `onMouseLeave` | `EventHandler` | `React.setOnMouseLeave(props, h)` | `.onMouseLeave(h)` |

## SyntheticEvent Hierarchy

All events extend `SyntheticEvent`:

```
SyntheticEvent (getTarget, preventDefault, stopPropagation)
├── ChangeEvent
├── KeyboardEvent (getKey, getCode, getAltKey, getCtrlKey, ...)
├── MouseEvent (getClientX, getClientY, getButton, ...)
├── FocusEvent (getRelatedTarget)
└── SubmitEvent
```

### EventTarget Interface

`SyntheticEvent.getTarget()` returns an `EventTarget`:

```java
public interface EventTarget extends JSObject {
    String getValue();       // text input value
    boolean getChecked();    // checkbox state
    String getType();        // input type attribute
    String getId();          // element id
    String getTagName();     // e.g., "INPUT", "BUTTON"
    String getClassName();   // CSS class(es)
}
```

## onClick -- EventHandler

The most common event handler. Receives a `SyntheticEvent`.

### Java Functional

```java
button("Click me").onClick(e -> {
    count.updateInt(n -> n + 1);
}).build()
```

### Java Builder DSL

```java
Button.create()
    .text("Click me")
    .onClick(e -> count.updateInt(n -> n + 1))
    .build()
```

### Kotlin DSL

```kotlin
button {
    +"Click me"
    onClick { count++ }
}
```

## onChange -- ChangeEventHandler with ChangeEvent

Used for text inputs, checkboxes, selects. The `ChangeEvent` provides typed access to the input's value.

`ChangeEvent` extends `SyntheticEvent`. Access input properties via `getTarget()`:

```java
// event.getTarget() returns EventTarget with getValue(), getChecked(), etc.
e.getTarget().getValue()     // text input value
e.getTarget().getChecked()   // checkbox state
```

### Text Input (Java)

```java
StateHandle<String> text = Hooks.useState("");

input("text")
    .value(text.getString())
    .onChange(e -> text.setString(e.getTarget().getValue()))
    .build()
```

### Checkbox (Java)

```java
StateHandle<Boolean> agreed = Hooks.useState(false);

input("checkbox")
    .checked(agreed.getBool())
    .onChange(e -> agreed.setBool(e.getTarget().getChecked()))
    .build()
```

### Kotlin DSL -- Text Input

```kotlin
var text by state("")

input("text") {
    value(text)
    onChange { text = it.getTarget().getValue() }
}
```

### Kotlin DSL -- Checkbox

```kotlin
var agreed by state(false)

input("checkbox") {
    checked(agreed)
    onChange { agreed = it.getTarget().getChecked() }
}
```

## onKeyDown / onKeyUp -- KeyboardEventHandler with KeyboardEvent

`KeyboardEvent` extends `SyntheticEvent` and adds:

```java
String getKey()        // "Enter", "Escape", "a", etc.
String getCode()       // "KeyA", "Enter", "Space", etc.
boolean getAltKey()
boolean getCtrlKey()
boolean getMetaKey()
boolean getShiftKey()
boolean getRepeat()    // true if key held down
// getTarget() inherited from SyntheticEvent
```

### Detecting Enter Key (Java)

```java
input("text")
    .value(query.getString())
    .onChange(e -> query.setString(e.getTarget().getValue()))
    .onKeyDown(e -> {
        if ("Enter".equals(e.getKey())) {
            performSearch(query.getString());
        }
    })
    .build()
```

### Keyboard Shortcuts (Kotlin)

```kotlin
div {
    onKeyDown { e ->
        when {
            e.getCtrlKey() && e.getKey() == "s" -> save()
            e.getKey() == "Escape" -> close()
        }
    }
}
```

## onFocus / onBlur -- FocusEventHandler

Both receive a `FocusEvent` (extends `SyntheticEvent`, adds `getRelatedTarget()`). Used for tracking focus state.

### Java Example

```java
StateHandle<Boolean> focused = Hooks.useState(false);

input("text")
    .onFocus(e -> focused.setBool(true))
    .onBlur(e -> focused.setBool(false))
    .className(focused.getBool() ? "input-focused" : "input-normal")
    .build()
```

### Kotlin DSL

```kotlin
var focused by state(false)

input("text") {
    onFocus { focused = true }
    onBlur { focused = false }
    className(if (focused) "input-focused" else "input-normal")
}
```

## onSubmit -- Form Handling with SubmitEventHandler

Receives a `SubmitEvent` (extends `SyntheticEvent`). Call `preventDefault()` directly on the event object.

### Java Form Example

```java
static ReactElement loginForm(JSObject props) {
    StateHandle<String> email = Hooks.useState("");
    StateHandle<String> password = Hooks.useState("");

    return form(
        div(
            label("Email"),
            input("email").value(email.getString())
                .onChange(e -> email.setString(e.getTarget().getValue())).build()
        ),
        div(
            label("Password"),
            input("password").value(password.getString())
                .onChange(e -> password.setString(e.getTarget().getValue())).build()
        ),
        button("Submit").type("submit").build()
    ).onSubmit(e -> {
        e.preventDefault();
        submitLogin(email.getString(), password.getString());
    }).build();
```

Note: `form(...)` returns an `ElementBuilder` (when called on `Html.form(String text)` it returns `ReactElement`, but when using the builder chain via `DomBuilder.Form.create()`, you chain `.onSubmit()` before `.build()`).

### Kotlin DSL

```kotlin
form {
    onSubmit { e ->
        e.preventDefault()
        submitLogin(email, password)
    }
    // form fields...
    button { +"Submit"; type("submit") }
}
```

## MouseEvent Interface

`MouseEvent` extends `SyntheticEvent` and adds mouse-specific properties. Cast the `SyntheticEvent` from an `EventHandler` to `MouseEvent`:

```java
public interface MouseEvent extends SyntheticEvent {
    double getClientX();
    double getClientY();
    double getPageX();
    double getPageY();
    int getButton();        // 0=left, 1=middle, 2=right
    boolean getAltKey();
    boolean getCtrlKey();
    boolean getMetaKey();
    boolean getShiftKey();
    // getTarget() inherited from SyntheticEvent
}
```

### Reading Mouse Coordinates (Java)

```java
div("Click area").onClick(e -> {
    MouseEvent me = (MouseEvent) e;
    JsUtil.consoleLog("Clicked at " + me.getClientX() + ", " + me.getClientY());
}).build()
```

## Important: Never Use setProperty for Event Handlers

Always use the dedicated setter methods (`React.setOnClick`, `.onClick()`, etc.). This is critical:

```java
// WRONG -- silently wraps as Java object, handler never fires
React.setProperty(props, "onClick", handler);

// CORRECT -- passes raw JS function via @JSFunctor
React.setOnClick(props, handler);
```

The `@JSFunctor` annotation on the handler interfaces tells TeaVM to pass the function as a raw JavaScript function reference. Using `setProperty` bypasses this and wraps it in a Java object wrapper.

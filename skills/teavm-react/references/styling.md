# Styling Reference

Read this file when applying visual styles to teavm-react components. Covers four approaches: CSS class names, Java inline style objects, Kotlin `StyleBuilder` DSL, and Kotlin `css()` shorthand.

## Approach 1: className with External CSS

Write CSS in the HTML shell or a linked stylesheet. Reference class names from components.

### Java

```java
button("Save").className("btn btn-primary").build()

// Or with DomBuilder
Div.create().className("card shadow-lg").child(...).build()
```

### Kotlin

```kotlin
div {
    className("card shadow-lg")
    button { +"Save"; className("btn btn-primary") }
}
```

Best for: apps with design systems, CSS frameworks, or shared stylesheets.

## Approach 2: Inline Style Builder (Java)

Use the `Style` fluent builder to create type-safe inline styles:

```java
Style style = Style.create()
    .backgroundColor("#282c34")
    .color("white")
    .padding("20px")
    .borderRadius("8px");

// Functional DSL
button("Styled").style(style).build();

// Builder DSL
Div.create().style(style).child(H2.create().text("Styled").build()).build();
```

Property names use **camelCase** (matching React's convention): `backgroundColor`, `fontSize`, `borderRadius`. The `Style` class provides fluent methods for all common CSS properties.

Best for: Java projects needing dynamic styles without Kotlin.

## Approach 3: Kotlin StyleBuilder DSL

The `style { }` block inside any `HtmlBuilder` element provides a type-safe builder:

```kotlin
div {
    style {
        display = "flex"
        flexDirection = "column"
        gap = "12px"
        backgroundColor = "#282c34"
        color = "white"
        padding = "20px"
        borderRadius = "8px"
    }
    h2 { +"Styled Content" }
}
```

### All StyleBuilder Properties

#### Layout
`display`, `position`, `top`, `right`, `bottom`, `left`, `zIndex`, `overflow`, `overflowX`, `overflowY`, `float`, `clear`

#### Flexbox
`flexDirection`, `flexWrap`, `justifyContent`, `alignItems`, `alignContent`, `alignSelf`, `flex`, `flexGrow`, `flexShrink`, `flexBasis`, `gap`, `rowGap`, `columnGap`, `order`

#### Grid
`gridTemplateColumns`, `gridTemplateRows`, `gridColumn`, `gridRow`, `gridGap`, `gridArea`, `gridTemplate`

#### Box Model
`width`, `height`, `minWidth`, `minHeight`, `maxWidth`, `maxHeight`, `margin`, `marginTop`, `marginRight`, `marginBottom`, `marginLeft`, `padding`, `paddingTop`, `paddingRight`, `paddingBottom`, `paddingLeft`, `boxSizing`

#### Border
`border`, `borderTop`, `borderRight`, `borderBottom`, `borderLeft`, `borderRadius`, `borderColor`, `borderStyle`, `borderWidth`, `outline`

#### Colors and Background
`color`, `backgroundColor`, `background`, `backgroundImage`, `backgroundSize`, `backgroundPosition`, `backgroundRepeat`, `opacity`

#### Typography
`fontSize`, `fontWeight`, `fontFamily`, `fontStyle`, `lineHeight`, `letterSpacing`, `textAlign`, `textDecoration`, `textTransform`, `whiteSpace`, `wordBreak`, `wordWrap`, `textOverflow`

#### Effects
`boxShadow`, `textShadow`, `transform`, `transition`, `animation`, `cursor`, `pointerEvents`, `userSelect`, `filter`, `visibility`

#### Custom Properties

For properties not covered above, use `property()`:

```kotlin
style {
    property("WebkitAppearance", "none")
    property("scrollBehavior", "smooth")
}
```

## Approach 4: Kotlin css() Shorthand

Pass a CSS-style string (kebab-case). It is parsed into a camelCase style object automatically.

```kotlin
div {
    css("background-color: #282c34; color: white; padding: 20px; border-radius: 8px")
    h2 { +"Quick Styled" }
}
```

The `css()` function calls `parseCssString()` internally, which converts kebab-case property names (e.g., `background-color`) to camelCase (`backgroundColor`).

Best for: quick prototyping or porting CSS snippets.

## Conditional Styles

### Java

```java
Style style = Style.create()
    .color(isError ? "red" : "green")
    .fontWeight(isError ? "bold" : "normal");
```

### Kotlin

```kotlin
div {
    style {
        color = if (isError) "red" else "green"
        fontWeight = if (isError) "bold" else "normal"
    }
}
```

## Combining className and Inline Styles

Both can be used on the same element. Inline styles override class styles (standard CSS specificity):

```kotlin
div {
    className("card")
    style { padding = "24px"; boxShadow = "0 2px 8px rgba(0,0,0,0.1)" }
}
```

package ca.weblite.teavmreact.html;

import ca.weblite.teavmreact.core.React;
import org.teavm.jso.JSObject;

/**
 * Fluent builder for React inline style objects. Hides the underlying
 * JSObject construction behind a type-safe Java API.
 *
 * <p>Usage:
 * <pre>
 * Div.create()
 *     .style(Style.create()
 *         .background("#333")
 *         .color("#fff")
 *         .padding("16px"))
 *     .build();
 * </pre>
 */
public class Style {

    private final JSObject obj;

    private Style() {
        obj = React.createObject();
    }

    public static Style create() {
        return new Style();
    }

    /**
     * Sets an arbitrary CSS property (use camelCase names, e.g. "borderRadius").
     */
    public Style set(String property, String value) {
        React.setProperty(obj, property, value);
        return this;
    }

    /**
     * Sets a numeric CSS property value.
     */
    public Style set(String property, int value) {
        React.setProperty(obj, property, value);
        return this;
    }

    /**
     * Sets a numeric CSS property value.
     */
    public Style set(String property, double value) {
        React.setProperty(obj, property, value);
        return this;
    }

    // -----------------------------------------------------------------------
    // Common CSS properties
    // -----------------------------------------------------------------------

    public Style background(String value) { return set("background", value); }
    public Style backgroundColor(String value) { return set("backgroundColor", value); }
    public Style color(String value) { return set("color", value); }
    public Style opacity(double value) { return set("opacity", value); }

    public Style padding(String value) { return set("padding", value); }
    public Style paddingTop(String value) { return set("paddingTop", value); }
    public Style paddingRight(String value) { return set("paddingRight", value); }
    public Style paddingBottom(String value) { return set("paddingBottom", value); }
    public Style paddingLeft(String value) { return set("paddingLeft", value); }

    public Style margin(String value) { return set("margin", value); }
    public Style marginTop(String value) { return set("marginTop", value); }
    public Style marginRight(String value) { return set("marginRight", value); }
    public Style marginBottom(String value) { return set("marginBottom", value); }
    public Style marginLeft(String value) { return set("marginLeft", value); }

    public Style border(String value) { return set("border", value); }
    public Style borderRadius(String value) { return set("borderRadius", value); }
    public Style borderColor(String value) { return set("borderColor", value); }

    public Style width(String value) { return set("width", value); }
    public Style height(String value) { return set("height", value); }
    public Style minWidth(String value) { return set("minWidth", value); }
    public Style minHeight(String value) { return set("minHeight", value); }
    public Style maxWidth(String value) { return set("maxWidth", value); }
    public Style maxHeight(String value) { return set("maxHeight", value); }

    public Style display(String value) { return set("display", value); }
    public Style position(String value) { return set("position", value); }
    public Style top(String value) { return set("top", value); }
    public Style right(String value) { return set("right", value); }
    public Style bottom(String value) { return set("bottom", value); }
    public Style left(String value) { return set("left", value); }
    public Style zIndex(int value) { return set("zIndex", value); }

    public Style flexDirection(String value) { return set("flexDirection", value); }
    public Style justifyContent(String value) { return set("justifyContent", value); }
    public Style alignItems(String value) { return set("alignItems", value); }
    public Style flexWrap(String value) { return set("flexWrap", value); }
    public Style flex(String value) { return set("flex", value); }
    public Style gap(String value) { return set("gap", value); }

    public Style fontSize(String value) { return set("fontSize", value); }
    public Style fontWeight(String value) { return set("fontWeight", value); }
    public Style fontFamily(String value) { return set("fontFamily", value); }
    public Style textAlign(String value) { return set("textAlign", value); }
    public Style textDecoration(String value) { return set("textDecoration", value); }
    public Style lineHeight(String value) { return set("lineHeight", value); }
    public Style letterSpacing(String value) { return set("letterSpacing", value); }

    public Style overflow(String value) { return set("overflow", value); }
    public Style overflowX(String value) { return set("overflowX", value); }
    public Style overflowY(String value) { return set("overflowY", value); }
    public Style cursor(String value) { return set("cursor", value); }
    public Style transition(String value) { return set("transition", value); }
    public Style transform(String value) { return set("transform", value); }
    public Style boxShadow(String value) { return set("boxShadow", value); }

    // -----------------------------------------------------------------------
    // Package-private accessor for DomBuilder/ElementBuilder
    // -----------------------------------------------------------------------

    JSObject toJSObject() {
        return obj;
    }
}

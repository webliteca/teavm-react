package ca.weblite.teavmreact.docs;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.html.Style;
import org.teavm.jso.JSObject;

/**
 * Helper for creating HTML elements with className or style attributes.
 * This fills the gap between Html.* (no attributes) and DomBuilder (verbose).
 *
 * Usage: El.div("my-class", child1, child2)
 *        El.table("api-table", thead, tbody)
 */
public final class El {

    private El() {}

    public static ReactElement classed(String tag, String className, ReactElement... children) {
        JSObject props = React.createObject();
        React.setProperty(props, "className", className);
        JSObject arr = React.createArray();
        for (ReactElement c : children) {
            React.arrayPush(arr, c);
        }
        return React.createElementFromArray(tag, props, arr);
    }

    public static ReactElement styled(String tag, Style style, ReactElement... children) {
        // Use DomBuilder to set style since Style.toJSObject() is package-private
        ca.weblite.teavmreact.html.DomBuilder.Div builder = ca.weblite.teavmreact.html.DomBuilder.Div.create();
        // We can't directly use DomBuilder for arbitrary tags with style, so use a workaround
        // For now, this method only supports div. For other tags, use DomBuilder directly.
        ca.weblite.teavmreact.html.DomBuilder db;
        switch (tag) {
            case "div": db = ca.weblite.teavmreact.html.DomBuilder.Div.create(); break;
            case "span": db = ca.weblite.teavmreact.html.DomBuilder.Span.create(); break;
            case "section": db = ca.weblite.teavmreact.html.DomBuilder.Section.create(); break;
            case "p": db = ca.weblite.teavmreact.html.DomBuilder.P.create(); break;
            default: db = ca.weblite.teavmreact.html.DomBuilder.Div.create(); break;
        }
        db.style(style);
        for (ReactElement c : children) {
            db.child(c);
        }
        return db.build();
    }

    public static ReactElement classedText(String tag, String className, String text) {
        JSObject props = React.createObject();
        React.setProperty(props, "className", className);
        return React.createElementWithText(tag, props, text);
    }

    // ---- Shortcuts ----

    public static ReactElement div(String className, ReactElement... children) {
        return classed("div", className, children);
    }

    public static ReactElement span(String className, ReactElement... children) {
        return classed("span", className, children);
    }

    public static ReactElement section(String className, ReactElement... children) {
        return classed("section", className, children);
    }

    public static ReactElement nav(String className, ReactElement... children) {
        return classed("nav", className, children);
    }

    public static ReactElement table(String className, ReactElement... children) {
        return classed("table", className, children);
    }

    public static ReactElement p(String className, String text) {
        return classedText("p", className, text);
    }

    public static ReactElement p(String className, ReactElement... children) {
        return classed("p", className, children);
    }

    public static ReactElement styledDiv(Style style, ReactElement... children) {
        return styled("div", style, children);
    }
}

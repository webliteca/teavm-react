package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Documentation page: Passing Props to Components.
 */
public class PropsPage {

    public static ReactElement render(JSObject props) {
        return El.div("docs-page",

            h1("Passing Props to a Component"),
            p("React components use props to communicate with each other. " +
              "Every parent component can pass information to its child components " +
              "by giving them props. In teavm-react, props are received as a JSObject " +
              "parameter in your render function."),

            // Section 1: Receiving props
            h2("How Components Receive Props"),
            p(
                text("In teavm-react, every component render function receives a "),
                code("JSObject"),
                text(" parameter representing the props object. You extract values from it using "),
                text("typed accessor methods on the "),
                code("React"),
                text(" class.")
            ),

            CodeTabs.create(
                """
                import ca.weblite.teavmreact.core.React;
                import ca.weblite.teavmreact.core.ReactElement;
                import org.teavm.jso.JSObject;
                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

                public class Greeting {
                    public static ReactElement render(JSObject props) {
                        String name = React.jsToString(
                            React.getProperty(props, "name")
                        );
                        return h1("Hello, " + name + "!");
                    }
                }""",
                """
                import ca.weblite.teavmreact.kotlin.*

                val Greeting = component("Greeting") { props ->
                    val name = props.string("name")
                    h1("Hello, ${'$'}name!")
                }"""
            ),

            // Section 2: Reading typed props
            h2("Reading Props with Typed Accessors"),
            p("teavm-react provides typed accessors for the most common prop types. " +
              "These use JS coercion under the hood, so you always get the correct Java type."),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    // String props
                    String label = React.jsToString(
                        React.getProperty(props, "label")
                    );

                    // Integer props
                    int count = React.jsToInt(
                        React.getProperty(props, "count")
                    );

                    // Boolean props
                    boolean isActive = React.jsToBool(
                        React.getProperty(props, "isActive")
                    );

                    return div(
                        h2(label),
                        p("Count: " + count),
                        p("Active: " + isActive)
                    );
                }""",
                """
                val InfoCard = component("InfoCard") { props ->
                    val label = props.string("label")
                    val count = props.int("count")
                    val isActive = props.bool("isActive")

                    div(
                        h2(label),
                        p("Count: ${'$'}count"),
                        p("Active: ${'$'}isActive")
                    )
                }"""
            ),

            Callout.note("Type safety",
                p("The typed accessors use JS coercion: jsToInt uses bitwise-or (|0), " +
                  "jsToBool uses double-bang (!!), and jsToString uses string " +
                  "concatenation ('' + value). If a prop is missing, you get 0, false, " +
                  "or \"undefined\" respectively.")
            ),

            // Section 3: Passing props when creating elements
            h2("Passing Props to Child Components"),
            p("When you create a component element, you build a props object " +
              "and set properties on it before passing it to createElement."),

            CodeTabs.create(
                """
                import ca.weblite.teavmreact.core.React;
                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

                public class App {
                    public static ReactElement render(JSObject props) {
                        // Build the props object
                        JSObject greetingProps = React.createObject();
                        React.setProperty(greetingProps, "name", "Alice");

                        // Create the element with props
                        JSObject greetingComp = React.wrapComponent(
                            Greeting::render, "Greeting"
                        );
                        ReactElement greeting = React.createElement(
                            greetingComp, greetingProps
                        );

                        return div(
                            h1("My App"),
                            greeting
                        );
                    }
                }""",
                """
                val App = component("App") {
                    div(
                        h1("My App"),
                        Greeting { "name" to "Alice" }
                    )
                }"""
            ),

            // Section 4: Default values pattern
            h2("Default Values for Props"),
            p("Since props may be missing, you often want to provide default values. " +
              "Use a simple null check or helper method."),

            CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    // Check for null/undefined and provide a default
                    JSObject rawName = React.getProperty(props, "name");
                    String name = (rawName != null)
                        ? React.jsToString(rawName)
                        : "World";

                    int size = React.getProperty(props, "size") != null
                        ? React.jsToInt(React.getProperty(props, "size"))
                        : 16;

                    return p("Hello, " + name + " (size=" + size + ")");
                }""",
                """
                val Hello = component("Hello") { props ->
                    val name = props.stringOrNull("name") ?: "World"
                    val size = props.intOrNull("size") ?: 16

                    p("Hello, ${'$'}name (size=${'$'}size)")
                }"""
            ),

            Callout.deepDive("Why not Java Optional?",
                p("TeaVM compiles to JavaScript, so the JSObject returned by " +
                  "getProperty may be a JS undefined. Java's Optional is not used " +
                  "because the values live in JS space. A simple null check on the " +
                  "raw JSObject is the idiomatic approach.")
            ),

            // Section 5: Passing children
            h2("Passing Children to Components"),
            p("In React, children are a special prop. In teavm-react, you can pass " +
              "children by including them after the props in createElement."),

            CodeTabs.create(
                """
                // A Card component that wraps children in a styled container
                public class Card {
                    public static ReactElement render(JSObject props) {
                        String title = React.jsToString(
                            React.getProperty(props, "title")
                        );

                        // Children are passed automatically via React's
                        // props.children mechanism. Access them with:
                        JSObject children = React.getProperty(
                            props, "children"
                        );

                        return El.div("card",

                            h3(title),
                            // Render the children element
                            React.createElementFromRaw(children)
                        );
                    }
                }

                // Using the Card component with children:
                public class App {
                    public static ReactElement render(JSObject props) {
                        JSObject cardProps = React.createObject();
                        React.setProperty(cardProps, "title", "My Card");

                        JSObject cardComp = React.wrapComponent(
                            Card::render, "Card"
                        );

                        // Pass children as extra args to createElement
                        return React.createElement(
                            cardComp,
                            cardProps,
                            new JSObject[]{ p("Card content here.") }
                        );
                    }
                }""",
                """
                val Card = component("Card") { props ->
                    val title = props.string("title")
                    div {
                        className = "card"
                        h3(title)
                        children() // renders props.children
                    }
                }

                val App = component("App") {
                    Card {
                        "title" to "My Card"
                        p("Card content here.")
                    }
                }"""
            ),

            Callout.pitfall("Props are read-only",
                p("Never try to modify the props object. Props flow one way: " +
                  "from parent to child. If a component needs to change data, " +
                  "use state instead (covered in the State page).")
            ),

            // Recap
            h2("Recap"),
            ul(
                li("Components receive props as a JSObject parameter."),
                li(
                    text("Use "),
                    code("React.jsToString()"),
                    text(", "),
                    code("React.jsToInt()"),
                    text(", and "),
                    code("React.jsToBool()"),
                    text(" to extract typed values.")
                ),
                li(
                    text("Build props with "),
                    code("React.createObject()"),
                    text(" and "),
                    code("React.setProperty()"),
                    text(".")
                ),
                li("Provide default values with null checks on the raw JSObject."),
                li("Children are a special prop passed via createElement.")
            )
        );
    }
}

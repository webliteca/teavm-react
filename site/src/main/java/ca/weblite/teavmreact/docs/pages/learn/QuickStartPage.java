package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.html.DomBuilder.Div;
import ca.weblite.teavmreact.html.DomBuilder.Section;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class QuickStartPage {

    public static ReactElement render(JSObject props) {
        return Div.create().className("doc-page")
            .child(h1("Quick Start"))
            .child(p("This page introduces the core concepts you will use every day when building "
              + "with teavm-react. By the end, you will know how to create components, "
              + "display data, respond to events, and update the screen."))
            .child(hr())
            .child(creatingComponentsSection())
            .child(hr())
            .child(displayingDataSection())
            .child(hr())
            .child(addingStylesSection())
            .child(hr())
            .child(respondingToEventsSection())
            .child(hr())
            .child(updatingTheScreenSection())
            .child(hr())
            .child(conditionalRenderingSection())
            .child(hr())
            .child(renderingListsSection())
            .child(hr())
            .child(sharingDataSection())
            .build();
    }

    // -----------------------------------------------------------------------
    // 1. Creating Components
    // -----------------------------------------------------------------------

    private static ReactElement creatingComponentsSection() {
        String javaCode = """
                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

                public class Greeting {
                    static ReactElement render(JSObject props) {
                        return h1("Hello, teavm-react!");
                    }
                }

                // Using it in another component:
                component(Greeting::render, "Greeting")""";

        String kotlinCode = """
                val Greeting = fc("Greeting") {
                    h1 { +"Hello, teavm-react!" }
                }

                // Using it in another component:
                +Greeting""";

        return Section.create().className("doc-section")
            .child(h2("Creating Components"))
            .child(p("Components are the building blocks of a teavm-react application. "
              + "A component is a static method that accepts a JSObject props parameter "
              + "and returns a ReactElement."))
            .child(p("You register a component with React using the component() helper, "
              + "passing a method reference and a display name:"))
            .child(CodeTabs.create(javaCode, kotlinCode))
            .child(Callout.note("Naming Convention",
                p("Component method names typically use render, but any name works. "
                  + "The display name passed to component() is what appears in React DevTools.")))
            .build();
    }

    // -----------------------------------------------------------------------
    // 2. Displaying Data
    // -----------------------------------------------------------------------

    private static ReactElement displayingDataSection() {
        String javaCode = """
                static ReactElement render(JSObject props) {
                    String userName = "Alice";
                    return div(
                        h1("Welcome, " + userName + "!"),
                        p("Your dashboard is ready."),
                        div(
                            span("Status: "),
                            strong("Active")
                        )
                    );
                }""";

        String kotlinCode = """
                val Dashboard = fc("Dashboard") {
                    val userName = "Alice"
                    div {
                        h1 { +"Welcome, $userName!" }
                        p { +"Your dashboard is ready." }
                        div {
                            span { +"Status: " }
                            strong { +"Active" }
                        }
                    }
                }""";

        return Section.create().className("doc-section")
            .child(h2("Displaying Data"))
            .child(p("Use the Html DSL to create elements. Text content is passed as a String argument. "
              + "You can nest elements by passing child ReactElements as arguments."))
            .child(CodeTabs.create(javaCode, kotlinCode))
            .child(p("The DSL provides factory methods for every standard HTML element: "
              + "div(), h1(), p(), span(), strong(), em(), ul(), li(), table(), and many more."))
            .build();
    }

    // -----------------------------------------------------------------------
    // 3. Adding Styles
    // -----------------------------------------------------------------------

    private static ReactElement addingStylesSection() {
        String javaCode = """
                static ReactElement render(JSObject props) {
                    return El.div("my-card",

                        h1("Styled Heading"),
                        p("This paragraph has a CSS class.")
                    );
                }

                // For inline styles, use the Style builder:
                // import ca.weblite.teavmreact.html.Style;
                // .style(Style.create()
                //     .set("color", "blue")
                //     .set("fontSize", "18px"))""";

        String kotlinCode = """
                val StyledCard = fc("StyledCard") {
                    div {
                        className = "my-card"
                        h1 { +"Styled Heading" }
                        p { +"This paragraph has a CSS class." }
                    }
                }""";

        return El.section("doc-section",

            h2("Adding Styles"),
            p("Apply CSS class names with the .className() method. Chain it onto any "
              + "element builder, then call .build() to produce the final ReactElement."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.note("className, not class",
                p("React uses className instead of class because class is a reserved "
                  + "word in both Java and JavaScript."))
        );
    }

    // -----------------------------------------------------------------------
    // 4. Responding to Events
    // -----------------------------------------------------------------------

    private static ReactElement respondingToEventsSection() {
        String javaCode = """
                static ReactElement render(JSObject props) {
                    return div(
                        button("Click me!")
                            .onClick(e -> {
                                // handle click
                            })
                            .build(),
                        input("text")
                            .placeholder("Type here...")
                            .onChange(e -> {
                                String value = e.getTarget().getValue();
                                // handle input change
                            })
                            .build()
                    );
                }""";

        String kotlinCode = """
                val Form = fc("Form") {
                    div {
                        button {
                            +"Click me!"
                            onClick { e ->
                                // handle click
                            }
                        }
                        input {
                            type = "text"
                            placeholder = "Type here..."
                            onChange { e ->
                                val value = e.targetValue
                                // handle input change
                            }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Responding to Events"),
            p("Interactive elements like button() and input() return an ElementBuilder "
              + "with event handler methods. Use .onClick(), .onChange(), .onKeyDown(), "
              + "and others to attach handlers."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.pitfall("Don't forget .build()",
                p("Event handlers are set on an ElementBuilder. You must call .build() "
                  + "at the end to produce the actual ReactElement. Forgetting .build() "
                  + "will cause a compile error since ElementBuilder is not a ReactElement."))
        );
    }

    // -----------------------------------------------------------------------
    // 5. Updating the Screen
    // -----------------------------------------------------------------------

    private static ReactElement updatingTheScreenSection() {
        String javaCode = """
                static ReactElement render(JSObject props) {
                    var count = Hooks.useState(0);
                    return div(
                        h1("Count: " + count.getInt()),
                        button("Increment")
                            .onClick(e -> count.updateInt(n -> n + 1))
                            .build(),
                        button("Reset")
                            .onClick(e -> count.setInt(0))
                            .build()
                    );
                }""";

        String kotlinCode = """
                val Counter = fc("Counter") {
                    var count by state(0)
                    div {
                        h1 { +"Count: $count" }
                        button {
                            +"Increment"
                            onClick { count++ }
                        }
                        button {
                            +"Reset"
                            onClick { count = 0 }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Updating the Screen"),
            p("Use the useState hook to add state to your component. When state changes, "
              + "React re-renders the component with the new value."),
            CodeTabs.create(javaCode, kotlinCode),
            p("Try the live counter below:"),
            LiveDemo.create(QuickStartPage::counterDemo),
            Callout.note("Functional Updates",
                p("Use updateInt(n -> n + 1) instead of setInt(count.getInt() + 1) when "
                  + "the new value depends on the previous value. This ensures correctness "
                  + "when multiple updates are batched."))
        );
    }

    private static ReactElement counterDemo(JSObject props) {
        var count = Hooks.useState(0);
        return El.div("counter-demo",

            p("Count: " + count.getInt()),
            button("Increment")
                .className("demo-btn")
                .onClick(e -> count.updateInt(n -> n + 1))
                .build(),
            button("Reset")
                .className("demo-btn")
                .onClick(e -> count.setInt(0))
                .build()
        );
    }

    // -----------------------------------------------------------------------
    // 6. Conditional Rendering
    // -----------------------------------------------------------------------

    private static ReactElement conditionalRenderingSection() {
        String javaCode = """
                static ReactElement render(JSObject props) {
                    var loggedIn = Hooks.useState(false);
                    return div(
                        loggedIn.getBool()
                            ? h1("Welcome back!")
                            : h1("Please sign in."),
                        button(loggedIn.getBool() ? "Log out" : "Log in")
                            .onClick(e -> loggedIn.setBool(!loggedIn.getBool()))
                            .build()
                    );
                }""";

        String kotlinCode = """
                val LoginStatus = fc("LoginStatus") {
                    var loggedIn by state(false)
                    div {
                        if (loggedIn) {
                            h1 { +"Welcome back!" }
                        } else {
                            h1 { +"Please sign in." }
                        }
                        button {
                            +(if (loggedIn) "Log out" else "Log in")
                            onClick { loggedIn = !loggedIn }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Conditional Rendering"),
            p("Use Java's ternary operator or if/else statements to conditionally "
              + "render different elements. Since component methods are plain Java, "
              + "you can use any control flow you like."),
            CodeTabs.create(javaCode, kotlinCode)
        );
    }

    // -----------------------------------------------------------------------
    // 7. Rendering Lists
    // -----------------------------------------------------------------------

    private static ReactElement renderingListsSection() {
        String javaCode = """
                static ReactElement render(JSObject props) {
                    var items = List.of("Apple", "Banana", "Cherry");
                    return ul(
                        mapToElements(items, item -> li(item))
                    );
                }

                // For more control, use a loop:
                static ReactElement renderManual(JSObject props) {
                    String[] names = {"Alice", "Bob", "Carol"};
                    ReactElement[] lis = new ReactElement[names.length];
                    for (int i = 0; i < names.length; i++) {
                        lis[i] = li(names[i]);
                    }
                    return ul(lis);
                }""";

        String kotlinCode = """
                val FruitList = fc("FruitList") {
                    val items = listOf("Apple", "Banana", "Cherry")
                    ul {
                        items.forEach { item ->
                            li { +item }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Rendering Lists"),
            p("Use mapToElements() to transform a list of data into an array of "
              + "ReactElements. You can also use a simple for-loop to build the array manually."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.note("Keys",
                p("When rendering lists of components, provide a unique key for each item "
                  + "using .key() on the ElementBuilder. Keys help React identify which items "
                  + "changed, were added, or were removed."))
        );
    }

    // -----------------------------------------------------------------------
    // 8. Sharing Data Between Components
    // -----------------------------------------------------------------------

    private static ReactElement sharingDataSection() {
        String javaCode = """
                public class ParentComponent {
                    static ReactElement render(JSObject props) {
                        var name = Hooks.useState("World");
                        return div(
                            input("text")
                                .value(name.getString())
                                .onChange(e -> name.setString(e.getTarget().getValue()))
                                .build(),
                            // Pass data down via the render call
                            component(p -> greeting(p, name.getString()), "Greeting")
                        );
                    }

                    private static ReactElement greeting(JSObject props, String name) {
                        return h1("Hello, " + name + "!");
                    }
                }""";

        String kotlinCode = """
                val ParentComponent = fc("ParentComponent") {
                    var name by state("World")
                    div {
                        input {
                            type = "text"
                            value = name
                            onChange { name = it.targetValue }
                        }
                        // Child reads the name via closure
                        h1 { +"Hello, $name!" }
                    }
                }""";

        return El.section("doc-section",

            h2("Sharing Data Between Components"),
            p("In teavm-react, you share data between components by lifting state up "
              + "to a common parent. The parent owns the state and passes it down "
              + "to children through the component render call or via closures."),
            CodeTabs.create(javaCode, kotlinCode),
            p("For deeply nested data, consider using React Context via ReactContext.create() "
              + "and Hooks.useContext() to avoid passing data through many levels of components.")
        );
    }
}

package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class FirstComponentPage {

    public static ReactElement render(JSObject props) {
        return El.div("doc-page",

            h1("Your First Component"),
            p("Components are the fundamental building blocks of every teavm-react "
              + "application. They are reusable pieces of UI that can be composed "
              + "together to build complex interfaces."),
            hr(),
            whatIsAComponentSection(),
            hr(),
            functionalApproachSection(),
            hr(),
            builderDslApproachSection(),
            hr(),
            classBasedApproachSection(),
            hr(),
            nestingComponentsSection(),
            hr(),
            liveDemoSection()
        );
    }

    // -----------------------------------------------------------------------
    // 1. What is a Component
    // -----------------------------------------------------------------------

    private static ReactElement whatIsAComponentSection() {
        return El.section("doc-section",

            h2("What is a Component?"),
            p("A component is a function that returns a ReactElement describing what "
              + "should appear on screen. React calls your component function whenever "
              + "it needs to figure out what to display."),
            p("teavm-react offers three ways to define components:"),
            ol(
                li(fragment(
                    strong("Functional"),
                    text(" - A static method wrapped with component(). The simplest and most common approach.")
                )),
                li(fragment(
                    strong("Builder DSL"),
                    text(" - Inner classes extending DomBuilder for a more structured approach.")
                )),
                li(fragment(
                    strong("Class-Based"),
                    text(" - Classes extending ReactView for stateful lifecycle management.")
                ))
            ),
            Callout.note("Which should I use?",
                p("Start with the functional approach. It is the simplest and most "
                  + "similar to modern React. The builder and class-based approaches "
                  + "are available for teams that prefer those patterns."))
        );
    }

    // -----------------------------------------------------------------------
    // 2. Approach A: Functional
    // -----------------------------------------------------------------------

    private static ReactElement functionalApproachSection() {
        String javaCode = """
                import ca.weblite.teavmreact.core.ReactElement;
                import org.teavm.jso.JSObject;
                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

                public class Profile {

                    // Step 1: Define a render method
                    static ReactElement render(JSObject props) {
                        return El.div("profile-card",

                            h2("Alice Johnson"),
                            p("Software Engineer"),
                            p("Loves building UIs with Java.")
                        );
                    }
                }

                // Step 2: Use it in a parent component
                public class App {
                    static ReactElement render(JSObject props) {
                        return div(
                            h1("Team Members"),
                            // Wrap the method reference with component()
                            component(Profile::render, "Profile"),
                            component(Profile::render, "Profile"),
                            component(Profile::render, "Profile")
                        );
                    }
                }""";

        String kotlinCode = """
                import ca.weblite.teavmreact.kotlin.*

                // Define a functional component with fc()
                val Profile = fc("Profile") {
                    div {
                        className = "profile-card"
                        h2 { +"Alice Johnson" }
                        p { +"Software Engineer" }
                        p { +"Loves building UIs with Kotlin." }
                    }
                }

                val App = fc("App") {
                    div {
                        h1 { +"Team Members" }
                        +Profile
                        +Profile
                        +Profile
                    }
                }""";

        return El.section("doc-section",

            h2("Approach A: Functional Components"),
            p("The functional approach is a static method that takes JSObject props "
              + "and returns a ReactElement. You wrap it with the component() helper "
              + "to register it with React."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.pitfall("Component Identity",
                p("Each call to component(Profile::render, \"Profile\") creates a new "
                  + "wrapper. For better performance, store the wrapped component in a "
                  + "static field using React.wrapComponent() and reuse it."))
        );
    }

    // -----------------------------------------------------------------------
    // 3. Approach B: Builder DSL
    // -----------------------------------------------------------------------

    private static ReactElement builderDslApproachSection() {
        String javaCode = """
                import ca.weblite.teavmreact.html.DomBuilder;
                import ca.weblite.teavmreact.core.ReactElement;

                public class ProfileBuilder extends DomBuilder {

                    private final String name;
                    private final String role;

                    public ProfileBuilder(String name, String role) {
                        this.name = name;
                        this.role = role;
                    }

                    @Override
                    public ReactElement build() {
                        return El.div("profile-card",

                            h2(name),
                            p(role)
                        );
                    }
                }

                // Usage:
                // new ProfileBuilder("Alice", "Engineer").build()""";

        String kotlinCode = """
                // In Kotlin, the fc() DSL is generally preferred
                // over the builder approach. The lambda-with-receiver
                // pattern gives you a natural builder syntax:

                val Profile = fc("Profile") {
                    // This lambda IS the builder
                    div {
                        className = "profile-card"
                        h2 { +"Alice Johnson" }
                        p { +"Engineer" }
                    }
                }""";

        return El.section("doc-section",

            h2("Approach B: Builder DSL"),
            p("The builder approach uses inner classes that extend DomBuilder. "
              + "The class encapsulates both the data and the rendering logic."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.note("When to use builders",
                p("Builders are useful when your component has complex construction "
                  + "logic or many parameters. They provide a clear separation between "
                  + "configuration and rendering."))
        );
    }

    // -----------------------------------------------------------------------
    // 4. Approach C: Class-Based
    // -----------------------------------------------------------------------

    private static ReactElement classBasedApproachSection() {
        String javaCode = """
                import ca.weblite.teavmreact.component.ReactView;
                import ca.weblite.teavmreact.core.ReactElement;
                import ca.weblite.teavmreact.hooks.Hooks;
                import org.teavm.jso.JSObject;

                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

                public class ClickCounter extends ReactView {

                    public ClickCounter() {
                        super("ClickCounter");
                    }

                    @Override
                    protected ReactElement render(JSObject props) {
                        var count = Hooks.useState(0);
                        return div(
                            p("Clicked " + count.getInt() + " times"),
                            button("Click me")
                                .onClick(e -> count.updateInt(n -> n + 1))
                                .build()
                        );
                    }
                }

                // Usage: wrap the instance as a component
                // new ClickCounter().asElement()""";

        String kotlinCode = """
                // Kotlin equivalent using fc() with state delegation:
                val ClickCounter = fc("ClickCounter") {
                    var count by state(0)
                    div {
                        p { +"Clicked $count times" }
                        button {
                            +"Click me"
                            onClick { count++ }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Approach C: Class-Based Components"),
            p("Class-based components extend ReactView and override the render method. "
              + "This approach is familiar to developers coming from Android or Swing, "
              + "and provides a natural home for component-specific helper methods."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.note("Hooks in class components",
                p("Even in class-based components, you use hooks (useState, useEffect, etc.) "
                  + "inside the render method. ReactView is a thin wrapper that integrates "
                  + "your class with React's functional component system."))
        );
    }

    // -----------------------------------------------------------------------
    // 5. Nesting Components
    // -----------------------------------------------------------------------

    private static ReactElement nestingComponentsSection() {
        String javaCode = """
                public class Gallery {
                    static ReactElement render(JSObject props) {
                        return El.section("gallery",

                            h1("Amazing Scientists"),
                            component(p -> profile(p, "Marie Curie",
                                "Physicist and chemist"), "Profile"),
                            component(p -> profile(p, "Ada Lovelace",
                                "Mathematician and writer"), "Profile"),
                            component(p -> profile(p, "Alan Turing",
                                "Computer scientist"), "Profile")
                        );
                    }

                    private static ReactElement profile(JSObject props,
                            String name, String description) {
                        return El.div("profile-card",

                            h2(name),
                            p(description)
                        );
                    }
                }""";

        String kotlinCode = """
                fun profile(name: String, description: String) = fc("Profile") {
                    div {
                        className = "profile-card"
                        h2 { +name }
                        p { +description }
                    }
                }

                val Gallery = fc("Gallery") {
                    section {
                        className = "gallery"
                        h1 { +"Amazing Scientists" }
                        +profile("Marie Curie", "Physicist and chemist")
                        +profile("Ada Lovelace", "Mathematician and writer")
                        +profile("Alan Turing", "Computer scientist")
                    }
                }""";

        return El.section("doc-section",

            h2("Nesting Components"),
            p("Components can render other components. This is how you build up "
              + "complex UIs from simple, reusable pieces."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.pitfall("Never define a component inside another component",
                p("Component definitions should always be at the top level. Defining "
                  + "a component inside another component's render method creates a new "
                  + "component identity on every render, which destroys and recreates "
                  + "all its state. Keep component methods as separate static methods "
                  + "or separate classes."))
        );
    }

    // -----------------------------------------------------------------------
    // 6. Live Demo: Greeting Card
    // -----------------------------------------------------------------------

    private static ReactElement liveDemoSection() {
        String javaCode = """
                static ReactElement greetingCard(JSObject props) {
                    var name = Hooks.useState("World");
                    return El.div("greeting-card",

                        h2("Greeting Card"),
                        input("text")
                            .value(name.getString())
                            .placeholder("Enter your name")
                            .onChange(e -> name.setString(e.getTarget().getValue()))
                            .build(),
                        El.div("greeting-output",

                            p("Hello, " + name.getString() + "!"),
                            p("Welcome to teavm-react.")
                        )
                    );
                }""";

        String kotlinCode = """
                val GreetingCard = fc("GreetingCard") {
                    var name by state("World")
                    div {
                        className = "greeting-card"
                        h2 { +"Greeting Card" }
                        input {
                            type = "text"
                            value = name
                            placeholder = "Enter your name"
                            onChange { name = it.targetValue }
                        }
                        div {
                            className = "greeting-output"
                            p { +"Hello, $name!" }
                            p { +"Welcome to teavm-react." }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Putting It Together: A Greeting Card"),
            p("Here is a complete interactive component that combines what you have "
              + "learned: a functional component with state, event handling, and "
              + "dynamic text."),
            CodeTabs.create(javaCode, kotlinCode),
            p("Try it live:"),
            LiveDemo.create(FirstComponentPage::greetingCardDemo)
        );
    }

    private static ReactElement greetingCardDemo(JSObject props) {
        var name = Hooks.useState("World");
        return El.div("greeting-card-demo",

            h3("Greeting Card"),
            input("text")
                .className("demo-input")
                .value(name.getString())
                .placeholder("Enter your name")
                .onChange(e -> name.setString(e.getTarget().getValue()))
                .build(),
            El.div("greeting-output",

                p("Hello, " + name.getString() + "!"),
                p("Welcome to teavm-react.")
            )
        );
    }
}

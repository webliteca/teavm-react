package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.hooks.Hooks;
import org.teavm.jso.JSObject;

import java.util.ArrayList;
import java.util.List;

import ca.weblite.teavmreact.html.DomBuilder.*;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

/**
 * Documentation page: Rendering Lists.
 */
public class RenderingListsPage {

    public static ReactElement render(JSObject props) {
        return Div.create().className("docs-page")
            .child(h1("Rendering Lists"))
            .child(p("You will often want to display multiple similar components from " +
              "a collection of data. In teavm-react, you build arrays of ReactElements " +
              "using standard Java loops and collections, then include them in your tree."))

            // Section 1: Building arrays manually
            .child(h2("Building Arrays of ReactElements"))
            .child(p("The most straightforward way to render a list is to build an array " +
              "of ReactElements in a loop and pass it to a container element."))

            .child(CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    String[] fruits = {"Apple", "Banana", "Cherry"};

                    // Build an array of <li> elements
                    ReactElement[] items = new ReactElement[fruits.length];
                    for (int i = 0; i < fruits.length; i++) {
                        items[i] = li(fruits[i]);
                    }

                    return div(
                        h2("Fruits"),
                        ul(items)
                    );
                }""",
                """
                val FruitList = component("FruitList") {
                    val fruits = listOf("Apple", "Banana", "Cherry")

                    div {
                        h2("Fruits")
                        ul {
                            fruits.forEach { fruit ->
                                li(fruit)
                            }
                        }
                    }
                }"""
            ))

            // Section 2: mapToElements
            .child(h2("Using Html.mapToElements()"))
            .child(p(
                text("The "),
                code("Html.mapToElements()"),
                text(" utility provides a functional way to transform a "),
                code("List<T>"),
                text(" into a "),
                code("ReactElement[]"),
                text(". This is similar to JavaScript's "),
                code("Array.map()"),
                text(".")
            ))

            .child(CodeTabs.create(
                """
                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;
                import java.util.List;

                public static ReactElement render(JSObject props) {
                    List<String> names = List.of(
                        "Alice", "Bob", "Charlie"
                    );

                    return ul(
                        mapToElements(names, name ->
                            li(name)
                        )
                    );
                }""",
                """
                val NameList = component("NameList") {
                    val names = listOf("Alice", "Bob", "Charlie")

                    ul {
                        names.forEach { name -> li(name) }
                    }
                }"""
            ))

            // Section 3: DomBuilder.forEach
            .child(h2("DomBuilder.forEach() Pattern"))
            .child(p(
                text("If you prefer the fluent "),
                code("DomBuilder"),
                text(" API, you can use the "),
                code("forEach()"),
                text(" method to iterate over a list and append each mapped element as a child.")
            ))

            .child(CodeTabs.create(
                """
                import ca.weblite.teavmreact.html.DomBuilder;
                import java.util.List;

                public static ReactElement render(JSObject props) {
                    List<String> tasks = List.of(
                        "Buy groceries",
                        "Walk the dog",
                        "Write code"
                    );

                    return DomBuilder.Ul.create()
                        .className("task-list")
                        .forEach(tasks, task ->
                            DomBuilder.Li.create()
                                .text(task)
                                .className("task-item")
                        )
                        .build();
                }""",
                """
                val TaskList = component("TaskList") {
                    val tasks = listOf(
                        "Buy groceries",
                        "Walk the dog",
                        "Write code"
                    )

                    ul {
                        className = "task-list"
                        tasks.forEach { task ->
                            li(task) { className = "task-item" }
                        }
                    }
                }"""
            ))

            // Section 4: Keys
            .child(h2("Keys and Why They Matter"))
            .child(p("When rendering lists, React needs a way to identify which items " +
              "have changed, been added, or been removed. You do this by giving " +
              "each element a unique key."))

            .child(CodeTabs.create(
                """
                public static ReactElement render(JSObject props) {
                    String[] items = {"apple", "banana", "cherry"};

                    ReactElement[] elements = new ReactElement[items.length];
                    for (int i = 0; i < items.length; i++) {
                        // Use .key() on the ElementBuilder to set
                        // the React key for this list item.
                        elements[i] = li(items[i]);
                    }

                    return ul(elements);
                }

                // With DomBuilder, use .key() on each item:
                public static ReactElement renderWithBuilder(JSObject props) {
                    List<String> items = List.of(
                        "apple", "banana", "cherry"
                    );

                    return DomBuilder.Ul.create()
                        .forEach(items, item ->
                            DomBuilder.Li.create()
                                .key(item) // unique key
                                .text(item)
                        )
                        .build();
                }""",
                """
                val FruitList = component("FruitList") {
                    val items = listOf("apple", "banana", "cherry")

                    ul {
                        items.forEach { item ->
                            li(item) { key = item }
                        }
                    }
                }"""
            ))

            .child(Callout.pitfall("Don't use array index as key",
                p("Using the loop index as a key can cause bugs when items are " +
                  "reordered, inserted, or deleted. React uses keys to match " +
                  "elements across renders. If the key stays the same but the " +
                  "data changes, React will reuse the wrong DOM node. Use a " +
                  "stable, unique identifier from your data instead.")
            ))

            .child(Callout.deepDive("How React uses keys",
                p("Keys tell React which array element corresponds to which " +
                  "component instance. When you reorder a list, React uses the " +
                  "keys to determine the minimum set of DOM operations needed. " +
                  "Without keys, React has to destroy and recreate elements " +
                  "unnecessarily, which is slower and resets component state.")
            ))

            // Section 5: Live demo
            .child(h2("Live Demo: Filterable List"))
            .child(p("Type in the filter box to narrow down the list of programming languages."))

            .child(LiveDemo.create(RenderingListsPage::filterableListDemo))

            .child(CodeBlock.create(
                """
                public static ReactElement filterableListDemo(JSObject props) {
                    var filter = Hooks.useState("");
                    String[] languages = {
                        "Java", "Kotlin", "JavaScript", "TypeScript",
                        "Python", "Rust", "Go", "Swift"
                    };

                    String query = filter.getString().toLowerCase();

                    // Build filtered list
                    List<String> filtered = new ArrayList<>();
                    for (String lang : languages) {
                        if (lang.toLowerCase().contains(query)) {
                            filtered.add(lang);
                        }
                    }

                    return div(
                        input("text")
                            .placeholder("Filter languages...")
                            .value(filter.getString())
                            .onChange(e -> filter.setString(
                                e.getTarget().getValue()))
                            .build(),
                        ul(mapToElements(filtered, lang -> li(lang))),
                        p(filtered.size() + " of "
                            + languages.length + " shown")
                    );
                }""",
                "java"
            ))

            // Recap
            .child(h2("Recap"))
            .child(ul(
                li("Build ReactElement arrays with loops or mapToElements()."),
                li("Use DomBuilder.forEach() for the fluent builder approach."),
                li("Always provide unique, stable keys when rendering lists."),
                li("Never use array indices as keys for dynamic lists.")
            ))
            .build();
    }

    private static ReactElement filterableListDemo(JSObject props) {
        var filter = Hooks.useState("");
        String[] languages = {
            "Java", "Kotlin", "JavaScript", "TypeScript",
            "Python", "Rust", "Go", "Swift"
        };

        String query = filter.getString().toLowerCase();

        List<String> filtered = new ArrayList<>();
        for (String lang : languages) {
            if (lang.toLowerCase().contains(query)) {
                filtered.add(lang);
            }
        }

        return div(
            input("text")
                .placeholder("Filter languages...")
                .value(filter.getString())
                .className("demo-input")
                .onChange(e -> filter.setString(e.getTarget().getValue()))
                .build(),
            ul(mapToElements(filtered, lang -> li(lang))),
            P.create().className("demo-hint")
                .text(filtered.size() + " of " + languages.length + " shown")
                .build()
        );
    }
}

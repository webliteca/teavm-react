package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class ThinkingPage {

    public static ReactElement render(JSObject props) {
        return El.div("doc-page",

            h1("Thinking in teavm-react"),
            p("teavm-react gives you the full power of React's component model with "
              + "Java's type safety. This guide walks you through the thought process "
              + "of building a searchable product table, from mockup to working code."),
            hr(),
            startWithMockupSection(),
            hr(),
            componentHierarchySection(),
            hr(),
            staticVersionSection(),
            hr(),
            minimalStateSection(),
            hr(),
            whereStateLivesSection(),
            hr(),
            inverseDataFlowSection()
        );
    }

    // -----------------------------------------------------------------------
    // Step 1: Start with a Mockup
    // -----------------------------------------------------------------------

    private static ReactElement startWithMockupSection() {
        return El.section("doc-section",

            h2("Step 1: Start with a Mockup"),
            p("Imagine you have a JSON API that returns a list of products and a "
              + "design mockup showing a searchable, filterable table."),
            p("The mockup shows:"),
            ul(
                li("A search bar at the top"),
                li("A checkbox to show only in-stock products"),
                li("A table of products grouped by category"),
                li("Out-of-stock products shown in red")
            ),
            p("The data looks like this:"),
            pre(code("""
                [
                  { category: "Fruits",      name: "Apple",      price: "$1",  stocked: true  },
                  { category: "Fruits",      name: "Dragonfruit", price: "$1", stocked: true  },
                  { category: "Fruits",      name: "Passionfruit", price: "$2", stocked: false },
                  { category: "Vegetables",  name: "Spinach",    price: "$2",  stocked: true  },
                  { category: "Vegetables",  name: "Pumpkin",    price: "$4",  stocked: false },
                  { category: "Vegetables",  name: "Peas",       price: "$1",  stocked: true  }
                ]"""))
        );
    }

    // -----------------------------------------------------------------------
    // Step 2: Break the UI into Components
    // -----------------------------------------------------------------------

    private static ReactElement componentHierarchySection() {
        return El.section("doc-section",

            h2("Step 2: Break the UI into a Component Hierarchy"),
            p("Draw boxes around every component and subcomponent in the mockup. "
              + "Each box becomes a component. The hierarchy for our product table is:"),
            ul(
                li(fragment(
                    strong("FilterableProductTable"),
                    text(" - the overall container")
                )),
                li(fragment(
                    strong("SearchBar"),
                    text(" - the search input and checkbox")
                )),
                li(fragment(
                    strong("ProductTable"),
                    text(" - the table displaying filtered results")
                )),
                li(fragment(
                    strong("ProductCategoryRow"),
                    text(" - a heading row for each category")
                )),
                li(fragment(
                    strong("ProductRow"),
                    text(" - a row for each product")
                ))
            ),
            Callout.note("Single Responsibility",
                p("A component should ideally do one thing. If it grows too large, "
                  + "break it into smaller subcomponents. This maps naturally to Java "
                  + "classes and methods."))
        );
    }

    // -----------------------------------------------------------------------
    // Step 3: Build a Static Version
    // -----------------------------------------------------------------------

    private static ReactElement staticVersionSection() {
        String javaCode = """
                public class ProductRow {
                    static ReactElement render(JSObject props, String name,
                                               String price, boolean stocked) {
                        return tr(
                            td(
                                stocked
                                    ? span(name)
                                    : El.span("out-of-stock",
name)
                            ),
                            td(price)
                        );
                    }
                }

                public class ProductCategoryRow {
                    static ReactElement render(JSObject props, String category) {
                        return tr(
                            El.classed("th", "category-header",
category)
                        );
                    }
                }

                public class ProductTable {
                    static ReactElement render(JSObject props, String[][] products) {
                        // Build rows from product data
                        ReactElement[] rows = new ReactElement[products.length];
                        String lastCategory = "";
                        int idx = 0;
                        for (String[] product : products) {
                            String category = product[0];
                            String name = product[1];
                            String price = product[2];
                            boolean stocked = "true".equals(product[3]);
                            if (!category.equals(lastCategory)) {
                                // Insert category header row
                                lastCategory = category;
                            }
                            rows[idx++] = component(
                                p -> ProductRow.render(p, name, price, stocked),
                                "ProductRow"
                            );
                        }
                        return table(
                            thead(tr(th("Name"), th("Price"))),
                            tbody(rows)
                        );
                    }
                }""";

        String kotlinCode = """
                data class Product(
                    val category: String,
                    val name: String,
                    val price: String,
                    val stocked: Boolean
                )

                val ProductRow = fc("ProductRow") { product: Product ->
                    tr {
                        td {
                            if (product.stocked) {
                                span { +product.name }
                            } else {
                                span {
                                    className = "out-of-stock"
                                    +product.name
                                }
                            }
                        }
                        td { +product.price }
                    }
                }

                val ProductTable = fc("ProductTable") { products: List<Product> ->
                    table {
                        thead { tr { th { +"Name" }; th { +"Price" } } }
                        tbody {
                            var lastCategory = ""
                            products.forEach { product ->
                                if (product.category != lastCategory) {
                                    lastCategory = product.category
                                    tr { th { +product.category } }
                                }
                                +ProductRow(product)
                            }
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Step 3: Build a Static Version"),
            p("Build a version that renders the UI from data but has no interactivity. "
              + "Don't use state yet. Just pass data down and render it."),
            CodeTabs.create(javaCode, kotlinCode),
            Callout.note("Top-Down or Bottom-Up?",
                p("For simpler apps, build top-down (start with the outermost component). "
                  + "For larger apps, build bottom-up and test each component in isolation."))
        );
    }

    // -----------------------------------------------------------------------
    // Step 4: Find the Minimal State
    // -----------------------------------------------------------------------

    private static ReactElement minimalStateSection() {
        return El.section("doc-section",

            h2("Step 4: Find the Minimal State"),
            p("Identify every piece of data in the application and decide if it is state. "
              + "Ask three questions about each piece of data:"),
            ol(
                li("Does it remain unchanged over time? If so, it is not state."),
                li("Is it passed from a parent via props? If so, it is not state."),
                li("Can you compute it from existing state or props? If so, it is not state.")
            ),
            p("For our product table, the data is:"),
            ul(
                li(fragment(
                    strong("The product list"),
                    text(" - passed as data, not state")
                )),
                li(fragment(
                    strong("The search text"),
                    text(" - changes over time, cannot be computed. This is state.")
                )),
                li(fragment(
                    strong("The checkbox value"),
                    text(" - changes over time, cannot be computed. This is state.")
                )),
                li(fragment(
                    strong("The filtered product list"),
                    text(" - can be computed from the product list, search text, "
                         + "and checkbox value. Not state.")
                ))
            ),
            Callout.deepDive("Computed Values in Java",
                p("In teavm-react, computed values are simply local variables in your "
                  + "render method. They are recalculated every time the component renders. "
                  + "For expensive computations, use Hooks.useMemo()."))
        );
    }

    // -----------------------------------------------------------------------
    // Step 5: Determine Where State Lives
    // -----------------------------------------------------------------------

    private static ReactElement whereStateLivesSection() {
        String javaCode = """
                public class FilterableProductTable {
                    static ReactElement render(JSObject props) {
                        // State lives here because both SearchBar
                        // and ProductTable need it
                        var filterText = Hooks.useState("");
                        var inStockOnly = Hooks.useState(false);

                        return div(
                            component(p -> SearchBar.render(p,
                                filterText.getString(),
                                inStockOnly.getBool(),
                                text -> filterText.setString(text),
                                checked -> inStockOnly.setBool(checked)
                            ), "SearchBar"),
                            component(p -> ProductTable.render(p,
                                PRODUCTS,
                                filterText.getString(),
                                inStockOnly.getBool()
                            ), "ProductTable")
                        );
                    }
                }""";

        String kotlinCode = """
                val FilterableProductTable = fc("FilterableProductTable") {
                    var filterText by state("")
                    var inStockOnly by state(false)

                    div {
                        +SearchBar(
                            filterText = filterText,
                            inStockOnly = inStockOnly,
                            onFilterTextChange = { filterText = it },
                            onInStockChange = { inStockOnly = it }
                        )
                        +ProductTable(
                            products = PRODUCTS,
                            filterText = filterText,
                            inStockOnly = inStockOnly
                        )
                    }
                }""";

        return El.section("doc-section",

            h2("Step 5: Determine Where State Lives"),
            p("For each piece of state, find the component that should own it. "
              + "Look for the closest common ancestor of all components that use the state."),
            p("In our example:"),
            ul(
                li("SearchBar needs the filter text and checkbox state (to display them)."),
                li("ProductTable needs them (to filter the product list)."),
                li("Their common parent is FilterableProductTable.")
            ),
            p("So the state lives in FilterableProductTable:"),
            CodeTabs.create(javaCode, kotlinCode)
        );
    }

    // -----------------------------------------------------------------------
    // Step 6: Add Inverse Data Flow
    // -----------------------------------------------------------------------

    private static ReactElement inverseDataFlowSection() {
        String javaCode = """
                public class SearchBar {
                    // Callbacks are passed as parameters (like props)
                    static ReactElement render(JSObject props,
                            String filterText,
                            boolean inStockOnly,
                            StringSetter onFilterTextChange,
                            BooleanSetter onInStockChange) {
                        return El.div("search-bar",

                            input("text")
                                .value(filterText)
                                .placeholder("Search...")
                                .onChange(e ->
                                    onFilterTextChange.set(e.getTarget().getValue()))
                                .build(),
                            label(
                                input("checkbox")
                                    .checked(inStockOnly)
                                    .onChange(e ->
                                        onInStockChange.set(!inStockOnly))
                                    .build(),
                                text(" Only show products in stock")
                            )
                        );
                    }

                    @JSFunctor
                    interface StringSetter extends JSObject {
                        void set(String value);
                    }

                    @JSFunctor
                    interface BooleanSetter extends JSObject {
                        void set(boolean value);
                    }
                }""";

        String kotlinCode = """
                val SearchBar = fc("SearchBar") {
                    filterText: String,
                    inStockOnly: Boolean,
                    onFilterTextChange: (String) -> Unit,
                    onInStockChange: (Boolean) -> Unit ->

                    div {
                        className = "search-bar"
                        input {
                            type = "text"
                            value = filterText
                            placeholder = "Search..."
                            onChange { onFilterTextChange(it.targetValue) }
                        }
                        label {
                            input {
                                type = "checkbox"
                                checked = inStockOnly
                                onChange { onInStockChange(!inStockOnly) }
                            }
                            +" Only show products in stock"
                        }
                    }
                }""";

        return El.section("doc-section",

            h2("Step 6: Add Inverse Data Flow"),
            p("Data flows down from parent to child, but user interactions in child "
              + "components need to update the parent's state. Pass callback functions "
              + "from parent to child so children can signal changes."),
            CodeTabs.create(javaCode, kotlinCode),
            p("Now the full circle is complete:"),
            ol(
                li("FilterableProductTable owns filterText and inStockOnly state."),
                li("It passes the current values and setter callbacks to SearchBar."),
                li("SearchBar renders the inputs with those values."),
                li("When the user types or clicks, SearchBar calls the callbacks."),
                li("The parent's state updates, triggering a re-render of both "
                   + "SearchBar and ProductTable with the new filtered data.")
            ),
            Callout.deepDive("The Java Advantage",
                p("In JavaScript React, prop-drilling callbacks is error-prone because "
                  + "there are no compile-time checks. In teavm-react, the Java compiler "
                  + "ensures every callback has the correct signature. A mismatched setter "
                  + "type is caught at compile time, not at runtime."))
        );
    }
}

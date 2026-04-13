package ca.weblite.teavmreact.kotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Verify that the public API surface of teavm-react-kotlin exists and is
 * accessible. These tests verify that key classes, functions, and annotations
 * are available — catching accidental renames, visibility changes, or deletions.
 *
 * They don't test runtime behavior (which requires a JS environment) but serve
 * as a contract test for consumers of the library.
 */
class ApiSurfaceTest {

    // ====================================================================
    // Annotations
    // ====================================================================

    @Test
    fun `HtmlDsl annotation exists`() {
        val annotation = HtmlDsl::class
        assertNotNull(annotation)
    }

    // ====================================================================
    // Core classes exist
    // ====================================================================

    @Test
    fun `HtmlBuilder class exists with expected tag constructor`() {
        val clazz = HtmlBuilder::class.java
        assertNotNull(clazz)
        assertTrue(clazz.constructors.any { it.parameterCount == 1 })
    }

    @Test
    fun `StyleBuilder class exists`() {
        assertNotNull(StyleBuilder::class)
    }

    @Test
    fun `ComponentScope class exists`() {
        assertNotNull(ComponentScope::class)
    }

    @Test
    fun `EffectScope class exists`() {
        assertNotNull(EffectScope::class)
    }

    // ====================================================================
    // State delegate classes exist
    // ====================================================================

    @Test
    fun `IntStateDelegate class exists`() {
        assertNotNull(IntStateDelegate::class)
    }

    @Test
    fun `StringStateDelegate class exists`() {
        assertNotNull(StringStateDelegate::class)
    }

    @Test
    fun `BooleanStateDelegate class exists`() {
        assertNotNull(BooleanStateDelegate::class)
    }

    @Test
    fun `DoubleStateDelegate class exists`() {
        assertNotNull(DoubleStateDelegate::class)
    }

    @Test
    fun `StringListStateDelegate class exists`() {
        assertNotNull(StringListStateDelegate::class)
    }

    // ====================================================================
    // Ref delegate classes exist
    // ====================================================================

    @Test
    fun `IntRefDelegate class exists`() {
        assertNotNull(IntRefDelegate::class)
    }

    @Test
    fun `StringRefDelegate class exists`() {
        assertNotNull(StringRefDelegate::class)
    }

    // ====================================================================
    // Context classes exist
    // ====================================================================

    @Test
    fun `TypedContext class exists`() {
        assertNotNull(TypedContext::class)
    }

    // ====================================================================
    // Flow extension classes exist
    // ====================================================================

    @Test
    fun `CollectedIntState class exists`() {
        assertNotNull(CollectedIntState::class)
    }

    @Test
    fun `CollectedStringState class exists`() {
        assertNotNull(CollectedStringState::class)
    }

    @Test
    fun `CollectedBooleanState class exists`() {
        assertNotNull(CollectedBooleanState::class)
    }

    @Test
    fun `CollectedDoubleState class exists`() {
        assertNotNull(CollectedDoubleState::class)
    }

    @Test
    fun `ProduceStateScope class exists`() {
        assertNotNull(ProduceStateScope::class)
    }

    // ====================================================================
    // Utility classes exist
    // ====================================================================

    @Test
    fun `PropsBuilder class exists`() {
        assertNotNull(PropsBuilder::class)
    }

    @Test
    fun `JsDispatcher object exists`() {
        assertNotNull(JsDispatcher::class)
    }

    // ====================================================================
    // Top-level functions are accessible (compile-time verification)
    // ====================================================================

    @Test
    fun `pure utility functions exist`() {
        // These are compile-time checks — if the functions don't exist, this won't compile
        val _1: (String) -> String = ::kebabToCamelCase
        val _2: (String) -> List<Pair<String, String>> = ::parseCssString
        val _3: (List<String>) -> String = ::encodeStringList
        val _4: (String) -> List<String> = ::decodeStringList
        assertNotNull(_1)
        assertNotNull(_2)
        assertNotNull(_3)
        assertNotNull(_4)
    }

    // ====================================================================
    // StyleBuilder has CSS properties
    // ====================================================================

    @Test
    fun `StyleBuilder has key CSS properties`() {
        val methods = StyleBuilder::class.java.methods.map { it.name }.toSet()
        val expectedProperties = listOf(
            "setColor", "setBackgroundColor", "setPadding", "setMargin",
            "setDisplay", "setFontSize", "setFontWeight", "setBorder",
            "setBorderRadius", "setWidth", "setHeight", "setFlexDirection",
            "setJustifyContent", "setAlignItems", "setGap", "setOpacity",
            "setCursor", "setPosition", "setOverflow"
        )
        for (prop in expectedProperties) {
            assertTrue(prop in methods, "StyleBuilder should have setter '$prop'")
        }
    }

    // ====================================================================
    // HtmlBuilder has element methods
    // ====================================================================

    @Test
    fun `HtmlBuilder has element builder methods`() {
        val methods = HtmlBuilder::class.java.methods.map { it.name }.toSet()
        val expectedMethods = listOf(
            "div", "span", "p", "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "table", "tr", "td", "th",
            "form", "button", "input", "textarea", "select",
            "a", "img", "section", "article", "header", "footer", "nav",
            "pre", "code", "strong", "em", "label",
            "hr", "br"
        )
        for (method in expectedMethods) {
            assertTrue(method in methods, "HtmlBuilder should have method '$method'")
        }
    }

    @Test
    fun `HtmlBuilder has attribute methods`() {
        val methods = HtmlBuilder::class.java.methods.map { it.name }.toSet()
        val expectedMethods = listOf(
            "className", "id", "key", "style", "css",
            "onClick", "onChange", "onKeyDown", "onSubmit",
            "value", "placeholder", "disabled", "checked", "href", "src", "alt",
            "show"
        )
        for (method in expectedMethods) {
            assertTrue(method in methods, "HtmlBuilder should have method '$method'")
        }
    }
}

package ca.weblite.teavmreact.kotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CssParsingTest {

    @Test
    fun `kebabToCamelCase converts simple property`() {
        assertEquals("backgroundColor", kebabToCamelCase("background-color"))
    }

    @Test
    fun `kebabToCamelCase converts multi-hyphen property`() {
        assertEquals("borderTopLeftRadius", kebabToCamelCase("border-top-left-radius"))
    }

    @Test
    fun `kebabToCamelCase leaves already camelCase unchanged`() {
        assertEquals("padding", kebabToCamelCase("padding"))
    }

    @Test
    fun `kebabToCamelCase leaves single word unchanged`() {
        assertEquals("color", kebabToCamelCase("color"))
    }

    @Test
    fun `kebabToCamelCase converts webkit prefix`() {
        assertEquals("WebkitTransform", kebabToCamelCase("-webkit-transform"))
    }

    @Test
    fun `parseCssString parses single property`() {
        val result = parseCssString("color: red")
        assertEquals(listOf("color" to "red"), result)
    }

    @Test
    fun `parseCssString parses multiple properties`() {
        val result = parseCssString("background-color: red; padding: 10px; font-size: 14px")
        assertEquals(
            listOf(
                "backgroundColor" to "red",
                "padding" to "10px",
                "fontSize" to "14px"
            ),
            result
        )
    }

    @Test
    fun `parseCssString handles trailing semicolon`() {
        val result = parseCssString("color: red;")
        assertEquals(listOf("color" to "red"), result)
    }

    @Test
    fun `parseCssString handles extra whitespace`() {
        val result = parseCssString("  color :  red  ;  padding :  5px  ")
        assertEquals(
            listOf("color" to "red", "padding" to "5px"),
            result
        )
    }

    @Test
    fun `parseCssString returns empty list for empty input`() {
        assertEquals(emptyList(), parseCssString(""))
    }

    @Test
    fun `parseCssString returns empty list for whitespace only`() {
        assertEquals(emptyList(), parseCssString("   "))
    }

    @Test
    fun `parseCssString skips malformed entries without colon`() {
        val result = parseCssString("color: red; invalid; padding: 5px")
        assertEquals(
            listOf("color" to "red", "padding" to "5px"),
            result
        )
    }

    @Test
    fun `parseCssString handles values with colons`() {
        // URL values contain colons
        val result = parseCssString("background: url(https://example.com/img.png)")
        assertEquals(
            listOf("background" to "url(https://example.com/img.png)"),
            result
        )
    }
}

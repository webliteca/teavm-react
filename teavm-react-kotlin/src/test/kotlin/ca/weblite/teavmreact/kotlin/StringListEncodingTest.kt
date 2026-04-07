package ca.weblite.teavmreact.kotlin

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringListEncodingTest {

    @Test
    fun `encodeStringList encodes empty list as empty string`() {
        assertEquals("", encodeStringList(emptyList()))
    }

    @Test
    fun `encodeStringList encodes single item`() {
        assertEquals("hello", encodeStringList(listOf("hello")))
    }

    @Test
    fun `encodeStringList encodes multiple items with separator`() {
        val encoded = encodeStringList(listOf("a", "b", "c"))
        assertEquals("a\u0000b\u0000c", encoded)
    }

    @Test
    fun `decodeStringList decodes empty string to empty list`() {
        assertEquals(emptyList(), decodeStringList(""))
    }

    @Test
    fun `decodeStringList decodes single item`() {
        assertEquals(listOf("hello"), decodeStringList("hello"))
    }

    @Test
    fun `decodeStringList decodes multiple items`() {
        assertEquals(listOf("a", "b", "c"), decodeStringList("a\u0000b\u0000c"))
    }

    @Test
    fun `roundtrip preserves list contents`() {
        val original = listOf("Buy milk", "Write code", "Test everything")
        val decoded = decodeStringList(encodeStringList(original))
        assertEquals(original, decoded)
    }

    @Test
    fun `roundtrip preserves items with commas and spaces`() {
        val original = listOf("item, with comma", "item with spaces", "normal")
        val decoded = decodeStringList(encodeStringList(original))
        assertEquals(original, decoded)
    }

    @Test
    fun `roundtrip preserves empty strings in list`() {
        val original = listOf("", "nonempty", "")
        val decoded = decodeStringList(encodeStringList(original))
        assertEquals(original, decoded)
    }

    @Test
    fun `STRING_LIST_SEPARATOR is null char`() {
        assertEquals("\u0000", STRING_LIST_SEPARATOR)
    }
}

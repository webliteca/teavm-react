package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.React
import org.teavm.jso.JSObject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Type-safe builder for inline React styles.
 *
 * Usage inside an HtmlBuilder:
 * ```
 * div {
 *     style {
 *         backgroundColor = "#282c34"
 *         color = "white"
 *         padding = "20px"
 *         display = Display.Flex
 *     }
 * }
 * ```
 */
@HtmlDsl
class StyleBuilder {
    @PublishedApi
    internal val styleObj: JSObject = React.createObject()

    // --- Layout ---
    var display: String by styleProperty("display")
    var position: String by styleProperty("position")
    var top: String by styleProperty("top")
    var right: String by styleProperty("right")
    var bottom: String by styleProperty("bottom")
    var left: String by styleProperty("left")
    var zIndex: String by styleProperty("zIndex")
    var overflow: String by styleProperty("overflow")
    var overflowX: String by styleProperty("overflowX")
    var overflowY: String by styleProperty("overflowY")
    var float: String by styleProperty("float")
    var clear: String by styleProperty("clear")

    // --- Flexbox ---
    var flexDirection: String by styleProperty("flexDirection")
    var flexWrap: String by styleProperty("flexWrap")
    var justifyContent: String by styleProperty("justifyContent")
    var alignItems: String by styleProperty("alignItems")
    var alignContent: String by styleProperty("alignContent")
    var alignSelf: String by styleProperty("alignSelf")
    var flex: String by styleProperty("flex")
    var flexGrow: String by styleProperty("flexGrow")
    var flexShrink: String by styleProperty("flexShrink")
    var flexBasis: String by styleProperty("flexBasis")
    var gap: String by styleProperty("gap")
    var rowGap: String by styleProperty("rowGap")
    var columnGap: String by styleProperty("columnGap")
    var order: String by styleProperty("order")

    // --- Grid ---
    var gridTemplateColumns: String by styleProperty("gridTemplateColumns")
    var gridTemplateRows: String by styleProperty("gridTemplateRows")
    var gridColumn: String by styleProperty("gridColumn")
    var gridRow: String by styleProperty("gridRow")
    var gridGap: String by styleProperty("gridGap")
    var gridArea: String by styleProperty("gridArea")
    var gridTemplate: String by styleProperty("gridTemplate")

    // --- Box model ---
    var width: String by styleProperty("width")
    var height: String by styleProperty("height")
    var minWidth: String by styleProperty("minWidth")
    var minHeight: String by styleProperty("minHeight")
    var maxWidth: String by styleProperty("maxWidth")
    var maxHeight: String by styleProperty("maxHeight")
    var margin: String by styleProperty("margin")
    var marginTop: String by styleProperty("marginTop")
    var marginRight: String by styleProperty("marginRight")
    var marginBottom: String by styleProperty("marginBottom")
    var marginLeft: String by styleProperty("marginLeft")
    var padding: String by styleProperty("padding")
    var paddingTop: String by styleProperty("paddingTop")
    var paddingRight: String by styleProperty("paddingRight")
    var paddingBottom: String by styleProperty("paddingBottom")
    var paddingLeft: String by styleProperty("paddingLeft")
    var boxSizing: String by styleProperty("boxSizing")

    // --- Border ---
    var border: String by styleProperty("border")
    var borderTop: String by styleProperty("borderTop")
    var borderRight: String by styleProperty("borderRight")
    var borderBottom: String by styleProperty("borderBottom")
    var borderLeft: String by styleProperty("borderLeft")
    var borderRadius: String by styleProperty("borderRadius")
    var borderColor: String by styleProperty("borderColor")
    var borderStyle: String by styleProperty("borderStyle")
    var borderWidth: String by styleProperty("borderWidth")
    var outline: String by styleProperty("outline")

    // --- Colors & Background ---
    var color: String by styleProperty("color")
    var backgroundColor: String by styleProperty("backgroundColor")
    var background: String by styleProperty("background")
    var backgroundImage: String by styleProperty("backgroundImage")
    var backgroundSize: String by styleProperty("backgroundSize")
    var backgroundPosition: String by styleProperty("backgroundPosition")
    var backgroundRepeat: String by styleProperty("backgroundRepeat")
    var opacity: String by styleProperty("opacity")

    // --- Typography ---
    var fontSize: String by styleProperty("fontSize")
    var fontWeight: String by styleProperty("fontWeight")
    var fontFamily: String by styleProperty("fontFamily")
    var fontStyle: String by styleProperty("fontStyle")
    var lineHeight: String by styleProperty("lineHeight")
    var letterSpacing: String by styleProperty("letterSpacing")
    var textAlign: String by styleProperty("textAlign")
    var textDecoration: String by styleProperty("textDecoration")
    var textTransform: String by styleProperty("textTransform")
    var whiteSpace: String by styleProperty("whiteSpace")
    var wordBreak: String by styleProperty("wordBreak")
    var wordWrap: String by styleProperty("wordWrap")
    var textOverflow: String by styleProperty("textOverflow")

    // --- Effects ---
    var boxShadow: String by styleProperty("boxShadow")
    var textShadow: String by styleProperty("textShadow")
    var transform: String by styleProperty("transform")
    var transition: String by styleProperty("transition")
    var animation: String by styleProperty("animation")
    var cursor: String by styleProperty("cursor")
    var pointerEvents: String by styleProperty("pointerEvents")
    var userSelect: String by styleProperty("userSelect")
    var filter: String by styleProperty("filter")
    var visibility: String by styleProperty("visibility")

    /**
     * Set an arbitrary CSS property by name.
     */
    fun property(name: String, value: String) {
        React.setProperty(styleObj, name, value)
    }

    internal fun build(): JSObject = styleObj

    private fun styleProperty(jsName: String) = object : ReadWriteProperty<StyleBuilder, String> {
        private var current = ""
        override fun getValue(thisRef: StyleBuilder, property: KProperty<*>): String = current
        override fun setValue(thisRef: StyleBuilder, property: KProperty<*>, value: String) {
            current = value
            React.setProperty(styleObj, jsName, value)
        }
    }
}

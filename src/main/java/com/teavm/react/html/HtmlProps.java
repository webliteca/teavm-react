package com.teavm.react.html;

import com.teavm.react.events.ChangeEventHandler;
import com.teavm.react.events.EventHandler;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * JSO overlay for common HTML element props.
 */
public interface HtmlProps extends JSObject {
    @JSProperty
    void setClassName(String className);

    @JSProperty
    String getClassName();

    @JSProperty
    void setId(String id);

    @JSProperty
    void setStyle(JSObject style);

    @JSProperty
    void setOnClick(EventHandler handler);

    @JSProperty
    void setOnChange(ChangeEventHandler handler);

    @JSProperty
    void setType(String type);

    @JSProperty
    void setValue(String value);

    @JSProperty
    void setPlaceholder(String placeholder);

    @JSProperty
    void setDisabled(boolean disabled);

    @JSProperty
    void setHref(String href);

    @JSProperty
    void setSrc(String src);

    @JSProperty
    void setAlt(String alt);

    @JSProperty
    void setKey(String key);
}

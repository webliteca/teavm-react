package ca.weblite.teavmreact.events;

import org.teavm.jso.JSProperty;

/**
 * Typed interface for React focus/blur events.
 */
public interface FocusEvent extends SyntheticEvent {
    @JSProperty
    EventTarget getRelatedTarget();
}

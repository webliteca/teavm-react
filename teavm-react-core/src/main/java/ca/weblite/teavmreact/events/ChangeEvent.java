package ca.weblite.teavmreact.events;

/**
 * Typed interface for React change events. The event target
 * (from {@link SyntheticEvent#getTarget()}) provides access to
 * input element properties like getValue() and getChecked().
 */
public interface ChangeEvent extends SyntheticEvent {
}

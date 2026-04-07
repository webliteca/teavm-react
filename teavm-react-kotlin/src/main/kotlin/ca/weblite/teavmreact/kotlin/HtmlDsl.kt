package ca.weblite.teavmreact.kotlin

/**
 * DslMarker annotation that prevents accidental access to outer builder scopes.
 * Applied to HtmlBuilder and StyleBuilder so that nested lambdas only see
 * the innermost receiver's members.
 */
@DslMarker
annotation class HtmlDsl

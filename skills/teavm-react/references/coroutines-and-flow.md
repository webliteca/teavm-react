# Coroutines and Flow Reference

Read this file when building Kotlin DSL components that need asynchronous behavior: timers, data fetching, streaming updates, or async state production. All features in this file are Kotlin-only and require the `teavm-react-kotlin` dependency.

## Overview

| Feature | Purpose | Signature |
|---------|---------|-----------|
| `effect(vararg keys) { }` | Side effects with CoroutineScope | `ComponentScope.effect(vararg keys: Any?, block: EffectScope.() -> Unit)` |
| `effectOnce { }` | Mount-only side effect | `ComponentScope.effectOnce(block: EffectScope.() -> Unit)` |
| `launchedEffect(vararg keys) { }` | Auto-cancelling suspend effect | `ComponentScope.launchedEffect(vararg keys: Any?, block: suspend CoroutineScope.() -> Unit)` |
| `Flow<T>.collectAsState(initial)` | Flow to read-only state | Returns `ReadOnlyProperty<Any?, T>` |
| `produceState(initial, vararg keys) { }` | Async state producer | `ComponentScope.produceState(initialValue, vararg keys, producer)` |

## effect()

Wraps `useEffect` with a `CoroutineScope` and structured cleanup via `onCleanup`.

### EffectScope

```kotlin
class EffectScope(parentJob: Job) : CoroutineScope {
    fun onCleanup(block: () -> Unit)
}
```

`EffectScope` is a `CoroutineScope` -- you can call `launch { }` directly inside it. When the effect re-runs or the component unmounts, the parent job is cancelled and `onCleanup` is invoked.

### Run Every Render (no keys)

```kotlin
effect {
    JsUtil.consoleLog("Component rendered")
}
```

### Run When Dependencies Change

```kotlin
var query by state("")

effect(query) {
    JsUtil.consoleLog("Query changed to: $query")
    // Optionally launch async work
    val job = launch {
        delay(300)  // debounce
        performSearch(query)
    }
    onCleanup { job.cancel() }
}
```

### Run Once on Mount

```kotlin
effectOnce {
    val id = JsUtil.setInterval({ tick() }, 1000)
    onCleanup { JsUtil.clearInterval(id) }
}
```

`effectOnce` passes `Hooks.deps()` (empty array) internally, so the effect runs only on mount and the cleanup runs on unmount.

### Timer Example

```kotlin
val Timer = fc("Timer") {
    var seconds by state(0)

    effectOnce {
        val id = JsUtil.setInterval({ seconds++ }, 1000)
        onCleanup { JsUtil.clearInterval(id) }
    }

    div {
        h2 { +"Elapsed: ${seconds}s" }
    }
}
```

## launchedEffect()

Compose-inspired coroutine launcher. Automatically creates a coroutine scope, launches the block, and cancels it on unmount or when dependencies change. No manual `onCleanup` needed for the coroutine itself.

### Signature

```kotlin
fun ComponentScope.launchedEffect(
    vararg keys: Any?,
    block: suspend CoroutineScope.() -> Unit
)
```

When `keys` is empty, `launchedEffect` passes `Hooks.deps()` (empty array) -- runs once on mount. When keys are provided, it re-launches when any key changes.

### Data Fetching Example

```kotlin
val UserProfile = fc("UserProfile") {
    val userId = propString("userId")
    var profileName by state("Loading...")
    var error by state("")

    launchedEffect(userId) {
        try {
            val name = fetchUserName(userId)  // suspend function
            profileName = name
        } catch (e: Exception) {
            error = e.message ?: "Unknown error"
        }
    }

    div {
        show(error.isNotEmpty()) {
            p { +"Error: $error"; style { color = "red" } }
        }
        show(error.isEmpty()) {
            p { +"Profile: $profileName" }
        }
    }
}
```

### Polling Example

```kotlin
launchedEffect {
    while (isActive) {
        val data = fetchLatestData()  // suspend
        items = data
        delay(5000)  // poll every 5 seconds
    }
}
```

The coroutine is cancelled automatically on unmount -- no leak.

## Flow.collectAsState()

Collects a Kotlin `Flow` into React state. Starts collecting on mount, cancels on unmount. Returns a **read-only** delegate.

### Signatures

```kotlin
fun Flow<Int>.collectAsState(initial: Int): CollectedIntState
fun Flow<String>.collectAsState(initial: String): CollectedStringState
fun Flow<Boolean>.collectAsState(initial: Boolean): CollectedBooleanState
fun Flow<Double>.collectAsState(initial: Double): CollectedDoubleState
```

All return `ReadOnlyProperty<Any?, T>` -- use with `val x by`.

### Clock Example with Flow

```kotlin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

val clock = flow {
    var ticks = 0
    while (true) {
        emit(ticks++)
        delay(1000)
    }
}

val ClockDisplay = fc("ClockDisplay") {
    val seconds by clock.collectAsState(0)

    div {
        h2 { +"Seconds elapsed: $seconds" }
    }
}
```

### Important Rules for collectAsState

1. **Call during render** (inside `fc { }` body), never inside `effect` or `launchedEffect`.
2. It calls `Hooks.useState` and `Hooks.useEffect` internally, so it must obey Rules of Hooks.
3. The returned delegate is **read-only** -- you cannot assign to it. If you need read-write state from async sources, use `produceState` instead.

### Multiple Flows

```kotlin
val ClockAndStatus = fc("ClockAndStatus") {
    val seconds by tickFlow.collectAsState(0)
    val status by statusFlow.collectAsState("idle")

    div {
        p { +"Time: $seconds" }
        p { +"Status: $status" }
    }
}
```

## produceState()

Creates state that is asynchronously produced by a coroutine. Similar to Jetpack Compose's `produceState`. The coroutine has access to a mutable `value` property.

### Signatures

```kotlin
fun ComponentScope.produceState(
    initialValue: Int,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<Int>.() -> Unit
): ReadOnlyProperty<Any?, Int>

fun ComponentScope.produceState(
    initialValue: String,
    vararg keys: Any?,
    producer: suspend ProduceStateScope<String>.() -> Unit
): ReadOnlyProperty<Any?, String>
```

### ProduceStateScope

```kotlin
class ProduceStateScope<T>(
    private val getter: () -> T,
    private val setter: (T) -> Unit
) {
    var value: T  // read/write -- setting triggers re-render
}
```

### Countdown Example

```kotlin
val Countdown = fc("Countdown") {
    val startFrom = propInt("start")

    val remaining by produceState(startFrom, startFrom) {
        while (value > 0) {
            delay(1000)
            value--
        }
    }

    div {
        h2 {
            +if (remaining > 0) "Countdown: $remaining" else "Done!"
        }
    }
}
```

### Async Data Loading

```kotlin
val UserName = fc("UserName") {
    val userId = propString("userId")

    val name by produceState("Loading...", userId) {
        value = fetchUserName(userId)  // suspend call
    }

    span { +name }
}
```

When `userId` changes, the producer re-runs: the old coroutine is cancelled and a fresh one starts with `value` reset to `"Loading..."`.

## Combining Features

### Data Table with Search, Debounce, and Loading State

```kotlin
val DataTable = fc("DataTable") {
    var query by state("")
    var loading by state(false)

    val results by produceState("[]", query) {
        delay(300)  // debounce
        value = fetchResults(query)
    }

    div {
        input("text") {
            placeholder("Search...")
            value(query)
            onChange { query = it.getTarget().getValue() }
        }
        show(loading) {
            p { +"Loading..." }
        }
        // render results...
    }
}
```

## Coroutine Dispatcher

All coroutine scopes in teavm-react use `JsDispatcher`, a custom `CoroutineDispatcher` that dispatches to the browser's event loop via `setTimeout`. This is required because TeaVM runs in a single-threaded browser environment -- `Dispatchers.Default` and `Dispatchers.IO` are not available.

You do not need to specify the dispatcher manually -- `effect`, `launchedEffect`, `collectAsState`, and `produceState` all use `JsDispatcher` internally.

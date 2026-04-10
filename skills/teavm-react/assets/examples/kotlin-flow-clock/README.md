# Kotlin Flow Clock

An elapsed-time clock demonstrating `Flow.collectAsState` to bridge Kotlin coroutines into React state.

## What it demonstrates
- `kotlinx.coroutines.flow.flow { }` to create a cold flow that emits every second
- `Flow<T>.collectAsState(initial)` to subscribe a flow as React state
- Automatic re-rendering on each flow emission
- Coroutines dependency setup in Maven

## Build and run
```bash
mvn process-classes
```

Serve the `target/kotlin-flow-clock-1.0-SNAPSHOT/` directory with any static file server.

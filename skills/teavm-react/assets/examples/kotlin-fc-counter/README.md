# Kotlin FC Counter

A minimal counter using the Kotlin `fc()` DSL for defining functional components.

## What it demonstrates
- `fc("name") { ... }` to define a named functional component
- `var x by state(0)` for delegated state that reads/writes like a plain variable
- Kotlin HTML builder DSL (`div {}`, `h2 {}`, `button {}`)
- `+"text"` unary plus operator for text nodes

## Build and run
```bash
mvn process-classes
```

Serve the `target/kotlin-fc-counter-1.0-SNAPSHOT/` directory with any static file server.

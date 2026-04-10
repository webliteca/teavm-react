# Java Builder Form

A contact form demonstrating the DomBuilder DSL for building UIs with a fluent, type-safe API.

## What it demonstrates
- `DomBuilder` inner classes (`Div`, `H1`, `P`, `Button`, `Input.text()`) for fluent element construction
- Controlled inputs with `onChange` and `value`
- Conditional rendering for validation feedback
- `disabled` button state tied to form validity

## Build and run
```bash
mvn process-classes
```

Serve the `target/java-builder-form-1.0-SNAPSHOT/` directory with any static file server.

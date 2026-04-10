# Java Functional Counter

A minimal counter app demonstrating the functional component pattern with TeaVM React.

## What it demonstrates
- `useState` hook for managing integer state
- Static `Html.*` imports for concise element creation
- `wrapComponent` to register a render function as a named React component
- `createRoot` / `render` for mounting the app

## Build and run
```bash
mvn process-classes
```

Serve the `target/java-functional-counter-1.0-SNAPSHOT/` directory with any static file server (e.g. `python3 -m http.server -d target/java-functional-counter-1.0-SNAPSHOT`).

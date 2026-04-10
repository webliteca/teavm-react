# Java Class Stopwatch

A stopwatch app using the class-based `ReactView` component pattern with lifecycle hooks.

## What it demonstrates
- `ReactView` subclass with `render()`, `onMount()`, and `onUnmount()` lifecycle methods
- `JsUtil.setInterval` / `JsUtil.clearInterval` for timers
- `useRefInt` to store the interval ID across renders
- `ReactView.view(ViewFactory, name)` to register a class-based component

## Build and run
```bash
mvn process-classes
```

Serve the `target/java-class-stopwatch-1.0-SNAPSHOT/` directory with any static file server.

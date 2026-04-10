# CLAUDE.md

## Project Overview

teavm-react is a Java/Kotlin library for writing React 18 web applications compiled to JavaScript via TeaVM. It provides type-safe React bindings including components, hooks, context, refs, and event handling.

## Architecture

Three Maven modules:
- **teavm-react-core** — Java bindings to React 18 via TeaVM JSBody annotations
- **teavm-react-kotlin** — Idiomatic Kotlin wrapper with delegated properties, coroutines, and DSL builders
- **teavm-react-demo** — Kitchen-sink demo application

Compilation flow: Java/Kotlin source -> javac/kotlinc -> class files -> TeaVM compiler -> classes.js -> runs with React 18 from CDN in browser.

## Build Commands

```bash
# Build everything
mvn clean install

# Build and run demo
./run.sh [port]    # default port 8080

# Run unit tests
mvn test -pl teavm-react-core,teavm-react-kotlin

# Run integration tests (TeaVM compilation + verification)
mvn install -N && mvn install -pl teavm-react-core,teavm-react-kotlin -DskipTests && mvn process-classes test -pl teavm-react-demo

# Compile TeaVM output only
mvn process-classes -pl teavm-react-demo
```

## Prerequisites

- JDK 21 (source/target compatibility is Java 11)
- Maven 3.8+
- Python 3 (for run.sh dev server)

## Key Dependencies

- TeaVM 0.13.1
- Kotlin 1.9.25
- kotlinx-coroutines-core 1.8.1
- JUnit 5.10.3

## Source Layout

```
teavm-react-core/src/main/java/ca/weblite/teavmreact/
  core/        — React, ReactDOM, ReactElement, ReactContext bindings
  hooks/       — useState, useEffect, useContext, useRef, useMemo, useCallback
  html/        — Html fluent DSL (Html.div().onClick(...).build())
  events/      — Type-safe event handlers (MouseEvent, KeyboardEvent, etc.)
  component/   — ReactView abstract class for class-based components

teavm-react-kotlin/src/main/kotlin/ca/weblite/teavmreact/kotlin/
  ComponentScope, StateDelegate, RefDelegate, HtmlBuilder, HtmlDsl,
  StyleBuilder, EffectScope, FlowExtensions, TypedContext, JsDispatcher
```

## AI Skills

This project publishes AI assistant skills via the `skills-jar-plugin`. To install skills for all dependencies into `.claude/skills/`:

```bash
mvn ca.weblite:skills-jar-plugin:install
```

To list available skills without installing:

```bash
mvn ca.weblite:skills-jar-plugin:list
```

Skills are packaged as `-skills.jar` classifier artifacts and deployed alongside the library JARs.

## Code Conventions

- Java 11 language level
- TeaVM JSBody annotations for JS interop — no runtime reflection
- Builder pattern for HTML elements in Java
- Lambda-with-receiver DSL in Kotlin
- Package: `ca.weblite.teavmreact`

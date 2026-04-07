# teavm-react

Write React 18 applications in Java and Kotlin, compiled to JavaScript via [TeaVM](https://teavm.org/).

teavm-react provides fully-typed React bindings -- components, hooks, context, refs, events, and memoization -- with complete type safety and IDE support. No JavaScript required.

## Features

- Full React 18 API: functional components, hooks (`useState`, `useEffect`, `useContext`, `useRef`, `useMemo`, `useCallback`, `useReducer`), context, and refs
- Fluent HTML DSL for building elements in Java
- Optional Kotlin wrapper with delegated properties, coroutine-based effects, and lambda-with-receiver DSL
- Class-based components via `ReactView`
- Type-safe event handling (click, keyboard, change, focus, submit)
- Compiles to efficient JavaScript via TeaVM

## Modules

| Module | Description |
|--------|-------------|
| `teavm-react-core` | Core Java bindings to React 18 |
| `teavm-react-kotlin` | Idiomatic Kotlin DSL and coroutine integration |
| `teavm-react-demo` | Kitchen-sink demo application |

## Quick Start

### Prerequisites

- JDK 21
- Maven 3.8+

### Build

```bash
mvn clean install
```

### Run the Demo

```bash
./run.sh
# Open http://localhost:8080
```

Or manually:

```bash
mvn clean install -N
mvn install -pl teavm-react-core -q
mvn -f teavm-react-demo/pom.xml process-classes -q
python3 -m http.server 8080 --directory teavm-react-demo/target/webapp
```

## Usage

### Java

```java
import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.StateHandle;

// Define a functional component
RenderFunction counter = props -> {
    StateHandle<Integer> count = Hooks.useState(0);

    return div(
        h1("Count: " + count.get()),
        button("Increment")
            .onClick(e -> count.set(count.get() + 1))
            .build()
    );
};

// Render to the DOM
ReactRoot root = ReactDOM.createRoot(document.getElementById("root"));
root.render(React.createElement(React.wrapComponent(counter)));
```

### Kotlin

```kotlin
import ca.weblite.teavmreact.kotlin.*

val counter = component {
    var count by state(0)

    div {
        h1 { +"Count: $count" }
        button {
            +"Increment"
            onClick { count++ }
        }
    }
}
```

## Testing

```bash
# Unit tests
mvn test -pl teavm-react-core,teavm-react-kotlin

# Integration tests (TeaVM compilation verification)
mvn install -N && \
mvn install -pl teavm-react-core,teavm-react-kotlin -DskipTests && \
mvn process-classes test -pl teavm-react-demo
```

## Documentation

- [Developer Guide](docs/developer-guide.adoc) -- comprehensive API reference and examples
- [Kotlin DSL Design](docs/kotlin-dsl-design.md) -- design specification for the Kotlin wrapper

## License

See [LICENSE](LICENSE) for details.

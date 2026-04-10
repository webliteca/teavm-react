# Build and Deploy Reference

Read this file for Maven commands, dev server setup, HTML shell templates, TeaVM plugin configuration, CI pipelines, and troubleshooting build issues.

## Maven Commands

### Full Build

```bash
mvn clean install
```

Builds all three modules (core, kotlin, demo), runs tests, and installs to local repo.

### Build a Single Module

```bash
# Build just the core library
mvn install -pl teavm-react-core

# Build just the Kotlin DSL
mvn install -pl teavm-react-kotlin

# Compile TeaVM output for demo (no tests)
mvn process-classes -pl teavm-react-demo
```

### Incremental Build (Dev Profile)

```bash
mvn process-classes -pl teavm-react-demo -Pdev
```

The `-Pdev` profile enables incremental TeaVM compilation and disables source maps and debug info for faster builds.

### Install Parent POM First

Before building child modules independently, install the parent POM:

```bash
mvn install -N  # -N = non-recursive, parent only
```

Without this, child modules cannot resolve `${teavm.version}` and other managed properties.

### Full Build Sequence (from clean checkout)

```bash
mvn install -N
mvn install -pl teavm-react-core,teavm-react-kotlin -DskipTests
mvn process-classes -pl teavm-react-demo
```

### Running Tests

```bash
# Unit tests only (core + kotlin)
mvn test -pl teavm-react-core,teavm-react-kotlin

# Integration tests (compile + verify demo)
mvn install -N && \
mvn install -pl teavm-react-core,teavm-react-kotlin -DskipTests && \
mvn process-classes test -pl teavm-react-demo
```

## run.sh -- Production-Style Server

```bash
./run.sh [port]   # default: 8080
```

What it does:
1. Builds the parent POM (`install -N`)
2. Builds teavm-react-core
3. Runs `process-classes` on the demo module (TeaVM compilation)
4. Verifies `classes.js` was produced
5. Starts a Python HTTP server on `target/webapp/`

## dev.sh -- Live-Reload Development Server

```bash
./dev.sh [port]   # default: 8080
```

What it does:
1. Uses `mvnd` (Maven Daemon) if available, falls back to `mvn`
2. Builds everything with `-Pdev` (incremental, no source maps)
3. Starts a Java-based dev server with file watching and SSE live-reload
4. On source file changes: recompiles only changed modules, notifies browser via SSE

### Installing mvnd for Faster Builds

Maven Daemon keeps a warm JVM between builds. Dramatically reduces rebuild time.

```bash
# macOS
brew install mvndaemon/tap/mvnd

# Linux (manual)
curl -L https://github.com/apache/maven-mvnd/releases/latest/download/mvnd-linux-amd64.zip -o mvnd.zip
unzip mvnd.zip && sudo mv mvnd-*/bin/mvnd /usr/local/bin/

# Verify
mvnd --version
```

`dev.sh` auto-detects and uses `mvnd` when available.

## HTML Shell Template

Copy this complete template for new projects. The order of script tags is critical.

### Development Version

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My teavm-react App</title>

    <!-- React 18 DEVELOPMENT (includes warnings and error messages) -->
    <script crossorigin src="https://unpkg.com/react@18/umd/react.development.js"></script>
    <script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.development.js"></script>

    <style>
        /* Your CSS here */
    </style>
</head>
<body>
    <div id="root">
        <p>Loading...</p>
    </div>

    <!-- TeaVM output MUST come after React scripts -->
    <script src="js/classes.js"></script>
    <script>
        if (typeof main === 'function') {
            main([]);
        } else {
            console.error('teavm-react: main() not found in classes.js');
        }
    </script>
</body>
</html>
```

### Production Version

Replace the React CDN URLs with minified production builds:

```html
<!-- React 18 PRODUCTION (minified, no dev warnings) -->
<script crossorigin src="https://unpkg.com/react@18/umd/react.production.min.js"></script>
<script crossorigin src="https://unpkg.com/react-dom@18/umd/react-dom.production.min.js"></script>
```

### Placement Rules

1. React scripts **must** load before `classes.js`
2. `classes.js` **must** load before `main([])`
3. The `<div id="root">` must exist before `main([])` runs

## TeaVM Maven Plugin Configuration

### Standard Configuration

```xml
<plugin>
    <groupId>org.teavm</groupId>
    <artifactId>teavm-maven-plugin</artifactId>
    <version>0.13.1</version>
    <executions>
        <execution>
            <goals><goal>compile</goal></goals>
            <phase>process-classes</phase>
            <configuration>
                <mainClass>com.example.App</mainClass>
                <targetType>JAVASCRIPT</targetType>
                <minifying>false</minifying>
                <debugInformationGenerated>true</debugInformationGenerated>
                <sourceMapsGenerated>true</sourceMapsGenerated>
                <stopOnErrors>true</stopOnErrors>
                <targetDirectory>${project.build.directory}/webapp/js</targetDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Key Configuration Options

| Option | Values | Description |
|--------|--------|-------------|
| `mainClass` | Fully qualified class | Entry point with `main(String[])` |
| `targetType` | `JAVASCRIPT` | Output format |
| `minifying` | `true`/`false` | Minify output JS |
| `debugInformationGenerated` | `true`/`false` | Include debug info |
| `sourceMapsGenerated` | `true`/`false` | Generate .js.map |
| `incremental` | `true`/`false` | Reuse previous compilation |
| `stopOnErrors` | `true`/`false` | Fail build on TeaVM errors |
| `targetDirectory` | Path | Where to write classes.js |

### Dev Profile (Faster Rebuilds)

```xml
<profile>
    <id>dev</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.teavm</groupId>
                <artifactId>teavm-maven-plugin</artifactId>
                <version>0.13.1</version>
                <executions>
                    <execution>
                        <goals><goal>compile</goal></goals>
                        <phase>process-classes</phase>
                        <configuration>
                            <mainClass>com.example.App</mainClass>
                            <targetType>JAVASCRIPT</targetType>
                            <minifying>false</minifying>
                            <debugInformationGenerated>false</debugInformationGenerated>
                            <sourceMapsGenerated>false</sourceMapsGenerated>
                            <incremental>true</incremental>
                            <stopOnErrors>true</stopOnErrors>
                            <targetDirectory>${project.build.directory}/webapp/js</targetDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

Activate with `mvn process-classes -Pdev`.

## Webapp Resource Copying

Add this plugin to copy `src/main/webapp/` (HTML, CSS, images) to `target/webapp/`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.3.1</version>
    <executions>
        <execution>
            <id>copy-webapp</id>
            <phase>process-classes</phase>
            <goals><goal>copy-resources</goal></goals>
            <configuration>
                <outputDirectory>${project.build.directory}/webapp</outputDirectory>
                <resources>
                    <resource>
                        <directory>src/main/webapp</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## CI Pipeline (GitHub Actions)

```yaml
name: Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build
        run: mvn clean install
      - name: Verify classes.js
        run: test -f teavm-react-demo/target/webapp/js/classes.js
```

## Troubleshooting

### "React is not defined"

**Cause:** `classes.js` loaded before the React CDN scripts, or the React scripts failed to load.

**Fix:** Ensure the React `<script>` tags appear before the `classes.js` `<script>` tag in the HTML. Check browser DevTools Network tab to confirm the CDN scripts loaded successfully.

### "classes.js not produced"

**Cause:** TeaVM compilation failed silently, or `process-classes` phase was not run.

**Fix:**
1. Run `mvn process-classes -pl <your-module>` explicitly
2. Check for TeaVM compilation errors in Maven output
3. Ensure the parent POM was installed first: `mvn install -N`
4. Verify `mainClass` in the TeaVM plugin config points to a class with `public static void main(String[] args)`

### "main is not a function"

**Cause:** TeaVM compiled but the entry point class was not found, or the `main` method signature is wrong.

**Fix:** The entry point must be `public static void main(String[] args)`. Check the `mainClass` setting in the TeaVM plugin configuration matches your actual class.

### Build succeeds but page is blank

**Cause:** Usually a runtime error in the React component. Check the browser console (F12) for JavaScript errors. Common causes:
- Hooks called conditionally (violates Rules of Hooks)
- Missing `.build()` on ElementBuilder (returns builder, not ReactElement)
- Null pointer in state access before initialization

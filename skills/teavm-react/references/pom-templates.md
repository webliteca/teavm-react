# POM Templates Reference

Read this file to get copy-pasteable `pom.xml` files for new teavm-react projects. Three templates: Java-only, Java+Kotlin, and production build with minification.

## Template A: Java-Only Project

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- ============================================================
         Project coordinates — change these for your app
         ============================================================ -->
    <groupId>com.example</groupId>
    <artifactId>my-teavm-react-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>My TeaVM React App</name>

    <!-- ============================================================
         Version properties
         ============================================================ -->
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <teavm.version>0.13.1</teavm.version>
        <teavm-react.version>0.1.0-SNAPSHOT</teavm-react.version>
    </properties>

    <!-- ============================================================
         Dependencies
         ============================================================ -->
    <dependencies>
        <!-- teavm-react core library (React bindings, hooks, Html DSL) -->
        <dependency>
            <groupId>ca.weblite</groupId>
            <artifactId>teavm-react-core</artifactId>
            <version>${teavm-react.version}</version>
        </dependency>

        <!-- TeaVM class library (subset of java.* for browser) -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-classlib</artifactId>
            <version>${teavm.version}</version>
        </dependency>

        <!-- TeaVM JSO (JavaScript Object interop: @JSBody, @JSFunctor) -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-jso</artifactId>
            <version>${teavm.version}</version>
        </dependency>

        <!-- TeaVM JSO APIs (DOM, typed arrays, browser APIs) -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-jso-apis</artifactId>
            <version>${teavm.version}</version>
        </dependency>
    </dependencies>

    <!-- ============================================================
         Build plugins
         ============================================================ -->
    <build>
        <plugins>
            <!-- TeaVM compiler: Java bytecode → JavaScript -->
            <plugin>
                <groupId>org.teavm</groupId>
                <artifactId>teavm-maven-plugin</artifactId>
                <version>${teavm.version}</version>
                <executions>
                    <execution>
                        <goals><goal>compile</goal></goals>
                        <phase>process-classes</phase>
                        <configuration>
                            <!-- Change to your entry point class -->
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

            <!-- Copy src/main/webapp → target/webapp (HTML shell, CSS, images) -->
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
        </plugins>
    </build>
</project>
```

### Minimal Entry Point (Java)

```java
package com.example;

import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.hooks.*;
import org.teavm.jso.dom.html.HTMLDocument;
import static ca.weblite.teavmreact.html.Html.*;

public class App {
    static JSObject AppComponent = React.wrapComponent(App::render, "App");

    static ReactElement render(org.teavm.jso.JSObject props) {
        StateHandle<Integer> count = Hooks.useState(0);
        return div(
            h1("My App"),
            p("Count: " + count.getInt()),
            button("Increment").onClick(e -> count.updateInt(n -> n + 1)).build()
        );
    }

    public static void main(String[] args) {
        ReactRoot root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(component(AppComponent));
    }
}
```

## Template B: Java + Kotlin Project

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- ============================================================
         Project coordinates
         ============================================================ -->
    <groupId>com.example</groupId>
    <artifactId>my-kotlin-react-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>My Kotlin TeaVM React App</name>

    <!-- ============================================================
         Version properties
         ============================================================ -->
    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <teavm.version>0.13.1</teavm.version>
        <teavm-react.version>0.1.0-SNAPSHOT</teavm-react.version>
        <kotlin.version>1.9.25</kotlin.version>
        <coroutines.version>1.8.1</coroutines.version>
    </properties>

    <!-- ============================================================
         Dependencies
         ============================================================ -->
    <dependencies>
        <!-- teavm-react core (React bindings, hooks, Html DSL) -->
        <dependency>
            <groupId>ca.weblite</groupId>
            <artifactId>teavm-react-core</artifactId>
            <version>${teavm-react.version}</version>
        </dependency>

        <!-- teavm-react Kotlin DSL (fc, state delegates, HtmlBuilder, coroutines) -->
        <dependency>
            <groupId>ca.weblite</groupId>
            <artifactId>teavm-react-kotlin</artifactId>
            <version>${teavm-react.version}</version>
        </dependency>

        <!-- Kotlin standard library -->
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${kotlin.version}</version>
        </dependency>

        <!-- Kotlin coroutines (required for effect, launchedEffect, Flow) -->
        <dependency>
            <groupId>org.jetbrains.kotlinx</groupId>
            <artifactId>kotlinx-coroutines-core</artifactId>
            <version>${coroutines.version}</version>
        </dependency>

        <!-- TeaVM class library -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-classlib</artifactId>
            <version>${teavm.version}</version>
        </dependency>

        <!-- TeaVM JSO -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-jso</artifactId>
            <version>${teavm.version}</version>
        </dependency>

        <!-- TeaVM JSO APIs -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-jso-apis</artifactId>
            <version>${teavm.version}</version>
        </dependency>
    </dependencies>

    <!-- ============================================================
         Build plugins
         ============================================================ -->
    <build>
        <!-- Kotlin source directories -->
        <sourceDirectory>src/main/kotlin</sourceDirectory>

        <plugins>
            <!-- Kotlin compiler — must run before TeaVM -->
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <phase>compile</phase>
                        <goals><goal>compile</goal></goals>
                    </execution>
                </executions>
            </plugin>

            <!-- TeaVM compiler: bytecode → JavaScript -->
            <plugin>
                <groupId>org.teavm</groupId>
                <artifactId>teavm-maven-plugin</artifactId>
                <version>${teavm.version}</version>
                <executions>
                    <execution>
                        <goals><goal>compile</goal></goals>
                        <phase>process-classes</phase>
                        <configuration>
                            <!-- Change to your entry point class -->
                            <mainClass>com.example.AppKt</mainClass>
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

            <!-- Copy webapp resources -->
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
        </plugins>
    </build>
</project>
```

### Minimal Entry Point (Kotlin)

```kotlin
package com.example

import ca.weblite.teavmreact.core.ReactDOM
import ca.weblite.teavmreact.kotlin.*
import org.teavm.jso.dom.html.HTMLDocument

val App = fc("App") {
    var count by state(0)
    div {
        h1 { +"My App" }
        p { +"Count: $count" }
        button { +"Increment"; onClick { count++ } }
    }
}

fun main(args: Array<String>) {
    val root = ReactDOM.createRoot(
        HTMLDocument.current().getElementById("root")
    )
    root.render(component(App))
}
```

Note: For Kotlin top-level `main`, the `mainClass` in TeaVM config is `com.example.AppKt` (Kotlin appends `Kt` to the file name).

## Template C: Production Build with Minification

This template extends Template B with a `prod` profile for minified output.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-prod-react-app</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>Production TeaVM React App</name>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <teavm.version>0.13.1</teavm.version>
        <teavm-react.version>0.1.0-SNAPSHOT</teavm-react.version>
        <kotlin.version>1.9.25</kotlin.version>
        <coroutines.version>1.8.1</coroutines.version>
    </properties>

    <dependencies>
        <!-- teavm-react core -->
        <dependency>
            <groupId>ca.weblite</groupId>
            <artifactId>teavm-react-core</artifactId>
            <version>${teavm-react.version}</version>
        </dependency>

        <!-- teavm-react Kotlin DSL -->
        <dependency>
            <groupId>ca.weblite</groupId>
            <artifactId>teavm-react-kotlin</artifactId>
            <version>${teavm-react.version}</version>
        </dependency>

        <!-- Kotlin -->
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-stdlib</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlinx</groupId>
            <artifactId>kotlinx-coroutines-core</artifactId>
            <version>${coroutines.version}</version>
        </dependency>

        <!-- TeaVM -->
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-classlib</artifactId>
            <version>${teavm.version}</version>
        </dependency>
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-jso</artifactId>
            <version>${teavm.version}</version>
        </dependency>
        <dependency>
            <groupId>org.teavm</groupId>
            <artifactId>teavm-jso-apis</artifactId>
            <version>${teavm.version}</version>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>src/main/kotlin</sourceDirectory>

        <plugins>
            <!-- Kotlin compiler -->
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <phase>compile</phase>
                        <goals><goal>compile</goal></goals>
                    </execution>
                </executions>
            </plugin>

            <!-- TeaVM compiler (default: development settings) -->
            <plugin>
                <groupId>org.teavm</groupId>
                <artifactId>teavm-maven-plugin</artifactId>
                <version>${teavm.version}</version>
                <executions>
                    <execution>
                        <goals><goal>compile</goal></goals>
                        <phase>process-classes</phase>
                        <configuration>
                            <mainClass>com.example.AppKt</mainClass>
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

            <!-- Copy webapp resources -->
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
        </plugins>
    </build>

    <!-- ============================================================
         Profiles
         ============================================================ -->
    <profiles>
        <!-- Dev profile: fast incremental builds, no debug artifacts -->
        <profile>
            <id>dev</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.teavm</groupId>
                        <artifactId>teavm-maven-plugin</artifactId>
                        <version>${teavm.version}</version>
                        <executions>
                            <execution>
                                <goals><goal>compile</goal></goals>
                                <phase>process-classes</phase>
                                <configuration>
                                    <mainClass>com.example.AppKt</mainClass>
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

        <!-- Production profile: minified output, no debug info -->
        <profile>
            <id>prod</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.teavm</groupId>
                        <artifactId>teavm-maven-plugin</artifactId>
                        <version>${teavm.version}</version>
                        <executions>
                            <execution>
                                <goals><goal>compile</goal></goals>
                                <phase>process-classes</phase>
                                <configuration>
                                    <mainClass>com.example.AppKt</mainClass>
                                    <targetType>JAVASCRIPT</targetType>
                                    <!-- Minify for smaller output -->
                                    <minifying>true</minifying>
                                    <!-- No debug artifacts in prod -->
                                    <debugInformationGenerated>false</debugInformationGenerated>
                                    <sourceMapsGenerated>false</sourceMapsGenerated>
                                    <incremental>false</incremental>
                                    <stopOnErrors>true</stopOnErrors>
                                    <targetDirectory>${project.build.directory}/webapp/js</targetDirectory>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
```

### Building with Profiles

```bash
# Development (default)
mvn process-classes

# Fast incremental dev
mvn process-classes -Pdev

# Production minified
mvn process-classes -Pprod
```

## Version Summary

| Dependency | Version | Property |
|-----------|---------|----------|
| teavm-react | `0.1.0-SNAPSHOT` | `${teavm-react.version}` |
| TeaVM | `0.13.1` | `${teavm.version}` |
| Kotlin | `1.9.25` | `${kotlin.version}` |
| kotlinx-coroutines | `1.8.1` | `${coroutines.version}` |
| JDK (build) | 21 | N/A |
| Java source/target | 11 | `maven.compiler.source/target` |

## Directory Structure

```
my-app/
  pom.xml
  src/
    main/
      java/          (Java sources, Template A)
      kotlin/        (Kotlin sources, Templates B/C)
      webapp/
        index.html   (HTML shell — see build-and-deploy.md)
        style.css    (optional)
  target/
    webapp/
      js/
        classes.js   (TeaVM output)
      index.html     (copied from src/main/webapp)
```

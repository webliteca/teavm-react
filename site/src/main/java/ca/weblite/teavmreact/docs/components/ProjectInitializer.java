package ca.weblite.teavmreact.docs.components;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.html.DomBuilder.*;
import ca.weblite.teavmreact.docs.El;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;

/**
 * Spring Initializr-style component that generates a starter teavm-react
 * Maven project as a downloadable ZIP file.  Supports both Java and Kotlin.
 */
public class ProjectInitializer {

    // -- JS interop for ZIP generation and download ----------------------------

    @JSBody(params = {"groupId", "artifactId", "packageName", "javaVersion", "teavmReactVersion"},
            script =
        "var zip = new JSZip();" +
        "var pkgPath = packageName.replace(/\\./g, '/');" +
        "var mainClass = packageName + '.App';" +

        // pom.xml
        "var pom = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n'" +
        " + '<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\\n'" +
        " + '         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\\n'" +
        " + '         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\\n'" +
        " + '           http://maven.apache.org/xsd/maven-4.0.0.xsd\">\\n'" +
        " + '    <modelVersion>4.0.0</modelVersion>\\n\\n'" +
        " + '    <groupId>' + groupId + '</groupId>\\n'" +
        " + '    <artifactId>' + artifactId + '</artifactId>\\n'" +
        " + '    <version>1.0.0-SNAPSHOT</version>\\n\\n'" +
        " + '    <properties>\\n'" +
        " + '        <maven.compiler.source>' + javaVersion + '</maven.compiler.source>\\n'" +
        " + '        <maven.compiler.target>' + javaVersion + '</maven.compiler.target>\\n'" +
        " + '        <teavm.version>0.13.1</teavm.version>\\n'" +
        " + '        <teavm-react.version>' + teavmReactVersion + '</teavm-react.version>\\n'" +
        " + '    </properties>\\n\\n'" +
        " + '    <dependencies>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>ca.weblite</groupId>\\n'" +
        " + '            <artifactId>teavm-react-core</artifactId>\\n'" +
        " + '            <version>${teavm-react.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.teavm</groupId>\\n'" +
        " + '            <artifactId>teavm-classlib</artifactId>\\n'" +
        " + '            <version>${teavm.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.teavm</groupId>\\n'" +
        " + '            <artifactId>teavm-jso</artifactId>\\n'" +
        " + '            <version>${teavm.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.teavm</groupId>\\n'" +
        " + '            <artifactId>teavm-jso-apis</artifactId>\\n'" +
        " + '            <version>${teavm.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '    </dependencies>\\n\\n'" +
        " + '    <build>\\n'" +
        " + '        <plugins>\\n'" +
        " + '            <plugin>\\n'" +
        " + '                <groupId>org.teavm</groupId>\\n'" +
        " + '                <artifactId>teavm-maven-plugin</artifactId>\\n'" +
        " + '                <version>${teavm.version}</version>\\n'" +
        " + '                <executions>\\n'" +
        " + '                    <execution>\\n'" +
        " + '                        <goals>\\n'" +
        " + '                            <goal>compile</goal>\\n'" +
        " + '                        </goals>\\n'" +
        " + '                        <phase>process-classes</phase>\\n'" +
        " + '                        <configuration>\\n'" +
        " + '                            <mainClass>' + mainClass + '</mainClass>\\n'" +
        " + '                            <targetType>JAVASCRIPT</targetType>\\n'" +
        " + '                            <minifying>false</minifying>\\n'" +
        " + '                            <targetDirectory>\\n'" +
        " + '                                ${project.build.directory}/webapp/js\\n'" +
        " + '                            </targetDirectory>\\n'" +
        " + '                        </configuration>\\n'" +
        " + '                    </execution>\\n'" +
        " + '                </executions>\\n'" +
        " + '            </plugin>\\n'" +
        " + '            <plugin>\\n'" +
        " + '                <groupId>org.apache.maven.plugins</groupId>\\n'" +
        " + '                <artifactId>maven-resources-plugin</artifactId>\\n'" +
        " + '                <version>3.3.1</version>\\n'" +
        " + '                <executions>\\n'" +
        " + '                    <execution>\\n'" +
        " + '                        <id>copy-webapp</id>\\n'" +
        " + '                        <phase>process-classes</phase>\\n'" +
        " + '                        <goals><goal>copy-resources</goal></goals>\\n'" +
        " + '                        <configuration>\\n'" +
        " + '                            <outputDirectory>${project.build.directory}/webapp</outputDirectory>\\n'" +
        " + '                            <resources>\\n'" +
        " + '                                <resource>\\n'" +
        " + '                                    <directory>src/main/webapp</directory>\\n'" +
        " + '                                </resource>\\n'" +
        " + '                            </resources>\\n'" +
        " + '                        </configuration>\\n'" +
        " + '                    </execution>\\n'" +
        " + '                </executions>\\n'" +
        " + '            </plugin>\\n'" +
        " + '        </plugins>\\n'" +
        " + '    </build>\\n'" +
        " + '</project>\\n';" +

        // App.java
        "var appJava = 'package ' + packageName + ';\\n\\n'" +
        " + 'import ca.weblite.teavmreact.core.ReactDOM;\\n'" +
        " + 'import ca.weblite.teavmreact.core.ReactElement;\\n'" +
        " + 'import org.teavm.jso.JSObject;\\n'" +
        " + 'import org.teavm.jso.dom.html.HTMLDocument;\\n\\n'" +
        " + 'import static ca.weblite.teavmreact.html.Html.*;\\n\\n'" +
        " + 'public class App {\\n\\n'" +
        " + '    public static void main(String[] args) {\\n'" +
        " + '        var root = ReactDOM.createRoot(\\n'" +
        " + '            HTMLDocument.current().getElementById(\"root\")\\n'" +
        " + '        );\\n'" +
        " + '        root.render(component(App::render, \"App\"));\\n'" +
        " + '    }\\n\\n'" +
        " + '    static ReactElement render(JSObject props) {\\n'" +
        " + '        return div(\\n'" +
        " + '            h1(\"Hello, teavm-react!\"),\\n'" +
        " + '            p(\"Your app is running. Edit App.java and rebuild.\")\\n'" +
        " + '        );\\n'" +
        " + '    }\\n'" +
        " + '}\\n';" +

        // index.html
        "var indexHtml = '<!DOCTYPE html>\\n'" +
        " + '<html lang=\"en\">\\n'" +
        " + '<head>\\n'" +
        " + '    <meta charset=\"UTF-8\">\\n'" +
        " + '    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\\n'" +
        " + '    <title>' + artifactId + '</title>\\n'" +
        " + '    <link rel=\"stylesheet\" href=\"css/styles.css\">\\n'" +
        " + '</head>\\n'" +
        " + '<body>\\n'" +
        " + '    <div id=\"root\"></div>\\n\\n'" +
        " + '    <!-- React 18 from CDN -->\\n'" +
        " + '    <script crossorigin\\n'" +
        " + '      src=\"https://unpkg.com/react@18/umd/react.development.js\">\\n'" +
        " + '    </script>\\n'" +
        " + '    <script crossorigin\\n'" +
        " + '      src=\"https://unpkg.com/react-dom@18/umd/react-dom.development.js\">\\n'" +
        " + '    </script>\\n\\n'" +
        " + '    <!-- TeaVM compiled output -->\\n'" +
        " + '    <script src=\"js/classes.js\"></script>\\n'" +
        " + '    <script>\\n'" +
        " + '        if (typeof main === \"function\") {\\n'" +
        " + '            main([]);\\n'" +
        " + '        }\\n'" +
        " + '    </script>\\n'" +
        " + '</body>\\n'" +
        " + '</html>\\n';" +

        // styles.css
        "var css = '/* Add your styles here */\\n\\n'" +
        " + '#root {\\n'" +
        " + '    max-width: 800px;\\n'" +
        " + '    margin: 0 auto;\\n'" +
        " + '    padding: 2rem;\\n'" +
        " + '    font-family: -apple-system, BlinkMacSystemFont, sans-serif;\\n'" +
        " + '}\\n';" +

        // run.sh
        "var runSh = '#!/bin/bash\\n'" +
        " + 'PORT=${1:-8080}\\n'" +
        " + 'mvn clean process-classes -q\\n'" +
        " + 'echo \"Serving at http://localhost:$PORT\"\\n'" +
        " + 'python3 -m http.server $PORT --directory target/webapp\\n';" +

        // Add files to zip
        "var root = artifactId + '/';" +
        "zip.file(root + 'pom.xml', pom);" +
        "zip.file(root + 'run.sh', runSh, {unixPermissions: '755'});" +
        "zip.file(root + 'src/main/java/' + pkgPath + '/App.java', appJava);" +
        "zip.file(root + 'src/main/webapp/index.html', indexHtml);" +
        "zip.file(root + 'src/main/webapp/css/styles.css', css);" +

        // Generate and trigger download
        "zip.generateAsync({type: 'blob', platform: 'UNIX'}).then(function(blob) {" +
        "  var link = document.createElement('a');" +
        "  link.href = URL.createObjectURL(blob);" +
        "  link.download = artifactId + '.zip';" +
        "  document.body.appendChild(link);" +
        "  link.click();" +
        "  document.body.removeChild(link);" +
        "  URL.revokeObjectURL(link.href);" +
        "});"
    )
    private static native void generateJavaProject(
            String groupId, String artifactId, String packageName,
            String javaVersion, String teavmReactVersion);

    @JSBody(params = {"groupId", "artifactId", "packageName", "javaVersion", "kotlinVersion", "teavmReactVersion"},
            script =
        "var zip = new JSZip();" +
        "var pkgPath = packageName.replace(/\\./g, '/');" +
        "var mainClass = packageName + '.AppKt';" +

        // pom.xml — Kotlin variant
        "var pom = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n'" +
        " + '<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\\n'" +
        " + '         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\\n'" +
        " + '         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\\n'" +
        " + '           http://maven.apache.org/xsd/maven-4.0.0.xsd\">\\n'" +
        " + '    <modelVersion>4.0.0</modelVersion>\\n\\n'" +
        " + '    <groupId>' + groupId + '</groupId>\\n'" +
        " + '    <artifactId>' + artifactId + '</artifactId>\\n'" +
        " + '    <version>1.0.0-SNAPSHOT</version>\\n\\n'" +
        " + '    <properties>\\n'" +
        " + '        <maven.compiler.source>' + javaVersion + '</maven.compiler.source>\\n'" +
        " + '        <maven.compiler.target>' + javaVersion + '</maven.compiler.target>\\n'" +
        " + '        <kotlin.version>' + kotlinVersion + '</kotlin.version>\\n'" +
        " + '        <teavm.version>0.13.1</teavm.version>\\n'" +
        " + '        <teavm-react.version>' + teavmReactVersion + '</teavm-react.version>\\n'" +
        " + '    </properties>\\n\\n'" +
        " + '    <dependencies>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>ca.weblite</groupId>\\n'" +
        " + '            <artifactId>teavm-react-core</artifactId>\\n'" +
        " + '            <version>${teavm-react.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>ca.weblite</groupId>\\n'" +
        " + '            <artifactId>teavm-react-kotlin</artifactId>\\n'" +
        " + '            <version>${teavm-react.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.jetbrains.kotlin</groupId>\\n'" +
        " + '            <artifactId>kotlin-stdlib</artifactId>\\n'" +
        " + '            <version>${kotlin.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.teavm</groupId>\\n'" +
        " + '            <artifactId>teavm-classlib</artifactId>\\n'" +
        " + '            <version>${teavm.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.teavm</groupId>\\n'" +
        " + '            <artifactId>teavm-jso</artifactId>\\n'" +
        " + '            <version>${teavm.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '        <dependency>\\n'" +
        " + '            <groupId>org.teavm</groupId>\\n'" +
        " + '            <artifactId>teavm-jso-apis</artifactId>\\n'" +
        " + '            <version>${teavm.version}</version>\\n'" +
        " + '        </dependency>\\n'" +
        " + '    </dependencies>\\n\\n'" +
        " + '    <build>\\n'" +
        " + '        <sourceDirectory>src/main/kotlin</sourceDirectory>\\n'" +
        " + '        <plugins>\\n'" +
        " + '            <plugin>\\n'" +
        " + '                <groupId>org.jetbrains.kotlin</groupId>\\n'" +
        " + '                <artifactId>kotlin-maven-plugin</artifactId>\\n'" +
        " + '                <version>${kotlin.version}</version>\\n'" +
        " + '                <executions>\\n'" +
        " + '                    <execution>\\n'" +
        " + '                        <id>compile</id>\\n'" +
        " + '                        <goals>\\n'" +
        " + '                            <goal>compile</goal>\\n'" +
        " + '                        </goals>\\n'" +
        " + '                    </execution>\\n'" +
        " + '                </executions>\\n'" +
        " + '            </plugin>\\n'" +
        " + '            <plugin>\\n'" +
        " + '                <groupId>org.teavm</groupId>\\n'" +
        " + '                <artifactId>teavm-maven-plugin</artifactId>\\n'" +
        " + '                <version>${teavm.version}</version>\\n'" +
        " + '                <executions>\\n'" +
        " + '                    <execution>\\n'" +
        " + '                        <goals>\\n'" +
        " + '                            <goal>compile</goal>\\n'" +
        " + '                        </goals>\\n'" +
        " + '                        <phase>process-classes</phase>\\n'" +
        " + '                        <configuration>\\n'" +
        " + '                            <mainClass>' + mainClass + '</mainClass>\\n'" +
        " + '                            <targetType>JAVASCRIPT</targetType>\\n'" +
        " + '                            <minifying>false</minifying>\\n'" +
        " + '                            <targetDirectory>\\n'" +
        " + '                                ${project.build.directory}/webapp/js\\n'" +
        " + '                            </targetDirectory>\\n'" +
        " + '                        </configuration>\\n'" +
        " + '                    </execution>\\n'" +
        " + '                </executions>\\n'" +
        " + '            </plugin>\\n'" +
        " + '            <plugin>\\n'" +
        " + '                <groupId>org.apache.maven.plugins</groupId>\\n'" +
        " + '                <artifactId>maven-resources-plugin</artifactId>\\n'" +
        " + '                <version>3.3.1</version>\\n'" +
        " + '                <executions>\\n'" +
        " + '                    <execution>\\n'" +
        " + '                        <id>copy-webapp</id>\\n'" +
        " + '                        <phase>process-classes</phase>\\n'" +
        " + '                        <goals><goal>copy-resources</goal></goals>\\n'" +
        " + '                        <configuration>\\n'" +
        " + '                            <outputDirectory>${project.build.directory}/webapp</outputDirectory>\\n'" +
        " + '                            <resources>\\n'" +
        " + '                                <resource>\\n'" +
        " + '                                    <directory>src/main/webapp</directory>\\n'" +
        " + '                                </resource>\\n'" +
        " + '                            </resources>\\n'" +
        " + '                        </configuration>\\n'" +
        " + '                    </execution>\\n'" +
        " + '                </executions>\\n'" +
        " + '            </plugin>\\n'" +
        " + '        </plugins>\\n'" +
        " + '    </build>\\n'" +
        " + '</project>\\n';" +

        // App.kt
        "var appKt = 'package ' + packageName + '\\n\\n'" +
        " + 'import ca.weblite.teavmreact.core.ReactDOM\\n'" +
        " + 'import ca.weblite.teavmreact.kotlin.*\\n'" +
        " + 'import org.teavm.jso.dom.html.HTMLDocument\\n\\n'" +
        " + 'fun main() {\\n'" +
        " + '    val App = fc(\"App\") {\\n'" +
        " + '        div {\\n'" +
        " + '            h1 { +\"Hello, teavm-react!\" }\\n'" +
        " + '            p { +\"Your app is running. Edit App.kt and rebuild.\" }\\n'" +
        " + '        }\\n'" +
        " + '    }\\n\\n'" +
        " + '    val root = ReactDOM.createRoot(\\n'" +
        " + '        HTMLDocument.current().getElementById(\"root\")\\n'" +
        " + '    )\\n'" +
        " + '    root.render(component(App))\\n'" +
        " + '}\\n';" +

        // index.html
        "var indexHtml = '<!DOCTYPE html>\\n'" +
        " + '<html lang=\"en\">\\n'" +
        " + '<head>\\n'" +
        " + '    <meta charset=\"UTF-8\">\\n'" +
        " + '    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\\n'" +
        " + '    <title>' + artifactId + '</title>\\n'" +
        " + '    <link rel=\"stylesheet\" href=\"css/styles.css\">\\n'" +
        " + '</head>\\n'" +
        " + '<body>\\n'" +
        " + '    <div id=\"root\"></div>\\n\\n'" +
        " + '    <!-- React 18 from CDN -->\\n'" +
        " + '    <script crossorigin\\n'" +
        " + '      src=\"https://unpkg.com/react@18/umd/react.development.js\">\\n'" +
        " + '    </script>\\n'" +
        " + '    <script crossorigin\\n'" +
        " + '      src=\"https://unpkg.com/react-dom@18/umd/react-dom.development.js\">\\n'" +
        " + '    </script>\\n\\n'" +
        " + '    <!-- TeaVM compiled output -->\\n'" +
        " + '    <script src=\"js/classes.js\"></script>\\n'" +
        " + '    <script>\\n'" +
        " + '        if (typeof main === \"function\") {\\n'" +
        " + '            main([]);\\n'" +
        " + '        }\\n'" +
        " + '    </script>\\n'" +
        " + '</body>\\n'" +
        " + '</html>\\n';" +

        // styles.css
        "var css = '/* Add your styles here */\\n\\n'" +
        " + '#root {\\n'" +
        " + '    max-width: 800px;\\n'" +
        " + '    margin: 0 auto;\\n'" +
        " + '    padding: 2rem;\\n'" +
        " + '    font-family: -apple-system, BlinkMacSystemFont, sans-serif;\\n'" +
        " + '}\\n';" +

        // run.sh
        "var runSh = '#!/bin/bash\\n'" +
        " + 'PORT=${1:-8080}\\n'" +
        " + 'mvn clean process-classes -q\\n'" +
        " + 'echo \"Serving at http://localhost:$PORT\"\\n'" +
        " + 'python3 -m http.server $PORT --directory target/webapp\\n';" +

        // Add files to zip
        "var root = artifactId + '/';" +
        "zip.file(root + 'pom.xml', pom);" +
        "zip.file(root + 'run.sh', runSh, {unixPermissions: '755'});" +
        "zip.file(root + 'src/main/kotlin/' + pkgPath + '/App.kt', appKt);" +
        "zip.file(root + 'src/main/webapp/index.html', indexHtml);" +
        "zip.file(root + 'src/main/webapp/css/styles.css', css);" +

        // Generate and trigger download
        "zip.generateAsync({type: 'blob', platform: 'UNIX'}).then(function(blob) {" +
        "  var link = document.createElement('a');" +
        "  link.href = URL.createObjectURL(blob);" +
        "  link.download = artifactId + '.zip';" +
        "  document.body.appendChild(link);" +
        "  link.click();" +
        "  document.body.removeChild(link);" +
        "  URL.revokeObjectURL(link.href);" +
        "});"
    )
    private static native void generateKotlinProject(
            String groupId, String artifactId, String packageName,
            String javaVersion, String kotlinVersion, String teavmReactVersion);

    // -- Component factory -----------------------------------------------------

    public static ReactElement create() {
        return component(ProjectInitializer::render, "ProjectInitializer");
    }

    // -- Configuration ---------------------------------------------------------

    private static final String TEAVM_REACT_VERSION = "0.1.5";

    // -- Render ----------------------------------------------------------------

    private static ReactElement render(JSObject props) {
        var groupId = Hooks.useState("com.example");
        var artifactId = Hooks.useState("my-teavm-react-app");
        var packageName = Hooks.useState("com.example");
        var javaVersion = Hooks.useState("21");
        var language = Hooks.useState("java");

        boolean isJava = language.getString().equals("java");

        return Div.create().className("project-initializer")

            // Header
            .child(Div.create().className("pi-header")
                .child(H2.create().text("Create a new project"))
                .child(P.create().text(
                    "Configure your project below and click Generate to download "
                    + "a ready-to-run teavm-react starter project.")))

            // Language toggle
            .child(Div.create().className("pi-language-toggle")
                .child(Button.create()
                    .className("pi-lang-btn" + (isJava ? " active" : ""))
                    .text("Java")
                    .onClick(e -> language.setString("java"))
                    .build())
                .child(Button.create()
                    .className("pi-lang-btn" + (!isJava ? " active" : ""))
                    .text("Kotlin")
                    .onClick(e -> language.setString("kotlin"))
                    .build()))

            // Form grid
            .child(Div.create().className("pi-form")

                // Group
                .child(Div.create().className("pi-field")
                    .child(Label.create().text("Group").prop("htmlFor", "pi-group"))
                    .child(Input.text()
                        .id("pi-group")
                        .value(groupId.getString())
                        .placeholder("com.example")
                        .onChange(e -> groupId.setString(e.getTarget().getValue()))))

                // Artifact
                .child(Div.create().className("pi-field")
                    .child(Label.create().text("Artifact").prop("htmlFor", "pi-artifact"))
                    .child(Input.text()
                        .id("pi-artifact")
                        .value(artifactId.getString())
                        .placeholder("my-teavm-react-app")
                        .onChange(e -> artifactId.setString(e.getTarget().getValue()))))

                // Package Name
                .child(Div.create().className("pi-field")
                    .child(Label.create().text("Package Name").prop("htmlFor", "pi-package"))
                    .child(Input.text()
                        .id("pi-package")
                        .value(packageName.getString())
                        .placeholder("com.example")
                        .onChange(e -> packageName.setString(e.getTarget().getValue()))))

                // Java Version
                .child(Div.create().className("pi-field")
                    .child(Label.create().text("Java Version").prop("htmlFor", "pi-java"))
                    .child(Select.create()
                        .id("pi-java")
                        .value(javaVersion.getString())
                        .onChange(e -> javaVersion.setString(e.getTarget().getValue()))
                        .child(option("21", "21"))
                        .child(option("17", "17")))))

            // Generate button
            .child(Div.create().className("pi-actions")
                .child(Button.create()
                    .className("btn btn-primary pi-generate-btn")
                    .text("Generate Project")
                    .onClick(e -> {
                        if (isJava) {
                            generateJavaProject(
                                groupId.getString(),
                                artifactId.getString(),
                                packageName.getString(),
                                javaVersion.getString(),
                                TEAVM_REACT_VERSION);
                        } else {
                            generateKotlinProject(
                                groupId.getString(),
                                artifactId.getString(),
                                packageName.getString(),
                                javaVersion.getString(),
                                "1.9.25",
                                TEAVM_REACT_VERSION);
                        }
                    })))

            // Summary line
            .child(Div.create().className("pi-summary")
                .child(P.create().text(
                    artifactId.getString() + ".zip  |  "
                    + (isJava ? "Java" : "Kotlin") + "  |  "
                    + packageName.getString() + "  |  Java " + javaVersion.getString())))

            .build();
    }

    // -- Helpers ---------------------------------------------------------------

    private static ReactElement option(String value, String label) {
        JSObject props = React.createObject();
        React.setProperty(props, "value", value);
        return React.createElementWithText("option", props, label);
    }
}

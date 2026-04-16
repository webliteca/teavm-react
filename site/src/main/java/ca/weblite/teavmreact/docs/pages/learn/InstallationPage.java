package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.ProjectInitializer;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class InstallationPage {

    public static ReactElement render(JSObject props) {
        return El.div("doc-page",

            h1("Installation"),
            p("The fastest way to get started is to generate a starter project. "
              + "Or follow the manual setup instructions below."),
            hr(),
            quickStartSection(),
            hr(),
            archetypeSection(),
            hr(),
            h2("Manual Setup"),
            p("If you prefer to set things up yourself, follow the steps below."),
            prerequisitesSection(),
            hr(),
            mavenSetupSection(),
            hr(),
            projectStructureSection(),
            hr(),
            htmlShellSection(),
            hr(),
            entryPointSection(),
            hr(),
            buildingSection(),
            hr(),
            runningLocallySection(),
            hr(),
            skillsSection()
        );
    }

    // -----------------------------------------------------------------------
    // 0. Quick Start — Project Generator
    // -----------------------------------------------------------------------

    private static ReactElement quickStartSection() {
        return El.section("doc-section",

            h2("Quick Start"),
            p("Enter your project details below, then click Generate to download a "
              + "ready-to-run Maven project. Unzip it, open it in your IDE, and run:"),
            CodeBlock.create("mvn clean process-classes\ncd target/webapp\npython3 -m http.server 8080", "bash"),
            ProjectInitializer.create(),
            Callout.note("Prerequisites",
                p("You will need JDK 21 or later, Maven 3.8+, and a modern web browser. "
                  + "See the Prerequisites section below for details."))
        );
    }

    // -----------------------------------------------------------------------
    // 0b. Maven Archetype
    // -----------------------------------------------------------------------

    private static ReactElement archetypeSection() {
        String javaArchetypeCommand = """
                mvn archetype:generate \\
                  -DarchetypeGroupId=ca.weblite \\
                  -DarchetypeArtifactId=teavm-react-archetype \\
                  -DarchetypeVersion=0.1.4 \\
                  -DgroupId=com.example \\
                  -DartifactId=my-teavm-react-app \\
                  -DjavaVersion=21 \\
                  -DinteractiveMode=false""";

        String kotlinArchetypeCommand = """
                mvn archetype:generate \\
                  -DarchetypeGroupId=ca.weblite \\
                  -DarchetypeArtifactId=teavm-react-kotlin-archetype \\
                  -DarchetypeVersion=0.1.4 \\
                  -DgroupId=com.example \\
                  -DartifactId=my-teavm-react-app \\
                  -DjavaVersion=21 \\
                  -DkotlinVersion=1.9.25 \\
                  -DinteractiveMode=false""";

        String runCommands = """
                cd my-teavm-react-app
                chmod +x run.sh
                ./run.sh""";

        return El.section("doc-section",

            h2("Maven Archetype"),
            p("You can also create a new project from the command line using the "
              + "Maven archetype. Both Java and Kotlin archetypes are available. "
              + "This is useful for scripted setups or if you "
              + "prefer not to use the web generator."),
            CodeTabs.create(javaArchetypeCommand, kotlinArchetypeCommand),
            p("This generates a complete project with the same structure as the "
              + "web generator above. To build and run it:"),
            CodeBlock.create(runCommands, "bash"),
            Callout.note("Customizable Properties",
                ul(
                    li("groupId — your project's Maven group ID (e.g. com.example)"),
                    li("artifactId — the project name and directory (e.g. my-teavm-react-app)"),
                    li("javaVersion — Java source/target version (default: 21, also supports 17)"),
                    li("kotlinVersion — Kotlin version (Kotlin archetype only, default: 1.9.25)")
                )),
            Callout.note("Interactive Mode",
                p("Omit -DinteractiveMode=false to be prompted for each property interactively."))
        );
    }

    // -----------------------------------------------------------------------
    // 1. Prerequisites
    // -----------------------------------------------------------------------

    private static ReactElement prerequisitesSection() {
        return El.section("doc-section",

            h2("Prerequisites"),
            p("Before you begin, make sure you have the following installed:"),
            ul(
                li("JDK 21 or later"),
                li("Maven 3.8 or later"),
                li("A modern web browser (Chrome, Firefox, Safari, or Edge)")
            ),
            Callout.note("JDK Version",
                p("teavm-react requires JDK 21 for compilation. The TeaVM compiler "
                  + "produces JavaScript that runs in any modern browser, regardless of "
                  + "JDK version."))
        );
    }

    // -----------------------------------------------------------------------
    // 2. Maven Setup
    // -----------------------------------------------------------------------

    private static ReactElement mavenSetupSection() {
        String pomXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                           http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>

                    <groupId>com.example</groupId>
                    <artifactId>my-teavm-react-app</artifactId>
                    <version>1.0.0-SNAPSHOT</version>

                    <properties>
                        <maven.compiler.source>17</maven.compiler.source>
                        <maven.compiler.target>17</maven.compiler.target>
                        <teavm.version>0.13.1</teavm.version>
                        <teavm-react.version>0.1.4</teavm-react.version>
                    </properties>

                    <dependencies>
                        <dependency>
                            <groupId>ca.weblite</groupId>
                            <artifactId>teavm-react-core</artifactId>
                            <version>${teavm-react.version}</version>
                        </dependency>
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
                        <plugins>
                            <plugin>
                                <groupId>org.teavm</groupId>
                                <artifactId>teavm-maven-plugin</artifactId>
                                <version>${teavm.version}</version>
                                <executions>
                                    <execution>
                                        <goals>
                                            <goal>compile</goal>
                                        </goals>
                                        <phase>process-classes</phase>
                                        <configuration>
                                            <mainClass>com.example.App</mainClass>
                                            <targetType>JAVASCRIPT</targetType>
                                            <minifying>false</minifying>
                                            <targetDirectory>
                                                ${project.build.directory}/webapp/js
                                            </targetDirectory>
                                        </configuration>
                                    </execution>
                                </executions>
                            </plugin>
                        </plugins>
                    </build>
                </project>""";

        return El.section("doc-section",

            h2("Maven Setup"),
            p("Create a new Maven project and configure your pom.xml with the teavm-react "
              + "dependency and the TeaVM compiler plugin:"),
            CodeBlock.create(pomXml, "xml"),
            Callout.note("Kotlin Projects",
                p("For Kotlin support, add the teavm-react-kotlin artifact and the "
                  + "kotlin-maven-plugin. See the Kotlin DSL page for full setup details."))
        );
    }

    // -----------------------------------------------------------------------
    // 3. Project Structure
    // -----------------------------------------------------------------------

    private static ReactElement projectStructureSection() {
        String structure = """
                my-teavm-react-app/
                  pom.xml
                  src/
                    main/
                      java/
                        com/example/
                          App.java          # Entry point
                          components/       # Your React components
                            Header.java
                            Counter.java
                      webapp/
                        index.html          # HTML shell
                        css/
                          styles.css        # Your styles
                  target/
                    webapp/
                      js/
                        classes.js          # TeaVM output (generated)
                      index.html            # Copied from src/main/webapp""";

        return El.section("doc-section",

            h2("Project Structure"),
            p("A typical teavm-react project follows standard Maven layout with a "
              + "webapp directory for static assets:"),
            CodeBlock.create(structure, "bash")
        );
    }

    // -----------------------------------------------------------------------
    // 4. HTML Shell
    // -----------------------------------------------------------------------

    private static ReactElement htmlShellSection() {
        String indexHtml = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">
                    <title>My teavm-react App</title>
                    <link rel="stylesheet" href="css/styles.css">
                </head>
                <body>
                    <div id="root"></div>

                    <!-- React 18 from CDN -->
                    <script crossorigin
                      src="https://unpkg.com/react@18/umd/react.development.js">
                    </script>
                    <script crossorigin
                      src="https://unpkg.com/react-dom@18/umd/react-dom.development.js">
                    </script>

                    <!-- TeaVM compiled output -->
                    <script src="js/classes.js"></script>
                </body>
                </html>""";

        return El.section("doc-section",

            h2("HTML Shell"),
            p("Create src/main/webapp/index.html with the React 18 CDN scripts and "
              + "a root div for your application:"),
            CodeBlock.create(indexHtml, "markup"),
            Callout.pitfall("Script Order Matters",
                p("React and ReactDOM must be loaded before classes.js. The TeaVM output "
                  + "expects React to be available as a global variable."))
        );
    }

    // -----------------------------------------------------------------------
    // 5. Entry Point
    // -----------------------------------------------------------------------

    private static ReactElement entryPointSection() {
        String javaCode = """
                import ca.weblite.teavmreact.core.ReactDOM;
                import ca.weblite.teavmreact.core.ReactElement;
                import org.teavm.jso.JSObject;

                import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

                public class App {
                    public static void main(String[] args) {
                        ReactDOM.renderToId("root",
                            component(App::render, "App")
                        );
                    }

                    static ReactElement render(JSObject props) {
                        return div(
                            h1("Hello, teavm-react!"),
                            p("Your app is running.")
                        );
                    }
                }""";

        String kotlinCode = """
                import ca.weblite.teavmreact.core.ReactDOM
                import ca.weblite.teavmreact.kotlin.*

                fun main() {
                    val App = fc("App") {
                        div {
                            h1 { +"Hello, teavm-react!" }
                            p { +"Your app is running." }
                        }
                    }
                    ReactDOM.renderToId("root", App)
                }""";

        return El.section("doc-section",

            h2("Entry Point"),
            p("Create your main application class. The main method calls "
              + "ReactDOM.renderToId() to mount your root component into the DOM:"),
            CodeTabs.create(javaCode, kotlinCode)
        );
    }

    // -----------------------------------------------------------------------
    // 6. Building
    // -----------------------------------------------------------------------

    private static ReactElement buildingSection() {
        String buildCommand = """
                mvn process-classes""";

        String fullBuildCommand = """
                # Full build (compile + generate JS)
                mvn process-classes

                # Clean build
                mvn clean process-classes

                # Production build with minification
                # (set <minifying>true</minifying> in pom.xml)
                mvn clean process-classes""";

        return El.section("doc-section",

            h2("Building"),
            p("Run the Maven process-classes phase to compile your Java source into "
              + "JavaScript via TeaVM:"),
            CodeBlock.create(buildCommand, "bash"),
            p("This compiles your Java code to class files, then runs the TeaVM "
              + "compiler to produce target/webapp/js/classes.js."),
            CodeBlock.create(fullBuildCommand, "bash"),
            Callout.note("Incremental Builds",
                p("The TeaVM Maven plugin supports incremental compilation via "
                  + "the <incremental>true</incremental> option, which caches analysis "
                  + "in target/teavm-cache/ and only re-analyzes changed bytecode. "
                  + "The dev server (see below) activates this automatically through "
                  + "the 'dev' Maven profile."))
        );
    }

    // -----------------------------------------------------------------------
    // 7. Running Locally
    // -----------------------------------------------------------------------

    private static ReactElement runningLocallySection() {
        String pythonServer = """
                # Serve from the target/webapp directory
                cd target/webapp
                python3 -m http.server 8080

                # Then open http://localhost:8080 in your browser""";

        String runScript = """
                # Or use the run.sh script if provided:
                ./run.sh 8080""";

        String devScript = """
                # Start the dev server with live reload:
                ./dev.sh 8080""";

        return El.section("doc-section",

            h2("Running Locally"),
            p("After building, serve the target/webapp directory with any static file "
              + "server. The simplest option is Python's built-in HTTP server:"),
            CodeBlock.create(pythonServer, "bash"),
            p("If your project includes a run.sh script:"),
            CodeBlock.create(runScript, "bash"),
            p("Open your browser to http://localhost:8080 and you should see your "
              + "application running."),

            h3("Dev Server with Hot Reload"),
            p("teavm-react includes a live-reload dev server. Run dev.sh to start "
              + "it:"),
            CodeBlock.create(devScript, "bash"),
            p("The dev server watches all src/ directories for changes to .java, "
              + ".kt, .html, and .css files. When a change is detected, it "
              + "automatically recompiles the affected modules and refreshes the "
              + "browser via Server-Sent Events."),
            Callout.note("Dev Server Features",
                ul(
                    li("Smart rebuild — only recompiles modules whose source changed"),
                    li("Fast path — HTML/CSS-only changes are copied directly without Maven"),
                    li("Incremental TeaVM — activates the 'dev' profile for cached, "
                      + "incremental compilation"),
                    li("Maven Daemon — auto-detects mvnd for faster JVM startup"),
                    li("Compiling overlay — shows a visual indicator in the browser "
                      + "while recompiling")
                ))
        );
    }

    // -----------------------------------------------------------------------
    // 8. AI Skills
    // -----------------------------------------------------------------------

    private static ReactElement skillsSection() {
        String installCommand = """
                mvn ca.weblite:skills-jar-plugin:install""";

        String listCommand = """
                mvn ca.weblite:skills-jar-plugin:list""";

        String pomPlugin = """
                <build>
                    <plugins>
                        <plugin>
                            <groupId>ca.weblite</groupId>
                            <artifactId>skills-jar-plugin</artifactId>
                            <version>0.1.2</version>
                        </plugin>
                    </plugins>
                </build>""";

        return El.section("doc-section",

            h2("AI Skills"),
            p("teavm-react publishes AI assistant skills that provide your IDE's AI "
              + "tools (such as Claude Code or Cursor) with library-specific guidance, "
              + "API signatures, and usage patterns. Skills are distributed as Maven "
              + "artifacts alongside the library JARs."),

            h3("Installing Skills"),
            p("Run the following command in your project directory to install skills "
              + "for all dependencies that publish them:"),
            CodeBlock.create(installCommand, "bash"),
            p("This resolves -skills.jar artifacts for each dependency in your project "
              + "and extracts them into .claude/skills/ in your project root. The "
              + "installed skills are automatically picked up by Claude Code and other "
              + "compatible AI tools."),

            h3("Listing Available Skills"),
            p("To see which of your dependencies provide skills without installing them:"),
            CodeBlock.create(listCommand, "bash"),

            h3("Adding the Plugin to Your POM"),
            p("For convenience, you can add the skills-jar-plugin to your pom.xml so "
              + "that skill installation is available as a standard Maven goal:"),
            CodeBlock.create(pomPlugin, "xml"),

            Callout.note("What Gets Installed",
                ul(
                    li("SKILL.md — the main skill file with API guidance and rules"),
                    li("references/ — detailed API signatures and usage patterns"),
                    li("assets/examples/ — complete working examples you can reference")
                )),
            Callout.note("Version Tracking",
                p("The plugin maintains a .skill-manifest.json file to track installed "
                  + "skills. Re-running the install command only updates skills whose "
                  + "upstream version has changed."))
        );
    }
}

package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.docs.components.Callout;
import ca.weblite.teavmreact.docs.components.CodeBlock;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class AISkillsPage {

    public static ReactElement render(JSObject props) {
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

        return El.div("doc-page",

            h1("AI Skills"),
            p("teavm-react publishes AI assistant skills that provide your IDE's AI "
              + "tools (such as Claude Code or Cursor) with library-specific guidance, "
              + "API signatures, and usage patterns. Skills are distributed as Maven "
              + "artifacts alongside the library JARs."),

            hr(),

            El.section("doc-section",
                h2("Installing Skills"),
                p("Run the following command in your project directory to install skills "
                  + "for all dependencies that publish them:"),
                CodeBlock.create(installCommand, "bash"),
                p("This resolves -skills.jar artifacts for each dependency in your project "
                  + "and extracts them into .claude/skills/ in your project root. The "
                  + "installed skills are automatically picked up by Claude Code and other "
                  + "compatible AI tools.")),

            hr(),

            El.section("doc-section",
                h2("Listing Available Skills"),
                p("To see which of your dependencies provide skills without installing them:"),
                CodeBlock.create(listCommand, "bash")),

            hr(),

            El.section("doc-section",
                h2("Adding the Plugin to Your POM"),
                p("For convenience, you can add the skills-jar-plugin to your pom.xml so "
                  + "that skill installation is available as a standard Maven goal:"),
                CodeBlock.create(pomPlugin, "xml")),

            hr(),

            El.section("doc-section",
                Callout.note("What Gets Installed",
                    ul(
                        li("SKILL.md — the main skill file with API guidance and rules"),
                        li("references/ — detailed API signatures and usage patterns"),
                        li("assets/examples/ — complete working examples you can reference")
                    )),
                Callout.note("Version Tracking",
                    p("The plugin maintains a .skill-manifest.json file to track installed "
                      + "skills. Re-running the install command only updates skills whose "
                      + "upstream version has changed.")))
        );
    }
}

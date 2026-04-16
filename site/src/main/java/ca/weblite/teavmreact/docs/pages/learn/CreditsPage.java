package ca.weblite.teavmreact.docs.pages.learn;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.html.DomBuilder.*;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class CreditsPage {

    public static ReactElement render(JSObject props) {
        return El.div("doc-page credits-page",

            h1("Credits"),
            p("teavm-react stands on the shoulders of incredible open-source projects. "
              + "We are deeply grateful to the people and communities that make this work possible."),

            hr(),

            // TeaVM
            El.section("doc-section",
                h2("TeaVM"),
                p("teavm-react would not exist without the fantastic work of "
                  + "Alexey Andreev on TeaVM, the ahead-of-time compiler that "
                  + "translates JVM bytecode to JavaScript."),
                p("TeaVM is the foundation that makes it possible to write "
                  + "Java and Kotlin code and run it in the browser. "
                  + "Its quality, performance, and thoughtful design are what "
                  + "make this entire project viable."),
                Div.create().className("credits-card")
                    .child(h3("Alexey Andreev"))
                    .child(P.create()
                        .text("Creator and maintainer of TeaVM"))
                    .child(Div.create().className("credits-links")
                        .child(a("GitHub")
                            .className("btn btn-secondary credits-link")
                            .href("https://github.com/konsoletyper")
                            .prop("target", "_blank")
                            .prop("rel", "noopener noreferrer")
                            .build())
                        .child(a("TeaVM")
                            .className("btn btn-secondary credits-link")
                            .href("https://teavm.org")
                            .prop("target", "_blank")
                            .prop("rel", "noopener noreferrer")
                            .build())
                        .build())
                    .build()),

            hr(),

            // React
            El.section("doc-section",
                h2("React"),
                p("teavm-react provides type-safe bindings to React 18, the "
                  + "widely-used JavaScript library for building user interfaces "
                  + "created and maintained by Meta."),
                Div.create().className("credits-card")
                    .child(h3("React"))
                    .child(P.create()
                        .text("A JavaScript library for building user interfaces"))
                    .child(Div.create().className("credits-links")
                        .child(a("react.dev")
                            .className("btn btn-secondary credits-link")
                            .href("https://react.dev")
                            .prop("target", "_blank")
                            .prop("rel", "noopener noreferrer")
                            .build())
                        .build())
                    .build()),

            hr(),

            // teavm-react
            El.section("doc-section",
                h2("teavm-react"),
                p("teavm-react was created by Steve Hannah."),
                Div.create().className("credits-card")
                    .child(h3("Steve Hannah"))
                    .child(P.create()
                        .text("Creator of teavm-react"))
                    .child(Div.create().className("credits-links")
                        .child(a("GitHub")
                            .className("btn btn-secondary credits-link")
                            .href("https://github.com/shannah")
                            .prop("target", "_blank")
                            .prop("rel", "noopener noreferrer")
                            .build())
                        .build())
                    .build()),

            hr(),

            // Support section
            El.section("doc-section",
                h2("Support This Project"),
                p("If you find teavm-react useful, the best way to support it is "
                  + "to support TeaVM. Without TeaVM, none of this would be possible."),
                Div.create().className("credits-sponsor")
                    .child(P.create()
                        .text("Please consider sponsoring Alexey Andreev's work on TeaVM:"))
                    .child(a("Sponsor TeaVM on GitHub")
                        .className("btn btn-primary credits-sponsor-btn")
                        .href("https://github.com/sponsors/konsoletyper")
                        .prop("target", "_blank")
                        .prop("rel", "noopener noreferrer")
                        .build())
                    .build())
        );
    }
}

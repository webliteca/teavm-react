package ca.weblite.teavmreact.docs.pages;

import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.docs.components.CodeTabs;
import ca.weblite.teavmreact.docs.components.FeatureCard;
import ca.weblite.teavmreact.docs.components.LiveDemo;
import ca.weblite.teavmreact.html.DomBuilder.Div;
import ca.weblite.teavmreact.html.DomBuilder.Section;
import org.teavm.jso.JSObject;

import static ca.weblite.teavmreact.html.Html.*;
import ca.weblite.teavmreact.docs.El;

public class HomePage {

    public static ReactElement render(JSObject props) {
        return Div.create().className("home-page")
            .child(heroSection())
            .child(sponsorBanner())
            .child(featureGrid())
            .child(codePreviewSection())
            .build();
    }

    private static ReactElement heroSection() {
        return Section.create().className("hero")
            .child(h1("teavm-react"))
            .child(p("Build React 18 apps in Java and Kotlin, compiled to JavaScript via TeaVM"))
            .child(Div.create().className("hero-buttons")
                .child(a("Get Started")
                    .className("hero-btn hero-btn-primary")
                    .href("#/learn/quick-start")
                    .build())
                .child(a("API Reference")
                    .className("hero-btn hero-btn-secondary")
                    .href("#/reference/hooks-overview")
                    .build())
                .build())
            .build();
    }

    private static ReactElement sponsorBanner() {
        return Div.create().className("sponsor-banner")
            .child(p("teavm-react is powered by TeaVM. If you use this project, please consider becoming a sponsor."))
            .child(a("Sponsor TeaVM")
                .className("btn btn-primary sponsor-btn")
                .href("https://github.com/sponsors/konsoletyper")
                .prop("target", "_blank")
                .prop("rel", "noopener noreferrer")
                .build())
            .build();
    }

    private static ReactElement featureGrid() {
        return Section.create().className("features-section")
            .child(h2("Why teavm-react?"))
            .child(Div.create().className("feature-grid")
                .child(FeatureCard.create(
                    "Type-Safe Components",
                    "Full IDE support with autocomplete, refactoring, and compile-time type checks. "
                    + "Catch errors before they reach the browser."
                ))
                .child(FeatureCard.create(
                    "React 18 Hooks",
                    "useState, useEffect, useRef, useContext, useMemo, useCallback, and more. "
                    + "All the hooks you know, with Java type safety."
                ))
                .child(FeatureCard.create(
                    "Three Component Styles",
                    "Choose the approach that fits your team: functional components, "
                    + "a builder DSL, or class-based components extending ReactView."
                ))
                .child(FeatureCard.create(
                    "Kotlin DSL",
                    "Idiomatic Kotlin with delegated state properties, coroutine integration, "
                    + "and a lambda-with-receiver DSL for building element trees."
                ))
                .build())
            .build();
    }

    private static ReactElement codePreviewSection() {
        String javaCode = """
                static ReactElement counterDemo(JSObject props) {
                    var count = Hooks.useState(0);
                    return div(
                        h1("Count: " + count.getInt()),
                        button("Increment")
                            .onClick(e -> count.updateInt(n -> n + 1))
                            .build()
                    );
                }""";

        String kotlinCode = """
                val Counter = fc("Counter") {
                    var count by state(0)
                    div {
                        h1 { +"Count: $count" }
                        button {
                            +"Increment"
                            onClick { count++ }
                        }
                    }
                }""";

        return Section.create().className("code-preview-section")
            .child(h2("A taste of teavm-react"))
            .child(p("A simple counter in just a few lines of code:"))
            .child(CodeTabs.create(javaCode, kotlinCode))
            .child(h3("Try it live"))
            .child(LiveDemo.create(HomePage::counterDemo))
            .build();
    }

    private static ReactElement counterDemo(JSObject props) {
        var count = Hooks.useState(0);
        return Div.create().className("counter-demo")
            .child(h2("Count: " + count.getInt()))
            .child(button("Increment")
                .className("demo-btn")
                .onClick(e -> count.updateInt(n -> n + 1))
                .build())
            .build();
    }
}

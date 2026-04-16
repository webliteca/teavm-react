package ca.weblite.teavmreact.docs;

import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.docs.pages.HomePage;
import ca.weblite.teavmreact.docs.pages.learn.*;
import ca.weblite.teavmreact.docs.pages.reference.*;
import org.teavm.jso.dom.html.HTMLDocument;

public class App {

    private static final Route[] ROUTES = {
        // Homepage (full width, no sidebar)
        new Route("", HomePage::render, true),

        // Learn section
        new Route("learn/quick-start", QuickStartPage::render),
        new Route("learn/installation", InstallationPage::render),
        new Route("learn/thinking", ThinkingPage::render),
        new Route("learn/first-component", FirstComponentPage::render),
        new Route("learn/props", PropsPage::render),
        new Route("learn/conditional-rendering", ConditionalRenderingPage::render),
        new Route("learn/rendering-lists", RenderingListsPage::render),
        new Route("learn/events", EventsPage::render),
        new Route("learn/state", StatePage::render),
        new Route("learn/reducers", ReducersPage::render),
        new Route("learn/context", ContextPage::render),
        new Route("learn/refs", RefsPage::render),
        new Route("learn/effects", EffectsPage::render),
        new Route("learn/ai-skills", AISkillsPage::render),
        new Route("learn/credits", CreditsPage::render),

        // Reference section
        new Route("reference/hooks-overview", HooksOverviewPage::render),
        new Route("reference/use-state", UseStatePage::render),
        new Route("reference/use-effect", UseEffectPage::render),
        new Route("reference/use-ref", UseRefPage::render),
        new Route("reference/use-context", UseContextPage::render),
        new Route("reference/html-dsl", HtmlDslPage::render),
        new Route("reference/components", ComponentsPage::render),
        new Route("reference/events", EventsReferencePage::render),
    };

    public static void main(String[] args) {
        var root = ReactDOM.createRoot(HTMLDocument.current().getElementById("root"));
        root.render(Router.create(ROUTES));
    }
}

package com.example;

import ca.weblite.teavmreact.component.ReactView;
import ca.weblite.teavmreact.core.JsUtil;
import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.StateHandle;
import org.teavm.jso.dom.html.HTMLDocument;

import static ca.weblite.teavmreact.html.Html.*;

// Stopwatch using ReactView class-based component with lifecycle hooks.
public class App {

    // Class-based component: extends ReactView.
    // The class is re-instantiated on every render — state comes from hook
    // field initializers, which are called in consistent order each render.
    static class StopwatchView extends ReactView {

        // State hooks as field initializers — called during render.
        private final StateHandle<Integer> elapsed = Hooks.useState(0);
        private final StateHandle<Boolean> running = Hooks.useState(false);

        @Override
        protected ReactElement render() {
            // Use useEffect to manage the timer — it re-runs whenever
            // 'running' changes because we don't pass empty deps.
            Hooks.useEffect(() -> {
                if (!running.getBool()) return null;
                int id = JsUtil.setInterval(
                    () -> elapsed.updateInt(n -> n + 1),
                    1000
                );
                // Cleanup: clear interval when effect re-runs or unmounts
                return () -> JsUtil.clearInterval(id);
            });

            // Format elapsed seconds as mm:ss
            int secs = elapsed.getInt();
            String display = String.format("%02d:%02d", secs / 60, secs % 60);

            return div(
                h1("Stopwatch"),
                h2(display),
                // Start / Stop toggle
                button(running.getBool() ? "Stop" : "Start")
                    .onClick(e -> running.setBool(!running.getBool()))
                    .build(),
                // Reset — only enabled when stopped
                button("Reset")
                    .disabled(running.getBool())
                    .onClick(e -> elapsed.setInt(0))
                    .build()
            );
        }
    }

    public static void main(String[] args) {
        // ReactView.view() returns a ReactElement directly — render it.
        ReactElement stopwatch = ReactView.view(StopwatchView::new, "Stopwatch");
        var root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(stopwatch);
    }
}

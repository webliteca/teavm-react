package ca.weblite.teavmreact.demo;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test that verifies the TeaVM compilation produced valid JS output.
 * This test runs during the 'test' phase, AFTER the 'process-classes' phase
 * where the TeaVM plugin compiles Java to JavaScript.
 *
 * If the TeaVM compilation fails or produces no output, this test catches it.
 */
public class TeaVMCompilationTest {

    private static final String JS_OUTPUT = "target/webapp/js/classes.js";

    @Test
    void teavmProducesJsOutput() {
        File jsFile = new File(JS_OUTPUT);
        assertTrue(jsFile.exists(),
                "TeaVM should produce classes.js at " + jsFile.getAbsolutePath());
    }

    @Test
    void teavmOutputIsNonEmpty() {
        File jsFile = new File(JS_OUTPUT);
        if (jsFile.exists()) {
            assertTrue(jsFile.length() > 0,
                    "classes.js should not be empty");
        }
    }

    @Test
    void teavmOutputHasReasonableSize() {
        File jsFile = new File(JS_OUTPUT);
        if (jsFile.exists()) {
            // The compiled JS should be at least 10KB for a non-trivial app
            assertTrue(jsFile.length() > 10_000,
                    "classes.js should be at least 10KB, was " + jsFile.length() + " bytes");
        }
    }
}

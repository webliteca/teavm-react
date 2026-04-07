package ca.weblite.teavmreact;

import ca.weblite.teavmreact.core.*;
import ca.weblite.teavmreact.events.*;
import ca.weblite.teavmreact.hooks.*;
import ca.weblite.teavmreact.html.*;
import ca.weblite.teavmreact.component.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verify that the public API surface of teavm-react-core is intact.
 * These tests ensure key classes, interfaces, and methods exist and are
 * accessible — catching accidental renames, visibility changes, or deletions.
 */
public class ApiSurfaceTest {

    // ====================================================================
    // Core classes/interfaces exist
    // ====================================================================

    @Test
    void reactClassExists() {
        assertNotNull(React.class);
        assertTrue(Modifier.isFinal(React.class.getModifiers()));
    }

    @Test
    void reactElementInterfaceExists() {
        assertTrue(ReactElement.class.isInterface());
    }

    @Test
    void reactDomClassExists() {
        assertNotNull(ReactDOM.class);
    }

    @Test
    void reactRootInterfaceExists() {
        assertTrue(ReactRoot.class.isInterface());
    }

    @Test
    void reactContextClassExists() {
        assertNotNull(ReactContext.class);
    }

    @Test
    void renderFunctionInterfaceExists() {
        assertTrue(RenderFunction.class.isInterface());
    }

    @Test
    void voidCallbackInterfaceExists() {
        assertTrue(VoidCallback.class.isInterface());
    }

    @Test
    void jsUtilClassExists() {
        assertNotNull(JsUtil.class);
        assertTrue(Modifier.isFinal(JsUtil.class.getModifiers()));
    }

    // ====================================================================
    // Hooks API surface
    // ====================================================================

    @Test
    void hooksClassExists() {
        assertNotNull(Hooks.class);
        assertTrue(Modifier.isFinal(Hooks.class.getModifiers()));
    }

    @Test
    void hooksHasUseStateMethods() {
        Set<String> methods = getPublicStaticMethodNames(Hooks.class);
        assertTrue(methods.contains("useState"), "Hooks should have useState");
    }

    @Test
    void hooksHasUseEffectMethods() {
        Set<String> methods = getPublicStaticMethodNames(Hooks.class);
        assertTrue(methods.contains("useEffect"), "Hooks should have useEffect");
    }

    @Test
    void hooksHasUseRefMethods() {
        Set<String> methods = getPublicStaticMethodNames(Hooks.class);
        assertTrue(methods.contains("useRef"), "Hooks should have useRef");
        assertTrue(methods.contains("useRefInt"), "Hooks should have useRefInt");
        assertTrue(methods.contains("useRefString"), "Hooks should have useRefString");
    }

    @Test
    void hooksHasUseMemoAndCallback() {
        Set<String> methods = getPublicStaticMethodNames(Hooks.class);
        assertTrue(methods.contains("useMemo"), "Hooks should have useMemo");
        assertTrue(methods.contains("useCallback"), "Hooks should have useCallback");
    }

    @Test
    void hooksHasUseReducerAndContext() {
        Set<String> methods = getPublicStaticMethodNames(Hooks.class);
        assertTrue(methods.contains("useReducer"), "Hooks should have useReducer");
        assertTrue(methods.contains("useContext"), "Hooks should have useContext");
    }

    @Test
    void hooksHasDepsMethods() {
        Set<String> methods = getPublicStaticMethodNames(Hooks.class);
        assertTrue(methods.contains("deps"), "Hooks should have deps");
    }

    @Test
    void stateHandleClassExists() {
        assertNotNull(StateHandle.class);
    }

    @Test
    void stateHandleHasTypedGetters() {
        Set<String> methods = getPublicMethodNames(StateHandle.class);
        assertTrue(methods.contains("get"));
        assertTrue(methods.contains("getString"));
        assertTrue(methods.contains("getInt"));
        assertTrue(methods.contains("getBool"));
        assertTrue(methods.contains("getDouble"));
    }

    @Test
    void stateHandleHasTypedSetters() {
        Set<String> methods = getPublicMethodNames(StateHandle.class);
        assertTrue(methods.contains("set"));
        assertTrue(methods.contains("setInt"));
        assertTrue(methods.contains("setString"));
        assertTrue(methods.contains("setBool"));
        assertTrue(methods.contains("setDouble"));
    }

    @Test
    void stateHandleHasFunctionalUpdaters() {
        Set<String> methods = getPublicMethodNames(StateHandle.class);
        assertTrue(methods.contains("updateInt"));
        assertTrue(methods.contains("updateString"));
    }

    @Test
    void refHandleClassExists() {
        assertNotNull(RefHandle.class);
        Set<String> methods = getPublicMethodNames(RefHandle.class);
        assertTrue(methods.contains("raw"));
        assertTrue(methods.contains("getCurrent"));
        assertTrue(methods.contains("setCurrent"));
        assertTrue(methods.contains("getCurrentString"));
        assertTrue(methods.contains("getCurrentInt"));
    }

    @Test
    void effectCallbackInterfaceExists() {
        assertTrue(EffectCallback.class.isInterface());
    }

    // ====================================================================
    // Event handler interfaces
    // ====================================================================

    @Test
    void eventHandlerInterfacesExist() {
        assertTrue(EventHandler.class.isInterface());
        assertTrue(ChangeEventHandler.class.isInterface());
        assertTrue(KeyboardEventHandler.class.isInterface());
        assertTrue(FocusEventHandler.class.isInterface());
        assertTrue(SubmitEventHandler.class.isInterface());
    }

    @Test
    void eventTypesExist() {
        assertTrue(ChangeEvent.class.isInterface());
        assertTrue(KeyboardEvent.class.isInterface());
        assertTrue(MouseEvent.class.isInterface());
    }

    @Test
    void changeEventHasTarget() {
        Set<String> methods = getPublicMethodNames(ChangeEvent.class);
        assertTrue(methods.contains("getTarget"));
    }

    // ====================================================================
    // HTML DSL classes
    // ====================================================================

    @Test
    void htmlClassExists() {
        assertNotNull(Html.class);
        assertTrue(Modifier.isFinal(Html.class.getModifiers()));
    }

    @Test
    void htmlHasElementFactoryMethods() {
        Set<String> methods = getPublicStaticMethodNames(Html.class);
        String[] expectedMethods = {
            "div", "span", "p", "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "li", "table", "form", "button", "input", "a",
            "text", "fragment", "component", "mapToElements"
        };
        for (String m : expectedMethods) {
            assertTrue(methods.contains(m), "Html should have static method '" + m + "'");
        }
    }

    @Test
    void elementBuilderClassExists() {
        assertNotNull(ElementBuilder.class);
    }

    @Test
    void domBuilderClassExists() {
        assertNotNull(DomBuilder.class);
    }

    // ====================================================================
    // Component model
    // ====================================================================

    @Test
    void reactViewClassExists() {
        assertNotNull(ReactView.class);
        assertTrue(Modifier.isAbstract(ReactView.class.getModifiers()));
    }

    // ====================================================================
    // React class has core methods
    // ====================================================================

    @Test
    void reactHasCreateElementMethods() {
        Set<String> methods = getPublicStaticMethodNames(React.class);
        assertTrue(methods.contains("createElement"));
        assertTrue(methods.contains("createElementWithText"));
    }

    @Test
    void reactHasComponentWrapping() {
        Set<String> methods = getPublicStaticMethodNames(React.class);
        assertTrue(methods.contains("wrapComponent"));
        assertTrue(methods.contains("memo"));
    }

    @Test
    void reactHasPropertyUtilities() {
        Set<String> methods = getPublicStaticMethodNames(React.class);
        assertTrue(methods.contains("createObject"));
        assertTrue(methods.contains("setProperty"));
    }

    @Test
    void reactHasEventHandlerSetters() {
        Set<String> methods = getPublicStaticMethodNames(React.class);
        String[] expectedSetters = {
            "setOnClick", "setOnChange", "setOnKeyDown", "setOnKeyUp",
            "setOnFocus", "setOnBlur", "setOnSubmit",
            "setOnMouseDown", "setOnMouseUp", "setOnMouseEnter", "setOnMouseLeave"
        };
        for (String setter : expectedSetters) {
            assertTrue(methods.contains(setter), "React should have '" + setter + "'");
        }
    }

    @Test
    void reactHasTypeConversionUtilities() {
        Set<String> methods = getPublicStaticMethodNames(React.class);
        assertTrue(methods.contains("stringToJS"));
        assertTrue(methods.contains("intToJS"));
        assertTrue(methods.contains("boolToJS"));
        assertTrue(methods.contains("jsToString"));
        assertTrue(methods.contains("jsToInt"));
        assertTrue(methods.contains("jsToBool"));
    }

    @Test
    void reactHasContextApi() {
        Set<String> methods = getPublicStaticMethodNames(React.class);
        assertTrue(methods.contains("createContext"));
        assertTrue(methods.contains("fragment"));
    }

    // ====================================================================
    // ReactContext API
    // ====================================================================

    @Test
    void reactContextHasFactoryMethods() {
        Set<String> methods = getPublicStaticMethodNames(ReactContext.class);
        assertTrue(methods.contains("create"));
    }

    @Test
    void reactContextHasInstanceMethods() {
        Set<String> methods = getPublicMethodNames(ReactContext.class);
        assertTrue(methods.contains("jsContext"));
        assertTrue(methods.contains("provider"));
        assertTrue(methods.contains("provide"));
    }

    // ====================================================================
    // JsUtil
    // ====================================================================

    @Test
    void jsUtilHasTimerMethods() {
        Set<String> methods = getPublicStaticMethodNames(JsUtil.class);
        assertTrue(methods.contains("setInterval"));
        assertTrue(methods.contains("clearInterval"));
        assertTrue(methods.contains("setTimeout"));
        assertTrue(methods.contains("clearTimeout"));
    }

    @Test
    void jsUtilHasConsoleMethods() {
        Set<String> methods = getPublicStaticMethodNames(JsUtil.class);
        assertTrue(methods.contains("consoleLog"));
        assertTrue(methods.contains("consoleError"));
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    private Set<String> getPublicStaticMethodNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }

    private Set<String> getPublicMethodNames(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .map(Method::getName)
                .collect(Collectors.toSet());
    }
}

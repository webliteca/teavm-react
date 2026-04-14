package ca.weblite.teavmreact.core;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * HTTP client wrapping the browser's {@code fetch()} API.
 *
 * <p>All methods are asynchronous — they return immediately and invoke
 * the appropriate callback when the response arrives or an error occurs.
 * The public API uses plain Java types only; no {@code JSObject} or
 * {@code JSString} values are exposed.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * Fetch.get("https://api.example.com/items",
 *     (body, status) -> data.setString(body),
 *     msg -> error.setString(msg));
 *
 * Fetch.post("https://api.example.com/items",
 *     "{\"name\":\"New\"}", "application/json",
 *     (body, status) -> { /* handle response * / },
 *     msg -> { /* handle error * / });
 * }</pre>
 */
public final class Fetch {

    private Fetch() {}

    // ====================================================================
    // Public callback interfaces — plain Java, no JSObject
    // ====================================================================

    /**
     * Callback invoked when a fetch response is received.
     *
     * @see #get(String, Callback, ErrorCallback)
     */
    @FunctionalInterface
    public interface Callback {
        /**
         * Called with the response body as text and the HTTP status code.
         *
         * @param body   the response body as a string
         * @param status the HTTP status code (e.g., 200, 404)
         */
        void onResponse(String body, int status);
    }

    /**
     * Callback invoked when a fetch request fails (network error, DNS
     * failure, CORS rejection, etc.).
     *
     * @see #get(String, Callback, ErrorCallback)
     */
    @FunctionalInterface
    public interface ErrorCallback {
        /**
         * Called with an error message describing the failure.
         *
         * @param message a description of the error
         */
        void onError(String message);
    }

    // ====================================================================
    // Public API — HTTP methods
    // ====================================================================

    /**
     * Perform an HTTP GET request.
     *
     * @param url       the URL to fetch
     * @param onSuccess called with the response body and status code
     * @param onError   called if the request fails
     */
    public static void get(String url, Callback onSuccess, ErrorCallback onError) {
        doFetchNoBody(url, "GET",
                (body, status) -> onSuccess.onResponse(body, status),
                msg -> onError.onError(msg));
    }

    /**
     * Perform an HTTP POST request with a text body.
     *
     * @param url         the URL to fetch
     * @param body        the request body
     * @param contentType the Content-Type header (e.g., {@code "application/json"})
     * @param onSuccess   called with the response body and status code
     * @param onError     called if the request fails
     */
    public static void post(String url, String body, String contentType,
                            Callback onSuccess, ErrorCallback onError) {
        doFetchWithBody(url, "POST", body, contentType,
                (b, s) -> onSuccess.onResponse(b, s),
                msg -> onError.onError(msg));
    }

    /**
     * Perform an HTTP PUT request with a text body.
     *
     * @param url         the URL to fetch
     * @param body        the request body
     * @param contentType the Content-Type header (e.g., {@code "application/json"})
     * @param onSuccess   called with the response body and status code
     * @param onError     called if the request fails
     */
    public static void put(String url, String body, String contentType,
                           Callback onSuccess, ErrorCallback onError) {
        doFetchWithBody(url, "PUT", body, contentType,
                (b, s) -> onSuccess.onResponse(b, s),
                msg -> onError.onError(msg));
    }

    /**
     * Perform an HTTP PATCH request with a text body.
     *
     * @param url         the URL to fetch
     * @param body        the request body
     * @param contentType the Content-Type header (e.g., {@code "application/json"})
     * @param onSuccess   called with the response body and status code
     * @param onError     called if the request fails
     */
    public static void patch(String url, String body, String contentType,
                             Callback onSuccess, ErrorCallback onError) {
        doFetchWithBody(url, "PATCH", body, contentType,
                (b, s) -> onSuccess.onResponse(b, s),
                msg -> onError.onError(msg));
    }

    /**
     * Perform an HTTP DELETE request.
     *
     * @param url       the URL to fetch
     * @param onSuccess called with the response body and status code
     * @param onError   called if the request fails
     */
    public static void delete(String url, Callback onSuccess, ErrorCallback onError) {
        doFetchNoBody(url, "DELETE",
                (body, status) -> onSuccess.onResponse(body, status),
                msg -> onError.onError(msg));
    }

    /**
     * Perform an HTTP request with the given method and no body.
     *
     * @param method    the HTTP method (e.g., {@code "GET"}, {@code "HEAD"})
     * @param url       the URL to fetch
     * @param onSuccess called with the response body and status code
     * @param onError   called if the request fails
     */
    public static void request(String method, String url,
                               Callback onSuccess, ErrorCallback onError) {
        doFetchNoBody(url, method,
                (body, status) -> onSuccess.onResponse(body, status),
                msg -> onError.onError(msg));
    }

    /**
     * Perform an HTTP request with the given method, body, and content type.
     *
     * @param method      the HTTP method (e.g., {@code "POST"}, {@code "PUT"})
     * @param url         the URL to fetch
     * @param body        the request body
     * @param contentType the Content-Type header
     * @param onSuccess   called with the response body and status code
     * @param onError     called if the request fails
     */
    public static void request(String method, String url,
                               String body, String contentType,
                               Callback onSuccess, ErrorCallback onError) {
        doFetchWithBody(url, method, body, contentType,
                (b, s) -> onSuccess.onResponse(b, s),
                msg -> onError.onError(msg));
    }

    // ====================================================================
    // Internal @JSFunctor interfaces for the JS bridge
    // ====================================================================

    @JSFunctor
    interface JsResponseCallback extends JSObject {
        void call(String body, int status);
    }

    @JSFunctor
    interface JsErrorCallback extends JSObject {
        void call(String message);
    }

    // ====================================================================
    // Internal @JSBody bridge methods — ES5 only, no arrow functions
    // ====================================================================

    @JSBody(params = {"url", "method", "onSuccess", "onError"}, script =
            "fetch(url, {method: method})" +
            ".then(function(r) {" +
            "  var status = r.status;" +
            "  return r.text().then(function(text) {" +
            "    onSuccess(text, status);" +
            "  });" +
            "})" +
            ".catch(function(err) {" +
            "  onError('' + err);" +
            "});")
    private static native void doFetchNoBody(
            String url, String method,
            JsResponseCallback onSuccess, JsErrorCallback onError);

    @JSBody(params = {"url", "method", "body", "contentType", "onSuccess", "onError"}, script =
            "fetch(url, {method: method, body: body," +
            " headers: {'Content-Type': contentType}})" +
            ".then(function(r) {" +
            "  var status = r.status;" +
            "  return r.text().then(function(text) {" +
            "    onSuccess(text, status);" +
            "  });" +
            "})" +
            ".catch(function(err) {" +
            "  onError('' + err);" +
            "});")
    private static native void doFetchWithBody(
            String url, String method, String body, String contentType,
            JsResponseCallback onSuccess, JsErrorCallback onError);
}

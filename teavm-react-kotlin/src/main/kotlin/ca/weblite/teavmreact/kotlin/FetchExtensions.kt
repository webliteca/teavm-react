package ca.weblite.teavmreact.kotlin

import ca.weblite.teavmreact.core.Fetch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Result of a fetch request. Contains the response body and HTTP status code.
 *
 * ```
 * val response = fetchText("https://api.example.com/data")
 * if (response.ok) {
 *     items = response.body
 * }
 * ```
 */
data class FetchResponse(val body: String, val status: Int) {
    /** True if the status code is in the 200..299 range. */
    val ok: Boolean get() = status in 200..299
}

/**
 * Exception thrown when a fetch request fails due to a network error,
 * DNS failure, CORS rejection, or similar transport-level problem.
 */
class FetchException(message: String) : Exception(message)

// ========================================================================
// Suspend wrappers — use inside launchedEffect {} or any CoroutineScope
// ========================================================================

/**
 * Perform an HTTP GET and suspend until the response arrives.
 *
 * ```
 * launchedEffect {
 *     val response = fetchText("https://api.example.com/items")
 *     if (response.ok) items = response.body
 * }
 * ```
 *
 * @throws FetchException on network/transport errors
 */
suspend fun fetchText(url: String): FetchResponse = suspendCoroutine { cont ->
    Fetch.get(url,
        { body, status -> cont.resume(FetchResponse(body, status)) },
        { msg -> cont.resumeWithException(FetchException(msg)) }
    )
}

/**
 * Perform an HTTP POST with a text body and suspend until the response arrives.
 *
 * @param contentType defaults to {@code "application/json"}
 * @throws FetchException on network/transport errors
 */
suspend fun postText(
    url: String,
    body: String,
    contentType: String = "application/json"
): FetchResponse = suspendCoroutine { cont ->
    Fetch.post(url, body, contentType,
        { respBody, status -> cont.resume(FetchResponse(respBody, status)) },
        { msg -> cont.resumeWithException(FetchException(msg)) }
    )
}

/**
 * Perform an HTTP PUT with a text body and suspend until the response arrives.
 *
 * @param contentType defaults to {@code "application/json"}
 * @throws FetchException on network/transport errors
 */
suspend fun putText(
    url: String,
    body: String,
    contentType: String = "application/json"
): FetchResponse = suspendCoroutine { cont ->
    Fetch.put(url, body, contentType,
        { respBody, status -> cont.resume(FetchResponse(respBody, status)) },
        { msg -> cont.resumeWithException(FetchException(msg)) }
    )
}

/**
 * Perform an HTTP PATCH with a text body and suspend until the response arrives.
 *
 * @param contentType defaults to {@code "application/json"}
 * @throws FetchException on network/transport errors
 */
suspend fun patchText(
    url: String,
    body: String,
    contentType: String = "application/json"
): FetchResponse = suspendCoroutine { cont ->
    Fetch.patch(url, body, contentType,
        { respBody, status -> cont.resume(FetchResponse(respBody, status)) },
        { msg -> cont.resumeWithException(FetchException(msg)) }
    )
}

/**
 * Perform an HTTP DELETE and suspend until the response arrives.
 *
 * @throws FetchException on network/transport errors
 */
suspend fun deleteText(url: String): FetchResponse = suspendCoroutine { cont ->
    Fetch.delete(url,
        { body, status -> cont.resume(FetchResponse(body, status)) },
        { msg -> cont.resumeWithException(FetchException(msg)) }
    )
}

/**
 * Perform an HTTP request with an arbitrary method and suspend until
 * the response arrives.
 *
 * @param method      the HTTP method (e.g., "OPTIONS", "HEAD")
 * @param body        optional request body (null for no body)
 * @param contentType required when [body] is non-null
 * @throws FetchException on network/transport errors
 */
suspend fun fetchRequest(
    method: String,
    url: String,
    body: String? = null,
    contentType: String? = null
): FetchResponse = suspendCoroutine { cont ->
    if (body != null && contentType != null) {
        Fetch.request(method, url, body, contentType,
            { respBody, status -> cont.resume(FetchResponse(respBody, status)) },
            { msg -> cont.resumeWithException(FetchException(msg)) }
        )
    } else {
        Fetch.request(method, url,
            { respBody, status -> cont.resume(FetchResponse(respBody, status)) },
            { msg -> cont.resumeWithException(FetchException(msg)) }
        )
    }
}

package com.vbforge.http_client_api;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;

/**
 * RequestBuildingTasks — implement every method below to make {@code RequestBuildingTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code RequestBuildingDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=RequestBuildingTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Most tests only inspect the {@code HttpRequest} you build; none touch the internet.
 */
public class RequestBuildingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a GET request for {@code uri} carrying every header in {@code headers}.
     * A GET request has no body.
     *
     * <p>Examples:
     * <pre>
     *   buildGet(uri, {"Accept": "application/json", "X-Trace": "42"})
     *       → method "GET", headers Accept=application/json and X-Trace=42, no body
     *
     *   buildGet(uri, {})  → method "GET", no headers
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Loop over the map entries and add each one to the builder.</li>
     * </ul>
     *
     * @param uri     the target URI
     * @param headers header name → header value (may be empty)
     * @return the immutable request
     */
    public HttpRequest buildGet(URI uri, Map<String, String> headers) {
        throw new UnsupportedOperationException("TODO: implement buildGet");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a POST request that sends {@code json} as its body, labels it
     * {@code Content-Type: application/json}, and asks for a JSON answer with
     * {@code Accept: application/json}.
     *
     * <p>Examples:
     * <pre>
     *   buildJsonPost(uri, "{\"a\":1}")
     *       → method "POST", Content-Type=application/json, Accept=application/json,
     *         body length 7 bytes
     *
     *   buildJsonPost(uri, "{\"n\":\"é\"}")  → body length 10 bytes (é takes 2 bytes in UTF-8)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The request body comes from {@code HttpRequest.BodyPublishers}.</li>
     *   <li>The server rejects a JSON body that lacks the Content-Type header with 415.</li>
     * </ul>
     *
     * @param uri  the target URI
     * @param json the JSON text to send
     * @return the immutable request
     */
    public HttpRequest buildJsonPost(URI uri, String json) {
        throw new UnsupportedOperationException("TODO: implement buildJsonPost");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a POST request with an HTML-form body: {@code name=value} pairs joined
     * by {@code &}, in the iteration order of {@code fields}, with <em>both</em> names and
     * values form-encoded as UTF-8. The content type must be
     * {@code application/x-www-form-urlencoded}.
     *
     * <p>Examples:
     * <pre>
     *   buildFormPost(uri, {"name": "Ada Lovelace", "lang": "en"})
     *       → body "name=Ada+Lovelace&amp;lang=en"
     *
     *   buildFormPost(uri, {"q": "a&amp;b=c"})  → body "q=a%26b%3Dc"
     *   buildFormPost(uri, {})                → empty body (length 0)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link java.net.URLEncoder} does the escaping — pick the overload that takes a
     *       {@code Charset}.</li>
     *   <li>Use a map that remembers insertion order when you test with it.</li>
     * </ul>
     *
     * @param uri    the target URI
     * @param fields form field name → value (may be empty)
     * @return the immutable request
     */
    public HttpRequest buildFormPost(URI uri, Map<String, String> fields) {
        throw new UnsupportedOperationException("TODO: implement buildFormPost");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Appends a correctly encoded query string to {@code base} and returns the
     * resulting {@link URI}. Parameters appear in the iteration order of {@code params};
     * names and values are form-encoded as UTF-8. With no parameters the result is
     * just {@code base}, with no trailing {@code ?}.
     *
     * <p>Examples:
     * <pre>
     *   buildUri("http://localhost/search", {"q": "java http", "lang": "en"})
     *       → http://localhost/search?q=java+http&amp;lang=en
     *
     *   buildUri("http://localhost/search", {"q": "café"})
     *       → http://localhost/search?q=caf%C3%A9
     *
     *   buildUri("http://localhost/search", {})
     *       → http://localhost/search
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code &} and {@code =} inside a value would corrupt the query — encode every
     *       name and every value, but not the {@code &}/{@code =} separators you add.</li>
     * </ul>
     *
     * @param base   the URL without a query string
     * @param params query parameter name → value (may be empty)
     * @return the full URI
     */
    public URI buildUri(String base, Map<String, String> params) {
        throw new UnsupportedOperationException("TODO: implement buildUri");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a GET request for {@code uri} that gives up if the response takes longer
     * than {@code timeout}.
     *
     * <p>Examples:
     * <pre>
     *   buildGetWithTimeout(uri, Duration.ofSeconds(2))
     *       → method "GET", timeout() == Optional[PT2S]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This is a <em>per-request</em> setting on the builder, not a client setting.</li>
     * </ul>
     *
     * @param uri     the target URI
     * @param timeout the maximum time to wait for the response
     * @return the immutable request
     */
    public HttpRequest buildGetWithTimeout(URI uri, Duration timeout) {
        throw new UnsupportedOperationException("TODO: implement buildGetWithTimeout");
    }
}

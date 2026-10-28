package com.vbforge.http_client_api;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * AsyncRequestsTasks — implement every method below to make {@code AsyncRequestsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code AsyncRequestsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=AsyncRequestsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * The tests talk to a local {@code DemoServer}, never to the internet.
 */
public class AsyncRequestsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Starts a GET request and returns a future of the response <em>body</em>. The method
     * must return immediately — it must not wait for the response.
     *
     * <p>Examples:
     * <pre>
     *   fetchBodyAsync(client, ".../hello").join()  → "Hello, HTTP!"
     *   fetchBodyAsync(client, ".../slow?ms=400")   → a future that is NOT done yet
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The non-blocking cousin of {@code send} lives on {@code HttpClient}.</li>
     *   <li>Transform the response into its body with a {@code CompletableFuture} stage.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to GET
     * @return a future completing with the body
     */
    public CompletableFuture<String> fetchBodyAsync(HttpClient client, URI uri) {
        throw new UnsupportedOperationException("TODO: implement fetchBodyAsync");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Fetches all {@code uris} <em>concurrently</em> and returns their bodies in the same
     * order as the input. The method waits until every response has arrived.
     *
     * <p>Examples:
     * <pre>
     *   fetchAllConcurrently(client, [".../hello", ".../json"])
     *       → ["Hello, HTTP!", "{\"id\":1,\"name\":\"Ada\"}"]
     *
     *   fetchAllConcurrently(client, [])  → []
     *
     *   // three requests to ".../barrier/x?n=3" only succeed when all three are
     *   // in flight at the same time:
     *   → ["released", "released", "released"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start every request first, wait afterwards — joining inside the loop that
     *       starts them makes the work sequential.</li>
     *   <li>{@code CompletableFuture.allOf} helps you wait for the whole group.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uris   the URIs to GET (may be empty)
     * @return the bodies, in input order
     */
    public List<String> fetchAllConcurrently(HttpClient client, List<URI> uris) {
        throw new UnsupportedOperationException("TODO: implement fetchAllConcurrently");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Starts a GET request and returns a future of the body, or of {@code fallback}
     * if the request <em>fails</em> (for example connection refused). A response with
     * an error status such as 404 is <em>not</em> a failure: its body is returned as usual.
     *
     * <p>Examples:
     * <pre>
     *   fetchBodyOrFallback(client, ".../hello",      "n/a").join()  → "Hello, HTTP!"
     *   fetchBodyOrFallback(client, ".../status/404", "n/a").join()  → "status 404"
     *   fetchBodyOrFallback(client, deadUri,          "n/a").join()  → "n/a"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A failed future can be recovered with a dedicated {@code CompletableFuture} stage.</li>
     * </ul>
     *
     * @param client   the client to use
     * @param uri      the URI to GET
     * @param fallback the value to complete with when the request fails
     * @return a future that never completes exceptionally because of a transport failure
     */
    public CompletableFuture<String> fetchBodyOrFallback(HttpClient client, URI uri, String fallback) {
        throw new UnsupportedOperationException("TODO: implement fetchBodyOrFallback");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Performs two <em>dependent</em> GET requests: first fetches {@code first}, passes
     * its body to {@code next} to compute a second URI, fetches that URI and completes
     * with the second body. The result must be a single flat {@code CompletableFuture<String>}.
     *
     * <p>Examples:
     * <pre>
     *   // first  = ".../query?code=404"          → body "code=404"
     *   // next   = body → ".../status/" + body.substring(5)
     *   fetchThenFetch(client, first, next).join()  → "status 404"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You can reuse your own {@code fetchBodyAsync}-style logic for each hop.</li>
     *   <li>Choose the stage that flattens a function returning a future — otherwise you
     *       end up with a future of a future.</li>
     * </ul>
     *
     * @param client the client to use
     * @param first  the first URI
     * @param next   computes the second URI from the first body
     * @return a future of the second body
     */
    public CompletableFuture<String> fetchThenFetch(HttpClient client, URI first, Function<String, URI> next) {
        throw new UnsupportedOperationException("TODO: implement fetchThenFetch");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Fetches all {@code uris} at once and returns a future that completes with the
     * body of whichever response arrives <em>first</em>. The method itself must not block.
     *
     * <p>Examples:
     * <pre>
     *   fastest(client, [".../slow?ms=1500", ".../hello"]).join()  → "Hello, HTTP!"
     *   fastest(client, [".../hello", ".../slow?ms=1500"]).join()  → "Hello, HTTP!"
     *   fastest(client, [".../json"]).join()                       → "{\"id\":1,\"name\":\"Ada\"}"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code CompletableFuture} has a static factory that completes when
     *       <em>any one</em> of several futures does; its result type is {@code Object}.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uris   a non-empty list of URIs to race
     * @return a future of the first body
     */
    public CompletableFuture<String> fastest(HttpClient client, List<URI> uris) {
        throw new UnsupportedOperationException("TODO: implement fastest");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Starts a GET request that is abandoned after {@code timeout}. The returned future
     * completes with the body when the response is in time, and completes
     * <em>exceptionally</em> (cause: {@code HttpTimeoutException}) when it is not.
     *
     * <p>Examples:
     * <pre>
     *   fetchWithTimeout(client, ".../hello",         Duration.ofSeconds(2)).join()  → "Hello, HTTP!"
     *   fetchWithTimeout(client, ".../slow?ms=1500",  Duration.ofMillis(200)).join()
     *       → throws CompletionException, cause HttpTimeoutException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A request-level timeout belongs on the {@code HttpRequest} builder.</li>
     *   <li>Do not recover from the failure — let it propagate.</li>
     * </ul>
     *
     * @param client  the client to use
     * @param uri     the URI to GET
     * @param timeout the maximum time to wait for the response
     * @return a future of the body
     */
    public CompletableFuture<String> fetchWithTimeout(HttpClient client, URI uri, Duration timeout) {
        throw new UnsupportedOperationException("TODO: implement fetchWithTimeout");
    }
}

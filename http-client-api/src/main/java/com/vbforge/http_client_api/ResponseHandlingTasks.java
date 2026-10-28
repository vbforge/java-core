package com.vbforge.http_client_api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.List;

/**
 * ResponseHandlingTasks — implement every method below to make {@code ResponseHandlingTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ResponseHandlingDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ResponseHandlingTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * The tests talk to a local {@code DemoServer}, never to the internet.
 */
public class ResponseHandlingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Sends a GET request and classifies the response status as one of the strings
     * {@code "SUCCESS"} (2xx), {@code "REDIRECT"} (3xx), {@code "CLIENT_ERROR"} (4xx)
     * or {@code "SERVER_ERROR"} (5xx). The client passed in does <em>not</em> follow
     * redirects, so a 3xx is visible to you.
     *
     * <p>Examples:
     * <pre>
     *   classifyStatus(client, ".../status/200") → "SUCCESS"
     *   classifyStatus(client, ".../status/204") → "SUCCESS"
     *   classifyStatus(client, ".../status/302") → "REDIRECT"
     *   classifyStatus(client, ".../status/404") → "CLIENT_ERROR"
     *   classifyStatus(client, ".../status/503") → "SERVER_ERROR"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Integer division of the status code tells you the class.</li>
     *   <li>A 404 or 500 is not an exception — you just read {@code statusCode()}.</li>
     * </ul>
     *
     * @param client a client that does not follow redirects
     * @param uri    the URI to GET
     * @return the status class name
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public String classifyStatus(HttpClient client, URI uri) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement classifyStatus");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Fetches {@code uri} with a client of your own that <em>follows</em> redirects
     * (never from HTTPS to HTTP), and returns the body of the final response.
     *
     * <p>Examples:
     * <pre>
     *   fetchFollowingRedirects(".../hello")    → "Hello, HTTP!"
     *   fetchFollowingRedirects(".../redirect") → "Hello, HTTP!"   (302 → /hello)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The redirect policy is a setting on the <em>client</em> builder.</li>
     * </ul>
     *
     * @param uri the URI to GET
     * @return the body of the final (non-redirect) response
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public String fetchFollowingRedirects(URI uri) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement fetchFollowingRedirects");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Sends a GET request and returns the response body split into lines, in order,
     * without line terminators.
     *
     * <p>Examples:
     * <pre>
     *   fetchLines(client, ".../lines")  → ["alpha", "beta", "gamma"]
     *   fetchLines(client, ".../hello")  → ["Hello, HTTP!"]
     *   fetchLines(client, ".../query")  → []            (empty body → no lines)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a built-in body handler that delivers a {@code Stream<String>}.</li>
     *   <li>The stream is lazy — turn it into a {@code List} before returning.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to GET
     * @return the lines of the body
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public List<String> fetchLines(HttpClient client, URI uri) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement fetchLines");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Streams the response body of a GET request straight into the file {@code target}
     * and returns the path of that file. If {@code target} already exists, its
     * previous content must be <em>completely</em> replaced — no leftover bytes.
     *
     * <p>Examples:
     * <pre>
     *   downloadToFile(client, ".../hello", target)  → target, now containing "Hello, HTTP!"
     *
     *   // target already holds 80 characters of old text:
     *   downloadToFile(client, ".../hello", target)  → file content is exactly "Hello, HTTP!"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code BodyHandlers.ofFile} delivers a {@code Path} body.</li>
     *   <li>Check which {@code OpenOption}s the one-argument overload uses — and whether
     *       that is enough to overwrite a longer file.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to GET
     * @param target the file to write (its parent directory exists)
     * @return the path of the written file
     * @throws IOException          on transport or file failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public Path downloadToFile(HttpClient client, URI uri, Path target) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement downloadToFile");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the body of a GET request when the status is 2xx; in <em>every other
     * case</em> returns {@code fallback} — a non-2xx status, a connection failure
     * ({@code IOException}) or an interrupted thread. This method never throws.
     *
     * <p>Examples:
     * <pre>
     *   fetchOrDefault(client, ".../hello",      "n/a")  → "Hello, HTTP!"
     *   fetchOrDefault(client, ".../status/201", "n/a")  → "status 201"
     *   fetchOrDefault(client, ".../status/404", "n/a")  → "n/a"
     *   fetchOrDefault(client, ".../status/500", "n/a")  → "n/a"
     *   fetchOrDefault(client, deadUri,          "n/a")  → "n/a"   (connection refused)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>When you catch {@code InterruptedException}, restore the thread's interrupt
     *       flag before returning.</li>
     * </ul>
     *
     * @param client   the client to use
     * @param uri      the URI to GET
     * @param fallback the value to return on any failure
     * @return the body on success, otherwise {@code fallback}
     */
    public String fetchOrDefault(HttpClient client, URI uri, String fallback) {
        throw new UnsupportedOperationException("TODO: implement fetchOrDefault");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the length (in characters) of the response body of a GET request —
     * but the conversion from body to {@code int} must happen <em>inside the body
     * handler</em>, so that {@code HttpResponse<Integer>.body()} is already the length.
     *
     * <p>Examples:
     * <pre>
     *   fetchBodyLength(client, ".../hello")  → 12
     *   fetchBodyLength(client, ".../lines")  → 17
     *   fetchBodyLength(client, ".../query")  → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code BodyHandler} is a functional interface — a lambda is enough.</li>
     *   <li>{@code HttpResponse.BodySubscribers} has a {@code mapping} factory that wraps
     *       another subscriber and converts its result.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to GET
     * @return the number of characters in the body
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public int fetchBodyLength(HttpClient client, URI uri) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement fetchBodyLength");
    }
}

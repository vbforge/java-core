package com.vbforge.http_client_api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

/**
 * HttpClientBasicsTasks — implement every method below to make {@code HttpClientBasicsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code HttpClientBasicsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=HttpClientBasicsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * The tests talk to a local {@code DemoServer}, never to the internet.
 */
public class HttpClientBasicsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a new {@link HttpClient} configured for the local demo server:
     * HTTP/1.1, redirects followed in the {@code NORMAL} way, and the given
     * connect timeout.
     *
     * <p>Examples:
     * <pre>
     *   buildClient(Duration.ofSeconds(2))
     *       → client.version()          == HTTP_1_1
     *         client.followRedirects()  == NORMAL
     *         client.connectTimeout()   == Optional[PT2S]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start from {@link HttpClient#newBuilder()} — the client itself is immutable.</li>
     *   <li>Each setting has a matching builder method named after the getter.</li>
     * </ul>
     *
     * @param connectTimeout how long the client may take to establish a connection
     * @return a new client; every call returns a different instance
     */
    public HttpClient buildClient(Duration connectTimeout) {
        throw new UnsupportedOperationException("TODO: implement buildClient");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Sends a synchronous GET request and returns only the HTTP status code.
     * An error status such as 404 or 500 is <em>returned</em>, not thrown.
     *
     * <p>Examples:
     * <pre>
     *   fetchStatus(client, ".../hello")      → 200
     *   fetchStatus(client, ".../status/204") → 204   (response without a body)
     *   fetchStatus(client, ".../status/404") → 404
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You do not need the body — one of the {@code BodyHandlers} throws it away.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to GET
     * @return the response status code
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public int fetchStatus(HttpClient client, URI uri) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement fetchStatus");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Sends a synchronous GET request and returns the response body as a
     * {@code String}, whatever the status code is.
     *
     * <p>Examples:
     * <pre>
     *   fetchBody(client, ".../hello")      → "Hello, HTTP!"
     *   fetchBody(client, ".../status/404") → "status 404"   (error bodies are still bodies)
     *   fetchBody(client, ".../query")      → ""             (empty body, not null)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build the request with {@link java.net.http.HttpRequest#newBuilder(URI)}.</li>
     *   <li>Pick the {@code BodyHandlers} factory that yields a {@code String}.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to GET
     * @return the response body
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public String fetchBody(HttpClient client, URI uri) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement fetchBody");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Sends a synchronous GET request and returns the first value of the named
     * response header, or {@link Optional#empty()} when the header is absent.
     * Header names are case-insensitive.
     *
     * <p>Examples:
     * <pre>
     *   fetchHeader(client, ".../hello", "X-Server")  → Optional["demo-server"]
     *   fetchHeader(client, ".../hello", "x-server")  → Optional["demo-server"]
     *   fetchHeader(client, ".../hello", "X-Missing") → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code HttpResponse.headers()} returns an {@code HttpHeaders} that already
     *       answers with an {@code Optional}.</li>
     * </ul>
     *
     * @param client     the client to use
     * @param uri        the URI to GET
     * @param headerName the response header to look up
     * @return the first value, or empty
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public Optional<String> fetchHeader(HttpClient client, URI uri, String headerName)
            throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement fetchHeader");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Sends {@code text} in the body of a POST request with the content type
     * {@code text/plain; charset=utf-8} and returns the response body. The target
     * server echoes the request body back.
     *
     * <p>Examples:
     * <pre>
     *   postText(client, ".../echo", "ping")          → "ping"
     *   postText(client, ".../echo", "")              → ""
     *   postText(client, ".../echo", "héllo wörld ✓") → "héllo wörld ✓"   (UTF-8 round trip)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The body is supplied through a {@code BodyPublisher}.</li>
     *   <li>Do not set {@code Content-Length} yourself — the client manages it.</li>
     * </ul>
     *
     * @param client the client to use
     * @param uri    the URI to POST to
     * @param text   the text to send
     * @return the response body
     * @throws IOException          on transport failure
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public String postText(HttpClient client, URI uri, String text) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement postText");
    }
}

package com.vbforge.http_client_api;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * HttpClientBasicsDemo — annotated reference for the core {@code java.net.http} types.
 *
 * <p>Read top-to-bottom. Every section talks to a local {@link DemoServer}, so the
 * demo works offline and prints the same thing every time.
 *
 * <p>Topics:
 * <ol>
 *   <li>Creating an {@link HttpClient} — defaults vs the builder</li>
 *   <li>Synchronous GET — {@code HttpRequest}, {@code send}, {@code HttpResponse}</li>
 *   <li>Response headers — {@code HttpHeaders}</li>
 *   <li>POST with a body — {@code BodyPublishers}</li>
 *   <li>Response body handlers — {@code BodyHandlers}</li>
 * </ol>
 */
public class HttpClientBasicsDemo {

    public static void main(String[] args) throws Exception {
        try (DemoServer server = DemoServer.start()) {
            // One client, reused for every request — see section 1 for why.
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            clientCreationDemo();
            System.out.println("---");
            syncGetDemo(server, client);
            System.out.println("---");
            responseHeadersDemo(server, client);
            System.out.println("---");
            postDemo(server, client);
            System.out.println("---");
            bodyHandlersDemo(server, client);
        }
    }

    // ── 1. Creating an HttpClient ─────────────────────────────────────────────

    /**
     * {@link HttpClient} (Java 11+) is the modern replacement for
     * {@code HttpURLConnection}. It is <b>immutable and thread-safe</b>, owns a
     * connection pool, and supports HTTP/1.1, HTTP/2, sync and async calls.
     *
     * <p>WHY this matters: {@code HttpURLConnection} had a clumsy stateful API, no
     * HTTP/2 and no async support. {@code HttpClient} replaces it with a builder
     * API and {@code CompletableFuture}.
     *
     * <p>Cost model:
     * <ul>
     *   <li>creating a client — relatively expensive (selector thread, pool)</li>
     *   <li>creating a request — trivial, requests are cheap immutable values</li>
     * </ul>
     *
     * <p>Common gotcha: create ONE client and reuse it. A new client per request
     * throws away connection reuse and wastes resources.
     * Another: the defaults are HTTP/2, redirects <em>never</em> followed, and no
     * connect timeout (waits as long as the OS allows).
     */
    static void clientCreationDemo() {
        System.out.println("=== Creating an HttpClient ===");

        HttpClient defaults = HttpClient.newHttpClient();
        System.out.println("default version        : " + defaults.version());          // HTTP_2
        System.out.println("default redirects      : " + defaults.followRedirects());  // NEVER
        System.out.println("default connectTimeout : " + defaults.connectTimeout());   // Optional.empty

        HttpClient custom = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(2))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        System.out.println("custom version         : " + custom.version());
        System.out.println("custom redirects       : " + custom.followRedirects());
        System.out.println("custom connectTimeout  : " + custom.connectTimeout());
    }

    // ── 2. Synchronous GET ────────────────────────────────────────────────────

    /**
     * A synchronous call is three steps: build an {@link HttpRequest}, call
     * {@link HttpClient#send}, read the {@link HttpResponse}.
     *
     * <p>WHY {@code send} needs a {@code BodyHandler}: the client must know up front
     * how you want the body delivered (String, bytes, lines, file, discarded), so
     * the response type {@code HttpResponse<T>} is decided by the handler you pass.
     *
     * <p>Complexity: {@code send} blocks the calling thread until the full body
     * (for {@code ofString}) has arrived — O(body size) memory.
     *
     * <p>Common gotcha: {@code send} throws the <em>checked</em>
     * {@code InterruptedException}. Never swallow it silently — restore the
     * interrupt flag with {@code Thread.currentThread().interrupt()}.
     */
    static void syncGetDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Synchronous GET ===");

        HttpRequest request = HttpRequest.newBuilder(server.uri("/hello"))
                .GET()                      // GET is the default; explicit for readability
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("status  : " + response.statusCode());   // 200
        System.out.println("body    : " + response.body());         // Hello, HTTP!
        System.out.println("version : " + response.version());      // HTTP_1_1
        System.out.println("method  : " + response.request().method()); // GET
    }

    // ── 3. Response headers ───────────────────────────────────────────────────

    /**
     * {@link java.net.http.HttpHeaders} is an immutable, <b>case-insensitive</b>
     * multimap: one header name may carry several values.
     *
     * <p>WHY it matters: header names are case-insensitive in HTTP, and HTTP/2
     * forces lower-case names. Looking up {@code "Content-Type"} vs
     * {@code "content-type"} must therefore give the same answer.
     *
     * <p>Common gotcha: {@code firstValue} returns an {@code Optional} — a missing
     * header is not an error, so never call {@code .get()} blindly.
     */
    static void responseHeadersDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Response headers ===");

        HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("content-type   : " + response.headers().firstValue("content-type").orElse("?"));
        System.out.println("CONTENT-TYPE   : " + response.headers().firstValue("CONTENT-TYPE").orElse("?")); // same!
        System.out.println("x-server       : " + response.headers().firstValue("X-Server").orElse("?"));
        System.out.println("missing header : " + response.headers().firstValue("X-Nope"));        // Optional.empty
        System.out.println("content-length : " + response.headers().firstValueAsLong("content-length")); // 12
        List<String> all = response.headers().allValues("x-server");
        System.out.println("allValues     : " + all);

        Map<String, List<String>> asMap = response.headers().map();
        System.out.println("distinct names : " + asMap.size());
    }

    // ── 4. POST with a body ───────────────────────────────────────────────────

    /**
     * A request body is supplied by a {@code BodyPublisher}. The factory
     * {@link HttpRequest.BodyPublishers} offers {@code ofString}, {@code ofByteArray},
     * {@code ofFile}, {@code ofInputStream} and {@code noBody}.
     *
     * <p>WHY: the body is not a plain {@code String} parameter because it may be
     * huge or streamed; a publisher lets the client pull it in chunks.
     *
     * <p>Common gotcha: {@code ofString} encodes as UTF-8 by default, and you must
     * set the {@code Content-Type} header yourself — the client never guesses it.
     */
    static void postDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== POST with a body ===");

        HttpRequest post = HttpRequest.newBuilder(server.uri("/echo"))
                .header("Content-Type", "text/plain; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString("ping"))
                .build();
        HttpResponse<String> response = client.send(post, HttpResponse.BodyHandlers.ofString());
        System.out.println("echoed body    : " + response.body());                              // ping
        System.out.println("server saw     : " + response.headers().firstValue("X-Method").orElse("?")); // POST

        HttpRequest empty = HttpRequest.newBuilder(server.uri("/echo"))
                .POST(HttpRequest.BodyPublishers.noBody())    // POST with an empty body
                .build();
        HttpResponse<String> emptyResponse = client.send(empty, HttpResponse.BodyHandlers.ofString());
        System.out.println("empty echo     : '" + emptyResponse.body() + "'");
    }

    // ── 5. Body handlers ──────────────────────────────────────────────────────

    /**
     * {@link HttpResponse.BodyHandlers} decides the Java type of {@code response.body()}.
     *
     * <ul>
     *   <li>{@code ofString()} — {@code String}</li>
     *   <li>{@code ofByteArray()} — {@code byte[]} (binary data: images, PDFs)</li>
     *   <li>{@code discarding()} — {@code Void}; body is read and thrown away</li>
     * </ul>
     *
     * <p>WHY: choosing the handler at call time lets the client stream straight into
     * the form you need instead of always buffering a String.
     *
     * <p>Common gotcha: use {@code discarding()} when you only care about the
     * status — {@code body()} is then {@code null}.
     */
    static void bodyHandlersDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Body handlers ===");

        HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();

        HttpResponse<String> asString = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("ofString       : " + asString.body());

        HttpResponse<byte[]> asBytes = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        System.out.println("ofByteArray    : " + asBytes.body().length + " bytes");   // 12

        HttpResponse<Void> discarded = client.send(request, HttpResponse.BodyHandlers.discarding());
        System.out.println("discarding     : status " + discarded.statusCode() + ", body " + discarded.body());
    }
}

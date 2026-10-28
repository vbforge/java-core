package com.vbforge.http_client_api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * RequestBuildingDemo — annotated reference for building {@link HttpRequest} objects.
 *
 * <p>Read top-to-bottom. Most sections only <em>build</em> and inspect requests (no
 * network at all); the later ones send them to a local {@link DemoServer}.
 *
 * <p>Topics:
 * <ol>
 *   <li>Anatomy of a request — {@code header} vs {@code setHeader}, timeout, {@code copy()}</li>
 *   <li>Query strings and URL encoding</li>
 *   <li>Body publishers — {@code ofString}, {@code ofByteArray}, {@code noBody}</li>
 *   <li>Content-Type: JSON and form bodies</li>
 *   <li>HTTP methods — GET, POST, PUT, DELETE and custom ones like PATCH</li>
 * </ol>
 */
public class RequestBuildingDemo {

    public static void main(String[] args) throws Exception {
        try (DemoServer server = DemoServer.start()) {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

            requestAnatomyDemo();
            System.out.println("---");
            queryStringDemo(server, client);
            System.out.println("---");
            bodyPublishersDemo();
            System.out.println("---");
            contentTypeDemo(server, client);
            System.out.println("---");
            httpMethodsDemo(server, client);
        }
    }

    // ── 1. Anatomy of a request ───────────────────────────────────────────────

    /**
     * An {@link HttpRequest} is an <b>immutable value</b>: URI + method + headers +
     * optional body + optional timeout. You create it with a <b>mutable builder</b>.
     *
     * <p>WHY it matters: because requests are immutable you can safely share, log
     * and retry the same request object from many threads. The builder is where the
     * mutation happens — {@code copy()} gives you an independent builder.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>{@code header()} <em>adds</em> a value; {@code setHeader()} <em>replaces</em>.</li>
     *   <li>Restricted headers ({@code Content-Length}, {@code Host}, {@code Connection},
     *       {@code Expect}, {@code Upgrade}) cannot be set — the client manages them.</li>
     *   <li>A request {@code timeout} covers waiting for the <em>response headers</em>;
     *       the client's {@code connectTimeout} covers only the connection.</li>
     * </ul>
     */
    static void requestAnatomyDemo() {
        System.out.println("=== Anatomy of a request ===");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/users"))
                .header("Accept", "application/json")
                .header("Accept", "text/plain")          // header() ADDS a second value
                .setHeader("X-Trace", "first")
                .setHeader("X-Trace", "second")          // setHeader() REPLACES the first
                .timeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        System.out.println("method   : " + request.method());                       // GET
        System.out.println("uri      : " + request.uri());
        System.out.println("Accept   : " + request.headers().allValues("Accept"));    // [application/json, text/plain]
        System.out.println("X-Trace  : " + request.headers().allValues("X-Trace"));   // [second]
        System.out.println("timeout  : " + request.timeout());                        // Optional[PT5S]
        System.out.println("version  : " + request.version());                        // Optional[HTTP_1_1]
        System.out.println("body     : " + request.bodyPublisher());                  // Optional.empty

        // Restricted headers are rejected immediately
        try {
            HttpRequest.newBuilder(URI.create("http://localhost/")).header("Content-Length", "5").build();
        } catch (IllegalArgumentException e) {
            System.out.println("restricted header rejected: IllegalArgumentException");
        }

        // copy() — derive several requests from one shared base without cross-talk
        HttpRequest.Builder base = HttpRequest.newBuilder().header("Accept", "application/json");
        HttpRequest users = base.copy().uri(URI.create("http://localhost/users")).build();
        HttpRequest orders = base.copy().uri(URI.create("http://localhost/orders")).header("X-Extra", "1").build();
        System.out.println("users  X-Extra : " + users.headers().firstValue("X-Extra"));   // Optional.empty
        System.out.println("orders X-Extra : " + orders.headers().firstValue("X-Extra"));  // Optional[1]
    }

    // ── 2. Query strings and URL encoding ─────────────────────────────────────

    /**
     * The client does <b>not</b> build or encode query strings for you — the URI you
     * pass is sent as-is. Encode every value yourself.
     *
     * <p>WHY this matters: a raw {@code &}, {@code =}, space or non-ASCII character
     * inside a value silently changes the meaning of the query (or makes
     * {@code URI.create} throw). {@link URLEncoder} produces a safe form.
     *
     * <p>Common gotcha: {@code URLEncoder} implements HTML <em>form</em> encoding, so
     * a space becomes {@code +}, not {@code %20}. That is right for query strings
     * and form bodies, but wrong inside a URL <em>path</em>.
     */
    static void queryStringDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Query strings & encoding ===");

        System.out.println("encode(java http)   : " + encode("java http"));          // java+http
        System.out.println("encode(a&b=c)       : " + encode("a&b=c"));              // a%26b%3Dc
        System.out.println("encode(café)        : " + encode("café"));               // caf%C3%A9

        try {
            URI.create("http://localhost/search?q=java http");   // raw space is illegal in a URI
        } catch (IllegalArgumentException e) {
            System.out.println("raw space in URI    : IllegalArgumentException");
        }

        String query = "q=" + encode("a&b=c") + "&lang=" + encode("en");
        HttpRequest request = HttpRequest.newBuilder(server.uri("/query?" + query)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("server received     : " + response.body());              // q=a%26b%3Dc&lang=en
    }

    /** Small helper used by the demo (and mirrored by the tasks): form-encodes one value as UTF-8. */
    static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // ── 3. Body publishers ────────────────────────────────────────────────────

    /**
     * {@link HttpRequest.BodyPublishers} turn Java data into a request body.
     *
     * <ul>
     *   <li>{@code ofString(s)} — UTF-8 by default; {@code ofString(s, charset)} to choose</li>
     *   <li>{@code ofByteArray(bytes)} — raw bytes (binary uploads)</li>
     *   <li>{@code noBody()} — an explicit empty body</li>
     *   <li>{@code ofFile(path)}, {@code ofInputStream(supplier)} — streamed bodies</li>
     * </ul>
     *
     * <p>WHY: every publisher reports its {@code contentLength()}; the client uses it
     * to set the {@code Content-Length} header for you (that is why you may not).
     *
     * <p>Common gotcha: length is in <em>bytes</em>, not characters. {@code "é"} is
     * one char but two UTF-8 bytes. {@code GET()} has <em>no</em> body publisher at
     * all ({@code Optional.empty}) whereas {@code POST(noBody())} has one of length 0.
     */
    static void bodyPublishersDemo() {
        System.out.println("=== Body publishers ===");

        System.out.println("ofString(hello)   : " + HttpRequest.BodyPublishers.ofString("hello").contentLength());   // 5
        System.out.println("ofString(héllo)   : " + HttpRequest.BodyPublishers.ofString("héllo").contentLength());   // 6 (é = 2 bytes)
        System.out.println("ofByteArray(3)    : " + HttpRequest.BodyPublishers.ofByteArray(new byte[]{1, 2, 3}).contentLength());
        System.out.println("noBody()          : " + HttpRequest.BodyPublishers.noBody().contentLength());            // 0

        URI uri = URI.create("http://localhost/");
        System.out.println("GET  has body?    : " + HttpRequest.newBuilder(uri).GET().build().bodyPublisher().isPresent());   // false
        System.out.println("POST noBody has?  : " + HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.noBody()).build().bodyPublisher().isPresent());                     // true
    }

    // ── 4. Content-Type: JSON and form bodies ─────────────────────────────────

    /**
     * The client never guesses a {@code Content-Type}. If you send JSON, you must say
     * so — otherwise the server sees an unlabelled body and may reject it.
     *
     * <p>WHY it matters: the Content-Type decides how the server parses the body. The
     * demo server mimics a strict API: {@code /json-echo} answers {@code 415 Unsupported
     * Media Type} unless the header is {@code application/json}.
     *
     * <p>No JSON library is used in this module: a JSON body is just a
     * {@code String} you assemble yourself. (The {@code json-jackson-basics} module
     * covers real serialization.)
     *
     * <p>Also shown: an {@code Authorization} header is an ordinary header.
     */
    static void contentTypeDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Content-Type ===");

        String json = "{\"name\":\"Ada\"}";

        HttpRequest withType = HttpRequest.newBuilder(server.uri("/json-echo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> ok = client.send(withType, HttpResponse.BodyHandlers.ofString());
        System.out.println("with Content-Type    : " + ok.statusCode() + " " + ok.body());   // 200 {"name":"Ada"}

        HttpRequest withoutType = HttpRequest.newBuilder(server.uri("/json-echo"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> rejected = client.send(withoutType, HttpResponse.BodyHandlers.ofString());
        System.out.println("without Content-Type : " + rejected.statusCode());                // 415

        HttpRequest form = HttpRequest.newBuilder(server.uri("/form"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("name=" + encode("Ada Lovelace") + "&lang=en"))
                .build();
        System.out.println("form echo            : " + client.send(form, HttpResponse.BodyHandlers.ofString()).body());

        HttpRequest auth = HttpRequest.newBuilder(server.uri("/echo-header/Authorization"))
                .header("Authorization", "Bearer token123")
                .build();
        System.out.println("Authorization seen   : " + client.send(auth, HttpResponse.BodyHandlers.ofString()).body());
    }

    // ── 5. HTTP methods ───────────────────────────────────────────────────────

    /**
     * The builder has shortcuts for the common verbs — {@code GET()}, {@code POST(p)},
     * {@code PUT(p)}, {@code DELETE()} — and a generic {@code method(name, publisher)}
     * for anything else (for example {@code PATCH}).
     *
     * <p>WHY: {@code HttpURLConnection} had no {@code PATCH} support at all; here any
     * token is allowed through {@code method(...)}.
     *
     * <p>Common gotcha: {@code DELETE()} takes no body; if the API needs one, use
     * {@code method("DELETE", publisher)}. And {@code GET()} is the default method
     * when you call neither.
     */
    static void httpMethodsDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== HTTP methods ===");

        URI uri = server.uri("/echo");
        List<HttpRequest> requests = List.of(
                HttpRequest.newBuilder(uri).GET().build(),
                HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.ofString("p")).build(),
                HttpRequest.newBuilder(uri).PUT(HttpRequest.BodyPublishers.ofString("p")).build(),
                HttpRequest.newBuilder(uri).DELETE().build(),
                HttpRequest.newBuilder(uri).method("PATCH", HttpRequest.BodyPublishers.ofString("p")).build());

        for (HttpRequest request : requests) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(request.method() + " → server saw " + response.headers().firstValue("X-Method").orElse("?"));
        }
    }
}

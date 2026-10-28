package com.vbforge.http_client_api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ResponseHandlingDemo — annotated reference for working with {@link HttpResponse}.
 *
 * <p>Read top-to-bottom. Every section talks to a local {@link DemoServer}.
 *
 * <p>Topics:
 * <ol>
 *   <li>Status codes — an error status is <em>not</em> an exception</li>
 *   <li>Redirects — {@code NEVER} (default) vs {@code NORMAL}, {@code previousResponse()}</li>
 *   <li>Body handlers — lines, file, input stream, byte array</li>
 *   <li>A custom body handler with {@code BodySubscribers.mapping}</li>
 *   <li>Failures — connection refused, request timeout</li>
 * </ol>
 */
public class ResponseHandlingDemo {

    public static void main(String[] args) throws Exception {
        try (DemoServer server = DemoServer.start()) {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

            statusCodesDemo(server, client);
            System.out.println("---");
            redirectsDemo(server);
            System.out.println("---");
            bodyHandlersDemo(server, client);
            System.out.println("---");
            customBodyHandlerDemo(server, client);
            System.out.println("---");
            failuresDemo(server, client);
        }
    }

    // ── 1. Status codes ───────────────────────────────────────────────────────

    /**
     * {@code HttpClient.send} returns normally for <b>every</b> HTTP status it can
     * read — 200, 404 and 500 alike. Only <em>transport</em> problems (cannot connect,
     * timeout, interrupted) throw.
     *
     * <p>WHY this matters: many other clients throw on 4xx/5xx. Here <em>you</em> must
     * inspect {@code statusCode()}; forgetting to do so means silently processing an
     * error page as if it were your data.
     *
     * <p>Status classes: 2xx success, 3xx redirect, 4xx client error, 5xx server error.
     */
    static void statusCodesDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Status codes ===");

        for (int code : new int[]{200, 204, 404, 500}) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/status/" + code)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(code + " → statusCode=" + response.statusCode()
                    + " body='" + response.body() + "' success=" + isSuccess(response.statusCode()));
        }
    }

    /** 2xx check — the only place the demo decides what "success" means. */
    static boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    // ── 2. Redirects ──────────────────────────────────────────────────────────

    /**
     * The default {@code followRedirects} policy is {@code Redirect.NEVER}: a
     * {@code 302} is handed back to you as an ordinary response with a
     * {@code Location} header.
     *
     * <p>Policies: {@code NEVER} (default), {@code NORMAL} (follow, but never
     * HTTPS → HTTP downgrade), {@code ALWAYS} (follow even downgrades).
     *
     * <p>WHY: following redirects silently can leak credentials or hide a moved
     * resource, so the safe default is "do nothing". Pick {@code NORMAL} when you
     * want browser-like behaviour.
     *
     * <p>When a redirect was followed, {@code response.uri()} is the <em>final</em> URI and
     * {@code response.previousResponse()} exposes the earlier hop.
     */
    static void redirectsDemo(DemoServer server) throws IOException, InterruptedException {
        System.out.println("=== Redirects ===");

        HttpRequest request = HttpRequest.newBuilder(server.uri("/redirect")).build();

        HttpClient never = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpResponse<String> notFollowed = never.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("NEVER  status   : " + notFollowed.statusCode());                                    // 302
        System.out.println("NEVER  Location : " + notFollowed.headers().firstValue("Location").orElse("?"));

        HttpClient normal = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpResponse<String> followed = normal.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("NORMAL status   : " + followed.statusCode());                                       // 200
        System.out.println("NORMAL body     : " + followed.body());                                            // Hello, HTTP!
        System.out.println("NORMAL final uri: " + followed.uri().getPath());                                   // /hello
        System.out.println("previous status : " + followed.previousResponse().map(HttpResponse::statusCode));  // Optional[302]
    }

    // ── 3. Body handlers ──────────────────────────────────────────────────────

    /**
     * Besides {@code ofString()} the JDK ships handlers for the other common shapes:
     *
     * <ul>
     *   <li>{@code ofLines()} — a lazy {@code Stream<String>}, one element per line</li>
     *   <li>{@code ofFile(path)} — streams the body straight to disk (no big buffer)</li>
     *   <li>{@code ofInputStream()} — you read the stream yourself; <b>you must close it</b></li>
     *   <li>{@code ofByteArray()} — everything in memory as {@code byte[]}</li>
     * </ul>
     *
     * <p>WHY: downloading a 2 GB file with {@code ofString()} means 2 GB on the heap.
     * {@code ofFile} keeps memory flat.
     *
     * <p>Common gotcha: {@code ofFile(path)} opens the file with
     * {@code CREATE, WRITE} only — it does <em>not</em> truncate. Downloading a short body
     * over a longer existing file leaves stale trailing bytes. Pass
     * {@code TRUNCATE_EXISTING} explicitly when overwriting.
     */
    static void bodyHandlersDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Body handlers ===");

        HttpRequest lines = HttpRequest.newBuilder(server.uri("/lines")).build();
        HttpResponse<java.util.stream.Stream<String>> lineResponse =
                client.send(lines, HttpResponse.BodyHandlers.ofLines());
        List<String> collected = lineResponse.body().collect(Collectors.toList());
        System.out.println("ofLines        : " + collected);                         // [alpha, beta, gamma]

        Path file = Files.createTempFile("http-client-demo", ".txt");
        try {
            Files.writeString(file, "a much longer stale content that is already in the file");
            client.send(lines, HttpResponse.BodyHandlers.ofFile(file));              // CREATE, WRITE — no truncate!
            System.out.println("ofFile (no trunc) : " + Files.readString(file).replace("\n", "|"));

            client.send(lines, HttpResponse.BodyHandlers.ofFile(file,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                    java.nio.file.StandardOpenOption.WRITE));
            System.out.println("ofFile (trunc)    : " + Files.readString(file).replace("\n", "|"));
        } finally {
            Files.deleteIfExists(file);
        }

        HttpResponse<InputStream> streamResponse = client.send(lines, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream in = streamResponse.body()) {
            System.out.println("ofInputStream  : " + in.readAllBytes().length + " bytes");   // 17
        }

        HttpResponse<byte[]> bytes = client.send(lines, HttpResponse.BodyHandlers.ofByteArray());
        System.out.println("ofByteArray    : " + bytes.body().length + " bytes");           // 17
    }

    // ── 4. A custom body handler ──────────────────────────────────────────────

    /**
     * A {@link HttpResponse.BodyHandler} is just a function from response info
     * (status, headers) to a {@link HttpResponse.BodySubscriber}. You rarely write a
     * subscriber from scratch — {@code BodySubscribers.mapping} adapts an existing one.
     *
     * <p>WHY: it converts the body to your own type <em>inside</em> the client, so
     * {@code response.body()} is already the type you want — no manual parsing after.
     *
     * <p>Common gotcha: {@code ofString()} decodes with the charset in the
     * {@code Content-Type} header, falling back to UTF-8; a mapping on
     * {@code BodySubscribers.ofString(charset)} fixes the charset explicitly.
     */
    static void customBodyHandlerDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Custom body handler ===");

        HttpResponse.BodyHandler<Integer> lengthHandler = info -> HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                String::length);

        HttpResponse<Integer> response = client.send(
                HttpRequest.newBuilder(server.uri("/hello")).build(), lengthHandler);
        System.out.println("body as Integer : " + response.body());                          // 12
    }

    // ── 5. Failures ───────────────────────────────────────────────────────────

    /**
     * Transport failures are exceptions; they all extend {@link IOException}:
     *
     * <ul>
     *   <li>{@link java.net.ConnectException} — nothing listens on that port</li>
     *   <li>{@link java.net.http.HttpConnectTimeoutException} — connect took too long</li>
     *   <li>{@link HttpTimeoutException} — a request {@code timeout(...)} elapsed</li>
     * </ul>
     *
     * <p>WHY it matters: networks fail — always decide what "failure" means for your
     * caller (retry? fallback? propagate?). Without a request timeout a stuck server
     * can block {@code send()} for a very long time.
     */
    static void failuresDemo(DemoServer server, HttpClient client) throws IOException, InterruptedException {
        System.out.println("=== Failures ===");

        URI dead = DemoServer.deadUri("/hello");
        try {
            client.send(HttpRequest.newBuilder(dead).build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.out.println("connection refused : " + e.getClass().getSimpleName());   // ConnectException
        }

        HttpRequest slow = HttpRequest.newBuilder(server.uri("/slow?ms=1500"))
                .timeout(Duration.ofMillis(200))
                .build();
        try {
            client.send(slow, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            System.out.println("request timeout    : HttpTimeoutException");
        }
    }
}

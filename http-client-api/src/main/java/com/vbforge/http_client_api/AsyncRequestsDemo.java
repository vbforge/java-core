package com.vbforge.http_client_api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * AsyncRequestsDemo — annotated reference for {@link HttpClient#sendAsync}.
 *
 * <p>Read top-to-bottom. Every section talks to a local {@link DemoServer}.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code sendAsync} is non-blocking — it returns a {@link CompletableFuture} immediately</li>
 *   <li>Consuming the result — {@code thenApply}, {@code thenAccept}, {@code join} vs {@code get}</li>
 *   <li>Concurrent fan-out — many requests at once with {@code allOf}</li>
 *   <li>Chaining and combining — {@code thenCompose} vs {@code thenCombine}</li>
 *   <li>Errors, timeouts and cancellation</li>
 *   <li>A custom executor</li>
 * </ol>
 */
public class AsyncRequestsDemo {

    public static void main(String[] args) throws Exception {
        try (DemoServer server = DemoServer.start()) {
            HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

            nonBlockingDemo(server, client);
            System.out.println("---");
            consumingResultsDemo(server, client);
            System.out.println("---");
            fanOutDemo(server, client);
            System.out.println("---");
            chainingDemo(server, client);
            System.out.println("---");
            errorsDemo(server, client);
            System.out.println("---");
            customExecutorDemo(server);
        }
    }

    // ── 1. sendAsync is non-blocking ──────────────────────────────────────────

    /**
     * {@code sendAsync(request, handler)} starts the exchange and returns a
     * {@code CompletableFuture<HttpResponse<T>>} <b>immediately</b>. The calling thread
     * is free to do other work while the response travels.
     *
     * <p>WHY it matters: with {@code send()} one thread is parked per in-flight
     * request. With {@code sendAsync()} the client's internal selector thread does the
     * I/O, so a handful of threads can drive thousands of requests.
     *
     * <p>Common gotcha: a future that is not completed yet says
     * {@code isDone() == false}. Forgetting to {@code join()} before the program
     * exits means the response may never be read.
     */
    static void nonBlockingDemo(DemoServer server, HttpClient client) {
        System.out.println("=== sendAsync is non-blocking ===");

        HttpRequest request = HttpRequest.newBuilder(server.uri("/slow?ms=300")).build();
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("done right after sendAsync : " + future.isDone());      // false — still waiting
        HttpResponse<String> response = future.join();                              // now we block
        System.out.println("done after join            : " + future.isDone());       // true
        System.out.println("body                       : " + response.body());      // slow done
    }

    // ── 2. Consuming the result ───────────────────────────────────────────────

    /**
     * Compose follow-up work with the {@code CompletableFuture} API instead of
     * blocking:
     *
     * <ul>
     *   <li>{@code thenApply(f)} — transform the value (response → body)</li>
     *   <li>{@code thenAccept(c)} — consume the value, no result</li>
     *   <li>{@code join()} — wait; failures arrive as <em>unchecked</em> {@link CompletionException}</li>
     *   <li>{@code get()} — wait; failures arrive as <em>checked</em> {@link ExecutionException}</li>
     * </ul>
     *
     * <p>WHY: transformations run when the response arrives, with no thread waiting.
     * {@code join()} is the convenient blocking call at the very edge of your program
     * (or in a test).
     */
    static void consumingResultsDemo(DemoServer server, HttpClient client) throws Exception {
        System.out.println("=== Consuming results ===");

        CompletableFuture<String> body = client
                .sendAsync(HttpRequest.newBuilder(server.uri("/hello")).build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
        System.out.println("thenApply → body : " + body.join());                      // Hello, HTTP!

        CompletableFuture<Void> printed = client
                .sendAsync(HttpRequest.newBuilder(server.uri("/hello")).build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenAccept(status -> System.out.println("thenAccept status : " + status));
        printed.get();                                                                // checked-exception flavour
    }

    // ── 3. Concurrent fan-out ─────────────────────────────────────────────────

    /**
     * To run N requests at once, fire all {@code sendAsync} calls first and wait
     * afterwards — {@code CompletableFuture.allOf(...)} completes when every one has.
     *
     * <p>WHY it matters: N sequential {@code send()} calls cost the <em>sum</em> of the
     * latencies, N concurrent ones cost roughly the <em>maximum</em>.
     *
     * <p>Proof of real concurrency: the server's {@code /barrier/{id}?n=3} endpoint
     * only answers "released" once three requests with the same id are
     * <em>in flight simultaneously</em>; a lone request times out after 3 seconds.
     * (The matching test shows the sequential version failing.)
     *
     * <p>Common gotcha: start <em>all</em> futures before joining any. A stream with
     * {@code .map(send).map(join)} in one pipeline joins each future before the next
     * is created — silently sequential.
     */
    static void fanOutDemo(DemoServer server, HttpClient client) {
        System.out.println("=== Concurrent fan-out ===");

        String id = "demo-" + System.nanoTime();
        List<CompletableFuture<String>> futures = List.of(1, 2, 3).stream()
                .map(i -> client.sendAsync(
                                HttpRequest.newBuilder(server.uri("/barrier/" + id + "?n=3")).build(),
                                HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body))
                .collect(Collectors.toList());                                       // all three are now in flight

        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
        List<String> bodies = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("barrier results : " + bodies);                            // [released, released, released]
    }

    // ── 4. Chaining and combining ─────────────────────────────────────────────

    /**
     * Two shapes of multi-request workflow:
     *
     * <ul>
     *   <li><b>Dependent</b> — the second URL is computed from the first response:
     *       {@code thenCompose(fn)} where {@code fn} returns a new future (flat-maps).</li>
     *   <li><b>Independent</b> — both requests can run together:
     *       {@code a.thenCombine(b, merge)}.</li>
     * </ul>
     *
     * <p>WHY: {@code thenApply} with a function that returns a future would give you a
     * {@code CompletableFuture<CompletableFuture<…>>}; {@code thenCompose} flattens it.
     */
    static void chainingDemo(DemoServer server, HttpClient client) {
        System.out.println("=== Chaining & combining ===");

        // Dependent: first call tells us which status to ask for
        CompletableFuture<String> chained = get(client, server.uri("/query?code=404"))
                .thenCompose(first -> get(client, server.uri("/status/" + first.substring("code=".length()))));
        System.out.println("thenCompose : " + chained.join());                        // status 404

        // Independent: both in flight at once, merged when both are done
        CompletableFuture<String> hello = get(client, server.uri("/hello"));
        CompletableFuture<String> json = get(client, server.uri("/json"));
        System.out.println("thenCombine : " + hello.thenCombine(json, (a, b) -> a + " + " + b).join());
    }

    private static CompletableFuture<String> get(HttpClient client, URI uri) {
        return client.sendAsync(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    // ── 5. Errors, timeouts, cancellation ─────────────────────────────────────

    /**
     * In async code a failure does not throw at the call site — the future completes
     * <em>exceptionally</em>.
     *
     * <ul>
     *   <li>{@code exceptionally(fn)} — recover with a fallback value</li>
     *   <li>{@code handle((value, error) -> …)} — see both outcomes</li>
     *   <li>{@code join()} wraps the real cause in a {@link CompletionException}</li>
     *   <li>{@code future.cancel(true)} — give up; the future reports {@code isCancelled()}</li>
     * </ul>
     *
     * <p>WHY it matters: an unhandled failed future is silent until somebody joins it.
     * Always attach {@code exceptionally}/{@code handle}, or join and catch.
     *
     * <p>Common gotcha: a 404 or 500 is <em>not</em> a failed future (see the status-code
     * section of {@code ResponseHandlingDemo}) — only transport errors are.
     */
    static void errorsDemo(DemoServer server, HttpClient client) throws IOException {
        System.out.println("=== Errors, timeouts, cancellation ===");

        URI dead = DemoServer.deadUri("/hello");

        CompletableFuture<String> recovered = client
                .sendAsync(HttpRequest.newBuilder(dead).build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(error -> "fallback");
        System.out.println("exceptionally  : " + recovered.join());                   // fallback

        try {
            client.sendAsync(HttpRequest.newBuilder(dead).build(), HttpResponse.BodyHandlers.ofString()).join();
        } catch (CompletionException e) {
            System.out.println("join wraps     : CompletionException → cause is IOException? "
                    + (e.getCause() instanceof IOException));                         // true
        }

        HttpRequest slow = HttpRequest.newBuilder(server.uri("/slow?ms=1500"))
                .timeout(java.time.Duration.ofMillis(200))
                .build();
        try {
            client.sendAsync(slow, HttpResponse.BodyHandlers.ofString()).join();
        } catch (CompletionException e) {
            System.out.println("timeout cause  : " + e.getCause().getClass().getSimpleName());   // HttpTimeoutException
        }

        CompletableFuture<HttpResponse<String>> cancelled = client.sendAsync(
                HttpRequest.newBuilder(server.uri("/slow?ms=1500")).build(), HttpResponse.BodyHandlers.ofString());
        System.out.println("cancel(true)   : " + cancelled.cancel(true) + ", isCancelled=" + cancelled.isCancelled());
    }

    // ── 6. A custom executor ──────────────────────────────────────────────────

    /**
     * By default the client uses its own cached thread pool for async work.
     * {@code HttpClient.Builder.executor(...)} lets you supply yours — to cap
     * concurrency, name threads, or propagate context.
     *
     * <p>WHY: the dependent stages ({@code thenApply}, …) of a {@code sendAsync} future
     * run on the client's executor, so in a real service you want control over it.
     *
     * <p>Common gotcha: an executor you pass in is <em>yours</em> to shut down — the
     * client will not do it (and on Java 17 the client has no {@code close()}; that
     * arrived in 21).
     */
    static void customExecutorDemo(DemoServer server) {
        System.out.println("=== Custom executor ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .executor(pool)
                    .build();
            System.out.println("client executor is ours : " + client.executor().orElseThrow().equals(pool));  // true
            System.out.println("response                : " + get(client, server.uri("/hello")).join());
        } finally {
            pool.shutdown();                                                          // our responsibility
        }
    }
}

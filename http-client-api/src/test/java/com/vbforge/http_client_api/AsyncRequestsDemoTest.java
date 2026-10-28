package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AsyncRequestsDemoTest — verifies every concept shown in {@link AsyncRequestsDemo}.
 *
 * <p>These tests are always GREEN. They talk only to a local {@link DemoServer}.
 */
@DisplayName("AsyncRequestsDemo — API contracts")
class AsyncRequestsDemoTest {

    private static DemoServer server;
    private static HttpClient client;

    @BeforeAll
    static void startServer() throws Exception {
        server = DemoServer.start();
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    @AfterAll
    static void stopServer() {
        server.close();
    }

    private static CompletableFuture<String> getBody(HttpClient c, URI uri) {
        return c.sendAsync(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    // ── 1. sendAsync is non-blocking ──────────────────────────────────────────

    @Nested
    @DisplayName("1. sendAsync is non-blocking")
    class NonBlockingTest {

        @Test
        @DisplayName("sendAsync returns before the response has arrived")
        void returnsImmediately() {
            CompletableFuture<HttpResponse<String>> future = client.sendAsync(
                    HttpRequest.newBuilder(server.uri("/slow?ms=400")).build(), HttpResponse.BodyHandlers.ofString());
            assertThat(future.isDone()).isFalse();
            assertThat(future.join().body()).isEqualTo("slow done");
            assertThat(future.isDone()).isTrue();
        }

        @Test
        @DisplayName("the future carries the same HttpResponse that send() would return")
        void sameResponse() {
            HttpResponse<String> response = client.sendAsync(
                    HttpRequest.newBuilder(server.uri("/hello")).build(), HttpResponse.BodyHandlers.ofString()).join();
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("Hello, HTTP!");
        }
    }

    // ── 2. Consuming the result ───────────────────────────────────────────────

    @Nested
    @DisplayName("2. Consuming results")
    class ConsumingResultsTest {

        @Test
        @DisplayName("thenApply transforms the response into its body")
        void thenApply() {
            assertThat(getBody(client, server.uri("/hello")).join()).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("thenAccept consumes the value and yields Void")
        void thenAccept() throws Exception {
            StringBuilder seen = new StringBuilder();
            CompletableFuture<Void> done = getBody(client, server.uri("/hello")).thenAccept(seen::append);
            assertThat(done.get(5, TimeUnit.SECONDS)).isNull();
            assertThat(seen).hasToString("Hello, HTTP!");
        }

        @Test
        @DisplayName("join() wraps failures in an unchecked CompletionException")
        void joinWrapsUnchecked() throws Exception {
            CompletableFuture<String> failed = getBody(client, DemoServer.deadUri("/hello"));
            assertThatThrownBy(failed::join).isInstanceOf(CompletionException.class);
        }

        @Test
        @DisplayName("get() wraps failures in a checked ExecutionException")
        void getWrapsChecked() throws Exception {
            CompletableFuture<String> failed = getBody(client, DemoServer.deadUri("/hello"));
            assertThatThrownBy(() -> failed.get(5, TimeUnit.SECONDS)).isInstanceOf(ExecutionException.class);
        }
    }

    // ── 3. Concurrent fan-out ─────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Concurrent fan-out")
    class FanOutTest {

        @Test
        @DisplayName("three requests in flight together release a barrier of 3")
        void concurrentRequestsReleaseBarrier() {
            String id = "fanout-" + System.nanoTime();
            List<CompletableFuture<String>> futures = List.of(1, 2, 3).stream()
                    .map(i -> getBody(client, server.uri("/barrier/" + id + "?n=3")))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
            assertThat(futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                    .containsExactly("released", "released", "released");
        }

        @Test
        @DisplayName("sequential send() calls can never satisfy a barrier of 2 — it times out")
        void sequentialCallsTimeOut() throws Exception {
            String id = "sequential-" + System.nanoTime();
            HttpRequest request = HttpRequest.newBuilder(server.uri("/barrier/" + id + "?n=2")).build();
            HttpResponse<String> first = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(first.body()).isEqualTo("timeout");
        }

        @Test
        @DisplayName("allOf() completes only when every future has completed")
        void allOfWaitsForEverything() {
            CompletableFuture<String> fast = getBody(client, server.uri("/hello"));
            CompletableFuture<String> slow = getBody(client, server.uri("/slow?ms=300"));
            CompletableFuture.allOf(fast, slow).join();
            assertThat(fast.isDone()).isTrue();
            assertThat(slow.isDone()).isTrue();
        }
    }

    // ── 4. Chaining and combining ─────────────────────────────────────────────

    @Nested
    @DisplayName("4. Chaining & combining")
    class ChainingTest {

        @Test
        @DisplayName("thenCompose chains a dependent second request")
        void thenCompose() {
            CompletableFuture<String> chained = getBody(client, server.uri("/query?code=404"))
                    .thenCompose(first -> getBody(client, server.uri("/status/" + first.substring("code=".length()))));
            assertThat(chained.join()).isEqualTo("status 404");
        }

        @Test
        @DisplayName("thenCombine merges two independent requests")
        void thenCombine() {
            CompletableFuture<String> combined = getBody(client, server.uri("/hello"))
                    .thenCombine(getBody(client, server.uri("/json")), (a, b) -> a + " + " + b);
            assertThat(combined.join()).isEqualTo("Hello, HTTP! + {\"id\":1,\"name\":\"Ada\"}");
        }

        @Test
        @DisplayName("thenApply with a future-returning function nests the future")
        void thenApplyNests() {
            CompletableFuture<CompletableFuture<String>> nested = getBody(client, server.uri("/hello"))
                    .thenApply(body -> getBody(client, server.uri("/json")));
            assertThat(nested.join().join()).isEqualTo("{\"id\":1,\"name\":\"Ada\"}");
        }
    }

    // ── 5. Errors, timeouts, cancellation ─────────────────────────────────────

    @Nested
    @DisplayName("5. Errors, timeouts, cancellation")
    class ErrorsTest {

        @Test
        @DisplayName("exceptionally() recovers with a fallback value")
        void exceptionally() throws Exception {
            String result = getBody(client, DemoServer.deadUri("/hello")).exceptionally(error -> "fallback").join();
            assertThat(result).isEqualTo("fallback");
        }

        @Test
        @DisplayName("join() reports an IOException as the cause of a connection failure")
        void causeIsIoException() throws Exception {
            CompletableFuture<String> failed = getBody(client, DemoServer.deadUri("/hello"));
            assertThatThrownBy(failed::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("handle() sees either the value or the error")
        void handle() {
            String ok = getBody(client, server.uri("/hello"))
                    .handle((value, error) -> error == null ? "ok:" + value : "failed").join();
            assertThat(ok).isEqualTo("ok:Hello, HTTP!");
        }

        @Test
        @DisplayName("a 404 does not fail the future")
        void errorStatusIsNotAFailure() {
            assertThat(getBody(client, server.uri("/status/404")).join()).isEqualTo("status 404");
        }

        @Test
        @DisplayName("a request timeout fails the future with HttpTimeoutException")
        void timeout() {
            HttpRequest slow = HttpRequest.newBuilder(server.uri("/slow?ms=1500"))
                    .timeout(Duration.ofMillis(200))
                    .build();
            CompletableFuture<HttpResponse<String>> future = client.sendAsync(slow, HttpResponse.BodyHandlers.ofString());
            assertThatThrownBy(future::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(HttpTimeoutException.class);
        }

        @Test
        @DisplayName("cancel(true) on a pending future marks it cancelled")
        void cancel() {
            CompletableFuture<HttpResponse<String>> future = client.sendAsync(
                    HttpRequest.newBuilder(server.uri("/slow?ms=1500")).build(), HttpResponse.BodyHandlers.ofString());
            assertThat(future.cancel(true)).isTrue();
            assertThat(future.isCancelled()).isTrue();
        }
    }

    // ── 6. Custom executor ────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Custom executor")
    class CustomExecutorTest {

        @Test
        @DisplayName("the client reports the executor it was built with")
        void reportsExecutor() {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                HttpClient custom = HttpClient.newBuilder().executor(pool).build();
                assertThat(custom.executor()).containsSame(pool);
            } finally {
                pool.shutdown();
            }
        }

        @Test
        @DisplayName("requests work through a client with a custom executor")
        void works() {
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                HttpClient custom = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_1_1)
                        .executor(pool)
                        .build();
                assertThat(getBody(custom, server.uri("/hello")).join()).isEqualTo("Hello, HTTP!");
            } finally {
                pool.shutdown();
            }
        }

        @Test
        @DisplayName("a default client reports no custom executor")
        void defaultExecutor() {
            assertThat(HttpClient.newHttpClient().executor()).isEmpty();
        }
    }
}

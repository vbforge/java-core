package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AsyncRequestsTasksTest — tests for every method in {@link AsyncRequestsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code AsyncRequestsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=AsyncRequestsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("AsyncRequestsTasks — your implementations")
class AsyncRequestsTasksTest {

    private static final String JSON_BODY = "{\"id\":1,\"name\":\"Ada\"}";

    private static DemoServer server;
    private static HttpClient client;

    private AsyncRequestsTasks tasks;

    @BeforeAll
    static void startServer() throws Exception {
        server = DemoServer.start();
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    @AfterAll
    static void stopServer() {
        server.close();
    }

    @BeforeEach
    void setUp() {
        tasks = new AsyncRequestsTasks();
    }

    // ── Task 1: fetchBodyAsync ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchBodyAsync()")
    @DisplayName("Task 1 — fetchBodyAsync()")
    class FetchBodyAsyncTest {

        @Test
        @DisplayName("/hello → \"Hello, HTTP!\"")
        void basic() throws Exception {
            assertThat(tasks.fetchBodyAsync(client, server.uri("/hello")).get(5, TimeUnit.SECONDS))
                    .isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("returns immediately — the future is not done while the server is still working")
        void nonBlocking() throws Exception {
            CompletableFuture<String> future = tasks.fetchBodyAsync(client, server.uri("/slow?ms=400"));
            assertThat(future.isDone()).isFalse();
            assertThat(future.get(5, TimeUnit.SECONDS)).isEqualTo("slow done");
        }

        @Test
        @DisplayName("error statuses still deliver their body")
        void errorBody() {
            assertThat(tasks.fetchBodyAsync(client, server.uri("/status/500")).join()).isEqualTo("status 500");
        }

        @Test
        @DisplayName("a connection failure completes the future exceptionally")
        void failure() throws Exception {
            CompletableFuture<String> future = tasks.fetchBodyAsync(client, DemoServer.deadUri("/hello"));
            assertThatThrownBy(future::join).isInstanceOf(CompletionException.class);
        }
    }

    // ── Task 2: fetchAllConcurrently ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchAllConcurrently()")
    @DisplayName("Task 2 — fetchAllConcurrently()")
    class FetchAllConcurrentlyTest {

        @Test
        @DisplayName("bodies come back in input order")
        void order() {
            List<String> bodies = tasks.fetchAllConcurrently(client,
                    List.of(server.uri("/hello"), server.uri("/json"), server.uri("/lines")));
            assertThat(bodies).containsExactly("Hello, HTTP!", JSON_BODY, "alpha\nbeta\ngamma\n");
        }

        @Test
        @DisplayName("order follows the input even when an early URI is slower")
        void orderWithSlowFirst() {
            List<String> bodies = tasks.fetchAllConcurrently(client,
                    List.of(server.uri("/slow?ms=300"), server.uri("/hello")));
            assertThat(bodies).containsExactly("slow done", "Hello, HTTP!");
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.fetchAllConcurrently(client, List.of())).isEmpty();
        }

        @Test
        @DisplayName("requests really overlap — a barrier of 3 is released")
        void trulyConcurrent() {
            String id = "tasks-" + System.nanoTime();
            URI barrier = server.uri("/barrier/" + id + "?n=3");
            assertThat(tasks.fetchAllConcurrently(client, List.of(barrier, barrier, barrier)))
                    .containsExactly("released", "released", "released");
        }
    }

    // ── Task 3: fetchBodyOrFallback ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchBodyOrFallback()")
    @DisplayName("Task 3 — fetchBodyOrFallback()")
    class FetchBodyOrFallbackTest {

        @Test
        @DisplayName("success → the body")
        void success() {
            assertThat(tasks.fetchBodyOrFallback(client, server.uri("/hello"), "n/a").join())
                    .isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("404 is not a failure — its body is returned")
        void errorStatusIsNotFailure() {
            assertThat(tasks.fetchBodyOrFallback(client, server.uri("/status/404"), "n/a").join())
                    .isEqualTo("status 404");
        }

        @Test
        @DisplayName("connection refused → the fallback")
        void connectionRefused() throws Exception {
            assertThat(tasks.fetchBodyOrFallback(client, DemoServer.deadUri("/hello"), "n/a").join())
                    .isEqualTo("n/a");
        }

        @Test
        @DisplayName("the fallback value is passed through unchanged")
        void customFallback() throws Exception {
            assertThat(tasks.fetchBodyOrFallback(client, DemoServer.deadUri("/hello"), "").join()).isEmpty();
        }
    }

    // ── Task 4: fetchThenFetch ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchThenFetch()")
    @DisplayName("Task 4 — fetchThenFetch()")
    class FetchThenFetchTest {

        @Test
        @DisplayName("first body decides the second URI: code=404 → status 404")
        void dependentRequest() {
            CompletableFuture<String> result = tasks.fetchThenFetch(client,
                    server.uri("/query?code=404"),
                    body -> server.uri("/status/" + body.substring("code=".length())));
            assertThat(result.join()).isEqualTo("status 404");
        }

        @Test
        @DisplayName("a different first body gives a different second request")
        void anotherCode() {
            CompletableFuture<String> result = tasks.fetchThenFetch(client,
                    server.uri("/query?code=503"),
                    body -> server.uri("/status/" + body.substring("code=".length())));
            assertThat(result.join()).isEqualTo("status 503");
        }

        @Test
        @DisplayName("the function receives exactly the first response body")
        void functionReceivesFirstBody() {
            StringBuilder seen = new StringBuilder();
            tasks.fetchThenFetch(client, server.uri("/hello"), body -> {
                seen.append(body);
                return server.uri("/json");
            }).join();
            assertThat(seen).hasToString("Hello, HTTP!");
        }

        @Test
        @DisplayName("a failing first request fails the whole future")
        void firstFails() throws Exception {
            CompletableFuture<String> result = tasks.fetchThenFetch(client,
                    DemoServer.deadUri("/hello"), body -> server.uri("/json"));
            assertThatThrownBy(result::join).isInstanceOf(CompletionException.class);
        }
    }

    // ── Task 5: fastest ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fastest()")
    @DisplayName("Task 5 — fastest()")
    class FastestTest {

        @Test
        @DisplayName("slow first, fast second → the fast body")
        void fastSecond() {
            assertThat(tasks.fastest(client, List.of(server.uri("/slow?ms=1500"), server.uri("/hello"))).join())
                    .isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("fast first, slow second → the fast body")
        void fastFirst() {
            assertThat(tasks.fastest(client, List.of(server.uri("/hello"), server.uri("/slow?ms=1500"))).join())
                    .isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("a single URI is simply fetched")
        void single() {
            assertThat(tasks.fastest(client, List.of(server.uri("/json"))).join()).isEqualTo(JSON_BODY);
        }

        @Test
        @DisplayName("returns a future immediately — it is not done while only slow servers answer")
        void nonBlocking() {
            CompletableFuture<String> future = tasks.fastest(client,
                    List.of(server.uri("/slow?ms=400"), server.uri("/slow?ms=500")));
            assertThat(future.isDone()).isFalse();
            assertThat(future.join()).isEqualTo("slow done");
        }
    }

    // ── Task 6: fetchWithTimeout ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchWithTimeout()")
    @DisplayName("Task 6 — fetchWithTimeout()")
    class FetchWithTimeoutTest {

        @Test
        @DisplayName("response within the timeout → the body")
        void inTime() {
            assertThat(tasks.fetchWithTimeout(client, server.uri("/hello"), Duration.ofSeconds(2)).join())
                    .isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("response too slow → CompletionException caused by HttpTimeoutException")
        void tooSlow() {
            CompletableFuture<String> future =
                    tasks.fetchWithTimeout(client, server.uri("/slow?ms=1500"), Duration.ofMillis(200));
            assertThatThrownBy(future::join)
                    .isInstanceOf(CompletionException.class)
                    .hasCauseInstanceOf(HttpTimeoutException.class);
        }

        @Test
        @DisplayName("a generous timeout lets a slow response through")
        void generousTimeout() {
            assertThat(tasks.fetchWithTimeout(client, server.uri("/slow?ms=200"), Duration.ofSeconds(5)).join())
                    .isEqualTo("slow done");
        }
    }
}

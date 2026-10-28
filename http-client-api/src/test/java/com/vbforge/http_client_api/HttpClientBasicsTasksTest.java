package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpClientBasicsTasksTest — tests for every method in {@link HttpClientBasicsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code HttpClientBasicsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=HttpClientBasicsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("HttpClientBasicsTasks — your implementations")
class HttpClientBasicsTasksTest {

    private static DemoServer server;
    private static HttpClient client;

    private HttpClientBasicsTasks tasks;

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
        tasks = new HttpClientBasicsTasks();
    }

    // ── Task 1: buildClient ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement buildClient()")
    @DisplayName("Task 1 — buildClient()")
    class BuildClientTest {

        @Test
        @DisplayName("HTTP/1.1, NORMAL redirects, connect timeout 2s")
        void basicConfiguration() {
            HttpClient built = tasks.buildClient(Duration.ofSeconds(2));
            assertThat(built.version()).isEqualTo(HttpClient.Version.HTTP_1_1);
            assertThat(built.followRedirects()).isEqualTo(HttpClient.Redirect.NORMAL);
            assertThat(built.connectTimeout()).contains(Duration.ofSeconds(2));
        }

        @ParameterizedTest(name = "connect timeout {0} ms is applied")
        @CsvSource({"1", "250", "30000"})
        @DisplayName("any connect timeout is passed through")
        void timeoutIsPassedThrough(long millis) {
            assertThat(tasks.buildClient(Duration.ofMillis(millis)).connectTimeout())
                    .contains(Duration.ofMillis(millis));
        }

        @Test
        @DisplayName("every call returns a new client")
        void newInstanceEachTime() {
            assertThat(tasks.buildClient(Duration.ofSeconds(1)))
                    .isNotSameAs(tasks.buildClient(Duration.ofSeconds(1)));
        }

        @Test
        @DisplayName("the client follows a redirect (NORMAL policy in action)")
        void followsRedirect() throws Exception {
            HttpClient built = tasks.buildClient(Duration.ofSeconds(2));
            HttpResponse<String> response = built.send(
                    HttpRequest.newBuilder(server.uri("/redirect")).build(), HttpResponse.BodyHandlers.ofString());
            assertThat(response.body()).isEqualTo("Hello, HTTP!");
        }
    }

    // ── Task 2: fetchStatus ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchStatus()")
    @DisplayName("Task 2 — fetchStatus()")
    class FetchStatusTest {

        @Test
        @DisplayName("/hello → 200")
        void ok() throws Exception {
            assertThat(tasks.fetchStatus(client, server.uri("/hello"))).isEqualTo(200);
        }

        @ParameterizedTest(name = "/status/{0} → {0}")
        @CsvSource({"201", "204", "404", "500", "503"})
        @DisplayName("any status is returned, never thrown")
        void anyStatus(int code) throws Exception {
            assertThat(tasks.fetchStatus(client, server.uri("/status/" + code))).isEqualTo(code);
        }

        @Test
        @DisplayName("a redirect is not followed by this client — 302 is returned")
        void redirectNotFollowed() throws Exception {
            assertThat(tasks.fetchStatus(client, server.uri("/redirect"))).isEqualTo(302);
        }
    }

    // ── Task 3: fetchBody ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchBody()")
    @DisplayName("Task 3 — fetchBody()")
    class FetchBodyTest {

        @Test
        @DisplayName("/hello → \"Hello, HTTP!\"")
        void hello() throws Exception {
            assertThat(tasks.fetchBody(client, server.uri("/hello"))).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("multi-line body keeps its line breaks")
        void multiLine() throws Exception {
            assertThat(tasks.fetchBody(client, server.uri("/lines"))).isEqualTo("alpha\nbeta\ngamma\n");
        }

        @Test
        @DisplayName("error responses still return their body")
        void errorBody() throws Exception {
            assertThat(tasks.fetchBody(client, server.uri("/status/404"))).isEqualTo("status 404");
        }

        @Test
        @DisplayName("empty body → empty string, not null")
        void emptyBody() throws Exception {
            assertThat(tasks.fetchBody(client, server.uri("/query"))).isEmpty();
        }
    }

    // ── Task 4: fetchHeader ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchHeader()")
    @DisplayName("Task 4 — fetchHeader()")
    class FetchHeaderTest {

        @Test
        @DisplayName("X-Server → Optional[demo-server]")
        void present() throws Exception {
            assertThat(tasks.fetchHeader(client, server.uri("/hello"), "X-Server")).contains("demo-server");
        }

        @Test
        @DisplayName("lookup is case-insensitive")
        void caseInsensitive() throws Exception {
            assertThat(tasks.fetchHeader(client, server.uri("/hello"), "x-SERVER")).contains("demo-server");
        }

        @Test
        @DisplayName("absent header → Optional.empty")
        void absent() throws Exception {
            assertThat(tasks.fetchHeader(client, server.uri("/hello"), "X-Missing")).isEmpty();
        }

        @Test
        @DisplayName("works on any response, e.g. Content-Type of /json")
        void contentType() throws Exception {
            assertThat(tasks.fetchHeader(client, server.uri("/json"), "Content-Type"))
                    .contains("application/json");
        }
    }

    // ── Task 5: postText ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement postText()")
    @DisplayName("Task 5 — postText()")
    class PostTextTest {

        @Test
        @DisplayName("\"ping\" is echoed back")
        void basic() throws Exception {
            assertThat(tasks.postText(client, server.uri("/echo"), "ping")).isEqualTo("ping");
        }

        @Test
        @DisplayName("empty text → empty response")
        void empty() throws Exception {
            assertThat(tasks.postText(client, server.uri("/echo"), "")).isEmpty();
        }

        @Test
        @DisplayName("non-ASCII text survives a UTF-8 round trip")
        void unicode() throws Exception {
            assertThat(tasks.postText(client, server.uri("/echo"), "héllo wörld ✓")).isEqualTo("héllo wörld ✓");
        }

        @Test
        @DisplayName("line breaks are preserved")
        void multiLine() throws Exception {
            assertThat(tasks.postText(client, server.uri("/echo"), "a\nb\n")).isEqualTo("a\nb\n");
        }
    }
}

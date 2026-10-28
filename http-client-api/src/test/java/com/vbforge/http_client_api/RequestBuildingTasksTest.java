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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * RequestBuildingTasksTest — tests for every method in {@link RequestBuildingTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code RequestBuildingTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=RequestBuildingTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("RequestBuildingTasks — your implementations")
class RequestBuildingTasksTest {

    private static DemoServer server;
    private static HttpClient client;

    private RequestBuildingTasks tasks;

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
        tasks = new RequestBuildingTasks();
    }

    /** Builds an insertion-ordered map from alternating key, value arguments. */
    private static Map<String, String> ordered(String... keysAndValues) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }

    // ── Task 1: buildGet ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement buildGet()")
    @DisplayName("Task 1 — buildGet()")
    class BuildGetTest {

        private final URI uri = URI.create("http://localhost:8080/api");

        @Test
        @DisplayName("GET with Accept and X-Trace headers")
        void withHeaders() {
            HttpRequest request = tasks.buildGet(uri, ordered("Accept", "application/json", "X-Trace", "42"));
            assertThat(request.method()).isEqualTo("GET");
            assertThat(request.uri()).isEqualTo(uri);
            assertThat(request.headers().firstValue("Accept")).contains("application/json");
            assertThat(request.headers().firstValue("X-Trace")).contains("42");
        }

        @Test
        @DisplayName("empty header map → request without headers")
        void noHeaders() {
            HttpRequest request = tasks.buildGet(uri, Map.of());
            assertThat(request.method()).isEqualTo("GET");
            assertThat(request.headers().map()).isEmpty();
        }

        @Test
        @DisplayName("a GET request has no body")
        void noBody() {
            assertThat(tasks.buildGet(uri, Map.of()).bodyPublisher()).isEmpty();
        }

        @Test
        @DisplayName("the built request can be sent and carries its headers to the server")
        void headersReachServer() throws Exception {
            HttpRequest request = tasks.buildGet(server.uri("/echo-header/X-Trace"), ordered("X-Trace", "abc-123"));
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.body()).isEqualTo("abc-123");
        }
    }

    // ── Task 2: buildJsonPost ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement buildJsonPost()")
    @DisplayName("Task 2 — buildJsonPost()")
    class BuildJsonPostTest {

        private final URI uri = URI.create("http://localhost:8080/api");

        @Test
        @DisplayName("POST with JSON Content-Type and Accept")
        void headersAndMethod() {
            HttpRequest request = tasks.buildJsonPost(uri, "{\"a\":1}");
            assertThat(request.method()).isEqualTo("POST");
            assertThat(request.uri()).isEqualTo(uri);
            assertThat(request.headers().allValues("Content-Type")).containsExactly("application/json");
            assertThat(request.headers().allValues("Accept")).containsExactly("application/json");
        }

        @Test
        @DisplayName("body length for {\"a\":1} is 7 bytes")
        void asciiBodyLength() {
            HttpRequest request = tasks.buildJsonPost(uri, "{\"a\":1}");
            assertThat(request.bodyPublisher()).isPresent();
            assertThat(request.bodyPublisher().get().contentLength()).isEqualTo(7);
        }

        @Test
        @DisplayName("body length counts UTF-8 bytes, not characters")
        void utf8BodyLength() {
            String json = "{\"n\":\"é\"}";
            HttpRequest request = tasks.buildJsonPost(uri, json);
            assertThat(request.bodyPublisher().get().contentLength())
                    .isEqualTo(json.getBytes(StandardCharsets.UTF_8).length)
                    .isEqualTo(10);
        }

        @Test
        @DisplayName("the strict server accepts it and echoes the JSON back")
        void acceptedByServer() throws Exception {
            String json = "{\"name\":\"Ada\"}";
            HttpResponse<String> response = client.send(tasks.buildJsonPost(server.uri("/json-echo"), json),
                    HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo(json);
        }
    }

    // ── Task 3: buildFormPost ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement buildFormPost()")
    @DisplayName("Task 3 — buildFormPost()")
    class BuildFormPostTest {

        private String send(Map<String, String> fields) throws Exception {
            HttpRequest request = tasks.buildFormPost(server.uri("/form"), fields);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);   // 415 means the Content-Type was wrong
            return response.body();
        }

        @Test
        @DisplayName("name=Ada Lovelace & lang=en → name=Ada+Lovelace&lang=en")
        void basic() throws Exception {
            assertThat(send(ordered("name", "Ada Lovelace", "lang", "en"))).isEqualTo("name=Ada+Lovelace&lang=en");
        }

        @Test
        @DisplayName("& and = inside values are percent-encoded")
        void specialCharacters() throws Exception {
            assertThat(send(ordered("q", "a&b=c"))).isEqualTo("q=a%26b%3Dc");
        }

        @Test
        @DisplayName("names are encoded too, non-ASCII uses UTF-8")
        void namesAndUnicode() throws Exception {
            assertThat(send(ordered("first name", "café"))).isEqualTo("first+name=caf%C3%A9");
        }

        @Test
        @DisplayName("empty map → empty body of length 0")
        void emptyFields() {
            HttpRequest request = tasks.buildFormPost(URI.create("http://localhost/"), Map.of());
            assertThat(request.method()).isEqualTo("POST");
            assertThat(request.bodyPublisher().get().contentLength()).isZero();
        }

        @Test
        @DisplayName("Content-Type is application/x-www-form-urlencoded")
        void contentType() {
            HttpRequest request = tasks.buildFormPost(URI.create("http://localhost/"), ordered("a", "1"));
            assertThat(request.headers().firstValue("Content-Type")).contains("application/x-www-form-urlencoded");
        }
    }

    // ── Task 4: buildUri ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement buildUri()")
    @DisplayName("Task 4 — buildUri()")
    class BuildUriTest {

        private static final String BASE = "http://localhost/search";

        @Test
        @DisplayName("q=java http & lang=en → ?q=java+http&lang=en")
        void basic() {
            URI uri = tasks.buildUri(BASE, ordered("q", "java http", "lang", "en"));
            assertThat(uri.toString()).isEqualTo("http://localhost/search?q=java+http&lang=en");
        }

        @Test
        @DisplayName("non-ASCII values are UTF-8 percent-encoded")
        void unicode() {
            assertThat(tasks.buildUri(BASE, ordered("q", "café")).toString())
                    .isEqualTo("http://localhost/search?q=caf%C3%A9");
        }

        @Test
        @DisplayName("no parameters → the base, without a trailing '?'")
        void noParams() {
            assertThat(tasks.buildUri(BASE, Map.of()).toString()).isEqualTo(BASE);
        }

        @Test
        @DisplayName("& and = in a value cannot break the query structure")
        void separatorsInValue() {
            assertThat(tasks.buildUri(BASE, ordered("q", "a&b=c")).toString())
                    .isEqualTo("http://localhost/search?q=a%26b%3Dc");
        }

        @Test
        @DisplayName("parameter order follows the map's iteration order")
        void order() {
            assertThat(tasks.buildUri(BASE, ordered("b", "2", "a", "1")).getRawQuery()).isEqualTo("b=2&a=1");
        }

        @Test
        @DisplayName("the server receives exactly the encoded query")
        void roundTrip() throws Exception {
            URI uri = tasks.buildUri(server.uri("/query").toString(), ordered("q", "a b&c", "n", "1"));
            HttpResponse<String> response = client.send(HttpRequest.newBuilder(uri).build(),
                    HttpResponse.BodyHandlers.ofString());
            assertThat(response.body()).isEqualTo("q=a+b%26c&n=1");
        }
    }

    // ── Task 5: buildGetWithTimeout ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement buildGetWithTimeout()")
    @DisplayName("Task 5 — buildGetWithTimeout()")
    class BuildGetWithTimeoutTest {

        private final URI uri = URI.create("http://localhost:8080/api");

        @Test
        @DisplayName("GET with a 2-second request timeout")
        void basic() {
            HttpRequest request = tasks.buildGetWithTimeout(uri, Duration.ofSeconds(2));
            assertThat(request.method()).isEqualTo("GET");
            assertThat(request.uri()).isEqualTo(uri);
            assertThat(request.timeout()).contains(Duration.ofSeconds(2));
        }

        @ParameterizedTest(name = "timeout {0} ms is stored as-is")
        @CsvSource({"1", "200", "60000"})
        @DisplayName("any timeout is stored unchanged")
        void anyTimeout(long millis) {
            assertThat(tasks.buildGetWithTimeout(uri, Duration.ofMillis(millis)).timeout())
                    .contains(Duration.ofMillis(millis));
        }

        @Test
        @DisplayName("the timeout really fires against a slow endpoint")
        void firesAgainstSlowServer() {
            HttpRequest request = tasks.buildGetWithTimeout(server.uri("/slow?ms=1500"), Duration.ofMillis(200));
            assertThatThrownBy(() -> client.send(request, HttpResponse.BodyHandlers.ofString()))
                    .isInstanceOf(HttpTimeoutException.class);
        }
    }
}

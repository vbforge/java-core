package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpClientBasicsDemoTest — verifies every concept shown in {@link HttpClientBasicsDemo}.
 *
 * <p>These tests are always GREEN. They talk only to a local {@link DemoServer}.
 */
@DisplayName("HttpClientBasicsDemo — API contracts")
class HttpClientBasicsDemoTest {

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

    // ── 1. Creating an HttpClient ─────────────────────────────────────────────

    @Nested
    @DisplayName("1. Creating an HttpClient")
    class ClientCreationTest {

        @Test
        @DisplayName("defaults: HTTP/2, redirects NEVER, no connect timeout")
        void defaults() {
            HttpClient defaults = HttpClient.newHttpClient();
            assertThat(defaults.version()).isEqualTo(HttpClient.Version.HTTP_2);
            assertThat(defaults.followRedirects()).isEqualTo(HttpClient.Redirect.NEVER);
            assertThat(defaults.connectTimeout()).isEmpty();
        }

        @Test
        @DisplayName("the builder overrides every default")
        void builder() {
            HttpClient custom = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(2))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            assertThat(custom.version()).isEqualTo(HttpClient.Version.HTTP_1_1);
            assertThat(custom.followRedirects()).isEqualTo(HttpClient.Redirect.NORMAL);
            assertThat(custom.connectTimeout()).contains(Duration.ofSeconds(2));
        }

        @Test
        @DisplayName("every build() produces a new client instance")
        void newInstances() {
            assertThat(HttpClient.newHttpClient()).isNotSameAs(HttpClient.newHttpClient());
        }
    }

    // ── 2. Synchronous GET ────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Synchronous GET")
    class SyncGetTest {

        @Test
        @DisplayName("send() returns status and body")
        void statusAndBody() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("the response remembers the request and negotiated version")
        void requestAndVersion() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.request().method()).isEqualTo("GET");   // GET is the default method
            assertThat(response.uri()).isEqualTo(server.uri("/hello"));
            assertThat(response.version()).isEqualTo(HttpClient.Version.HTTP_1_1);
        }

        @Test
        @DisplayName("a 404 is a normal response, not an exception")
        void errorStatusDoesNotThrow() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/status/404")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(404);
        }
    }

    // ── 3. Response headers ───────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Response headers")
    class ResponseHeadersTest {

        private HttpResponse<String> hello() throws Exception {
            return client.send(HttpRequest.newBuilder(server.uri("/hello")).build(),
                    HttpResponse.BodyHandlers.ofString());
        }

        @Test
        @DisplayName("header names are case-insensitive")
        void caseInsensitive() throws Exception {
            HttpResponse<String> response = hello();
            assertThat(response.headers().firstValue("x-server")).contains("demo-server");
            assertThat(response.headers().firstValue("X-SERVER")).contains("demo-server");
        }

        @Test
        @DisplayName("a missing header is Optional.empty, not an exception")
        void missingHeader() throws Exception {
            assertThat(hello().headers().firstValue("X-Nope")).isEmpty();
        }

        @Test
        @DisplayName("firstValueAsLong parses numeric headers")
        void numericHeader() throws Exception {
            assertThat(hello().headers().firstValueAsLong("content-length")).hasValue(12);
        }

        @Test
        @DisplayName("allValues returns every value of a header")
        void allValues() throws Exception {
            assertThat(hello().headers().allValues("x-server")).containsExactly("demo-server");
            assertThat(hello().headers().allValues("x-nope")).isEmpty();
        }

        @Test
        @DisplayName("map() exposes the headers as a multimap")
        void asMap() throws Exception {
            assertThat(hello().headers().map()).containsKey("content-type");
        }
    }

    // ── 4. POST with a body ───────────────────────────────────────────────────

    @Nested
    @DisplayName("4. POST with a body")
    class PostTest {

        @Test
        @DisplayName("ofString body is delivered to the server")
        void bodyDelivered() throws Exception {
            HttpRequest post = HttpRequest.newBuilder(server.uri("/echo"))
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString("ping"))
                    .build();
            HttpResponse<String> response = client.send(post, HttpResponse.BodyHandlers.ofString());
            assertThat(response.body()).isEqualTo("ping");
            assertThat(response.headers().firstValue("X-Method")).contains("POST");
        }

        @Test
        @DisplayName("noBody() sends an empty body")
        void emptyBody() throws Exception {
            HttpRequest post = HttpRequest.newBuilder(server.uri("/echo"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            assertThat(client.send(post, HttpResponse.BodyHandlers.ofString()).body()).isEmpty();
        }

        @Test
        @DisplayName("ofString encodes as UTF-8 and round-trips non-ASCII text")
        void utf8RoundTrip() throws Exception {
            HttpRequest post = HttpRequest.newBuilder(server.uri("/echo"))
                    .POST(HttpRequest.BodyPublishers.ofString("héllo wörld ✓"))
                    .build();
            assertThat(client.send(post, HttpResponse.BodyHandlers.ofString()).body()).isEqualTo("héllo wörld ✓");
        }

        @Test
        @DisplayName("the publisher knows its content length in bytes")
        void contentLength() {
            assertThat(HttpRequest.BodyPublishers.ofString("héllo").contentLength()).isEqualTo(6);
        }
    }

    // ── 5. Body handlers ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Body handlers")
    class BodyHandlersTest {

        private final HttpRequest hello = HttpRequest.newBuilder(server.uri("/hello")).build();

        @Test
        @DisplayName("ofString() yields a String")
        void asString() throws Exception {
            assertThat(client.send(hello, HttpResponse.BodyHandlers.ofString()).body()).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("ofByteArray() yields the raw bytes")
        void asBytes() throws Exception {
            byte[] body = client.send(hello, HttpResponse.BodyHandlers.ofByteArray()).body();
            assertThat(body).hasSize(12);
            assertThat(new String(body, java.nio.charset.StandardCharsets.UTF_8)).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("discarding() keeps the status but body() is null")
        void discarding() throws Exception {
            HttpResponse<Void> response = client.send(hello, HttpResponse.BodyHandlers.discarding());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNull();
        }
    }
}

package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * RequestBuildingDemoTest — verifies every concept shown in {@link RequestBuildingDemo}.
 *
 * <p>These tests are always GREEN. They talk only to a local {@link DemoServer}.
 */
@DisplayName("RequestBuildingDemo — API contracts")
class RequestBuildingDemoTest {

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

    // ── 1. Anatomy of a request ───────────────────────────────────────────────

    @Nested
    @DisplayName("1. Anatomy of a request")
    class AnatomyTest {

        private final URI uri = URI.create("http://localhost:8080/api");

        @Test
        @DisplayName("header() adds values, setHeader() replaces them")
        void headerVsSetHeader() {
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Accept", "application/json")
                    .header("Accept", "text/plain")
                    .setHeader("X-Trace", "first")
                    .setHeader("X-Trace", "second")
                    .build();
            assertThat(request.headers().allValues("Accept")).containsExactly("application/json", "text/plain");
            assertThat(request.headers().allValues("X-Trace")).containsExactly("second");
        }

        @Test
        @DisplayName("method defaults to GET and has no body publisher")
        void defaultsToGet() {
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            assertThat(request.method()).isEqualTo("GET");
            assertThat(request.bodyPublisher()).isEmpty();
        }

        @Test
        @DisplayName("timeout and version are optional and reported as Optionals")
        void timeoutAndVersion() {
            HttpRequest plain = HttpRequest.newBuilder(uri).build();
            assertThat(plain.timeout()).isEmpty();
            assertThat(plain.version()).isEmpty();

            HttpRequest tuned = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(5))
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();
            assertThat(tuned.timeout()).contains(Duration.ofSeconds(5));
            assertThat(tuned.version()).contains(HttpClient.Version.HTTP_1_1);
        }

        @Test
        @DisplayName("restricted headers such as Content-Length are rejected")
        void restrictedHeader() {
            assertThatThrownBy(() -> HttpRequest.newBuilder(uri).header("Content-Length", "5").build())
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("copy() gives an independent builder")
        void copyIsIndependent() {
            HttpRequest.Builder base = HttpRequest.newBuilder().header("Accept", "application/json");
            HttpRequest plain = base.copy().uri(uri).build();
            HttpRequest extended = base.copy().uri(uri).header("X-Extra", "1").build();
            assertThat(plain.headers().firstValue("X-Extra")).isEmpty();
            assertThat(extended.headers().firstValue("X-Extra")).contains("1");
            assertThat(plain.headers().firstValue("Accept")).contains("application/json");
        }
    }

    // ── 2. Query strings and URL encoding ─────────────────────────────────────

    @Nested
    @DisplayName("2. Query strings & URL encoding")
    class QueryStringTest {

        @ParameterizedTest(name = "encode({0}) = {1}")
        @CsvSource({
                "'java http', java+http",
                "'a&b=c',     a%26b%3Dc",
                "'café',      caf%C3%A9",
                "plain,       plain",
        })
        @DisplayName("URLEncoder form-encodes values as UTF-8")
        void encode(String raw, String expected) {
            assertThat(RequestBuildingDemo.encode(raw)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a raw space makes URI.create throw")
        void rawSpaceIsIllegal() {
            assertThatThrownBy(() -> URI.create("http://localhost/search?q=java http"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the server receives the encoded query string untouched")
        void serverSeesEncodedQuery() throws Exception {
            String query = "q=" + RequestBuildingDemo.encode("a&b=c") + "&lang=en";
            HttpRequest request = HttpRequest.newBuilder(server.uri("/query?" + query)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.body()).isEqualTo("q=a%26b%3Dc&lang=en");
        }
    }

    // ── 3. Body publishers ────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Body publishers")
    class BodyPublishersTest {

        @Test
        @DisplayName("contentLength counts bytes, not characters")
        void bytesNotChars() {
            assertThat(HttpRequest.BodyPublishers.ofString("hello").contentLength()).isEqualTo(5);
            assertThat(HttpRequest.BodyPublishers.ofString("héllo").contentLength()).isEqualTo(6);
        }

        @Test
        @DisplayName("ofByteArray and noBody report their length")
        void byteArrayAndNoBody() {
            assertThat(HttpRequest.BodyPublishers.ofByteArray(new byte[]{1, 2, 3}).contentLength()).isEqualTo(3);
            assertThat(HttpRequest.BodyPublishers.noBody().contentLength()).isZero();
        }

        @Test
        @DisplayName("GET has no body publisher, POST(noBody()) has an empty one")
        void getVsEmptyPost() {
            URI uri = URI.create("http://localhost/");
            assertThat(HttpRequest.newBuilder(uri).GET().build().bodyPublisher()).isEmpty();
            HttpRequest post = HttpRequest.newBuilder(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
            assertThat(post.bodyPublisher()).isPresent();
            assertThat(post.bodyPublisher().get().contentLength()).isZero();
        }
    }

    // ── 4. Content-Type ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Content-Type: JSON and form bodies")
    class ContentTypeTest {

        private static final String JSON = "{\"name\":\"Ada\"}";

        @Test
        @DisplayName("JSON body with the right Content-Type is accepted")
        void jsonAccepted() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/json-echo"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo(JSON);
        }

        @Test
        @DisplayName("JSON body without Content-Type is rejected with 415")
        void jsonRejectedWithoutContentType() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/json-echo"))
                    .POST(HttpRequest.BodyPublishers.ofString(JSON))
                    .build();
            assertThat(client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode()).isEqualTo(415);
        }

        @Test
        @DisplayName("form body is accepted with the urlencoded Content-Type")
        void formAccepted() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/form"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("name=Ada+Lovelace&lang=en"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("name=Ada+Lovelace&lang=en");
        }

        @Test
        @DisplayName("Authorization is an ordinary request header")
        void authorizationHeader() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/echo-header/Authorization"))
                    .header("Authorization", "Bearer token123")
                    .build();
            assertThat(client.send(request, HttpResponse.BodyHandlers.ofString()).body()).isEqualTo("Bearer token123");
        }
    }

    // ── 5. HTTP methods ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. HTTP methods")
    class HttpMethodsTest {

        @ParameterizedTest(name = "{0} reaches the server as {0}")
        @CsvSource({"GET", "POST", "PUT", "DELETE", "PATCH"})
        @DisplayName("every verb is sent as requested")
        void verbIsSent(String method) throws Exception {
            HttpRequest.BodyPublisher body = method.equals("GET") || method.equals("DELETE")
                    ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString("p");
            HttpRequest request = HttpRequest.newBuilder(server.uri("/echo")).method(method, body).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.headers().firstValue("X-Method")).contains(method);
        }

        @Test
        @DisplayName("the shortcut methods set the matching verb")
        void shortcuts() {
            URI uri = URI.create("http://localhost/");
            HttpRequest.BodyPublisher p = HttpRequest.BodyPublishers.ofString("p");
            assertThat(HttpRequest.newBuilder(uri).GET().build().method()).isEqualTo("GET");
            assertThat(HttpRequest.newBuilder(uri).POST(p).build().method()).isEqualTo("POST");
            assertThat(HttpRequest.newBuilder(uri).PUT(p).build().method()).isEqualTo("PUT");
            assertThat(HttpRequest.newBuilder(uri).DELETE().build().method()).isEqualTo("DELETE");
        }
    }
}

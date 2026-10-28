package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ResponseHandlingDemoTest — verifies every concept shown in {@link ResponseHandlingDemo}.
 *
 * <p>These tests are always GREEN. They talk only to a local {@link DemoServer}.
 */
@DisplayName("ResponseHandlingDemo — API contracts")
class ResponseHandlingDemoTest {

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

    private static HttpRequest get(String path) {
        return HttpRequest.newBuilder(server.uri(path)).build();
    }

    // ── 1. Status codes ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Status codes")
    class StatusCodesTest {

        @ParameterizedTest(name = "/status/{0} → statusCode {0}, success={1}")
        @CsvSource({"200,true", "204,true", "404,false", "500,false"})
        @DisplayName("every status is returned, never thrown")
        void statusIsReturned(int code, boolean success) throws Exception {
            HttpResponse<String> response = client.send(get("/status/" + code), HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(code);
            assertThat(ResponseHandlingDemo.isSuccess(response.statusCode())).isEqualTo(success);
        }

        @Test
        @DisplayName("error responses still carry a body; 204 has none")
        void bodies() throws Exception {
            assertThat(client.send(get("/status/404"), HttpResponse.BodyHandlers.ofString()).body())
                    .isEqualTo("status 404");
            assertThat(client.send(get("/status/204"), HttpResponse.BodyHandlers.ofString()).body()).isEmpty();
        }
    }

    // ── 2. Redirects ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Redirects")
    class RedirectsTest {

        @Test
        @DisplayName("NEVER (default) hands you the 302 and its Location header")
        void neverFollows() throws Exception {
            HttpResponse<String> response = client.send(get("/redirect"), HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(302);
            assertThat(response.headers().firstValue("Location")).contains(server.uri("/hello").toString());
        }

        @Test
        @DisplayName("NORMAL follows the redirect to the final resource")
        void normalFollows() throws Exception {
            HttpClient following = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpResponse<String> response = following.send(get("/redirect"), HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("Hello, HTTP!");
            assertThat(response.uri()).isEqualTo(server.uri("/hello"));
        }

        @Test
        @DisplayName("previousResponse() exposes the redirect hop")
        void previousResponse() throws Exception {
            HttpClient following = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpResponse<String> response = following.send(get("/redirect"), HttpResponse.BodyHandlers.ofString());
            assertThat(response.previousResponse()).isPresent();
            assertThat(response.previousResponse().get().statusCode()).isEqualTo(302);
        }
    }

    // ── 3. Body handlers ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Body handlers")
    class BodyHandlersTest {

        @Test
        @DisplayName("ofLines() delivers a lazy Stream of lines")
        void ofLines() throws Exception {
            HttpResponse<Stream<String>> response = client.send(get("/lines"), HttpResponse.BodyHandlers.ofLines());
            assertThat(response.body().collect(Collectors.toList())).containsExactly("alpha", "beta", "gamma");
        }

        @Test
        @DisplayName("ofFile(path) streams the body into a file")
        void ofFile(@TempDir Path dir) throws Exception {
            Path file = dir.resolve("out.txt");
            HttpResponse<Path> response = client.send(get("/hello"), HttpResponse.BodyHandlers.ofFile(file));
            assertThat(response.body()).isEqualTo(file);
            assertThat(Files.readString(file)).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("ofFile(path) does NOT truncate — stale trailing bytes survive")
        void ofFileDoesNotTruncate(@TempDir Path dir) throws Exception {
            Path file = dir.resolve("out.txt");
            Files.writeString(file, "a much longer stale content that is already in the file");
            client.send(get("/hello"), HttpResponse.BodyHandlers.ofFile(file));
            assertThat(Files.readString(file)).startsWith("Hello, HTTP!").isNotEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("ofFile with TRUNCATE_EXISTING replaces the whole file")
        void ofFileTruncating(@TempDir Path dir) throws Exception {
            Path file = dir.resolve("out.txt");
            Files.writeString(file, "a much longer stale content that is already in the file");
            client.send(get("/hello"), HttpResponse.BodyHandlers.ofFile(file,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
            assertThat(Files.readString(file)).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("ofInputStream() gives you a stream you must close")
        void ofInputStream() throws Exception {
            HttpResponse<InputStream> response = client.send(get("/lines"), HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = response.body()) {
                assertThat(in.readAllBytes()).hasSize(17);
            }
        }

        @Test
        @DisplayName("ofByteArray() keeps every byte")
        void ofByteArray() throws Exception {
            assertThat(client.send(get("/lines"), HttpResponse.BodyHandlers.ofByteArray()).body()).hasSize(17);
        }
    }

    // ── 4. Custom body handler ────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Custom body handler")
    class CustomBodyHandlerTest {

        @Test
        @DisplayName("BodySubscribers.mapping converts the body inside the client")
        void mapping() throws Exception {
            HttpResponse.BodyHandler<Integer> lengthHandler = info -> HttpResponse.BodySubscribers.mapping(
                    HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8), String::length);
            HttpResponse<Integer> response = client.send(get("/hello"), lengthHandler);
            assertThat(response.body()).isEqualTo(12);
        }

        @Test
        @DisplayName("the handler receives status and headers before the body")
        void handlerSeesResponseInfo() throws Exception {
            HttpResponse.BodyHandler<String> statusHandler = info -> HttpResponse.BodySubscribers.mapping(
                    HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8),
                    body -> info.statusCode() + ":" + body);
            assertThat(client.send(get("/status/404"), statusHandler).body()).isEqualTo("404:status 404");
        }
    }

    // ── 5. Failures ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Failures")
    class FailuresTest {

        @Test
        @DisplayName("connection refused surfaces as an IOException")
        void connectionRefused() throws Exception {
            HttpRequest request = HttpRequest.newBuilder(DemoServer.deadUri("/hello")).build();
            assertThatThrownBy(() -> client.send(request, HttpResponse.BodyHandlers.ofString()))
                    .isInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("an elapsed request timeout throws HttpTimeoutException")
        void requestTimeout() {
            HttpRequest slow = HttpRequest.newBuilder(server.uri("/slow?ms=1500"))
                    .timeout(Duration.ofMillis(200))
                    .build();
            assertThatThrownBy(() -> client.send(slow, HttpResponse.BodyHandlers.ofString()))
                    .isInstanceOf(HttpTimeoutException.class);
        }
    }
}

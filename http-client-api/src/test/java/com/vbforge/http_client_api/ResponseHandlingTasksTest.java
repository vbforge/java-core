package com.vbforge.http_client_api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ResponseHandlingTasksTest — tests for every method in {@link ResponseHandlingTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ResponseHandlingTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ResponseHandlingTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ResponseHandlingTasks — your implementations")
class ResponseHandlingTasksTest {

    private static DemoServer server;
    /** A client with the default redirect policy (NEVER). */
    private static HttpClient client;

    private ResponseHandlingTasks tasks;

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
        tasks = new ResponseHandlingTasks();
    }

    // ── Task 1: classifyStatus ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement classifyStatus()")
    @DisplayName("Task 1 — classifyStatus()")
    class ClassifyStatusTest {

        @ParameterizedTest(name = "/status/{0} → {1}")
        @CsvSource({
                "200, SUCCESS",
                "204, SUCCESS",
                "299, SUCCESS",
                "301, REDIRECT",
                "302, REDIRECT",
                "400, CLIENT_ERROR",
                "404, CLIENT_ERROR",
                "500, SERVER_ERROR",
                "503, SERVER_ERROR",
        })
        @DisplayName("status code → class name")
        void classification(int code, String expected) throws Exception {
            assertThat(tasks.classifyStatus(client, server.uri("/status/" + code))).isEqualTo(expected);
        }

        @Test
        @DisplayName("a real redirect endpoint is classified, not followed")
        void realRedirect() throws Exception {
            assertThat(tasks.classifyStatus(client, server.uri("/redirect"))).isEqualTo("REDIRECT");
        }
    }

    // ── Task 2: fetchFollowingRedirects ───────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchFollowingRedirects()")
    @DisplayName("Task 2 — fetchFollowingRedirects()")
    class FetchFollowingRedirectsTest {

        @Test
        @DisplayName("no redirect → plain body")
        void noRedirect() throws Exception {
            assertThat(tasks.fetchFollowingRedirects(server.uri("/hello"))).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("302 → /hello is followed and the final body returned")
        void followsRedirect() throws Exception {
            assertThat(tasks.fetchFollowingRedirects(server.uri("/redirect"))).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("a non-redirect error is returned as is, not retried")
        void errorBody() throws Exception {
            assertThat(tasks.fetchFollowingRedirects(server.uri("/status/404"))).isEqualTo("status 404");
        }
    }

    // ── Task 3: fetchLines ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchLines()")
    @DisplayName("Task 3 — fetchLines()")
    class FetchLinesTest {

        @Test
        @DisplayName("/lines → [alpha, beta, gamma]")
        void basic() throws Exception {
            assertThat(tasks.fetchLines(client, server.uri("/lines"))).containsExactly("alpha", "beta", "gamma");
        }

        @Test
        @DisplayName("single-line body → one element")
        void singleLine() throws Exception {
            assertThat(tasks.fetchLines(client, server.uri("/hello"))).containsExactly("Hello, HTTP!");
        }

        @Test
        @DisplayName("empty body → empty list")
        void emptyBody() throws Exception {
            assertThat(tasks.fetchLines(client, server.uri("/query"))).isEmpty();
        }
    }

    // ── Task 4: downloadToFile ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement downloadToFile()")
    @DisplayName("Task 4 — downloadToFile()")
    class DownloadToFileTest {

        @Test
        @DisplayName("writes the body into a new file and returns its path")
        void newFile(@TempDir Path dir) throws Exception {
            Path target = dir.resolve("hello.txt");
            Path result = tasks.downloadToFile(client, server.uri("/hello"), target);
            assertThat(result).isEqualTo(target);
            assertThat(Files.readString(target)).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("a longer existing file is completely replaced — no stale bytes")
        void overwritesLongerFile(@TempDir Path dir) throws Exception {
            Path target = dir.resolve("hello.txt");
            Files.writeString(target, "a much longer stale content that is already in the file");
            tasks.downloadToFile(client, server.uri("/hello"), target);
            assertThat(Files.readString(target)).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("multi-line content is written unchanged")
        void multiLine(@TempDir Path dir) throws Exception {
            Path target = dir.resolve("lines.txt");
            tasks.downloadToFile(client, server.uri("/lines"), target);
            assertThat(Files.readString(target)).isEqualTo("alpha\nbeta\ngamma\n");
        }

        @Test
        @DisplayName("an empty body produces an empty file")
        void emptyBody(@TempDir Path dir) throws Exception {
            Path target = dir.resolve("empty.txt");
            tasks.downloadToFile(client, server.uri("/query"), target);
            assertThat(Files.exists(target)).isTrue();
            assertThat(Files.size(target)).isZero();
        }
    }

    // ── Task 5: fetchOrDefault ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchOrDefault()")
    @DisplayName("Task 5 — fetchOrDefault()")
    class FetchOrDefaultTest {

        @Test
        @DisplayName("200 → the body")
        void success() {
            assertThat(tasks.fetchOrDefault(client, server.uri("/hello"), "n/a")).isEqualTo("Hello, HTTP!");
        }

        @Test
        @DisplayName("any 2xx counts as success — 201 returns its body")
        void created() {
            assertThat(tasks.fetchOrDefault(client, server.uri("/status/201"), "n/a")).isEqualTo("status 201");
        }

        @ParameterizedTest(name = "/status/{0} → fallback")
        @CsvSource({"400", "404", "500", "503"})
        @DisplayName("non-2xx → the fallback")
        void errorStatus(int code) {
            assertThat(tasks.fetchOrDefault(client, server.uri("/status/" + code), "n/a")).isEqualTo("n/a");
        }

        @Test
        @DisplayName("connection refused → the fallback, no exception")
        void connectionRefused() throws Exception {
            assertThat(tasks.fetchOrDefault(client, DemoServer.deadUri("/hello"), "n/a")).isEqualTo("n/a");
        }

        @Test
        @DisplayName("an interrupted thread → the fallback, interrupt flag restored")
        void interrupted() {
            Thread.currentThread().interrupt();
            try {
                assertThat(tasks.fetchOrDefault(client, server.uri("/slow?ms=1500"), "n/a")).isEqualTo("n/a");
                assertThat(Thread.currentThread().isInterrupted()).isTrue();
            } finally {
                Thread.interrupted();   // clear the flag so later tests are unaffected
            }
        }
    }

    // ── Task 6: fetchBodyLength ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fetchBodyLength()")
    @DisplayName("Task 6 — fetchBodyLength()")
    class FetchBodyLengthTest {

        @ParameterizedTest(name = "{0} → {1} characters")
        @CsvSource({
                "/hello,  12",
                "/lines,  17",
                "/query,   0",
        })
        @DisplayName("length of the response body")
        void lengths(String path, int expected) throws Exception {
            assertThat(tasks.fetchBodyLength(client, server.uri(path))).isEqualTo(expected);
        }

        @Test
        @DisplayName("error bodies are measured too")
        void errorBody() throws Exception {
            assertThat(tasks.fetchBodyLength(client, server.uri("/status/404"))).isEqualTo("status 404".length());
        }
    }
}

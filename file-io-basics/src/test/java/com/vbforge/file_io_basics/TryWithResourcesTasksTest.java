package com.vbforge.file_io_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TryWithResourcesTasksTest — tests for every method in {@link TryWithResourcesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code TryWithResourcesTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=TryWithResourcesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("TryWithResourcesTasks — your implementations")
class TryWithResourcesTasksTest {

    @TempDir
    File tmp;

    private TryWithResourcesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TryWithResourcesTasks();
    }

    private File fileWith(String name, String text) throws IOException {
        File file = new File(tmp, name);
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private List<String> linesOf(File file) throws IOException {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    // ── Task 1: readFirstLine ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement readFirstLine()")
    @DisplayName("Task 1 — readFirstLine()")
    class ReadFirstLineTest {

        @Test
        @DisplayName("returns the first line without its line break")
        void basic() throws IOException {
            assertThat(tasks.readFirstLine(fileWith("a.txt", "alpha\nbeta\n"))).isEqualTo("alpha");
        }

        @Test
        @DisplayName("a file without a final line break works too")
        void noFinalBreak() throws IOException {
            assertThat(tasks.readFirstLine(fileWith("a.txt", "only"))).isEqualTo("only");
        }

        @Test
        @DisplayName("a blank first line is returned as an empty string")
        void blankFirstLine() throws IOException {
            assertThat(tasks.readFirstLine(fileWith("a.txt", "\nsecond"))).isEqualTo("");
        }

        @Test
        @DisplayName("an empty file gives null")
        void emptyFile() throws IOException {
            assertThat(tasks.readFirstLine(fileWith("a.txt", ""))).isNull();
        }

        @Test
        @DisplayName("decodes UTF-8")
        void utf8() throws IOException {
            assertThat(tasks.readFirstLine(fileWith("a.txt", "héllo €\n"))).isEqualTo("héllo €");
        }
    }

    // ── Task 2: firstLineOrDefault ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement firstLineOrDefault()")
    @DisplayName("Task 2 — firstLineOrDefault()")
    class FirstLineOrDefaultTest {

        @Test
        @DisplayName("an existing file gives its first line")
        void existing() throws IOException {
            assertThat(tasks.firstLineOrDefault(fileWith("a.txt", "alpha\nbeta"), "none")).isEqualTo("alpha");
        }

        @Test
        @DisplayName("a missing file gives the fallback")
        void missing() throws IOException {
            assertThat(tasks.firstLineOrDefault(new File(tmp, "missing.txt"), "none")).isEqualTo("none");
        }

        @Test
        @DisplayName("an empty file gives the fallback")
        void empty() throws IOException {
            assertThat(tasks.firstLineOrDefault(fileWith("a.txt", ""), "none")).isEqualTo("none");
        }

        @Test
        @DisplayName("a blank first line is not replaced by the fallback")
        void blankFirstLine() throws IOException {
            assertThat(tasks.firstLineOrDefault(fileWith("a.txt", "\nsecond"), "none")).isEqualTo("");
        }

        @Test
        @DisplayName("the fallback is returned as the same object")
        void sameFallbackObject() throws IOException {
            String fallback = new String("none");
            assertThat(tasks.firstLineOrDefault(new File(tmp, "missing.txt"), fallback)).isSameAs(fallback);
        }
    }

    // ── Task 3: upperCaseCopy ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement upperCaseCopy()")
    @DisplayName("Task 3 — upperCaseCopy()")
    class UpperCaseCopyTest {

        @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
        @CsvSource({
                "hello, HELLO",
                "World, WORLD",
                "mixed Case 123, MIXED CASE 123",
                "straße, STRASSE"
        })
        @DisplayName("every line is converted to upper case")
        void upperCases(String input, String expected) throws IOException {
            File in = fileWith("in.txt", input + "\n");
            File out = new File(tmp, "out.txt");
            tasks.upperCaseCopy(in, out);
            assertThat(linesOf(out)).containsExactly(expected);
        }

        @Test
        @DisplayName("several lines keep their order, blank lines stay blank")
        void severalLines() throws IOException {
            File in = fileWith("in.txt", "hello\n\nWorld\n");
            File out = new File(tmp, "out.txt");
            tasks.upperCaseCopy(in, out);
            assertThat(linesOf(out)).containsExactly("HELLO", "", "WORLD");
        }

        @Test
        @DisplayName("an empty input gives an empty output file")
        void emptyInput() throws IOException {
            File in = fileWith("in.txt", "");
            File out = new File(tmp, "out.txt");
            tasks.upperCaseCopy(in, out);
            assertThat(out.exists()).isTrue();
            assertThat(out.length()).isZero();
        }

        @Test
        @DisplayName("an existing output is overwritten and the input is unchanged")
        void overwrites() throws IOException {
            File in = fileWith("in.txt", "abc\n");
            File out = fileWith("out.txt", "OLD\nOLD\nOLD\n");
            tasks.upperCaseCopy(in, out);
            assertThat(linesOf(out)).containsExactly("ABC");
            assertThat(linesOf(in)).containsExactly("abc");
        }
    }

    // ── Task 4: eventLog ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement eventLog()")
    @DisplayName("Task 4 — eventLog()")
    class EventLogTest {

        @Test
        @DisplayName("resources open in order and close in reverse order")
        void order() {
            assertThat(tasks.eventLog("A", "B"))
                    .containsExactly("open A", "open B", "body", "close B", "close A");
        }

        @Test
        @DisplayName("the same name twice keeps both events")
        void sameNames() {
            assertThat(tasks.eventLog("x", "x"))
                    .containsExactly("open x", "open x", "body", "close x", "close x");
        }

        @Test
        @DisplayName("the log has exactly five entries")
        void size() {
            assertThat(tasks.eventLog("first", "second")).hasSize(5);
        }

        @Test
        @DisplayName("every call builds a new log")
        void independentCalls() {
            List<String> one = tasks.eventLog("A", "B");
            List<String> two = tasks.eventLog("A", "B");
            assertThat(one).isNotSameAs(two);
            assertThat(two).hasSize(5);
        }
    }

    // ── Task 5: failureSummary ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement failureSummary()")
    @DisplayName("Task 5 — failureSummary()")
    class FailureSummaryTest {

        @Test
        @DisplayName("the body's exception is primary, the close failure is suppressed")
        void basic() {
            assertThat(tasks.failureSummary("boom", "close failed"))
                    .isEqualTo("body=boom, suppressed=close failed");
        }

        @ParameterizedTest(name = "body={0}, close={1}")
        @CsvSource({
                "a, b",
                "disk full, cannot close",
                "first, second"
        })
        @DisplayName("works for any pair of messages")
        void anyMessages(String body, String close) {
            assertThat(tasks.failureSummary(body, close))
                    .isEqualTo("body=" + body + ", suppressed=" + close);
        }

        @Test
        @DisplayName("the same message on both sides is reported on both sides")
        void sameMessage() {
            assertThat(tasks.failureSummary("same", "same")).isEqualTo("body=same, suppressed=same");
        }
    }
}

package com.vbforge.file_io_nio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PathsAndFilesTasksTest — tests for every method in {@link PathsAndFilesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code PathsAndFilesTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=PathsAndFilesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("PathsAndFilesTasks — your implementations")
class PathsAndFilesTasksTest {

    private PathsAndFilesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PathsAndFilesTasks();
    }

    private static List<String> entryNames(Path dir) throws IOException {
        try (Stream<Path> children = Files.list(dir)) {
            return children.map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList());
        }
    }

    // ── Task 1: relativeBetween ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement relativeBetween()")
    @DisplayName("Task 1 — relativeBetween()")
    class RelativeBetweenTest {

        @Test
        @DisplayName("a/b → a/c/d gives ../c/d")
        void siblingBranch() {
            assertThat(tasks.relativeBetween(Paths.get("a", "b"), Paths.get("a", "c", "d")))
                    .isEqualTo(Paths.get("..", "c", "d"));
        }

        @ParameterizedTest(name = "{0} → {1} = {2}")
        @CsvSource({
                "a/b,       a/b/c,   c",
                "a/b,       a/c,     ../c",
                "a/b/c,     a,       ../..",
                "x,         y/z,     ../y/z",
        })
        @DisplayName("parametrized relative paths")
        void parametrized(String from, String to, String expected) {
            assertThat(tasks.relativeBetween(Paths.get(from), Paths.get(to)))
                    .isEqualTo(Paths.get(expected));
        }

        @Test
        @DisplayName("both paths are normalized first")
        void normalizesFirst() {
            Path from = Paths.get("a", "b", "..", "b");
            Path to = Paths.get("a", ".", "c");
            assertThat(tasks.relativeBetween(from, to)).isEqualTo(Paths.get("..", "c"));
        }

        @Test
        @DisplayName("identical paths give the empty path")
        void samePath() {
            Path result = tasks.relativeBetween(Paths.get("a", "b"), Paths.get("a", "b"));
            assertThat(result).isEqualTo(Paths.get(""));
            assertThat(result.toString()).isEmpty();
        }

        @Test
        @DisplayName("works for absolute paths too")
        void absolutePaths(@TempDir Path tmp) {
            Path from = tmp.resolve("one");
            Path to = tmp.resolve("two").resolve("x.txt");
            assertThat(tasks.relativeBetween(from, to)).isEqualTo(Paths.get("..", "two", "x.txt"));
        }

        @Test
        @DisplayName("mixing absolute and relative paths throws IllegalArgumentException")
        void mixedTypes(@TempDir Path tmp) {
            assertThatThrownBy(() -> tasks.relativeBetween(Paths.get("a", "b"), tmp))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 2: resolveSafely ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement resolveSafely()")
    @DisplayName("Task 2 — resolveSafely()")
    class ResolveSafelyTest {

        @Test
        @DisplayName("photos/cat.png resolves inside the base")
        void plainChild(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            assertThat(tasks.resolveSafely(base, "photos/cat.png"))
                    .isEqualTo(base.resolve("photos").resolve("cat.png"));
        }

        @Test
        @DisplayName("a/../b.txt is normalized and still inside")
        void dotDotThatStaysInside(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            assertThat(tasks.resolveSafely(base, "a/../b.txt")).isEqualTo(base.resolve("b.txt"));
        }

        @Test
        @DisplayName("'.' resolves to the base itself, which counts as inside")
        void baseItself(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            assertThat(tasks.resolveSafely(base, ".")).isEqualTo(base);
        }

        @Test
        @DisplayName("../etc/passwd escapes the base → IllegalArgumentException")
        void parentEscape(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            assertThatThrownBy(() -> tasks.resolveSafely(base, "../etc/passwd"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a deeper escape (a/../../x) is detected after normalizing")
        void hiddenEscape(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            assertThatThrownBy(() -> tasks.resolveSafely(base, "a/../../x"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an absolute user input is rejected")
        void absoluteInput(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            String absolute = tmp.resolve("elsewhere").toAbsolutePath().toString();
            assertThatThrownBy(() -> tasks.resolveSafely(base, absolute))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a sibling directory sharing the same name prefix is NOT inside")
        void siblingWithSamePrefix(@TempDir Path tmp) {
            Path base = tmp.resolve("uploads");
            assertThatThrownBy(() -> tasks.resolveSafely(base, "../uploads-evil/x"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 3: writeLines ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement writeLines()")
    @DisplayName("Task 3 — writeLines()")
    class WriteLinesTest {

        @Test
        @DisplayName("writes lines and creates missing parent directories")
        void createsParents(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("out").resolve("deep").resolve("a.txt");
            tasks.writeLines(file, Arrays.asList("x", "y"));
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8)).containsExactly("x", "y");
        }

        @Test
        @DisplayName("an existing file is overwritten, not appended to")
        void overwrites(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("a.txt");
            Files.write(file, "old content that is longer".getBytes(StandardCharsets.UTF_8));
            tasks.writeLines(file, Collections.singletonList("new"));
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8)).containsExactly("new");
        }

        @Test
        @DisplayName("an empty list produces an empty file")
        void emptyList(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("empty.txt");
            tasks.writeLines(file, Collections.<String>emptyList());
            assertThat(Files.exists(file)).isTrue();
            assertThat(Files.size(file)).isZero();
        }

        @Test
        @DisplayName("text is encoded as UTF-8 ('é' takes 2 bytes)")
        void utf8(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("accents.txt");
            tasks.writeLines(file, Collections.singletonList("é"));
            byte[] bytes = Files.readAllBytes(file);
            assertThat(bytes[0]).isEqualTo((byte) 0xC3);
            assertThat(bytes[1]).isEqualTo((byte) 0xA9);
        }

        @Test
        @DisplayName("writing into an existing directory works without error")
        void existingDirectory(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("a.txt");
            tasks.writeLines(file, Collections.singletonList("one"));
            tasks.writeLines(file, Collections.singletonList("two"));
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8)).containsExactly("two");
        }
    }

    // ── Task 4: readNonBlankLines ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement readNonBlankLines()")
    @DisplayName("Task 4 — readNonBlankLines()")
    class ReadNonBlankLinesTest {

        private Path write(Path dir, String content) throws IOException {
            return Files.write(dir.resolve("in.txt"), content.getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("trims lines and drops blank ones")
        void trimsAndSkipsBlank(@TempDir Path tmp) throws IOException {
            Path file = write(tmp, "  a \n\n b\n   \n");
            assertThat(tasks.readNonBlankLines(file)).containsExactly("a", "b");
        }

        @Test
        @DisplayName("the last line needs no trailing newline")
        void noTrailingNewline(@TempDir Path tmp) throws IOException {
            Path file = write(tmp, "one\ntwo");
            assertThat(tasks.readNonBlankLines(file)).containsExactly("one", "two");
        }

        @Test
        @DisplayName("a missing file gives an empty list instead of an exception")
        void missingFile(@TempDir Path tmp) throws IOException {
            assertThat(tasks.readNonBlankLines(tmp.resolve("nope.txt"))).isEmpty();
        }

        @Test
        @DisplayName("an empty file gives an empty list")
        void emptyFile(@TempDir Path tmp) throws IOException {
            assertThat(tasks.readNonBlankLines(write(tmp, ""))).isEmpty();
        }

        @Test
        @DisplayName("UTF-8 content is decoded correctly")
        void utf8(@TempDir Path tmp) throws IOException {
            Path file = write(tmp, "café\n");
            assertThat(tasks.readNonBlankLines(file)).containsExactly("café");
        }

        @Test
        @DisplayName("other I/O problems are NOT swallowed (reading a directory fails)")
        void otherErrorsPropagate(@TempDir Path tmp) {
            assertThatThrownBy(() -> tasks.readNonBlankLines(tmp)).isInstanceOf(IOException.class);
        }
    }

    // ── Task 5: replaceAtomically ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement replaceAtomically()")
    @DisplayName("Task 5 — replaceAtomically()")
    class ReplaceAtomicallyTest {

        private String read(Path file) throws IOException {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        }

        @Test
        @DisplayName("creates the target when it does not exist")
        void createsTarget(@TempDir Path tmp) throws IOException {
            Path target = tmp.resolve("config.txt");
            tasks.replaceAtomically(target, "v1");
            assertThat(read(target)).isEqualTo("v1");
        }

        @Test
        @DisplayName("replaces an existing target completely")
        void replacesTarget(@TempDir Path tmp) throws IOException {
            Path target = tmp.resolve("config.txt");
            Files.write(target, "a much longer previous version".getBytes(StandardCharsets.UTF_8));
            tasks.replaceAtomically(target, "v2");
            assertThat(read(target)).isEqualTo("v2");
        }

        @Test
        @DisplayName("no temporary file is left in the directory")
        void noLeftovers(@TempDir Path tmp) throws IOException {
            Path target = tmp.resolve("config.txt");
            tasks.replaceAtomically(target, "v1");
            tasks.replaceAtomically(target, "v2");
            assertThat(entryNames(tmp)).containsExactly("config.txt");
        }

        @Test
        @DisplayName("UTF-8 content is preserved")
        void utf8(@TempDir Path tmp) throws IOException {
            Path target = tmp.resolve("config.txt");
            tasks.replaceAtomically(target, "café");
            assertThat(read(target)).isEqualTo("café");
        }

        @Test
        @DisplayName("on failure the exception propagates and the temporary file is cleaned up")
        void failureCleansUp(@TempDir Path tmp) throws IOException {
            Path target = Files.createDirectory(tmp.resolve("config.txt"));
            Files.write(target.resolve("inner.txt"), new byte[]{1});

            assertThatThrownBy(() -> tasks.replaceAtomically(target, "v1"))
                    .isInstanceOf(IOException.class);
            assertThat(entryNames(tmp)).containsExactly("config.txt");
            assertThat(entryNames(target)).containsExactly("inner.txt");
        }
    }
}

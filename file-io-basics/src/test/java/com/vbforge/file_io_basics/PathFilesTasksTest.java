package com.vbforge.file_io_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PathFilesTasksTest — tests for every method in {@link PathFilesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code PathFilesTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=PathFilesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("PathFilesTasks — your implementations")
class PathFilesTasksTest {

    @TempDir
    Path tmp;

    private PathFilesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PathFilesTasks();
    }

    private Path fileWith(String name, String text) throws IOException {
        Path file = tmp.resolve(name);
        Files.write(file, text.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private String contentOf(Path file) throws IOException {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    // ── Task 1: relativeBetween ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement relativeBetween()")
    @DisplayName("Task 1 — relativeBetween()")
    class RelativeBetweenTest {

        @Test
        @DisplayName("a target below the base gives the sub-path")
        void below() {
            Path base = tmp.resolve("app");
            assertThat(tasks.relativeBetween(base, base.resolve("logs").resolve("a.log")))
                    .isEqualTo(Paths.get("logs", "a.log"));
        }

        @Test
        @DisplayName("a target in another branch needs .. elements")
        void otherBranch() {
            Path base = tmp.resolve("app").resolve("logs");
            Path target = tmp.resolve("cfg").resolve("x");
            assertThat(tasks.relativeBetween(base, target)).isEqualTo(Paths.get("..", "..", "cfg", "x"));
        }

        @Test
        @DisplayName("the same path gives the empty path")
        void samePath() {
            Path base = tmp.resolve("app");
            assertThat(tasks.relativeBetween(base, base).toString()).isEmpty();
        }

        @Test
        @DisplayName("resolving the result against the base returns the target")
        void roundTrip() {
            Path base = tmp.resolve("one").resolve("two");
            Path target = tmp.resolve("one").resolve("three").resolve("f.txt");
            assertThat(base.resolve(tasks.relativeBetween(base, target)).normalize()).isEqualTo(target);
        }
    }

    // ── Task 2: resolveInside ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement resolveInside()")
    @DisplayName("Task 2 — resolveInside()")
    class ResolveInsideTest {

        private Path base;

        @BeforeEach
        void baseDir() {
            base = tmp.toAbsolutePath().normalize().resolve("files");
        }

        @Test
        @DisplayName("a plain relative path is resolved below the base")
        void plain() {
            assertThat(tasks.resolveInside(base, "docs/a.txt")).isEqualTo(base.resolve("docs").resolve("a.txt"));
        }

        @Test
        @DisplayName("a harmless .. is normalised away")
        void harmlessDots() {
            assertThat(tasks.resolveInside(base, "docs/../a.txt")).isEqualTo(base.resolve("a.txt"));
        }

        @ParameterizedTest(name = "\"{0}\" escapes the base")
        @ValueSource(strings = {"../etc/passwd", "docs/../../x", "../files2/x", "a/b/../../../c"})
        @DisplayName("paths that leave the base are rejected")
        void escapes(String userPath) {
            assertThatThrownBy(() -> tasks.resolveInside(base, userPath))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an absolute input is rejected")
        void absoluteInput() {
            String absolute = tmp.toAbsolutePath().toString();
            assertThatThrownBy(() -> tasks.resolveInside(base, absolute))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the disk is never touched — the paths do not have to exist")
        void noDiskAccess() {
            assertThat(Files.exists(base)).isFalse();
            assertThat(tasks.resolveInside(base, "nothing/here.txt")).isEqualTo(base.resolve("nothing").resolve("here.txt"));
        }
    }

    // ── Task 3: nonBlankLines ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement nonBlankLines()")
    @DisplayName("Task 3 — nonBlankLines()")
    class NonBlankLinesTest {

        @Test
        @DisplayName("removes empty lines")
        void emptyLines() throws IOException {
            assertThat(tasks.nonBlankLines(fileWith("a.txt", "a\n\nb\n"))).containsExactly("a", "b");
        }

        @Test
        @DisplayName("removes whitespace-only lines but keeps the others untrimmed")
        void whitespaceOnly() throws IOException {
            assertThat(tasks.nonBlankLines(fileWith("a.txt", "  \nx \n\t\n"))).containsExactly("x ");
        }

        @Test
        @DisplayName("keeps leading indentation")
        void indentation() throws IOException {
            assertThat(tasks.nonBlankLines(fileWith("a.txt", "  indented\n"))).containsExactly("  indented");
        }

        @Test
        @DisplayName("a file of blank lines gives an empty list")
        void allBlank() throws IOException {
            assertThat(tasks.nonBlankLines(fileWith("a.txt", "\n  \n\n"))).isEmpty();
        }

        @Test
        @DisplayName("an empty file gives an empty list")
        void emptyFile() throws IOException {
            assertThat(tasks.nonBlankLines(fileWith("a.txt", ""))).isEmpty();
        }
    }

    // ── Task 4: countLinesContaining ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countLinesContaining()")
    @DisplayName("Task 4 — countLinesContaining()")
    class CountLinesContainingTest {

        @Test
        @DisplayName("counts the lines that contain the text")
        void basic() throws IOException {
            Path log = fileWith("a.log", "INFO a\nERROR b\nINFO c\nERROR d\n");
            assertThat(tasks.countLinesContaining(log, "ERROR")).isEqualTo(2L);
        }

        @Test
        @DisplayName("matching is case-sensitive")
        void caseSensitive() throws IOException {
            assertThat(tasks.countLinesContaining(fileWith("a.txt", "abc\nABC\n"), "abc")).isEqualTo(1L);
        }

        @Test
        @DisplayName("no match gives 0")
        void noMatch() throws IOException {
            assertThat(tasks.countLinesContaining(fileWith("a.txt", "x\n"), "y")).isZero();
        }

        @Test
        @DisplayName("an empty file gives 0")
        void emptyFile() throws IOException {
            assertThat(tasks.countLinesContaining(fileWith("a.txt", ""), "x")).isZero();
        }

        @Test
        @DisplayName("an empty needle matches every line")
        void emptyNeedle() throws IOException {
            assertThat(tasks.countLinesContaining(fileWith("a.txt", "a\nb\n"), "")).isEqualTo(2L);
        }

        @Test
        @DisplayName("the file stays usable afterwards — it can be deleted (handle closed)")
        void handleClosed() throws IOException {
            Path file = fileWith("a.txt", "x\ny\n");
            tasks.countLinesContaining(file, "x");
            Files.delete(file);
            assertThat(Files.exists(file)).isFalse();
        }
    }

    // ── Task 5: writeText ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement writeText()")
    @DisplayName("Task 5 — writeText()")
    class WriteTextTest {

        @Test
        @DisplayName("creates missing parent directories")
        void createsParents() throws IOException {
            Path file = tmp.resolve("a").resolve("b").resolve("c.txt");
            tasks.writeText(file, "hi");
            assertThat(contentOf(file)).isEqualTo("hi");
        }

        @Test
        @DisplayName("overwrites an existing file completely")
        void overwrites() throws IOException {
            Path file = tmp.resolve("x.txt");
            tasks.writeText(file, "first and longer");
            tasks.writeText(file, "second");
            assertThat(contentOf(file)).isEqualTo("second");
        }

        @Test
        @DisplayName("an empty text gives an empty file")
        void emptyText() throws IOException {
            Path file = tmp.resolve("e.txt");
            tasks.writeText(file, "");
            assertThat(Files.exists(file)).isTrue();
            assertThat(Files.size(file)).isZero();
        }

        @Test
        @DisplayName("text is stored as UTF-8")
        void utf8() throws IOException {
            Path file = tmp.resolve("u.txt");
            tasks.writeText(file, "héllo €");
            assertThat(Files.size(file)).isEqualTo(10L);
            assertThat(contentOf(file)).isEqualTo("héllo €");
        }

        @Test
        @DisplayName("an existing directory is not an error")
        void existingDirectory() throws IOException {
            Files.createDirectories(tmp.resolve("d"));
            Path file = tmp.resolve("d").resolve("f.txt");
            tasks.writeText(file, "ok");
            assertThat(contentOf(file)).isEqualTo("ok");
        }
    }

    // ── Task 6: backup ────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement backup()")
    @DisplayName("Task 6 — backup()")
    class BackupTest {

        @Test
        @DisplayName("creates name.bak next to the original with the same content")
        void basic() throws IOException {
            Path file = fileWith("notes.txt", "important");
            Path backup = tasks.backup(file);
            assertThat(backup).isEqualTo(tmp.resolve("notes.txt.bak"));
            assertThat(contentOf(backup)).isEqualTo("important");
        }

        @Test
        @DisplayName("the original stays untouched")
        void originalStays() throws IOException {
            Path file = fileWith("notes.txt", "important");
            tasks.backup(file);
            assertThat(contentOf(file)).isEqualTo("important");
        }

        @Test
        @DisplayName("a second backup replaces the first")
        void replacesOldBackup() throws IOException {
            Path file = fileWith("notes.txt", "v1");
            tasks.backup(file);
            Files.write(file, Collections.singletonList("v2"), StandardCharsets.UTF_8);
            Path backup = tasks.backup(file);
            assertThat(Files.readAllLines(backup, StandardCharsets.UTF_8)).isEqualTo(Arrays.asList("v2"));
        }

        @Test
        @DisplayName("a file without extension gets .bak appended")
        void noExtension() throws IOException {
            Path backup = tasks.backup(fileWith("data", "x"));
            assertThat(backup.getFileName().toString()).isEqualTo("data.bak");
        }

        @Test
        @DisplayName("a missing file throws NoSuchFileException")
        void missing() {
            assertThatThrownBy(() -> tasks.backup(tmp.resolve("missing.txt")))
                    .isInstanceOf(NoSuchFileException.class);
        }
    }
}

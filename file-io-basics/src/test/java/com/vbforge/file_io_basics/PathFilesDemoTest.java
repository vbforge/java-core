package com.vbforge.file_io_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PathFilesDemoTest — verifies every concept shown in {@link PathFilesDemo}.
 *
 * <p>These tests are always GREEN. Paths that are only manipulated as values use fixed
 * Unix-style text built with {@code Paths.get(...)} on the temp directory, and files live in
 * a {@code @TempDir}.
 */
@DisplayName("PathFilesDemo — API contracts")
class PathFilesDemoTest {

    @TempDir
    Path workDir;

    // ── 1. Path basics ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Path basics")
    class PathTest {

        private final Path base = Paths.get("data", "app");

        @Test
        @DisplayName("getFileName and getParent split a path")
        void splitting() {
            Path file = Paths.get("data", "app", "a.txt");
            assertThat(file.getFileName().toString()).isEqualTo("a.txt");
            assertThat(file.getParent()).isEqualTo(base);
            assertThat(Paths.get("a.txt").getParent()).isNull();
        }

        @Test
        @DisplayName("resolve appends a relative path")
        void resolveRelative() {
            assertThat(base.resolve("logs")).isEqualTo(Paths.get("data", "app", "logs"));
        }

        @Test
        @DisplayName("resolve with an absolute argument returns that argument")
        void resolveAbsoluteDropsBase() {
            Path absolute = workDir.toAbsolutePath();
            assertThat(base.resolve(absolute)).isEqualTo(absolute);
        }

        @Test
        @DisplayName("resolveSibling swaps the last name element")
        void resolveSibling() {
            assertThat(Paths.get("data", "a.txt").resolveSibling("b.txt")).isEqualTo(Paths.get("data", "b.txt"));
        }

        @Test
        @DisplayName("normalize removes . and .. elements without touching the disk")
        void normalize() {
            assertThat(Paths.get("a", ".", "b", "..", "c").normalize()).isEqualTo(Paths.get("a", "c"));
        }

        @Test
        @DisplayName("relativize is the inverse of resolve")
        void relativize() {
            Path root = workDir.toAbsolutePath();
            Path target = root.resolve("logs").resolve("a.log");
            assertThat(root.relativize(target)).isEqualTo(Paths.get("logs", "a.log"));
            assertThat(root.resolve("logs").relativize(root.resolve("cfg").resolve("x")))
                    .isEqualTo(Paths.get("..", "cfg", "x"));
        }

        @Test
        @DisplayName("getNameCount and getName(i) walk the elements")
        void nameElements() {
            Path path = Paths.get("a", "b", "c.txt");
            assertThat(path.getNameCount()).isEqualTo(3);
            assertThat(path.getName(1).toString()).isEqualTo("b");
        }

        @Test
        @DisplayName("Path is Comparable and has value-based equals")
        void equality() {
            assertThat(Paths.get("a", "b")).isEqualTo(Paths.get("a", "b"));
            assertThat(Paths.get("a", "b").compareTo(Paths.get("a", "c"))).isNegative();
        }
    }

    // ── 2. Read and write ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Read and write")
    class ReadWriteTest {

        @Test
        @DisplayName("write(lines) then readAllLines round-trips the lines")
        void roundTrip() throws IOException {
            Path file = workDir.resolve("lines.txt");
            Files.write(file, Arrays.asList("a", "b", "c"), StandardCharsets.UTF_8);
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8)).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("write(bytes) and readAllBytes round-trip exact bytes")
        void bytes() throws IOException {
            Path file = workDir.resolve("b.bin");
            byte[] data = {0, 1, -1, 127, -128};
            Files.write(file, data);
            assertThat(Files.readAllBytes(file)).containsExactly(0, 1, -1, 127, -128);
            assertThat(Files.size(file)).isEqualTo(5L);
        }

        @Test
        @DisplayName("write overwrites by default; APPEND adds to the end")
        void overwriteVersusAppend() throws IOException {
            Path file = workDir.resolve("t.txt");
            Files.write(file, Arrays.asList("one"), StandardCharsets.UTF_8);
            Files.write(file, Arrays.asList("two"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8)).containsExactly("one", "two");
            Files.write(file, Arrays.asList("three"), StandardCharsets.UTF_8);
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8)).containsExactly("three");
        }

        @Test
        @DisplayName("reading a missing file throws NoSuchFileException")
        void missingFile() {
            Path missing = workDir.resolve("missing.txt");
            assertThatThrownBy(() -> Files.readAllLines(missing, StandardCharsets.UTF_8))
                    .isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("write fails when the parent directory is missing")
        void missingParent() {
            Path file = workDir.resolve("no/such/dir/f.txt");
            assertThatThrownBy(() -> Files.write(file, new byte[]{1})).isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("createDirectories creates all levels and is idempotent")
        void createDirectories() throws IOException {
            Path deep = workDir.resolve("a/b/c");
            Files.createDirectories(deep);
            Files.createDirectories(deep);
            assertThat(Files.isDirectory(deep)).isTrue();
        }

        @Test
        @DisplayName("exists, isDirectory and isRegularFile describe a path")
        void inspect() throws IOException {
            Path file = workDir.resolve("f.txt");
            Files.write(file, new byte[0]);
            assertThat(Files.exists(file)).isTrue();
            assertThat(Files.isRegularFile(file)).isTrue();
            assertThat(Files.isDirectory(file)).isFalse();
            assertThat(Files.exists(workDir.resolve("nope"))).isFalse();
        }
    }

    // ── 3. Lazy line streams ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Lazy line streams")
    class LinesStreamTest {

        private Path logFile() throws IOException {
            Path file = workDir.resolve("app.log");
            Files.write(file, Arrays.asList("INFO a", "ERROR b", "INFO c", "ERROR d"), StandardCharsets.UTF_8);
            return file;
        }

        @Test
        @DisplayName("Files.lines inside try-with-resources filters and counts")
        void linesInTry() throws IOException {
            try (Stream<String> lines = Files.lines(logFile(), StandardCharsets.UTF_8)) {
                assertThat(lines.filter(l -> l.startsWith("ERROR")).count()).isEqualTo(2L);
            }
        }

        @Test
        @DisplayName("Files.lines keeps the original order")
        void order() throws IOException {
            try (Stream<String> lines = Files.lines(logFile(), StandardCharsets.UTF_8)) {
                List<String> first = lines.limit(2).collect(Collectors.toList());
                assertThat(first).containsExactly("INFO a", "ERROR b");
            }
        }

        @Test
        @DisplayName("a missing file fails on Files.lines itself with a checked exception")
        void missing() {
            Path missing = workDir.resolve("missing.log");
            assertThatThrownBy(() -> Files.lines(missing, StandardCharsets.UTF_8))
                    .isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("bad bytes surface as UncheckedIOException while the stream is consumed")
        void malformedInput() throws IOException {
            Path file = workDir.resolve("bad.txt");
            Files.write(file, new byte[]{(byte) 0xC3, (byte) 0x28, '\n'});
            assertThatThrownBy(() -> {
                try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                    lines.count();
                }
            }).isInstanceOf(UncheckedIOException.class);
        }
    }

    // ── 4. Copy, move, delete ─────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Copy, move, delete")
    class CopyMoveDeleteTest {

        private Path source() throws IOException {
            Path file = workDir.resolve("src.txt");
            Files.write(file, "content".getBytes(StandardCharsets.UTF_8));
            return file;
        }

        @Test
        @DisplayName("copy leaves the source in place")
        void copy() throws IOException {
            Path source = source();
            Path target = workDir.resolve("copy.txt");
            Files.copy(source, target);
            assertThat(Files.exists(source)).isTrue();
            assertThat(Files.readAllBytes(target)).isEqualTo(Files.readAllBytes(source));
        }

        @Test
        @DisplayName("copy refuses to overwrite unless REPLACE_EXISTING is given")
        void copyNoOverwrite() throws IOException {
            Path source = source();
            Path target = workDir.resolve("copy.txt");
            Files.write(target, "old".getBytes(StandardCharsets.UTF_8));
            assertThatThrownBy(() -> Files.copy(source, target)).isInstanceOf(FileAlreadyExistsException.class);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            assertThat(new String(Files.readAllBytes(target), StandardCharsets.UTF_8)).isEqualTo("content");
        }

        @Test
        @DisplayName("move removes the source")
        void move() throws IOException {
            Path source = source();
            Path target = workDir.resolve("moved.txt");
            Files.move(source, target);
            assertThat(Files.exists(source)).isFalse();
            assertThat(Files.exists(target)).isTrue();
        }

        @Test
        @DisplayName("delete throws for a missing file; deleteIfExists returns false")
        void deleteMissing() throws IOException {
            Path missing = workDir.resolve("missing.txt");
            assertThatThrownBy(() -> Files.delete(missing)).isInstanceOf(NoSuchFileException.class);
            assertThat(Files.deleteIfExists(missing)).isFalse();
        }

        @Test
        @DisplayName("deleteIfExists returns true when it deleted something")
        void deleteExisting() throws IOException {
            assertThat(Files.deleteIfExists(source())).isTrue();
        }

        @Test
        @DisplayName("delete of a non-empty directory throws DirectoryNotEmptyException")
        void deleteNonEmptyDirectory() throws IOException {
            Path dir = workDir.resolve("dir");
            Files.createDirectories(dir);
            Files.write(dir.resolve("x.txt"), new byte[]{1});
            assertThatThrownBy(() -> Files.delete(dir)).isInstanceOf(DirectoryNotEmptyException.class);
        }
    }

    // ── 5. List and walk ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. List and walk")
    class ListAndWalkTest {

        private void buildTree() throws IOException {
            Files.createDirectories(workDir.resolve("sub/deeper"));
            Files.write(workDir.resolve("a.txt"), new byte[]{1});
            Files.write(workDir.resolve("sub/b.txt"), new byte[]{1});
            Files.write(workDir.resolve("sub/deeper/c.txt"), new byte[]{1});
        }

        @Test
        @DisplayName("Files.list returns direct children only")
        void list() throws IOException {
            buildTree();
            try (Stream<Path> children = Files.list(workDir)) {
                List<String> names = children.map(p -> p.getFileName().toString()).sorted()
                        .collect(Collectors.toList());
                assertThat(names).containsExactly("a.txt", "sub");
            }
        }

        @Test
        @DisplayName("Files.walk visits the root and everything below it")
        void walk() throws IOException {
            buildTree();
            try (Stream<Path> all = Files.walk(workDir)) {
                assertThat(all.count()).isEqualTo(6L);
            }
        }

        @Test
        @DisplayName("walk with a regular-file filter finds only the files")
        void walkFilesOnly() throws IOException {
            buildTree();
            try (Stream<Path> all = Files.walk(workDir)) {
                List<String> files = all.filter(Files::isRegularFile)
                        .map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList());
                assertThat(files).containsExactly("a.txt", "b.txt", "c.txt");
            }
        }

        @Test
        @DisplayName("walk with maxDepth 1 behaves like list plus the root")
        void walkDepth() throws IOException {
            buildTree();
            try (Stream<Path> all = Files.walk(workDir, 1)) {
                assertThat(all.count()).isEqualTo(3L);
            }
        }

        @Test
        @DisplayName("deleteTree removes a directory with its content")
        void deleteTree() throws IOException {
            buildTree();
            PathFilesDemo.deleteTree(workDir.resolve("sub"));
            assertThat(Files.exists(workDir.resolve("sub"))).isFalse();
            assertThat(Files.exists(workDir.resolve("a.txt"))).isTrue();
        }
    }

    // ── 6. Path safety ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Path safety")
    class PathSafetyTest {

        private final Path base = Paths.get("/srv/files").toAbsolutePath().normalize();

        @Test
        @DisplayName("startsWith on a raw resolved path is fooled by ..")
        void rawStartsWithIsFooled() {
            Path sneaky = base.resolve("../etc/passwd");
            assertThat(sneaky.startsWith(base)).isTrue();
        }

        @Test
        @DisplayName("normalize before the check exposes the escape")
        void normalizeThenCheck() {
            Path clean = base.resolve("../etc/passwd").normalize();
            assertThat(clean.startsWith(base)).isFalse();
        }

        @Test
        @DisplayName("a harmless .. stays inside the base")
        void harmlessDots() {
            Path clean = base.resolve("docs/../a.txt").normalize();
            assertThat(clean.startsWith(base)).isTrue();
            assertThat(clean).isEqualTo(base.resolve("a.txt"));
        }

        @Test
        @DisplayName("Path.startsWith compares whole elements; String.startsWith does not")
        void elementWise() {
            Path sibling = base.resolveSibling("files2").resolve("x");
            assertThat(sibling.startsWith(base)).isFalse();
            assertThat(sibling.toString().startsWith(base.toString())).isTrue();
        }

        @Test
        @DisplayName("an absolute user path replaces the base")
        void absoluteReplacesBase() {
            Path absolute = workDir.toAbsolutePath();
            assertThat(base.resolve(absolute)).isEqualTo(absolute);
        }
    }
}

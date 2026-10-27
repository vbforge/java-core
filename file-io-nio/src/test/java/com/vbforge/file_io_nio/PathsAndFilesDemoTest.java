package com.vbforge.file_io_nio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PathsAndFilesDemoTest — verifies every concept shown in {@link PathsAndFilesDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the NIO.2 {@code Path} and {@code Files} APIs.
 */
@DisplayName("PathsAndFilesDemo — API contracts")
class PathsAndFilesDemoTest {

    private static byte[] utf8(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    // ── 1. Path basics ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Path basics")
    class PathBasicsTest {

        private final Path p = Paths.get("projects", "java", "app", "Main.java");

        @Test
        @DisplayName("a path decomposes into parent, file name and name elements")
        void decompose() {
            assertThat(p.getFileName().toString()).isEqualTo("Main.java");
            assertThat(p.getParent()).isEqualTo(Paths.get("projects", "java", "app"));
            assertThat(p.getNameCount()).isEqualTo(4);
            assertThat(p.getName(1).toString()).isEqualTo("java");
            assertThat(p.subpath(1, 3)).isEqualTo(Paths.get("java", "app"));
        }

        @Test
        @DisplayName("a relative path has no root and is not absolute")
        void relativeHasNoRoot() {
            assertThat(p.getRoot()).isNull();
            assertThat(p.isAbsolute()).isFalse();
            assertThat(p.toAbsolutePath().isAbsolute()).isTrue();
        }

        @Test
        @DisplayName("startsWith compares whole name elements, not characters")
        void startsWithIsElementWise() {
            assertThat(p.startsWith(Paths.get("projects", "java"))).isTrue();
            assertThat(p.startsWith("proj")).isFalse();
            assertThat(p.endsWith("Main.java")).isTrue();
        }

        @Test
        @DisplayName("paths are immutable — resolveSibling returns a new Path")
        void immutable() {
            Path sibling = p.resolveSibling("Other.java");
            assertThat(sibling).isEqualTo(Paths.get("projects", "java", "app", "Other.java"));
            assertThat(p).isEqualTo(Paths.get("projects", "java", "app", "Main.java"));
            assertThat(sibling).isNotSameAs(p);
        }

        @Test
        @DisplayName("a Path never touches the disk — a non-existent path is perfectly valid")
        void noDiskAccess(@TempDir Path tmp) {
            Path ghost = tmp.resolve("does").resolve("not").resolve("exist.txt");
            assertThat(ghost.getFileName().toString()).isEqualTo("exist.txt");
            assertThat(Files.exists(ghost)).isFalse();
        }

        @Test
        @DisplayName("toFile() / toPath() bridge to legacy java.io.File")
        void fileBridge() {
            assertThat(p.toFile().toPath()).isEqualTo(p);
        }
    }

    // ── 2. Path operations ────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Path operations")
    class PathOperationsTest {

        private final Path base = Paths.get("data", "reports");

        @Test
        @DisplayName("resolve appends a relative path")
        void resolveAppends() {
            assertThat(base.resolve("q1.csv")).isEqualTo(Paths.get("data", "reports", "q1.csv"));
            assertThat(base.resolve(Paths.get("2024", "q1.csv")))
                    .isEqualTo(Paths.get("data", "reports", "2024", "q1.csv"));
        }

        @Test
        @DisplayName("resolve with an absolute argument returns that argument")
        void resolveAbsoluteReplaces() {
            Path absolute = Paths.get("config").toAbsolutePath();
            assertThat(base.resolve(absolute)).isEqualTo(absolute);
        }

        @Test
        @DisplayName("resolveSibling replaces the last element")
        void resolveSibling() {
            assertThat(base.resolveSibling("archive")).isEqualTo(Paths.get("data", "archive"));
        }

        @Test
        @DisplayName("relativize gives the path from one location to another")
        void relativize() {
            Path target = Paths.get("data", "logs", "app.log");
            Path relative = base.relativize(target);
            assertThat(relative).isEqualTo(Paths.get("..", "logs", "app.log"));
            assertThat(base.resolve(relative).normalize()).isEqualTo(target);
        }

        @Test
        @DisplayName("relativize refuses to mix absolute and relative paths")
        void relativizeMixedTypes() {
            Path absolute = Paths.get("data").toAbsolutePath();
            assertThatThrownBy(() -> base.relativize(absolute))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("normalize removes '.' and collapses 'name/..'")
        void normalize() {
            Path messy = Paths.get("data", "reports", "..", "logs", ".", "app.log");
            assertThat(messy.normalize()).isEqualTo(Paths.get("data", "logs", "app.log"));
        }

        @Test
        @DisplayName("a leading '..' survives normalize()")
        void leadingDotDot() {
            assertThat(Paths.get("..", "a").normalize()).isEqualTo(Paths.get("..", "a"));
        }

        @Test
        @DisplayName("resolve + normalize can escape the base — startsWith on Paths detects it")
        void traversalDetected() {
            Path uploads = Paths.get("uploads");
            Path evil = uploads.resolve(Paths.get("..", "secret.txt")).normalize();
            assertThat(evil).isEqualTo(Paths.get("secret.txt"));
            assertThat(evil.startsWith(uploads)).isFalse();

            Path fine = uploads.resolve(Paths.get("a", "b.txt")).normalize();
            assertThat(fine.startsWith(uploads)).isTrue();
        }
    }

    // ── 3. Files read / write ─────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Files read / write")
    class FilesReadWriteTest {

        @Test
        @DisplayName("write(lines) then readAllLines round-trips")
        void roundTrip(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("todo.txt");
            Files.write(file, Arrays.asList("buy milk", "write code"), StandardCharsets.UTF_8);
            assertThat(Files.readAllLines(file, StandardCharsets.UTF_8))
                    .containsExactly("buy milk", "write code");
        }

        @Test
        @DisplayName("default write options truncate an existing file")
        void writeTruncates(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("f.txt");
            Files.write(file, utf8("a long first version"));
            Files.write(file, utf8("short"));
            assertThat(Files.readAllBytes(file)).isEqualTo(utf8("short"));
        }

        @Test
        @DisplayName("APPEND adds to the end of the file")
        void append(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("f.txt");
            Files.write(file, utf8("one"));
            Files.write(file, utf8("two"), StandardOpenOption.APPEND);
            assertThat(Files.readAllBytes(file)).isEqualTo(utf8("onetwo"));
        }

        @Test
        @DisplayName("CREATE_NEW refuses to overwrite an existing file")
        void createNew(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("f.txt");
            Files.write(file, utf8("x"));
            assertThatThrownBy(() -> Files.write(file, utf8("y"), StandardOpenOption.CREATE_NEW))
                    .isInstanceOf(FileAlreadyExistsException.class);
        }

        @Test
        @DisplayName("reading a missing file throws NoSuchFileException")
        void missingFile(@TempDir Path tmp) {
            Path missing = tmp.resolve("missing.txt");
            assertThatThrownBy(() -> Files.readAllLines(missing, StandardCharsets.UTF_8))
                    .isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("createDirectories builds the whole chain and is idempotent; createDirectory needs the parent")
        void createDirectories(@TempDir Path tmp) throws IOException {
            Path deep = tmp.resolve("a").resolve("b").resolve("c");
            Files.createDirectories(deep);
            Files.createDirectories(deep);                       // second call does not fail
            assertThat(Files.isDirectory(deep)).isTrue();

            Path orphan = tmp.resolve("x").resolve("y");
            assertThatThrownBy(() -> Files.createDirectory(orphan))
                    .isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("buffered writer and reader stream a file line by line")
        void buffered(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("big.txt");
            try (BufferedWriter out = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                out.write("line 1");
                out.newLine();
                out.write("line 2");
                out.newLine();
            }
            try (BufferedReader in = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                assertThat(in.readLine()).isEqualTo("line 1");
                assertThat(in.readLine()).isEqualTo("line 2");
                assertThat(in.readLine()).isNull();
            }
        }

        @Test
        @DisplayName("Files.lines is a lazy Stream that must be closed")
        void linesStream(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("l.txt");
            Files.write(file, Arrays.asList("a", "", "b"), StandardCharsets.UTF_8);
            try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
                assertThat(lines.filter(s -> !s.isEmpty()).count()).isEqualTo(2L);
            }
        }

        @Test
        @DisplayName("size is measured in bytes, not characters ('é' is 2 bytes in UTF-8)")
        void sizeInBytes(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("accents.txt");
            Files.write(file, utf8("é"));
            assertThat(Files.size(file)).isEqualTo(2L);
        }

        @Test
        @DisplayName("Path.equals compares names; Files.isSameFile compares the files")
        void sameFileVsEquals(@TempDir Path tmp) throws IOException {
            Path file = Files.write(tmp.resolve("f.txt"), utf8("x"));
            Path viaDot = tmp.resolve(".").resolve("f.txt");
            assertThat(viaDot).isNotEqualTo(file);
            assertThat(Files.isSameFile(viaDot, file)).isTrue();
        }
    }

    // ── 4. Copy / move / delete ───────────────────────────────────────────────

    @Nested
    @DisplayName("4. Copy / move / delete")
    class CopyMoveDeleteTest {

        @Test
        @DisplayName("copy refuses to overwrite; REPLACE_EXISTING overwrites")
        void copyOverwrite(@TempDir Path tmp) throws IOException {
            Path a = Files.write(tmp.resolve("a.txt"), utf8("v1"));
            Path b = tmp.resolve("b.txt");
            Files.copy(a, b);
            assertThatThrownBy(() -> Files.copy(a, b)).isInstanceOf(FileAlreadyExistsException.class);

            Files.write(a, utf8("v2"));
            Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING);
            assertThat(Files.readAllBytes(b)).isEqualTo(utf8("v2"));
        }

        @Test
        @DisplayName("copying a directory is shallow — the copy is empty")
        void shallowDirectoryCopy(@TempDir Path tmp) throws IOException {
            Path d = Files.createDirectory(tmp.resolve("d"));
            Files.write(d.resolve("inner.txt"), utf8("x"));
            Path copy = tmp.resolve("d2");
            Files.copy(d, copy);
            assertThat(Files.isDirectory(copy)).isTrue();
            try (Stream<Path> children = Files.list(copy)) {
                assertThat(children.count()).isZero();
            }
        }

        @Test
        @DisplayName("move renames: source is gone, content arrives")
        void move(@TempDir Path tmp) throws IOException {
            Path src = Files.write(tmp.resolve("src.txt"), utf8("payload"));
            Path dst = tmp.resolve("dst.txt");
            Files.move(src, dst);
            assertThat(Files.notExists(src)).isTrue();
            assertThat(Files.readAllBytes(dst)).isEqualTo(utf8("payload"));
        }

        @Test
        @DisplayName("ATOMIC_MOVE within one directory succeeds")
        void atomicMove(@TempDir Path tmp) throws IOException {
            Path src = Files.write(tmp.resolve("src.txt"), utf8("payload"));
            Path dst = tmp.resolve("dst.txt");
            Files.move(src, dst, StandardCopyOption.ATOMIC_MOVE);
            assertThat(Files.notExists(src)).isTrue();
            assertThat(Files.readAllBytes(dst)).isEqualTo(utf8("payload"));
        }

        @Test
        @DisplayName("delete throws for a missing file; deleteIfExists returns false")
        void deleteMissing(@TempDir Path tmp) throws IOException {
            Path missing = tmp.resolve("nope.txt");
            assertThatThrownBy(() -> Files.delete(missing)).isInstanceOf(NoSuchFileException.class);
            assertThat(Files.deleteIfExists(missing)).isFalse();
        }

        @Test
        @DisplayName("deleteIfExists returns true when it deleted something")
        void deleteExisting(@TempDir Path tmp) throws IOException {
            Path file = Files.write(tmp.resolve("f.txt"), utf8("x"));
            assertThat(Files.deleteIfExists(file)).isTrue();
            assertThat(Files.exists(file)).isFalse();
        }

        @Test
        @DisplayName("deleting a non-empty directory throws DirectoryNotEmptyException")
        void deleteNonEmptyDirectory(@TempDir Path tmp) throws IOException {
            Path d = Files.createDirectory(tmp.resolve("d"));
            Files.write(d.resolve("inner.txt"), utf8("x"));
            assertThatThrownBy(() -> Files.delete(d)).isInstanceOf(DirectoryNotEmptyException.class);
        }

        @Test
        @DisplayName("copy works between a Path and streams")
        void streamCopy(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("from-stream.txt");
            Files.copy(new ByteArrayInputStream(utf8("streamed")), file);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            Files.copy(file, sink);
            assertThat(sink.toByteArray()).isEqualTo(utf8("streamed"));
        }

        @Test
        @DisplayName("createTempFile creates an empty file with the given prefix/suffix in the given directory")
        void tempFile(@TempDir Path tmp) throws IOException {
            Path temp = Files.createTempFile(tmp, "pre-", ".tmp");
            assertThat(temp.getParent()).isEqualTo(tmp);
            assertThat(temp.getFileName().toString()).startsWith("pre-").endsWith(".tmp");
            assertThat(Files.size(temp)).isZero();
        }

        @Test
        @DisplayName("the demo's cleanup helper removes a whole tree and tolerates a missing root")
        void deleteTreeHelper(@TempDir Path tmp) throws IOException {
            Path victim = tmp.resolve("victim");
            Files.createDirectories(victim.resolve("a").resolve("b"));
            Files.write(victim.resolve("a").resolve("b").resolve("f.txt"), utf8("x"));

            PathsAndFilesDemo.deleteTree(victim);
            assertThat(Files.exists(victim)).isFalse();

            PathsAndFilesDemo.deleteTree(victim);               // already gone — no exception
        }
    }
}

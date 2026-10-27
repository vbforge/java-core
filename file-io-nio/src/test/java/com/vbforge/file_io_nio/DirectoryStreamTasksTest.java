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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DirectoryStreamTasksTest — tests for every method in {@link DirectoryStreamTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code DirectoryStreamTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=DirectoryStreamTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("DirectoryStreamTasks — your implementations")
class DirectoryStreamTasksTest {

    private DirectoryStreamTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DirectoryStreamTasks();
    }

    /** Writes {@code size} bytes to {@code root/relative}, creating parent directories. */
    private static void file(Path root, String relative, int size) throws IOException {
        Path file = root.resolve(relative);
        Files.createDirectories(file.getParent());
        byte[] content = new byte[size];
        java.util.Arrays.fill(content, (byte) 'x');
        Files.write(file, content);
    }

    private static Path p(String first, String... more) {
        return Paths.get(first, more);
    }

    // ── Task 1: listFileNames ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement listFileNames()")
    @DisplayName("Task 1 — listFileNames()")
    class ListFileNamesTest {

        private Path tree(Path root) throws IOException {
            file(root, "a.txt", 1);
            file(root, "b.java", 1);
            file(root, "c.txt", 1);
            Files.createDirectory(root.resolve("d.txt"));          // a DIRECTORY whose name matches *.txt
            file(root, "sub/e.txt", 1);
            return root;
        }

        @Test
        @DisplayName("*.txt → [a.txt, c.txt] (sorted, no directory, not recursive)")
        void globTxt(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFileNames(tree(tmp), "*.txt")).containsExactly("a.txt", "c.txt");
        }

        @Test
        @DisplayName("*.{txt,java} → [a.txt, b.java, c.txt]")
        void alternation(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFileNames(tree(tmp), "*.{txt,java}"))
                    .containsExactly("a.txt", "b.java", "c.txt");
        }

        @Test
        @DisplayName("no match → empty list")
        void noMatch(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFileNames(tree(tmp), "*.md")).isEmpty();
        }

        @Test
        @DisplayName("an empty directory → empty list")
        void emptyDirectory(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFileNames(tmp, "*")).isEmpty();
        }

        @Test
        @DisplayName("the result is sorted even though the directory order is unspecified")
        void sorted(@TempDir Path tmp) throws IOException {
            for (String name : new String[]{"m.txt", "z.txt", "b.txt", "k.txt", "a.txt"}) {
                file(tmp, name, 1);
            }
            assertThat(tasks.listFileNames(tmp, "*.txt"))
                    .containsExactly("a.txt", "b.txt", "k.txt", "m.txt", "z.txt");
        }

        @Test
        @DisplayName("a missing directory throws an IOException")
        void missingDirectory(@TempDir Path tmp) {
            assertThatThrownBy(() -> tasks.listFileNames(tmp.resolve("nope"), "*"))
                    .isInstanceOf(NoSuchFileException.class);
        }
    }

    // ── Task 2: findByExtension ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement findByExtension()")
    @DisplayName("Task 2 — findByExtension()")
    class FindByExtensionTest {

        private Path tree(Path root) throws IOException {
            file(root, "a.txt", 1);
            file(root, "c.txt", 1);
            file(root, "b.java", 1);
            file(root, "sub/d.txt", 1);
            file(root, "sub/deep/f.txt", 1);
            file(root, "sub/e.java", 1);
            return root;
        }

        @Test
        @DisplayName("txt → root-relative, sorted, any depth")
        void txtFiles(@TempDir Path tmp) throws IOException {
            assertThat(tasks.findByExtension(tree(tmp), "txt")).containsExactly(
                    p("a.txt"), p("c.txt"), p("sub", "d.txt"), p("sub", "deep", "f.txt"));
        }

        @Test
        @DisplayName("the extension is matched case-insensitively")
        void caseInsensitive(@TempDir Path tmp) throws IOException {
            assertThat(tasks.findByExtension(tree(tmp), "JAVA"))
                    .containsExactly(p("b.java"), p("sub", "e.java"));
        }

        @Test
        @DisplayName("upper-case file extensions match a lower-case query")
        void upperCaseFiles(@TempDir Path tmp) throws IOException {
            file(tmp, "LOUD.TXT", 1);
            file(tmp, "quiet.txt", 1);
            assertThat(tasks.findByExtension(tmp, "txt")).containsExactlyInAnyOrder(p("LOUD.TXT"), p("quiet.txt"));
        }

        @Test
        @DisplayName("no match → empty list")
        void noMatch(@TempDir Path tmp) throws IOException {
            assertThat(tasks.findByExtension(tree(tmp), "md")).isEmpty();
        }

        @Test
        @DisplayName("notes.txt.bak and a file literally named 'txt' are not txt files")
        void notAnExtension(@TempDir Path tmp) throws IOException {
            file(tmp, "notes.txt.bak", 1);
            file(tmp, "txt", 1);
            file(tmp, "real.txt", 1);
            assertThat(tasks.findByExtension(tmp, "txt")).containsExactly(p("real.txt"));
        }

        @Test
        @DisplayName("a directory whose name ends with .txt is not a file")
        void directoriesIgnored(@TempDir Path tmp) throws IOException {
            Files.createDirectory(tmp.resolve("folder.txt"));
            file(tmp, "folder.txt/inside.txt", 1);
            assertThat(tasks.findByExtension(tmp, "txt")).containsExactly(p("folder.txt", "inside.txt"));
        }
    }

    // ── Task 3: sizeByExtension ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sizeByExtension()")
    @DisplayName("Task 3 — sizeByExtension()")
    class SizeByExtensionTest {

        @Test
        @DisplayName("groups sizes by lower-cased extension; no extension → (none)")
        void basic(@TempDir Path tmp) throws IOException {
            file(tmp, "a.txt", 2);
            file(tmp, "c.txt", 4);
            file(tmp, "sub/d.TXT", 1);
            file(tmp, "b.java", 3);
            file(tmp, "README", 5);

            Map<String, Long> sizes = tasks.sizeByExtension(tmp);
            assertThat(sizes).containsEntry("txt", 7L)
                    .containsEntry("java", 3L)
                    .containsEntry("(none)", 5L)
                    .hasSize(3);
        }

        @Test
        @DisplayName("an empty directory → empty map")
        void emptyDirectory(@TempDir Path tmp) throws IOException {
            assertThat(tasks.sizeByExtension(tmp)).isEmpty();
        }

        @Test
        @DisplayName("directories contribute nothing, even when nested and empty")
        void directoriesIgnored(@TempDir Path tmp) throws IOException {
            Files.createDirectories(tmp.resolve("empty").resolve("deeper"));
            file(tmp, "only.log", 6);
            assertThat(tasks.sizeByExtension(tmp)).containsOnlyKeys("log").containsEntry("log", 6L);
        }

        @Test
        @DisplayName("the extension is the text after the LAST dot (archive.tar.gz → gz)")
        void lastDot(@TempDir Path tmp) throws IOException {
            file(tmp, "archive.tar.gz", 10);
            file(tmp, "other.gz", 1);
            assertThat(tasks.sizeByExtension(tmp)).containsOnlyKeys("gz").containsEntry("gz", 11L);
        }

        @ParameterizedTest(name = "{0} ({1} bytes) → key {2}")
        @CsvSource({
                "photo.JPG,   3, jpg",
                "Makefile,    4, (none)",
                "data.csv,    5, csv",
        })
        @DisplayName("parametrized single-file cases")
        void parametrized(String name, int size, String expectedKey, @TempDir Path tmp) throws IOException {
            file(tmp, name, size);
            assertThat(tasks.sizeByExtension(tmp)).containsOnlyKeys(expectedKey)
                    .containsEntry(expectedKey, (long) size);
        }
    }

    // ── Task 4: copyDirectory ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement copyDirectory()")
    @DisplayName("Task 4 — copyDirectory()")
    class CopyDirectoryTest {

        private Path source(Path tmp) throws IOException {
            Path source = Files.createDirectory(tmp.resolve("source"));
            Files.write(source.resolve("a.txt"), "A".getBytes(StandardCharsets.UTF_8));
            file(source, "sub/b.txt", 3);
            Files.createDirectory(source.resolve("empty"));
            return source;
        }

        private String read(Path file) throws IOException {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        }

        @Test
        @DisplayName("copies files, nested files and empty directories")
        void copiesEverything(@TempDir Path tmp) throws IOException {
            Path source = source(tmp);
            Path target = tmp.resolve("target");
            tasks.copyDirectory(source, target);

            assertThat(read(target.resolve("a.txt"))).isEqualTo("A");
            assertThat(Files.size(target.resolve("sub").resolve("b.txt"))).isEqualTo(3L);
            assertThat(Files.isDirectory(target.resolve("empty"))).isTrue();
        }

        @Test
        @DisplayName("missing parent directories of the target are created")
        void createsTargetParents(@TempDir Path tmp) throws IOException {
            Path source = source(tmp);
            Path target = tmp.resolve("out").resolve("deep").resolve("target");
            tasks.copyDirectory(source, target);
            assertThat(Files.exists(target.resolve("a.txt"))).isTrue();
        }

        @Test
        @DisplayName("merges into an existing target: overwrites common files, keeps the others")
        void merges(@TempDir Path tmp) throws IOException {
            Path source = source(tmp);
            Path target = Files.createDirectory(tmp.resolve("target"));
            Files.write(target.resolve("a.txt"), "old".getBytes(StandardCharsets.UTF_8));
            Files.write(target.resolve("extra.txt"), "keep".getBytes(StandardCharsets.UTF_8));

            tasks.copyDirectory(source, target);

            assertThat(read(target.resolve("a.txt"))).isEqualTo("A");
            assertThat(read(target.resolve("extra.txt"))).isEqualTo("keep");
        }

        @Test
        @DisplayName("the source is left unchanged")
        void sourceUntouched(@TempDir Path tmp) throws IOException {
            Path source = source(tmp);
            tasks.copyDirectory(source, tmp.resolve("target"));

            assertThat(read(source.resolve("a.txt"))).isEqualTo("A");
            assertThat(Files.exists(source.resolve("sub").resolve("b.txt"))).isTrue();
            assertThat(Files.isDirectory(source.resolve("empty"))).isTrue();
        }

        @Test
        @DisplayName("copying an empty source directory creates an empty target")
        void emptySource(@TempDir Path tmp) throws IOException {
            Path source = Files.createDirectory(tmp.resolve("source"));
            Path target = tmp.resolve("target");
            tasks.copyDirectory(source, target);
            assertThat(Files.isDirectory(target)).isTrue();
            assertThat(target.toFile().list()).isEmpty();
        }
    }

    // ── Task 5: listFilesSkipping ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement listFilesSkipping()")
    @DisplayName("Task 5 — listFilesSkipping()")
    class ListFilesSkippingTest {

        private Path tree(Path root) throws IOException {
            file(root, "a.txt", 1);
            file(root, "sub/d.txt", 1);
            file(root, "sub/deep/f.txt", 1);
            file(root, "skipme/g.txt", 1);
            return root;
        }

        @Test
        @DisplayName("skipping 'skipme' removes that whole branch")
        void skipTopLevel(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFilesSkipping(tree(tmp), "skipme"))
                    .containsExactly(p("a.txt"), p("sub", "d.txt"), p("sub", "deep", "f.txt"));
        }

        @Test
        @DisplayName("a directory is skipped at any depth ('deep')")
        void skipNested(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFilesSkipping(tree(tmp), "deep"))
                    .containsExactly(p("a.txt"), p("skipme", "g.txt"), p("sub", "d.txt"));
        }

        @Test
        @DisplayName("a name that matches nothing skips nothing")
        void skipNothing(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFilesSkipping(tree(tmp), "nothing")).containsExactly(
                    p("a.txt"), p("skipme", "g.txt"), p("sub", "d.txt"), p("sub", "deep", "f.txt"));
        }

        @Test
        @DisplayName("only directories are skipped — a regular FILE with that name is listed")
        void fileWithSkippedName(@TempDir Path tmp) throws IOException {
            file(tmp, "sub/skipme", 1);                 // a regular file called "skipme"
            file(tmp, "skipme/hidden.txt", 1);          // a directory called "skipme"
            assertThat(tasks.listFilesSkipping(tmp, "skipme")).containsExactly(p("sub", "skipme"));
        }

        @Test
        @DisplayName("the root itself is never skipped, even if its name matches")
        void rootNeverSkipped(@TempDir Path tmp) throws IOException {
            Path root = Files.createDirectory(tmp.resolve("skipme"));
            file(root, "kept.txt", 1);
            assertThat(tasks.listFilesSkipping(root, "skipme")).containsExactly(p("kept.txt"));
        }

        @Test
        @DisplayName("an empty directory → empty list")
        void emptyRoot(@TempDir Path tmp) throws IOException {
            assertThat(tasks.listFilesSkipping(tmp, "x")).isEmpty();
        }
    }
}

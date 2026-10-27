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
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileBasicsTasksTest — tests for every method in {@link FileBasicsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code FileBasicsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=FileBasicsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("FileBasicsTasks — your implementations")
class FileBasicsTasksTest {

    @TempDir
    File tmp;

    private FileBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FileBasicsTasks();
    }

    private File fileWithBytes(File file, int size) throws IOException {
        File parent = file.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("cannot create " + parent);
        }
        byte[] data = new byte[size];
        Files.write(file.toPath(), data);
        return file;
    }

    // ── Task 1: fileExtension ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fileExtension()")
    @DisplayName("Task 1 — fileExtension()")
    class FileExtensionTest {

        @ParameterizedTest(name = "{0} → \"{1}\"")
        @CsvSource({
                "notes.txt, txt",
                "REPORT.PDF, pdf",
                "archive.tar.gz, gz",
                "README, ''",
                ".gitignore, ''",
                "file., ''",
                "Mixed.JpG, jpg"
        })
        @DisplayName("extension is lower-case text after the last dot, or empty")
        void extensions(String name, String expected) {
            assertThat(tasks.fileExtension(new File(name))).isEqualTo(expected);
        }

        @Test
        @DisplayName("a dot in a parent directory is not an extension")
        void dotInParent() {
            assertThat(tasks.fileExtension(new File(new File(tmp, "my.dir"), "readme"))).isEmpty();
        }

        @Test
        @DisplayName("works on a full path of a file that does not exist")
        void fullPath() {
            assertThat(tasks.fileExtension(new File(tmp, "sub/data.CSV"))).isEqualTo("csv");
        }

        @Test
        @DisplayName("a hidden file with an extension keeps the extension")
        void hiddenWithExtension() {
            assertThat(tasks.fileExtension(new File(".config.json"))).isEqualTo("json");
        }
    }

    // ── Task 2: ensureParentDirs ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement ensureParentDirs()")
    @DisplayName("Task 2 — ensureParentDirs()")
    class EnsureParentDirsTest {

        @Test
        @DisplayName("creates every missing level and returns the same File")
        void createsLevels() {
            File target = new File(tmp, "a/b/c/out.txt");
            File result = tasks.ensureParentDirs(target);
            assertThat(result).isSameAs(target);
            assertThat(new File(tmp, "a/b/c").isDirectory()).isTrue();
        }

        @Test
        @DisplayName("does not create the file itself")
        void doesNotCreateFile() {
            File target = new File(tmp, "x/y/out.txt");
            tasks.ensureParentDirs(target);
            assertThat(target.exists()).isFalse();
        }

        @Test
        @DisplayName("existing parents are fine — no error, nothing changes")
        void existingParents() throws IOException {
            File target = new File(tmp, "out.txt");
            File inside = fileWithBytes(new File(tmp, "keep.txt"), 3);
            assertThat(tasks.ensureParentDirs(target)).isSameAs(target);
            assertThat(inside.exists()).isTrue();
        }

        @Test
        @DisplayName("calling it twice is safe")
        void twice() {
            File target = new File(tmp, "p/q/out.txt");
            tasks.ensureParentDirs(target);
            assertThat(tasks.ensureParentDirs(target)).isSameAs(target);
            assertThat(new File(tmp, "p/q").isDirectory()).isTrue();
        }

        @Test
        @DisplayName("a bare relative name has no parent — returned unchanged")
        void noParent() {
            File bare = new File("a.txt");
            assertThat(tasks.ensureParentDirs(bare)).isSameAs(bare);
        }
    }

    // ── Task 3: listFileNames ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement listFileNames()")
    @DisplayName("Task 3 — listFileNames()")
    class ListFileNamesTest {

        @Test
        @DisplayName("returns sorted names of regular files only")
        void sortedFilesOnly() throws IOException {
            fileWithBytes(new File(tmp, "b.txt"), 1);
            fileWithBytes(new File(tmp, "a.log"), 1);
            fileWithBytes(new File(tmp, "c.csv"), 1);
            assertThat(new File(tmp, "sub").mkdir()).isTrue();
            assertThat(tasks.listFileNames(tmp)).containsExactly("a.log", "b.txt", "c.csv");
        }

        @Test
        @DisplayName("does not look into sub-directories")
        void notRecursive() throws IOException {
            fileWithBytes(new File(tmp, "top.txt"), 1);
            fileWithBytes(new File(tmp, "sub/inner.txt"), 1);
            assertThat(tasks.listFileNames(tmp)).containsExactly("top.txt");
        }

        @Test
        @DisplayName("an empty directory gives an empty list")
        void emptyDirectory() {
            assertThat(tasks.listFileNames(tmp)).isEmpty();
        }

        @Test
        @DisplayName("a missing directory gives an empty list, not null and not an exception")
        void missingDirectory() {
            assertThat(tasks.listFileNames(new File(tmp, "missing"))).isEmpty();
        }

        @Test
        @DisplayName("a regular file instead of a directory gives an empty list")
        void regularFile() throws IOException {
            File file = fileWithBytes(new File(tmp, "file.txt"), 1);
            assertThat(tasks.listFileNames(file)).isEmpty();
        }
    }

    // ── Task 4: totalSize ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement totalSize()")
    @DisplayName("Task 4 — totalSize()")
    class TotalSizeTest {

        @Test
        @DisplayName("sums every file in a nested tree")
        void nestedTree() throws IOException {
            fileWithBytes(new File(tmp, "a.txt"), 3);
            fileWithBytes(new File(tmp, "sub/b.txt"), 5);
            fileWithBytes(new File(tmp, "sub/deeper/c.txt"), 2);
            assertThat(tasks.totalSize(tmp)).isEqualTo(10L);
        }

        @Test
        @DisplayName("a regular file returns its own length")
        void singleFile() throws IOException {
            File file = fileWithBytes(new File(tmp, "seven.bin"), 7);
            assertThat(tasks.totalSize(file)).isEqualTo(7L);
        }

        @Test
        @DisplayName("an empty directory is 0")
        void emptyDirectory() {
            assertThat(tasks.totalSize(tmp)).isZero();
        }

        @Test
        @DisplayName("a missing path is 0")
        void missing() {
            assertThat(tasks.totalSize(new File(tmp, "missing"))).isZero();
        }

        @Test
        @DisplayName("empty sub-directories add nothing")
        void emptySubDirectories() throws IOException {
            assertThat(new File(tmp, "e1/e2").mkdirs()).isTrue();
            fileWithBytes(new File(tmp, "x.bin"), 4);
            assertThat(tasks.totalSize(tmp)).isEqualTo(4L);
        }
    }

    // ── Task 5: deleteRecursively ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement deleteRecursively()")
    @DisplayName("Task 5 — deleteRecursively()")
    class DeleteRecursivelyTest {

        @Test
        @DisplayName("deletes a regular file")
        void regularFile() throws IOException {
            File file = fileWithBytes(new File(tmp, "one.txt"), 1);
            assertThat(tasks.deleteRecursively(file)).isTrue();
            assertThat(file.exists()).isFalse();
        }

        @Test
        @DisplayName("deletes an empty directory")
        void emptyDirectory() {
            File dir = new File(tmp, "empty");
            assertThat(dir.mkdir()).isTrue();
            assertThat(tasks.deleteRecursively(dir)).isTrue();
            assertThat(dir.exists()).isFalse();
        }

        @Test
        @DisplayName("deletes a whole tree with nested folders and files")
        void wholeTree() throws IOException {
            File root = new File(tmp, "tree");
            fileWithBytes(new File(root, "a.txt"), 1);
            fileWithBytes(new File(root, "sub/b.txt"), 2);
            fileWithBytes(new File(root, "sub/deeper/c.txt"), 3);
            assertThat(tasks.deleteRecursively(root)).isTrue();
            assertThat(root.exists()).isFalse();
        }

        @Test
        @DisplayName("a path that does not exist returns false")
        void missing() {
            assertThat(tasks.deleteRecursively(new File(tmp, "missing"))).isFalse();
        }

        @Test
        @DisplayName("only the target goes away — its siblings stay")
        void siblingsStay() throws IOException {
            fileWithBytes(new File(tmp, "target/x.txt"), 1);
            File sibling = fileWithBytes(new File(tmp, "sibling.txt"), 1);
            tasks.deleteRecursively(new File(tmp, "target"));
            assertThat(sibling.exists()).isTrue();
            assertThat(new File(tmp, "target").exists()).isFalse();
        }
    }
}

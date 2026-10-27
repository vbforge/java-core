package com.vbforge.file_io_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * FileBasicsDemoTest — verifies every concept shown in {@link FileBasicsDemo}.
 *
 * <p>These tests are always GREEN. Every test works inside its own temporary directory
 * ({@code @TempDir}), so nothing depends on the machine's file system layout.
 */
@DisplayName("FileBasicsDemo — API contracts")
class FileBasicsDemoTest {

    @TempDir
    File workDir;

    private void write(File file, String text) throws IOException {
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    // ── 1. The File object ────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. The File object")
    class FileObjectTest {

        @Test
        @DisplayName("creating a File does not touch the disk")
        void noDiskAccess() {
            File notes = new File(workDir, "notes.txt");
            assertThat(notes.exists()).isFalse();
            assertThat(notes.getName()).isEqualTo("notes.txt");
            assertThat(notes.getParentFile()).isEqualTo(workDir);
        }

        @Test
        @DisplayName("a missing path is neither a file nor a directory")
        void missingIsNeither() {
            File missing = new File(workDir, "missing");
            assertThat(missing.isFile()).isFalse();
            assertThat(missing.isDirectory()).isFalse();
        }

        @Test
        @DisplayName("isAbsolute distinguishes absolute from relative paths")
        void absoluteVersusRelative() {
            assertThat(new File(workDir, "x").isAbsolute()).isTrue();
            assertThat(new File("data/input.csv").isAbsolute()).isFalse();
        }

        @Test
        @DisplayName("equals compares path text, not the file it points to")
        void equalsComparesText() {
            assertThat(new File("a.txt")).isEqualTo(new File("a.txt"));
            assertThat(new File("a/../a.txt")).isNotEqualTo(new File("a.txt"));
        }
    }

    // ── 2. Create, inspect, rename, delete ────────────────────────────────────

    @Nested
    @DisplayName("2. Create, inspect, rename, delete")
    class CreateAndInspectTest {

        @Test
        @DisplayName("createNewFile returns true once, then false — never an exception")
        void createNewFile() throws IOException {
            File file = new File(workDir, "created.txt");
            assertThat(file.createNewFile()).isTrue();
            assertThat(file.createNewFile()).isFalse();
            assertThat(file.length()).isZero();
        }

        @Test
        @DisplayName("length counts bytes, not characters")
        void lengthInBytes() throws IOException {
            File file = new File(workDir, "bytes.txt");
            write(file, "héllo");
            assertThat(file.length()).isEqualTo(6L);
        }

        @Test
        @DisplayName("a file exists as a regular file, is readable and has a modification time")
        void inspect() throws IOException {
            File file = new File(workDir, "f.txt");
            write(file, "x");
            assertThat(file.isFile()).isTrue();
            assertThat(file.canRead()).isTrue();
            assertThat(file.lastModified()).isGreaterThan(0L);
        }

        @Test
        @DisplayName("renameTo moves the file")
        void rename() throws IOException {
            File file = new File(workDir, "old.txt");
            File renamed = new File(workDir, "new.txt");
            write(file, "x");
            assertThat(file.renameTo(renamed)).isTrue();
            assertThat(file.exists()).isFalse();
            assertThat(renamed.exists()).isTrue();
        }

        @Test
        @DisplayName("delete returns true, then false for a missing file")
        void delete() throws IOException {
            File file = new File(workDir, "gone.txt");
            write(file, "x");
            assertThat(file.delete()).isTrue();
            assertThat(file.delete()).isFalse();
        }

        @Test
        @DisplayName("a missing file has length 0, just like an empty one")
        void missingLengthIsZero() {
            assertThat(new File(workDir, "missing").length()).isZero();
        }
    }

    // ── 3. Directories ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Directories")
    class DirectoriesTest {

        @Test
        @DisplayName("mkdir fails without parents; mkdirs creates every level")
        void mkdirVersusMkdirs() {
            File deep = new File(workDir, "reports/2024/march");
            assertThat(deep.mkdir()).isFalse();
            assertThat(deep.mkdirs()).isTrue();
            assertThat(deep.isDirectory()).isTrue();
        }

        @Test
        @DisplayName("mkdirs returns false for an existing directory — that is not an error")
        void mkdirsTwice() {
            File dir = new File(workDir, "a/b");
            assertThat(dir.mkdirs()).isTrue();
            assertThat(dir.mkdirs()).isFalse();
            assertThat(dir.isDirectory()).isTrue();
        }

        @Test
        @DisplayName("list returns names in unspecified order — sort before comparing")
        void listNames() throws IOException {
            write(new File(workDir, "summary.txt"), "x");
            write(new File(workDir, "data.csv"), "y");
            new File(workDir, "march").mkdir();
            String[] names = workDir.list();
            Arrays.sort(names);
            assertThat(names).containsExactly("data.csv", "march", "summary.txt");
        }

        @Test
        @DisplayName("listFiles(FileFilter) keeps matching entries only")
        void listFilesWithFilter() throws IOException {
            write(new File(workDir, "a.txt"), "x");
            write(new File(workDir, "b.txt"), "y");
            new File(workDir, "folder").mkdir();
            assertThat(workDir.listFiles(f -> f.isFile())).hasSize(2);
        }

        @Test
        @DisplayName("list(FilenameFilter) filters by name")
        void listWithNameFilter() throws IOException {
            write(new File(workDir, "data.csv"), "x");
            write(new File(workDir, "notes.txt"), "y");
            assertThat(workDir.list((dir, name) -> name.endsWith(".csv"))).containsExactly("data.csv");
        }

        @Test
        @DisplayName("list() returns null for a regular file and for a missing path")
        void nullTrap() throws IOException {
            File file = new File(workDir, "file.txt");
            write(file, "x");
            assertThat(file.list()).isNull();
            assertThat(new File(workDir, "missing").list()).isNull();
        }

        @Test
        @DisplayName("a non-empty directory cannot be deleted with File.delete")
        void deleteNonEmpty() throws IOException {
            File dir = new File(workDir, "dir");
            dir.mkdir();
            write(new File(dir, "inside.txt"), "x");
            assertThat(dir.delete()).isFalse();
            assertThat(dir.exists()).isTrue();
        }
    }

    // ── 4. Walking a tree ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Walking a tree")
    class TreeWalkTest {

        private File buildTree() throws IOException {
            File root = new File(workDir, "tree");
            File deeper = new File(root, "sub/deeper");
            assertThat(deeper.mkdirs()).isTrue();
            write(new File(root, "a.txt"), "aaa");
            write(new File(root, "sub/b.txt"), "bbbbb");
            write(new File(deeper, "c.txt"), "cc");
            return root;
        }

        @Test
        @DisplayName("list() is not recursive — it sees only direct children")
        void listIsShallow() throws IOException {
            File root = buildTree();
            String[] names = root.list();
            Arrays.sort(names);
            assertThat(names).containsExactly("a.txt", "sub");
        }

        @Test
        @DisplayName("a name filter only sees the top level")
        void filterIsShallow() throws IOException {
            File root = buildTree();
            assertThat(root.list((dir, name) -> name.endsWith(".txt"))).containsExactly("a.txt");
        }

        @Test
        @DisplayName("File is Comparable by path, so listFiles results can be sorted")
        void filesAreComparable() throws IOException {
            File root = buildTree();
            File[] children = root.listFiles();
            Arrays.sort(children);
            assertThat(children[0].getName()).isEqualTo("a.txt");
            assertThat(children[1].getName()).isEqualTo("sub");
        }

        @Test
        @DisplayName("listFiles() on a regular file is null — the recursion must guard it")
        void leavesAreNull() throws IOException {
            File root = buildTree();
            assertThat(new File(root, "a.txt").listFiles()).isNull();
        }

        @Test
        @DisplayName("the demo's main() cleanup helper removes a whole tree")
        void deleteTreeHelper() throws IOException {
            File root = buildTree();
            PathFilesDemo.deleteTree(root.toPath());
            assertThat(root.exists()).isFalse();
        }
    }

    // ── 5. Legacy gotchas ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Legacy gotchas")
    class LegacyGotchasTest {

        @Test
        @DisplayName("File.delete on a missing file returns false; Files.delete explains why")
        void deleteMissing() {
            File missing = new File(workDir, "missing.txt");
            assertThat(missing.delete()).isFalse();
            assertThatThrownBy(() -> Files.delete(missing.toPath())).isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("deleting a non-empty directory: false vs DirectoryNotEmptyException")
        void deleteNonEmpty() throws IOException {
            File folder = new File(workDir, "folder");
            assertThat(folder.mkdir()).isTrue();
            assertThat(new File(folder, "inside.txt").createNewFile()).isTrue();
            assertThat(folder.delete()).isFalse();
            assertThatThrownBy(() -> Files.delete(folder.toPath())).isInstanceOf(DirectoryNotEmptyException.class);
        }

        @Test
        @DisplayName("toPath() and toFile() are a lossless bridge")
        void bridge() {
            File file = new File(workDir, "x.txt");
            assertThat(file.toPath().toFile()).isEqualTo(file);
        }
    }
}

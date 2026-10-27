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
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * FileAttributesTasksTest — tests for every method in {@link FileAttributesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code FileAttributesTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=FileAttributesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("FileAttributesTasks — your implementations")
class FileAttributesTasksTest {

    private FileAttributesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FileAttributesTasks();
    }

    /** Creates {@code dir/name} with {@code size} bytes and the given last-modified epoch second. */
    private static Path file(Path dir, String name, int size, long epochSecond) throws IOException {
        Path file = dir.resolve(name);
        Files.createDirectories(file.getParent());
        Files.write(file, new byte[size]);
        Files.setLastModifiedTime(file, FileTime.from(Instant.ofEpochSecond(epochSecond)));
        return file;
    }

    // ── Task 1: kind ──────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement kind()")
    @DisplayName("Task 1 — kind()")
    class KindTest {

        @Test
        @DisplayName("an existing regular file → FILE")
        void regularFile(@TempDir Path tmp) throws IOException {
            Path file = file(tmp, "notes.txt", 3, 1_000);
            assertThat(tasks.kind(file)).isEqualTo("FILE");
        }

        @Test
        @DisplayName("an empty file is still a FILE")
        void emptyFile(@TempDir Path tmp) throws IOException {
            assertThat(tasks.kind(Files.createFile(tmp.resolve("empty")))).isEqualTo("FILE");
        }

        @Test
        @DisplayName("an existing directory → DIRECTORY")
        void directory(@TempDir Path tmp) {
            assertThat(tasks.kind(tmp)).isEqualTo("DIRECTORY");
        }

        @Test
        @DisplayName("a missing path → MISSING")
        void missing(@TempDir Path tmp) {
            assertThat(tasks.kind(tmp.resolve("nope.txt"))).isEqualTo("MISSING");
        }

        @Test
        @DisplayName("a path below a missing parent → MISSING (no exception)")
        void missingParent(@TempDir Path tmp) {
            assertThat(tasks.kind(tmp.resolve("no").resolve("such").resolve("dir").resolve("x"))).isEqualTo("MISSING");
        }
    }

    // ── Task 2: isNewerThan ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isNewerThan()")
    @DisplayName("Task 2 — isNewerThan()")
    class IsNewerThanTest {

        @Test
        @DisplayName("a@2000 is newer than b@1000")
        void newer(@TempDir Path tmp) throws IOException {
            Path a = file(tmp, "a", 1, 2_000);
            Path b = file(tmp, "b", 1, 1_000);
            assertThat(tasks.isNewerThan(a, b)).isTrue();
        }

        @Test
        @DisplayName("a@1000 is not newer than b@2000")
        void older(@TempDir Path tmp) throws IOException {
            Path a = file(tmp, "a", 1, 1_000);
            Path b = file(tmp, "b", 1, 2_000);
            assertThat(tasks.isNewerThan(a, b)).isFalse();
        }

        @Test
        @DisplayName("equal timestamps → false (strictly later is required)")
        void equal(@TempDir Path tmp) throws IOException {
            Path a = file(tmp, "a", 1, 1_000);
            Path b = file(tmp, "b", 1, 1_000);
            assertThat(tasks.isNewerThan(a, b)).isFalse();
        }

        @Test
        @DisplayName("a missing file throws NoSuchFileException")
        void missing(@TempDir Path tmp) throws IOException {
            Path a = file(tmp, "a", 1, 1_000);
            Path missing = tmp.resolve("missing");
            assertThatThrownBy(() -> tasks.isNewerThan(a, missing)).isInstanceOf(NoSuchFileException.class);
            assertThatThrownBy(() -> tasks.isNewerThan(missing, a)).isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("works for directories as well")
        void directories(@TempDir Path tmp) throws IOException {
            Path older = Files.createDirectory(tmp.resolve("older"));
            Path newer = Files.createDirectory(tmp.resolve("newer"));
            Files.setLastModifiedTime(older, FileTime.from(Instant.ofEpochSecond(1_000)));
            Files.setLastModifiedTime(newer, FileTime.from(Instant.ofEpochSecond(5_000)));
            assertThat(tasks.isNewerThan(newer, older)).isTrue();
        }
    }

    // ── Task 3: touch ─────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement touch()")
    @DisplayName("Task 3 — touch()")
    class TouchTest {

        @Test
        @DisplayName("creates a missing file, empty, with the requested modification time")
        void createsFile(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("new.txt");
            Instant when = Instant.parse("2020-09-13T12:26:40Z");
            tasks.touch(file, when);

            assertThat(Files.exists(file)).isTrue();
            assertThat(Files.size(file)).isZero();
            assertThat(Files.getLastModifiedTime(file).toInstant()).isEqualTo(when);
        }

        @Test
        @DisplayName("an existing file keeps its content and gets the new time")
        void keepsContent(@TempDir Path tmp) throws IOException {
            Path file = file(tmp, "old.txt", 0, 5_000);
            Files.write(file, "data".getBytes(StandardCharsets.UTF_8));
            Instant when = Instant.parse("2001-09-09T01:46:40Z");

            tasks.touch(file, when);

            assertThat(new String(Files.readAllBytes(file), StandardCharsets.UTF_8)).isEqualTo("data");
            assertThat(Files.getLastModifiedTime(file).toInstant()).isEqualTo(when);
        }

        @Test
        @DisplayName("touching twice is fine — the second time wins")
        void twice(@TempDir Path tmp) throws IOException {
            Path file = tmp.resolve("f.txt");
            tasks.touch(file, Instant.ofEpochSecond(1_000_000_000L));
            tasks.touch(file, Instant.ofEpochSecond(1_500_000_000L));
            assertThat(Files.getLastModifiedTime(file).toInstant())
                    .isEqualTo(Instant.ofEpochSecond(1_500_000_000L));
        }

        @Test
        @DisplayName("a missing parent directory is an IOException")
        void missingParent(@TempDir Path tmp) {
            Path file = tmp.resolve("no-such-dir").resolve("f.txt");
            assertThatThrownBy(() -> tasks.touch(file, Instant.ofEpochSecond(1_000_000_000L)))
                    .isInstanceOf(IOException.class);
        }
    }

    // ── Task 4: newestFile ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement newestFile()")
    @DisplayName("Task 4 — newestFile()")
    class NewestFileTest {

        @Test
        @DisplayName("a@1000, b@3000, c@2000 → b")
        void picksNewest(@TempDir Path tmp) throws IOException {
            file(tmp, "a.txt", 1, 1_000);
            Path b = file(tmp, "b.txt", 1, 3_000);
            file(tmp, "c.txt", 1, 2_000);
            assertThat(tasks.newestFile(tmp)).contains(b);
        }

        @Test
        @DisplayName("a newer sub-directory is ignored")
        void ignoresDirectories(@TempDir Path tmp) throws IOException {
            Path a = file(tmp, "a.txt", 1, 1_000);
            Path logs = Files.createDirectory(tmp.resolve("logs"));
            Files.setLastModifiedTime(logs, FileTime.from(Instant.ofEpochSecond(9_000)));
            assertThat(tasks.newestFile(tmp)).contains(a);
        }

        @Test
        @DisplayName("only sub-directories → Optional.empty()")
        void onlyDirectories(@TempDir Path tmp) throws IOException {
            Files.createDirectory(tmp.resolve("logs"));
            assertThat(tasks.newestFile(tmp)).isEqualTo(Optional.empty());
        }

        @Test
        @DisplayName("an empty directory → Optional.empty()")
        void emptyDirectory(@TempDir Path tmp) throws IOException {
            assertThat(tasks.newestFile(tmp)).isEmpty();
        }

        @Test
        @DisplayName("not recursive — a newer file in a sub-directory is ignored")
        void notRecursive(@TempDir Path tmp) throws IOException {
            Path top = file(tmp, "top.txt", 1, 1_000);
            file(tmp, "sub/deep.txt", 1, 8_000);
            assertThat(tasks.newestFile(tmp)).contains(top);
        }

        @Test
        @DisplayName("a single file is the newest")
        void single(@TempDir Path tmp) throws IOException {
            Path only = file(tmp, "only.txt", 1, 1_000);
            assertThat(tasks.newestFile(tmp)).contains(only);
        }
    }

    // ── Task 5: namesLargerThan ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement namesLargerThan()")
    @DisplayName("Task 5 — namesLargerThan()")
    class NamesLargerThanTest {

        private Path tree(Path root) throws IOException {
            file(root, "small.txt", 1, 1_000);
            file(root, "mid.txt", 5, 1_000);
            file(root, "sub/big.bin", 10, 1_000);
            file(root, "empty.dat", 0, 1_000);
            return root;
        }

        @Test
        @DisplayName("> 4 bytes → [big.bin, mid.txt]")
        void basic(@TempDir Path tmp) throws IOException {
            assertThat(tasks.namesLargerThan(tree(tmp), 4)).containsExactly("big.bin", "mid.txt");
        }

        @Test
        @DisplayName("the bound is exclusive: > 5 bytes → [big.bin]")
        void exclusive(@TempDir Path tmp) throws IOException {
            assertThat(tasks.namesLargerThan(tree(tmp), 5)).containsExactly("big.bin");
        }

        @Test
        @DisplayName("a huge bound → empty list")
        void none(@TempDir Path tmp) throws IOException {
            assertThat(tasks.namesLargerThan(tree(tmp), 100)).isEmpty();
        }

        @Test
        @DisplayName("> 0 bytes excludes empty files")
        void excludesEmpty(@TempDir Path tmp) throws IOException {
            assertThat(tasks.namesLargerThan(tree(tmp), 0))
                    .containsExactly("big.bin", "mid.txt", "small.txt");
        }

        @Test
        @DisplayName("directories are never returned")
        void directoriesIgnored(@TempDir Path tmp) throws IOException {
            Files.createDirectories(tmp.resolve("folder.bin"));
            file(tmp, "x.bin", 3, 1_000);
            assertThat(tasks.namesLargerThan(tmp, 0)).containsExactly("x.bin");
        }
    }

    // ── Task 6: permissionsToOctal ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement permissionsToOctal()")
    @DisplayName("Task 6 — permissionsToOctal()")
    class PermissionsToOctalTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "rw-r--r--, 644",
                "rwxr-xr-x, 755",
                "rwx------, 700",
                "---------, 000",
                "rwxrwxrwx, 777",
                "r--r-----, 440",
                "-w--w--w-, 222",
                "--x--x--x, 111",
        })
        @DisplayName("converts permission strings to octal digits")
        void conversions(String rwx, String octal) {
            assertThat(tasks.permissionsToOctal(rwx)).isEqualTo(octal);
        }

        @Test
        @DisplayName("a string of the wrong length throws IllegalArgumentException")
        void wrongLength() {
            assertThatThrownBy(() -> tasks.permissionsToOctal("rwxrwx"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.permissionsToOctal(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an invalid character throws IllegalArgumentException")
        void invalidCharacter() {
            assertThatThrownBy(() -> tasks.permissionsToOctal("rwxrwxrwz"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the result always has exactly three characters (leading zeros kept)")
        void keepsLeadingZeros() {
            assertThat(tasks.permissionsToOctal("------r--")).hasSize(3).isEqualTo("004");
        }
    }
}

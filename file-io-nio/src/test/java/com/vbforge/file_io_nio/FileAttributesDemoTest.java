package com.vbforge.file_io_nio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * FileAttributesDemoTest — verifies every concept shown in {@link FileAttributesDemo}.
 *
 * <p>These tests are always GREEN. Tests that need an optional file-system feature
 * (POSIX permissions, symbolic links) are <em>skipped</em> — not failed — where the
 * feature is unavailable, so the build stays portable.
 */
@DisplayName("FileAttributesDemo — API contracts")
class FileAttributesDemoTest {

    private static final Instant MOMENT = Instant.ofEpochSecond(1_600_000_000L);

    private static Path fileWith(Path dir, String name, byte[] content) throws IOException {
        return Files.write(dir.resolve(name), content);
    }

    // ── 1. Basic attributes ───────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Basic attributes")
    class BasicAttributesTest {

        @Test
        @DisplayName("readAttributes reports size and type flags for a file")
        void fileAttributes(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "hello.txt", "hello".getBytes(StandardCharsets.UTF_8));
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            assertThat(attrs.size()).isEqualTo(5L);
            assertThat(attrs.isRegularFile()).isTrue();
            assertThat(attrs.isDirectory()).isFalse();
            assertThat(attrs.isSymbolicLink()).isFalse();
        }

        @Test
        @DisplayName("readAttributes reports isDirectory for a directory")
        void directoryAttributes(@TempDir Path tmp) throws IOException {
            BasicFileAttributes attrs = Files.readAttributes(tmp, BasicFileAttributes.class);
            assertThat(attrs.isDirectory()).isTrue();
            assertThat(attrs.isRegularFile()).isFalse();
        }

        @Test
        @DisplayName("all three timestamps are present")
        void timestampsPresent(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "f.txt", new byte[0]);
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            assertThat(attrs.lastModifiedTime()).isNotNull();
            assertThat(attrs.creationTime()).isNotNull();
            assertThat(attrs.lastAccessTime()).isNotNull();
        }

        @Test
        @DisplayName("the attributes snapshot agrees with the convenience methods")
        void agreesWithShortcuts(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "f.bin", new byte[]{1, 2, 3});
            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            assertThat(attrs.size()).isEqualTo(Files.size(file));
            assertThat(attrs.lastModifiedTime()).isEqualTo(Files.getLastModifiedTime(file));
        }

        @Test
        @DisplayName("exists() is false for a missing file, but readAttributes explains why")
        void missingFile(@TempDir Path tmp) {
            Path missing = tmp.resolve("nope");
            assertThat(Files.exists(missing)).isFalse();
            assertThatThrownBy(() -> Files.readAttributes(missing, BasicFileAttributes.class))
                    .isInstanceOf(NoSuchFileException.class);
        }
    }

    // ── 2. Timestamps ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Timestamps")
    class TimestampsTest {

        @Test
        @DisplayName("setLastModifiedTime round-trips a whole-second Instant")
        void roundTrip(@TempDir Path tmp) throws IOException {
            Path file = Files.createFile(tmp.resolve("f.txt"));
            Files.setLastModifiedTime(file, FileTime.from(MOMENT));
            assertThat(Files.getLastModifiedTime(file).toInstant()).isEqualTo(MOMENT);
        }

        @Test
        @DisplayName("FileTime converts to and from Instant and epoch millis")
        void conversions() {
            FileTime ft = FileTime.from(MOMENT);
            assertThat(ft.toInstant()).isEqualTo(MOMENT);
            assertThat(ft.toMillis()).isEqualTo(1_600_000_000_000L);
            assertThat(FileTime.fromMillis(1_600_000_000_000L)).isEqualTo(ft);
        }

        @Test
        @DisplayName("FileTime is Comparable — older sorts before newer")
        void comparable() {
            FileTime older = FileTime.from(Instant.ofEpochSecond(1_000_000_000L));
            FileTime newer = FileTime.from(MOMENT);
            assertThat(older.compareTo(newer)).isNegative();
            assertThat(newer.compareTo(older)).isPositive();
            assertThat(newer.compareTo(FileTime.from(MOMENT))).isZero();
        }

        @Test
        @DisplayName("directories have settable timestamps too")
        void directoryTimestamp(@TempDir Path tmp) throws IOException {
            Path dir = Files.createDirectory(tmp.resolve("d"));
            Files.setLastModifiedTime(dir, FileTime.from(MOMENT));
            assertThat(Files.getLastModifiedTime(dir).toInstant()).isEqualTo(MOMENT);
        }
    }

    // ── 3. Attribute views ────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Attribute views")
    class AttributeViewsTest {

        @Test
        @DisplayName("every file system supports the 'basic' view")
        void basicAlwaysSupported() {
            assertThat(FileSystems.getDefault().supportedFileAttributeViews()).contains("basic");
        }

        @Test
        @DisplayName("getAttribute returns a single value as Object (size is a Long)")
        void getAttribute(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "data.bin", new byte[]{1, 2, 3, 4, 5, 6, 7});
            assertThat(Files.getAttribute(file, "size")).isEqualTo(7L);
        }

        @Test
        @DisplayName("readAttributes(path, \"a,b\") returns only the requested attributes")
        void selectedAttributes(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "data.bin", new byte[]{1, 2, 3});
            Map<String, Object> some = Files.readAttributes(file, "size,isDirectory");
            assertThat(some).containsEntry("size", 3L).containsEntry("isDirectory", false).hasSize(2);
        }

        @Test
        @DisplayName("'basic:*' returns the whole basic view")
        void wholeView(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "data.bin", new byte[]{1});
            Map<String, Object> all = Files.readAttributes(file, "basic:*");
            assertThat(all).containsKeys("size", "lastModifiedTime", "creationTime",
                    "isRegularFile", "isDirectory", "isSymbolicLink");
        }

        @Test
        @DisplayName("a view can write: setTimes updates only the non-null values")
        void writeThroughView(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "data.bin", new byte[]{1});
            Files.setLastModifiedTime(file, FileTime.from(MOMENT));

            BasicFileAttributeView view = Files.getFileAttributeView(file, BasicFileAttributeView.class);
            FileTime changed = FileTime.from(Instant.ofEpochSecond(1_500_000_000L));
            view.setTimes(changed, null, null);

            assertThat(view.readAttributes().lastModifiedTime().toInstant())
                    .isEqualTo(Instant.ofEpochSecond(1_500_000_000L));
        }

        @Test
        @DisplayName("an unknown attribute name throws IllegalArgumentException")
        void unknownAttribute(@TempDir Path tmp) throws IOException {
            Path file = fileWith(tmp, "data.bin", new byte[]{1});
            assertThatThrownBy(() -> Files.getAttribute(file, "bogus"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── 4. POSIX permissions ──────────────────────────────────────────────────

    @Nested
    @DisplayName("4. POSIX permissions")
    class PosixPermissionsTest {

        @Test
        @DisplayName("fromString parses rw-r--r-- into four permission constants")
        void fromString() {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
            assertThat(perms).containsExactlyInAnyOrder(
                    PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ);
        }

        @Test
        @DisplayName("toString is the inverse of fromString")
        void roundTrip() {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
            assertThat(PosixFilePermissions.toString(perms)).isEqualTo("rwxr-x---");
        }

        @Test
        @DisplayName("an invalid permission string throws IllegalArgumentException")
        void invalidString() {
            assertThatThrownBy(() -> PosixFilePermissions.fromString("rwx"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> PosixFilePermissions.fromString("rwxrwxrwz"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("setPosixFilePermissions is read back exactly (skipped on non-POSIX systems)")
        void onDisk(@TempDir Path tmp) throws IOException {
            assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"),
                    "POSIX attribute view not supported on this file system");
            Path file = Files.createFile(tmp.resolve("secret.txt"));
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
            Set<PosixFilePermission> now = Files.getPosixFilePermissions(file);
            assertThat(PosixFilePermissions.toString(now)).isEqualTo("rw-------");
            assertThat(now).doesNotContain(PosixFilePermission.OTHERS_READ);
        }
    }

    // ── 5. Symbolic links ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Symbolic links")
    class SymbolicLinksTest {

        /** Creates link → real.txt, or skips the test where symbolic links cannot be created. */
        private Path[] linkFixture(Path tmp) throws IOException {
            Path target = fileWith(tmp, "real.txt", "data".getBytes(StandardCharsets.UTF_8));
            Path link = tmp.resolve("alias.txt");
            try {
                Files.createSymbolicLink(link, Paths.get("real.txt"));
            } catch (UnsupportedOperationException | IOException e) {
                assumeTrue(false, "symbolic links are not available here: " + e);
            }
            return new Path[]{link, target};
        }

        @Test
        @DisplayName("isSymbolicLink and readSymbolicLink describe the link itself")
        void linkIdentity(@TempDir Path tmp) throws IOException {
            Path link = linkFixture(tmp)[0];
            assertThat(Files.isSymbolicLink(link)).isTrue();
            assertThat(Files.readSymbolicLink(link)).isEqualTo(Paths.get("real.txt"));
        }

        @Test
        @DisplayName("by default links are followed; NOFOLLOW_LINKS asks about the link itself")
        void followVsNoFollow(@TempDir Path tmp) throws IOException {
            Path link = linkFixture(tmp)[0];
            assertThat(Files.isRegularFile(link)).isTrue();
            assertThat(Files.isRegularFile(link, LinkOption.NOFOLLOW_LINKS)).isFalse();
        }

        @Test
        @DisplayName("deleting a link leaves the target untouched")
        void deleteLink(@TempDir Path tmp) throws IOException {
            Path[] fixture = linkFixture(tmp);
            Files.delete(fixture[0]);
            assertThat(Files.exists(fixture[0], LinkOption.NOFOLLOW_LINKS)).isFalse();
            assertThat(Files.exists(fixture[1])).isTrue();
        }

        @Test
        @DisplayName("a dangling link: exists() is false, exists(NOFOLLOW_LINKS) is true")
        void danglingLink(@TempDir Path tmp) throws IOException {
            Path[] fixture = linkFixture(tmp);
            Files.delete(fixture[1]);                              // remove the target
            assertThat(Files.exists(fixture[0])).isFalse();
            assertThat(Files.exists(fixture[0], LinkOption.NOFOLLOW_LINKS)).isTrue();
        }
    }
}

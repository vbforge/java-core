package com.vbforge.file_io_nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
import java.util.TreeSet;

/**
 * FileAttributesDemo — annotated reference for NIO.2 file metadata.
 *
 * <p>Topics:
 * <ol>
 *   <li>Basic attributes — size, type flags and timestamps in <em>one</em> call</li>
 *   <li>Timestamps — {@link FileTime}, converting to/from {@link Instant}, changing them</li>
 *   <li>Attribute views — the string-based API and which views a file system supports</li>
 *   <li>POSIX permissions — {@code rwxr-xr-x} as a {@link Set} of {@link PosixFilePermission}</li>
 *   <li>Symbolic links — following vs not following with {@link LinkOption#NOFOLLOW_LINKS}</li>
 * </ol>
 *
 * <p>Not every file system supports every attribute. That is the central design idea of
 * the {@code java.nio.file.attribute} package: attributes are grouped into <em>views</em>,
 * and only the {@code basic} view is guaranteed to exist.
 */
public class FileAttributesDemo {

    public static void main(String[] args) throws IOException {
        basicAttributesDemo();
        System.out.println("---");
        timestampsDemo();
        System.out.println("---");
        attributeViewsDemo();
        System.out.println("---");
        posixPermissionsDemo();
        System.out.println("---");
        symbolicLinksDemo();
    }

    // ── 1. Basic attributes ───────────────────────────────────────────────────

    /**
     * {@link BasicFileAttributes} bundles size, type flags and timestamps into one immutable snapshot.
     *
     * <p>WHY this matters: {@code File.length()}, {@code File.isDirectory()}, {@code File.lastModified()}
     * each hit the file system separately (and returned {@code 0}/{@code false} on error). One
     * {@code Files.readAttributes} call is one system call and <em>throws</em> if the file is missing.
     * It is also a consistent snapshot — size and timestamp belong to the same instant.
     *
     * <p>Complexity: O(1) — one {@code stat}.
     *
     * <p>Common gotcha: the convenience checks {@code Files.exists/isDirectory/isRegularFile} return
     * {@code false} both when the answer is "no" and when the check itself failed (e.g. no permission).
     */
    static void basicAttributesDemo() throws IOException {
        System.out.println("=== Basic attributes ===");

        Path dir = Files.createTempDirectory("nio-attr-");
        try {
            Path file = dir.resolve("hello.txt");
            Files.write(file, "hello".getBytes(StandardCharsets.UTF_8));

            BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
            System.out.println("size           : " + attrs.size());              // 5
            System.out.println("isRegularFile  : " + attrs.isRegularFile());     // true
            System.out.println("isDirectory    : " + attrs.isDirectory());       // false
            System.out.println("isSymbolicLink : " + attrs.isSymbolicLink());    // false
            System.out.println("has timestamps : " + (attrs.lastModifiedTime() != null
                    && attrs.creationTime() != null && attrs.lastAccessTime() != null));

            BasicFileAttributes dirAttrs = Files.readAttributes(dir, BasicFileAttributes.class);
            System.out.println("dir isDirectory: " + dirAttrs.isDirectory());    // true

            // Convenience shortcuts — each is a separate system call
            System.out.println("Files.size     : " + Files.size(file));
            System.out.println("exists missing : " + Files.exists(dir.resolve("nope")));   // false

            // But readAttributes on a missing file tells you WHY
            try {
                Files.readAttributes(dir.resolve("nope"), BasicFileAttributes.class);
            } catch (IOException e) {
                System.out.println("missing        : " + e.getClass().getSimpleName());     // NoSuchFileException
            }
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }

    // ── 2. Timestamps ─────────────────────────────────────────────────────────

    /**
     * Timestamps are {@link FileTime} objects: an immutable point in time with
     * <em>file-system-dependent precision</em> (1 s on some, nanoseconds on others).
     *
     * <p>WHY this matters: "newer than", "backup if changed", "delete logs older than 30 days" all
     * compare timestamps. {@code FileTime} converts losslessly to {@link Instant} — no
     * timezone is involved, so there is nothing locale- or clock-zone-dependent to get wrong.
     *
     * <p>Common gotcha: a timestamp you <em>set</em> may be truncated to the file system's precision
     * when read back. Whole seconds are safe on every file system.
     */
    static void timestampsDemo() throws IOException {
        System.out.println("=== Timestamps ===");

        Path dir = Files.createTempDirectory("nio-time-");
        try {
            Path file = Files.createFile(dir.resolve("f.txt"));

            Instant moment = Instant.ofEpochSecond(1_600_000_000L);            // 2020-09-13T12:26:40Z
            Files.setLastModifiedTime(file, FileTime.from(moment));
            System.out.println("read back      : " + Files.getLastModifiedTime(file).toInstant());

            // FileTime ⇄ Instant ⇄ millis
            FileTime ft = FileTime.from(moment);
            System.out.println("toMillis       : " + ft.toMillis());             // 1600000000000
            System.out.println("fromMillis     : " + FileTime.fromMillis(1_600_000_000_000L).toInstant());

            // FileTime is Comparable → "which is newer?" without arithmetic
            FileTime older = FileTime.from(Instant.ofEpochSecond(1_000_000_000L));
            System.out.println("older < newer  : " + (older.compareTo(ft) < 0)); // true

            // Directories have timestamps too
            Files.setLastModifiedTime(dir, FileTime.from(moment));
            System.out.println("dir time       : " + Files.getLastModifiedTime(dir).toInstant());
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }

    // ── 3. Attribute views ────────────────────────────────────────────────────

    /**
     * An <em>attribute view</em> is a named group of attributes. {@code basic} is always present;
     * {@code posix} (Linux/macOS), {@code dos} (Windows), {@code owner}, {@code acl}, {@code user}
     * depend on the file system.
     *
     * <p>Three ways to read attributes, from most type-safe to most flexible:
     * <ul>
     *   <li>{@code Files.readAttributes(path, BasicFileAttributes.class)} — typed snapshot</li>
     *   <li>{@code Files.getFileAttributeView(path, XxxView.class)} — a view you can also <em>write</em> through</li>
     *   <li>{@code Files.getAttribute(path, "view:name")} / {@code readAttributes(path, "view:a,b")} —
     *       string-based, returns {@code Object}/{@code Map}, handy for dynamic code</li>
     * </ul>
     *
     * <p>WHY this matters: portable code can use {@code basic} everywhere and feature-detect the rest
     * with {@code supportedFileAttributeViews()} instead of failing on Windows.
     *
     * <p>Common gotcha: asking for an unsupported view <em>by class</em> returns {@code null};
     * asking by string throws {@link UnsupportedOperationException}.
     */
    static void attributeViewsDemo() throws IOException {
        System.out.println("=== Attribute views ===");

        Set<String> views = new TreeSet<>(FileSystems.getDefault().supportedFileAttributeViews());
        System.out.println("supported views: " + views);                         // always contains "basic"

        Path dir = Files.createTempDirectory("nio-views-");
        try {
            Path file = dir.resolve("data.bin");
            Files.write(file, new byte[]{1, 2, 3, 4, 5, 6, 7});

            // String-based: single attribute
            Object size = Files.getAttribute(file, "size");
            System.out.println("getAttribute   : " + size + " (" + size.getClass().getSimpleName() + ")");  // 7 (Long)

            // String-based: several attributes at once → Map
            Map<String, Object> some = Files.readAttributes(file, "size,isDirectory");
            System.out.println("selected       : " + new java.util.TreeMap<>(some));

            // "basic:*" — everything in the basic view
            Map<String, Object> all = Files.readAttributes(file, "basic:*");
            System.out.println("basic:* keys   : " + new TreeSet<>(all.keySet()));

            // A view lets you WRITE: setTimes(lastModified, lastAccess, create) — null leaves a value alone
            BasicFileAttributeView view = Files.getFileAttributeView(file, BasicFileAttributeView.class);
            view.setTimes(FileTime.from(Instant.ofEpochSecond(1_500_000_000L)), null, null);
            System.out.println("via view       : " + view.readAttributes().lastModifiedTime().toInstant());

            // Unknown attribute name → IllegalArgumentException
            try {
                Files.getAttribute(file, "bogus");
            } catch (IllegalArgumentException e) {
                System.out.println("unknown name   : IllegalArgumentException");
            }
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }

    // ── 4. POSIX permissions ──────────────────────────────────────────────────

    /**
     * POSIX permissions ({@code rwxr-xr-x}) are modelled as a {@link Set} of {@link PosixFilePermission}
     * constants — nine possible values: {@code OWNER_/GROUP_/OTHERS_} × {@code READ/WRITE/EXECUTE}.
     * {@link PosixFilePermissions} converts between the familiar 9-character string and that Set.
     *
     * <p>WHY this matters: the string conversion is pure (no file needed) and validates input for you.
     * Applying it to a real file only works where the {@code posix} view exists — check first.
     *
     * <p>Common gotcha: permissions passed to {@code Files.createFile(path, attribute)} are filtered by the
     * process <em>umask</em>, so you may get fewer bits than you asked for. Call
     * {@code Files.setPosixFilePermissions} afterwards when you need exact permissions.
     */
    static void posixPermissionsDemo() throws IOException {
        System.out.println("=== POSIX permissions ===");

        // Pure string ⇄ Set conversion — works on every OS
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
        System.out.println("fromString     : " + new TreeSet<>(perms));
        System.out.println("toString       : " + PosixFilePermissions.toString(perms));   // rw-r--r--

        try {
            PosixFilePermissions.fromString("rwx");
        } catch (IllegalArgumentException e) {
            System.out.println("bad string     : IllegalArgumentException");
        }

        // Applying them to a file requires the "posix" view
        boolean posix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
        System.out.println("posix supported: " + posix);
        if (!posix) {
            return;
        }

        Path dir = Files.createTempDirectory("nio-posix-");
        try {
            Path file = Files.createFile(dir.resolve("secret.txt"));
            Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
            Set<PosixFilePermission> now = Files.getPosixFilePermissions(file);
            System.out.println("on disk        : " + PosixFilePermissions.toString(now)); // rw-------
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }

    // ── 5. Symbolic links ─────────────────────────────────────────────────────

    /**
     * By default NIO.2 <em>follows</em> symbolic links: {@code Files.isRegularFile(link)} answers about
     * the <em>target</em>. Pass {@link LinkOption#NOFOLLOW_LINKS} to ask about the link itself.
     *
     * <p>WHY this matters: code that deletes or measures files must decide whether a link counts as
     * "the file" or "a pointer to it". {@code Files.delete(link)} removes the link only, never the target.
     *
     * <p>Common gotcha: a <em>dangling</em> link (target deleted) makes {@code Files.exists(link)}
     * return {@code false} — yet the link is still there; {@code exists(link, NOFOLLOW_LINKS)} sees it.
     *
     * <p>Creating symbolic links needs OS support (and on Windows a privilege), so this demo skips
     * itself gracefully when it cannot.
     */
    static void symbolicLinksDemo() throws IOException {
        System.out.println("=== Symbolic links ===");

        Path dir = Files.createTempDirectory("nio-link-");
        try {
            Path target = Files.write(dir.resolve("real.txt"), "data".getBytes(StandardCharsets.UTF_8));
            Path link = dir.resolve("alias.txt");
            try {
                Files.createSymbolicLink(link, Paths.get("real.txt"));          // relative target
            } catch (UnsupportedOperationException | IOException e) {
                System.out.println("symlinks unavailable on this system — skipping");
                return;
            }

            System.out.println("isSymbolicLink : " + Files.isSymbolicLink(link));                       // true
            System.out.println("readSymbolicLink: " + Files.readSymbolicLink(link));                     // real.txt
            System.out.println("isRegularFile  : " + Files.isRegularFile(link));                         // true  (follows)
            System.out.println("  NOFOLLOW     : " + Files.isRegularFile(link, LinkOption.NOFOLLOW_LINKS)); // false

            Files.delete(link);                                                  // removes only the link
            System.out.println("target survives: " + Files.exists(target));      // true

            // dangling link
            Files.createSymbolicLink(link, Paths.get("real.txt"));
            Files.delete(target);
            System.out.println("dangling exists: " + Files.exists(link));                                // false
            System.out.println("  NOFOLLOW     : " + Files.exists(link, LinkOption.NOFOLLOW_LINKS));    // true
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }
}

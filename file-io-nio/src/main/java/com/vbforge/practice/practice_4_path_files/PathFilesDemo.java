package com.vbforge.practice.practice_4_path_files;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Demonstrates the NIO.2 Path and Files API — the modern filesystem layer
 * introduced in Java 7 and enhanced in Java 8/11.
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  java.nio.file package (NIO.2 — Java 7+)                               │
 * │                                                                         │
 * │  Path    — immutable value representing a filesystem path.              │
 * │            No disk access happens when you construct or manipulate it.  │
 * │                                                                         │
 * │  Files   — static utility class for actual I/O and metadata operations.│
 * │            Always throws checked IOException on failure (unlike File).  │
 * │                                                                         │
 * │  Paths   — factory for Path via Paths.get() (superseded by Path.of()). │
 * │                                                                         │
 * │  FileSystem / FileSystems — plug-in model; ZipFileSystem, custom VFS.  │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * NIO.2 vs legacy java.io.File:
 *   File                     │  Path + Files
 *   ─────────────────────────┼──────────────────────────────────────────
 *   returns false on error   │  throws IOException — explicit error handling
 *   path + I/O mixed         │  path (value) separated from I/O (Files)
 *   no symbolic link support │  full symlink awareness
 *   no file attributes       │  BasicFileAttributes, PosixFileAttributes
 *   no directory streams     │  Files.list(), walk(), find() → Stream<Path>
 *   no WatchService          │  WatchService for filesystem events
 *
 * This module focuses on the NIO.2-specific capabilities that go beyond what
 * was covered in the _09_io.file_api module (which covered the basics).
 */
public class PathFilesDemo {

    static final Path DATA_DIR = Path.of("file-io-nio/data_files");

    /** Truncate a line for display; avoids StringIndexOutOfBounds on short files. */
    private static String preview(String line, int max) {
        return line.length() <= max ? line : line.substring(0, max) + "…";
    }

    // =========================================================================
    // 1. Path construction and the FileSystem abstraction
    // =========================================================================

    /**
     * Path.of() is the standard factory since Java 11.
     * Paths.get() (Java 7) is identical — just older spelling.
     * Path.of(URI) allows URIs: Path.of(URI.create("file:///tmp/x.txt"))
     *
     * The default filesystem is the OS filesystem.
     * FileSystems.newFileSystem(zipPath) opens a ZIP as a virtual filesystem.
     */
    static void pathConstructionDemo() {
        System.out.println("\n--- 1. Path construction ---");

        // Relative path
        Path rel = Path.of("src", "main", "java", "App.java");
        System.out.println("relative:          " + rel);

        // Absolute path (platform-aware — shows correct separator)
        Path abs = rel.toAbsolutePath();
        System.out.println("toAbsolutePath():  " + abs);

        // Path from URI
        Path uri = Path.of(java.net.URI.create("file:///tmp/example.txt"));
        System.out.println("from URI:          " + uri);

        // Path.of with multiple segments — auto-joins with separator
        Path joined = Path.of("com", "vbforge", "Main.java");
        System.out.println("multi-segment:     " + joined);

        // Default FileSystem
        FileSystem fs = FileSystems.getDefault();
        System.out.println("separator:         '" + fs.getSeparator() + "'");
        System.out.println("rootDirs:          " + fs.getRootDirectories());
    }

    // =========================================================================
    // 2. Path operations — purely structural (no disk access)
    // =========================================================================

    /**
     * All operations below work on the path VALUE, not the actual file.
     * A path to a non-existent file is perfectly valid here.
     */
    static void pathOperationsDemo() {
        System.out.println("\n--- 2. Path operations (structural) ---");

        Path p = Path.of("src/main/java/com/vbforge/App.java");

        // Decomposition
        System.out.println("toString():        " + p);
        System.out.println("getFileName():     " + p.getFileName());
        System.out.println("getParent():       " + p.getParent());
        System.out.println("getRoot():         " + p.getRoot()); // null → relative
        System.out.println("getNameCount():    " + p.getNameCount());

        // Name elements — zero-indexed
        for (int i = 0; i < p.getNameCount(); i++) {
            System.out.println("  getName(" + i + "):   " + p.getName(i));
        }

        // Subpath
        System.out.println("subpath(2,5):      " + p.subpath(2, 5));

        // resolve / relativize
        Path base    = Path.of("src/main");
        Path sibling = base.resolve("resources/config.yaml");
        System.out.println("resolve:           " + sibling);

        Path from = Path.of("src/main/java");
        Path to   = Path.of("src/main/resources/app.yaml");
        System.out.println("relativize:        " + from.relativize(to)); // ../resources/app.yaml

        // normalize — collapses . and ..
        Path messy = Path.of("src/../src/./main/./java/../App.java");
        System.out.println("normalize:         " + messy.normalize());

        // compareTo / equals — lexicographic on the path string
        Path a = Path.of("src/A.java");
        Path b = Path.of("src/B.java");
        System.out.println("a.compareTo(b):    " + a.compareTo(b));
    }

    // =========================================================================
    // 3. Files metadata — existence, type, size, timestamps
    // =========================================================================

    static void filesMetadataDemo() throws IOException {
        System.out.println("\n--- 3. Files metadata ---");

        Path sample = DATA_DIR.resolve("sample.txt");

        // Basic checks
        System.out.println("exists():          " + Files.exists(sample));
        System.out.println("notExists():       " + Files.notExists(sample));
        System.out.println("isRegularFile():   " + Files.isRegularFile(sample));
        System.out.println("isDirectory():     " + Files.isDirectory(DATA_DIR));
        System.out.println("isReadable():      " + Files.isReadable(sample));
        System.out.println("isWritable():      " + Files.isWritable(sample));
        System.out.println("isHidden():        " + Files.isHidden(sample));
        System.out.println("size():            " + Files.size(sample) + " bytes");

        // Bulk attribute read — cheaper than multiple individual calls
        BasicFileAttributes attrs =
            Files.readAttributes(sample, BasicFileAttributes.class);
        System.out.println("creationTime:      " + attrs.creationTime());
        System.out.println("lastModifiedTime:  " + attrs.lastModifiedTime());
        System.out.println("isSymbolicLink:    " + attrs.isSymbolicLink());
        System.out.println("fileKey:           " + attrs.fileKey()); // inode on Unix

        // Modify timestamp
        Files.setLastModifiedTime(sample,
            FileTime.fromMillis(System.currentTimeMillis()));
        System.out.println("updated mtime:     " + Files.getLastModifiedTime(sample));
    }

    // =========================================================================
    // 4. Files I/O — reading and writing text / bytes
    // =========================================================================

    /**
     * Convenience methods on Files for small-to-medium files:
     *
     *   readString(path)           — entire file → String        (Java 11+)
     *   readAllLines(path)         — entire file → List<String>
     *   readAllBytes(path)         — entire file → byte[]
     *   writeString(path, cs)      — String → file               (Java 11+)
     *   write(path, bytes)         — byte[] → file
     *   write(path, lines)         — Iterable<String> → file
     *
     * For large files: use Files.newBufferedReader / newBufferedWriter (streaming).
     */
    static void filesIoDemo() throws IOException {
        System.out.println("\n--- 4. Files I/O ---");

        Path sample = DATA_DIR.resolve("sample.txt");
        Path out    = DATA_DIR.resolve("output_path_files.txt");

        // Read
        String content = Files.readString(sample, StandardCharsets.UTF_8);
        System.out.println("readString lines:  " + content.lines().count());

        List<String> lines = Files.readAllLines(sample, StandardCharsets.UTF_8);
        System.out.println("readAllLines[0]:   " + preview(lines.get(0), 40));

        byte[] bytes = Files.readAllBytes(sample);
        System.out.println("readAllBytes len:  " + bytes.length);

        // Write
        Files.writeString(out, "written by Files.writeString\n", StandardCharsets.UTF_8);
        Files.writeString(out, "second line\n",
            StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        Files.write(out, List.of("via write(lines)", "another line"),
            StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        System.out.println("written lines:     " + Files.readAllLines(out).size());

        // Streaming read for large files — no full file in memory
        try (Stream<String> stream = Files.lines(sample, StandardCharsets.UTF_8)) {
            long wordCount = stream
                .flatMap(l -> java.util.Arrays.stream(l.split("\\s+")))
                .count();
            System.out.println("word count:        " + wordCount);
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 5. Directory operations — list, walk, find
    // =========================================================================

    /**
     * Files.list(dir)       — Stream<Path> of DIRECT children (depth 1).
     * Files.walk(dir)       — Stream<Path> of ALL descendants (recursive).
     * Files.walk(dir, depth)— bounded recursion.
     * Files.find(dir, depth, BiPredicate<Path, BasicFileAttributes>)
     *                       — like walk but filters with access to attributes.
     *
     * All three return a Stream that holds an open directory handle.
     * ALWAYS close with try-with-resources.
     */
    static void directoryOperationsDemo() throws IOException {
        System.out.println("\n--- 5. Directory operations ---");

        // list — direct children only
        System.out.println("Files.list(data_files):");
        try (Stream<Path> s = Files.list(DATA_DIR)) {
            s.sorted().forEach(p -> System.out.println("  " + p.getFileName()));
        }

        // walk with depth limit — resolve relative to module root, not repo root
        Path srcRoot = Path.of(
            "file-io-nio/src/main/java/com/vbforge/practice/practice_4_path_files");
        if (Files.exists(srcRoot)) {
            System.out.println("Files.walk depth=1 (practice_4_path_files):");
            try (Stream<Path> s = Files.walk(srcRoot, 1)) {
                s.filter(p -> !p.equals(srcRoot))
                 .sorted()
                 .forEach(p -> System.out.println("  " + p.getFileName()));
            }

            // find — only .java files
            System.out.println("Files.find .java files:");
            try (Stream<Path> s = Files.find(srcRoot, 5,
                    (p, a) -> a.isRegularFile() && p.toString().endsWith(".java"))) {
                s.forEach(p -> System.out.println("  " + p.getFileName()));
            }
        } else {
            System.out.println("(skipped: " + srcRoot + " not found from CWD "
                + Path.of(".").toAbsolutePath().normalize() + ")");
        }
    }

    // =========================================================================
    // 6. createTempFile / createTempDirectory
    // =========================================================================

    /**
     * Temporary files are useful for unit tests, intermediate processing,
     * and safe atomic writes (write to temp, then move to final destination).
     *
     * Files.createTempFile(dir, prefix, suffix) — in a specified directory.
     * Files.createTempFile(prefix, suffix)      — in the system's temp dir.
     * Files.createTempDirectory(prefix)         — a temp directory.
     */
    static void tempFilesDemo() throws IOException {
        System.out.println("\n--- 6. Temp files ---");

        // System temp directory
        Path tempFile = Files.createTempFile("nio_demo_", ".txt");
        System.out.println("createTempFile:    " + tempFile.getFileName());
        System.out.println("in temp dir:       " + tempFile.getParent());

        Files.writeString(tempFile, "temporary content");
        System.out.println("size:              " + Files.size(tempFile));

        // Temp dir
        Path tempDir = Files.createTempDirectory("nio_temp_");
        System.out.println("createTempDir:     " + tempDir.getFileName());

        // Cleanup
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempDir);
        System.out.println("cleaned up");
    }

    // =========================================================================
    // 7. Atomic move — safe file replacement
    // =========================================================================

    /**
     * The classic safe-write pattern:
     *   1. Write new content to a temporary file.
     *   2. Atomically rename the temp file to the final destination.
     *
     * On most OS/filesystems, renaming within the same volume is atomic —
     * readers either see the old file or the new file, never a partial write.
     *
     * ATOMIC_MOVE throws AtomicMoveNotSupportedException if the move cannot
     * be done atomically (e.g., across different filesystems).
     */
    static void atomicMoveDemo() throws IOException {
        System.out.println("\n--- 7. Atomic move (safe write pattern) ---");

        Path finalPath = DATA_DIR.resolve("output_atomic.txt");
        Path tempPath  = DATA_DIR.resolve("output_atomic.tmp");

        // Step 1: write to temp file
        Files.writeString(tempPath, "new content safely written", StandardCharsets.UTF_8);

        // Step 2: atomic rename to final destination
        try {
            Files.move(tempPath, finalPath,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
            System.out.println("atomic move succeeded");
        } catch (AtomicMoveNotSupportedException e) {
            // Fallback: non-atomic replace (acceptable on many systems)
            Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("atomic move not supported — fell back to regular move");
        }

        System.out.println("final content:     " + Files.readString(finalPath));
        Files.deleteIfExists(finalPath);
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== Path & Files Demo (NIO.2) ===");
        pathConstructionDemo();
        pathOperationsDemo();
        filesMetadataDemo();
        filesIoDemo();
        directoryOperationsDemo();
        tempFilesDemo();
        atomicMoveDemo();
    }
}
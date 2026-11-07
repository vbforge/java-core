package com.vbforge.practice.practice_11_file_api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Demonstrates the legacy java.io.File API and its modern replacement:
 * the java.nio.file.Path / Files / Paths trio (introduced in Java 7).
 *
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │  java.io.File  (legacy — pre Java 7)                                │
 * │    - Mixes file metadata queries and path manipulation.             │
 * │    - Most methods return false/null on error instead of exceptions. │
 * │    - Not easily mockable; doesn't follow single-responsibility.     │
 * │                                                                     │
 * │  java.nio.file.Path  (modern — Java 7+)                             │
 * │    - Path: pure path representation (no I/O, immutable value).      │
 * │    - Files: static utility class for actual I/O operations.         │
 * │    - Paths: factory for Path instances (or Path.of() since Java 11).│
 * │    - Throws checked IOExceptions instead of silently returning false.│
 * └─────────────────────────────────────────────────────────────────────┘
 *
 * Rule of thumb:
 *   Prefer Path + Files for all new code.
 *   Use File only when an older API forces it (e.g., FileInputStream(File)).
 *   Convert between them: file.toPath()  /  path.toFile().
 */
public class FileApiDemo {

    static final Path DATA_DIR = Path.of("file-io-basics/data_files");

    // =========================================================================
    // 1. Legacy java.io.File — path manipulation and metadata
    // =========================================================================

    static void legacyFileDemo() {
        System.out.println("\n--- 1. Legacy java.io.File ---");

        File file = new File("file-io-basics/data_files/sample.txt");

        System.out.println("getName():           " + file.getName());
        System.out.println("getParent():         " + file.getParent());
        System.out.println("getAbsolutePath():   " + file.getAbsolutePath());
        System.out.println("exists():            " + file.exists());
        System.out.println("isFile():            " + file.isFile());
        System.out.println("isDirectory():       " + file.isDirectory());
        System.out.println("length() bytes:      " + file.length());
        System.out.println("canRead():           " + file.canRead());
        System.out.println("canWrite():          " + file.canWrite());

        File dir = new File("file-io-basics/data_files");
        String[] entries = dir.list();
        if (entries != null) {
            System.out.println("dir.list():");
            for (String entry : entries) System.out.println("  " + entry);
        }
    }

    // =========================================================================
    // 2. Modern Path — immutable path representation (no I/O!)
    // =========================================================================

    static void pathRepresentationDemo() {
        System.out.println("\n--- 2. Path representation ---");

        Path p = Path.of("file-io-basics/data_files/sample.txt");

        System.out.println("toString():          " + p);
        System.out.println("getFileName():       " + p.getFileName());
        System.out.println("getParent():         " + p.getParent());
        System.out.println("getRoot():           " + p.getRoot());
        System.out.println("getNameCount():      " + p.getNameCount());
        System.out.println("getName(0):          " + p.getName(0));
        System.out.println("isAbsolute():        " + p.isAbsolute());
        System.out.println("toAbsolutePath():    " + p.toAbsolutePath());

        System.out.println("subpath(1,3):        " + p.subpath(1, 3));

        Path parent = p.getParent();
        Path sibling = parent.resolve("people.csv");
        System.out.println("resolve sibling:     " + sibling);

        Path relative = Path.of("src").relativize(Path.of("src/main/App.java"));
        System.out.println("relativize:          " + relative);

        Path messy = Path.of("src/../src/./main/App.java");
        System.out.println("normalize:           " + messy.normalize());

        System.out.println("startsWith(file-io-basics): " + p.startsWith("file-io-basics"));
        System.out.println("endsWith(.txt):      " + p.endsWith("sample.txt"));

        File asFile = p.toFile();
        System.out.println("toFile().getName():  " + asFile.getName());
    }

    // =========================================================================
    // 3. Files utility class — actual I/O and metadata
    // =========================================================================

    static void filesUtilityDemo() throws IOException {
        System.out.println("\n--- 3. Files utility class ---");

        Path sample = DATA_DIR.resolve("sample.txt");

        System.out.println("Files.exists():          " + Files.exists(sample));
        System.out.println("Files.isRegularFile():   " + Files.isRegularFile(sample));
        System.out.println("Files.isDirectory():     " + Files.isDirectory(DATA_DIR));
        System.out.println("Files.size() bytes:      " + Files.size(sample));
        System.out.println("Files.isReadable():      " + Files.isReadable(sample));

        String content = Files.readString(sample);
        System.out.println("readString() first line: " + content.lines().findFirst().orElse(""));

        java.util.List<String> lines = Files.readAllLines(sample);
        System.out.println("readAllLines() count:    " + lines.size());

        BasicFileAttributes attrs = Files.readAttributes(sample, BasicFileAttributes.class);
        System.out.println("creationTime:            " + attrs.creationTime());
        System.out.println("lastModifiedTime:        " + attrs.lastModifiedTime());
        System.out.println("isSymbolicLink:          " + attrs.isSymbolicLink());
    }

    // =========================================================================
    // 4. Creating, writing, copying, moving, deleting — Files operations
    // =========================================================================

    static void filesOperationsDemo() throws IOException {
        System.out.println("\n--- 4. Files operations (create/write/copy/delete) ---");

        Path created    = DATA_DIR.resolve("output_file_api.txt");
        Path copied     = DATA_DIR.resolve("output_file_api_copy.txt");

        Files.writeString(created, "line one\nline two\nline three\n");
        System.out.println("writeString: created " + created.getFileName());

        Files.writeString(created, "line four\n", StandardOpenOption.APPEND);
        System.out.println("append: now " + Files.readAllLines(created).size() + " lines");

        Files.copy(created, copied, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("copy: " + copied.getFileName() + " exists=" + Files.exists(copied));

        Path moved = DATA_DIR.resolve("output_file_api_moved.txt");
        Files.move(copied, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("move: original gone=" + !Files.exists(copied)
            + ", moved exists=" + Files.exists(moved));

        Files.delete(created);
        Files.deleteIfExists(moved);
        System.out.println("cleanup: both deleted");
    }

    // =========================================================================
    // 5. Directory listing — Files.list() and Files.walk()
    // =========================================================================

    static void directoryListingDemo() throws IOException {
        System.out.println("\n--- 5. Directory listing ---");

        System.out.println("Files.list() of data_files:");
        try (var stream = Files.list(DATA_DIR)) {
            stream.forEach(p -> System.out.println("  " + p.getFileName()));
        }

        Path srcRoot = Path.of("file-io-basics/src/main/java/com/vbforge/practice/practice_11_file_api");
        if (Files.exists(srcRoot)) {
            System.out.println("Files.walk() .java files:");
            try (var stream = Files.walk(srcRoot)) {
                stream
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> System.out.println("  " + p.getFileName()));
            }
        }
    }

    // =========================================================================
    // 6. createDirectories — safely create nested directories
    // =========================================================================

    static void createDirectoriesDemo() throws IOException {
        System.out.println("\n--- 6. createDirectories ---");

        Path nested = DATA_DIR.resolve("temp/subdir/deep");

        Files.createDirectories(nested);
        System.out.println("created: " + nested + " → exists=" + Files.exists(nested));

        Files.delete(nested);
        Files.delete(nested.getParent());
        Files.delete(nested.getParent().getParent());
        System.out.println("cleanup done");
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== File API Demo ===");
        legacyFileDemo();
        pathRepresentationDemo();
        filesUtilityDemo();
        filesOperationsDemo();
        directoryListingDemo();
        createDirectoriesDemo();
    }
}
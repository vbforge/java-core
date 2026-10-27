package com.vbforge.file_io_basics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;

/**
 * FileBasicsDemo — annotated reference for {@link File}, the original (Java 1.0) file API.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>All demos work inside a throw-away temporary directory that {@code main} deletes at the
 * end, so running them leaves nothing behind and never depends on a fixed location.
 *
 * <p>Topics:
 * <ol>
 *   <li>The File object — a <em>path</em>, not an open file</li>
 *   <li>Creating, inspecting, renaming and deleting files</li>
 *   <li>Directories — {@code mkdir} vs {@code mkdirs}, listing, the {@code null} trap</li>
 *   <li>Walking a directory tree with recursion</li>
 *   <li>Legacy gotchas — failures are booleans, and what {@code java.nio.file.Files} does instead</li>
 * </ol>
 */
public class FileBasicsDemo {

    public static void main(String[] args) throws IOException {
        File workDir = Files.createTempDirectory("file-io-basics-").toFile();
        try {
            fileObjectDemo(workDir);
            System.out.println("---");
            createAndInspectDemo(workDir);
            System.out.println("---");
            directoriesDemo(workDir);
            System.out.println("---");
            treeWalkDemo(workDir);
            System.out.println("---");
            legacyGotchasDemo(workDir);
        } finally {
            PathFilesDemo.deleteTree(workDir.toPath());
        }
    }

    // ── 1. The File object ────────────────────────────────────────────────────

    /**
     * A {@link File} is an <strong>abstract path name</strong> — a wrapper around a string.
     * Creating one does not touch the disk, does not open anything and cannot fail because
     * the file is missing.
     *
     * <p>WHY this matters: {@code new File("report.txt")} says nothing about whether the file
     * exists. You ask the questions ({@code exists()}, {@code isFile()}, {@code length()}) when you need
     * the answer, and the answer can change between two calls.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>For a path that does not exist, {@code isFile()} <em>and</em> {@code isDirectory()} are both
     *       {@code false} — "not a file" does not mean "is a directory".</li>
     *   <li>{@code equals} compares the path text, not the files: {@code a/../a.txt} is not equal to {@code a.txt}.</li>
     *   <li>A relative path is resolved against the JVM's working directory, which differs between
     *       the IDE, Maven and a deployed server. Prefer absolute paths built from a known parent.</li>
     * </ul>
     */
    static void fileObjectDemo(File workDir) {
        System.out.println("=== The File object ===");

        // new File(parent, child) — the safe way to build a path (no manual separators)
        File notes = new File(workDir, "notes.txt");

        System.out.println("name           : " + notes.getName());                           // notes.txt
        System.out.println("parent is dir  : " + notes.getParentFile().equals(workDir));     // true
        System.out.println("exists         : " + notes.exists());                            // false — nothing created yet
        System.out.println("isFile         : " + notes.isFile());                            // false
        System.out.println("isDirectory    : " + notes.isDirectory());                       // false — missing is neither!

        // Absolute vs relative
        System.out.println("isAbsolute     : " + notes.isAbsolute());                        // true  (temp dir is absolute)
        System.out.println("relative       : " + new File("data/input.csv").isAbsolute());   // false — depends on the working dir

        // equals() compares path text only
        System.out.println("same text      : " + new File("a.txt").equals(new File("a.txt")));       // true
        System.out.println("a/../a.txt     : " + new File("a/../a.txt").equals(new File("a.txt")));  // false
    }

    // ── 2. Create, inspect, rename, delete ────────────────────────────────────

    /**
     * Files are created, measured, renamed and deleted through methods on {@link File}.
     *
     * <p>WHY this matters: these are the everyday operations, and each one reports its outcome as a
     * {@code boolean} rather than an exception. That is convenient in a quick script, but you must
     * check the result — an ignored {@code false} silently means "nothing happened".
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code createNewFile()} returns {@code false} (no exception) if the file already exists.</li>
     *   <li>{@code length()} of a missing file is {@code 0} — indistinguishable from an empty file
     *       unless you check {@code exists()} first.</li>
     *   <li>{@code renameTo} may fail across drives or file systems, again just with {@code false}.</li>
     * </ul>
     */
    static void createAndInspectDemo(File workDir) throws IOException {
        System.out.println("=== Create, inspect, rename, delete ===");

        File file = new File(workDir, "created.txt");

        System.out.println("createNewFile  : " + file.createNewFile());       // true
        System.out.println("again          : " + file.createNewFile());       // false — already there, no exception
        System.out.println("length (empty) : " + file.length());              // 0

        Files.write(file.toPath(), "hello".getBytes(StandardCharsets.UTF_8)); // writing is covered in the next demos
        System.out.println("length         : " + file.length());              // 5 (bytes, not characters)
        System.out.println("isFile         : " + file.isFile());              // true
        System.out.println("canRead        : " + file.canRead());             // true
        System.out.println("modified > 0   : " + (file.lastModified() > 0));  // true

        File renamed = new File(workDir, "renamed.txt");
        System.out.println("renameTo       : " + file.renameTo(renamed));     // true
        System.out.println("old exists     : " + file.exists());              // false
        System.out.println("new exists     : " + renamed.exists());           // true

        System.out.println("delete         : " + renamed.delete());           // true
        System.out.println("delete again   : " + renamed.delete());           // false — nothing to delete, no exception
        System.out.println("length missing : " + renamed.length());           // 0 — same as an empty file!
    }

    // ── 3. Directories ────────────────────────────────────────────────────────

    /**
     * Directories are {@link File}s too. {@code mkdir()} creates exactly one level;
     * {@code mkdirs()} creates every missing level.
     *
     * <p>WHY this matters: output paths such as {@code reports/2024/march/summary.txt} need their
     * parent folders to exist before you can write. {@code mkdirs()} on the <em>parent</em> is the
     * standard preparation step.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code mkdirs()} returns {@code false} when the directory already exists — {@code false}
     *       here does not mean failure. Check {@code isDirectory()} afterwards.</li>
     *   <li>{@code list()} and {@code listFiles()} return {@code null} (not an empty array) when the
     *       path is not a directory or cannot be read. Always null-check.</li>
     *   <li>The order of {@code list()} is unspecified — sort if you need stable output.</li>
     *   <li>{@code delete()} on a non-empty directory just returns {@code false}.</li>
     * </ul>
     */
    static void directoriesDemo(File workDir) throws IOException {
        System.out.println("=== Directories ===");

        File deep = new File(workDir, "reports/2024/march");

        System.out.println("mkdir (no parent): " + deep.mkdir());     // false — parents are missing
        System.out.println("mkdirs           : " + deep.mkdirs());    // true  — all levels created
        System.out.println("mkdirs again     : " + deep.mkdirs());    // false — already exists (not an error!)
        System.out.println("isDirectory      : " + deep.isDirectory()); // true

        File year = deep.getParentFile();                              // .../reports/2024
        Files.write(new File(year, "summary.txt").toPath(), "x".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(year, "data.csv").toPath(), "y".getBytes(StandardCharsets.UTF_8));

        String[] names = year.list();                                  // order is unspecified ...
        Arrays.sort(names);                                            // ... so sort before printing
        System.out.println("list()           : " + Arrays.toString(names));   // [data.csv, march, summary.txt]

        File[] onlyFiles = year.listFiles(f -> f.isFile());            // FileFilter: keep regular files
        System.out.println("files only       : " + onlyFiles.length);  // 2

        String[] csv = year.list((dir, name) -> name.endsWith(".csv")); // FilenameFilter: filter by name
        System.out.println("*.csv            : " + Arrays.toString(csv)); // [data.csv]

        // The null trap
        System.out.println("list() on a file : " + new File(year, "data.csv").list());   // null
        System.out.println("list() on missing: " + new File(year, "nope").list());       // null

        // A non-empty directory cannot be deleted directly
        System.out.println("delete non-empty : " + year.delete());     // false
    }

    // ── 4. Walking a tree ─────────────────────────────────────────────────────

    /**
     * {@link File} has no built-in "walk this folder and everything below it". The classic
     * pattern is a small recursive method: handle the entry, and if it is a directory, recurse
     * into each child from {@code listFiles()}.
     *
     * <p>WHY this matters: size of a folder, searching for files, deleting a tree — all of them
     * are this same recursion with a different action. The same job in NIO.2 is
     * {@code Files.walk} (see {@code PathFilesDemo}).
     *
     * <p>Complexity: O(n) in the number of entries; recursion depth equals directory depth.
     */
    static void treeWalkDemo(File workDir) throws IOException {
        System.out.println("=== Walking a tree ===");

        File root = new File(workDir, "tree");
        File deeper = new File(root, "sub/deeper");
        deeper.mkdirs();
        Files.write(new File(root, "a.txt").toPath(), "aaa".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(root, "sub/b.txt").toPath(), "bbbbb".getBytes(StandardCharsets.UTF_8));
        Files.write(new File(deeper, "c.txt").toPath(), "cc".getBytes(StandardCharsets.UTF_8));

        printTree(root, 0);   // a.txt / sub/ / b.txt / deeper/ / c.txt, indented by depth

        String[] txtAtTop = root.list((dir, name) -> name.endsWith(".txt"));
        System.out.println("top-level .txt  : " + Arrays.toString(txtAtTop));    // [a.txt] — list() is NOT recursive
    }

    private static void printTree(File entry, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        System.out.println(indent + entry.getName() + (entry.isDirectory() ? "/" : ""));

        File[] children = entry.listFiles();                       // null for files — guard it
        if (children == null) {
            return;
        }
        Arrays.sort(children);                                      // File is Comparable (by path)
        for (File child : children) {
            printTree(child, depth + 1);
        }
    }

    // ── 5. Legacy gotchas ─────────────────────────────────────────────────────

    /**
     * {@link File} predates exceptions-with-reasons. Almost every failure is a bare
     * {@code false} (or {@code null}); you never learn <em>why</em>: missing file? permissions? disk full?
     *
     * <p>WHY this matters: {@code java.nio.file.Files} (Java 7+) does the same jobs but throws a
     * specific {@link java.io.IOException} subclass — {@link NoSuchFileException},
     * {@link DirectoryNotEmptyException}, ... — so a failure can be handled or logged properly.
     * New code should prefer {@code Path}/{@code Files}; {@code File} stays relevant for the
     * many libraries that still accept it. Bridge with {@code file.toPath()} and {@code path.toFile()}.
     */
    static void legacyGotchasDemo(File workDir) throws IOException {
        System.out.println("=== Legacy gotchas ===");

        File missing = new File(workDir, "missing.txt");
        System.out.println("File.delete(missing)   : " + missing.delete());    // false — no reason given

        try {
            Files.delete(missing.toPath());                                    // same job, with a reason
        } catch (NoSuchFileException e) {
            System.out.println("Files.delete(missing)  : " + e.getClass().getSimpleName()); // NoSuchFileException
        }

        File folder = new File(workDir, "folder");
        folder.mkdir();
        new File(folder, "inside.txt").createNewFile();

        System.out.println("File.delete(non-empty) : " + folder.delete());     // false — why? not said
        try {
            Files.delete(folder.toPath());
        } catch (DirectoryNotEmptyException e) {
            System.out.println("Files.delete(non-empty): " + e.getClass().getSimpleName()); // DirectoryNotEmptyException
        }

        // The bridge between the two worlds
        System.out.println("round trip             : " + folder.toPath().toFile().equals(folder)); // true
    }
}

package com.vbforge.file_io_nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * PathsAndFilesDemo — annotated reference for the NIO.2 {@link Path} and {@link Files} APIs.
 *
 * <p>Read top-to-bottom. NIO.2 (Java 7+) replaces most of {@code java.io.File} with two ideas:
 * <ul>
 *   <li>{@link Path} — an <em>immutable value</em> describing a location. Pure string
 *       manipulation, it never touches the disk.</li>
 *   <li>{@link Files} — static methods that do the actual I/O and
 *       <em>throw descriptive exceptions</em> instead of returning {@code false}.</li>
 * </ul>
 *
 * <p>Topics:
 * <ol>
 *   <li>Path basics — decomposing a path, element-wise {@code startsWith}, immutability</li>
 *   <li>Path operations — {@code resolve}, {@code relativize}, {@code normalize}, path traversal</li>
 *   <li>Files read/write — {@code write}, {@code readAllLines}, open options, buffered, {@code Files.lines}</li>
 *   <li>Copy / move / delete — copy options, atomic move, exceptions, temp files</li>
 * </ol>
 *
 * <p>Every demo that touches the disk works inside a fresh temporary directory
 * and deletes it afterwards.
 */
public class PathsAndFilesDemo {

    public static void main(String[] args) throws IOException {
        pathBasicsDemo();
        System.out.println("---");
        pathOperationsDemo();
        System.out.println("---");
        filesReadWriteDemo();
        System.out.println("---");
        copyMoveDeleteDemo();
    }

    // ── 1. Path basics ────────────────────────────────────────────────────────

    /**
     * A {@link Path} is an immutable sequence of name elements, optionally preceded by a root.
     *
     * <p>WHY this matters: {@code java.io.File} mixed "a name" with "a thing on disk" and
     * returned {@code false}/{@code null} on failure. {@code Path} is only the name — every
     * method here is pure string manipulation, so none of them touch the file system and
     * none of them throw {@link IOException}.
     *
     * <p>Complexity: all operations here are O(number of name elements).
     *
     * <p>Common gotcha: {@code startsWith}/{@code endsWith} compare whole <em>name elements</em>,
     * not characters. {@code "projects/java".startsWith("proj")} is {@code false}.
     */
    static void pathBasicsDemo() {
        System.out.println("=== Path basics ===");

        // Paths.get joins elements with the platform separator — never concatenate "/" by hand
        Path p = Paths.get("projects", "java", "app", "Main.java");
        System.out.println("path          : " + p);
        System.out.println("getFileName   : " + p.getFileName());     // Main.java
        System.out.println("getParent     : " + p.getParent());       // projects/java/app
        System.out.println("getRoot       : " + p.getRoot());         // null — relative path has no root
        System.out.println("getNameCount  : " + p.getNameCount());    // 4
        System.out.println("getName(1)    : " + p.getName(1));        // java
        System.out.println("subpath(1,3)  : " + p.subpath(1, 3));     // java/app  — [begin, end)
        System.out.println("isAbsolute    : " + p.isAbsolute());      // false
        System.out.println("toAbsolute    : " + p.toAbsolutePath());  // prefixed with the working dir

        // startsWith / endsWith work on name ELEMENTS, not on characters
        System.out.println("startsWith(projects/java) : " + p.startsWith(Paths.get("projects", "java"))); // true
        System.out.println("startsWith(\"proj\")        : " + p.startsWith("proj"));                     // false!
        System.out.println("endsWith(Main.java)        : " + p.endsWith("Main.java"));                  // true

        // Paths are immutable — every "modifying" method returns a NEW Path
        Path sibling = p.resolveSibling("Other.java");
        System.out.println("sibling       : " + sibling);
        System.out.println("original kept : " + p);

        // A Path never touches the disk — this one points at nothing and that is fine
        Path ghost = Paths.get("this", "does", "not", "exist.txt");
        System.out.println("exists?       : " + Files.exists(ghost));  // false — only Files.* looks at the disk

        // Bridging to legacy java.io.File code, both directions
        File legacy = p.toFile();
        System.out.println("File → Path   : " + legacy.toPath().equals(p));  // true
    }

    // ── 2. Path operations ────────────────────────────────────────────────────

    /**
     * {@code resolve}, {@code relativize} and {@code normalize} are the three algebra
     * operations on paths.
     *
     * <ul>
     *   <li>{@code a.resolve(b)} — "start at a, then go to b". If {@code b} is absolute, the
     *       result is simply {@code b}.</li>
     *   <li>{@code a.relativize(b)} — the inverse: the path that leads from {@code a} to {@code b}.
     *       Both must be absolute or both relative, otherwise {@link IllegalArgumentException}.</li>
     *   <li>{@code normalize()} — removes {@code .} and collapses {@code name/..} lexically.
     *       It does <em>not</em> consult the disk (so symlinks are not honoured).</li>
     * </ul>
     *
     * <p>WHY this matters: user-supplied path fragments such as {@code "../../etc/passwd"}
     * are the classic <em>path traversal</em> vulnerability. {@code resolve} alone happily
     * produces a path that escapes the base directory; you must {@code normalize()} and then
     * check {@code startsWith(base)} on {@link Path} objects (not on Strings).
     */
    static void pathOperationsDemo() {
        System.out.println("=== Path operations ===");

        Path base = Paths.get("data", "reports");

        // resolve — append
        System.out.println("resolve       : " + base.resolve("q1.csv"));
        System.out.println("resolve(Path) : " + base.resolve(Paths.get("2024", "q1.csv")));

        // resolve with an ABSOLUTE argument replaces the base entirely
        Path absolute = Paths.get("config").toAbsolutePath();
        System.out.println("resolve(abs)  : " + base.resolve(absolute).equals(absolute));  // true

        // resolveSibling — replace the last element
        System.out.println("resolveSibling: " + base.resolveSibling("archive"));           // data/archive

        // relativize — "how do I get from base to target?"
        Path target = Paths.get("data", "logs", "app.log");
        Path relative = base.relativize(target);
        System.out.println("relativize    : " + relative);                                 // ../logs/app.log
        System.out.println("round trip    : " + base.resolve(relative).normalize().equals(target)); // true

        // relativize refuses to mix absolute and relative paths
        try {
            base.relativize(absolute);
        } catch (IllegalArgumentException e) {
            System.out.println("mixed types   : IllegalArgumentException");
        }

        // normalize — collapse "." and "x/.."
        Path messy = Paths.get("data", "reports", "..", "logs", ".", "app.log");
        System.out.println("normalize     : " + messy.normalize());                        // data/logs/app.log
        // a leading ".." cannot be collapsed — there is no parent element to cancel it
        System.out.println("leading ..    : " + Paths.get("..", "a").normalize());         // ../a

        // Path traversal: resolve() happily escapes the base directory
        Path uploads = Paths.get("uploads");
        Path evil = uploads.resolve(Paths.get("..", "secret.txt")).normalize();
        System.out.println("escaped path  : " + evil);                                     // secret.txt
        System.out.println("inside base?  : " + evil.startsWith(uploads));                 // false → reject it
    }

    // ── 3. Files read / write ─────────────────────────────────────────────────

    /**
     * {@link Files} offers one-liners for the common cases and {@code newBufferedReader/Writer}
     * for streaming.
     *
     * <p>WHY this matters: {@code FileReader}/{@code FileWriter} used the platform default charset
     * and swallowed failures. Every {@code Files} method takes an explicit {@link java.nio.charset.Charset}
     * and reports problems as precise {@link IOException} subclasses
     * ({@link NoSuchFileException}, {@link FileAlreadyExistsException}, …).
     *
     * <p>Default options of {@code Files.write}: {@code CREATE, TRUNCATE_EXISTING, WRITE} —
     * i.e. it <em>overwrites</em>. Pass {@code APPEND} to add, {@code CREATE_NEW} to refuse overwriting.
     *
     * <p>Complexity: {@code readAllLines}/{@code readAllBytes} are O(file size) in time <em>and memory</em> —
     * use {@code Files.lines} or a buffered reader for large files.
     *
     * <p>Common gotcha: {@code Files.lines}, {@code Files.list} and {@code Files.walk} return a
     * {@link Stream} that holds an open file handle — always use try-with-resources.
     */
    static void filesReadWriteDemo() throws IOException {
        System.out.println("=== Files read / write ===");

        Path dir = Files.createTempDirectory("nio-rw-");
        try {
            Path file = dir.resolve("notes").resolve("todo.txt");

            // createDirectories — like mkdir -p, silently succeeds if the directory exists
            Files.createDirectories(file.getParent());

            // write lines (UTF-8, truncates an existing file)
            Files.write(file, Arrays.asList("buy milk", "write code"), StandardCharsets.UTF_8);
            System.out.println("readAllLines  : " + Files.readAllLines(file, StandardCharsets.UTF_8));

            // APPEND adds instead of overwriting
            Files.write(file, "walk dog".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            System.out.println("size (bytes)  : " + Files.size(file));

            // CREATE_NEW — fail if the file already exists (safe "do not clobber")
            try {
                Files.write(file, new byte[0], StandardOpenOption.CREATE_NEW);
            } catch (FileAlreadyExistsException e) {
                System.out.println("CREATE_NEW    : FileAlreadyExistsException");
            }

            // Reading a missing file gives a precise exception
            try {
                Files.readAllLines(dir.resolve("missing.txt"), StandardCharsets.UTF_8);
            } catch (NoSuchFileException e) {
                System.out.println("missing file  : NoSuchFileException");
            }

            // Streaming write + read — constant memory regardless of file size
            Path big = dir.resolve("big.txt");
            try (BufferedWriter out = Files.newBufferedWriter(big, StandardCharsets.UTF_8)) {
                for (int i = 1; i <= 3; i++) {
                    out.write("line " + i);
                    out.newLine();
                }
            }
            try (BufferedReader in = Files.newBufferedReader(big, StandardCharsets.UTF_8)) {
                System.out.println("first line    : " + in.readLine());
            }

            // Files.lines — lazy Stream<String>; MUST be closed
            try (Stream<String> lines = Files.lines(big, StandardCharsets.UTF_8)) {
                System.out.println("line count    : " + lines.count());
            }

            // Charset matters: 'é' is ONE char but TWO bytes in UTF-8
            Path accents = dir.resolve("accents.txt");
            Files.write(accents, "é".getBytes(StandardCharsets.UTF_8));
            System.out.println("UTF-8 size    : " + Files.size(accents));                   // 2

            // Path equality is about the NAME, not the file: use isSameFile for identity
            Path viaDot = dir.resolve(".").resolve("big.txt");
            System.out.println("equals        : " + viaDot.equals(big));                    // false
            System.out.println("isSameFile    : " + Files.isSameFile(viaDot, big));         // true
        } finally {
            deleteTree(dir);
        }
    }

    // ── 4. Copy / move / delete ───────────────────────────────────────────────

    /**
     * {@code copy}, {@code move} and {@code delete} are strict by default: they throw rather than
     * silently overwrite or ignore.
     *
     * <ul>
     *   <li>{@code Files.copy(src, dst)} — throws {@link FileAlreadyExistsException} unless
     *       {@code REPLACE_EXISTING} is given.</li>
     *   <li>{@code Files.copy} of a <em>directory</em> is shallow: it creates an empty directory.</li>
     *   <li>{@code Files.move(..., ATOMIC_MOVE)} — either the whole move happens or none of it;
     *       other observers never see a half-moved file. Fails with
     *       {@link java.nio.file.AtomicMoveNotSupportedException} across file stores.</li>
     *   <li>{@code Files.delete} throws {@link NoSuchFileException} for a missing target and
     *       {@link DirectoryNotEmptyException} for a non-empty directory;
     *       {@code deleteIfExists} returns {@code false} instead of the first.</li>
     * </ul>
     *
     * <p>WHY this matters: {@code File.delete()} just returned {@code false} — you never learned
     * <em>why</em> it failed.
     */
    static void copyMoveDeleteDemo() throws IOException {
        System.out.println("=== Copy / move / delete ===");

        Path dir = Files.createTempDirectory("nio-copy-");
        try {
            Path a = dir.resolve("a.txt");
            Files.write(a, "hello".getBytes(StandardCharsets.UTF_8));

            // copy — refuses to overwrite by default
            Path b = dir.resolve("b.txt");
            Files.copy(a, b);
            try {
                Files.copy(a, b);
            } catch (FileAlreadyExistsException e) {
                System.out.println("copy twice    : FileAlreadyExistsException");
            }
            Files.write(a, "v2".getBytes(StandardCharsets.UTF_8));
            Files.copy(a, b, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("after replace : " + new String(Files.readAllBytes(b), StandardCharsets.UTF_8));

            // copy of a directory is SHALLOW — contents are not copied
            Path d = Files.createDirectory(dir.resolve("d"));
            Files.write(d.resolve("inner.txt"), "x".getBytes(StandardCharsets.UTF_8));
            Path d2 = dir.resolve("d2");
            Files.copy(d, d2);
            try (Stream<Path> children = Files.list(d2)) {
                System.out.println("copied dir has: " + children.count() + " entries");   // 0
            }

            // move — plain rename within a file store
            Path moved = dir.resolve("moved.txt");
            Files.move(b, moved);
            System.out.println("source gone   : " + Files.notExists(b));

            // ATOMIC_MOVE — all or nothing
            Path atomic = dir.resolve("atomic.txt");
            Files.move(moved, atomic, StandardCopyOption.ATOMIC_MOVE);
            System.out.println("atomic moved  : " + Files.exists(atomic));

            // delete vs deleteIfExists
            Files.delete(atomic);
            try {
                Files.delete(atomic);
            } catch (NoSuchFileException e) {
                System.out.println("delete twice  : NoSuchFileException");
            }
            System.out.println("deleteIfExists: " + Files.deleteIfExists(atomic));         // false

            // deleting a non-empty directory is refused
            try {
                Files.delete(d);
            } catch (DirectoryNotEmptyException e) {
                System.out.println("non-empty dir : DirectoryNotEmptyException");
            }

            // copy between a Path and a stream
            Path fromStream = dir.resolve("from-stream.txt");
            Files.copy(new ByteArrayInputStream("streamed".getBytes(StandardCharsets.UTF_8)), fromStream);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            Files.copy(fromStream, sink);
            System.out.println("stream copy   : " + sink.toString("UTF-8"));

            // temp files live in the directory you choose — the OS does not delete them for you
            Path temp = Files.createTempFile(dir, "pre-", ".tmp");
            System.out.println("temp file     : " + temp.getFileName().toString().endsWith(".tmp"));
        } finally {
            deleteTree(dir);
        }
    }

    // ── helper used by every demo ─────────────────────────────────────────────

    /**
     * Deletes a directory tree (children first). Used only to clean up demo temp directories;
     * {@link DirectoryStreamDemo} explains {@code walkFileTree} in detail.
     */
    static void deleteTree(Path root) throws IOException {
        if (Files.notExists(root)) {
            return;
        }
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

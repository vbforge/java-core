package com.vbforge.file_io_basics;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * PathFilesDemo — annotated reference for the modern file API: {@link Path} (a location) and
 * {@link Files} (the operations), both from {@code java.nio.file} (Java 7+).
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>Path — building, taking apart and combining locations</li>
 *   <li>Files — reading and writing whole files, open options</li>
 *   <li>Files.lines — lazy streams that must be closed</li>
 *   <li>Copy, move, delete — with reasons for every failure</li>
 *   <li>Listing and walking directories</li>
 *   <li>Path safety — {@code resolve} does not normalise</li>
 * </ol>
 *
 * <p>Directory watching, file attributes and {@code DirectoryStream} belong to the next module
 * ({@code file-io-nio}).
 */
public class PathFilesDemo {

    public static void main(String[] args) throws IOException {
        Path workDir = Files.createTempDirectory("file-io-basics-");
        try {
            pathDemo();
            System.out.println("---");
            readWriteDemo(workDir);
            System.out.println("---");
            linesStreamDemo(workDir);
            System.out.println("---");
            copyMoveDeleteDemo(workDir);
            System.out.println("---");
            listAndWalkDemo(workDir);
            System.out.println("---");
            pathSafetyDemo();
        } finally {
            deleteTree(workDir);
        }
    }

    // ── 1. Path ───────────────────────────────────────────────────────────────

    /**
     * A {@link Path} is an immutable location made of name elements. Like {@code File} it does
     * not touch the disk. It replaces {@code File} for new code: richer, and every operation that
     * <em>does</em> hit the disk lives in {@link Files} and reports problems with exceptions.
     *
     * <p>WHY this matters: path arithmetic — "the parent", "the same path relative to another
     * folder", "join these two" — is something every program does, and doing it by string
     * concatenation breaks on separators and {@code ..}.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code resolve(other)} <em>replaces</em> the base when {@code other} is absolute.</li>
     *   <li>{@code startsWith} compares whole name elements: {@code /a/bc} does not start with
     *       {@code /a/b} (a String comparison would say it does).</li>
     *   <li>{@code Path.of(...)} exists only from Java 11; on Java 8 use {@code Paths.get(...)}.</li>
     * </ul>
     *
     * <p>(Output below is shown with {@code /} separators; on Windows it prints {@code \}.)
     */
    static void pathDemo() {
        System.out.println("=== Path ===");

        Path report = Paths.get("docs", "reports", "q1.txt");        // elements joined with the right separator

        System.out.println("fileName       : " + report.getFileName());      // q1.txt
        System.out.println("parent         : " + report.getParent());        // docs/reports
        System.out.println("root           : " + report.getRoot());          // null  (relative path)
        System.out.println("nameCount      : " + report.getNameCount());     // 3
        System.out.println("name(0)        : " + report.getName(0));         // docs
        System.out.println("isAbsolute     : " + report.isAbsolute());       // false

        Path base = Paths.get("/data/app");
        System.out.println("resolve        : " + base.resolve("logs/a.log"));                       // /data/app/logs/a.log
        System.out.println("resolveSibling : " + base.resolveSibling("other"));                     // /data/other
        System.out.println("resolve absolute: " + base.resolve("/etc/hosts"));                      // /etc/hosts — base is dropped!
        System.out.println("relativize     : " + base.relativize(Paths.get("/data/app/logs/a.log"))); // logs/a.log
        System.out.println("relativize ..  : " + Paths.get("/data/app/logs").relativize(Paths.get("/data/cfg/x"))); // ../../cfg/x

        System.out.println("normalize      : " + Paths.get("/a/b/../c/./d").normalize());           // /a/c/d

        // Element-wise startsWith vs String startsWith
        Path bc = Paths.get("/a/bc");
        System.out.println("Path startsWith: " + bc.startsWith(Paths.get("/a/b")));                // false
        System.out.println("String startsWith: " + "/a/bc".startsWith("/a/b"));                     // true — the classic bug

        // Bridge to the old API
        System.out.println("toFile         : " + report.toFile().getName());                        // q1.txt
    }

    // ── 2. Reading and writing whole files ────────────────────────────────────

    /**
     * {@link Files} reads or writes a whole file in one call: bytes ({@code readAllBytes},
     * {@code write}) or lines ({@code readAllLines}). For files that fit in memory this removes all
     * stream ceremony — nothing to open, nothing to close.
     *
     * <p>WHY this matters: most files an application reads (config, small data, test fixtures)
     * are small. The one-liner is shorter and cannot leak a handle.
     *
     * <p>Complexity: O(n) time and O(n) memory — the whole file is held at once.
     * For big files use {@code Files.lines} or {@code Files.newBufferedReader}.
     *
     * <p>Open options decide what {@code write} does with an existing file. The default is
     * {@code CREATE + TRUNCATE_EXISTING + WRITE}: create if missing, empty it if present.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code readAllLines} and {@code newBufferedReader} default to UTF-8 (unlike {@code FileReader}) —
     *       still pass the charset to make it obvious.</li>
     *   <li>{@code readString}/{@code writeString} are Java 11+; on Java 8 go through bytes
     *       or {@code write(path, lines, charset)}.</li>
     *   <li>Reading a missing file throws {@link NoSuchFileException} — a subclass of {@link IOException}
     *       that tells you which path.</li>
     * </ul>
     */
    static void readWriteDemo(Path workDir) throws IOException {
        System.out.println("=== Reading and writing whole files ===");

        Path file = workDir.resolve("data.txt");

        Files.write(file, Arrays.asList("one", "two"), StandardCharsets.UTF_8);       // lines in, line breaks added
        System.out.println("readAllLines   : " + Files.readAllLines(file, StandardCharsets.UTF_8));  // [one, two]
        System.out.println("size           : " + Files.size(file));                    // 8 (one\ntwo\n on a '\n' system)

        // Bytes <-> String
        Files.write(file, "héllo".getBytes(StandardCharsets.UTF_8));                  // default options: overwrite
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        System.out.println("bytes -> String: " + text);                                // héllo
        System.out.println("size in bytes  : " + Files.size(file));                    // 6 (é is 2 bytes)

        // APPEND
        Files.write(file, "!".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        System.out.println("after APPEND   : " + new String(Files.readAllBytes(file), StandardCharsets.UTF_8)); // héllo!

        // CREATE_NEW refuses to overwrite
        try {
            Files.write(file, new byte[0], StandardOpenOption.CREATE_NEW);
        } catch (FileAlreadyExistsException e) {
            System.out.println("CREATE_NEW     : FileAlreadyExistsException");
        }

        // Missing file: an exception that names the path
        try {
            Files.readAllLines(workDir.resolve("missing.txt"), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            System.out.println("missing        : NoSuchFileException");
        }

        // Questions about the file system
        System.out.println("exists         : " + Files.exists(file));                  // true
        System.out.println("isRegularFile  : " + Files.isRegularFile(file));           // true
        System.out.println("isDirectory    : " + Files.isDirectory(workDir));          // true
        System.out.println("notExists      : " + Files.notExists(workDir.resolve("nope"))); // true
    }

    // ── 3. Files.lines ────────────────────────────────────────────────────────

    /**
     * {@code Files.lines(path)} returns a <em>lazy</em> {@link Stream} of lines: the file is read
     * as the stream is consumed, so a huge file never has to fit in memory.
     *
     * <p>WHY this matters: the stream holds an open file handle. It must be closed — put it in a
     * try-with-resources — or the handle leaks until garbage collection.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code Files.readAllLines} throws a checked {@link IOException} for malformed input;
     *       {@code Files.lines} cannot (a stream pipeline has no checked exceptions), so the same
     *       problem surfaces <em>later, during consumption</em>, as an {@link UncheckedIOException}.</li>
     *   <li>Do not return the stream from the method that opened it — it would be closed already
     *       or leak. Collect inside the try block.</li>
     * </ul>
     */
    static void linesStreamDemo(Path workDir) throws IOException {
        System.out.println("=== Files.lines ===");

        Path file = workDir.resolve("log.txt");
        Files.write(file, Arrays.asList("INFO start", "ERROR disk", "INFO end", "ERROR net"), StandardCharsets.UTF_8);

        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            List<String> errors = lines.filter(l -> l.startsWith("ERROR")).collect(Collectors.toList());
            System.out.println("errors         : " + errors);                           // [ERROR disk, ERROR net]
        }

        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            System.out.println("count          : " + lines.count());                    // 4
        }

        // Invalid UTF-8 (0xC3 must be followed by a continuation byte, here 0x28)
        Path broken = workDir.resolve("broken.txt");
        Files.write(broken, new byte[]{(byte) 0xC3, 0x28, '\n'});

        try {
            Files.readAllLines(broken, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            System.out.println("readAllLines   : MalformedInputException (checked IOException)");
        }

        try (Stream<String> lines = Files.lines(broken, StandardCharsets.UTF_8)) {
            lines.count();
        } catch (UncheckedIOException e) {
            System.out.println("Files.lines    : UncheckedIOException caused by "
                    + e.getCause().getClass().getSimpleName());                         // MalformedInputException
        }
    }

    // ── 4. Copy, move, delete ─────────────────────────────────────────────────

    /**
     * {@code Files.copy}, {@code move} and {@code delete} do what {@code File.renameTo/delete}
     * did — and tell you precisely why they could not.
     *
     * <p>WHY this matters: "silently did nothing" is the worst failure mode. By default
     * {@code copy} and {@code move} refuse to overwrite an existing target; pass
     * {@link StandardCopyOption#REPLACE_EXISTING} when you really want that.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code Files.delete} throws if the file is missing; {@code deleteIfExists} returns a boolean.</li>
     *   <li>Neither deletes a non-empty directory — {@link DirectoryNotEmptyException}.</li>
     *   <li>{@code createDirectory} fails if the directory exists; {@code createDirectories} is
     *       idempotent and creates parents (the NIO twin of {@code mkdirs}).</li>
     * </ul>
     */
    static void copyMoveDeleteDemo(Path workDir) throws IOException {
        System.out.println("=== Copy, move, delete ===");

        Path source = workDir.resolve("source.txt");
        Path target = workDir.resolve("target.txt");
        Files.write(source, "v1".getBytes(StandardCharsets.UTF_8));

        Files.copy(source, target);                                                    // creates target
        System.out.println("copied         : " + Files.exists(target));               // true

        Files.write(source, "v2".getBytes(StandardCharsets.UTF_8));
        try {
            Files.copy(source, target);                                                // target exists
        } catch (FileAlreadyExistsException e) {
            System.out.println("copy again     : FileAlreadyExistsException");
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("replaced       : " + new String(Files.readAllBytes(target), StandardCharsets.UTF_8)); // v2

        Path moved = workDir.resolve("moved.txt");
        Files.move(target, moved);                                                     // rename
        System.out.println("after move     : " + Files.exists(target) + " / " + Files.exists(moved)); // false / true

        Files.delete(moved);
        try {
            Files.delete(moved);
        } catch (NoSuchFileException e) {
            System.out.println("delete missing : NoSuchFileException");
        }
        System.out.println("deleteIfExists : " + Files.deleteIfExists(moved));         // false — no exception

        // Directories
        Path nested = workDir.resolve("x/y/z");
        Files.createDirectories(nested);
        Files.createDirectories(nested);                                               // idempotent
        System.out.println("createDirectories twice: ok");
        try {
            Files.delete(workDir.resolve("x"));
        } catch (DirectoryNotEmptyException e) {
            System.out.println("delete non-empty: DirectoryNotEmptyException");
        }
    }

    // ── 5. Listing and walking ────────────────────────────────────────────────

    /**
     * {@code Files.list(dir)} streams the entries of <em>one</em> directory;
     * {@code Files.walk(dir)} streams the <em>whole tree</em> depth-first, starting with
     * {@code dir} itself.
     *
     * <p>WHY this matters: both replace the hand-written recursion from {@code FileBasicsDemo}
     * with a stream you can filter, map and count.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>Both streams hold an open directory handle — use try-with-resources.</li>
     *   <li>The order is unspecified; sort before comparing or printing.</li>
     *   <li>{@code walk} includes the start directory itself in the results.</li>
     *   <li>To delete a tree, walk it and delete in <em>reverse</em> order so children go before
     *       their parent — see {@link #deleteTree(Path)}.</li>
     * </ul>
     */
    static void listAndWalkDemo(Path workDir) throws IOException {
        System.out.println("=== Listing and walking ===");

        Path root = workDir.resolve("tree");
        Files.createDirectories(root.resolve("sub/deeper"));
        Files.write(root.resolve("a.txt"), "a".getBytes(StandardCharsets.UTF_8));
        Files.write(root.resolve("sub/b.txt"), "b".getBytes(StandardCharsets.UTF_8));
        Files.write(root.resolve("sub/deeper/c.txt"), "c".getBytes(StandardCharsets.UTF_8));

        try (Stream<Path> entries = Files.list(root)) {                               // one level
            List<String> names = entries.map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList());
            System.out.println("list           : " + names);                           // [a.txt, sub]
        }

        try (Stream<Path> all = Files.walk(root)) {                                    // everything
            System.out.println("walk count     : " + all.count());                     // 6 (root, a.txt, sub, b.txt, deeper, c.txt)
        }

        try (Stream<Path> all = Files.walk(root)) {
            List<String> files = all.filter(Files::isRegularFile)
                    .map(p -> root.relativize(p).toString().replace('\\', '/'))
                    .sorted()
                    .collect(Collectors.toList());
            System.out.println("regular files  : " + files);                           // [a.txt, sub/b.txt, sub/deeper/c.txt]
        }

        try (Stream<Path> all = Files.walk(root, 1)) {                                 // depth limit: root + direct children
            System.out.println("walk depth 1   : " + all.count());                     // 3 (root, a.txt, sub)
        }
    }

    /**
     * Deletes {@code root} and everything below it. Used by the demos to clean up their temporary
     * directories. Children are deleted before their parent because the stream is sorted in reverse order.
     */
    static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> all = Files.walk(root)) {
            List<Path> paths = all.sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            for (Path p : paths) {
                Files.delete(p);
            }
        }
    }

    // ── 6. Path safety ────────────────────────────────────────────────────────

    /**
     * {@code resolve} only <em>joins</em>; it never cleans up {@code ..} segments. So a path that
     * looks like it is inside a folder may point outside it.
     *
     * <p>WHY this matters: this is the "path traversal" vulnerability. A web app that serves
     * {@code base.resolve(userInput)} lets {@code ../../etc/passwd} escape the base directory.
     * The defence is always the same three steps: resolve, <strong>normalize</strong>, then check
     * {@code startsWith(base)} on the normalised path.
     *
     * <p>Note: {@code normalize()} works on the text of the path only; it does not look at the disk
     * and does not follow symbolic links (that is {@code toRealPath()}).
     */
    static void pathSafetyDemo() {
        System.out.println("=== Path safety ===");

        Path base = Paths.get("/srv/files");

        Path sneaky = base.resolve("../etc/passwd");
        System.out.println("resolved       : " + sneaky);                                  // /srv/files/../etc/passwd
        System.out.println("startsWith(raw): " + sneaky.startsWith(base));                 // true  — looks safe, is not!

        Path clean = sneaky.normalize();
        System.out.println("normalized     : " + clean);                                   // /srv/etc/passwd
        System.out.println("startsWith(norm): " + clean.startsWith(base));                 // false — escape detected

        Path fine = base.resolve("docs/../a.txt").normalize();
        System.out.println("harmless ..    : " + fine + " inside=" + fine.startsWith(base)); // /srv/files/a.txt inside=true
    }
}

package com.vbforge.file_io_nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DirectoryStreamDemo — annotated reference for listing and traversing directories with NIO.2.
 *
 * <p>Four tools, from "one level, lazy" to "whole tree, full control":
 * <ol>
 *   <li>{@link DirectoryStream} — one directory level, lazy, optional glob/filter</li>
 *   <li>{@code Files.list / walk / find} — {@link Stream}-based listing and recursion</li>
 *   <li>{@link Files#walkFileTree} — visitor-based traversal that can skip subtrees and terminate early</li>
 *   <li>{@link PathMatcher} — glob and regex matching on {@link Path} objects</li>
 * </ol>
 *
 * <p>The demos operate on this sample tree (sizes in bytes in brackets):
 * <pre>
 *   a.txt(2)  b.java(3)  c.txt(4)
 *   sub/d.txt(1)  sub/e.java(2)  sub/deep/f.txt(3)
 *   skipme/g.txt(4)
 * </pre>
 */
public class DirectoryStreamDemo {

    public static void main(String[] args) throws IOException {
        directoryStreamDemo();
        System.out.println("---");
        listWalkFindDemo();
        System.out.println("---");
        walkFileTreeDemo();
        System.out.println("---");
        pathMatcherDemo();
    }

    // ── 1. DirectoryStream ────────────────────────────────────────────────────

    /**
     * {@link DirectoryStream} iterates the <em>direct children</em> of one directory, lazily,
     * optionally pre-filtered by a glob or a {@link DirectoryStream.Filter}.
     *
     * <p>WHY this matters: {@code File.list()} loaded <em>every</em> name into an array up front.
     * A {@code DirectoryStream} yields entries one at a time, so a directory with a million
     * files costs O(1) memory, and filtering happens while reading.
     *
     * <p>Complexity: O(n) time over the directory entries, O(1) extra memory.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>Iteration order is <em>unspecified</em> — sort if order matters.</li>
     *   <li>It holds an OS handle: close it (try-with-resources).</li>
     *   <li>{@code iterator()} may be called only once.</li>
     *   <li>It is <em>not</em> recursive and it includes sub-directories unless you filter them out.</li>
     * </ul>
     */
    static void directoryStreamDemo() throws IOException {
        System.out.println("=== DirectoryStream ===");

        Path root = Files.createTempDirectory("nio-ds-");
        try {
            buildSampleTree(root);

            // 1. glob — the pattern is matched against the entry's FILE NAME
            List<String> txt = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, "*.txt")) {
                for (Path entry : stream) {
                    txt.add(entry.getFileName().toString());
                }
            }
            Collections.sort(txt);                                      // order is unspecified!
            System.out.println("glob *.txt     : " + txt);               // [a.txt, c.txt] — not sub/d.txt

            // 2. alternation inside a glob
            List<String> either = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, "*.{txt,java}")) {
                for (Path entry : stream) {
                    either.add(entry.getFileName().toString());
                }
            }
            Collections.sort(either);
            System.out.println("glob {txt,java}: " + either);            // [a.txt, b.java, c.txt]

            // 3. a Filter can look at attributes — here: regular files larger than 3 bytes
            DirectoryStream.Filter<Path> bigFiles =
                    entry -> Files.isRegularFile(entry) && Files.size(entry) > 3;
            List<String> big = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, bigFiles)) {
                for (Path entry : stream) {
                    big.add(entry.getFileName().toString());
                }
            }
            System.out.println("filter > 3 B   : " + big);               // [c.txt]

            // 4. no glob → directories are included too
            List<String> all = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
                for (Path entry : stream) {
                    all.add(entry.getFileName().toString());
                }
            }
            Collections.sort(all);
            System.out.println("no filter      : " + all);               // includes skipme, sub

            // 5. iterator() can only be requested once
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
                stream.iterator();
                try {
                    stream.iterator();
                } catch (IllegalStateException e) {
                    System.out.println("2nd iterator() : IllegalStateException");
                }
            }
        } finally {
            PathsAndFilesDemo.deleteTree(root);
        }
    }

    // ── 2. Files.list / walk / find ───────────────────────────────────────────

    /**
     * The {@link Stream}-returning methods trade the explicit iterator for the whole Stream API.
     *
     * <ul>
     *   <li>{@code Files.list(dir)} — direct children only (like a DirectoryStream, as a Stream)</li>
     *   <li>{@code Files.walk(dir, maxDepth)} — depth-first walk; <strong>includes {@code dir} itself</strong>
     *       as the first element; {@code maxDepth = 0} yields only {@code dir}</li>
     *   <li>{@code Files.find(dir, maxDepth, (path, attrs) -> ...)} — like {@code walk().filter()}, but the
     *       predicate receives the already-read {@link BasicFileAttributes}, so no extra {@code stat} call per file</li>
     * </ul>
     *
     * <p>WHY this matters: one pipeline replaces a hand-written recursive method.
     *
     * <p>Common gotcha: these Streams hold open directory handles. Leaving one unclosed leaks
     * file descriptors — always use try-with-resources.
     */
    static void listWalkFindDemo() throws IOException {
        System.out.println("=== Files.list / walk / find ===");

        Path root = Files.createTempDirectory("nio-walk-");
        try {
            buildSampleTree(root);

            try (Stream<Path> children = Files.list(root)) {
                System.out.println("list           : " + children.map(p -> p.getFileName().toString())
                        .sorted().collect(Collectors.toList()));
            }

            try (Stream<Path> shallow = Files.walk(root, 1)) {
                System.out.println("walk depth 1   : " + shallow.count() + " (root + 5 children)");
            }

            try (Stream<Path> everything = Files.walk(root)) {
                System.out.println("walk all       : " + everything.count() + " (root + 3 dirs + 7 files)");
            }

            try (Stream<Path> only = Files.walk(root, 0)) {
                System.out.println("walk depth 0   : " + only.collect(Collectors.toList()).equals(
                        Collections.singletonList(root)));                         // true — just the root
            }

            // find — attributes arrive with the path
            try (Stream<Path> javaFiles = Files.find(root, Integer.MAX_VALUE,
                    (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".java"))) {
                System.out.println("find *.java   : " + javaFiles.map(root::relativize)
                        .sorted().collect(Collectors.toList()));
            }
        } finally {
            PathsAndFilesDemo.deleteTree(root);
        }
    }

    // ── 3. walkFileTree ───────────────────────────────────────────────────────

    /**
     * {@link Files#walkFileTree} drives a {@link java.nio.file.FileVisitor} over a tree and lets you
     * react at four moments:
     * <ul>
     *   <li>{@code preVisitDirectory} — before the children; can return {@code SKIP_SUBTREE}</li>
     *   <li>{@code visitFile} — for each non-directory</li>
     *   <li>{@code visitFileFailed} — when an entry cannot be read (permissions, race with deletion)</li>
     *   <li>{@code postVisitDirectory} — after <em>all</em> children — the right moment to delete a directory</li>
     * </ul>
     * The returned {@link FileVisitResult} steers the walk: {@code CONTINUE}, {@code SKIP_SUBTREE},
     * {@code SKIP_SIBLINGS}, {@code TERMINATE}.
     *
     * <p>WHY this matters: {@code Files.walk} cannot prune a branch or run code <em>after</em> a
     * directory is finished. Recursive copy, recursive delete and "ignore {@code node_modules}"
     * all need exactly that.
     *
     * <p>Complexity: O(n) over visited entries; skipped subtrees cost nothing.
     *
     * <p>Common gotcha: by default symbolic links are <em>not</em> followed, which protects you from cycles.
     */
    static void walkFileTreeDemo() throws IOException {
        System.out.println("=== walkFileTree ===");

        Path root = Files.createTempDirectory("nio-tree-");
        try {
            buildSampleTree(root);

            // 1. count every file
            final int[] files = {0};
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    files[0]++;
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("files          : " + files[0]);                    // 7

            // 2. prune a branch with SKIP_SUBTREE
            final int[] kept = {0};
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.getFileName().toString().equals("skipme")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    kept[0]++;
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("without skipme : " + kept[0]);                     // 6

            // 3. stop everything early with TERMINATE
            final int[] seen = {0};
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    seen[0]++;
                    return FileVisitResult.TERMINATE;
                }
            });
            System.out.println("after TERMINATE: " + seen[0]);                     // 1

            // 4. event order for ONE directory: pre → files → post
            final List<String> events = new ArrayList<>();
            Files.walkFileTree(root.resolve("sub").resolve("deep"), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    events.add("pre:" + dir.getFileName());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    events.add("file:" + file.getFileName());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    events.add("post:" + dir.getFileName());
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("event order    : " + events);                      // [pre:deep, file:f.txt, post:deep]
        } finally {
            PathsAndFilesDemo.deleteTree(root);
        }
    }

    // ── 4. PathMatcher ────────────────────────────────────────────────────────

    /**
     * A {@link PathMatcher} tests a {@link Path} against a {@code glob:} or {@code regex:} pattern.
     * It is the same engine {@code newDirectoryStream(dir, glob)} uses internally.
     *
     * <p>Glob cheat sheet:
     * <ul>
     *   <li>{@code *} — any characters within <em>one</em> name element (never crosses {@code /})</li>
     *   <li>{@code **} — any characters, <em>crossing</em> directory boundaries</li>
     *   <li>{@code ?} — exactly one character</li>
     *   <li>{@code {a,b}} — alternation; {@code [abc]} — character class</li>
     * </ul>
     *
     * <p>WHY this matters: filtering by name pattern without writing a regex.
     *
     * <p>Common gotcha: the matcher is applied to the <em>whole path you give it</em>. {@code glob:*.java}
     * matches {@code Main.java} but <strong>not</strong> {@code src/Main.java}; match against
     * {@code path.getFileName()} or use {@code glob:**&#47;*.java}.
     */
    static void pathMatcherDemo() {
        System.out.println("=== PathMatcher ===");

        PathMatcher star = FileSystems.getDefault().getPathMatcher("glob:*.java");
        Path flat = Paths.get("Main.java");
        Path nested = Paths.get("src", "Main.java");
        System.out.println("*.java  Main.java      : " + star.matches(flat));                     // true
        System.out.println("*.java  src/Main.java  : " + star.matches(nested));                   // false — crosses a dir
        System.out.println("*.java  on getFileName : " + star.matches(nested.getFileName()));     // true

        PathMatcher deep = FileSystems.getDefault().getPathMatcher("glob:**/*.java");
        System.out.println("**/*.java src/Main.java: " + deep.matches(nested));                   // true
        System.out.println("**/*.java Main.java    : " + deep.matches(flat));                     // false — needs a '/'

        PathMatcher single = FileSystems.getDefault().getPathMatcher("glob:?.txt");
        System.out.println("?.txt a.txt            : " + single.matches(Paths.get("a.txt")));     // true
        System.out.println("?.txt ab.txt           : " + single.matches(Paths.get("ab.txt")));    // false

        PathMatcher regex = FileSystems.getDefault().getPathMatcher("regex:.*\\.(txt|md)");
        System.out.println("regex txt|md notes.md  : " + regex.matches(Paths.get("notes.md")));   // true
    }

    // ── helper ────────────────────────────────────────────────────────────────

    /** Creates the sample tree documented at the top of this class. */
    static void buildSampleTree(Path root) throws IOException {
        write(root.resolve("a.txt"), "aa");
        write(root.resolve("b.java"), "bbb");
        write(root.resolve("c.txt"), "cccc");
        write(root.resolve("sub").resolve("d.txt"), "d");
        write(root.resolve("sub").resolve("e.java"), "ee");
        write(root.resolve("sub").resolve("deep").resolve("f.txt"), "fff");
        write(root.resolve("skipme").resolve("g.txt"), "gggg");
    }

    private static void write(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }
}

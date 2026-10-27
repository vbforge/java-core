package com.vbforge.file_io_nio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DirectoryStreamDemoTest — verifies every concept shown in {@link DirectoryStreamDemo}.
 *
 * <p>These tests are always GREEN. Every test builds this tree (sizes in bytes in brackets):
 * <pre>
 *   a.txt(2)  b.java(3)  c.txt(4)
 *   sub/d.txt(1)  sub/e.java(2)  sub/deep/f.txt(3)
 *   skipme/g.txt(4)
 * </pre>
 */
@DisplayName("DirectoryStreamDemo — API contracts")
class DirectoryStreamDemoTest {

    private static void write(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void buildTree(Path root) throws IOException {
        write(root.resolve("a.txt"), "aa");
        write(root.resolve("b.java"), "bbb");
        write(root.resolve("c.txt"), "cccc");
        write(root.resolve("sub").resolve("d.txt"), "d");
        write(root.resolve("sub").resolve("e.java"), "ee");
        write(root.resolve("sub").resolve("deep").resolve("f.txt"), "fff");
        write(root.resolve("skipme").resolve("g.txt"), "gggg");
    }

    private static List<String> names(DirectoryStream<Path> stream) {
        List<String> result = new ArrayList<>();
        for (Path entry : stream) {
            result.add(entry.getFileName().toString());
        }
        Collections.sort(result);
        return result;
    }

    // ── 1. DirectoryStream ────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. DirectoryStream")
    class DirectoryStreamTest {

        @Test
        @DisplayName("a glob selects by file name and is not recursive")
        void globIsNotRecursive(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmp, "*.txt")) {
                assertThat(names(stream)).containsExactly("a.txt", "c.txt");
            }
        }

        @Test
        @DisplayName("a glob supports alternation with {a,b}")
        void globAlternation(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmp, "*.{txt,java}")) {
                assertThat(names(stream)).containsExactly("a.txt", "b.java", "c.txt");
            }
        }

        @Test
        @DisplayName("a Filter can inspect attributes — here: regular files over 3 bytes")
        void filter(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            DirectoryStream.Filter<Path> big = entry -> Files.isRegularFile(entry) && Files.size(entry) > 3;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmp, big)) {
                assertThat(names(stream)).containsExactly("c.txt");
            }
        }

        @Test
        @DisplayName("without a glob, sub-directories are listed too")
        void includesDirectories(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmp)) {
                assertThat(names(stream)).containsExactly("a.txt", "b.java", "c.txt", "skipme", "sub");
            }
        }

        @Test
        @DisplayName("iterator() may be requested only once")
        void iteratorOnce(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tmp)) {
                stream.iterator();
                assertThatThrownBy(stream::iterator).isInstanceOf(IllegalStateException.class);
            }
        }

        @Test
        @DisplayName("a missing directory throws NoSuchFileException")
        void missingDirectory(@TempDir Path tmp) {
            Path missing = tmp.resolve("nope");
            assertThatThrownBy(() -> Files.newDirectoryStream(missing))
                    .isInstanceOf(NoSuchFileException.class);
        }

        @Test
        @DisplayName("a regular file throws NotDirectoryException")
        void notADirectory(@TempDir Path tmp) throws IOException {
            Path file = Files.write(tmp.resolve("f.txt"), new byte[0]);
            assertThatThrownBy(() -> Files.newDirectoryStream(file))
                    .isInstanceOf(NotDirectoryException.class);
        }
    }

    // ── 2. Files.list / walk / find ───────────────────────────────────────────

    @Nested
    @DisplayName("2. Files.list / walk / find")
    class ListWalkFindTest {

        @Test
        @DisplayName("list returns the direct children only")
        void list(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> children = Files.list(tmp)) {
                List<String> names = children.map(p -> p.getFileName().toString())
                        .sorted().collect(Collectors.toList());
                assertThat(names).containsExactly("a.txt", "b.java", "c.txt", "skipme", "sub");
            }
        }

        @Test
        @DisplayName("walk includes the start directory itself as the first element")
        void walkIncludesRoot(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> walked = Files.walk(tmp)) {
                assertThat(walked.findFirst()).contains(tmp);
            }
        }

        @Test
        @DisplayName("walk visits every directory and file (1 root + 3 dirs + 7 files)")
        void walkAll(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> walked = Files.walk(tmp)) {
                assertThat(walked.count()).isEqualTo(11L);
            }
        }

        @Test
        @DisplayName("walk with maxDepth 1 yields the root plus its direct children")
        void walkDepthOne(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> walked = Files.walk(tmp, 1)) {
                assertThat(walked.count()).isEqualTo(6L);
            }
        }

        @Test
        @DisplayName("walk with maxDepth 0 yields only the root")
        void walkDepthZero(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> walked = Files.walk(tmp, 0)) {
                assertThat(walked.collect(Collectors.toList())).containsExactly(tmp);
            }
        }

        @Test
        @DisplayName("find passes attributes to the predicate")
        void find(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> found = Files.find(tmp, Integer.MAX_VALUE,
                    (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".java"))) {
                List<Path> relative = found.map(tmp::relativize).sorted().collect(Collectors.toList());
                assertThat(relative).containsExactly(Paths.get("b.java"), Paths.get("sub", "e.java"));
            }
        }

        @Test
        @DisplayName("find can select by size using the supplied attributes")
        void findBySize(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            try (Stream<Path> found = Files.find(tmp, Integer.MAX_VALUE,
                    (path, attrs) -> attrs.isRegularFile() && attrs.size() >= 4)) {
                List<String> names = found.map(p -> p.getFileName().toString())
                        .sorted().collect(Collectors.toList());
                assertThat(names).containsExactly("c.txt", "g.txt");
            }
        }
    }

    // ── 3. walkFileTree ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. walkFileTree")
    class WalkFileTreeTest {

        @Test
        @DisplayName("visitFile is called for every file")
        void countsFiles(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            final int[] files = {0};
            Files.walkFileTree(tmp, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    files[0]++;
                    return FileVisitResult.CONTINUE;
                }
            });
            assertThat(files[0]).isEqualTo(7);
        }

        @Test
        @DisplayName("SKIP_SUBTREE prunes a directory without visiting its content")
        void skipSubtree(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            final List<String> visited = new ArrayList<>();
            Files.walkFileTree(tmp, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return dir.getFileName().toString().equals("skipme")
                            ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    visited.add(file.getFileName().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
            assertThat(visited).hasSize(6).doesNotContain("g.txt");
        }

        @Test
        @DisplayName("TERMINATE stops the whole walk")
        void terminate(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            final int[] seen = {0};
            Files.walkFileTree(tmp, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    seen[0]++;
                    return FileVisitResult.TERMINATE;
                }
            });
            assertThat(seen[0]).isEqualTo(1);
        }

        @Test
        @DisplayName("a directory is reported pre → files → post")
        void eventOrder(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            final List<String> events = new ArrayList<>();
            Files.walkFileTree(tmp.resolve("sub").resolve("deep"), new SimpleFileVisitor<Path>() {
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
            assertThat(events).containsExactly("pre:deep", "file:f.txt", "post:deep");
        }

        @Test
        @DisplayName("attributes arrive with the file — no extra stat call needed")
        void attributesProvided(@TempDir Path tmp) throws IOException {
            buildTree(tmp);
            final long[] total = {0};
            Files.walkFileTree(tmp, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    total[0] += attrs.size();
                    return FileVisitResult.CONTINUE;
                }
            });
            assertThat(total[0]).isEqualTo(2 + 3 + 4 + 1 + 2 + 3 + 4);
        }
    }

    // ── 4. PathMatcher ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. PathMatcher")
    class PathMatcherTest {

        private PathMatcher glob(String pattern) {
            return FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        }

        @Test
        @DisplayName("'*' does not cross a directory boundary")
        void starStaysInOneElement() {
            PathMatcher star = glob("*.java");
            assertThat(star.matches(Paths.get("Main.java"))).isTrue();
            assertThat(star.matches(Paths.get("src", "Main.java"))).isFalse();
        }

        @Test
        @DisplayName("matching against getFileName() fixes the '*' gotcha")
        void matchFileName() {
            PathMatcher star = glob("*.java");
            assertThat(star.matches(Paths.get("src", "Main.java").getFileName())).isTrue();
        }

        @Test
        @DisplayName("'**/' crosses directories but requires at least one")
        void doubleStar() {
            PathMatcher deep = glob("**/*.java");
            assertThat(deep.matches(Paths.get("src", "Main.java"))).isTrue();
            assertThat(deep.matches(Paths.get("src", "main", "Main.java"))).isTrue();
            assertThat(deep.matches(Paths.get("Main.java"))).isFalse();
        }

        @Test
        @DisplayName("'?' matches exactly one character")
        void questionMark() {
            PathMatcher single = glob("?.txt");
            assertThat(single.matches(Paths.get("a.txt"))).isTrue();
            assertThat(single.matches(Paths.get("ab.txt"))).isFalse();
        }

        @Test
        @DisplayName("'{a,b}' is alternation and '[abc]' a character class")
        void alternationAndClass() {
            PathMatcher either = glob("*.{txt,md}");
            assertThat(either.matches(Paths.get("a.md"))).isTrue();
            assertThat(either.matches(Paths.get("a.java"))).isFalse();

            PathMatcher range = glob("file[12].log");
            assertThat(range.matches(Paths.get("file1.log"))).isTrue();
            assertThat(range.matches(Paths.get("file3.log"))).isFalse();
        }

        @Test
        @DisplayName("regex: syntax uses java.util.regex")
        void regex() {
            PathMatcher regex = FileSystems.getDefault().getPathMatcher("regex:.*\\.(txt|md)");
            assertThat(regex.matches(Paths.get("notes.md"))).isTrue();
            assertThat(regex.matches(Paths.get("notes.java"))).isFalse();
        }

        @Test
        @DisplayName("an unknown syntax prefix is rejected")
        void unknownSyntax() {
            assertThatThrownBy(() -> FileSystems.getDefault().getPathMatcher("wildcard:*.txt"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}

package com.vbforge.file_io_nio;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * DirectoryStreamTasks — implement every method below to make {@code DirectoryStreamTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code DirectoryStreamDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=DirectoryStreamTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Close every directory stream / {@code Stream<Path>} you open.
 */
public class DirectoryStreamTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the file names of the <em>regular files</em> directly inside {@code dir} that match
     * {@code glob}, sorted alphabetically. Sub-directories are never returned — even when their
     * name matches — and nothing below the first level is looked at.
     *
     * <p>Examples (dir holds {@code a.txt, b.java, c.txt}, a sub-directory {@code d.txt/},
     * and {@code sub/e.txt}):
     * <pre>
     *   listFileNames(dir, "*.txt")          → ["a.txt", "c.txt"]
     *   listFileNames(dir, "*.{txt,java}")   → ["a.txt", "b.java", "c.txt"]
     *   listFileNames(dir, "*.md")           → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One {@code Files} factory method accepts a glob directly.</li>
     *   <li>The order of a directory stream is unspecified — sort the result yourself.</li>
     * </ul>
     *
     * @param dir  the directory to list
     * @param glob a glob pattern matched against the file name (e.g. {@code *.txt})
     * @return sorted file names (not full paths) of matching regular files
     * @throws IOException if {@code dir} does not exist or is not a directory
     */
    public List<String> listFileNames(Path dir, String glob) throws IOException {
        throw new UnsupportedOperationException("TODO: implement listFileNames");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Finds every regular file under {@code root} (any depth) whose file name ends with
     * {@code "." + extension}, compared case-insensitively. Returns the paths <em>relative to
     * {@code root}</em>, sorted in natural {@link Path} order. {@code extension} is given
     * without the dot.
     *
     * <p>Examples (tree: {@code a.txt, c.txt, b.java, sub/d.txt, sub/deep/f.txt, sub/e.java}):
     * <pre>
     *   findByExtension(root, "txt")   → [a.txt, c.txt, sub/d.txt, sub/deep/f.txt]
     *   findByExtension(root, "JAVA")  → [b.java, sub/e.java]
     *   findByExtension(root, "md")    → []
     * </pre>
     * A file named {@code notes.txt.bak} or just {@code txt} is <em>not</em> a {@code txt} file.
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code Stream}-returning {@code Files} method walks a whole tree.</li>
     *   <li>{@link Path#relativize(Path)} turns the walked paths into root-relative ones.</li>
     * </ul>
     *
     * @param root      the directory to search
     * @param extension the extension without a leading dot, e.g. {@code "txt"}
     * @return root-relative paths of matching files, sorted
     * @throws IOException if the tree cannot be read
     */
    public List<Path> findByExtension(Path root, String extension) throws IOException {
        throw new UnsupportedOperationException("TODO: implement findByExtension");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Sums the size in bytes of all regular files under {@code root} (any depth), grouped by
     * file extension. The extension is the text after the <em>last</em> {@code '.'} in the file
     * name, lower-cased. A file name without any {@code '.'} is grouped under the key
     * {@code "(none)"}. Directories contribute nothing.
     *
     * <p>Examples (sizes in bytes: {@code a.txt}=2, {@code c.txt}=4, {@code sub/d.TXT}=1,
     * {@code b.java}=3, {@code README}=5):
     * <pre>
     *   sizeByExtension(root)  → {"txt"=7, "java"=3, "(none)"=5}
     *   sizeByExtension(emptyDir) → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Files.walkFileTree} hands each file's {@code BasicFileAttributes} to {@code visitFile} —
     *       no need to ask for the size again.</li>
     *   <li>{@code Map.merge} accumulates nicely.</li>
     * </ul>
     *
     * @param root the directory to scan
     * @return map from lower-cased extension (or {@code "(none)"}) to total bytes
     * @throws IOException if the tree cannot be read
     */
    public Map<String, Long> sizeByExtension(Path root) throws IOException {
        throw new UnsupportedOperationException("TODO: implement sizeByExtension");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Recursively copies the directory tree {@code source} into {@code target}.
     * {@code target} (and missing parents) are created if necessary. Empty sub-directories are
     * preserved. If {@code target} already exists the copy <em>merges</em>: files that exist in both
     * are overwritten, files that exist only in {@code target} are left alone. {@code source} is not modified.
     *
     * <p>Examples:
     * <pre>
     *   source: a.txt, sub/b.txt, empty/        target: (does not exist)
     *   copyDirectory(source, target)  → target: a.txt, sub/b.txt, empty/
     *
     *   target already holds a.txt("old") and extra.txt
     *   copyDirectory(source, target)  → a.txt is overwritten, extra.txt is still there
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need to act <em>before</em> descending into a directory (to create its twin) and on
     *       each file — a file-tree visitor gives you both hooks.</li>
     *   <li>{@code Path.relativize} + {@code Path.resolve} map a source path to its target path.</li>
     * </ul>
     *
     * @param source the existing directory to copy
     * @param target the destination directory
     * @throws IOException if anything cannot be copied
     */
    public void copyDirectory(Path source, Path target) throws IOException {
        throw new UnsupportedOperationException("TODO: implement copyDirectory");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Lists every regular file under {@code root} (any depth) as a path relative to {@code root},
     * sorted in natural {@link Path} order — but <strong>never entering</strong> a directory whose name
     * equals {@code skipDirName} (at any depth). Only <em>directories</em> are skipped: a regular
     * file with that name is still listed. {@code root} itself is never skipped.
     *
     * <p>Examples (tree: {@code a.txt, sub/d.txt, sub/deep/f.txt, skipme/g.txt}):
     * <pre>
     *   listFilesSkipping(root, "skipme")  → [a.txt, sub/d.txt, sub/deep/f.txt]
     *   listFilesSkipping(root, "deep")    → [a.txt, skipme/g.txt, sub/d.txt]
     *   listFilesSkipping(root, "nothing") → [a.txt, skipme/g.txt, sub/d.txt, sub/deep/f.txt]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Pruning a branch <em>without visiting it</em> is the whole point — look at the constants of
     *       {@link java.nio.file.FileVisitResult}.</li>
     * </ul>
     *
     * @param root        the directory to scan
     * @param skipDirName name of directories to ignore, e.g. {@code "node_modules"}
     * @return sorted root-relative paths of the files that were not skipped
     * @throws IOException if the tree cannot be read
     */
    public List<Path> listFilesSkipping(Path root, String skipDirName) throws IOException {
        throw new UnsupportedOperationException("TODO: implement listFilesSkipping");
    }
}

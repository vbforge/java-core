package com.vbforge.file_io_basics;

import java.io.File;
import java.util.List;

/**
 * FileBasicsTasks — implement every method below to make {@code FileBasicsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code FileBasicsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=FileBasicsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Use {@link File} (not {@code java.nio.file}) in this class — that is the point of the exercise.
 */
public class FileBasicsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the extension of the file's <em>name</em> — the text after the last dot —
     * in lower case, without the dot. Returns an empty string when there is no extension:
     * no dot at all, a dot only at the very start (hidden files), or a dot at the very end.
     * Dots in parent directories are never considered.
     *
     * <p>Examples:
     * <pre>
     *   fileExtension("notes.txt")            → "txt"
     *   fileExtension("REPORT.PDF")           → "pdf"
     *   fileExtension("archive.tar.gz")       → "gz"
     *   fileExtension("README")               → ""
     *   fileExtension(".gitignore")           → ""      (leading dot = hidden file, not an extension)
     *   fileExtension("file.")                → ""
     *   fileExtension("/tmp/my.dir/readme")   → ""      (the dot belongs to a parent directory)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Work on {@code file.getName()}, not on the full path.</li>
     *   <li>{@code String.lastIndexOf} gives the position of the last dot.</li>
     * </ul>
     *
     * @param file any file or path (it does not have to exist)
     * @return lower-case extension without the dot, or {@code ""}
     */
    public String fileExtension(File file) {
        throw new UnsupportedOperationException("TODO: implement fileExtension");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Makes sure the parent directories of {@code file} exist, creating every missing level,
     * and returns the same {@code file} object. The file itself is <strong>not</strong> created.
     * If the parents already exist nothing happens. A file without a parent (a bare relative
     * name such as {@code "a.txt"}) is simply returned.
     *
     * <p>Examples:
     * <pre>
     *   ensureParentDirs(new File(tmp, "a/b/c/out.txt"))  → same File; tmp/a/b/c now exists, out.txt does not
     *   ensureParentDirs(new File(tmp, "out.txt"))        → same File; nothing to create
     *   ensureParentDirs(new File("a.txt"))               → same File; no parent, nothing to do
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getParentFile()} can return {@code null} — guard against it.</li>
     *   <li>Remember that {@code mkdirs()} returns {@code false} for an already existing directory;
     *       do not treat that as an error.</li>
     * </ul>
     *
     * @param file the file whose parent folders must exist
     * @return the same {@code file}
     */
    public File ensureParentDirs(File file) {
        throw new UnsupportedOperationException("TODO: implement ensureParentDirs");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Lists the names of the <em>regular files</em> that are directly inside {@code dir}
     * (sub-directories are skipped, nothing is searched recursively), sorted alphabetically.
     * Returns an empty list when {@code dir} does not exist or is not a directory.
     *
     * <p>Examples:
     * <pre>
     *   dir contains: b.txt, a.log, sub/ (a folder), c.csv   → ["a.log", "b.txt", "c.csv"]
     *   dir is empty                                          → []
     *   dir does not exist                                    → []
     *   dir is actually a regular file                        → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code listFiles} / {@code list} may return {@code null} — see the "null trap" in the demo.</li>
     *   <li>The order of the listing is not guaranteed; sort the result.</li>
     * </ul>
     *
     * @param dir the directory to look into
     * @return alphabetically sorted names of the files directly inside {@code dir}
     */
    public List<String> listFileNames(File dir) {
        throw new UnsupportedOperationException("TODO: implement listFileNames");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the total size in bytes of every regular file in the tree below
     * {@code root}, including all nested directories. If {@code root} is itself a regular
     * file its own length is returned. A path that does not exist counts as {@code 0}.
     *
     * <p>Examples:
     * <pre>
     *   root/{a.txt (3 B), sub/{b.txt (5 B), deeper/{c.txt (2 B)}}}  → 10
     *   root is a 7-byte file                                         → 7
     *   root is an empty directory                                    → 0
     *   root does not exist                                           → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the recursive pattern from "Walking a tree" in the demo.</li>
     *   <li>Directories have a {@code length()} too, but it is not the sum of their content —
     *       do not add it.</li>
     * </ul>
     *
     * @param root a file or directory
     * @return sum of the sizes of all regular files at or below {@code root}
     */
    public long totalSize(File root) {
        throw new UnsupportedOperationException("TODO: implement totalSize");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Deletes {@code target}. If it is a directory, everything inside it is deleted first.
     * Returns {@code true} only if the target existed and has been completely removed;
     * {@code false} if it did not exist in the first place.
     *
     * <p>Examples:
     * <pre>
     *   deleteRecursively(regular file)                     → true   (file gone)
     *   deleteRecursively(empty directory)                  → true
     *   deleteRecursively(tree with sub-folders and files)  → true   (whole tree gone)
     *   deleteRecursively(path that does not exist)         → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A directory can only be deleted when it is empty — so delete the children first.</li>
     *   <li>{@code listFiles()} returns {@code null} for regular files.</li>
     * </ul>
     *
     * @param target a file or directory
     * @return {@code true} if something existed and is now gone
     */
    public boolean deleteRecursively(File target) {
        throw new UnsupportedOperationException("TODO: implement deleteRecursively");
    }
}

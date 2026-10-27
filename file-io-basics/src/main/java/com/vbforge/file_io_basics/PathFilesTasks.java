package com.vbforge.file_io_basics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * PathFilesTasks — implement every method below to make {@code PathFilesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code PathFilesDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=PathFilesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Use {@code java.nio.file.Path} and {@code java.nio.file.Files} in this class. All text is UTF-8.
 */
public class PathFilesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the path that leads from the directory {@code base} to {@code target}.
     * Both paths are of the same kind (both absolute); the result may contain {@code ..}.
     *
     * <p>Examples:
     * <pre>
     *   relativeBetween(/data/app,       /data/app/logs/a.log)  → logs/a.log
     *   relativeBetween(/data/app/logs,  /data/cfg/x)           → ../../cfg/x
     *   relativeBetween(/data/app,       /data/app)             → (empty path)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a method on {@link Path} that does exactly this job.</li>
     * </ul>
     *
     * @param base   the starting directory
     * @param target the location to reach
     * @return the relative path from {@code base} to {@code target}
     */
    public Path relativeBetween(Path base, Path target) {
        throw new UnsupportedOperationException("TODO: implement relativeBetween");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Resolves {@code userPath} against {@code base} and returns the cleaned-up
     * ({@code .} and {@code ..} removed) result — but only if the result stays inside
     * {@code base}. A path that escapes {@code base} is rejected with an
     * {@link IllegalArgumentException}. The disk is never touched.
     *
     * <p>Examples:
     * <pre>
     *   resolveInside(/srv/files, "docs/a.txt")          → /srv/files/docs/a.txt
     *   resolveInside(/srv/files, "docs/../a.txt")       → /srv/files/a.txt
     *   resolveInside(/srv/files, "../etc/passwd")       → throws IllegalArgumentException
     *   resolveInside(/srv/files, "docs/../../x")        → throws IllegalArgumentException
     *   resolveInside(/srv/files, "/etc/passwd")         → throws IllegalArgumentException   (absolute input)
     *   resolveInside(/srv/files, "../files2/x")         → throws IllegalArgumentException   (sibling with the same prefix)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Order matters: resolve, then normalize, then check — see "Path safety" in the demo.</li>
     *   <li>{@code Path.startsWith(Path)} compares whole name elements, so the
     *       {@code files2} case is already handled for you.</li>
     *   <li>{@code base} is already normalised and absolute.</li>
     * </ul>
     *
     * @param base     the allowed root directory (absolute, normalised)
     * @param userPath untrusted relative path text
     * @return the normalised path inside {@code base}
     * @throws IllegalArgumentException if the result is not inside {@code base}
     */
    public Path resolveInside(Path base, String userPath) {
        throw new UnsupportedOperationException("TODO: implement resolveInside");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the lines of {@code file} that are <em>not blank</em> — lines that contain
     * at least one non-whitespace character. The lines are returned exactly as they are in
     * the file (not trimmed), in order.
     *
     * <p>Examples:
     * <pre>
     *   file ["a", "", "b"]            → ["a", "b"]
     *   file ["  ", "x ", "\t"]        → ["x "]
     *   file ["", "  ", ""]            → []
     *   empty file                     → []
     *   file ["  indented"]            → ["  indented"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Files.readAllLines} with an explicit charset gives you the list in one call.</li>
     *   <li>{@code String.trim().isEmpty()} is the Java 8 way to test for blank
     *       ({@code isBlank()} needs Java 11).</li>
     * </ul>
     *
     * @param file the file to read
     * @return the non-blank lines, untouched
     * @throws IOException if the file cannot be read
     */
    public List<String> nonBlankLines(Path file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement nonBlankLines");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Counts the lines of {@code file} that contain the text {@code needle} (as a substring,
     * case-sensitive) — <em>lazily</em>, without loading the whole file into memory.
     * The file handle must be closed when the method returns.
     *
     * <p>Examples:
     * <pre>
     *   file ["INFO a", "ERROR b", "INFO c", "ERROR d"], needle "ERROR"  → 2
     *   file ["abc", "ABC"],                             needle "abc"    → 1   (case-sensitive)
     *   file ["x"],                                      needle "y"      → 0
     *   empty file,                                      needle "x"      → 0
     *   file ["a", "b"],                                 needle ""       → 2   (every line contains "")
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Files.lines} returns a {@code Stream} that owns an open file —
     *       what do you do with resources?</li>
     *   <li>{@code Stream.filter(...).count()} does the counting.</li>
     * </ul>
     *
     * @param file   the file to scan
     * @param needle the text to look for
     * @return number of lines containing {@code needle}
     * @throws IOException if the file cannot be read
     */
    public long countLinesContaining(Path file, String needle) throws IOException {
        throw new UnsupportedOperationException("TODO: implement countLinesContaining");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Writes {@code text} (UTF-8) to {@code file}, first creating every missing parent
     * directory. An existing file is overwritten completely.
     *
     * <p>Examples:
     * <pre>
     *   writeText(tmp/a/b/c.txt, "hi")         → creates tmp/a/b/, file contains "hi"
     *   writeText(tmp/x.txt, "first")  then writeText(tmp/x.txt, "second") → file contains "second"
     *   writeText(tmp/e.txt, "")               → empty file
     *   writeText(tmp/u.txt, "héllo €")        → file size is 10 bytes
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getParent()} can be {@code null} for a path without a parent — guard it.</li>
     *   <li>{@code Files.createDirectories} does not complain if the directory already exists.</li>
     *   <li>The default open options of {@code Files.write} already overwrite.</li>
     * </ul>
     *
     * @param file the file to write
     * @param text the content
     * @throws IOException if the file cannot be written
     */
    public void writeText(Path file, String text) throws IOException {
        throw new UnsupportedOperationException("TODO: implement writeText");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Creates a backup copy of {@code file} next to it, named like the original with
     * {@code ".bak"} appended ({@code notes.txt → notes.txt.bak}). An already existing
     * backup is replaced. The original stays untouched. Returns the path of the backup.
     *
     * <p>Examples:
     * <pre>
     *   backup(dir/notes.txt)                      → dir/notes.txt.bak (same content as the original)
     *   backup(dir/notes.txt) twice                → the second call replaces the first backup
     *   backup(dir/data)                           → dir/data.bak
     *   backup(dir/missing.txt)                    → throws NoSuchFileException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Path.resolveSibling} builds a path in the same directory.</li>
     *   <li>Without a copy option, {@code Files.copy} refuses to overwrite — see {@code StandardCopyOption}.</li>
     * </ul>
     *
     * @param file the file to back up
     * @return the path of the backup file
     * @throws IOException if copying fails (for example {@link java.nio.file.NoSuchFileException})
     */
    public Path backup(Path file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement backup");
    }
}

package com.vbforge.file_io_nio;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * PathsAndFilesTasks — implement every method below to make {@code PathsAndFilesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code PathsAndFilesDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=PathsAndFilesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Always use UTF-8 explicitly; never rely on the platform default charset.
 */
public class PathsAndFilesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the path that leads <em>from</em> {@code from} <em>to</em> {@code to}.
     * Both arguments are {@linkplain Path#normalize() normalized} first, so {@code .} and
     * {@code name/..} segments never influence the result.
     *
     * <p>Examples (relative paths shown with {@code /}):
     * <pre>
     *   relativeBetween(a/b,       a/c/d)  → ../c/d
     *   relativeBetween(a/b,       a/b/c)  → c
     *   relativeBetween(a/b/../b,  a/./c)  → ../c      (normalized first: a/b → a/c)
     *   relativeBetween(a/b,       a/b)    → (empty path)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Path} has a method that is the inverse of {@code resolve}.</li>
     *   <li>Mixing an absolute and a relative path is an error — let the
     *       {@link IllegalArgumentException} from the JDK propagate.</li>
     * </ul>
     *
     * @param from the starting location (absolute and relative are both fine, but must match {@code to})
     * @param to   the destination
     * @return the relative path from {@code from} to {@code to}
     */
    public Path relativeBetween(Path from, Path to) {
        throw new UnsupportedOperationException("TODO: implement relativeBetween");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Resolves a user-supplied relative path against {@code baseDir} and guarantees the result
     * stays <em>inside</em> {@code baseDir} (protection against path traversal).
     * The result is normalized. {@code baseDir} itself counts as inside.
     * Anything that escapes — via {@code ..}, or an absolute {@code userInput} — must throw
     * {@link IllegalArgumentException}.
     *
     * <p>Examples ({@code base} = {@code /srv/uploads}):
     * <pre>
     *   resolveSafely(base, "photos/cat.png")       → /srv/uploads/photos/cat.png
     *   resolveSafely(base, "a/../b.txt")           → /srv/uploads/b.txt        (stays inside)
     *   resolveSafely(base, ".")                    → /srv/uploads
     *   resolveSafely(base, "../etc/passwd")        → throws IllegalArgumentException
     *   resolveSafely(base, "/etc/passwd")          → throws IllegalArgumentException
     *   resolveSafely(base, "../uploads-evil/x")    → throws IllegalArgumentException  (sibling with same prefix)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Normalize <em>before</em> checking, otherwise {@code ..} segments hide the escape.</li>
     *   <li>Compare {@link Path} objects with the {@code Path} API — comparing Strings would wrongly
     *       accept {@code /srv/uploads-evil}.</li>
     * </ul>
     *
     * @param baseDir   the directory the result must stay within (absolute)
     * @param userInput untrusted relative path fragment
     * @return the normalized resolved path, inside {@code baseDir}
     * @throws IllegalArgumentException if the result would be outside {@code baseDir}
     */
    public Path resolveSafely(Path baseDir, String userInput) {
        throw new UnsupportedOperationException("TODO: implement resolveSafely");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Writes {@code lines} to {@code file} as UTF-8 text, one line per element.
     * Missing parent directories are created. An existing file is overwritten completely
     * (not appended to). An empty list produces an empty file.
     *
     * <p>Examples:
     * <pre>
     *   writeLines(out/a.txt, ["x","y"])   → out/a.txt contains the lines x, y   (out/ is created)
     *   writeLines(a.txt (had "old"), ["new"])  → a.txt contains only the line new
     *   writeLines(a.txt, [])              → a.txt exists and is empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One {@code Files} method creates a whole missing directory chain without failing when it exists.</li>
     *   <li>The default open options of {@code Files.write} already truncate — check the demo.</li>
     * </ul>
     *
     * @param file  the file to (over)write
     * @param lines the lines to write
     * @throws IOException if the file cannot be written
     */
    public void writeLines(Path file, List<String> lines) throws IOException {
        throw new UnsupportedOperationException("TODO: implement writeLines");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Reads a UTF-8 text file and returns its lines <em>trimmed</em>, skipping lines that are
     * blank after trimming. If the file does not exist, returns an empty list instead of throwing.
     *
     * <p>Examples:
     * <pre>
     *   file "  a \n\n b\n   \n"   → ["a", "b"]
     *   file "one\ntwo"            → ["one", "two"]
     *   missing file               → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Decide whether to test for existence first or to catch a specific
     *       {@link java.nio.file.NoSuchFileException} — but do not swallow other I/O errors.</li>
     *   <li>If you use {@code Files.lines}, remember it must be closed.</li>
     * </ul>
     *
     * @param file the text file
     * @return trimmed, non-blank lines in file order (never {@code null})
     * @throws IOException if the file exists but cannot be read
     */
    public List<String> readNonBlankLines(Path file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement readNonBlankLines");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Replaces the content of {@code target} with {@code content} (UTF-8) <em>atomically</em>:
     * a concurrent reader sees either the complete old file or the complete new file, never a
     * half-written one. {@code target} may or may not exist already. When the method returns,
     * no temporary file may remain in the directory — and if anything fails, the temporary
     * file must be cleaned up too.
     *
     * <p>Examples:
     * <pre>
     *   replaceAtomically(dir/config.txt (missing),  "v1")  → dir/config.txt = "v1"
     *   replaceAtomically(dir/config.txt (had "v1"), "v2")  → dir/config.txt = "v2"
     *   after either call, dir contains exactly one entry: config.txt
     *   target is a NON-EMPTY DIRECTORY                     → throws IOException, and no temporary file is left behind
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Write the new content to a temporary file in the <em>same directory</em> as the target —
     *       an atomic move only works within one file store.</li>
     *   <li>{@code StandardCopyOption} has a constant for exactly this kind of move.</li>
     * </ul>
     *
     * @param target  the file to replace; must have a parent directory that exists
     * @param content the new content
     * @throws IOException if writing or moving fails
     */
    public void replaceAtomically(Path target, String content) throws IOException {
        throw new UnsupportedOperationException("TODO: implement replaceAtomically");
    }
}

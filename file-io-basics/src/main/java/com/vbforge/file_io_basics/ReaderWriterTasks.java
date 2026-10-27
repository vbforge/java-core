package com.vbforge.file_io_basics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * ReaderWriterTasks — implement every method below to make {@code ReaderWriterTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ReaderWriterDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ReaderWriterTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * All files are UTF-8 text. Use the character streams from the demo
 * ({@code BufferedReader}, {@code BufferedWriter}, ...), always close them, and name the charset explicitly.
 */
public class ReaderWriterTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Writes every element of {@code lines} to {@code file}, each one followed by a line break.
     * The file is created if it is missing and <strong>overwritten</strong> if it exists.
     * An empty list produces an empty file.
     *
     * <p>Examples:
     * <pre>
     *   writeLines(f, ["roses", "violets"])     → file reads back as ["roses", "violets"]
     *   writeLines(f, ["héllo €"])              → file reads back as ["héllo €"]   (UTF-8)
     *   writeLines(f, [])                       → empty file (length 0)
     *   writeLines(f, ["old"]) then writeLines(f, ["new"])  → file reads back as ["new"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code BufferedWriter} over an {@code OutputStreamWriter} gives you buffering,
     *       an explicit charset and {@code newLine()}.</li>
     *   <li>Opening the stream in non-append mode already empties an existing file.</li>
     * </ul>
     *
     * @param file  the target file
     * @param lines the lines to write
     * @throws IOException if the file cannot be written
     */
    public void writeLines(File file, List<String> lines) throws IOException {
        throw new UnsupportedOperationException("TODO: implement writeLines");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Reads the whole file and returns its lines, in order, without line breaks.
     * Blank lines are kept as empty strings; a final line break does not create an extra
     * empty element. An empty file gives an empty list.
     *
     * <p>Examples:
     * <pre>
     *   file "a\nb\n"        → ["a", "b"]
     *   file "a\n\nb"        → ["a", "", "b"]
     *   file "héllo €\n"     → ["héllo €"]
     *   empty file           → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The loop {@code while ((line = reader.readLine()) != null)} is the classic way.</li>
     *   <li>The reader must be closed even if reading fails.</li>
     * </ul>
     *
     * @param file the file to read
     * @return the lines of the file
     * @throws IOException if the file cannot be read
     */
    public List<String> readLines(File file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement readLines");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Appends {@code line} followed by a line break to the end of {@code file}.
     * The file is created if it does not exist; existing content is kept untouched.
     *
     * <p>Examples:
     * <pre>
     *   missing file,        appendLine(f, "one")                 → file reads back as ["one"]
     *   file ["one"],        appendLine(f, "two")                 → ["one", "two"]
     *   appendLine(f, "a"), appendLine(f, "b"), appendLine(f, "c") → ["a", "b", "c"]
     *   appendLine(f, "")                                         → adds one blank line
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Append mode is chosen when the underlying byte stream is opened — see the
     *       second argument of {@code FileOutputStream}.</li>
     *   <li>Do not use {@code new FileWriter(file, true)}: it ignores your charset choice on Java 8.</li>
     * </ul>
     *
     * @param file the file to extend
     * @param line the text of the new line (without line break)
     * @throws IOException if the file cannot be written
     */
    public void appendLine(File file, String line) throws IOException {
        throw new UnsupportedOperationException("TODO: implement appendLine");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Counts the words in the file. A word is a maximal run of non-whitespace characters;
     * lines may contain any amount of leading, trailing or repeated whitespace, and blank
     * lines contain no words.
     *
     * <p>Examples:
     * <pre>
     *   file "one two three\n"                 → 3
     *   file "  spaced    out  \n\nlast\n"     → 3
     *   file "\n\n   \n"                       → 0
     *   empty file                             → 0
     *   file "tab\tseparated words"            → 3
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code "".split("\\s+")} and {@code "  a".split("\\s+")} both give an element that is
     *       an empty string — {@code trim()} first and handle the empty line explicitly.</li>
     *   <li>Process line by line; do not load the file into one big string.</li>
     * </ul>
     *
     * @param file the file to scan
     * @return number of words
     * @throws IOException if the file cannot be read
     */
    public int countWords(File file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement countWords");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the longest line of the file (by number of characters). If several lines
     * share the maximum length, the <em>first</em> of them wins. An empty file gives
     * {@link Optional#empty()}.
     *
     * <p>Examples:
     * <pre>
     *   file ["a", "ccc", "bb"]        → Optional["ccc"]
     *   file ["ab", "cd", "e"]         → Optional["ab"]     (tie: first one wins)
     *   file ["", "", "x"]             → Optional["x"]
     *   file with a single blank line  → Optional[""]
     *   empty file                     → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Keep only the best candidate while reading — you never need all lines in memory.</li>
     *   <li>Use a strict {@code >} comparison so ties keep the earlier line.</li>
     * </ul>
     *
     * @param file the file to scan
     * @return the longest line, or empty if the file has no lines
     * @throws IOException if the file cannot be read
     */
    public Optional<String> longestLine(File file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement longestLine");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Copies {@code source} to {@code target} line by line, putting a 1-based line number
     * in front of every line in the form {@code "<n>: <text>"}. The target is created or
     * overwritten. The source is not modified.
     *
     * <p>Examples:
     * <pre>
     *   source ["alpha", "beta"]    → target ["1: alpha", "2: beta"]
     *   source ["a", "", "c"]       → target ["1: a", "2: ", "3: c"]
     *   source ["héllo €"]          → target ["1: héllo €"]
     *   empty source                → empty target
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reading and writing happen at the same time — you need two resources open together.</li>
     *   <li>Do not read the whole source first; transform each line as it arrives.</li>
     * </ul>
     *
     * @param source the file to read
     * @param target the file to write
     * @throws IOException if reading or writing fails
     */
    public void copyWithLineNumbers(File source, File target) throws IOException {
        throw new UnsupportedOperationException("TODO: implement copyWithLineNumbers");
    }
}

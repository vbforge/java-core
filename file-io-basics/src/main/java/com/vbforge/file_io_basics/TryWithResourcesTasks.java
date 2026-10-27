package com.vbforge.file_io_basics;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * TryWithResourcesTasks — implement every method below to make {@code TryWithResourcesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code TryWithResourcesDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=TryWithResourcesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every resource must be managed with {@code try-with-resources} — no manual {@code finally}.
 * All files are UTF-8 text.
 */
public class TryWithResourcesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the first line of {@code file}, without its line break, or {@code null}
     * if the file is empty. The reader must be closed.
     *
     * <p>Examples:
     * <pre>
     *   file "alpha\nbeta\n"   → "alpha"
     *   file "only"            → "only"
     *   file "\nsecond"        → ""        (the first line exists and is blank)
     *   empty file             → null
     *   file "héllo €\n"       → "héllo €"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code readLine()} already returns {@code null} at the end of input.</li>
     *   <li>Declare the reader (and the stream inside it, if you build one) in the try header.</li>
     * </ul>
     *
     * @param file the file to read
     * @return the first line or {@code null}
     * @throws IOException if the file cannot be read
     */
    public String readFirstLine(File file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement readFirstLine");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the first line of {@code file}, or {@code fallback} when the file does not
     * exist or is empty. Any other I/O problem is <em>not</em> hidden — it propagates.
     *
     * <p>Examples:
     * <pre>
     *   file "alpha\nbeta"       , fallback "none"  → "alpha"
     *   file does not exist      , fallback "none"  → "none"
     *   empty file               , fallback "none"  → "none"
     *   file "\nsecond"          , fallback "none"  → ""         (blank first line is still a line)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You can reuse {@link #readFirstLine(File)} once you have implemented it.</li>
     *   <li>Checking {@code file.exists()} up front is simpler than catching an exception here.</li>
     * </ul>
     *
     * @param file     the file to read
     * @param fallback the value for a missing or empty file
     * @return the first line or {@code fallback}
     * @throws IOException if the file exists but cannot be read
     */
    public String firstLineOrDefault(File file, String fallback) throws IOException {
        throw new UnsupportedOperationException("TODO: implement firstLineOrDefault");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Copies {@code in} to {@code out}, converting every line to upper case
     * (with {@code String.toUpperCase(java.util.Locale.ROOT)}). The output file is created or
     * overwritten. Both the reader and the writer must be closed — declare both in
     * <em>one</em> try-with-resources header.
     *
     * <p>Examples:
     * <pre>
     *   in ["hello", "World"]     → out ["HELLO", "WORLD"]
     *   in ["", "a"]              → out ["", "A"]
     *   in ["straße"]             → out ["STRASSE"]
     *   empty in                  → empty out
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Separate the two resources with a semicolon; they will be closed in reverse order.</li>
     *   <li>Process one line at a time — the file may be larger than memory.</li>
     * </ul>
     *
     * @param in  the source file
     * @param out the target file
     * @throws IOException if reading or writing fails
     */
    public void upperCaseCopy(File in, File out) throws IOException {
        throw new UnsupportedOperationException("TODO: implement upperCaseCopy");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Opens two {@link TryWithResourcesDemo.NamedResource}s — {@code first}, then
     * {@code second} — in a single try-with-resources header, adds the text {@code "body"} to the
     * shared log inside the try block, and returns the log. Because the resources log
     * {@code "open <name>"} when created and {@code "close <name>"} when closed, the returned
     * list shows the exact order of events.
     *
     * <p>Examples:
     * <pre>
     *   eventLog("A", "B")  → ["open A", "open B", "body", "close B", "close A"]
     *   eventLog("x", "x")  → ["open x", "open x", "body", "close x", "close x"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Create an {@code ArrayList<String>} first and pass it to both resources.</li>
     *   <li>You do not need to write any {@code close} calls yourself.</li>
     * </ul>
     *
     * @param first  name of the resource opened first
     * @param second name of the resource opened second
     * @return the event log
     */
    public List<String> eventLog(String first, String second) {
        throw new UnsupportedOperationException("TODO: implement eventLog");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Runs a try-with-resources whose body throws {@code new IllegalStateException(bodyMessage)}
     * and whose only resource, a {@link TryWithResourcesDemo.FailingResource}, throws
     * {@code IllegalStateException(closeMessage)} from {@code close()}. Catches the exception that
     * escapes and returns a summary of both problems in the form
     * {@code "body=<primary message>, suppressed=<message of the first suppressed exception>"}.
     *
     * <p>Examples:
     * <pre>
     *   failureSummary("boom", "close failed")  → "body=boom, suppressed=close failed"
     *   failureSummary("a", "b")                → "body=a, suppressed=b"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Which of the two exceptions is the "primary" one? Re-read the "Suppressed exceptions"
     *       section of the demo.</li>
     *   <li>{@code Throwable.getSuppressed()} returns an array.</li>
     * </ul>
     *
     * @param bodyMessage  message for the exception thrown by the body
     * @param closeMessage message for the exception thrown by {@code close()}
     * @return the summary text
     */
    public String failureSummary(String bodyMessage, String closeMessage) {
        throw new UnsupportedOperationException("TODO: implement failureSummary");
    }
}

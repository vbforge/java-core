package com.vbforge.file_io_basics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * ReaderWriterDemo — annotated reference for reading and writing <em>text</em> with the
 * character streams: {@link BufferedReader}, {@link BufferedWriter}, {@link PrintWriter}
 * and {@link Scanner}.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>Writing text — {@code BufferedWriter}, overwrite vs append</li>
 *   <li>Reading text — {@code readLine()}, the {@code null} end marker, blank lines</li>
 *   <li>Charsets — characters are not bytes</li>
 *   <li>PrintWriter — convenient, but it swallows exceptions</li>
 *   <li>Scanner — token-based reading and the {@code nextInt()} / {@code nextLine()} trap</li>
 * </ol>
 */
public class ReaderWriterDemo {

    public static void main(String[] args) throws IOException {
        File workDir = Files.createTempDirectory("file-io-basics-").toFile();
        try {
            writingDemo(workDir);
            System.out.println("---");
            readingDemo(workDir);
            System.out.println("---");
            charsetDemo(workDir);
            System.out.println("---");
            printWriterDemo(workDir);
            System.out.println("---");
            scannerDemo(workDir);
        } finally {
            PathFilesDemo.deleteTree(workDir.toPath());
        }
    }

    // ── 1. Writing text ───────────────────────────────────────────────────────

    /**
     * A {@link java.io.Writer} turns characters into bytes and sends them to a destination.
     * {@link BufferedWriter} adds an in-memory buffer so many small {@code write} calls become
     * a few large ones, and {@code newLine()} for line breaks.
     *
     * <p>WHY this matters: every unbuffered write to a file is a call into the operating system.
     * Wrapping in a {@code BufferedWriter} is the standard way to make text output fast.
     *
     * <p>Overwrite or append is decided when the stream is <em>opened</em>:
     * {@code new FileOutputStream(file)} truncates the file; {@code new FileOutputStream(file, true)}
     * appends to it.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code new FileWriter(file)} silently uses the platform default charset on Java 8–17
     *       (UTF-8 only from Java 18). Wrap an {@code OutputStreamWriter} with an explicit charset instead.</li>
     *   <li>{@code newLine()} writes the platform line separator ({@code \n} or {@code \r\n}),
     *       not always {@code \n}.</li>
     *   <li>Unflushed data is lost if the writer is never closed — close it (try-with-resources, next demo).</li>
     * </ul>
     */
    static void writingDemo(File workDir) throws IOException {
        System.out.println("=== Writing text ===");

        File file = new File(workDir, "poem.txt");

        try (BufferedWriter writer = newWriter(file, false)) {   // false = overwrite
            writer.write("roses are red");
            writer.newLine();
            writer.write("violets are blue");
            writer.newLine();
        }
        System.out.println("after write    : " + Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        // [roses are red, violets are blue]

        try (BufferedWriter writer = newWriter(file, true)) {    // true = append
            writer.write("sugar is sweet");
            writer.newLine();
        }
        System.out.println("after append   : " + Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        // [roses are red, violets are blue, sugar is sweet]

        try (BufferedWriter writer = newWriter(file, false)) {   // opening in overwrite mode empties the file at once
            writer.write("only line");
            writer.newLine();
        }
        System.out.println("after overwrite: " + Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        // [only line]
    }

    /** UTF-8 {@link BufferedWriter}; {@code append} selects append or overwrite mode. */
    private static BufferedWriter newWriter(File file, boolean append) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));
    }

    // ── 2. Reading text ───────────────────────────────────────────────────────

    /**
     * {@link BufferedReader#readLine()} returns the next line <em>without</em> its line break,
     * and {@code null} when there is nothing left. That {@code null} is the only end-of-file signal.
     *
     * <p>WHY this matters: the loop {@code while ((line = reader.readLine()) != null)} is the
     * idiom for processing a text file one line at a time without loading it all into memory —
     * O(n) time, O(longest line) memory.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>A trailing line break does <em>not</em> produce an extra empty last line:
     *       {@code "a\nb\n"} and {@code "a\nb"} both give {@code [a, b]}.</li>
     *   <li>Blank lines in the middle do show up as empty strings.</li>
     *   <li>An empty file gives {@code null} on the very first call.</li>
     *   <li>{@code read()} (single character) returns an {@code int}: {@code -1} at the end.</li>
     * </ul>
     */
    static void readingDemo(File workDir) throws IOException {
        System.out.println("=== Reading text ===");

        File file = new File(workDir, "lines.txt");
        Files.write(file.toPath(), "a\n\nb\n".getBytes(StandardCharsets.UTF_8));

        // The classic loop
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = newReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        System.out.println("lines          : " + lines);                    // [a, , b]
        System.out.println("count          : " + lines.size());             // 3 — the blank line counts, the final \n does not

        // No trailing newline gives the same list shape
        Files.write(file.toPath(), "a\nb".getBytes(StandardCharsets.UTF_8));
        try (BufferedReader reader = newReader(file)) {
            System.out.println("no final \\n    : " + reader.lines().collect(Collectors.toList())); // [a, b]
        }

        // Empty file: the very first readLine() is already null
        Files.write(file.toPath(), new byte[0]);
        try (BufferedReader reader = newReader(file)) {
            System.out.println("empty file     : " + reader.readLine());    // null
        }

        // read() returns an int; -1 means end of stream
        Files.write(file.toPath(), "hi".getBytes(StandardCharsets.UTF_8));
        try (BufferedReader reader = newReader(file)) {
            System.out.println("read()         : " + (char) reader.read()); // h
            System.out.println("read()         : " + (char) reader.read()); // i
            System.out.println("read() at end  : " + reader.read());        // -1
        }
    }

    /** UTF-8 {@link BufferedReader}. */
    private static BufferedReader newReader(File file) throws IOException {
        return new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    // ── 3. Charsets ───────────────────────────────────────────────────────────

    /**
     * A file holds <em>bytes</em>; a charset is the rule that maps characters to bytes and back.
     * UTF-8 uses 1 byte for ASCII and 2–4 bytes for everything else.
     *
     * <p>WHY this matters: reading with a different charset than the one used for writing does
     * not fail — it silently produces garbage ("mojibake") or {@code ?}. Always name the charset
     * explicitly; never rely on the machine's default.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>String length (characters) and byte length are different numbers for non-ASCII text.</li>
     *   <li>Encoding a character the charset cannot represent (€ in ISO-8859-1) yields {@code ?}.</li>
     * </ul>
     */
    static void charsetDemo(File workDir) throws IOException {
        System.out.println("=== Charsets ===");

        String text = "héllo €";                                    // 7 characters
        System.out.println("characters     : " + text.length());    // 7

        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        System.out.println("UTF-8 bytes    : " + utf8.length);      // 10  (é = 2 bytes, € = 3 bytes)

        byte[] latin1 = text.getBytes(StandardCharsets.ISO_8859_1);
        System.out.println("Latin-1 bytes  : " + latin1.length);    // 7   (one byte each ...)
        System.out.println("€ in Latin-1   : " + (char) latin1[6]); // ?   (... and € cannot be represented)

        // Round trip with the SAME charset is lossless
        File file = new File(workDir, "text-utf8.txt");
        Files.write(file.toPath(), utf8);
        try (BufferedReader reader = newReader(file)) {
            System.out.println("same charset   : " + reader.readLine().equals(text));   // true
        }

        // Reading UTF-8 bytes with the WRONG charset: no exception, just wrong text
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
            String wrong = reader.readLine();
            System.out.println("wrong charset  : " + wrong.length());     // 10 — every byte became its own character
            System.out.println("equal to text? : " + wrong.equals(text)); // false
        }

        // The default charset is machine-dependent — the reason to always be explicit
        System.out.println("default is set: " + (Charset.defaultCharset() != null));   // true (but WHICH one varies)
    }

    // ── 4. PrintWriter ────────────────────────────────────────────────────────

    /**
     * {@link PrintWriter} adds {@code println}, {@code print} and {@code printf} on top of any
     * writer or file — the same API as {@code System.out}, pointed at a file.
     *
     * <p>WHY this matters: it is the shortest way to produce formatted text output.
     * The price: none of its methods throw {@link IOException}. A failed write only sets an
     * internal flag that you can read with {@code checkError()} — if you never ask, you never know.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code println} ends lines with the platform separator, like {@code newLine()}.</li>
     *   <li>{@code printf("%.2f", x)} depends on the default locale (comma vs dot) — pass a
     *       {@code Locale} when the format must be stable.</li>
     *   <li>Writing to a closed PrintWriter does not throw; {@code checkError()} becomes {@code true}.</li>
     * </ul>
     */
    static void printWriterDemo(File workDir) throws IOException {
        System.out.println("=== PrintWriter ===");

        File file = new File(workDir, "report.txt");

        try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
            out.println("Report");
            out.printf("items: %d%n", 3);                      // %n = platform line break
            out.print("done");
        }
        System.out.println("content        : " + Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        // [Report, items: 3, done]

        // Errors are swallowed — only checkError() reveals them
        PrintWriter closed = new PrintWriter(file, "UTF-8");
        closed.close();
        closed.println("too late");                            // no exception!
        System.out.println("checkError     : " + closed.checkError());   // true
    }

    // ── 5. Scanner ────────────────────────────────────────────────────────────

    /**
     * {@link Scanner} splits input into <em>tokens</em> (separated by whitespace) and can parse
     * them as numbers: {@code nextInt()}, {@code nextDouble()}, ... — or read whole lines with
     * {@code nextLine()}.
     *
     * <p>WHY this matters: it is the quickest way to read numbers or words from a file or
     * the console without writing the splitting code yourself. It is slower than
     * {@code BufferedReader} for big files, so prefer the latter for plain line processing.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code nextInt()} stops <em>before</em> the line break. The next {@code nextLine()}
     *       then returns the rest of the current line — an empty string — not the next line.</li>
     *   <li>A Scanner hides {@link IOException}; check {@code ioException()} if it matters.</li>
     *   <li>Always pass a charset when scanning a file ({@code new Scanner(file, "UTF-8")}).</li>
     * </ul>
     */
    static void scannerDemo(File workDir) throws IOException {
        System.out.println("=== Scanner ===");

        File file = new File(workDir, "numbers.txt");
        Files.write(file.toPath(), "10 20\n30\nhello world\n".getBytes(StandardCharsets.UTF_8));

        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            int sum = 0;
            for (int i = 0; i < 3; i++) {
                sum += scanner.nextInt();                       // tokens: 10, 20, 30 — line breaks are just whitespace
            }
            System.out.println("sum            : " + sum);       // 60

            String rest = scanner.nextLine();                    // GOTCHA: the remainder of the "30" line
            System.out.println("nextLine       : '" + rest + "'"); // ''  (empty!)
            System.out.println("next nextLine  : '" + scanner.nextLine() + "'"); // 'hello world'
            System.out.println("hasNextLine    : " + scanner.hasNextLine());      // false
        }
    }
}

package com.vbforge.practice.practice_9_character_streams;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates character streams — Reader and Writer and their key subclasses.
 *
 * Why character streams instead of byte streams for text?
 *   Bytes are just numbers. Text has an ENCODING — a mapping from bytes to characters.
 *   If you read a UTF-8 file byte-by-byte you get bytes, not characters.
 *   Reader/Writer handle charset conversion transparently:
 *
 *     FileInputStream  → bytes (e.g. 0xC3 0xA9)
 *     InputStreamReader wrapping it → characters (e.g. 'é')
 *
 * Core abstract classes:
 *   Reader  — source of characters (read() returns int: char or -1)
 *   Writer  — sink of characters   (write(int c), write(String), etc.)
 *
 * Key concrete classes:
 *
 *   FileReader / FileWriter
 *     — direct file ↔ character access
 *     — FileReader uses platform default charset (risky!); prefer InputStreamReader
 *       with explicit charset for correctness across platforms.
 *
 *   InputStreamReader / OutputStreamWriter
 *     — BRIDGE between byte streams and character streams
 *     — Always specify charset explicitly: new InputStreamReader(is, StandardCharsets.UTF_8)
 *
 *   StringReader / StringWriter
 *     — in-memory character I/O; StringWriter backed by StringBuilder
 *     — useful for capturing Writer output in tests or building strings
 *
 *   PrintWriter
 *     — adds print/println/printf on top of Writer
 *     — autoFlush option: flush after every println (useful for network sockets)
 *     — SWALLOWS exceptions by default! Always check checkError() or use underlying writer.
 */
public class ReadersWritersDemo {

    static final Path DATA_DIR = Path.of("file-io-basics/data_files");

    // =========================================================================
    // 1. FileReader / FileWriter — simplest text file I/O
    // =========================================================================

    static void fileReaderWriterDemo() throws IOException {
        System.out.println("\n--- 1. FileReader / FileWriter ---");

        Path out = DATA_DIR.resolve("output_rw_file.txt");

        try (FileWriter fw = new FileWriter(out.toFile())) {
            fw.write("Hello from FileWriter\n");
            fw.write("Second line\n");
        }

        System.out.print("FileReader read: ");
        try (FileReader fr = new FileReader(out.toFile())) {
            char[] buf = new char[256];
            int n;
            while ((n = fr.read(buf)) != -1) {
                System.out.print(new String(buf, 0, n));
            }
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 2. InputStreamReader / OutputStreamWriter — explicit charset bridge
    // =========================================================================

    static void bridgeStreamsDemo() throws IOException {
        System.out.println("\n--- 2. InputStreamReader / OutputStreamWriter (bridge) ---");

        Path out = DATA_DIR.resolve("output_rw_bridge.txt");

        try (OutputStreamWriter osw = new OutputStreamWriter(
                new FileOutputStream(out.toFile()), StandardCharsets.UTF_8)) {
            osw.write("Мова: Java — encoding: UTF-8\n");
            osw.write("Line two\n");
        }

        System.out.print("bridge read: ");
        try (InputStreamReader isr = new InputStreamReader(
                new FileInputStream(out.toFile()), StandardCharsets.UTF_8)) {
            char[] buf = new char[256];
            int n;
            while ((n = isr.read(buf)) != -1) {
                System.out.print(new String(buf, 0, n));
            }
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 3. StringReader / StringWriter — in-memory character streams
    // =========================================================================

    static void stringReaderWriterDemo() throws IOException {
        System.out.println("\n--- 3. StringReader / StringWriter ---");

        StringWriter sw = new StringWriter();
        sw.write("captured ");
        sw.write("in memory");
        System.out.println("StringWriter: " + sw.toString());

        StringReader sr = new StringReader("Hello from StringReader");
        char[] buf = new char[64];
        int n = sr.read(buf);
        System.out.println("StringReader: " + new String(buf, 0, n));
        sr.close();

        processText(new StringReader("processing a string as a Reader"));
    }

    static void processText(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[128];
        int n;
        while ((n = reader.read(buf)) != -1) sb.append(buf, 0, n);
        System.out.println("processText received: " + sb);
    }

    // =========================================================================
    // 4. PrintWriter — formatted output to any Writer
    // =========================================================================

    static void printWriterDemo() throws IOException {
        System.out.println("\n--- 4. PrintWriter ---");

        Path out = DATA_DIR.resolve("output_rw_print.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(out.toFile()))) {
            pw.println("line one");
            pw.println("line two");
            pw.printf("formatted: %d + %d = %d%n", 3, 4, 7);
            pw.print("no-newline at end");

            if (pw.checkError()) System.out.println("  [PrintWriter had an error!]");
        }

        System.out.println("PrintWriter output:");
        Files.readAllLines(out).forEach(l -> System.out.println("  " + l));

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 5. Reading a CSV file line-by-line with FileReader + char-by-char
    // =========================================================================

    static void csvParseDemo() throws IOException {
        System.out.println("\n--- 5. CSV parse via FileReader ---");

        Path csv = DATA_DIR.resolve("people.csv");

        try (FileReader fr = new FileReader(csv.toFile())) {
            StringBuilder line = new StringBuilder();
            int ch;
            int rowNum = 0;

            while ((ch = fr.read()) != -1) {
                if (ch == '\n') {
                    if (line.length() > 0) {
                        String[] parts = line.toString().split(",");
                        System.out.printf("  row %d: name=%-8s age=%-3s city=%s%n",
                            ++rowNum, parts[0], parts[1], parts[2].trim());
                    }
                    line.setLength(0);
                } else {
                    line.append((char) ch);
                }
            }
            if (line.length() > 0) {
                System.out.println("  remaining: " + line);
            }
        }
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== Readers & Writers Demo ===");
        fileReaderWriterDemo();
        bridgeStreamsDemo();
        stringReaderWriterDemo();
        printWriterDemo();
        csvParseDemo();
    }
}
package com.vbforge.practice.practice_10_buffered_io;

import java.io.*;
import java.nio.file.*;

/**
 * Demonstrates buffered I/O — the most important performance pattern in java.io.
 *
 * The problem with unbuffered I/O:
 *   Every read() or write() call on an unbuffered FileInputStream/FileReader
 *   goes directly to the OS, which performs a system call.
 *   System calls are EXPENSIVE (context switch from user space to kernel space).
 *
 *   Reading a 1 MB file byte-by-byte = ~1,000,000 system calls.
 *   Reading the same file with a 8KB buffer = ~128 system calls.
 *
 * The solution — buffered wrappers:
 *   ┌─────────────────────────────────────────────────────────────────────┐
 *   │  BufferedInputStream  — buffers bytes read from an InputStream      │
 *   │  BufferedOutputStream — accumulates bytes, flushes in chunks        │
 *   │  BufferedReader       — buffers chars + provides readLine()         │
 *   │  BufferedWriter       — buffers chars + provides newLine()          │
 *   └─────────────────────────────────────────────────────────────────────┘
 *
 * Default buffer size: 8192 bytes / 8192 chars. Tunable via constructor.
 *
 * CRITICAL:
 *   - ALWAYS wrap file streams in a buffered wrapper for production code.
 *   - Flush explicitly (or via close()) to ensure buffered data is written.
 *   - For text files: Files.newBufferedReader/Writer is the idiomatic Java 7+ way.
 */
public class BufferedIoDemo {

    static final Path DATA_DIR = Path.of("file-io-basics/data_files");

    // =========================================================================
    // 1. BufferedReader — readLine() is the key feature
    // =========================================================================

    static void bufferedReaderDemo() throws IOException {
        System.out.println("\n--- 1. BufferedReader.readLine() ---");

        Path sample = DATA_DIR.resolve("sample.txt");

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(sample.toFile()),
                    java.nio.charset.StandardCharsets.UTF_8))) {

            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                System.out.printf("  %2d: %s%n", ++lineNum, line);
            }
        }
    }

    // =========================================================================
    // 2. Files.newBufferedReader — idiomatic Java 7+ shortcut
    // =========================================================================

    static void filesNewBufferedReaderDemo() throws IOException {
        System.out.println("\n--- 2. Files.newBufferedReader (idiomatic) ---");

        Path csv = DATA_DIR.resolve("people.csv");

        try (BufferedReader br = Files.newBufferedReader(csv,
                java.nio.charset.StandardCharsets.UTF_8)) {

            br.lines()
              .map(line -> line.split(","))
              .forEach(parts ->
                  System.out.printf("  name=%-8s age=%-3s city=%s%n",
                      parts[0], parts[1], parts[2].trim())
              );
        }
    }

    // =========================================================================
    // 3. BufferedWriter — newLine() and write with flush
    // =========================================================================

    static void bufferedWriterDemo() throws IOException {
        System.out.println("\n--- 3. BufferedWriter ---");

        Path out = DATA_DIR.resolve("buffered_output.txt");

        try (BufferedWriter bw = Files.newBufferedWriter(out,
                java.nio.charset.StandardCharsets.UTF_8)) {

            bw.write("First line");
            bw.newLine();
            bw.write("Second line");
            bw.newLine();
            bw.write("Third line");
        }

        System.out.println("written lines:");
        Files.readAllLines(out).forEach(l -> System.out.println("  " + l));

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 4. BufferedInputStream / BufferedOutputStream — binary buffering
    // =========================================================================

    static void bufferedByteStreamDemo() throws IOException {
        System.out.println("\n--- 4. BufferedInputStream / BufferedOutputStream ---");

        Path src  = DATA_DIR.resolve("sample.txt");
        Path dest = DATA_DIR.resolve("buffered_copy.txt");

        long start = System.nanoTime();

        try (BufferedInputStream  bis = new BufferedInputStream(
                    new FileInputStream(src.toFile()));
             BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(dest.toFile()))) {

            byte[] buf = new byte[4096];
            int n;
            long total = 0;
            while ((n = bis.read(buf)) != -1) {
                bos.write(buf, 0, n);
                total += n;
            }
            System.out.println("buffered binary copy: " + total + " bytes");
        }

        long elapsed = System.nanoTime() - start;
        System.out.printf("time: %.3f ms%n", elapsed / 1_000_000.0);

        Files.deleteIfExists(dest);
    }

    // =========================================================================
    // 5. mark() and reset() — re-read a portion of a stream
    // =========================================================================

    static void markResetDemo() throws IOException {
        System.out.println("\n--- 5. mark() / reset() ---");

        byte[] data = "ABCDEFGHIJ".getBytes();
        BufferedInputStream bis = new BufferedInputStream(
            new ByteArrayInputStream(data));

        System.out.println("markSupported: " + bis.markSupported());

        System.out.print("first read:  ");
        for (int i = 0; i < 3; i++) System.out.print((char) bis.read());
        System.out.println();

        bis.mark(4);

        System.out.print("after mark:  ");
        for (int i = 0; i < 4; i++) System.out.print((char) bis.read());
        System.out.println();

        bis.reset();
        System.out.print("after reset: ");
        for (int i = 0; i < 4; i++) System.out.print((char) bis.read());
        System.out.println();
    }

    // =========================================================================
    // 6. Performance illustration — buffered vs unbuffered
    // =========================================================================

    static void performanceComparisonDemo() throws IOException {
        System.out.println("\n--- 6. Performance: buffered vs unbuffered write ---");

        Path out = DATA_DIR.resolve("buffered_perf.txt");
        int writes = 50_000;
        byte val = 'x';

        long start = System.nanoTime();
        try (FileOutputStream fos = new FileOutputStream(out.toFile())) {
            for (int i = 0; i < writes; i++) fos.write(val);
        }
        long unbufferedMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(out.toFile()))) {
            for (int i = 0; i < writes; i++) bos.write(val);
        }
        long bufferedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("unbuffered: %3d ms%n", unbufferedMs);
        System.out.printf("buffered:   %3d ms%n", bufferedMs);
        System.out.println("buffered is typically 10x–100x faster for small writes");

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== Buffered IO Demo ===");
        bufferedReaderDemo();
        filesNewBufferedReaderDemo();
        bufferedWriterDemo();
        bufferedByteStreamDemo();
        markResetDemo();
        performanceComparisonDemo();
    }
}
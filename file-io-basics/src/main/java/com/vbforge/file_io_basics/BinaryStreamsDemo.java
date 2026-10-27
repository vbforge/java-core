package com.vbforge.file_io_basics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * BinaryStreamsDemo — annotated reference for byte streams: {@link InputStream} and
 * {@link OutputStream} and their most useful wrappers.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Rule of thumb: <strong>bytes</strong> (images, archives, any file you do not own the format of)
 * use {@code *Stream}; <strong>text</strong> uses {@code Reader}/{@code Writer} (see {@code ReaderWriterDemo}).
 *
 * <p>Topics:
 * <ol>
 *   <li>FileInputStream / FileOutputStream — {@code read()} returns an {@code int}</li>
 *   <li>Buffering — why {@code BufferedOutputStream} exists (counted, not timed)</li>
 *   <li>DataInputStream / DataOutputStream — primitives in a fixed binary layout</li>
 *   <li>In-memory streams — byte arrays as files</li>
 *   <li>Copying — the buffer loop and what {@code read(byte[])} really returns</li>
 * </ol>
 */
public class BinaryStreamsDemo {

    public static void main(String[] args) throws IOException {
        File workDir = Files.createTempDirectory("file-io-basics-").toFile();
        try {
            fileStreamsDemo(workDir);
            System.out.println("---");
            bufferingDemo(workDir);
            System.out.println("---");
            dataStreamsDemo(workDir);
            System.out.println("---");
            inMemoryStreamsDemo();
            System.out.println("---");
            copyDemo(workDir);
        } finally {
            PathFilesDemo.deleteTree(workDir.toPath());
        }
    }

    // ── 1. File streams ───────────────────────────────────────────────────────

    /**
     * {@link FileOutputStream} writes bytes to a file, {@link FileInputStream} reads them back.
     *
     * <p>WHY this matters: every other stream and reader is built on these primitives.
     * The key facts are in the return types.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code write(int b)} takes an {@code int} but writes only its <em>low 8 bits</em>.</li>
     *   <li>{@code read()} returns an {@code int} in the range {@code 0..255}, or {@code -1} at the
     *       end. It returns an int precisely so that the end marker ({@code -1}) cannot be confused
     *       with a real byte: the byte {@code (byte) 0xFF} is the {@code int} 255, not -1.</li>
     *   <li>A Java {@code byte} is <em>signed</em> ({@code -128..127}); {@code (byte) 200} is {@code -56}.
     *       Use {@code b & 0xFF} to get back the unsigned value.</li>
     *   <li>{@code new FileOutputStream(file)} truncates; {@code new FileOutputStream(file, true)} appends.</li>
     * </ul>
     */
    static void fileStreamsDemo(File workDir) throws IOException {
        System.out.println("=== File streams ===");

        File file = new File(workDir, "bytes.bin");

        try (OutputStream out = new FileOutputStream(file)) {
            out.write(65);                                  // 'A'
            out.write(new byte[]{66, 67});                  // 'B', 'C'
            out.write(200);                                 // a value above 127
            out.write(0x1FF);                               // only the low 8 bits (0xFF = 255) are written
        }
        System.out.println("length         : " + file.length());                   // 5

        try (InputStream in = new FileInputStream(file)) {
            int b;
            StringBuilder values = new StringBuilder();
            while ((b = in.read()) != -1) {                 // -1 = end of stream
                values.append(b).append(' ');
            }
            System.out.println("read() values  : " + values.toString().trim());     // 65 66 67 200 255
        }

        // Signed byte vs unsigned int
        byte[] raw = Files.readAllBytes(file.toPath());
        System.out.println("(byte) 200     : " + raw[3]);                           // -56
        System.out.println("raw[3] & 0xFF  : " + (raw[3] & 0xFF));                  // 200
        System.out.println("raw[4] == -1   : " + (raw[4] == -1));                   // true — as a SIGNED byte 0xFF is -1 ...
        // ... but in.read() delivered it as 255, so the end-of-stream marker was never confused with it.

        // Append mode
        try (OutputStream out = new FileOutputStream(file, true)) {
            out.write(68);                                  // 'D' after the existing 5 bytes
        }
        System.out.println("after append   : " + file.length());                    // 6
    }

    // ── 2. Buffering ──────────────────────────────────────────────────────────

    /**
     * {@link BufferedOutputStream} collects bytes in an 8 KB array and hands them to the
     * underlying stream in one big call. Each call to an unbuffered {@code FileOutputStream}
     * is a call into the operating system — the expensive part.
     *
     * <p>WHY this matters: writing 1000 bytes one at a time means 1000 system calls unbuffered, versus one
     * buffered. The demo <em>counts</em> the calls reaching the target with a counting stream
     * instead of timing them, so the result is exact and repeatable.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>Buffered data is not visible in the file until {@code flush()} or {@code close()}.
     *       A program that exits without closing loses the tail.</li>
     *   <li>{@code close()} flushes for you — one more reason to use try-with-resources.</li>
     *   <li>Do not buffer twice (a {@code BufferedOutputStream} around a {@code BufferedOutputStream}): no gain.</li>
     * </ul>
     */
    static void bufferingDemo(File workDir) throws IOException {
        System.out.println("=== Buffering ===");

        // 1000 single-byte writes, unbuffered
        CountingOutputStream direct = new CountingOutputStream();
        for (int i = 0; i < 1000; i++) {
            direct.write(i);
        }
        System.out.println("unbuffered calls: " + direct.writeCalls);               // 1000

        // The same 1000 writes through a buffer
        CountingOutputStream target = new CountingOutputStream();
        try (BufferedOutputStream buffered = new BufferedOutputStream(target)) {
            for (int i = 0; i < 1000; i++) {
                buffered.write(i);
            }
            System.out.println("before flush    : " + target.writeCalls);           // 0 — everything is still in the buffer
        }                                                                           // close() flushes
        System.out.println("after close     : " + target.writeCalls);               // 1 — one batch of 1000 bytes
        System.out.println("bytes delivered : " + target.byteCount);                // 1000

        // Visibility on a real file
        File file = new File(workDir, "buffered.bin");
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(new byte[]{1, 2, 3, 4, 5});
            System.out.println("length, no flush: " + file.length());               // 0 — not on disk yet
            out.flush();
            System.out.println("length, flushed : " + file.length());               // 5
        }
    }

    /** An {@link OutputStream} that counts how often it is called and how many bytes it receives. */
    private static class CountingOutputStream extends OutputStream {
        int writeCalls;
        int byteCount;

        @Override
        public void write(int b) {
            writeCalls++;
            byteCount++;
        }

        @Override
        public void write(byte[] b, int off, int len) {
            writeCalls++;
            byteCount += len;
        }
    }

    // ── 3. Data streams ───────────────────────────────────────────────────────

    /**
     * {@link DataOutputStream} writes Java primitives and strings in a fixed, portable binary
     * layout; {@link DataInputStream} reads them back.
     *
     * <ul>
     *   <li>{@code writeInt} → 4 bytes, big-endian; {@code writeLong} → 8; {@code writeDouble} → 8;
     *       {@code writeBoolean} → 1</li>
     *   <li>{@code writeUTF(s)} → a 2-byte length followed by the text (a modified UTF-8)</li>
     * </ul>
     *
     * <p>WHY this matters: it is the simplest way to store structured binary data you read back
     * with the same program, and the model for how network protocols and file formats are laid out.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>The format has <em>no field names or types</em>. You must read in exactly the order and
     *       with exactly the methods you wrote; anything else yields nonsense without an error.</li>
     *   <li>Reading past the end throws {@link EOFException} — the normal way to detect the end of
     *       a data file (unlike {@code read()}, which returns -1).</li>
     * </ul>
     */
    static void dataStreamsDemo(File workDir) throws IOException {
        System.out.println("=== Data streams ===");

        File file = new File(workDir, "data.bin");

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeInt(258);                               // 4 bytes: 0 0 1 2
            out.writeUTF("héllo");                           // 2 length bytes + 6 text bytes
            out.writeBoolean(true);                          // 1 byte
            out.writeDouble(1.5);                            // 8 bytes
        }
        System.out.println("file length    : " + file.length());                    // 21 = 4 + (2+6) + 1 + 8

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            System.out.println("readInt        : " + in.readInt());                  // 258
            System.out.println("readUTF        : " + in.readUTF());                  // héllo
            System.out.println("readBoolean    : " + in.readBoolean());              // true
            System.out.println("readDouble     : " + in.readDouble());               // 1.5
            try {
                in.readInt();                                // nothing left
            } catch (EOFException e) {
                System.out.println("past the end   : EOFException");
            }
        }

        // The layout of an int, byte by byte (big-endian = most significant byte first)
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeInt(258);
        }
        System.out.println("258 as bytes   : " + Arrays.toString(bytes.toByteArray())); // [0, 0, 1, 2]

        // Reading in the wrong order does not fail — it just returns something else
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            System.out.println("two shorts     : " + in.readShort() + ", " + in.readShort()); // 0, 258
        }
    }

    // ── 4. In-memory streams ──────────────────────────────────────────────────

    /**
     * {@link ByteArrayOutputStream} and {@link ByteArrayInputStream} are streams backed by a
     * byte array instead of a file.
     *
     * <p>WHY this matters: any code written against {@code InputStream}/{@code OutputStream} can be
     * tested without touching the disk — hand it a {@code ByteArrayInputStream}, then inspect what a
     * {@code ByteArrayOutputStream} collected. They are also handy to build a small payload in memory.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code close()} on these two classes does nothing; the data stays available.</li>
     *   <li>{@code toString()} on a ByteArrayOutputStream uses the default charset — use
     *       {@code toString("UTF-8")} (or decode {@code toByteArray()} yourself).</li>
     * </ul>
     */
    static void inMemoryStreamsDemo() throws IOException {
        System.out.println("=== In-memory streams ===");

        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        sink.write("héllo".getBytes(StandardCharsets.UTF_8));
        sink.write('!');
        System.out.println("size           : " + sink.size());                       // 7
        System.out.println("decoded        : " + sink.toString("UTF-8"));            // héllo!

        InputStream source = new ByteArrayInputStream(new byte[]{10, 20, 30});
        int b;
        int sum = 0;
        while ((b = source.read()) != -1) {
            sum += b;
        }
        System.out.println("sum            : " + sum);                               // 60
        System.out.println("available      : " + source.available());                // 0

        sink.close();                                                                // no-op ...
        System.out.println("after close    : " + sink.size());                       // ... still 7
    }

    // ── 5. Copying ────────────────────────────────────────────────────────────

    /**
     * Copying a stream is the canonical read loop: fill a byte array, write exactly the bytes that
     * were read, repeat until {@code read} returns {@code -1}.
     *
     * <p>WHY this matters: it copies a file of any size using O(buffer) memory — 8 KB — instead of
     * loading it whole. The same loop copies network sockets, uploads and zip entries.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code read(byte[])} returns how many bytes it <em>actually</em> read, which may be less than
     *       the array length (at the end of the file, or when a network delivers part of the data).
     *       Always write {@code out.write(buffer, 0, n)}, never the whole array.</li>
     *   <li>It returns {@code -1} (not 0) at the end of the stream.</li>
     *   <li>{@code InputStream.readAllBytes()} and {@code transferTo()} exist since Java 9; on Java 8
     *       you write this loop or use {@code Files.copy}.</li>
     * </ul>
     */
    static void copyDemo(File workDir) throws IOException {
        System.out.println("=== Copying ===");

        File source = new File(workDir, "source.bin");
        File copy = new File(workDir, "copy.bin");
        byte[] content = new byte[10];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i * 30);                        // includes values above 127 (negative bytes)
        }
        Files.write(source.toPath(), content);

        // A deliberately tiny buffer so the partial last read is visible
        byte[] buffer = new byte[4];
        StringBuilder reads = new StringBuilder();
        long total = 0;
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(copy)) {
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);                         // only the n valid bytes
                reads.append(n).append(' ');
                total += n;
            }
        }
        System.out.println("reads          : " + reads.toString().trim());           // 4 4 2
        System.out.println("total          : " + total);                             // 10
        System.out.println("identical      : " + Arrays.equals(content, Files.readAllBytes(copy.toPath()))); // true

        // GOTCHA — writing the whole buffer repeats stale bytes from the previous read
        File broken = new File(workDir, "broken.bin");
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(broken)) {
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer);                               // BUG: ignores n
            }
        }
        System.out.println("buggy length   : " + broken.length());                    // 12 — the last read of 2 bytes wrote 4
        System.out.println("buggy identical: " + Arrays.equals(content, Files.readAllBytes(broken.toPath()))); // false
    }
}

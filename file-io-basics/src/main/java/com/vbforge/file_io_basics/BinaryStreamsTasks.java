package com.vbforge.file_io_basics;

import java.io.File;
import java.io.IOException;

/**
 * BinaryStreamsTasks — implement every method below to make {@code BinaryStreamsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code BinaryStreamsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=BinaryStreamsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Use the byte streams from the demo ({@code FileInputStream}, {@code FileOutputStream},
 * {@code DataInputStream}, ...). Do <em>not</em> use {@code java.nio.file.Files} — and always
 * close what you open.
 */
public class BinaryStreamsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Copies the bytes of {@code source} to {@code target} (created or overwritten) and
     * returns the number of bytes copied. Works for files of any size, using a fixed-size buffer.
     *
     * <p>Examples:
     * <pre>
     *   source = 10 bytes                    → target has the same 10 bytes, returns 10
     *   source = empty file                  → target is empty, returns 0
     *   source = 100 000 bytes (many buffers) → identical copy, returns 100000
     *   source contains bytes above 127       → copied unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The loop is {@code read(buffer)} → {@code write(buffer, 0, n)} until {@code read} returns -1.</li>
     *   <li>Write only the {@code n} bytes that were read — see "Copying" in the demo.</li>
     * </ul>
     *
     * @param source the file to read
     * @param target the file to write
     * @return the number of bytes copied
     * @throws IOException if reading or writing fails
     */
    public long copy(File source, File target) throws IOException {
        throw new UnsupportedOperationException("TODO: implement copy");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Reads the whole file into a byte array using a stream (no {@code Files.readAllBytes}).
     *
     * <p>Examples:
     * <pre>
     *   file with bytes [1, 2, 3]            → [1, 2, 3]
     *   empty file                           → []   (an empty array, not null)
     *   file with bytes [0, -1, 127, -128]    → [0, -1, 127, -128]
     *   file of 20 000 bytes                  → array of length 20000, same content
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You do not know the length in advance (well, {@code file.length()} does, but a
     *       stream-based solution should not depend on it) — collect the chunks you read in a
     *       {@link java.io.ByteArrayOutputStream}.</li>
     * </ul>
     *
     * @param file the file to read
     * @return all bytes of the file
     * @throws IOException if the file cannot be read
     */
    public byte[] readAllBytes(File file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement readAllBytes");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Writes every value of {@code values} to {@code file} as a 4-byte big-endian
     * {@code int}, one after the other, with no header or separator. The file is created or
     * overwritten. An empty array produces an empty file.
     *
     * <p>Examples:
     * <pre>
     *   writeInts(f, [1, 2, 3])        → f has 12 bytes: 0 0 0 1  0 0 0 2  0 0 0 3
     *   writeInts(f, [258])            → f has 4 bytes:  0 0 1 2
     *   writeInts(f, [-1])             → f has 4 bytes:  -1 -1 -1 -1
     *   writeInts(f, [])               → f is empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code DataOutputStream.writeInt} does the byte layout for you.</li>
     *   <li>Wrap the file stream in a buffered stream — there may be many values.</li>
     * </ul>
     *
     * @param file   the file to write
     * @param values the numbers to store
     * @throws IOException if the file cannot be written
     */
    public void writeInts(File file, int[] values) throws IOException {
        throw new UnsupportedOperationException("TODO: implement writeInts");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Reads back a file written by {@link #writeInts(File, int[])}: every 4 bytes are one
     * big-endian {@code int}. The file length is always a multiple of 4. An empty file gives an
     * empty array.
     *
     * <p>Examples:
     * <pre>
     *   file 0 0 0 1  0 0 0 2  0 0 0 3     → [1, 2, 3]
     *   file 0 0 1 2                       → [258]
     *   file -1 -1 -1 -1                    → [-1]
     *   empty file                          → []
     *   writeInts(f, x) then readInts(f)    → x again, for any int[] x
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code DataInputStream.readInt} throws {@link java.io.EOFException} at the end of
     *       the data — either use that, or compute how many ints to read from {@code file.length() / 4}.</li>
     * </ul>
     *
     * @param file a file made of 4-byte ints
     * @return the numbers in the file
     * @throws IOException if the file cannot be read
     */
    public int[] readInts(File file) throws IOException {
        throw new UnsupportedOperationException("TODO: implement readInts");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Counts how many times the byte {@code target} occurs in {@code file}. The comparison
     * is on the byte value, including values above 127 (negative as Java bytes).
     *
     * <p>Examples:
     * <pre>
     *   file "banana" (bytes), target 'a'          → 3
     *   file "banana",         target 'z'          → 0
     *   file bytes [-1, 5, -1, -1], target (byte) -1  → 3   (0xFF — must not be mistaken for end-of-stream)
     *   file bytes [0, 0, 1], target (byte) 0         → 2
     *   empty file, target any                         → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code InputStream.read()} returns an {@code int} 0..255 — the byte {@code -1} arrives as 255.</li>
     *   <li>Convert before comparing: cast the int to a byte, or mask the byte with {@code & 0xFF}.</li>
     *   <li>A buffered stream makes the one-byte-at-a-time loop affordable.</li>
     * </ul>
     *
     * @param file   the file to scan
     * @param target the byte to count
     * @return number of occurrences
     * @throws IOException if the file cannot be read
     */
    public int countByte(File file, byte target) throws IOException {
        throw new UnsupportedOperationException("TODO: implement countByte");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Tells whether two files have exactly the same content, byte for byte.
     * Files of different length are not equal, and two empty files are equal.
     * Neither file is modified. The method must not load either file completely into memory.
     *
     * <p>Examples:
     * <pre>
     *   [1,2,3] vs [1,2,3]          → true
     *   [1,2,3] vs [1,2,4]          → false
     *   [1,2,3] vs [1,2,3,4]        → false   (one is a prefix of the other)
     *   empty vs empty              → true
     *   a file vs itself            → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Comparing the lengths first is a cheap early exit.</li>
     *   <li>Read both files through buffered streams, one byte at a time, and stop at the first difference.</li>
     * </ul>
     *
     * @param a first file
     * @param b second file
     * @return {@code true} if both files contain the same bytes
     * @throws IOException if either file cannot be read
     */
    public boolean sameContent(File a, File b) throws IOException {
        throw new UnsupportedOperationException("TODO: implement sameContent");
    }
}

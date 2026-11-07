package com.vbforge.practice.practice_8_byte_streams;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates byte streams — the lowest-level I/O abstraction in java.io.
 *
 * Byte streams operate on RAW BYTES (int 0–255).
 * They are the foundation for ALL other I/O in Java.
 *
 * Core abstract classes:
 *   InputStream   — source of bytes  (read())
 *   OutputStream  — sink of bytes    (write())
 *
 * Key concrete implementations:
 *
 *   FileInputStream  / FileOutputStream   — read/write from/to a file
 *   ByteArrayInputStream / ByteArrayOutputStream — in-memory byte buffer
 *   DataInputStream  / DataOutputStream   — primitives (int, long, double…) over a stream
 *   ObjectInputStream / ObjectOutputStream — serialization (see serialization module)
 *
 * Wrapping pattern (Decorator):
 *   Java IO streams are composable via the Decorator pattern.
 *   Each stream wraps another, adding a behavior:
 *
 *     new BufferedInputStream(new FileInputStream("file.bin"))
 *          ↑ adds buffering   ↑ provides raw bytes from file
 *
 * ALWAYS close streams — they hold OS file descriptors.
 * Use try-with-resources to guarantee closing even on exceptions.
 *
 * When to use byte streams vs character streams (Reader/Writer):
 *   - Byte streams : binary data (images, audio, zip, class files, any binary protocol)
 *   - Char streams : text data  (source files, config, human-readable logs)
 *   Do NOT use byte streams for text — they don't handle charset conversion.
 */
public class StreamsDemo {

    static final Path DATA_DIR = Path.of("file-io-basics/data_files");

    // =========================================================================
    // 1. InputStream / OutputStream fundamentals
    // =========================================================================

    static void inputOutputStreamFundamentals() throws IOException {
        System.out.println("\n--- 1. InputStream / OutputStream fundamentals ---");

        Path out = DATA_DIR.resolve("output_streams_raw.txt");

        try (OutputStream os = new FileOutputStream(out.toFile())) {
            String msg = "Hello from OutputStream\n";
            for (byte b : msg.getBytes()) {
                os.write(b);
            }
            os.write("second line\n".getBytes());
            os.flush();
        }

        System.out.print("read byte-by-byte: ");
        try (InputStream is = new FileInputStream(out.toFile())) {
            int byteVal;
            while ((byteVal = is.read()) != -1) {
                System.out.print((char) byteVal);
            }
        }

        System.out.print("read into buffer:  ");
        try (InputStream is = new FileInputStream(out.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                System.out.print(new String(buffer, 0, bytesRead));
            }
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 2. ByteArrayInputStream / ByteArrayOutputStream — in-memory streams
    // =========================================================================

    static void byteArrayStreamDemo() throws IOException {
        System.out.println("\n--- 2. ByteArray streams (in-memory) ---");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("in-memory data".getBytes());
        baos.write(0x0A);

        byte[] bytes = baos.toByteArray();
        System.out.println("captured bytes:  " + bytes.length);
        System.out.println("as string:       " + baos.toString().trim());

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        System.out.print("re-read:         ");
        int b;
        while ((b = bais.read()) != -1) {
            if (b != '\n') System.out.print((char) b);
        }
        System.out.println();

        bais.reset();
        System.out.println("after reset, available: " + bais.available());
    }

    // =========================================================================
    // 3. DataInputStream / DataOutputStream — typed primitives over bytes
    // =========================================================================

    static void dataStreamDemo() throws IOException {
        System.out.println("\n--- 3. DataInputStream / DataOutputStream ---");

        Path out = DATA_DIR.resolve("output_streams_data.bin");

        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(out.toFile()))) {

            dos.writeInt(42);
            dos.writeLong(9_876_543_210L);
            dos.writeDouble(3.14159);
            dos.writeBoolean(true);
            dos.writeUTF("Hello DataStream");
            System.out.println("written: int=42, long=9876543210, double=3.14159, bool=true, str=Hello DataStream");
        }

        System.out.println("file size (bytes): " + Files.size(out));

        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(out.toFile()))) {

            int     i   = dis.readInt();
            long    l   = dis.readLong();
            double  d   = dis.readDouble();
            boolean b   = dis.readBoolean();
            String  s   = dis.readUTF();

            System.out.printf("read back: int=%d, long=%d, double=%.5f, bool=%b, str=%s%n",
                i, l, d, b, s);
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 4. skip() and available()
    // =========================================================================

    static void skipAndAvailableDemo() throws IOException {
        System.out.println("\n--- 4. skip() and available() ---");

        byte[] data = "ABCDEFGHIJ".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);

        System.out.println("available() before read: " + bais.available());

        bais.skip(3);
        System.out.println("after skip(3) — next char: " + (char) bais.read());

        System.out.println("available() remaining:   " + bais.available());
    }

    // =========================================================================
    // 5. Copying streams — the transfer pattern
    // =========================================================================

    static void transferDemo() throws IOException {
        System.out.println("\n--- 5. Stream transfer (copy) ---");

        Path src  = DATA_DIR.resolve("sample.txt");
        Path dest = DATA_DIR.resolve("output_streams_transfer.txt");

        try (InputStream is  = new FileInputStream(src.toFile());
             OutputStream os = new FileOutputStream(dest.toFile())) {

            byte[] buf = new byte[4096];
            int n;
            long total = 0;
            while ((n = is.read(buf)) != -1) {
                os.write(buf, 0, n);
                total += n;
            }
            System.out.println("manual transfer: " + total + " bytes");
        }

        try (InputStream is  = new FileInputStream(src.toFile());
             OutputStream os = new FileOutputStream(dest.toFile())) {

            long transferred = is.transferTo(os);
            System.out.println("transferTo():    " + transferred + " bytes");
        }

        System.out.println("files equal: " + Arrays.equals(
            Files.readAllBytes(src), Files.readAllBytes(dest)));

        Files.deleteIfExists(dest);
    }

    static final class Arrays {
        static boolean equals(byte[] a, byte[] b) { return java.util.Arrays.equals(a, b); }
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== Streams Demo (byte streams) ===");
        inputOutputStreamFundamentals();
        byteArrayStreamDemo();
        dataStreamDemo();
        skipAndAvailableDemo();
        transferDemo();
    }
}
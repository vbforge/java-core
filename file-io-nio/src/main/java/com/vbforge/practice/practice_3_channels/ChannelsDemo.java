package com.vbforge.practice.practice_3_channels;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Demonstrates NIO Channels — the central abstraction for I/O in java.nio.
 *
 * What is a Channel?
 *   A Channel is a connection to an I/O source or destination (file, socket, pipe).
 *   Unlike a stream, a channel is BIDIRECTIONAL — the same channel can read AND write.
 *   Channels always work in conjunction with Buffers: data flows Channel ↔ Buffer.
 *
 * Stream vs Channel mental model:
 *   Stream (java.io)  — unidirectional byte hose; blocking; OS-level syscall per read/write
 *   Channel (java.nio) — bidirectional connection; can be non-blocking; scatter/gather;
 *                        memory-mapped; transferTo/From zero-copy
 *
 * Key Channel types:
 *   FileChannel       — file I/O with locking, memory-mapping, and direct transfer
 *   SocketChannel     — TCP client connection (non-blocking)
 *   ServerSocketChannel — TCP server (non-blocking)
 *   DatagramChannel   — UDP
 *   Pipe.SinkChannel / SourceChannel — inter-thread communication
 *
 * This module focuses on FileChannel — the most common Channel in non-network code.
 *
 * FileChannel capabilities beyond streams:
 *   - Random access via position() and read/write at specific offsets
 *   - File locking (FileLock) — advisory lock within or across JVMs
 *   - transferTo / transferFrom — OS zero-copy (sendfile syscall on Linux)
 *   - Memory-mapped files (MappedByteBuffer) — file appears as a byte[]
 *   - Scatter/gather I/O — read into / write from multiple Buffers atomically
 */
public class ChannelsDemo {

    static final Path DATA_DIR =
        Path.of("file-io-nio/data_files");

    // =========================================================================
    // 1. Opening a FileChannel — three ways
    // =========================================================================

    /**
     * Three ways to get a FileChannel:
     *
     *   A) FileChannel.open(path, options...)      — NIO.2, preferred
     *   B) new FileInputStream(file).getChannel()  — legacy bridge
     *   C) new RandomAccessFile(file, "rw").getChannel() — legacy bridge
     *
     * Always use try-with-resources — FileChannel implements Closeable.
     */
    static void openingChannelDemo() throws IOException {
        System.out.println("\n--- 1. Opening a FileChannel ---");

        Path sample = DATA_DIR.resolve("sample.txt");

        // A) NIO.2 — cleanest
        try (FileChannel ch = FileChannel.open(sample, StandardOpenOption.READ)) {
            System.out.println("A) FileChannel.open — size: " + ch.size() + " bytes");
        }

        // B) from FileInputStream
        try (FileInputStream fis = new FileInputStream(sample.toFile());
             FileChannel ch = fis.getChannel()) {
            System.out.println("B) from FileInputStream — size: " + ch.size());
        }

        // C) read/write via RandomAccessFile
        Path out = DATA_DIR.resolve("output_channels_raf.txt");
        try (RandomAccessFile raf = new RandomAccessFile(out.toFile(), "rw");
             FileChannel ch = raf.getChannel()) {
            ByteBuffer buf = ByteBuffer.wrap("via RandomAccessFile\n".getBytes());
            ch.write(buf);
            System.out.println("C) via RandomAccessFile — written " + out.getFileName());
        }
        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 2. Basic read and write via Buffer
    // =========================================================================

    /**
     * Channel I/O always goes through a Buffer:
     *
     *   Write:  fill Buffer → flip() → channel.write(buffer)
     *   Read:   channel.read(buffer) → flip() → drain Buffer
     *
     * flip() switches the buffer from write mode (position = write head)
     * to read mode (limit = position, position = 0).
     * See the buffers module for full Buffer mechanics.
     */
    static void basicReadWriteDemo() throws IOException {
        System.out.println("\n--- 2. Basic read / write ---");

        Path out = DATA_DIR.resolve("output_channels_rw.txt");

        // WRITE
        try (FileChannel ch = FileChannel.open(out,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            ByteBuffer buf = ByteBuffer.allocate(256);

            // Fill buffer
            buf.put("Hello, NIO Channel!\n".getBytes(StandardCharsets.UTF_8));
            buf.put("Second line here.\n".getBytes(StandardCharsets.UTF_8));

            // MUST flip before writing: switches to read mode
            buf.flip();

            int written = ch.write(buf);
            System.out.println("written: " + written + " bytes");
        }

        // READ
        try (FileChannel ch = FileChannel.open(out, StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocate((int) ch.size());

            // Fill the buffer from the channel
            int bytesRead = ch.read(buf);

            // Flip to read mode, then decode
            buf.flip();
            String content = StandardCharsets.UTF_8.decode(buf).toString();
            System.out.print("read back: " + content);
            System.out.println("bytesRead: " + bytesRead);
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 3. Random access — position() and read/write at offset
    // =========================================================================

    /**
     * FileChannel maintains a position (file pointer).
     * position()     — get current position
     * position(n)    — seek to offset n
     * read(buf, pos) — read without changing the channel's position
     * write(buf, pos)— write without changing the channel's position
     *
     * This is the key advantage over streams — no need to skip forward sequentially.
     */
    static void randomAccessDemo() throws IOException {
        System.out.println("\n--- 3. Random access ---");

        Path out = DATA_DIR.resolve("output_channels_ra.txt");

        // Write a fixed-width record format: 4 records, 20 bytes each
        String[] records = {"RECORD_ZERO_____", "RECORD_ONE______",
                            "RECORD_TWO______", "RECORD_THREE____"};

        try (FileChannel ch = FileChannel.open(out,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            for (String rec : records) {
                // Pad to exactly 20 bytes
                String padded = String.format("%-20s", rec).substring(0, 20);
                ch.write(ByteBuffer.wrap(padded.getBytes(StandardCharsets.UTF_8)));
            }
        }

        System.out.println("file size: " + Files.size(out) + " bytes (4 × 20)");

        // Read record 2 directly (offset 40)
        try (FileChannel ch = FileChannel.open(out, StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocate(20);
            int n = ch.read(buf, 40);    // absolute read — position unchanged
            buf.flip();
            System.out.println("record at offset 40: '"
                + StandardCharsets.UTF_8.decode(buf).toString().trim() + "'");
            System.out.println("channel position after absolute read: " + ch.position()); // still 0

            // Sequential seek and read
            ch.position(20);             // seek to record 1
            buf.clear();
            ch.read(buf);
            buf.flip();
            System.out.println("record at position 20: '"
                + StandardCharsets.UTF_8.decode(buf).toString().trim() + "'");
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 4. transferTo / transferFrom — zero-copy file copy
    // =========================================================================

    /**
     * transferTo(pos, count, dest) copies bytes from this channel to dest
     * using the OS sendfile / copy_file_range syscall where available.
     *
     * "Zero-copy" means the data moves directly from the OS page cache
     * to the destination without being copied into JVM heap memory.
     * This is significantly faster for large files than buffer-based copy.
     *
     * Common use: high-performance file copy, HTTP file serving.
     */
    static void transferDemo() throws IOException {
        System.out.println("\n--- 4. transferTo / transferFrom (zero-copy) ---");

        Path src  = DATA_DIR.resolve("sample.txt");
        Path dest = DATA_DIR.resolve("output_channels_transfer.txt");

        try (FileChannel srcCh  = FileChannel.open(src,  StandardOpenOption.READ);
             FileChannel destCh = FileChannel.open(dest,
                 StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                 StandardOpenOption.TRUNCATE_EXISTING)) {

            long size      = srcCh.size();
            long position  = 0;
            long remaining = size;

            // transferTo may not transfer everything in one call — loop until done
            while (remaining > 0) {
                long transferred = srcCh.transferTo(position, remaining, destCh);
                position  += transferred;
                remaining -= transferred;
            }

            System.out.println("transferred: " + size + " bytes via zero-copy");
        }

        // Verify
        byte[] srcBytes  = Files.readAllBytes(src);
        byte[] destBytes = Files.readAllBytes(dest);
        System.out.println("files identical: " + java.util.Arrays.equals(srcBytes, destBytes));

        Files.deleteIfExists(dest);
    }

    // =========================================================================
    // 5. Scatter / Gather I/O
    // =========================================================================

    /**
     * Scatter read:  channel.read(ByteBuffer[]) — fills each buffer in sequence.
     * Gather write:  channel.write(ByteBuffer[]) — drains each buffer in sequence.
     *
     * The operation is atomic at the OS level — the kernel fills/drains all
     * buffers in one syscall (readv / writev on Unix).
     *
     * Use case: fixed-format binary protocols where the payload is divided
     * into a header, body, and trailer stored in separate buffers.
     */
    static void scatterGatherDemo() throws IOException {
        System.out.println("\n--- 5. Scatter / Gather I/O ---");

        Path out = DATA_DIR.resolve("output_channels_sg.bin");

        // GATHER write — three logical sections in separate buffers
        ByteBuffer header  = ByteBuffer.wrap("HEADER__".getBytes(StandardCharsets.UTF_8)); // 8
        ByteBuffer body    = ByteBuffer.wrap("BODY_PAYLOAD____".getBytes());                // 16
        ByteBuffer trailer = ByteBuffer.wrap("TRAILER_".getBytes(StandardCharsets.UTF_8)); // 8

        try (FileChannel ch = FileChannel.open(out,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            long written = ch.write(new ByteBuffer[]{header, body, trailer});
            System.out.println("gather write: " + written + " bytes");
        }

        // SCATTER read — distribute into matching sections
        ByteBuffer rHeader  = ByteBuffer.allocate(8);
        ByteBuffer rBody    = ByteBuffer.allocate(16);
        ByteBuffer rTrailer = ByteBuffer.allocate(8);

        try (FileChannel ch = FileChannel.open(out, StandardOpenOption.READ)) {
            ch.read(new ByteBuffer[]{rHeader, rBody, rTrailer});
        }

        rHeader.flip();  rBody.flip();  rTrailer.flip();
        System.out.println("scatter header:  " + StandardCharsets.UTF_8.decode(rHeader));
        System.out.println("scatter body:    " + StandardCharsets.UTF_8.decode(rBody));
        System.out.println("scatter trailer: " + StandardCharsets.UTF_8.decode(rTrailer));

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 6. Memory-mapped files
    // =========================================================================

    /**
     * MappedByteBuffer maps a region of a file directly into process memory.
     * The OS manages paging — reads/writes go through the page cache.
     *
     * MapMode.READ_ONLY  — read-only mapping (changes to file not reflected after map)
     * MapMode.READ_WRITE — read+write; changes write through to the file
     * MapMode.PRIVATE    — copy-on-write; changes local, not persisted
     *
     * Benefits:
     *   - Fastest possible file access for random reads (no syscall per access).
     *   - Shared memory between processes mapping the same file.
     *   - Ideal for large read-mostly files (databases, indexes).
     *
     * Caveats:
     *   - Memory is pinned until the buffer is GC'd (can cause resource leaks).
     *   - MappedByteBuffer.force() flushes dirty pages to disk.
     *   - Unmapping is not possible in Java before GC (known JDK limitation).
     */
    static void memoryMappedDemo() throws IOException {
        System.out.println("\n--- 6. Memory-mapped files ---");

        Path out = DATA_DIR.resolve("output_channels_mmap.bin");

        // Write initial content via mapping
        try (FileChannel ch = FileChannel.open(out,
                StandardOpenOption.CREATE, StandardOpenOption.READ,
                StandardOpenOption.WRITE)) {

            // Map a 64-byte region
            MappedByteBuffer mmap = ch.map(FileChannel.MapMode.READ_WRITE, 0, 64);

            // Write directly to mapped memory
            mmap.put("MemMapped Content Here  ".getBytes(StandardCharsets.UTF_8));
            mmap.putInt(42);        // write an int at current position
            mmap.putDouble(3.14);   // write a double

            mmap.force();  // flush dirty pages to disk
            System.out.println("mapped write complete — position: " + mmap.position());
        }

        // Read back via mapping (READ_ONLY)
        try (FileChannel ch = FileChannel.open(out, StandardOpenOption.READ)) {
            MappedByteBuffer mmap = ch.map(FileChannel.MapMode.READ_ONLY, 0, 64);

            byte[] textBytes = new byte[24];
            mmap.get(textBytes);
            System.out.println("mapped read text: '" +
                new String(textBytes, StandardCharsets.UTF_8).trim() + "'");
            System.out.println("mapped read int:  " + mmap.getInt());
            System.out.printf("mapped read dbl:  %.2f%n", mmap.getDouble());
        }

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException {
        System.out.println("=== Channels Demo ===");
        openingChannelDemo();
        basicReadWriteDemo();
        randomAccessDemo();
        transferDemo();
        scatterGatherDemo();
        memoryMappedDemo();
    }
}
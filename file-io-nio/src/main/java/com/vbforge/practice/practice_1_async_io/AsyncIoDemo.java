package com.vbforge.practice.practice_1_async_io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.*;

/**
 * Demonstrates Asynchronous I/O — performing file operations without blocking
 * the calling thread.
 *
 * Background — why async I/O?
 *   Synchronous I/O blocks the thread while the OS performs the read/write.
 *   For many concurrent I/O operations (e.g., web server handling 10k requests),
 *   blocking means needing thousands of threads — expensive in memory and CPU.
 *   Async I/O lets a small thread pool initiate many operations and receive
 *   callbacks when each one completes.
 *
 * Java NIO async classes (java.nio.channels — Java 7+):
 *   AsynchronousFileChannel    — async file read/write
 *   AsynchronousSocketChannel  — async TCP client
 *   AsynchronousServerSocketChannel — async TCP server
 *
 * Two completion styles:
 *
 *   1. Future style:
 *      Future<Integer> f = channel.read(buf, pos);
 *      // ... do other work ...
 *      int n = f.get();  // block until done (or timeout)
 *      Useful when: simple sequencing; you have other work to do while waiting.
 *
 *   2. CompletionHandler style (callback / continuation):
 *      channel.read(buf, pos, attachment, new CompletionHandler<>() {
 *          public void completed(Integer n, A attach) { ... }
 *          public void failed(Throwable t, A attach)  { ... }
 *      });
 *      Useful when: fully non-blocking pipelines; chaining multiple async ops.
 *
 * Thread pool:
 *   By default, AsynchronousFileChannel uses a thread pool from
 *   AsynchronousChannelGroup.withThreadPool(...) or the default system group.
 *   You can supply your own ExecutorService via open().
 */
public class AsyncIoDemo {

    static final Path DATA_DIR = Path.of("file-io-nio/data_files");

    /** Truncate a line for display; avoids StringIndexOutOfBounds on short files. */
    private static String preview(String line, int max) {
        return line.length() <= max ? line : line.substring(0, max) + "…";
    }

    // =========================================================================
    // 1. Future style — initiate + poll / block
    // =========================================================================

    /**
     * The Future style is the simpler of the two approaches.
     * channel.read() returns immediately with a Future<Integer>.
     * f.get() blocks until the operation completes and returns byte count.
     *
     * This is "async" only in the sense that you COULD do other work between
     * initiating the read and calling get(). In practice it's often used with
     * a timeout to avoid hanging forever.
     */
    static void futureStyleDemo() throws Exception {
        System.out.println("\n--- 1. Future style (read) ---");

        Path sample = DATA_DIR.resolve("sample.txt");

        try (AsynchronousFileChannel afc =
                AsynchronousFileChannel.open(sample, StandardOpenOption.READ)) {

            long size = afc.size();
            ByteBuffer buf = ByteBuffer.allocate((int) size);

            // Initiate read — returns immediately, I/O happens in background
            Future<Integer> future = afc.read(buf, 0);

            System.out.println("read initiated — doing other work...");
            // Simulate other work here
            Thread.sleep(1);

            // Block until read completes (or throw on timeout)
            int bytesRead = future.get(5, TimeUnit.SECONDS);

            buf.flip();
            String content = StandardCharsets.UTF_8.decode(buf).toString();
            System.out.println("bytes read: " + bytesRead);
            System.out.println("first line: " +
                preview(content.lines().findFirst().orElse(""), 40));
        }
    }

    // =========================================================================
    // 2. Future style — write
    // =========================================================================

    static void futureWriteDemo() throws Exception {
        System.out.println("\n--- 2. Future style (write) ---");

        Path out = DATA_DIR.resolve("output_async_write.txt");

        try (AsynchronousFileChannel afc = AsynchronousFileChannel.open(out,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            ByteBuffer buf = StandardCharsets.UTF_8.encode("Async write via Future style\n");

            Future<Integer> future = afc.write(buf, 0);
            int written = future.get(5, TimeUnit.SECONDS);
            System.out.println("written: " + written + " bytes");
        }

        System.out.println("content: " + Files.readString(out).trim());
        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 3. CompletionHandler style — callback / continuation
    // =========================================================================

    /**
     * CompletionHandler<V, A>:
     *   V = result type (Integer for read/write byte count)
     *   A = attachment type — arbitrary object you associate with the operation;
     *       passed to both completed() and failed().
     *
     * The callback is invoked on a thread from the channel's thread pool —
     * NOT on the calling thread. This means:
     *   - Do NOT block inside completed() / failed().
     *   - Do NOT assume any particular thread.
     *   - Use a CountDownLatch / CompletableFuture to synchronize with the caller.
     */
    static void completionHandlerReadDemo() throws Exception {
        System.out.println("\n--- 3. CompletionHandler style (read) ---");

        Path sample = DATA_DIR.resolve("sample.txt");
        CountDownLatch latch = new CountDownLatch(1);

        try (AsynchronousFileChannel afc =
                AsynchronousFileChannel.open(sample, StandardOpenOption.READ)) {

            long size  = afc.size();
            ByteBuffer buf = ByteBuffer.allocate((int) size);

            // Attachment: the latch itself (passed to both callbacks)
            afc.read(buf, 0, latch, new CompletionHandler<Integer, CountDownLatch>() {

                @Override
                public void completed(Integer bytesRead, CountDownLatch attachment) {
                    System.out.println("completed on thread: "
                        + Thread.currentThread().getName());
                    buf.flip();
                    System.out.println("bytes read: " + bytesRead);
                    System.out.println("first line: " +
                        preview(StandardCharsets.UTF_8.decode(buf).toString()
                            .lines().findFirst().orElse(""), 40));
                    attachment.countDown();  // signal caller
                }

                @Override
                public void failed(Throwable exc, CountDownLatch attachment) {
                    System.err.println("async read failed: " + exc.getMessage());
                    attachment.countDown();
                }
            });

            System.out.println("read initiated on calling thread: "
                + Thread.currentThread().getName());

            // Wait for completion handler (max 5 seconds)
            boolean done = latch.await(5, TimeUnit.SECONDS);
            System.out.println("completed within timeout: " + done);
        }
    }

    // =========================================================================
    // 4. CompletionHandler style — chaining writes
    // =========================================================================

    /**
     * A common async pattern: write chunk A, and in the completed() callback
     * for A, initiate write of chunk B. This avoids multi-threaded concurrency
     * on the same channel while still being non-blocking.
     */
    static void completionHandlerChainedWriteDemo() throws Exception {
        System.out.println("\n--- 4. CompletionHandler — chained writes ---");

        Path out = DATA_DIR.resolve("output_async_chained.txt");
        CountDownLatch latch = new CountDownLatch(1);

        try (AsynchronousFileChannel afc = AsynchronousFileChannel.open(out,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            ByteBuffer first  = StandardCharsets.UTF_8.encode("First chunk\n");
            ByteBuffer second = StandardCharsets.UTF_8.encode("Second chunk\n");

            // Write first chunk; when done, write second chunk in the callback
            afc.write(first, 0, null, new CompletionHandler<Integer, Void>() {

                @Override
                public void completed(Integer n, Void unused) {
                    System.out.println("first chunk written: " + n + " bytes");
                    // Chain: write second at offset = first chunk size
                    afc.write(second, n, null, new CompletionHandler<Integer, Void>() {
                        @Override
                        public void completed(Integer n2, Void unused2) {
                            System.out.println("second chunk written: " + n2 + " bytes");
                            latch.countDown();
                        }
                        @Override
                        public void failed(Throwable t, Void unused2) {
                            System.err.println("second write failed: " + t);
                            latch.countDown();
                        }
                    });
                }

                @Override
                public void failed(Throwable t, Void unused) {
                    System.err.println("first write failed: " + t);
                    latch.countDown();
                }
            });

            latch.await(5, TimeUnit.SECONDS);
        }

        System.out.println("final content:\n" +
            Files.readString(out, StandardCharsets.UTF_8).indent(4).stripTrailing());
        Files.deleteIfExists(out);
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws Exception {
        System.out.println("=== Async I/O Demo ===");
        futureStyleDemo();
        futureWriteDemo();
        completionHandlerReadDemo();
        completionHandlerChainedWriteDemo();
    }
}
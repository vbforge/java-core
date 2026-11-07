package com.vbforge.practice.practice_5_file_watcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

/**
 * Demonstrates WatchService — the NIO.2 filesystem event notification API.
 *
 * What is WatchService?
 *   A WatchService lets your code REACT to filesystem changes (files created,
 *   modified, or deleted in a directory) without polling. The OS delivers events
 *   (inotify on Linux, FSEvents on macOS, ReadDirectoryChangesW on Windows).
 *
 * Core types:
 *   WatchService — the event queue; created from FileSystems.getDefault()
 *   Watchable    — what can be watched; Path implements Watchable
 *   WatchKey     — a subscription token returned by path.register(...)
 *   WatchEvent<T>— a single event: kind + context (the affected filename)
 *
 * Event kinds (StandardWatchEventKinds):
 *   ENTRY_CREATE — a file or directory was created
 *   ENTRY_DELETE — a file or directory was deleted
 *   ENTRY_MODIFY — a file was modified (content or metadata)
 *   OVERFLOW     — events were lost because the queue was full; never discard!
 *
 * WatchKey lifecycle:
 *   1. register() → key is READY; events accumulate
 *   2. take() / poll() → key is SIGNALLED; you process pollEvents()
 *   3. key.reset() → key returns to READY state, ready for more events
 *   4. key.cancel() / WatchService.close() → key is CANCELLED; no more events
 *
 * IMPORTANT:
 *   - WatchService watches DIRECTORIES, not individual files.
 *   - Events are for DIRECT children only (not recursive by default).
 *   - Always call key.reset() after processing — if you forget, the key
 *     stays signalled and you'll never receive further events.
 *   - OVERFLOW events should prompt re-scanning the directory.
 *   - WatchService.take() BLOCKS; run it in a dedicated thread.
 *
 * Platform quirks:
 *   - Granularity varies: macOS may coalesce rapid events.
 *   - ENTRY_MODIFY fires differently per OS (may fire twice on Linux).
 *   - For directories, ENTRY_CREATE fires for the dir itself; its contents
 *     require a NEW registration on that sub-directory.
 */
public class FileWatcherDemo {

    static final Path DATA_DIR =
        Path.of("file-io-nio/data_files");

    // =========================================================================
    // 1. Basic watch loop — synchronous (blocking)
    // =========================================================================

    /**
     * The canonical watch loop:
     *   while (running) {
     *       WatchKey key = watcher.take();   // blocks
     *       for (WatchEvent<?> event : key.pollEvents()) {
     *           handle(event);
     *       }
     *       key.reset();
     *   }
     *
     * We run a short-lived demo: watch a temp directory, create/modify/delete
     * a file, and capture the events.
     */
    static void basicWatchDemo() throws Exception {
        System.out.println("\n--- 1. Basic WatchService loop ---");

        Path watchDir = DATA_DIR.resolve("watched");
        Files.createDirectories(watchDir);

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {

            // Register for CREATE and MODIFY events
            WatchKey key = watchDir.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

            System.out.println("watching: " + watchDir.toAbsolutePath());

            // Trigger events from a separate thread
            Thread trigger = new Thread(() -> {
                try {
                    Path file = watchDir.resolve("watch_demo.txt");
                    Files.writeString(file, "created");          // CREATE
                    Thread.sleep(50);
                    Files.writeString(file, "modified content"); // MODIFY
                    Thread.sleep(50);
                    Files.deleteIfExists(file);                   // DELETE
                } catch (Exception e) {
                    System.err.println("trigger error: " + e);
                }
            });
            trigger.start();

            int eventsReceived = 0;
            long deadline = System.currentTimeMillis() + 5_000;

            while (eventsReceived < 3 && System.currentTimeMillis() < deadline) {
                // poll with timeout so we don't block forever in the demo
                WatchKey wk = watcher.poll(2, TimeUnit.SECONDS);
                if (wk == null) continue; // timeout, no event yet

                for (WatchEvent<?> event : wk.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        System.out.println("  OVERFLOW — events lost!");
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path affected = watchDir.resolve(pathEvent.context());

                    System.out.printf("  event: %-15s  file: %s%n",
                        kind.name(), affected.getFileName());
                    eventsReceived++;
                }

                // MUST reset; without this, no more events will be delivered
                boolean valid = wk.reset();
                if (!valid) {
                    System.out.println("  key invalidated (dir deleted?)");
                    break;
                }
            }

            key.cancel();
            trigger.join(2_000);
        }

        // Cleanup
        Files.walk(DATA_DIR.resolve("watched"))
             .sorted(java.util.Comparator.reverseOrder())
             .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException e) {} });
    }

    // =========================================================================
    // 2. poll() — non-blocking check
    // =========================================================================

    /**
     * poll()             — returns null immediately if no events are ready.
     * poll(timeout, unit)— waits up to timeout for an event, then returns null.
     * take()             — blocks indefinitely until an event arrives.
     *
     * Use poll() in an event loop where you need to interleave watching with
     * other work (e.g., checking a shutdown flag).
     */
    static void pollVsTakeDemo() throws Exception {
        System.out.println("\n--- 2. poll() vs take() ---");

        Path watchDir = DATA_DIR.resolve("watched_poll");
        Files.createDirectories(watchDir);

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            watchDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

            // poll() — returns null immediately if no events
            WatchKey noEvent = watcher.poll();
            System.out.println("poll() with no events: " + noEvent); // null

            // Create a file to generate an event
            Path file = watchDir.resolve("polled.txt");
            Files.writeString(file, "trigger");
            Thread.sleep(100); // brief wait for OS to deliver the event

            // poll with timeout — should now see the event
            WatchKey k = watcher.poll(1, TimeUnit.SECONDS);
            if (k != null) {
                for (WatchEvent<?> ev : k.pollEvents()) {
                    System.out.println("poll() with event: " + ev.kind().name()
                        + " → " + ev.context());
                }
                k.reset();
            }

            // Cleanup
            Files.deleteIfExists(file);
            k = watcher.poll();
            if (k != null) k.pollEvents(); // drain delete event; k.reset();
        }

        Files.deleteIfExists(DATA_DIR.resolve("watched_poll"));
    }

    // =========================================================================
    // 3. WatchKey validity — cancel and invalidation
    // =========================================================================

    /**
     * A WatchKey is valid as long as:
     *   - It has not been explicitly cancelled via key.cancel()
     *   - The watched directory has not been deleted
     *   - The WatchService has not been closed
     *
     * After key.reset() returns false, the key is cancelled/invalid.
     * After watcher.close(), all keys become invalid.
     */
    static void watchKeyValidityDemo() throws Exception {
        System.out.println("\n--- 3. WatchKey validity ---");

        Path watchDir = DATA_DIR.resolve("watched_validity");
        Files.createDirectories(watchDir);

        WatchService watcher = FileSystems.getDefault().newWatchService();
        WatchKey key = watchDir.register(watcher,
            StandardWatchEventKinds.ENTRY_CREATE);

        System.out.println("key.isValid() after register: " + key.isValid()); // true

        key.cancel();
        System.out.println("key.isValid() after cancel:   " + key.isValid()); // false

        watcher.close();

        Files.deleteIfExists(DATA_DIR.resolve("watched_validity"));
    }

    // =========================================================================
    // 4. Recursive watching — manual registration of subdirectories
    // =========================================================================

    /**
     * WatchService only watches DIRECT children of a registered directory.
     * To watch subdirectories recursively, you must:
     *   1. Register each subdirectory individually.
     *   2. When an ENTRY_CREATE event fires for a directory, register that
     *      new subdirectory too.
     *
     * This is the standard pattern — there is no built-in recursive mode.
     */
    static void recursiveWatchExplanation() {
        System.out.println("\n--- 4. Recursive watching ---");
        System.out.println("  WatchService only covers direct children of each registered dir.");
        System.out.println("  For recursion:");
        System.out.println("  1. Register each existing subdirectory at startup.");
        System.out.println("  2. In the event loop, when ENTRY_CREATE fires for a dir,");
        System.out.println("     call newDir.register(watcher, kinds...) immediately.");
        System.out.println("  3. On Java 21+, use Thread.ofVirtual() for the watch thread.");
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws Exception {
        System.out.println("=== File Watcher Demo ===");
        basicWatchDemo();
        pollVsTakeDemo();
        watchKeyValidityDemo();
        recursiveWatchExplanation();
    }
}
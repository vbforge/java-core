package com.vbforge.file_io_nio;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

/**
 * WatchServiceDemo — annotated reference for {@link WatchService}, the NIO.2 file-change notifier.
 *
 * <p>A {@code WatchService} lets a program <em>react</em> to changes in a directory instead of
 * re-scanning it in a loop. The moving parts:
 * <pre>
 *   WatchService  ← the queue of "something changed" signals
 *   WatchKey      ← one registered directory (+ the events pending for it)
 *   WatchEvent    ← one change: kind (CREATE / MODIFY / DELETE) + context (the child's NAME)
 * </pre>
 *
 * <p>Topics:
 * <ol>
 *   <li>The basic loop — register, wait for a key, read events, <em>reset</em></li>
 *   <li>Event kinds and limits — what you get, what you do not (no recursion!)</li>
 *   <li>Key lifecycle — {@code reset}, {@code cancel}, closing the service</li>
 * </ol>
 *
 * <p><b>Platform note:</b> on Linux (inotify) and Windows notifications arrive almost instantly;
 * on macOS the JDK <em>polls</em>, so a change can take several seconds to be reported.
 * That is why the demo waits with a generous timeout rather than a fixed sleep.
 */
public class WatchServiceDemo {

    /** Upper bound for waiting for an event we expect. Returns as soon as the event arrives. */
    private static final long WAIT_SECONDS = 30;

    public static void main(String[] args) throws Exception {
        basicLoopDemo();
        System.out.println("---");
        eventKindsDemo();
        System.out.println("---");
        keyLifecycleDemo();
    }

    // ── 1. The basic loop ─────────────────────────────────────────────────────

    /**
     * The canonical watch loop: {@code register → poll/take → pollEvents → reset}.
     *
     * <p>WHY this matters: polling a directory every second burns CPU and still misses short-lived
     * files. {@code WatchService} delegates to the OS (inotify, ReadDirectoryChangesW, …) and wakes
     * your thread only when something happened.
     *
     * <ul>
     *   <li>{@code take()} blocks forever; {@code poll()} returns immediately (or {@code null});
     *       {@code poll(timeout, unit)} blocks up to a limit — prefer it so the thread can shut down.</li>
     *   <li>{@code event.context()} is the <em>relative</em> name of the child, not a full path. Resolve it
     *       against {@code key.watchable()}.</li>
     * </ul>
     *
     * <p>Complexity: registration is O(1) per directory; waiting costs no CPU.
     *
     * <p>Common gotcha: {@code event.count()} can be greater than 1 for repeated events and a single file
     * write often produces several {@code ENTRY_MODIFY} events — never assume "one change = one event".
     */
    static void basicLoopDemo() throws IOException, InterruptedException {
        System.out.println("=== Basic loop ===");

        Path dir = Files.createTempDirectory("nio-watch-");
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            // Register BEFORE causing the change, otherwise the event is missed
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

            Files.createFile(dir.resolve("hello.txt"));

            WatchKey key = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
            if (key == null) {
                System.out.println("no event within " + WAIT_SECONDS + "s");
                return;
            }

            Path watched = (Path) key.watchable();                          // the registered directory
            for (WatchEvent<?> event : key.pollEvents()) {
                Path name = (Path) event.context();                         // relative: "hello.txt"
                System.out.println("kind           : " + event.kind());      // ENTRY_CREATE
                System.out.println("context        : " + name);
                System.out.println("full path      : " + watched.resolve(name).equals(dir.resolve("hello.txt")));
            }

            // Mandatory: without reset() this key is never queued again
            System.out.println("reset() valid  : " + key.reset());
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }

    // ── 2. Event kinds and limits ─────────────────────────────────────────────

    /**
     * {@link StandardWatchEventKinds} defines the kinds you can subscribe to:
     * {@code ENTRY_CREATE}, {@code ENTRY_MODIFY}, {@code ENTRY_DELETE}. A fourth, {@code OVERFLOW},
     * means "events were lost" — it is delivered even if you did not ask for it, and the correct
     * reaction is to re-scan the directory.
     *
     * <p>Behaviour demonstrated:
     * <ul>
     *   <li>You only receive the kinds you registered for.</li>
     *   <li>Registering the same directory again with the same service returns the <em>same</em> key
     *       (and replaces its event set).</li>
     *   <li>Watching is <strong>not recursive</strong> — a change inside a sub-directory is invisible to the
     *       parent's key. To watch a tree, register every directory yourself (see the tasks).</li>
     *   <li>Only directories can be registered; a regular file throws {@link java.nio.file.NotDirectoryException}.</li>
     * </ul>
     *
     * <p>WHY this matters: these four facts explain nearly every "my watcher misses events" bug.
     */
    static void eventKindsDemo() throws IOException, InterruptedException {
        System.out.println("=== Event kinds and limits ===");

        Path dir = Files.createTempDirectory("nio-kinds-");
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            System.out.println("kind names     : " + StandardWatchEventKinds.ENTRY_CREATE.name() + ", "
                    + StandardWatchEventKinds.ENTRY_MODIFY.name() + ", "
                    + StandardWatchEventKinds.ENTRY_DELETE.name() + ", "
                    + StandardWatchEventKinds.OVERFLOW.name());

            // 1. Only registered kinds are reported: subscribe to DELETE, then CREATE a file
            WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
            Files.createFile(dir.resolve("ignored.txt"));
            System.out.println("create ignored : " + (watcher.poll(500, TimeUnit.MILLISECONDS) == null)); // true

            // 2. Re-registering returns the same key
            WatchKey again = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            System.out.println("same key       : " + (again == key));                                    // true

            // 3. Not recursive: CREATE watcher on `dir` does not see files created in dir/child
            Path child = Files.createDirectory(dir.resolve("child"));
            // (drain the CREATE event for "child" itself, which IS a direct child of dir)
            drain(watcher);
            Files.createFile(child.resolve("nested.txt"));
            System.out.println("nested create  : " + (watcher.poll(500, TimeUnit.MILLISECONDS) == null)); // true

            // 4. A regular file cannot be registered
            try {
                dir.resolve("ignored.txt").register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            } catch (java.nio.file.NotDirectoryException e) {
                System.out.println("register file : NotDirectoryException");
            }
        } finally {
            PathsAndFilesDemo.deleteTree(dir);
        }
    }

    // ── 3. Key lifecycle ──────────────────────────────────────────────────────

    /**
     * A {@link WatchKey} is a small state machine:
     * <pre>
     *   READY ──(event arrives)──► SIGNALLED (queued in the service)
     *   SIGNALLED ──(you poll it)──► SIGNALLED (but no longer queued!)
     *   SIGNALLED ──(reset())──► READY      — or straight back into the queue if events are pending
     *   any state ──(cancel() / service.close())──► INVALID
     * </pre>
     *
     * <p>WHY this matters: the single most common {@code WatchService} bug is forgetting
     * {@code key.reset()}. The key stays signalled, new events pile up inside it, and the
     * service never hands it out again — the watcher silently goes deaf.
     *
     * <p>{@code reset()} returns {@code false} when the key is no longer valid (directory deleted,
     * key cancelled, service closed) — that is your signal to stop watching that directory.
     *
     * <p>Common gotcha: after {@code watcher.close()} every key is invalid and every further call on the
     * service throws {@link ClosedWatchServiceException}.
     */
    static void keyLifecycleDemo() throws IOException, InterruptedException {
        System.out.println("=== Key lifecycle ===");

        Path dir = Files.createTempDirectory("nio-life-");
        WatchService watcher = FileSystems.getDefault().newWatchService();
        try {
            WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

            Files.createFile(dir.resolve("first.txt"));
            WatchKey signalled = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
            System.out.println("got key        : " + (signalled == key));
            signalled.pollEvents();                                          // consume first.txt

            // Forgot reset()? New events are stored in the key but the key is NOT re-queued
            Files.createFile(dir.resolve("second.txt"));
            System.out.println("no reset → poll: " + watcher.poll(500, TimeUnit.MILLISECONDS)); // null

            // reset() puts it back in play — the pending event is delivered
            System.out.println("reset()        : " + key.reset());
            WatchKey redelivered = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
            System.out.println("redelivered    : " + (redelivered == key));
            redelivered.pollEvents();

            // cancel() invalidates the key; reset() then reports false
            key.cancel();
            System.out.println("valid          : " + key.isValid());          // false
            System.out.println("reset after    : " + key.reset());            // false
        } finally {
            watcher.close();
            PathsAndFilesDemo.deleteTree(dir);
        }

        try {
            watcher.poll();
        } catch (ClosedWatchServiceException e) {
            System.out.println("closed service : ClosedWatchServiceException");
        }
    }

    // ── helper ────────────────────────────────────────────────────────────────

    /** Consumes whatever is currently queued (waits briefly for it to arrive). */
    private static void drain(WatchService watcher) throws InterruptedException {
        WatchKey key = watcher.poll(WAIT_SECONDS, TimeUnit.SECONDS);
        if (key != null) {
            key.pollEvents();
            key.reset();
        }
    }
}

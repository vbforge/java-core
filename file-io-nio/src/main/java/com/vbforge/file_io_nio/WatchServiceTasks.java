package com.vbforge.file_io_nio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.List;

/**
 * WatchServiceTasks — implement every method below to make {@code WatchServiceTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code WatchServiceDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=WatchServiceTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 *
 * <p><b>Note:</b> on macOS the JDK polls for changes, so tests that wait for an event may take up to
 * ~10 seconds. They still pass — they just wait for the timeout you were given.
 */
public class WatchServiceTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Describes a watch event kind as a lower-case word.
     *
     * <p>Examples:
     * <pre>
     *   describeKind(ENTRY_CREATE)  → "created"
     *   describeKind(ENTRY_MODIFY)  → "modified"
     *   describeKind(ENTRY_DELETE)  → "deleted"
     *   describeKind(OVERFLOW)      → "overflow"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The four constants live in {@link java.nio.file.StandardWatchEventKinds}.</li>
     *   <li>Kinds are singletons, but {@code WatchEvent.Kind} also has a {@code name()} method.</li>
     * </ul>
     *
     * @param kind a watch event kind
     * @return {@code "created"}, {@code "modified"}, {@code "deleted"} or {@code "overflow"}
     * @throws IllegalArgumentException for any other kind
     */
    public String describeKind(WatchEvent.Kind<?> kind) {
        throw new UnsupportedOperationException("TODO: implement describeKind");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Watches {@code dir} for <em>newly created</em> entries. The method:
     * <ol>
     *   <li>creates a {@link WatchService} and registers {@code dir} for creation events only,</li>
     *   <li><em>then</em> runs {@code trigger} (registering first guarantees no event is missed),</li>
     *   <li>collects the names of created entries until {@code expectedCount} names are gathered or
     *       {@code timeoutMillis} has elapsed — whichever comes first,</li>
     *   <li>closes the watch service and returns the names in the order they were reported.</li>
     * </ol>
     * It returns early as soon as enough names arrived. If the timeout expires first, it returns
     * whatever was collected (possibly an empty list). Overflow events are ignored. Deleting or
     * modifying files must not produce names.
     *
     * <p>Examples:
     * <pre>
     *   trigger creates a.txt and b.txt, expectedCount=2   → ["a.txt","b.txt"]  (order may vary by platform)
     *   trigger does nothing, expectedCount=1, timeout 300 → []                  (after ~300 ms)
     *   trigger deletes an existing file, expectedCount=1  → []                  (deletes are not creations)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Wait with a timeout, not forever — compute the remaining time from a deadline on each loop.</li>
     *   <li>After handling a key's events the key must be told it may be signalled again.</li>
     *   <li>An event's {@code context()} is a <em>relative</em> name.</li>
     * </ul>
     *
     * @param dir           the directory to watch
     * @param trigger       action that causes changes in {@code dir}; runs after registration
     * @param expectedCount how many created names to wait for
     * @param timeoutMillis maximum time to wait in milliseconds
     * @return created entry names, in report order
     * @throws IOException          if the directory cannot be watched ({@code NoSuchFileException} if missing)
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public List<String> waitForCreated(Path dir, Runnable trigger, int expectedCount, long timeoutMillis)
            throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement waitForCreated");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Watches {@code dir} for the <em>deletion</em> of one specific entry. Registers {@code dir}
     * for deletion events only, then runs {@code trigger}, then waits until a deletion event whose
     * name equals {@code fileName} is reported or {@code timeoutMillis} elapses. Deletions of other
     * entries are ignored but must not end the wait early. The watch service is closed before returning.
     *
     * <p>Examples:
     * <pre>
     *   dir holds wanted.txt, trigger deletes it                   → true
     *   dir holds wanted.txt and other.txt, trigger deletes other  → false  (after the timeout)
     *   trigger does nothing                                       → false  (after the timeout)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You will write the same wait-loop as in {@code waitForCreated} — only the registered kind
     *       and the stop condition differ.</li>
     * </ul>
     *
     * @param dir           the directory to watch
     * @param fileName      the name of the entry whose deletion we wait for
     * @param trigger       action that causes changes in {@code dir}; runs after registration
     * @param timeoutMillis maximum time to wait in milliseconds
     * @return {@code true} if the named entry was reported deleted in time, otherwise {@code false}
     * @throws IOException          if the directory cannot be watched
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public boolean waitForDeletion(Path dir, String fileName, Runnable trigger, long timeoutMillis)
            throws IOException, InterruptedException {
        throw new UnsupportedOperationException("TODO: implement waitForDeletion");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Registers {@code root} <em>and every directory below it</em> with {@code watchService}
     * for creation events, and returns how many directories were registered (including
     * {@code root}). Regular files are never registered. After this call, creating a file in any
     * of the directories makes the service report a key whose {@code watchable()} is that directory.
     *
     * <p>Examples (tree: {@code root/}, {@code root/a/}, {@code root/a/b/}, {@code root/c/}, {@code root/x.txt}):
     * <pre>
     *   registerAll(ws, root)       → 4      (root, a, a/b, c)
     *   registerAll(ws, emptyDir)   → 1
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A single registration is never recursive — you must visit the tree yourself.</li>
     *   <li>You need a hook that fires for <em>directories</em>, not for files.</li>
     * </ul>
     *
     * @param watchService the service to register with (not closed by this method)
     * @param root         the top directory of the tree
     * @return the number of directories registered
     * @throws IOException if a directory cannot be registered
     */
    public int registerAll(WatchService watchService, Path root) throws IOException {
        throw new UnsupportedOperationException("TODO: implement registerAll");
    }
}

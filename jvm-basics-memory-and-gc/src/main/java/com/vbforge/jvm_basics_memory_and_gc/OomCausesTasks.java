package com.vbforge.jvm_basics_memory_and_gc;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Tasks for {@link OomCausesDemo}: implement each method so that
 * {@code OomCausesTasksTest} passes.
 */
public class OomCausesTasks {

    /**
     * Classify an {@code OutOfMemoryError} message into a family name. Return the first
     * match of these (the message only needs to <em>contain</em> the text):
     * <pre>
     * "Java heap space"                       → "HEAP"
     * "GC overhead limit exceeded"            → "GC_OVERHEAD"
     * "Metaspace"                             → "METASPACE"
     * "unable to create native thread"        → "NATIVE_THREAD"
     * "Requested array size exceeds VM limit" → "ARRAY_LIMIT"
     * "Direct buffer memory"                  → "DIRECT_BUFFER"
     * anything else, or {@code null}          → "UNKNOWN"
     * </pre>
     *
     * <p>Examples:
     * <pre>
     * diagnoseOom("Java heap space")                               → "HEAP"
     * diagnoseOom("unable to create native thread: possibly out …") → "NATIVE_THREAD"
     * diagnoseOom("Compressed class space is gone")                → "UNKNOWN"
     * diagnoseOom(null)                                            → "UNKNOWN"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check for {@code null} first</li>
     *   <li>{@code String.contains} is enough — no regex needed</li>
     * </ul>
     *
     * @param message the {@code getMessage()} of an OutOfMemoryError, possibly {@code null}
     * @return the family name
     */
    public String diagnoseOom(String message) {
        throw new UnsupportedOperationException("TODO: implement diagnoseOom");
    }

    /**
     * Create an <b>access-ordered LRU cache</b>: a {@link Map} that never holds more than
     * {@code maxEntries} entries. When a {@code put} would exceed the limit, the
     * <em>least recently used</em> entry (read via {@code get} or written via {@code put})
     * is removed.
     *
     * <p>Examples:
     * <pre>
     * max 2:  put(a) put(b) put(c)             → keys [b, c]
     * max 2:  put(a) put(b) get(a) put(c)      → keys [a, c]   (b was least recently used)
     * max 1:  put(a) put(b)                    → keys [b]
     * max 0:  → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code LinkedHashMap} has a constructor flag for access order</li>
     *   <li>It also has a protected hook that is called after each insertion</li>
     * </ul>
     *
     * @param maxEntries the capacity, at least 1
     * @param <K>        key type
     * @param <V>        value type
     * @return an empty, bounded, access-ordered map
     * @throws IllegalArgumentException if {@code maxEntries < 1}
     */
    public <K, V> Map<K, V> newLruCache(int maxEntries) {
        throw new UnsupportedOperationException("TODO: implement newLruCache");
    }

    /**
     * Run {@code body} with {@code value} stored in {@code threadLocal}, and make sure the
     * thread-local is <b>cleared afterwards even if {@code body} throws</b>. Return what
     * {@code body} returned; exceptions from {@code body} propagate unchanged.
     *
     * <p>Examples:
     * <pre>
     * runWithThreadLocal(tl, "u1", () -&gt; tl.get())  → "u1"   (and tl.get() is null again afterwards)
     * body throws IllegalStateException              → the exception propagates, tl is still cleared
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code try} / {@code finally}</li>
     *   <li>{@code ThreadLocal.remove()} — not {@code set(null)} — releases the slot</li>
     * </ul>
     *
     * @param threadLocal the thread-local to use
     * @param value       the value to store while the body runs
     * @param body        the code to run
     * @param <T>         the thread-local value type
     * @param <R>         the result type
     * @return the result of {@code body}
     */
    public <T, R> R runWithThreadLocal(ThreadLocal<T> threadLocal, T value, Supplier<R> body) {
        throw new UnsupportedOperationException("TODO: implement runWithThreadLocal");
    }

    /**
     * Remove, <b>in place</b>, every entry of {@code lastAccessMillis} whose last access is
     * older than the time-to-live, and return how many entries were removed. An entry is
     * stale when {@code nowMillis - lastAccess > ttlMillis} (strictly greater).
     *
     * <p>Examples:
     * <pre>
     * {a=100, b=900, c=500}, now=1000, ttl=300 → returns 2, map becomes {b=900}
     * {a=700}, now=1000, ttl=300               → returns 0 (age 300 is not &gt; 300)
     * {}                                       → returns 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Do not remove from a map while iterating it with a plain for-each — use the iterator or a collection view's bulk-removal API</li>
     * </ul>
     *
     * @param lastAccessMillis key → last access time, modified in place
     * @param nowMillis        the current time
     * @param ttlMillis        the maximum age that is still kept
     * @return the number of removed entries
     */
    public int evictStale(Map<String, Long> lastAccessMillis, long nowMillis, long ttlMillis) {
        throw new UnsupportedOperationException("TODO: implement evictStale");
    }
}

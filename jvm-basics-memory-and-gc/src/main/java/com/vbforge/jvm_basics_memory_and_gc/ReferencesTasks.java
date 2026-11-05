package com.vbforge.jvm_basics_memory_and_gc;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Tasks for {@link ReferencesDemo}: implement each method so that
 * {@code ReferencesTasksTest} passes.
 */
public class ReferencesTasks {

    /**
     * Return the referent of {@code ref}, or {@code fallback} when the reference has been
     * cleared. The referent must be read <b>only once</b>, because the GC may clear a
     * weak reference at any moment.
     *
     * <p>Examples:
     * <pre>
     * getOrDefault(new WeakReference&lt;&gt;("x"), "none") → "x"
     * getOrDefault(clearedRef, "none")                 → "none"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Store the result of {@code get()} in a local variable before testing it</li>
     * </ul>
     *
     * @param ref      a weak reference, never {@code null}
     * @param fallback the value to use when the referent is gone
     * @param <T>      the referent type
     * @return the referent or the fallback
     */
    public <T> T getOrDefault(WeakReference<T> ref, T fallback) {
        throw new UnsupportedOperationException("TODO: implement getOrDefault");
    }

    /**
     * Count how many of the given references still have a referent.
     *
     * <p>Examples:
     * <pre>
     * countAlive([ref("a"), ref("b"), clearedRef]) → 2
     * countAlive([])                              → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A cleared reference returns {@code null} from {@code get()}</li>
     * </ul>
     *
     * @param refs the references to inspect, never {@code null}
     * @return the number of references that are not cleared
     */
    public int countAlive(Collection<? extends Reference<?>> refs) {
        throw new UnsupportedOperationException("TODO: implement countAlive");
    }

    /**
     * Remove every cleared reference from {@code refs} <b>in place</b> and return how many
     * were removed. Live references keep their relative order.
     *
     * <p>Examples:
     * <pre>
     * [a, CLEARED, b, CLEARED] → list becomes [a, b], returns 2
     * [a, b]                   → list unchanged,      returns 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Removing from a list while iterating needs care — consider the collection-level API</li>
     * </ul>
     *
     * @param refs a modifiable list of references
     * @return the number of removed entries
     */
    public int pruneCleared(List<? extends Reference<?>> refs) {
        throw new UnsupportedOperationException("TODO: implement pruneCleared");
    }

    /**
     * Take every reference currently waiting on {@code queue} and return how many there
     * were. The method must not block when the queue is empty.
     *
     * <p>Examples:
     * <pre>
     * queue with 3 enqueued references → 3 (queue is empty afterwards)
     * empty queue                      → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ReferenceQueue} has a non-blocking and a blocking retrieval method — pick the non-blocking one</li>
     * </ul>
     *
     * @param queue the queue to drain, never {@code null}
     * @return the number of references removed from the queue
     */
    public int drainQueue(ReferenceQueue<?> queue) {
        throw new UnsupportedOperationException("TODO: implement drainQueue");
    }

    /**
     * A memory-sensitive cache lookup. Return the cached value for {@code key} if its
     * soft reference is still alive; otherwise call {@code loader}, store the result
     * wrapped in a <b>new</b> {@link SoftReference} under {@code key}, and return it.
     * The loader must run at most once per call and not at all on a cache hit.
     *
     * <p>Examples:
     * <pre>
     * empty cache,        key "a" → loader runs, cache now holds "a"
     * live entry for "a", key "a" → returns cached value, loader NOT called
     * cleared entry for "a"       → loader runs again, entry replaced
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The map may contain the key but with a cleared reference — treat that as a miss</li>
     *   <li>Read the referent once into a local variable</li>
     * </ul>
     *
     * @param cache  the cache map, never {@code null}
     * @param key    the lookup key
     * @param loader computes the value on a miss
     * @param <K>    key type
     * @param <V>    value type
     * @return the cached or freshly loaded value
     */
    public <K, V> V getOrLoad(Map<K, SoftReference<V>> cache, K key, Function<K, V> loader) {
        throw new UnsupportedOperationException("TODO: implement getOrLoad");
    }
}

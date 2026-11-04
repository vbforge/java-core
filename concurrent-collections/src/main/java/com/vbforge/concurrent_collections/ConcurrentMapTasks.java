package com.vbforge.concurrent_collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Tasks for {@code ConcurrentMapDemo}. Implement each method, then remove
 * {@code @Disabled} from the matching {@code @Nested} class in
 * {@code ConcurrentMapTasksTest}.
 */
public class ConcurrentMapTasks {

    /**
     * Count how many times each word occurs, using several threads that all
     * write into ONE shared map.
     *
     * <p>Split {@code words} into {@code threads} slices of (roughly) equal
     * size. Each worker thread counts the words of its own slice into the shared
     * map. Return only after every worker has finished. No increment may be lost.
     *
     * <p>Examples:
     * <pre>
     * countWordsConcurrently(["a", "b", "a"], 2)  →  {a=2, b=1}
     * countWordsConcurrently([], 4)               →  {}
     * countWordsConcurrently(["x"], 8)            →  {x=1}      (more threads than words is fine)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A plain {@code HashMap} (or a get-then-put sequence) loses updates under contention</li>
     *   <li>{@code ConcurrentMap} has a method that inserts-or-combines in one atomic step</li>
     *   <li>{@code Thread.join()} (or an {@code ExecutorService}) is how you wait for the workers</li>
     * </ul>
     *
     * @param words   the words to count (never null, no null elements)
     * @param threads number of worker threads, at least 1
     * @return word → number of occurrences
     */
    public Map<String, Integer> countWordsConcurrently(List<String> words, int threads) {
        throw new UnsupportedOperationException("TODO: implement countWordsConcurrently");
    }

    /**
     * Return the cached value for {@code key}; if there is none yet, compute it
     * with {@code loader}, store it and return it. The loader must run
     * <b>at most once per key</b>, even when many threads ask for the same
     * missing key at the same moment.
     *
     * <p>Examples:
     * <pre>
     * cache = {}
     * getOrLoad(cache, "a", String::toUpperCase)   →  "A"      cache is now {a=A}
     * getOrLoad(cache, "a", String::toLowerCase)   →  "A"      loader NOT called again
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Checking {@code get(key) == null} and then calling {@code put} is a check-then-act race</li>
     *   <li>One {@code ConcurrentMap} method takes a mapping function and runs it only if the key is absent</li>
     * </ul>
     *
     * @param cache  the shared cache
     * @param key    the key to look up (never null)
     * @param loader computes the value for a key (never returns null)
     * @return the cached or freshly loaded value
     */
    public String getOrLoad(ConcurrentMap<String, String> cache, String key, Function<String, String> loader) {
        throw new UnsupportedOperationException("TODO: implement getOrLoad");
    }

    /**
     * Remove {@code key} only if it is currently mapped to {@code expectedValue},
     * as one atomic step. Values are compared with {@code equals}, not {@code ==}.
     *
     * <p>Examples:
     * <pre>
     * map = {a=1}
     * removeIfUnchanged(map, "a", 2)   →  false    map is still {a=1}
     * removeIfUnchanged(map, "a", 1)   →  true     map is now {}
     * removeIfUnchanged(map, "z", 1)   →  false    key was never there
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ConcurrentMap} declares a two-argument {@code remove}</li>
     * </ul>
     *
     * @param map           the shared map
     * @param key           the key to remove (never null)
     * @param expectedValue the value the key must currently have
     * @return {@code true} if the entry was removed
     */
    public boolean removeIfUnchanged(ConcurrentMap<String, Integer> map, String key, int expectedValue) {
        throw new UnsupportedOperationException("TODO: implement removeIfUnchanged");
    }

    /**
     * Add 1 to the value of {@code key} <b>only if the key is already present</b>.
     * An absent key must stay absent. Safe to call from many threads at once:
     * no increment may be lost.
     *
     * <p>Examples:
     * <pre>
     * map = {a=1}
     * incrementIfPresent(map, "a")   →  2       map is now {a=2}
     * incrementIfPresent(map, "z")   →  null    map is still {a=2}  (no "z" was created)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One of the {@code compute...} methods applies a function only when the key exists</li>
     *   <li>That same method returns the new value, or {@code null} when nothing was applied</li>
     * </ul>
     *
     * @param map the shared map
     * @param key the key to increment (never null)
     * @return the new value, or {@code null} if the key was absent
     */
    public Integer incrementIfPresent(ConcurrentMap<String, Integer> map, String key) {
        throw new UnsupportedOperationException("TODO: implement incrementIfPresent");
    }

    /**
     * Return the sum of all values in the map, using the bulk operations that
     * are specific to {@link ConcurrentHashMap}. An empty map has the sum 0.
     *
     * <p>Examples:
     * <pre>
     * sumOfValues({a=1, b=2, c=3})   →  6
     * sumOfValues({a=-5, b=5})       →  0
     * sumOfValues({})                →  0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The bulk methods take a "parallelism threshold" as their first argument</li>
     *   <li>Read the Javadoc of the reduce method about what it returns for an empty map</li>
     * </ul>
     *
     * @param map the map to sum (never null)
     * @return the sum of all values
     */
    public int sumOfValues(ConcurrentHashMap<String, Integer> map) {
        throw new UnsupportedOperationException("TODO: implement sumOfValues");
    }
}

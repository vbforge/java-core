package com.vbforge.concurrent_collections;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Demo: {@link ConcurrentHashMap} — the thread-safe replacement for {@link HashMap}.
 *
 * <p>Read the sections top to bottom. Every claim in a comment is either printed
 * by {@link #main(String[])} or asserted in {@code ConcurrentMapDemoTest}.
 *
 * <p>The sections that need two or more threads are written so that the outcome
 * is <b>deterministic</b>: a {@link CyclicBarrier} or a latch forces the exact
 * interleaving we want to show, instead of hoping the scheduler produces it.
 */
public class ConcurrentMapDemo {

    public static void main(String[] args) throws InterruptedException {
        nullHostilityDemo();
        System.out.println("---");
        checkThenActRaceDemo();
        System.out.println("---");
        atomicCompoundOperationsDemo();
        System.out.println("---");
        concurrentCountingDemo();
        System.out.println("---");
        weaklyConsistentIteratorsDemo();
        System.out.println("---");
        bulkOperationsDemo();
        System.out.println("---");
        concurrentKeySetDemo();
    }

    // =========================================================================
    // 1. Null hostility
    // =========================================================================

    /*
     * WHAT
     *   ConcurrentHashMap rejects null keys AND null values (NullPointerException).
     *   HashMap accepts both.
     *
     * WHY
     *   In a concurrent map, "get(k) returned null" must mean exactly one thing:
     *   the key is absent. If null values were legal you would have to call
     *   containsKey(k) afterwards to tell "absent" from "mapped to null" - and
     *   another thread may change the map between those two calls. Banning null
     *   removes the ambiguity.
     *
     * COMPLEXITY
     *   - not relevant here
     *
     * GOTCHA
     *   Code ported from HashMap that stores null as "no value" will start
     *   throwing NullPointerException. Store a sentinel or use Optional instead.
     */
    public static void nullHostilityDemo() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(null, "value-for-null-key");
        hashMap.put("key", null);
        System.out.println("HashMap accepts null key and null value, size = " + hashMap.size());

        ConcurrentMap<String, String> concurrent = new ConcurrentHashMap<>();
        try {
            concurrent.put(null, "value");
        } catch (NullPointerException e) {
            System.out.println("ConcurrentHashMap.put(null, value) -> NullPointerException");
        }
        try {
            concurrent.put("key", null);
        } catch (NullPointerException e) {
            System.out.println("ConcurrentHashMap.put(key, null)   -> NullPointerException");
        }
        try {
            concurrent.get(null);
        } catch (NullPointerException e) {
            System.out.println("ConcurrentHashMap.get(null)        -> NullPointerException");
        }
    }

    // =========================================================================
    // 2. The check-then-act race (and its cure)
    // =========================================================================

    /*
     * WHAT
     *   "if (map.get(k) == null) map.put(k, create())" is two separate operations.
     *   Each one is thread-safe on its own, but another thread can slip in
     *   BETWEEN them. computeIfAbsent performs check + create + insert as ONE
     *   atomic step.
     *
     * WHY
     *   Thread-safe collection != thread-safe program. Making individual calls
     *   safe does not make a sequence of calls safe. The fix is to use the
     *   single compound method the collection offers.
     *
     * HOW THE DEMO WORKS
     *   A CyclicBarrier(2) is placed between the "check" and the "act", so both
     *   threads are guaranteed to have seen "absent" before either of them
     *   writes. The race happens every single run, not "sometimes".
     *
     * COMPLEXITY
     *   - computeIfAbsent: O(1) average, plus the cost of the mapping function
     *
     * GOTCHA
     *   Keep the mapping function short and side-effect free, and never touch
     *   the same map from inside it: other updates to that part of the map are
     *   blocked while your function runs.
     */
    public static void checkThenActRaceDemo() throws InterruptedException {
        ConcurrentMap<String, String> broken = new ConcurrentHashMap<>();
        AtomicInteger brokenInitCount = new AtomicInteger();
        CyclicBarrier bothHaveChecked = new CyclicBarrier(2);

        runInParallel(2, i -> {
            if (broken.get("config") == null) {          // check
                bothHaveChecked.await();                  // ...both threads are now between check and act
                brokenInitCount.incrementAndGet();        // "expensive" initialisation runs twice!
                broken.put("config", "created-by-" + i);  // act
            }
        });
        System.out.println("get + put            -> initialiser ran " + brokenInitCount.get() + " times");

        ConcurrentMap<String, String> safe = new ConcurrentHashMap<>();
        AtomicInteger safeInitCount = new AtomicInteger();
        runInParallel(8, i -> safe.computeIfAbsent("config", key -> {
            safeInitCount.incrementAndGet();
            return "created-once";
        }));
        System.out.println("computeIfAbsent      -> initialiser ran " + safeInitCount.get() + " time");
    }

    // =========================================================================
    // 3. Atomic compound operations
    // =========================================================================

    /*
     * WHAT
     *   The ConcurrentMap interface adds atomic "read-modify-write" methods:
     *     putIfAbsent(k, v)          insert only if absent, returns previous value (or null)
     *     computeIfAbsent(k, f)      create the value lazily, only if absent
     *     computeIfPresent(k, f)     update only if present; returning null removes the entry
     *     compute(k, f)              absent OR present - f sees null when absent
     *     merge(k, v, f)             insert v if absent, otherwise combine old and v with f
     *     replace(k, old, new)       compare-and-set on the value (uses equals)
     *     remove(k, v)               compare-and-remove on the value (uses equals)
     *
     * WHY
     *   Each of these replaces a get / if / put sequence that would otherwise
     *   need a lock around it.
     *
     * COMPLEXITY
     *   - each call: O(1) average
     *
     * GOTCHA
     *   putIfAbsent returns the PREVIOUS value: null means "I inserted it",
     *   non-null means "someone else already had it". It does not return the
     *   value you passed in.
     */
    public static void atomicCompoundOperationsDemo() {
        ConcurrentMap<String, Integer> stock = new ConcurrentHashMap<>();

        Integer first = stock.putIfAbsent("apple", 5);
        Integer second = stock.putIfAbsent("apple", 99);
        System.out.println("putIfAbsent twice        -> " + first + " then " + second
                + ", apple = " + stock.get("apple"));

        stock.computeIfAbsent("pear", key -> 7);
        stock.computeIfPresent("apple", (key, old) -> old + 1);
        System.out.println("computeIfAbsent/Present  -> pear = " + stock.get("pear")
                + ", apple = " + stock.get("apple"));

        stock.compute("kiwi", (key, old) -> old == null ? 1 : old + 1);
        stock.merge("kiwi", 10, Integer::sum);
        System.out.println("compute + merge          -> kiwi = " + stock.get("kiwi"));

        boolean replaced = stock.replace("kiwi", 11, 12);
        boolean notReplaced = stock.replace("kiwi", 11, 13);
        System.out.println("replace(k, old, new)     -> " + replaced + " then " + notReplaced
                + ", kiwi = " + stock.get("kiwi"));

        boolean wrongValue = stock.remove("pear", 8);
        boolean rightValue = stock.remove("pear", 7);
        System.out.println("remove(k, value)         -> " + wrongValue + " then " + rightValue
                + ", pear = " + stock.get("pear"));

        System.out.println("getOrDefault             -> " + stock.getOrDefault("missing", 0));
    }

    // =========================================================================
    // 4. Counting from many threads
    // =========================================================================

    /*
     * WHAT
     *   merge(key, 1, Integer::sum) is the idiomatic thread-safe counter for a
     *   map of counts.
     *
     * WHY
     *   The "obvious" version, map.put(k, map.getOrDefault(k, 0) + 1), loses
     *   updates when two threads read the same old value. merge reads, adds and
     *   writes as one atomic step, so no increment is ever lost.
     *
     * COMPLEXITY
     *   - each merge: O(1) average
     *
     * GOTCHA
     *   Integer::sum works because the values are Integers. For a map of
     *   mutable counters (Map<K, AtomicInteger> or LongAdder) you would use
     *   computeIfAbsent to create the counter and then increment it.
     */
    public static void concurrentCountingDemo() throws InterruptedException {
        ConcurrentMap<String, Integer> counts = new ConcurrentHashMap<>();
        String[] colours = {"red", "green", "blue"};

        runInParallel(4, thread -> {
            for (int i = 0; i < 1000; i++) {
                counts.merge(colours[i % 3], 1, Integer::sum);
            }
        });

        int total = counts.get("red") + counts.get("green") + counts.get("blue");
        System.out.println("4 threads x 1000 merges  -> " + counts + ", total = " + total);
    }

    // =========================================================================
    // 5. Weakly consistent iterators
    // =========================================================================

    /*
     * WHAT
     *   HashMap iterators are FAIL-FAST: structurally modify the map while
     *   iterating and the next call to next() throws ConcurrentModificationException.
     *   ConcurrentHashMap iterators are WEAKLY CONSISTENT: they never throw
     *   ConcurrentModificationException, they traverse every entry that existed
     *   when the iterator was created, and they MAY or may not show later changes.
     *
     * WHY
     *   In a concurrent program "someone else modified the map" is normal, not
     *   an error. Failing the reader would make iteration unusable.
     *
     * COMPLEXITY
     *   - full iteration: O(n)
     *
     * GOTCHA
     *   "May or may not show later changes" means you cannot rely on seeing a
     *   concurrently inserted entry - and must not rely on NOT seeing it either.
     *   Also: even in a single thread, a loop that adds keys while iterating
     *   needs a stop condition, because the iterator may visit the new keys.
     */
    public static void weaklyConsistentIteratorsDemo() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("a", 1);
        hashMap.put("b", 2);
        hashMap.put("c", 3);
        boolean hashMapFailed = false;
        try {
            for (String key : hashMap.keySet()) {
                hashMap.put(key + "!", 0);
            }
        } catch (ConcurrentModificationException e) {
            hashMapFailed = true;
        }
        System.out.println("HashMap, put while iterating           -> ConcurrentModificationException: "
                + hashMapFailed);

        ConcurrentMap<String, Integer> concurrent = new ConcurrentHashMap<>();
        concurrent.put("a", 1);
        concurrent.put("b", 2);
        concurrent.put("c", 3);
        int originalsVisited = 0;
        for (String key : concurrent.keySet()) {
            if (!key.endsWith("!")) {          // stop condition: never derive keys from derived keys
                concurrent.put(key + "!", 0);
                originalsVisited++;
            }
        }
        System.out.println("ConcurrentHashMap, put while iterating -> visited " + originalsVisited
                + " original keys, size is now " + concurrent.size());
    }

    // =========================================================================
    // 6. Bulk operations (Java 8)
    // =========================================================================

    /*
     * WHAT
     *   Java 8 added bulk operations that exist only on the ConcurrentHashMap
     *   CLASS (not on the ConcurrentMap interface): forEach, search, reduce,
     *   reduceValues, reduceKeys, ... each taking a "parallelism threshold".
     *
     * WHY
     *   They traverse the map safely while it is being updated, and can fan the
     *   work out across the common ForkJoinPool when the map is big enough.
     *   The threshold is "how many entries before we go parallel":
     *     1               -> parallelise as much as possible
     *     Long.MAX_VALUE  -> never parallelise, run on the calling thread
     *
     * COMPLEXITY
     *   - O(n) in the number of entries; parallel speed-up only pays off for
     *     large maps and non-trivial per-entry work
     *
     * GOTCHA
     *   reduceValues (and reduce, reduceKeys, ...) returns NULL for an empty
     *   map. Unboxing that result into an int throws NullPointerException.
     *   Also: size() returns an int and is only an estimate while other threads
     *   are updating; mappingCount() returns a long and is the preferred call.
     */
    public static void bulkOperationsDemo() {
        ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
        scores.put("ann", 90);
        scores.put("bob", 72);
        scores.put("cy", 85);

        long sequential = Long.MAX_VALUE;

        Integer total = scores.reduceValues(sequential, Integer::sum);
        String topScorer = scores.search(sequential, (name, score) -> score > 85 ? name : null);
        AtomicInteger visited = new AtomicInteger();
        scores.forEach(sequential, (name, score) -> visited.incrementAndGet());
        Integer emptyTotal = new ConcurrentHashMap<String, Integer>().reduceValues(sequential, Integer::sum);

        System.out.println("reduceValues(sum)        -> " + total);
        System.out.println("search(score > 85)       -> " + topScorer);
        System.out.println("forEach visited          -> " + visited.get() + " entries");
        System.out.println("mappingCount()           -> " + scores.mappingCount());
        System.out.println("reduceValues on empty    -> " + emptyTotal);
    }

    // =========================================================================
    // 7. A concurrent Set
    // =========================================================================

    /*
     * WHAT
     *   ConcurrentHashMap.newKeySet() returns a Set backed by a ConcurrentHashMap.
     *
     * WHY
     *   There is no ConcurrentHashSet class in the JDK. This factory (Java 8) is
     *   the standard way to get a hash-based, thread-safe Set with the same
     *   iteration guarantees as the map.
     *
     * COMPLEXITY
     *   - add / contains / remove: O(1) average
     *
     * GOTCHA
     *   Like the map, it rejects null elements. And add() returns false if the
     *   element was already there - use that return value instead of calling
     *   contains() first (that would be another check-then-act race).
     */
    public static void concurrentKeySetDemo() throws InterruptedException {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        boolean firstAdd = seen.add("x");
        boolean secondAdd = seen.add("x");
        System.out.println("add twice                -> " + firstAdd + " then " + secondAdd);

        Set<Integer> ids = ConcurrentHashMap.newKeySet();
        runInParallel(4, thread -> {
            for (int i = 0; i < 100; i++) {
                ids.add(i);
            }
        });
        System.out.println("4 threads add 0..99      -> size = " + ids.size());
    }

    // =========================================================================
    // Helper: start N threads together, wait for all of them
    // =========================================================================

    @FunctionalInterface
    private interface Body {
        void run(int threadIndex) throws Exception;
    }

    private static void runInParallel(int threads, Body body) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    body.run(index);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                }
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        if (failure.get() != null) {
            throw new IllegalStateException("a worker thread failed", failure.get());
        }
    }
}

package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ConcurrentMapDemo — API contracts")
class ConcurrentMapDemoTest {

    @Nested
    @DisplayName("1. Null hostility")
    class NullHostility {

        @Test
        @DisplayName("HashMap accepts a null key and a null value")
        void hashMapAcceptsNulls() {
            Map<String, String> map = new HashMap<>();
            map.put(null, "for-null-key");
            map.put("key", null);

            assertThat(map.get(null)).isEqualTo("for-null-key");
            assertThat(map.containsKey("key")).isTrue();
            assertThat(map.get("key")).isNull();
        }

        @Test
        @DisplayName("ConcurrentHashMap rejects a null key")
        void rejectsNullKey() {
            ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

            assertThatThrownBy(() -> map.put(null, "value")).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> map.get(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> map.containsKey(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("ConcurrentHashMap rejects a null value")
        void rejectsNullValue() {
            ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

            assertThatThrownBy(() -> map.put("key", null)).isInstanceOf(NullPointerException.class);
            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("get returns null only when the key is absent")
        void nullMeansAbsent() {
            ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
            map.put("present", "value");

            assertThat(map.get("present")).isEqualTo("value");
            assertThat(map.get("absent")).isNull();
        }
    }

    @Nested
    @DisplayName("2. Check-then-act race")
    @Timeout(10)
    class CheckThenAct {

        @Test
        @DisplayName("get + put lets two threads both initialise the same key")
        void getThenPutRunsInitialiserTwice() throws InterruptedException {
            ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
            AtomicInteger initCount = new AtomicInteger();
            CyclicBarrier bothHaveChecked = new CyclicBarrier(2);

            runInParallel(2, i -> {
                if (map.get("config") == null) {
                    bothHaveChecked.await();
                    initCount.incrementAndGet();
                    map.put("config", "created-by-" + i);
                }
            });

            assertThat(initCount.get()).isEqualTo(2);
            assertThat(map).hasSize(1);
        }

        @Test
        @DisplayName("computeIfAbsent runs the initialiser once for 8 racing threads")
        void computeIfAbsentRunsOnce() throws InterruptedException {
            ConcurrentMap<String, String> map = new ConcurrentHashMap<>();
            AtomicInteger initCount = new AtomicInteger();

            runInParallel(8, i -> map.computeIfAbsent("config", key -> {
                initCount.incrementAndGet();
                return "created-once";
            }));

            assertThat(initCount.get()).isEqualTo(1);
            assertThat(map).containsEntry("config", "created-once");
        }

        @Test
        @DisplayName("every racing thread receives the same value")
        void everyThreadSeesTheSameValue() throws InterruptedException {
            ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
            Set<Object> results = ConcurrentHashMap.newKeySet();

            runInParallel(8, i -> results.add(map.computeIfAbsent("config", key -> new Object())));

            assertThat(results).hasSize(1);
        }
    }

    @Nested
    @DisplayName("3. Atomic compound operations")
    class CompoundOperations {

        @Test
        @DisplayName("putIfAbsent returns the previous value and never overwrites")
        void putIfAbsent() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

            assertThat(map.putIfAbsent("apple", 5)).isNull();
            assertThat(map.putIfAbsent("apple", 99)).isEqualTo(5);
            assertThat(map).containsEntry("apple", 5);
        }

        @Test
        @DisplayName("computeIfAbsent does not call the function when the key is present")
        void computeIfAbsentSkipsPresentKey() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("apple", 5);
            AtomicInteger calls = new AtomicInteger();

            Integer result = map.computeIfAbsent("apple", key -> {
                calls.incrementAndGet();
                return 99;
            });

            assertThat(result).isEqualTo(5);
            assertThat(calls.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("computeIfPresent updates a present key and ignores an absent one")
        void computeIfPresent() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("apple", 5);

            assertThat(map.computeIfPresent("apple", (key, old) -> old + 1)).isEqualTo(6);
            assertThat(map.computeIfPresent("pear", (key, old) -> old + 1)).isNull();
            assertThat(map).containsEntry("apple", 6);
            assertThat(map.containsKey("pear")).isFalse();
        }

        @Test
        @DisplayName("computeIfPresent returning null removes the entry")
        void computeIfPresentRemoves() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("apple", 5);

            map.computeIfPresent("apple", (key, old) -> null);

            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("compute sees null for an absent key")
        void compute() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

            map.compute("kiwi", (key, old) -> old == null ? 1 : old + 1);
            map.compute("kiwi", (key, old) -> old == null ? 1 : old + 1);

            assertThat(map).containsEntry("kiwi", 2);
        }

        @Test
        @DisplayName("merge inserts when absent and combines when present")
        void merge() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

            map.merge("kiwi", 1, Integer::sum);
            map.merge("kiwi", 10, Integer::sum);

            assertThat(map).containsEntry("kiwi", 11);
        }

        @Test
        @DisplayName("merge returning null removes the entry")
        void mergeRemoves() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("kiwi", 1);

            map.merge("kiwi", 1, (old, added) -> null);

            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("replace(key, old, new) only succeeds when the value matches")
        void replace() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("kiwi", 11);

            assertThat(map.replace("kiwi", 11, 12)).isTrue();
            assertThat(map.replace("kiwi", 11, 13)).isFalse();
            assertThat(map).containsEntry("kiwi", 12);
        }

        @Test
        @DisplayName("remove(key, value) only succeeds when the value matches, compared with equals")
        void conditionalRemove() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("pear", 1000);

            assertThat(map.remove("pear", 8)).isFalse();
            assertThat(map.remove("pear", 1000)).isTrue();
            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("getOrDefault returns the default for an absent key without inserting it")
        void getOrDefault() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

            assertThat(map.getOrDefault("missing", 0)).isEqualTo(0);
            assertThat(map).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. Concurrent counting")
    @Timeout(10)
    class ConcurrentCounting {

        @Test
        @DisplayName("merge loses no update across 4 threads and 3 keys")
        void mergeCounts() throws InterruptedException {
            ConcurrentMap<String, Integer> counts = new ConcurrentHashMap<>();
            String[] colours = {"red", "green", "blue"};

            runInParallel(4, thread -> {
                for (int i = 0; i < 1000; i++) {
                    counts.merge(colours[i % 3], 1, Integer::sum);
                }
            });

            // per thread: i in 0..999 -> red 334, green 333, blue 333; times 4 threads
            assertThat(counts).containsEntry("red", 1336);
            assertThat(counts).containsEntry("green", 1332);
            assertThat(counts).containsEntry("blue", 1332);
        }

        @Test
        @DisplayName("8 threads hammering ONE key still total exactly threads x iterations")
        void singleHotKey() throws InterruptedException {
            ConcurrentMap<String, Integer> counts = new ConcurrentHashMap<>();

            runInParallel(8, thread -> {
                for (int i = 0; i < 5_000; i++) {
                    counts.merge("hot", 1, Integer::sum);
                }
            });

            assertThat(counts).containsEntry("hot", 40_000);
        }
    }

    @Nested
    @DisplayName("5. Weakly consistent iterators")
    class WeaklyConsistentIterators {

        @Test
        @DisplayName("HashMap throws ConcurrentModificationException when a key is added while iterating")
        void hashMapIsFailFast() {
            Map<String, Integer> map = new HashMap<>();
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            assertThatThrownBy(() -> {
                for (String key : map.keySet()) {
                    map.put(key + "!", 0);
                }
            }).isInstanceOf(ConcurrentModificationException.class);
        }

        @Test
        @DisplayName("ConcurrentHashMap never throws, and visits every key that existed at the start")
        void concurrentHashMapIsWeaklyConsistent() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);
            Set<String> originalsVisited = new TreeSet<>();

            for (String key : map.keySet()) {
                if (!key.endsWith("!")) {
                    map.put(key + "!", 0);
                    originalsVisited.add(key);
                }
            }

            assertThat(originalsVisited).containsExactly("a", "b", "c");
            assertThat(map).hasSize(6);
        }

        @Test
        @DisplayName("removing entries while iterating is allowed")
        void removeWhileIterating() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            for (String key : map.keySet()) {
                map.remove(key);
            }

            assertThat(map).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. Bulk operations")
    class BulkOperations {

        private ConcurrentHashMap<String, Integer> scores() {
            ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
            scores.put("ann", 90);
            scores.put("bob", 72);
            scores.put("cy", 85);
            return scores;
        }

        @Test
        @DisplayName("reduceValues sums the values")
        void reduceValues() {
            Integer total = scores().reduceValues(Long.MAX_VALUE, Integer::sum);

            assertThat(total).isEqualTo(247);
        }

        @Test
        @DisplayName("reduceValues on an empty map returns null, not 0")
        void reduceValuesOnEmptyMap() {
            ConcurrentHashMap<String, Integer> empty = new ConcurrentHashMap<>();

            Integer total = empty.reduceValues(Long.MAX_VALUE, Integer::sum);

            assertThat(total).isNull();
        }

        @Test
        @DisplayName("search returns the first non-null result, or null when nothing matches")
        void search() {
            ConcurrentHashMap<String, Integer> scores = scores();

            // Assign to a typed variable first: passing the generic search(...) call
            // straight into assertThat(...) makes javac fail with "ambiguous" overloads.
            String topScorer = scores.search(Long.MAX_VALUE, (name, score) -> score > 85 ? name : null);
            String nobody = scores.search(Long.MAX_VALUE, (name, score) -> score > 100 ? name : null);

            assertThat(topScorer).isEqualTo("ann");
            assertThat(nobody).isNull();
        }

        @Test
        @DisplayName("forEach with a threshold visits every entry")
        void forEach() {
            Set<String> visited = ConcurrentHashMap.newKeySet();

            scores().forEach(Long.MAX_VALUE, (name, score) -> visited.add(name + "=" + score));

            assertThat(visited).containsExactlyInAnyOrder("ann=90", "bob=72", "cy=85");
        }

        @Test
        @DisplayName("mappingCount returns the number of entries as a long")
        void mappingCount() {
            assertThat(scores().mappingCount()).isEqualTo(3L);
        }

        @Test
        @DisplayName("a parallelism threshold of 1 gives the same result as a sequential run")
        void parallelGivesSameResult() {
            ConcurrentHashMap<Integer, Integer> numbers = new ConcurrentHashMap<>();
            for (int i = 0; i < 1000; i++) {
                numbers.put(i, i);
            }

            Integer sequential = numbers.reduceValues(Long.MAX_VALUE, Integer::sum);
            Integer parallel = numbers.reduceValues(1, Integer::sum);

            assertThat(sequential).isEqualTo(499_500);
            assertThat(parallel).isEqualTo(499_500);
        }
    }

    @Nested
    @DisplayName("7. Concurrent key set")
    @Timeout(10)
    class ConcurrentKeySet {

        @Test
        @DisplayName("add returns true the first time and false for a duplicate")
        void addReturnValue() {
            Set<String> set = ConcurrentHashMap.newKeySet();

            assertThat(set.add("x")).isTrue();
            assertThat(set.add("x")).isFalse();
            assertThat(set).hasSize(1);
        }

        @Test
        @DisplayName("4 threads adding the same 100 values produce 100 elements")
        void concurrentAdds() throws InterruptedException {
            Set<Integer> set = ConcurrentHashMap.newKeySet();

            runInParallel(4, thread -> {
                for (int i = 0; i < 100; i++) {
                    set.add(i);
                }
            });

            assertThat(set).hasSize(100);
        }

        @Test
        @DisplayName("rejects null elements")
        void rejectsNull() {
            Set<String> set = ConcurrentHashMap.newKeySet();

            assertThatThrownBy(() -> set.add(null)).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("8. main() runs every section")
    @Timeout(10)
    class MainMethod {

        @Test
        @DisplayName("prints seven sections separated by ---")
        void mainPrintsAllSections() throws InterruptedException {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured, true));
            try {
                ConcurrentMapDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            String[] sections = captured.toString().split("(?m)^---$");
            assertThat(sections).hasSize(7);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: start N threads together, wait for all of them
    // -------------------------------------------------------------------------

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
            throw new AssertionError("a worker thread failed: " + failure.get(), failure.get());
        }
    }
}
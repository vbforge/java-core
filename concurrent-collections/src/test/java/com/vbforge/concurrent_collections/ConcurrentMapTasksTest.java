package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConcurrentMapTasks — your implementations")
class ConcurrentMapTasksTest {

    private ConcurrentMapTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ConcurrentMapTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countWordsConcurrently()")
    @DisplayName("countWordsConcurrently")
    @Timeout(10)
    class CountWordsConcurrentlyTest {

        @Test
        @DisplayName("counts the words of the Javadoc example")
        void example() {
            Map<String, Integer> counts = tasks.countWordsConcurrently(Arrays.asList("a", "b", "a"), 2);

            assertThat(counts).hasSize(2);
            assertThat(counts).containsEntry("a", 2);
            assertThat(counts).containsEntry("b", 1);
        }

        @Test
        @DisplayName("an empty list gives an empty map")
        void emptyInput() {
            assertThat(tasks.countWordsConcurrently(Collections.<String>emptyList(), 4)).isEmpty();
        }

        @Test
        @DisplayName("more threads than words is fine")
        void moreThreadsThanWords() {
            Map<String, Integer> counts = tasks.countWordsConcurrently(Collections.singletonList("x"), 8);

            assertThat(counts).hasSize(1);
            assertThat(counts).containsEntry("x", 1);
        }

        @Test
        @DisplayName("a word that occurs in every slice is counted once per occurrence")
        void sameWordInEverySlice() {
            List<String> words = new ArrayList<>(Collections.nCopies(1000, "same"));

            assertThat(tasks.countWordsConcurrently(words, 4)).containsEntry("same", 1000);
        }

        @ParameterizedTest(name = "{0} thread(s) give the same counts")
        @CsvSource({"1", "2", "4", "8"})
        void sameResultForAnyThreadCount(int threads) {
            List<String> words = new ArrayList<>();
            for (int i = 0; i < 20_000; i++) {
                words.add("w" + (i % 5));
            }

            Map<String, Integer> counts = tasks.countWordsConcurrently(words, threads);

            assertThat(counts).hasSize(5);
            assertThat(counts).containsEntry("w0", 4000);
            assertThat(counts).containsEntry("w1", 4000);
            assertThat(counts).containsEntry("w2", 4000);
            assertThat(counts).containsEntry("w3", 4000);
            assertThat(counts).containsEntry("w4", 4000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getOrLoad()")
    @DisplayName("getOrLoad")
    @Timeout(10)
    class GetOrLoadTest {

        @Test
        @DisplayName("loads, stores and returns the value of the Javadoc example")
        void example() {
            ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();

            assertThat(tasks.getOrLoad(cache, "a", String::toUpperCase)).isEqualTo("A");
            assertThat(cache).containsEntry("a", "A");
        }

        @Test
        @DisplayName("a cached value is returned and the loader is not called")
        void cachedValueWins() {
            ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();
            cache.put("a", "cached");
            AtomicInteger calls = new AtomicInteger();

            String value = tasks.getOrLoad(cache, "a", key -> {
                calls.incrementAndGet();
                return "loaded";
            });

            assertThat(value).isEqualTo("cached");
            assertThat(calls.get()).isEqualTo(0);
        }

        @Test
        @DisplayName("repeated calls for one key call the loader once")
        void loaderCalledOnceForRepeatedCalls() {
            ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();
            AtomicInteger calls = new AtomicInteger();
            Function<String, String> loader = key -> {
                calls.incrementAndGet();
                return key.toUpperCase();
            };

            tasks.getOrLoad(cache, "a", loader);
            tasks.getOrLoad(cache, "a", loader);
            tasks.getOrLoad(cache, "a", loader);

            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("different keys are loaded independently")
        void differentKeys() {
            ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();
            AtomicInteger calls = new AtomicInteger();
            Function<String, String> loader = key -> {
                calls.incrementAndGet();
                return key.toUpperCase();
            };

            assertThat(tasks.getOrLoad(cache, "a", loader)).isEqualTo("A");
            assertThat(tasks.getOrLoad(cache, "b", loader)).isEqualTo("B");
            assertThat(calls.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("8 threads asking for the same missing key run a slow loader once")
        void loaderRunsOnceUnderContention() throws InterruptedException {
            ConcurrentMap<String, String> cache = new ConcurrentHashMap<>();
            AtomicInteger calls = new AtomicInteger();
            Set<String> results = ConcurrentHashMap.newKeySet();
            Function<String, String> slowLoader = key -> {
                calls.incrementAndGet();
                sleepQuietly(20);                    // widens the race window for a get-then-put solution
                return key.toUpperCase();
            };

            runInParallel(8, i -> results.add(tasks.getOrLoad(cache, "config", slowLoader)));

            assertThat(calls.get()).isEqualTo(1);
            assertThat(results).containsExactly("CONFIG");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement removeIfUnchanged()")
    @DisplayName("removeIfUnchanged")
    class RemoveIfUnchangedTest {

        @Test
        @DisplayName("removes the entry when the value matches (Javadoc example)")
        void example() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);

            assertThat(tasks.removeIfUnchanged(map, "a", 2)).isFalse();
            assertThat(map).containsEntry("a", 1);

            assertThat(tasks.removeIfUnchanged(map, "a", 1)).isTrue();
            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("an absent key returns false")
        void absentKey() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);

            assertThat(tasks.removeIfUnchanged(map, "z", 1)).isFalse();
            assertThat(map).hasSize(1);
        }

        @Test
        @DisplayName("other entries are left alone")
        void otherEntriesUntouched() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);
            map.put("b", 1);

            tasks.removeIfUnchanged(map, "a", 1);

            assertThat(map).hasSize(1);
            assertThat(map).containsEntry("b", 1);
        }

        @ParameterizedTest(name = "stored {0}, expected {1} -> removed {2}")
        @CsvSource({"1, 1, true", "1, 2, false", "1000, 1000, true", "-5, -5, true", "0, 1, false"})
        void parametrized(int stored, int expected, boolean removed) {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("k", stored);

            assertThat(tasks.removeIfUnchanged(map, "k", expected)).isEqualTo(removed);
            assertThat(map.containsKey("k")).isEqualTo(!removed);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement incrementIfPresent()")
    @DisplayName("incrementIfPresent")
    @Timeout(10)
    class IncrementIfPresentTest {

        @Test
        @DisplayName("increments a present key and returns the new value (Javadoc example)")
        void example() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);

            assertThat(tasks.incrementIfPresent(map, "a")).isEqualTo(2);
            assertThat(map).containsEntry("a", 2);
        }

        @Test
        @DisplayName("an absent key returns null and is NOT created")
        void absentKeyStaysAbsent() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 2);

            assertThat(tasks.incrementIfPresent(map, "z")).isNull();
            assertThat(map.containsKey("z")).isFalse();
            assertThat(map).hasSize(1);
        }

        @Test
        @DisplayName("works on a value of 0 and on negative values")
        void zeroAndNegative() {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("zero", 0);
            map.put("negative", -1);

            assertThat(tasks.incrementIfPresent(map, "zero")).isEqualTo(1);
            assertThat(tasks.incrementIfPresent(map, "negative")).isEqualTo(0);
            assertThat(map).containsEntry("negative", 0);
        }

        @Test
        @DisplayName("4 threads x 500 increments lose no update")
        void concurrentIncrements() throws InterruptedException {
            ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("counter", 0);

            runInParallel(4, thread -> {
                for (int i = 0; i < 500; i++) {
                    tasks.incrementIfPresent(map, "counter");
                }
            });

            assertThat(map).containsEntry("counter", 2000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumOfValues()")
    @DisplayName("sumOfValues")
    class SumOfValuesTest {

        @Test
        @DisplayName("adds up the values (Javadoc example)")
        void example() {
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            assertThat(tasks.sumOfValues(map)).isEqualTo(6);
        }

        @Test
        @DisplayName("an empty map has the sum 0")
        void emptyMap() {
            assertThat(tasks.sumOfValues(new ConcurrentHashMap<String, Integer>())).isEqualTo(0);
        }

        @Test
        @DisplayName("negative values are summed too")
        void negativeValues() {
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("a", -5);
            map.put("b", 5);

            assertThat(tasks.sumOfValues(map)).isEqualTo(0);
        }

        @Test
        @DisplayName("a single entry")
        void singleEntry() {
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            map.put("only", 42);

            assertThat(tasks.sumOfValues(map)).isEqualTo(42);
        }

        @Test
        @DisplayName("1000 entries")
        void manyEntries() {
            ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
            for (int i = 0; i < 1000; i++) {
                map.put("key" + i, i);
            }

            assertThat(tasks.sumOfValues(map)).isEqualTo(499_500);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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

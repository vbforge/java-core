package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReferencesTasks — your implementations")
class ReferencesTasksTest {

    private ReferencesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ReferencesTasks();
    }

    /** Strongly held values so that only explicit clear() calls empty a reference. */
    private final List<Object> keepAlive = new ArrayList<>();

    private WeakReference<Object> live(String value) {
        keepAlive.add(value);
        return new WeakReference<Object>(value);
    }

    private WeakReference<Object> cleared() {
        WeakReference<Object> ref = new WeakReference<Object>("gone");
        ref.clear();
        return ref;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getOrDefault()")
    @DisplayName("getOrDefault")
    class GetOrDefaultTest {

        @Test
        @DisplayName("a live reference returns its referent")
        void alive() {
            String value = "x";
            assertThat(tasks.getOrDefault(new WeakReference<>(value), "none")).isEqualTo("x");
        }

        @Test
        @DisplayName("a cleared reference returns the fallback")
        void clearedRef() {
            WeakReference<String> ref = new WeakReference<>("x");
            ref.clear();
            assertThat(tasks.getOrDefault(ref, "none")).isEqualTo("none");
        }

        @Test
        @DisplayName("a null fallback is returned as null when the reference is cleared")
        void nullFallback() {
            WeakReference<String> ref = new WeakReference<>("x");
            ref.clear();
            assertThat(tasks.getOrDefault(ref, null)).isNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countAlive()")
    @DisplayName("countAlive")
    class CountAliveTest {

        @Test
        @DisplayName("[a, b, cleared] → 2")
        void example() {
            List<Reference<Object>> refs = new ArrayList<Reference<Object>>();
            refs.add(live("a"));
            refs.add(live("b"));
            refs.add(cleared());
            assertThat(tasks.countAlive(refs)).isEqualTo(2);
        }

        @Test
        @DisplayName("empty collection → 0")
        void empty() {
            assertThat(tasks.countAlive(Collections.<Reference<Object>>emptyList())).isZero();
        }

        @Test
        @DisplayName("all cleared → 0")
        void allCleared() {
            List<Reference<Object>> refs = new ArrayList<Reference<Object>>();
            refs.add(cleared());
            refs.add(cleared());
            assertThat(tasks.countAlive(refs)).isZero();
        }

        @Test
        @DisplayName("works with soft references too")
        void softRefs() {
            SoftReference<String> alive = new SoftReference<>("a");
            SoftReference<String> dead = new SoftReference<>("b");
            dead.clear();
            assertThat(tasks.countAlive(Arrays.asList(alive, dead))).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement pruneCleared()")
    @DisplayName("pruneCleared")
    class PruneClearedTest {

        @Test
        @DisplayName("[a, CLEARED, b, CLEARED] → [a, b], returns 2")
        void example() {
            WeakReference<Object> a = live("a");
            WeakReference<Object> b = live("b");
            List<Reference<Object>> refs = new ArrayList<Reference<Object>>();
            refs.add(a);
            refs.add(cleared());
            refs.add(b);
            refs.add(cleared());

            assertThat(tasks.pruneCleared(refs)).isEqualTo(2);
            assertThat(refs).containsExactly(a, b);
        }

        @Test
        @DisplayName("nothing cleared → unchanged, returns 0")
        void nothingCleared() {
            List<Reference<Object>> refs = new ArrayList<Reference<Object>>();
            refs.add(live("a"));
            refs.add(live("b"));
            assertThat(tasks.pruneCleared(refs)).isZero();
            assertThat(refs).hasSize(2);
        }

        @Test
        @DisplayName("consecutive cleared entries are all removed")
        void consecutive() {
            WeakReference<Object> a = live("a");
            List<Reference<Object>> refs = new ArrayList<Reference<Object>>();
            refs.add(cleared());
            refs.add(cleared());
            refs.add(cleared());
            refs.add(a);
            assertThat(tasks.pruneCleared(refs)).isEqualTo(3);
            assertThat(refs).containsExactly(a);
        }

        @Test
        @DisplayName("empty list → 0")
        void empty() {
            assertThat(tasks.pruneCleared(new ArrayList<Reference<Object>>())).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement drainQueue()")
    @DisplayName("drainQueue")
    class DrainQueueTest {

        @Test
        @DisplayName("three enqueued references → 3, queue empty afterwards")
        void example() {
            ReferenceQueue<Object> queue = new ReferenceQueue<>();
            List<Object> strong = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Object referent = new Object();
                strong.add(referent);
                new WeakReference<>(referent, queue).enqueue();
            }
            assertThat(tasks.drainQueue(queue)).isEqualTo(3);
            assertThat(queue.poll()).isNull();
        }

        @Test
        @DisplayName("an empty queue → 0 and does not block")
        void empty() {
            assertThat(tasks.drainQueue(new ReferenceQueue<Object>())).isZero();
        }

        @Test
        @DisplayName("draining twice returns 0 the second time")
        void idempotent() {
            ReferenceQueue<Object> queue = new ReferenceQueue<>();
            Object referent = new Object();
            new WeakReference<>(referent, queue).enqueue();
            assertThat(tasks.drainQueue(queue)).isEqualTo(1);
            assertThat(tasks.drainQueue(queue)).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getOrLoad()")
    @DisplayName("getOrLoad")
    class GetOrLoadTest {

        @Test
        @DisplayName("empty cache → loader runs and the value is cached")
        void miss() {
            Map<String, SoftReference<String>> cache = new HashMap<>();
            AtomicInteger calls = new AtomicInteger();
            String value = tasks.getOrLoad(cache, "a", k -> {
                calls.incrementAndGet();
                return k.toUpperCase();
            });
            assertThat(value).isEqualTo("A");
            assertThat(calls.get()).isEqualTo(1);
            assertThat(cache).containsKey("a");
            assertThat(cache.get("a").get()).isEqualTo("A");
        }

        @Test
        @DisplayName("live entry → cached value returned, loader NOT called")
        void hit() {
            Map<String, SoftReference<String>> cache = new HashMap<>();
            String cached = "cached";
            cache.put("a", new SoftReference<>(cached));
            AtomicInteger calls = new AtomicInteger();
            String value = tasks.getOrLoad(cache, "a", k -> {
                calls.incrementAndGet();
                return "fresh";
            });
            assertThat(value).isSameAs(cached);
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("cleared entry → loader runs again and the reference is replaced")
        void clearedEntry() {
            Map<String, SoftReference<String>> cache = new HashMap<>();
            SoftReference<String> old = new SoftReference<>("old");
            old.clear();
            cache.put("a", old);

            String value = tasks.getOrLoad(cache, "a", k -> "fresh");

            assertThat(value).isEqualTo("fresh");
            assertThat(cache.get("a")).isNotSameAs(old);
            assertThat(cache.get("a").get()).isEqualTo("fresh");
        }

        @Test
        @DisplayName("different keys are loaded independently")
        void independentKeys() {
            Map<String, SoftReference<String>> cache = new HashMap<>();
            assertThat(tasks.getOrLoad(cache, "a", k -> k + "1")).isEqualTo("a1");
            assertThat(tasks.getOrLoad(cache, "b", k -> k + "2")).isEqualTo("b2");
            assertThat(cache).hasSize(2);
        }
    }
}

package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OomCausesTasks — your implementations")
class OomCausesTasksTest {

    private OomCausesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new OomCausesTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement diagnoseOom()")
    @DisplayName("diagnoseOom")
    class DiagnoseOomTest {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "Java heap space, HEAP",
                "GC overhead limit exceeded, GC_OVERHEAD",
                "Metaspace, METASPACE",
                "unable to create native thread: possibly out of memory or process/resource limits reached, NATIVE_THREAD",
                "Requested array size exceeds VM limit, ARRAY_LIMIT",
                "Direct buffer memory, DIRECT_BUFFER",
                "Compressed class space is gone, UNKNOWN"
        })
        @DisplayName("each documented message maps to its family")
        void table(String message, String expected) {
            assertThat(tasks.diagnoseOom(message)).isEqualTo(expected);
        }

        @Test
        @DisplayName("null → UNKNOWN")
        void nullMessage() {
            assertThat(tasks.diagnoseOom(null)).isEqualTo("UNKNOWN");
        }

        @Test
        @DisplayName("the marker may appear inside a longer message")
        void contained() {
            assertThat(tasks.diagnoseOom("OutOfMemoryError: Java heap space (while loading)")).isEqualTo("HEAP");
        }

        @Test
        @DisplayName("an empty message → UNKNOWN")
        void emptyMessage() {
            assertThat(tasks.diagnoseOom("")).isEqualTo("UNKNOWN");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement newLruCache()")
    @DisplayName("newLruCache")
    class NewLruCacheTest {

        @Test
        @DisplayName("max 2: put(a) put(b) put(c) → keys [b, c]")
        void evictsOldest() {
            Map<String, Integer> cache = tasks.newLruCache(2);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.put("c", 3);
            assertThat(cache.keySet()).containsExactly("b", "c");
        }

        @Test
        @DisplayName("max 2: put(a) put(b) get(a) put(c) → keys [a, c]")
        void readRefreshesEntry() {
            Map<String, Integer> cache = tasks.newLruCache(2);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.get("a");
            cache.put("c", 3);
            assertThat(cache.keySet()).containsExactly("a", "c");
        }

        @Test
        @DisplayName("max 1: put(a) put(b) → keys [b]")
        void capacityOne() {
            Map<String, Integer> cache = tasks.newLruCache(1);
            cache.put("a", 1);
            cache.put("b", 2);
            assertThat(cache.keySet()).containsExactly("b");
        }

        @Test
        @DisplayName("size never exceeds the limit")
        void bounded() {
            Map<Integer, Integer> cache = tasks.newLruCache(50);
            for (int i = 0; i < 1000; i++) {
                cache.put(i, i);
            }
            assertThat(cache).hasSize(50);
            assertThat(cache).containsKey(999).doesNotContainKey(0);
        }

        @Test
        @DisplayName("overwriting an existing key does not evict anything")
        void overwrite() {
            Map<String, Integer> cache = tasks.newLruCache(2);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.put("a", 10);
            assertThat(cache).containsEntry("a", 10).containsEntry("b", 2);
        }

        @Test
        @DisplayName("maxEntries < 1 → IllegalArgumentException")
        void invalid() {
            assertThatThrownBy(() -> tasks.newLruCache(0)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runWithThreadLocal()")
    @DisplayName("runWithThreadLocal")
    class RunWithThreadLocalTest {

        @Test
        @DisplayName("the body sees the value, and the thread-local is cleared afterwards")
        void example() {
            ThreadLocal<String> context = new ThreadLocal<>();
            String seen = tasks.runWithThreadLocal(context, "u1", context::get);
            assertThat(seen).isEqualTo("u1");
            assertThat(context.get()).isNull();
        }

        @Test
        @DisplayName("an exception from the body propagates and the thread-local is still cleared")
        void exceptionStillCleans() {
            ThreadLocal<String> context = new ThreadLocal<>();
            assertThatThrownBy(() -> tasks.runWithThreadLocal(context, "u1", () -> {
                throw new IllegalStateException("boom");
            })).isInstanceOf(IllegalStateException.class).hasMessage("boom");
            assertThat(context.get()).isNull();
        }

        @Test
        @DisplayName("an initial value defined with withInitial is restored, proving remove() was used")
        void removeNotSetNull() {
            ThreadLocal<String> context = ThreadLocal.withInitial(() -> "default");
            tasks.runWithThreadLocal(context, "u1", () -> "ignored");
            assertThat(context.get()).isEqualTo("default");
        }

        @Test
        @DisplayName("the body's return value is returned unchanged")
        void returnsBodyResult() {
            ThreadLocal<Integer> context = new ThreadLocal<>();
            Integer result = tasks.runWithThreadLocal(context, 20, () -> context.get() + 22);
            assertThat(result).isEqualTo(42);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement evictStale()")
    @DisplayName("evictStale")
    class EvictStaleTest {

        @Test
        @DisplayName("{a=100, b=900, c=500}, now=1000, ttl=300 → 2, map {b=900}")
        void example() {
            Map<String, Long> map = new LinkedHashMap<>();
            map.put("a", 100L);
            map.put("b", 900L);
            map.put("c", 500L);
            assertThat(tasks.evictStale(map, 1000, 300)).isEqualTo(2);
            assertThat(map).containsOnlyKeys("b").containsEntry("b", 900L);
        }

        @Test
        @DisplayName("age exactly equal to the ttl is kept")
        void boundary() {
            Map<String, Long> map = new HashMap<>();
            map.put("a", 700L);
            assertThat(tasks.evictStale(map, 1000, 300)).isZero();
            assertThat(map).containsKey("a");
        }

        @Test
        @DisplayName("an empty map → 0")
        void empty() {
            assertThat(tasks.evictStale(new HashMap<String, Long>(), 1000, 300)).isZero();
        }

        @Test
        @DisplayName("everything stale → map becomes empty")
        void allStale() {
            Map<String, Long> map = new HashMap<>();
            map.put("a", 1L);
            map.put("b", 2L);
            assertThat(tasks.evictStale(map, 10_000, 100)).isEqualTo(2);
            assertThat(map).isEmpty();
        }

        @Test
        @DisplayName("the same map instance is modified, not a copy")
        void inPlace() {
            Map<String, Long> map = new HashMap<>();
            map.put("old", 0L);
            map.put("new", 999L);
            Map<String, Long> alias = map;
            tasks.evictStale(map, 1000, 100);
            assertThat(alias).isSameAs(map).containsOnlyKeys("new");
        }
    }
}

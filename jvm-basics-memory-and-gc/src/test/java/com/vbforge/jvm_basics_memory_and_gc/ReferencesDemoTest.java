package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReferencesDemo — API contracts")
class ReferencesDemoTest {

    @Nested
    @DisplayName("1. Strong references")
    class Strong {

        @Test
        @DisplayName("a weak reference does not die while a strong reference exists")
        void weakSurvivesWhileStrong() {
            assertThat(ReferencesDemo.weakSurvivesWhileStronglyHeld()).isTrue();
        }
    }

    @Nested
    @DisplayName("2. Weak references")
    class Weak {

        @Test
        @DisplayName("a weak reference is cleared by the GC once the object is unreachable")
        void clearedAfterGc() {
            assertThat(ReferencesDemo.weakClearedAfterGc()).isTrue();
        }

        @Test
        @DisplayName("readOnce returns the value while it is alive")
        void readOnceAlive() {
            assertThat(ReferencesDemo.readOnce(new WeakReference<>("value"))).isEqualTo("value");
        }

        @Test
        @DisplayName("readOnce returns the fallback after the reference is cleared")
        void readOnceCleared() {
            WeakReference<String> ref = new WeakReference<>("value");
            ref.clear();
            assertThat(ReferencesDemo.readOnce(ref)).isEqualTo("gone");
        }
    }

    @Nested
    @DisplayName("3. Soft references")
    class Soft {

        @Test
        @DisplayName("a normal GC does not reclaim a fresh soft reference")
        void survivesNormalGc() {
            assertThat(ReferencesDemo.softSurvivesNormalGc()).isTrue();
        }

        @Test
        @DisplayName("SoftCache loads once and then serves from the cache")
        void cacheHit() {
            ReferencesDemo.SoftCache<String, String> cache = new ReferencesDemo.SoftCache<>();
            assertThat(cache.get("a", String::toUpperCase)).isEqualTo("A");
            assertThat(cache.get("a", String::toUpperCase)).isEqualTo("A");
            assertThat(cache.loads()).isEqualTo(1);
        }

        @Test
        @DisplayName("SoftCache transparently reloads after its references are cleared")
        void reloadAfterPressure() {
            ReferencesDemo.SoftCache<String, String> cache = new ReferencesDemo.SoftCache<>();
            cache.get("a", String::toUpperCase);
            cache.simulateMemoryPressure();
            assertThat(cache.get("a", String::toUpperCase)).isEqualTo("A");
            assertThat(cache.loads()).isEqualTo(2);
        }

        @Test
        @DisplayName("clear() makes get() return null — what the GC does under memory pressure")
        void clearEffect() {
            SoftReference<String> ref = new SoftReference<>("x");
            ref.clear();
            assertThat(ref.get()).isNull();
        }
    }

    @Nested
    @DisplayName("4. Phantom references")
    class Phantom {

        @Test
        @DisplayName("PhantomReference.get() always returns null")
        void alwaysNull() {
            assertThat(ReferencesDemo.phantomGetAlwaysNull()).isTrue();
        }

        @Test
        @DisplayName("the phantom reference is enqueued once its referent is collected")
        void enqueued() throws InterruptedException {
            assertThat(ReferencesDemo.phantomEnqueuedAfterGc()).isTrue();
        }
    }

    @Nested
    @DisplayName("5. WeakHashMap")
    class WeakMap {

        @Test
        @DisplayName("an entry disappears after its key becomes unreachable")
        void evicts() {
            int[] sizes = ReferencesDemo.weakHashMapEvictsUnreferencedKey();
            assertThat(sizes).containsExactly(1, 0);
        }

        @Test
        @DisplayName("a literal (interned) key is never evicted")
        void literalStays() {
            assertThat(ReferencesDemo.weakHashMapLiteralKeyStays()).isTrue();
        }
    }

    @Test
    @DisplayName("gcUntilCleared returns immediately for an already-cleared reference")
    void gcUntilClearedFastPath() {
        WeakReference<Object> ref = new WeakReference<>(new Object());
        ref.clear();
        assertThat(ReferencesDemo.gcUntilCleared(ref, 5)).isTrue();
    }
}

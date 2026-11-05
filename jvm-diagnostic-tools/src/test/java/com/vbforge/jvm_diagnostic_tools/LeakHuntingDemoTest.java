package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LeakHuntingDemo — API contracts")
class LeakHuntingDemoTest {

    @Nested
    @DisplayName("1. The static collection that only grows")
    class StaticCollection {

        @Test
        @DisplayName("an unbounded cache keeps every entry, a bounded one keeps its limit")
        void sizes() {
            assertThat(LeakHuntingDemo.fillCaches(10_000, 100)).containsExactly(10_000, 100);
        }

        @Test
        @DisplayName("a bounded cache below its limit loses nothing")
        void belowLimit() {
            assertThat(LeakHuntingDemo.fillCaches(50, 100)).containsExactly(50, 50);
        }

        @Test
        @DisplayName("the bounded cache evicts the least recently used entry")
        void evictsLeastRecentlyUsed() {
            LeakHuntingDemo.BoundedCache<String, Integer> cache = new LeakHuntingDemo.BoundedCache<>(2);
            cache.put("a", 1);
            cache.put("b", 2);
            cache.get("a");
            cache.put("c", 3);

            assertThat(cache.keySet()).containsExactly("a", "c");
        }

        @Test
        @DisplayName("an unbounded cache reports its size")
        void unboundedSize() {
            LeakHuntingDemo.UnboundedCache<String, String> cache = new LeakHuntingDemo.UnboundedCache<>();
            cache.put("k", "v");
            cache.put("k", "w");
            assertThat(cache.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("2. Keys without equals()/hashCode()")
    class MissingEquals {

        @Test
        @DisplayName("without equals every put adds an entry; with equals there is one")
        void entryCounts() {
            assertThat(LeakHuntingDemo.countEntriesForRepeatedKey(1_000)).containsExactly(1_000, 1);
        }

        @Test
        @DisplayName("a single repetition cannot show the difference")
        void singleRepeat() {
            assertThat(LeakHuntingDemo.countEntriesForRepeatedKey(1)).containsExactly(1, 1);
        }

        @Test
        @DisplayName("GoodKey equality is based on the id")
        void goodKeyEquality() {
            assertThat(new LeakHuntingDemo.GoodKey("x")).isEqualTo(new LeakHuntingDemo.GoodKey("x"))
                    .hasSameHashCodeAs(new LeakHuntingDemo.GoodKey("x"));
            assertThat(new LeakHuntingDemo.GoodKey("x")).isNotEqualTo(new LeakHuntingDemo.GoodKey("y"));
        }

        @Test
        @DisplayName("BadKey equality is identity")
        void badKeyIdentity() {
            assertThat(new LeakHuntingDemo.BadKey("x")).isNotEqualTo(new LeakHuntingDemo.BadKey("x"));
        }
    }

    @Nested
    @DisplayName("3. The lapsed listener")
    class LapsedListener {

        @Test
        @DisplayName("registered listeners stay until they are unregistered")
        void staysRegistered() {
            LeakHuntingDemo.ListenerRegistry registry = new LeakHuntingDemo.ListenerRegistry();
            registry.register(() -> { });
            registry.register(() -> { });
            assertThat(registry.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("unregister removes exactly that listener")
        void unregisterRemoves() {
            LeakHuntingDemo.ListenerRegistry registry = new LeakHuntingDemo.ListenerRegistry();
            Runnable keep = () -> { };
            Runnable drop = () -> { };
            registry.register(keep);
            registry.register(drop);

            assertThat(registry.unregister(drop)).isTrue();
            assertThat(registry.size()).isEqualTo(1);
            assertThat(registry.unregister(drop)).isFalse();
        }

        @Test
        @DisplayName("fire calls every registered listener")
        void fireCallsAll() {
            LeakHuntingDemo.ListenerRegistry registry = new LeakHuntingDemo.ListenerRegistry();
            AtomicInteger calls = new AtomicInteger();
            registry.register(calls::incrementAndGet);
            registry.register(calls::incrementAndGet);

            registry.fire();

            assertThat(calls.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("a listener that is never unregistered is still called later")
        void lapsedListenerStillRuns() {
            LeakHuntingDemo.ListenerRegistry registry = new LeakHuntingDemo.ListenerRegistry();
            AtomicInteger calls = new AtomicInteger();
            registry.register(calls::incrementAndGet);

            registry.fire();
            registry.fire();

            assertThat(calls.get()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("4. Measuring retention")
    class Retention {

        @Test
        @DisplayName("used-after-GC rises by about the retained size and falls after release")
        void retentionIsVisible() {
            long[] samples = LeakHuntingDemo.measureRetention(10);

            long before = samples[0];
            long whileHeld = samples[1];
            long afterRelease = samples[2];

            assertThat(whileHeld - before).isGreaterThan(5_000_000L);
            assertThat(whileHeld - afterRelease).isGreaterThan(5_000_000L);
        }

        @Test
        @DisplayName("usedAfterGc is positive")
        void usedPositive() {
            assertThat(LeakHuntingDemo.usedAfterGc()).isPositive();
        }
    }
}

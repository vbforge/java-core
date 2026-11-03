package com.vbforge.java_memory_model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DoubleCheckedLockingDemo — API contracts")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class DoubleCheckedLockingDemoTest {

    @Nested
    @DisplayName("1. Synchronized lazy")
    class SynchronizedLazyTest {

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("factory runs once and every thread gets the same instance")
        @CsvSource({"1", "2", "16", "64"})
        void createdOnce(int threads) {
            assertThat(DoubleCheckedLockingDemo.synchronizedLazyDemo(threads)).containsExactly(1, 1);
        }
    }

    @Nested
    @DisplayName("2. Double-checked locking")
    class DoubleCheckedLockingTest {

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("factory runs once and every thread gets the same instance")
        @CsvSource({"1", "2", "16", "64"})
        void createdOnce(int threads) {
            assertThat(DoubleCheckedLockingDemo.doubleCheckedLockingDemo(threads)).containsExactly(1, 1);
        }

        @Test
        @DisplayName("later calls return the cached instance, not a new one")
        void cachedAfterFirstCall() {
            Supplier<Object> lazy = new DoubleCheckedLockingDemo.DoubleCheckedLazy<>(Object::new);

            Object first = lazy.get();

            assertThat(lazy.get()).isSameAs(first);
        }
    }

    @Nested
    @DisplayName("3. Initialization-on-demand holder")
    class HolderIdiomTest {

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("singleton is constructed exactly once and shared by all threads")
        @CsvSource({"1", "16", "64"})
        void createdOnce(int threads) {
            assertThat(DoubleCheckedLockingDemo.holderIdiomDemo(threads)).containsExactly(1, 1);
        }

        @Test
        @DisplayName("getInstance() always returns the same object")
        void sameInstance() {
            assertThat(DoubleCheckedLockingDemo.HolderSingleton.getInstance())
                    .isSameAs(DoubleCheckedLockingDemo.HolderSingleton.getInstance());
        }
    }

    @Nested
    @DisplayName("4. Laziness")
    class LazinessTest {

        @Test
        @DisplayName("nothing is created before the first get(); one creation after two gets")
        void lazyUntilFirstGet() {
            assertThat(DoubleCheckedLockingDemo.lazinessDemo()).containsExactly(0, 1);
        }

        @Test
        @DisplayName("raceGet returns one slot per thread")
        void raceGetReturnsOneSlotPerThread() {
            AtomicInteger calls = new AtomicInteger();

            Object[] seen = DoubleCheckedLockingDemo.raceGet(calls::incrementAndGet, 8);

            assertThat(seen).hasSize(8);
            assertThat(calls.get()).isEqualTo(8);
        }
    }
}

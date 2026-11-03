package com.vbforge.java_memory_model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DoubleCheckedLockingTasks — your implementations")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class DoubleCheckedLockingTasksTest {

    private DoubleCheckedLockingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DoubleCheckedLockingTasks();
    }

    /** Releases all threads at once and collects what each {@code supplier.get()} returned. */
    private static Object[] race(Supplier<?> supplier, int threads) throws InterruptedException {
        Object[] seen = new Object[threads];
        CountDownLatch startGate = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int slot = i;
            workers[i] = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                seen[slot] = supplier.get();
            });
            workers[i].start();
        }
        startGate.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        return seen;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lazyWithSynchronized()")
    @DisplayName("lazyWithSynchronized")
    class LazyWithSynchronizedTest {

        @Test
        @DisplayName("repeated get() returns the same instance")
        void javadocExample() {
            Supplier<Object> lazy = tasks.lazyWithSynchronized(Object::new);

            assertThat(lazy.get()).isSameAs(lazy.get());
        }

        @Test
        @DisplayName("factory is not called before the first get()")
        void notCalledEagerly() {
            AtomicInteger calls = new AtomicInteger();

            tasks.lazyWithSynchronized(() -> {
                calls.incrementAndGet();
                return new Object();
            });

            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("factory is called exactly once for many sequential get() calls")
        void calledOnceSequentially() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Object> lazy = tasks.lazyWithSynchronized(() -> {
                calls.incrementAndGet();
                return new Object();
            });

            lazy.get();
            lazy.get();
            lazy.get();

            assertThat(calls.get()).isEqualTo(1);
        }

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("factory runs once and all racing threads share one instance")
        @CsvSource({"2", "16", "64"})
        void calledOnceUnderContention(int threads) throws InterruptedException {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Object> lazy = tasks.lazyWithSynchronized(() -> {
                calls.incrementAndGet();
                return new Object();
            });

            Object[] seen = race(lazy, threads);

            assertThat(calls.get()).isEqualTo(1);
            for (Object instance : seen) {
                assertThat(instance).isSameAs(seen[0]);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lazyWithDoubleCheckedLocking()")
    @DisplayName("lazyWithDoubleCheckedLocking")
    class LazyWithDoubleCheckedLockingTest {

        @Test
        @DisplayName("repeated get() returns the same instance")
        void javadocExample() {
            Supplier<Object> lazy = tasks.lazyWithDoubleCheckedLocking(Object::new);

            assertThat(lazy.get()).isSameAs(lazy.get());
        }

        @Test
        @DisplayName("factory is not called before the first get()")
        void notCalledEagerly() {
            AtomicInteger calls = new AtomicInteger();

            tasks.lazyWithDoubleCheckedLocking(() -> {
                calls.incrementAndGet();
                return new Object();
            });

            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("factory is called exactly once for many sequential get() calls")
        void calledOnceSequentially() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Object> lazy = tasks.lazyWithDoubleCheckedLocking(() -> {
                calls.incrementAndGet();
                return new Object();
            });

            lazy.get();
            lazy.get();
            lazy.get();

            assertThat(calls.get()).isEqualTo(1);
        }

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("factory runs once and all racing threads share one instance")
        @CsvSource({"2", "16", "64"})
        void calledOnceUnderContention(int threads) throws InterruptedException {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Object> lazy = tasks.lazyWithDoubleCheckedLocking(() -> {
                calls.incrementAndGet();
                return new Object();
            });

            Object[] seen = race(lazy, threads);

            assertThat(calls.get()).isEqualTo(1);
            for (Object instance : seen) {
                assertThat(instance).isSameAs(seen[0]);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lazyNullable()")
    @DisplayName("lazyNullable")
    class LazyNullableTest {

        @Test
        @DisplayName("null result is returned repeatedly without calling the factory again")
        void javadocExample() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<String> lazy = tasks.lazyNullable(() -> {
                calls.incrementAndGet();
                return null;
            });

            assertThat(lazy.get()).isNull();
            assertThat(lazy.get()).isNull();
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("factory is not called before the first get()")
        void notCalledEagerly() {
            AtomicInteger calls = new AtomicInteger();

            tasks.lazyNullable(() -> {
                calls.incrementAndGet();
                return null;
            });

            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("non-null results are cached as well")
        void nonNullResultIsCached() {
            Supplier<Object> lazy = tasks.lazyNullable(Object::new);

            assertThat(lazy.get()).isSameAs(lazy.get());
        }

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("factory returning null still runs once under contention")
        @CsvSource({"2", "16", "64"})
        void nullFactoryCalledOnceUnderContention(int threads) throws InterruptedException {
            AtomicInteger calls = new AtomicInteger();
            Supplier<Object> lazy = tasks.lazyNullable(() -> {
                calls.incrementAndGet();
                return null;
            });

            Object[] seen = race(lazy, threads);

            assertThat(calls.get()).isEqualTo(1);
            for (Object instance : seen) {
                assertThat(instance).isNull();
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement holderIdiomInstance()")
    @DisplayName("holderIdiomInstance")
    class HolderIdiomInstanceTest {

        @Test
        @DisplayName("two calls return the same instance")
        void javadocExample() {
            assertThat(tasks.holderIdiomInstance()).isNotNull();
            assertThat(tasks.holderIdiomInstance()).isSameAs(tasks.holderIdiomInstance());
        }

        @Test
        @DisplayName("different Tasks objects share the same instance")
        void sharedAcrossTasksObjects() {
            assertThat(new DoubleCheckedLockingTasks().holderIdiomInstance())
                    .isSameAs(new DoubleCheckedLockingTasks().holderIdiomInstance());
        }

        @ParameterizedTest(name = "{0} racing threads")
        @DisplayName("all racing threads receive the same instance")
        @CsvSource({"2", "16", "64"})
        void sameInstanceUnderContention(int threads) throws InterruptedException {
            Object[] seen = race(tasks::holderIdiomInstance, threads);

            for (Object instance : seen) {
                assertThat(instance).isSameAs(seen[0]);
            }
        }
    }
}

package com.vbforge.design_patterns.singleton_pattern;

import com.vbforge.design_patterns.singleton_pattern.SingletonTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SingletonTasks — your implementations")
class SingletonTasksTest {

    private SingletonTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new SingletonTasks();
    }

    // ── Task 1: double-checked locking ────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement getDoubleCheckedSingleton()")
    @DisplayName("Task 1 — getDoubleCheckedSingleton()")
    class DoubleCheckedTest {

        @Test
        @DisplayName("returns the same instance on every call")
        void sameInstance() {
            assertThat(tasks.getDoubleCheckedSingleton())
                    .isSameAs(tasks.getDoubleCheckedSingleton());
        }

        @Test
        @DisplayName("two references share counter state")
        void sharedState() {
            SingletonTasks.Counter a = tasks.getDoubleCheckedSingleton();
            SingletonTasks.Counter b = tasks.getDoubleCheckedSingleton();
            a.increment();
            a.increment();
            assertThat(b.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("concurrent access yields one instance and no lost increments")
        void concurrency() throws Exception {
            int threads = 20;
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            List<Callable<SingletonTasks.Counter>> jobs = new ArrayList<>();
            for (int i = 0; i < threads; i++) jobs.add(tasks::getDoubleCheckedSingleton);

            List<Future<SingletonTasks.Counter>> results = pool.invokeAll(jobs);
            SingletonTasks.Counter first = results.get(0).get();
            for (Future<SingletonTasks.Counter> f : results) {
                assertThat(f.get()).isSameAs(first);
            }
            pool.shutdown();
        }
    }

    // ── Task 2: holder idiom ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement getHolderSingleton()")
    @DisplayName("Task 2 — getHolderSingleton()")
    class HolderTest {

        @Test
        @DisplayName("returns the same instance on every call")
        void sameInstance() {
            assertThat(tasks.getHolderSingleton())
                    .isSameAs(tasks.getHolderSingleton());
        }

        @Test
        @DisplayName("state is shared across returned references")
        void sharedState() {
            SingletonTasks.Counter a = tasks.getHolderSingleton();
            SingletonTasks.Counter b = tasks.getHolderSingleton();
            a.increment();
            assertThat(b.get()).isEqualTo(1);
        }
    }

    // ── Task 3: enum singleton ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement getEnumSingleton()")
    @DisplayName("Task 3 — getEnumSingleton()")
    class EnumTest {

        @Test
        @DisplayName("returns the same instance on every call")
        void sameInstance() {
            assertThat(tasks.getEnumSingleton())
                    .isSameAs(tasks.getEnumSingleton());
        }

        @Test
        @DisplayName("returned object is an enum constant")
        void isEnum() {
            assertThat(tasks.getEnumSingleton().getClass().isEnum()).isTrue();
        }

        @Test
        @DisplayName("increment returns the new value")
        void incrementReturnsNew() {
            SingletonTasks.Counter c = tasks.getEnumSingleton();
            assertThat(c.increment()).isPositive();
        }
    }
}
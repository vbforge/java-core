package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ThreadSafetyTasks — your implementations")
class ThreadSafetyTasksTest {

    private ThreadSafetyTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ThreadSafetyTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement incrementIfBelow()")
    @DisplayName("incrementIfBelow")
    class IncrementIfBelowTest {

        @Test
        @DisplayName("Below the limit → true and incremented")
        void belowLimit() {
            AtomicInteger c = new AtomicInteger(3);
            assertThat(tasks.incrementIfBelow(c, 5)).isTrue();
            assertThat(c.get()).isEqualTo(4);
        }

        @Test
        @DisplayName("At the limit → false and unchanged")
        void atLimit() {
            AtomicInteger c = new AtomicInteger(5);
            assertThat(tasks.incrementIfBelow(c, 5)).isFalse();
            assertThat(c.get()).isEqualTo(5);
        }

        @Test
        @DisplayName("Above the limit → false and unchanged")
        void aboveLimit() {
            AtomicInteger c = new AtomicInteger(9);
            assertThat(tasks.incrementIfBelow(c, 5)).isFalse();
            assertThat(c.get()).isEqualTo(9);
        }

        @Test
        @DisplayName("8 threads × 1000 calls with limit 1000 → exactly 1000 successes, counter = 1000")
        void concurrent() throws InterruptedException {
            AtomicInteger c = new AtomicInteger();
            AtomicInteger successes = new AtomicInteger();
            SynchronizationDemo.hammer(8, 1_000, () -> {
                if (tasks.incrementIfBelow(c, 1_000)) {
                    successes.incrementAndGet();
                }
            });
            assertThat(c.get()).isEqualTo(1_000);
            assertThat(successes.get()).isEqualTo(1_000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement perThread()")
    @DisplayName("perThread")
    class PerThreadTest {

        @Test
        @DisplayName("Same thread gets the same value on repeated get()")
        void sameThread() {
            ThreadLocal<StringBuilder> tl = tasks.perThread(StringBuilder::new);
            assertThat(tl.get()).isNotNull().isSameAs(tl.get());
        }

        @Test
        @DisplayName("Another thread gets a different value")
        void otherThread() throws InterruptedException {
            ThreadLocal<StringBuilder> tl = tasks.perThread(StringBuilder::new);
            StringBuilder mine = tl.get();
            AtomicReference<StringBuilder> other = new AtomicReference<>();
            Thread t = new Thread(() -> other.set(tl.get()));
            t.start();
            t.join();
            assertThat(other.get()).isNotNull().isNotSameAs(mine);
        }

        @Test
        @DisplayName("Factory runs once per thread and never before the first get()")
        void factoryCalledLazily() throws InterruptedException {
            AtomicInteger calls = new AtomicInteger();
            ThreadLocal<Object> tl = tasks.perThread(() -> {
                calls.incrementAndGet();
                return new Object();
            });
            assertThat(calls.get()).isZero();
            tl.get();
            tl.get();
            assertThat(calls.get()).isEqualTo(1);
            Thread t = new Thread(tl::get);
            t.start();
            t.join();
            assertThat(calls.get()).isEqualTo(2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement unmodifiableCopy()")
    @DisplayName("unmodifiableCopy")
    class UnmodifiableCopyTest {

        @Test
        @DisplayName("[1, 2, 3] → equal content")
        void sameContent() {
            assertThat(tasks.unmodifiableCopy(Arrays.asList(1, 2, 3))).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Later changes to the source do not show up in the copy")
        void independentOfSource() {
            List<Integer> source = new ArrayList<>(Arrays.asList(1, 2, 3));
            List<Integer> copy = tasks.unmodifiableCopy(source);
            source.add(4);
            source.set(0, 99);
            assertThat(copy).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("The copy rejects modification")
        void readOnly() {
            List<Integer> copy = tasks.unmodifiableCopy(new ArrayList<>(Arrays.asList(1, 2, 3)));
            assertThatThrownBy(() -> copy.add(9)).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> copy.remove(0)).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> copy.set(0, 7)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Empty list → empty list")
        void empty() {
            assertThat(tasks.unmodifiableCopy(Collections.<Integer>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("The result is a new object, not the source")
        void notSame() {
            List<Integer> source = new ArrayList<>(Arrays.asList(1));
            assertThat(tasks.unmodifiableCopy(source)).isNotSameAs(source);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getOrCreate()")
    @DisplayName("getOrCreate")
    class GetOrCreateTest {

        @Test
        @DisplayName("Empty ref → creates, stores and returns the value")
        void creates() {
            AtomicReference<Object> ref = new AtomicReference<>();
            Object created = new Object();
            assertThat(tasks.getOrCreate(ref, () -> created)).isSameAs(created);
            assertThat(ref.get()).isSameAs(created);
        }

        @Test
        @DisplayName("Filled ref → returns the existing value and never calls the factory")
        void existing() {
            Object existing = new Object();
            AtomicReference<Object> ref = new AtomicReference<>(existing);
            AtomicInteger calls = new AtomicInteger();
            Object result = tasks.getOrCreate(ref, () -> {
                calls.incrementAndGet();
                return new Object();
            });
            assertThat(result).isSameAs(existing);
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("16 threads released together → factory runs once, all get the same object")
        void concurrent() throws InterruptedException {
            int threads = 16;
            AtomicReference<Object> ref = new AtomicReference<>();
            AtomicInteger calls = new AtomicInteger();
            CyclicBarrier barrier = new CyclicBarrier(threads);
            Object[] seen = new Object[threads];
            Thread[] workers = new Thread[threads];
            for (int i = 0; i < threads; i++) {
                final int slot = i;
                workers[i] = new Thread(() -> {
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                    seen[slot] = tasks.getOrCreate(ref, () -> {
                        calls.incrementAndGet();
                        try {
                            Thread.sleep(5); // widen the race window
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return new Object();
                    });
                });
                workers[i].start();
            }
            for (Thread w : workers) {
                w.join();
            }
            assertThat(calls.get()).isEqualTo(1);
            assertThat(seen).doesNotContainNull();
            for (Object o : seen) {
                assertThat(o).isSameAs(seen[0]);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumConcurrently()")
    @DisplayName("sumConcurrently")
    class SumConcurrentlyTest {

        @Test
        @DisplayName("[1..6] on 3 threads → 21")
        void happyPath() throws InterruptedException {
            assertThat(tasks.sumConcurrently(new int[]{1, 2, 3, 4, 5, 6}, 3)).isEqualTo(21);
        }

        @Test
        @DisplayName("More threads than elements → still correct")
        void moreThreadsThanElements() throws InterruptedException {
            assertThat(tasks.sumConcurrently(new int[]{1, 2, 3}, 8)).isEqualTo(6);
        }

        @Test
        @DisplayName("Empty array → 0")
        void empty() throws InterruptedException {
            assertThat(tasks.sumConcurrently(new int[0], 4)).isZero();
        }

        @Test
        @DisplayName("threads < 1 → IllegalArgumentException")
        void invalidThreads() {
            assertThatThrownBy(() -> tasks.sumConcurrently(new int[]{1}, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Sum above Integer.MAX_VALUE does not overflow")
        void noOverflow() throws InterruptedException {
            int[] big = {Integer.MAX_VALUE, Integer.MAX_VALUE, 10};
            assertThat(tasks.sumConcurrently(big, 2)).isEqualTo(2L * Integer.MAX_VALUE + 10);
        }

        @ParameterizedTest(name = "length {0}, {1} threads → {2}")
        @CsvSource({
                "1,  1,  1",
                "10, 3,  55",
                "10, 10, 55",
                "100, 7, 5050",
                "1000, 4, 500500"
        })
        @DisplayName("Sum of 1..n for several sizes and thread counts")
        void parametrized(int length, int threads, long expected) throws InterruptedException {
            int[] numbers = new int[length];
            for (int i = 0; i < length; i++) {
                numbers[i] = i + 1;
            }
            assertThat(tasks.sumConcurrently(numbers, threads)).isEqualTo(expected);
        }
    }
}

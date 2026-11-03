package com.vbforge.multithreading_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ThreadSafetyDemo — API contracts")
class ThreadSafetyDemoTest {

    @Nested
    @DisplayName("1. Stateless code")
    class Stateless {

        @Test
        @DisplayName("factorial(10) is 3 628 800 on every thread")
        void sameResultOnAllThreads() throws InterruptedException {
            long[] results = new long[6];
            Thread[] threads = new Thread[results.length];
            for (int i = 0; i < threads.length; i++) {
                final int slot = i;
                threads[i] = new Thread(() -> results[slot] = ThreadSafetyDemo.factorial(10));
                threads[i].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            assertThat(results).containsOnly(3_628_800L);
        }

        @Test
        @DisplayName("factorial(0) and factorial(1) are 1")
        void edgeCases() {
            assertThat(ThreadSafetyDemo.factorial(0)).isEqualTo(1);
            assertThat(ThreadSafetyDemo.factorial(1)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("2. Immutability")
    class Immutability {

        @Test
        @DisplayName("withX() returns a new object and leaves the original unchanged")
        void withXCopies() {
            ThreadSafetyDemo.ImmutablePoint p = new ThreadSafetyDemo.ImmutablePoint(1, 2);
            ThreadSafetyDemo.ImmutablePoint moved = p.withX(10);
            assertThat(moved).isNotSameAs(p);
            assertThat(p.getX()).isEqualTo(1);
            assertThat(moved.getX()).isEqualTo(10);
            assertThat(moved.getY()).isEqualTo(2);
        }

        @Test
        @DisplayName("The class and all its fields are final")
        void everythingFinal() {
            Class<?> type = ThreadSafetyDemo.ImmutablePoint.class;
            assertThat(Modifier.isFinal(type.getModifiers())).isTrue();
            for (Field f : type.getDeclaredFields()) {
                assertThat(Modifier.isFinal(f.getModifiers())).as("field " + f.getName()).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("3. Thread confinement with ThreadLocal")
    class ThreadConfinement {

        @Test
        @DisplayName("Same thread gets the same instance every time")
        void sameThreadSameInstance() {
            assertThat(ThreadSafetyDemo.FORMAT.get()).isSameAs(ThreadSafetyDemo.FORMAT.get());
        }

        @Test
        @DisplayName("Another thread gets its own, different instance")
        void otherThreadOwnInstance() throws InterruptedException {
            SimpleDateFormat mine = ThreadSafetyDemo.FORMAT.get();
            AtomicReference<SimpleDateFormat> other = new AtomicReference<>();
            Thread t = new Thread(() -> other.set(ThreadSafetyDemo.FORMAT.get()));
            t.start();
            t.join();
            assertThat(other.get()).isNotNull().isNotSameAs(mine);
        }

        @Test
        @DisplayName("remove() makes the next get() create a fresh instance")
        void removeResets() {
            SimpleDateFormat before = ThreadSafetyDemo.FORMAT.get();
            ThreadSafetyDemo.FORMAT.remove();
            assertThat(ThreadSafetyDemo.FORMAT.get()).isNotSameAs(before);
        }
    }

    @Nested
    @DisplayName("4. Atomic variables")
    class AtomicVariables {

        @Test
        @DisplayName("incrementAndGet() from 4 threads loses nothing")
        void exactTotal() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            SynchronizationDemo.hammer(4, 50_000, counter::incrementAndGet);
            assertThat(counter.get()).isEqualTo(200_000);
        }

        @Test
        @DisplayName("compareAndSet succeeds only when the expected value matches")
        void compareAndSetSemantics() {
            AtomicInteger counter = new AtomicInteger(5);
            assertThat(counter.compareAndSet(4, 9)).isFalse();
            assertThat(counter.get()).isEqualTo(5);
            assertThat(counter.compareAndSet(5, 9)).isTrue();
            assertThat(counter.get()).isEqualTo(9);
        }
    }

    @Nested
    @DisplayName("5. Compound actions")
    class CompoundActions {

        @Test
        @DisplayName("Safe version: counter ends exactly at the limit, exactly limit calls succeed")
        void safeNeverExceedsLimit() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            AtomicInteger successes = new AtomicInteger();
            SynchronizationDemo.hammer(8, 1_000, () -> {
                if (ThreadSafetyDemo.incrementIfBelowSafe(counter, 1_000)) {
                    successes.incrementAndGet();
                }
            });
            assertThat(counter.get()).isEqualTo(1_000);
            assertThat(successes.get()).isEqualTo(1_000);
        }

        @Test
        @DisplayName("Unsafe version reaches the limit but may overshoot it")
        void unsafeMayOvershoot() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger();
            SynchronizationDemo.hammer(8, 1_000, () -> ThreadSafetyDemo.incrementIfBelowUnsafe(counter, 1_000));
            assertThat(counter.get()).isGreaterThanOrEqualTo(1_000);
        }

        @Test
        @DisplayName("Both versions behave the same on a single thread")
        void singleThreadSame() {
            AtomicInteger a = new AtomicInteger(4);
            AtomicInteger b = new AtomicInteger(4);
            assertThat(ThreadSafetyDemo.incrementIfBelowUnsafe(a, 5)).isTrue();
            assertThat(ThreadSafetyDemo.incrementIfBelowSafe(b, 5)).isTrue();
            assertThat(ThreadSafetyDemo.incrementIfBelowUnsafe(a, 5)).isFalse();
            assertThat(ThreadSafetyDemo.incrementIfBelowSafe(b, 5)).isFalse();
            assertThat(a.get()).isEqualTo(5);
            assertThat(b.get()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("6. Lazy initialization")
    class LazyInit {

        @Test
        @DisplayName("Synchronized get(): 8 threads released together create exactly one instance")
        void exactlyOneInstance() throws Exception {
            ThreadSafetyDemo.LazyHolder holder = new ThreadSafetyDemo.LazyHolder();
            int threads = 8;
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
                    seen[slot] = holder.get();
                });
                workers[i].start();
            }
            for (Thread w : workers) {
                w.join();
            }
            assertThat(holder.getCreations()).isEqualTo(1);
            assertThat(seen).doesNotContainNull();
            for (Object o : seen) {
                assertThat(o).isSameAs(seen[0]);
            }
        }

        @Test
        @DisplayName("Repeated get() returns the same object")
        void sameObject() {
            ThreadSafetyDemo.LazyHolder holder = new ThreadSafetyDemo.LazyHolder();
            assertThat(holder.get()).isSameAs(holder.get());
            assertThat(holder.getCreations()).isEqualTo(1);
        }
    }
}

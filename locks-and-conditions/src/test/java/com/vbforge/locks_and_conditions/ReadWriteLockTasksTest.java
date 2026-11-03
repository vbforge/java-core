package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReadWriteLockTasks — your implementations")
class ReadWriteLockTasksTest {

    private ReadWriteLockTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ReadWriteLockTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getCached()")
    @DisplayName("getCached")
    class GetCachedTest {

        @Test
        @DisplayName("Existing key returns its value")
        void existingKey() {
            Map<String, Integer> cache = new HashMap<>();
            cache.put("a", 1);

            assertThat(tasks.getCached(new ReentrantReadWriteLock(), cache, "a")).isEqualTo(1);
        }

        @Test
        @DisplayName("Missing key returns null")
        void missingKey() {
            Map<String, Integer> cache = new HashMap<>();
            cache.put("a", 1);

            assertThat(tasks.getCached(new ReentrantReadWriteLock(), cache, "z")).isNull();
        }

        @Test
        @DisplayName("Empty cache returns null")
        void emptyCache() {
            assertThat(tasks.getCached(new ReentrantReadWriteLock(), new HashMap<String, Integer>(), "a")).isNull();
        }

        @Test
        @DisplayName("Read lock is released afterwards")
        void lockReleased() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            tasks.getCached(lock, new HashMap<String, Integer>(), "a");

            assertThat(lock.getReadLockCount()).isZero();
            assertThat(lock.isWriteLocked()).isFalse();
        }

        @Test
        @DisplayName("Does not need (or take) the write lock")
        void worksWhileOtherThreadReads() throws InterruptedException {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            Map<String, Integer> cache = new HashMap<>();
            cache.put("a", 1);
            CountDownLatch held = new CountDownLatch(1);
            CountDownLatch finish = new CountDownLatch(1);
            Thread reader = new Thread(() -> {
                lock.readLock().lock();
                try {
                    held.countDown();
                    finish.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.readLock().unlock();
                }
            });
            reader.start();
            held.await();

            Integer value = tasks.getCached(lock, cache, "a");

            finish.countDown();
            reader.join();
            assertThat(value).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement putCached()")
    @DisplayName("putCached")
    class PutCachedTest {

        @Test
        @DisplayName("Stores a new entry")
        void newEntry() {
            Map<String, Integer> cache = new HashMap<>();

            tasks.putCached(new ReentrantReadWriteLock(), cache, "a", 1);

            assertThat(cache).containsEntry("a", 1).hasSize(1);
        }

        @Test
        @DisplayName("Overwrites an existing entry")
        void overwrite() {
            Map<String, Integer> cache = new HashMap<>();
            cache.put("a", 1);

            tasks.putCached(new ReentrantReadWriteLock(), cache, "a", 2);

            assertThat(cache).containsEntry("a", 2).hasSize(1);
        }

        @Test
        @DisplayName("Write lock is held during the put and released afterwards")
        void lockReleased() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            tasks.putCached(lock, new HashMap<String, Integer>(), "a", 1);

            assertThat(lock.isWriteLocked()).isFalse();
            assertThat(lock.getReadLockCount()).isZero();
        }

        @Test
        @DisplayName("Concurrent writers lose no entries")
        void concurrentWriters() throws InterruptedException {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            Map<Integer, Integer> cache = new HashMap<>();
            Thread[] threads = new Thread[4];
            for (int t = 0; t < threads.length; t++) {
                final int offset = t * 500;
                threads[t] = new Thread(() -> {
                    for (int i = 0; i < 500; i++) {
                        tasks.putCached(lock, cache, offset + i, i);
                    }
                });
                threads[t].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }

            assertThat(cache).hasSize(2_000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement getOrCompute()")
    @DisplayName("getOrCompute")
    class GetOrComputeTest {

        @Test
        @DisplayName("Present key: value returned, loader not called")
        void presentKey() {
            Map<String, Integer> cache = new HashMap<>();
            cache.put("a", 1);
            AtomicInteger calls = new AtomicInteger();

            Integer value = tasks.getOrCompute(new ReentrantReadWriteLock(), cache, "a", () -> {
                calls.incrementAndGet();
                return 99;
            });

            assertThat(value).isEqualTo(1);
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("Missing key: loader computes, value stored and returned")
        void missingKey() {
            Map<String, Integer> cache = new HashMap<>();

            Integer value = tasks.getOrCompute(new ReentrantReadWriteLock(), cache, "b", () -> 7);

            assertThat(value).isEqualTo(7);
            assertThat(cache).containsEntry("b", 7);
        }

        @Test
        @DisplayName("Second call for the same key does not call the loader again")
        void secondCallUsesCache() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            Map<String, Integer> cache = new HashMap<>();
            AtomicInteger calls = new AtomicInteger();

            tasks.getOrCompute(lock, cache, "k", calls::incrementAndGet);
            tasks.getOrCompute(lock, cache, "k", calls::incrementAndGet);

            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("8 threads, same missing key: loader runs exactly once")
        void loaderRunsOnce() throws InterruptedException {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            Map<String, Integer> cache = new HashMap<>();
            AtomicInteger calls = new AtomicInteger();
            CountDownLatch start = new CountDownLatch(1);
            Thread[] threads = new Thread[8];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    tasks.getOrCompute(lock, cache, "k", calls::incrementAndGet);
                });
                threads[i].start();
            }
            start.countDown();
            for (Thread thread : threads) {
                thread.join();
            }

            assertThat(calls.get()).isEqualTo(1);
            assertThat(cache).hasSize(1);
        }

        @Test
        @DisplayName("Lock is completely free afterwards")
        void lockReleased() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            tasks.getOrCompute(lock, new HashMap<String, Integer>(), "k", () -> 1);

            assertThat(lock.isWriteLocked()).isFalse();
            assertThat(lock.getReadLockCount()).isZero();
        }

        @ParameterizedTest(name = "key \"{0}\" -> loader result {1}")
        @CsvSource({"alpha, 1", "beta, 2", "gamma, 3"})
        @DisplayName("Parametrised keys")
        void parametrized(String key, int loaded) {
            Map<String, Integer> cache = new HashMap<>();

            Integer value = tasks.getOrCompute(new ReentrantReadWriteLock(), cache, key, () -> loaded);

            assertThat(value).isEqualTo(loaded);
            assertThat(cache).containsEntry(key, loaded);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement canWriteWhileHoldingRead()")
    @DisplayName("canWriteWhileHoldingRead")
    class CanWriteWhileHoldingReadTest {

        @Test
        @DisplayName("Write lock cannot be obtained while holding the read lock")
        void noUpgrade() {
            assertThat(tasks.canWriteWhileHoldingRead(new ReentrantReadWriteLock())).isFalse();
        }

        @Test
        @DisplayName("Lock is completely free afterwards")
        void lockReleased() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            tasks.canWriteWhileHoldingRead(lock);

            assertThat(lock.getReadLockCount()).isZero();
            assertThat(lock.isWriteLocked()).isFalse();
        }

        @Test
        @DisplayName("Calling it twice works (nothing leaked the first time)")
        void repeatable() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            assertThat(tasks.canWriteWhileHoldingRead(lock)).isFalse();
            assertThat(tasks.canWriteWhileHoldingRead(lock)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement downgradeToReadLock()")
    @DisplayName("downgradeToReadLock")
    class DowngradeToReadLockTest {

        @Test
        @DisplayName("Downgrade leaves one read hold and no write hold")
        void downgradeWorks() {
            assertThat(tasks.downgradeToReadLock(new ReentrantReadWriteLock())).isTrue();
        }

        @Test
        @DisplayName("Lock is completely free afterwards")
        void lockReleased() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            tasks.downgradeToReadLock(lock);

            assertThat(lock.getReadLockCount()).isZero();
            assertThat(lock.isWriteLocked()).isFalse();
        }

        @Test
        @DisplayName("Calling it twice works (nothing leaked the first time)")
        void repeatable() {
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

            assertThat(tasks.downgradeToReadLock(lock)).isTrue();
            assertThat(tasks.downgradeToReadLock(lock)).isTrue();
        }
    }
}

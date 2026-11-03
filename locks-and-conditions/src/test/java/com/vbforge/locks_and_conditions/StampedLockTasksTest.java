package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.StampedLock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StampedLockTasks — your implementations")
class StampedLockTasksTest {

    private StampedLockTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StampedLockTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement writePoint()")
    @DisplayName("writePoint")
    class WritePointTest {

        @Test
        @DisplayName("Overwrites both coordinates")
        void javadocExample() {
            double[] point = {0, 0};

            tasks.writePoint(new StampedLock(), point, 3, 4);

            assertThat(point).containsExactly(3.0, 4.0);
        }

        @Test
        @DisplayName("Lock is free afterwards")
        void lockReleased() {
            StampedLock lock = new StampedLock();

            tasks.writePoint(lock, new double[]{0, 0}, 1, 2);

            assertThat(lock.isWriteLocked()).isFalse();
            assertThat(lock.isReadLocked()).isFalse();
        }

        @Test
        @DisplayName("Writes invalidate earlier optimistic stamps")
        void invalidatesStamps() {
            StampedLock lock = new StampedLock();
            long stamp = lock.tryOptimisticRead();

            tasks.writePoint(lock, new double[]{0, 0}, 1, 2);

            assertThat(lock.validate(stamp)).isFalse();
        }

        @Test
        @DisplayName("Writing the same values again still works")
        void repeatable() {
            StampedLock lock = new StampedLock();
            double[] point = {5, 5};

            tasks.writePoint(lock, point, 5, 5);
            tasks.writePoint(lock, point, 5, 5);

            assertThat(point).containsExactly(5.0, 5.0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement snapshotOptimistic()")
    @DisplayName("snapshotOptimistic")
    class SnapshotOptimisticTest {

        @Test
        @DisplayName("Returns a copy of the point")
        void javadocExample() {
            double[] point = {3, 4};

            double[] snapshot = tasks.snapshotOptimistic(new StampedLock(), point);

            assertThat(snapshot).containsExactly(3.0, 4.0);
            assertThat(snapshot).isNotSameAs(point);
        }

        @Test
        @DisplayName("Lock is free afterwards")
        void lockReleased() {
            StampedLock lock = new StampedLock();

            tasks.snapshotOptimistic(lock, new double[]{1, 2});

            assertThat(lock.isReadLocked()).isFalse();
            assertThat(lock.isWriteLocked()).isFalse();
        }

        @Test
        @DisplayName("Falls back to a read lock when a writer holds the lock")
        void worksAfterWriter() throws InterruptedException {
            StampedLock lock = new StampedLock();
            double[] point = {1, 1};
            long writeStamp = lock.writeLock();
            double[][] result = new double[1][];
            Thread reader = new Thread(() -> result[0] = tasks.snapshotOptimistic(lock, point));
            reader.start();
            while (reader.getState() != Thread.State.WAITING && reader.isAlive()) {
                Thread.yield();
            }
            point[0] = 9;
            point[1] = 9;
            lock.unlockWrite(writeStamp);
            reader.join();

            assertThat(result[0]).containsExactly(9.0, 9.0);
        }

        @Test
        @DisplayName("Never sees a half-written point while a writer runs")
        void neverTorn() throws InterruptedException {
            StampedLock lock = new StampedLock();
            double[] point = {0, 0};
            AtomicBoolean stop = new AtomicBoolean();
            Thread writer = new Thread(() -> {
                double v = 1;
                while (!stop.get()) {
                    long stamp = lock.writeLock();
                    try {
                        point[0] = v;
                        point[1] = v;
                    } finally {
                        lock.unlockWrite(stamp);
                    }
                    v++;
                }
            });
            writer.start();

            boolean torn = false;
            for (int i = 0; i < 20_000 && !torn; i++) {
                double[] s = tasks.snapshotOptimistic(lock, point);
                torn = s[0] != s[1];
            }
            stop.set(true);
            writer.join();

            assertThat(torn).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isStampStillValid()")
    @DisplayName("isStampStillValid")
    class IsStampStillValidTest {

        @ParameterizedTest(name = "writeInBetween={0} -> valid={1}")
        @CsvSource({"false, true", "true, false"})
        @DisplayName("Javadoc examples")
        void javadocExamples(boolean writeInBetween, boolean expected) {
            assertThat(tasks.isStampStillValid(new StampedLock(), writeInBetween)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Lock is free afterwards")
        void lockReleased() {
            StampedLock lock = new StampedLock();

            tasks.isStampStillValid(lock, true);

            assertThat(lock.isWriteLocked()).isFalse();
            assertThat(lock.isReadLocked()).isFalse();
        }

        @Test
        @DisplayName("Works repeatedly on the same lock")
        void repeatable() {
            StampedLock lock = new StampedLock();

            assertThat(tasks.isStampStillValid(lock, true)).isFalse();
            assertThat(tasks.isStampStillValid(lock, false)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement upgradeSoleReader()")
    @DisplayName("upgradeSoleReader")
    class UpgradeSoleReaderTest {

        @Test
        @DisplayName("Sole reader: conversion succeeds")
        void soleReader() {
            StampedLock lock = new StampedLock();

            assertThat(tasks.upgradeSoleReader(lock)).isTrue();
            assertThat(lock.isReadLocked()).isFalse();
            assertThat(lock.isWriteLocked()).isFalse();
        }

        @Test
        @DisplayName("Another reader present: conversion fails but nothing leaks")
        void sharedReader() throws InterruptedException {
            StampedLock lock = new StampedLock();
            CountDownLatch held = new CountDownLatch(1);
            CountDownLatch finish = new CountDownLatch(1);
            Thread other = new Thread(() -> {
                long stamp = lock.readLock();
                try {
                    held.countDown();
                    finish.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockRead(stamp);
                }
            });
            other.start();
            held.await();

            boolean result = tasks.upgradeSoleReader(lock);
            int readersLeft = lock.getReadLockCount();

            finish.countDown();
            other.join();
            assertThat(result).isFalse();
            assertThat(readersLeft).isEqualTo(1);
            assertThat(lock.isReadLocked()).isFalse();
        }

        @Test
        @DisplayName("Can be called repeatedly")
        void repeatable() {
            StampedLock lock = new StampedLock();

            assertThat(tasks.upgradeSoleReader(lock)).isTrue();
            assertThat(tasks.upgradeSoleReader(lock)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement writeLockIsReentrant()")
    @DisplayName("writeLockIsReentrant")
    class WriteLockIsReentrantTest {

        @Test
        @DisplayName("Second write acquisition fails")
        void javadocExample() {
            assertThat(tasks.writeLockIsReentrant(new StampedLock())).isFalse();
        }

        @Test
        @DisplayName("Lock is completely free afterwards")
        void lockReleased() {
            StampedLock lock = new StampedLock();

            tasks.writeLockIsReentrant(lock);

            assertThat(lock.isWriteLocked()).isFalse();
            assertThat(lock.isReadLocked()).isFalse();
        }

        @Test
        @DisplayName("Can be called repeatedly")
        void repeatable() {
            StampedLock lock = new StampedLock();

            assertThat(tasks.writeLockIsReentrant(lock)).isFalse();
            assertThat(tasks.writeLockIsReentrant(lock)).isFalse();
        }
    }
}

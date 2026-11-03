package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.StampedLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StampedLockDemo — API contracts")
class StampedLockDemoTest {

    @Nested
    @DisplayName("1. Stamps and the optimistic-read pattern")
    class StampsAndOptimisticRead {

        @Test
        @DisplayName("New point is at the origin")
        void origin() {
            assertThat(new StampedLockDemo.StampedPoint().distanceFromOrigin()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("move(3, 4) puts the point at distance 5")
        void moveThenMeasure() {
            StampedLockDemo.StampedPoint p = new StampedLockDemo.StampedPoint();
            p.move(3, 4);
            assertThat(p.distanceFromOrigin()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Lock is free after reads and writes")
        void lockFree() {
            StampedLockDemo.StampedPoint p = new StampedLockDemo.StampedPoint();
            p.move(1, 1);
            p.distanceFromOrigin();
            assertThat(p.isLocked()).isFalse();
        }

        @Test
        @DisplayName("tryOptimisticRead does not lock anything")
        void optimisticReadLocksNothing() {
            StampedLock lock = new StampedLock();
            long stamp = lock.tryOptimisticRead();
            assertThat(stamp).isNotZero();
            assertThat(lock.isReadLocked()).isFalse();
            assertThat(lock.isWriteLocked()).isFalse();
        }

        @Test
        @DisplayName("tryOptimisticRead returns 0 while a writer holds the lock")
        void optimisticReadFailsDuringWrite() {
            StampedLock lock = new StampedLock();
            long writeStamp = lock.writeLock();
            try {
                assertThat(lock.tryOptimisticRead()).isZero();
            } finally {
                lock.unlockWrite(writeStamp);
            }
        }

        @Test
        @DisplayName("Concurrent moves lose no updates")
        void concurrentMoves() throws InterruptedException {
            StampedLockDemo.StampedPoint p = new StampedLockDemo.StampedPoint();
            Thread[] threads = new Thread[4];
            for (int t = 0; t < threads.length; t++) {
                threads[t] = new Thread(() -> {
                    for (int i = 0; i < 1_000; i++) {
                        p.move(1, 0);
                    }
                });
                threads[t].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            assertThat(p.distanceFromOrigin()).isEqualTo(4_000.0);
        }
    }

    @Nested
    @DisplayName("2. An optimistic stamp is invalidated by a writer")
    class Validation {

        @Test
        @DisplayName("No writer in between: stamp is valid")
        void noWriter() {
            assertThat(StampedLockDemo.optimisticStampStillValid(false)).isTrue();
        }

        @Test
        @DisplayName("Writer in between: stamp is invalid")
        void withWriter() {
            assertThat(StampedLockDemo.optimisticStampStillValid(true)).isFalse();
        }

        @Test
        @DisplayName("A read lock does not invalidate optimistic stamps")
        void readerDoesNotInvalidate() {
            StampedLock lock = new StampedLock();
            long stamp = lock.tryOptimisticRead();
            long readStamp = lock.readLock();
            lock.unlockRead(readStamp);
            assertThat(lock.validate(stamp)).isTrue();
        }
    }

    @Nested
    @DisplayName("3. Not reentrant")
    class NotReentrant {

        @Test
        @DisplayName("The owner cannot take the write lock a second time")
        void secondWriteRefused() {
            assertThat(StampedLockDemo.secondWriteLockObtained()).isFalse();
        }

        @Test
        @DisplayName("tryWriteLock returns 0 while the lock is held")
        void tryWriteReturnsZero() {
            StampedLock lock = new StampedLock();
            long stamp = lock.writeLock();
            try {
                assertThat(lock.tryWriteLock()).isZero();
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        @Test
        @DisplayName("Lock is free after the experiment")
        void lockFreeAfterwards() {
            StampedLockDemo.secondWriteLockObtained();
            StampedLock lock = new StampedLock();
            assertThat(lock.tryWriteLock()).isNotZero();
        }
    }

    @Nested
    @DisplayName("4. Conversion read -> write")
    class Conversion {

        @Test
        @DisplayName("Sole reader can convert to the write lock")
        void soleReader() throws InterruptedException {
            assertThat(StampedLockDemo.convertReadToWrite(false)).isTrue();
        }

        @Test
        @DisplayName("Reader sharing the lock cannot convert")
        void sharedReader() throws InterruptedException {
            assertThat(StampedLockDemo.convertReadToWrite(true)).isFalse();
        }

        @Test
        @DisplayName("Converted stamp is a write stamp")
        void convertedIsWriteStamp() {
            StampedLock lock = new StampedLock();
            long readStamp = lock.readLock();
            long writeStamp = lock.tryConvertToWriteLock(readStamp);
            try {
                assertThat(writeStamp).isNotZero();
                assertThat(lock.isWriteLocked()).isTrue();
                assertThat(lock.isReadLocked()).isFalse();
            } finally {
                lock.unlockWrite(writeStamp);
            }
        }
    }

    @Nested
    @DisplayName("5. Wrong stamp")
    class WrongStamp {

        @Test
        @DisplayName("Bogus stamp is rejected")
        void bogusStamp() {
            assertThat(StampedLockDemo.wrongStampRejected()).isTrue();
        }

        @Test
        @DisplayName("Unlocking a lock nobody holds throws")
        void unlockFreeLock() {
            StampedLock lock = new StampedLock();
            assertThatThrownBy(() -> lock.unlockWrite(lock.tryOptimisticRead()))
                    .isInstanceOf(IllegalMonitorStateException.class);
        }
    }
}

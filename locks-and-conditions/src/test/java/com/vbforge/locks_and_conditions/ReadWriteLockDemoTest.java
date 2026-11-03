package com.vbforge.locks_and_conditions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReadWriteLockDemo — API contracts")
class ReadWriteLockDemoTest {

    @Nested
    @DisplayName("1. Read-mostly cache")
    class Cache {

        @Test
        @DisplayName("Missing key returns null")
        void missingKey() {
            assertThat(new ReadWriteLockDemo.ReadWriteCache<String, Integer>().get("x")).isNull();
        }

        @Test
        @DisplayName("put then get returns the stored value")
        void putThenGet() {
            ReadWriteLockDemo.ReadWriteCache<String, Integer> cache = new ReadWriteLockDemo.ReadWriteCache<>();
            cache.put("a", 1);
            assertThat(cache.get("a")).isEqualTo(1);
        }

        @Test
        @DisplayName("put overwrites an existing value")
        void overwrite() {
            ReadWriteLockDemo.ReadWriteCache<String, Integer> cache = new ReadWriteLockDemo.ReadWriteCache<>();
            cache.put("a", 1);
            cache.put("a", 2);
            assertThat(cache.get("a")).isEqualTo(2);
            assertThat(cache.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("Concurrent writers on different keys lose nothing")
        void concurrentWriters() throws InterruptedException {
            ReadWriteLockDemo.ReadWriteCache<Integer, Integer> cache = new ReadWriteLockDemo.ReadWriteCache<>();
            Thread[] threads = new Thread[4];
            for (int t = 0; t < threads.length; t++) {
                final int offset = t * 250;
                threads[t] = new Thread(() -> {
                    for (int i = 0; i < 250; i++) {
                        cache.put(offset + i, i);
                    }
                });
                threads[t].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            assertThat(cache.size()).isEqualTo(1_000);
        }
    }

    @Nested
    @DisplayName("2. Readers share the lock")
    class ReadersShare {

        @Test
        @DisplayName("Two threads hold the read lock at the same time")
        void twoReaders() throws InterruptedException {
            assertThat(ReadWriteLockDemo.readersShareLock()).isEqualTo(2);
        }

        @Test
        @DisplayName("One thread can hold the read lock several times")
        void readIsReentrant() {
            ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
            rw.readLock().lock();
            rw.readLock().lock();
            try {
                assertThat(rw.getReadHoldCount()).isEqualTo(2);
                assertThat(rw.getReadLockCount()).isEqualTo(2);
            } finally {
                rw.readLock().unlock();
                rw.readLock().unlock();
            }
            assertThat(rw.getReadLockCount()).isZero();
        }
    }

    @Nested
    @DisplayName("3. A writer is exclusive")
    class WriterExclusive {

        @Test
        @DisplayName("Write lock is refused while another thread reads")
        void writeRefusedWhileReading() throws InterruptedException {
            assertThat(ReadWriteLockDemo.writeWhileOtherThreadReads()).isFalse();
        }

        @Test
        @DisplayName("Read lock is refused from another thread while a writer holds the lock")
        void readRefusedWhileWriting() throws InterruptedException {
            ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
            rw.writeLock().lock();
            boolean[] result = new boolean[]{true};
            try {
                Thread other = new Thread(() -> {
                    result[0] = rw.readLock().tryLock();
                    if (result[0]) {
                        rw.readLock().unlock();
                    }
                });
                other.start();
                other.join();
            } finally {
                rw.writeLock().unlock();
            }
            assertThat(result[0]).isFalse();
        }

        @Test
        @DisplayName("Write lock is reentrant for its owner")
        void writeIsReentrant() {
            ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
            rw.writeLock().lock();
            rw.writeLock().lock();
            try {
                assertThat(rw.getWriteHoldCount()).isEqualTo(2);
            } finally {
                rw.writeLock().unlock();
                rw.writeLock().unlock();
            }
            assertThat(rw.isWriteLocked()).isFalse();
        }
    }

    @Nested
    @DisplayName("4. Upgrade vs downgrade")
    class UpgradeDowngrade {

        @Test
        @DisplayName("Upgrading read -> write is not possible")
        void noUpgrade() {
            assertThat(ReadWriteLockDemo.tryUpgrade()).isFalse();
        }

        @Test
        @DisplayName("Downgrading write -> read leaves exactly one read hold")
        void downgradeWorks() {
            assertThat(ReadWriteLockDemo.downgrade()).isTrue();
        }

        @Test
        @DisplayName("Another reader may join after a downgrade")
        void readersJoinAfterDowngrade() throws InterruptedException {
            ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
            rw.writeLock().lock();
            rw.readLock().lock();
            rw.writeLock().unlock();
            boolean[] joined = new boolean[1];
            try {
                Thread other = new Thread(() -> {
                    joined[0] = rw.readLock().tryLock();
                    if (joined[0]) {
                        rw.readLock().unlock();
                    }
                });
                other.start();
                other.join();
            } finally {
                rw.readLock().unlock();
            }
            assertThat(joined[0]).isTrue();
        }
    }
}

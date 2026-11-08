package com.vbforge.practice.read_write.practice_7_read_write_cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A tiny in-memory cache where reads are far more frequent than writes.
 * Many threads may read at the same time (shared read lock); a write is exclusive.
 */
public class ReadWriteCacheDemo {

    static class ReadWriteCache {
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        private String cachedValue = "initial";
        // Many readers run read() at once, so a plain int++ here would be a data race.
        private final AtomicInteger readCount = new AtomicInteger();

        /** Concurrent reads - many threads may execute this simultaneously. */
        public String read() {
            readLock.lock();
            try {
                readCount.incrementAndGet();
                return cachedValue;
            } finally {
                readLock.unlock();
            }
        }

        /** Exclusive write - no other reader or writer may proceed. */
        public void write(String newValue) {
            writeLock.lock();
            try {
                cachedValue = newValue;
            } finally {
                writeLock.unlock();
            }
        }

        public int getReadCount() {
            return readCount.get();
        }
    }

    public static void main(String[] args) {
        ReadWriteCache cache = new ReadWriteCache();
        System.out.println("read: " + cache.read());
        cache.write("updated");
        System.out.println("read after write: " + cache.read());
        System.out.println("reads so far: " + cache.getReadCount());
    }
}

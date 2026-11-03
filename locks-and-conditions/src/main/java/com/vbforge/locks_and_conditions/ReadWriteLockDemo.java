package com.vbforge.locks_and_conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@link ReentrantReadWriteLock} — many readers OR one writer.
 *
 * <p>Sections:
 * <ol>
 *   <li>A read-mostly cache guarded by a read/write lock</li>
 *   <li>Readers share the lock</li>
 *   <li>A writer is exclusive</li>
 *   <li>No upgrade (read → write), but downgrade (write → read) works</li>
 * </ol>
 */
public class ReadWriteLockDemo {

    // =====================================================================
    // 1. A read-mostly cache
    // =====================================================================

    /**
     * What: a {@code ReadWriteLock} holds a <i>pair</i> of locks. The read lock
     * can be held by any number of threads at once as long as nobody holds the
     * write lock. The write lock is exclusive: no readers and no other writers.
     *
     * <p>Why it matters: a plain mutex lets only one thread touch the data, even
     * when 99 % of the operations are reads that cannot hurt each other. The
     * read/write split lets those reads run in parallel.
     *
     * <p>Complexity:
     * <ul>
     *   <li>Acquire/release: O(1)</li>
     *   <li>Throughput: reads scale with the number of cores; writes serialise</li>
     * </ul>
     *
     * <p>Common gotcha: it only pays off for <b>read-heavy</b> data and
     * non-trivial critical sections. For short sections or frequent writes a
     * plain {@code ReentrantLock} is often faster, because the read lock still
     * performs an atomic update on shared state.
     */
    public static class ReadWriteCache<K, V> {
        private final Map<K, V> map = new HashMap<>();
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public V get(K key) {
            lock.readLock().lock();
            try {
                return map.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }

        public void put(K key, V value) {
            lock.writeLock().lock();
            try {
                map.put(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public int size() {
            lock.readLock().lock();
            try {
                return map.size();
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    static void cacheDemo() throws InterruptedException {
        System.out.println("== 1. Read-mostly cache ==");
        ReadWriteCache<String, Integer> cache = new ReadWriteCache<>();
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                cache.put("key" + i, i);
            }
        });
        writer.start();
        writer.join();
        System.out.println("size = " + cache.size() + ", key42 = " + cache.get("key42"));
    }

    // =====================================================================
    // 2. Readers share the lock
    // =====================================================================

    /**
     * What: two different threads can hold the read lock at the same moment.
     *
     * <p>Why it matters: this is the whole point of the class — proof that
     * readers do not block each other.
     *
     * <p>Complexity: O(1) to query {@code getReadLockCount()}.
     *
     * <p>Common gotcha: {@code getReadLockCount()} counts <i>all</i> read holds
     * of all threads; {@code getReadHoldCount()} counts only the calling thread.
     *
     * @return the number of read locks held while two threads were reading
     */
    static int readersShareLock() throws InterruptedException {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        CountDownLatch otherHolds = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(1);

        Thread otherReader = new Thread(() -> {
            rw.readLock().lock();
            try {
                otherHolds.countDown();
                finish.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rw.readLock().unlock();
            }
        });
        otherReader.start();
        otherHolds.await();

        rw.readLock().lock();                    // second reader: not blocked
        try {
            return rw.getReadLockCount();
        } finally {
            rw.readLock().unlock();
            finish.countDown();
            otherReader.join();
        }
    }

    static void readersShareDemo() throws InterruptedException {
        System.out.println("== 2. Readers share the lock ==");
        System.out.println("read locks held at once: " + readersShareLock());
    }

    // =====================================================================
    // 3. A writer is exclusive
    // =====================================================================

    /**
     * What: while any thread holds the read lock, no other thread can get the
     * write lock (and vice versa).
     *
     * <p>Why it matters: this is what keeps readers from seeing half-written data.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: a steady stream of overlapping readers can starve a
     * waiting writer on a non-fair lock. Use the fair constructor or keep read
     * sections short if writes must get through.
     *
     * @return {@code true} if the write lock could be taken while another
     *         thread was reading (it cannot — the answer is {@code false})
     */
    static boolean writeWhileOtherThreadReads() throws InterruptedException {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        CountDownLatch otherHolds = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(1);

        Thread reader = new Thread(() -> {
            rw.readLock().lock();
            try {
                otherHolds.countDown();
                finish.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rw.readLock().unlock();
            }
        });
        reader.start();
        otherHolds.await();

        boolean gotWriteLock = rw.writeLock().tryLock();
        if (gotWriteLock) {
            rw.writeLock().unlock();
        }
        finish.countDown();
        reader.join();
        return gotWriteLock;
    }

    static void writerExclusiveDemo() throws InterruptedException {
        System.out.println("== 3. A writer is exclusive ==");
        System.out.println("write lock while another thread reads: " + writeWhileOtherThreadReads());
    }

    // =====================================================================
    // 4. No upgrade, but downgrade
    // =====================================================================

    /**
     * What: a thread holding the <b>read</b> lock cannot upgrade to the write
     * lock. {@code writeLock().lock()} would wait for all readers — including
     * the caller itself — to leave, so the thread deadlocks with itself.
     * The opposite direction works: a writer may take the read lock while
     * still holding the write lock, then release the write lock. That is
     * <i>downgrading</i>.
     *
     * <p>Why it matters: downgrade lets you publish a fresh value and keep
     * reading it consistently without letting another writer sneak in between.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: never call {@code writeLock().lock()} while holding the
     * read lock. Release the read lock first (and re-check your condition), or
     * use {@code tryLock()} as shown here.
     *
     * @return {@code true} if the thread could get the write lock while holding
     *         the read lock (it cannot — the answer is {@code false})
     */
    static boolean tryUpgrade() {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        rw.readLock().lock();
        try {
            boolean upgraded = rw.writeLock().tryLock();
            if (upgraded) {
                rw.writeLock().unlock();
            }
            return upgraded;
        } finally {
            rw.readLock().unlock();
        }
    }

    /**
     * @return {@code true} if after downgrading the thread holds exactly one
     *         read lock and no write lock
     */
    static boolean downgrade() {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        rw.writeLock().lock();
        rw.readLock().lock();            // take read while still holding write
        rw.writeLock().unlock();         // now only the read lock remains
        try {
            return !rw.isWriteLocked() && rw.getReadHoldCount() == 1;
        } finally {
            rw.readLock().unlock();
        }
    }

    static void upgradeDowngradeDemo() {
        System.out.println("== 4. Upgrade vs downgrade ==");
        System.out.println("upgrade read -> write possible? " + tryUpgrade());
        System.out.println("downgrade write -> read possible? " + downgrade());
    }

    public static void main(String[] args) throws InterruptedException {
        cacheDemo();
        System.out.println("---");
        readersShareDemo();
        System.out.println("---");
        writerExclusiveDemo();
        System.out.println("---");
        upgradeDowngradeDemo();
    }
}

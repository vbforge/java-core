// IMPLEMENTATION
package com.vbforge.practice.practice_1_explicit_locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Demonstrates explicit locks from java.util.concurrent.locks — the more powerful
 * alternative to the intrinsic synchronized keyword.
 *
 * Why explicit locks over synchronized?
 *   synchronized limitations:
 *     - Cannot try to acquire without blocking (no tryLock)
 *     - Cannot interrupt a thread waiting for a lock
 *     - Cannot have multiple condition queues per lock
 *     - Always a single exclusive lock — no read/write split
 *
 * Lock hierarchy in java.util.concurrent.locks:
 *
 *   Lock (interface)
 *     └─ ReentrantLock          — exclusive, reentrant, mirrors synchronized + extras
 *
 *   ReadWriteLock (interface)
 *     └─ ReentrantReadWriteLock — split read (shared) / write (exclusive) lock
 *
 *   StampedLock                 — optimistic reads + read/write; NOT reentrant
 *
 * ReentrantLock extras over synchronized:
 *   tryLock()                — returns immediately with true/false
 *   tryLock(time, unit)      — waits up to timeout
 *   lockInterruptibly()      — throws InterruptedException if interrupted while waiting
 *   newCondition()           — creates a Condition bound to this lock
 *   isHeldByCurrentThread()  — introspection
 *   getQueueLength()         — number of threads waiting
 *   isFair()                 — true if constructed with fairness
 *
 * Condition (replaces wait/notifyAll):
 *   await()          — releases lock, suspends thread  (like Object.wait)
 *   signal()         — wakes one waiting thread        (like Object.notify)
 *   signalAll()      — wakes all waiting threads       (like Object.notifyAll)
 *   awaitNanos(n)    — timed wait
 *   Advantage: one ReentrantLock can have MULTIPLE Condition queues (notFull, notEmpty).
 *   This allows targeted signalling — wake only producers OR only consumers.
 *
 * ReentrantReadWriteLock:
 *   - Many threads may hold the read lock simultaneously (shared).
 *   - The write lock is exclusive — no readers or other writers allowed.
 *   - Read-heavy workloads get much higher throughput than a single exclusive lock.
 *   - Downgrade: write → read is allowed; upgrade: read → write is NOT.
 *
 * StampedLock:
 *   - Optimistic read: no lock acquired; validate stamp afterwards.
 *     If validation fails (a write happened), fall back to a full read lock.
 *   - Higher throughput for read-dominant workloads at the cost of complexity.
 *   - NOT reentrant — do not call from a method that already holds the lock.
 *
 * Golden rule for all explicit locks:
 *   ALWAYS release in a finally block — every single time.
 *
 *       lock.lock();
 *       try {
 *           // critical section
 *       } finally {
 *           lock.unlock();   // runs even if exception is thrown
 *       }
 */
public class LocksDemo {

    // ── 1. ReentrantLock — basic mutual exclusion ─────────────────────────────

    /**
     * Drop-in replacement for a synchronized counter.
     * The finally-unlock pattern is non-negotiable.
     */
    static class ReentrantCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count = 0;

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int get() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    // ── 2. tryLock — non-blocking acquisition attempt ─────────────────────────

    /**
     * tryLock() returns immediately.
     * Useful when a thread has useful work to do rather than blocking.
     */
    static class TryLockDemo {
        private final ReentrantLock lock = new ReentrantLock();
        private int resourceValue = 0;

        /**
         * Returns true and updates the value if lock is available immediately.
         * Returns false without blocking if another thread holds the lock.
         */
        public boolean tryUpdate(int newValue) {
            if (lock.tryLock()) {
                try {
                    resourceValue = newValue;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false;   // lock was busy — caller decides what to do
        }

        public int getValue() {
            lock.lock();
            try { return resourceValue; } finally { lock.unlock(); }
        }
    }

    // ── 3. Condition — replacing wait/notifyAll with targeted signalling ───────

    /**
     * Bounded queue re-implemented with ReentrantLock + two Condition queues.
     *
     * Key improvement over wait/notifyAll:
     *   - notFull  is signalled only to producers  → no spurious wake of other consumers
     *   - notEmpty is signalled only to consumers  → no spurious wake of other producers
     * This eliminates the thundering-herd problem inherent in notifyAll.
     */
    static class ConditionBoundedQueue<T> {
        private final Queue<T>        buffer;
        private final int             capacity;
        private final ReentrantLock   lock     = new ReentrantLock();
        private final Condition       notFull  = lock.newCondition();
        private final Condition       notEmpty = lock.newCondition();

        ConditionBoundedQueue(int capacity) {
            this.capacity = capacity;
            this.buffer   = new ArrayDeque<>(capacity);
        }

        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (buffer.size() == capacity) {
                    notFull.await();   // wait until a consumer removes an item
                }
                buffer.add(item);
                notEmpty.signal();     // wake exactly ONE waiting consumer
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (buffer.isEmpty()) {
                    notEmpty.await();  // wait until a producer adds an item
                }
                T item = buffer.poll();
                notFull.signal();      // wake exactly ONE waiting producer
                return item;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try { return buffer.size(); } finally { lock.unlock(); }
        }
    }

    // ── 4. ReentrantReadWriteLock — shared reads, exclusive writes ────────────

    /**
     * A simple in-memory cache where reads are far more frequent than writes.
     * Multiple threads may read concurrently; writes are exclusive.
     */
    static class ReadWriteCache {
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock  readLock  = rwLock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        private String cachedValue = "initial";
        private int    readCount   = 0;   // access only under readLock for demo

        /** Concurrent reads — many threads may execute this simultaneously. */
        public String read() {
            readLock.lock();
            try {
                readCount++;
                return cachedValue;
            } finally {
                readLock.unlock();
            }
        }

        /** Exclusive write — no other reader or writer may proceed. */
        public void write(String newValue) {
            writeLock.lock();
            try {
                cachedValue = newValue;
            } finally {
                writeLock.unlock();
            }
        }

        public int getReadCount() {
            readLock.lock();
            try { return readCount; } finally { readLock.unlock(); }
        }
    }

    // ── 5. StampedLock — optimistic reads ─────────────────────────────────────

    /**
     * A 2D point protected by StampedLock.
     *
     * Optimistic read workflow:
     *   1. tryOptimisticRead()  — get a stamp without acquiring any lock
     *   2. Read the fields
     *   3. validate(stamp)      — check no write happened during step 2
     *   4. If invalid           — fall back to a full read lock
     *
     * Best for: very read-heavy data where writes are rare.
     * The optimistic path has near-zero contention cost.
     */
    static class StampedPoint {
        private final StampedLock sl = new StampedLock();
        private double x;
        private double y;

        StampedPoint(double x, double y) { this.x = x; this.y = y; }

        public void move(double deltaX, double deltaY) {
            long stamp = sl.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                sl.unlockWrite(stamp);
            }
        }

        /**
         * Optimistic read — acquires no lock.
         * Falls back to a pessimistic read lock only if a write was concurrent.
         */
        public double distanceFromOrigin() {
            // --- optimistic path ---
            long stamp = sl.tryOptimisticRead();
            double currentX = x;
            double currentY = y;

            if (!sl.validate(stamp)) {
                // A write happened while we read — retry with full read lock
                stamp = sl.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    sl.unlockRead(stamp);
                }
            }
            return Math.hypot(currentX, currentY);
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Locks Demo ===\n");
        final int THREADS    = 5;
        final int INCREMENTS = 1_000;

        // --- 1. ReentrantLock basic ---
        System.out.println("-- ReentrantLock counter --");
        ReentrantCounter counter = new ReentrantCounter();
        java.util.List<Thread> threads = new java.util.ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) counter.increment();
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
        System.out.printf("Expected %d, got %d%n", THREADS * INCREMENTS, counter.get());

        // --- 2. tryLock ---
        System.out.println("\n-- tryLock --");
        TryLockDemo tryDemo = new TryLockDemo();
        System.out.println("tryUpdate(42): " + tryDemo.tryUpdate(42));
        System.out.println("value: " + tryDemo.getValue());

        // --- 3. Condition bounded queue ---
        System.out.println("\n-- Condition BoundedQueue (capacity=2) --");
        ConditionBoundedQueue<Integer> cbq = new ConditionBoundedQueue<>(2);
        Thread prod = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) { cbq.put(i); Thread.sleep(20); }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Producer");
        Thread cons = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    System.out.println("[Consumer] took: " + cbq.take());
                    Thread.sleep(60);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Consumer");
        prod.start(); cons.start(); prod.join(); cons.join();

        // --- 4. ReadWriteLock ---
        System.out.println("\n-- ReadWriteCache --");
        ReadWriteCache cache = new ReadWriteCache();
        System.out.println("read: " + cache.read());
        cache.write("updated");
        System.out.println("read after write: " + cache.read());

        // --- 5. StampedLock ---
        System.out.println("\n-- StampedLock point --");
        StampedPoint point = new StampedPoint(3.0, 4.0);
        System.out.printf("distance: %.1f (expected 5.0)%n", point.distanceFromOrigin());
        point.move(1.0, 0.0);
        System.out.printf("distance after move: %.2f%n", point.distanceFromOrigin());

        System.out.println("\nMain done.");
    }
}
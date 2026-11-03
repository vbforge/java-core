package com.vbforge.multithreading_basics;

import java.util.concurrent.CountDownLatch;

/**
 * Race conditions and intrinsic locks: synchronized methods and blocks,
 * instance vs class locks, reentrancy, and wait/notifyAll.
 *
 * Run: mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_basics.SynchronizationDemo"
 */
public class SynchronizationDemo {

    public static void main(String[] args) throws Exception {
        raceConditionDemo();
        System.out.println("---");
        synchronizedMethodDemo();
        System.out.println("---");
        synchronizedBlockDemo();
        System.out.println("---");
        instanceVsClassLockDemo();
        System.out.println("---");
        reentrancyDemo();
        System.out.println("---");
        waitNotifyDemo();
    }

    /**
     * Starts {@code threads} threads, each running {@code action} {@code times} times, and waits for all.
     * A start gate releases all threads at the same moment so they really overlap; without it
     * the first thread often finishes before the second one even starts, and the race hides.
     */
    public static void hammer(int threads, final int times, final Runnable action) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int n = 0; n < times; n++) {
                    action.run();
                }
            });
            workers[i].start();
        }
        startGate.countDown();
        for (Thread w : workers) {
            w.join();
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. Race condition
    //
    // What:  count++ looks like one step but is read → add → write. Two threads can
    //        read the same value and both write back value+1, so one update is LOST.
    // Why:   this is the root problem synchronization solves.
    // Gotcha: the result is non-deterministic: it is never MORE than expected, usually
    //         less, and occasionally exact — which is why such bugs survive testing.
    // ─────────────────────────────────────────────────────────────────────
    public static class UnsafeCounter {
        private int count;

        public void increment() {
            count++;
        }

        public int get() {
            return count;
        }
    }

    static void raceConditionDemo() throws InterruptedException {
        UnsafeCounter counter = new UnsafeCounter();
        hammer(4, 100_000, counter::increment);
        System.out.println("expected 400000, got " + counter.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. synchronized method
    //
    // What:  only one thread at a time can run ANY synchronized instance method of the
    //        same object: the lock is the object itself (this).
    // Why:   it makes the read-modify-write atomic AND makes the new value visible to the
    //        next thread that takes the same lock.
    // Complexity: O(1) per call, but contended calls are serialized; throughput does
    //        not scale with threads.
    // Gotcha: both reads and writes must use the same lock — a synchronized increment()
    //        next to an unsynchronized get() is still a bug.
    // ─────────────────────────────────────────────────────────────────────
    public static class SynchronizedCounter {
        private int count;

        public synchronized void increment() {
            count++;
        }

        public synchronized int get() {
            return count;
        }
    }

    static void synchronizedMethodDemo() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        hammer(4, 100_000, counter::increment);
        System.out.println("expected 400000, got " + counter.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. synchronized block with a private lock object
    //
    // What:  synchronized (lock) { ... } guards only the critical section.
    // Why:   a private final lock cannot be grabbed by outside code, while synchronized
    //        on `this` can (any caller may write synchronized (counter) { ... } and
    //        stall your object). Keep the locked region as small as possible.
    // ─────────────────────────────────────────────────────────────────────
    public static class LockObjectCounter {
        private final Object lock = new Object();
        private int count;

        public void increment() {
            synchronized (lock) {
                count++;
            }
        }

        public int get() {
            synchronized (lock) {
                return count;
            }
        }
    }

    static void synchronizedBlockDemo() throws InterruptedException {
        LockObjectCounter counter = new LockObjectCounter();
        hammer(4, 100_000, counter::increment);
        System.out.println("expected 400000, got " + counter.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. Which lock? instance vs class
    //
    // What:  synchronized instance method → lock on `this`.
    //        static synchronized method   → lock on the Class object (Holder.class).
    // Why:   they are different monitors, so they do NOT exclude each other.
    // Gotcha: guarding static state with an instance lock protects nothing between
    //         two different instances.
    // ─────────────────────────────────────────────────────────────────────
    public static class Holder {

        /** Takes the instance lock, signals {@code entered}, then keeps it until {@code release}. */
        public synchronized void holdInstanceLock(CountDownLatch entered, CountDownLatch release)
                throws InterruptedException {
            entered.countDown();
            release.await();
        }

        public static synchronized String staticMethod() {
            return "static-ok";
        }

        // ── 5. Reentrancy ──
        // A thread that already owns a monitor may enter it again; it does not deadlock
        // against itself. Without this, synchronized methods could not call each other.
        public synchronized int outer() {
            return inner() + 1;
        }

        public synchronized int inner() {
            return 41;
        }
    }

    static void instanceVsClassLockDemo() throws InterruptedException {
        final Holder holder = new Holder();
        final CountDownLatch entered = new CountDownLatch(1);
        final CountDownLatch release = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            try {
                holder.holdInstanceLock(entered, release);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
        t.start();
        entered.await();
        System.out.println("instance lock held by other thread, static call -> " + Holder.staticMethod());
        release.countDown();
        t.join();
    }

    static void reentrancyDemo() {
        System.out.println("outer() = " + new Holder().outer());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 6. wait / notifyAll — a one-slot mailbox
    //
    // What:  wait() releases the monitor and sleeps until another thread calls
    //        notify()/notifyAll() on the same object; then it re-acquires the monitor.
    // Why:   lets a thread wait for a CONDITION (slot full / slot empty) without spinning.
    // Gotcha: always call wait() in a while loop that re-checks the condition, and
    //        prefer notifyAll() unless you can prove one waiter is enough.
    // ─────────────────────────────────────────────────────────────────────
    public static class Mailbox {
        private String message;

        public synchronized void put(String value) throws InterruptedException {
            while (message != null) {
                wait();
            }
            message = value;
            notifyAll();
        }

        public synchronized String take() throws InterruptedException {
            while (message == null) {
                wait();
            }
            String value = message;
            message = null;
            notifyAll();
            return value;
        }
    }

    static void waitNotifyDemo() throws InterruptedException {
        final Mailbox box = new Mailbox();
        Thread consumer = new Thread(() -> {
            try {
                System.out.println("consumer got: " + box.take());
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        box.put("hello");
        consumer.join();
    }
}

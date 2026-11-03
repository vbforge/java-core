package com.vbforge.locks_and_conditions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link ReentrantLock} — the explicit alternative to {@code synchronized}.
 *
 * <p>Sections:
 * <ol>
 *   <li>lock / unlock with try-finally</li>
 *   <li>Reentrancy and hold count</li>
 *   <li>{@code tryLock()} — do not wait at all</li>
 *   <li>{@code tryLock(timeout)} — wait, but not forever</li>
 *   <li>{@code lockInterruptibly()} — waiting that can be cancelled</li>
 *   <li>Fair vs non-fair locks</li>
 * </ol>
 */
public class ReentrantLockDemo {

    // =====================================================================
    // 1. lock / unlock with try-finally
    // =====================================================================

    /**
     * What: {@code ReentrantLock} gives the same mutual exclusion as
     * {@code synchronized}, but lock and unlock are explicit method calls.
     *
     * <p>Why it matters: with {@code synchronized} the JVM releases the monitor
     * for you, even on exceptions. With a {@code Lock} <b>you</b> must release it.
     * The only safe pattern is:
     * <pre>
     * lock.lock();
     * try {
     *     // critical section
     * } finally {
     *     lock.unlock();   // always runs, even if the section throws
     * }
     * </pre>
     *
     * <p>Complexity:
     * <ul>
     *   <li>Uncontended lock/unlock: O(1), a couple of CAS operations</li>
     *   <li>Contended: threads park in a FIFO wait queue (AQS) — no busy spinning</li>
     * </ul>
     *
     * <p>Common gotcha: putting {@code lock.lock()} <i>inside</i> the {@code try}.
     * If {@code lock()} itself fails, {@code finally} would call {@code unlock()}
     * on a lock you never held and throw {@link IllegalMonitorStateException}.
     */
    public static class LockedCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private int value;

        public void increment() {
            lock.lock();
            try {
                value++;
            } finally {
                lock.unlock();
            }
        }

        public int get() {
            lock.lock();
            try {
                return value;
            } finally {
                lock.unlock();
            }
        }

        /** Throws on purpose, to prove the lock is released by {@code finally}. */
        public void failWhileLocked() {
            lock.lock();
            try {
                throw new IllegalStateException("boom");
            } finally {
                lock.unlock();
            }
        }

        public boolean isLocked() {
            return lock.isLocked();
        }
    }

    static void lockUnlockDemo() throws InterruptedException {
        System.out.println("== 1. lock / unlock with try-finally ==");
        LockedCounter counter = new LockedCounter();
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1_000; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("4 threads x 1000 increments = " + counter.get());

        try {
            counter.failWhileLocked();
        } catch (IllegalStateException expected) {
            System.out.println("exception thrown, lock still held? " + counter.isLocked());
        }
    }

    // =====================================================================
    // 2. Reentrancy and hold count
    // =====================================================================

    /**
     * What: the thread that owns a {@code ReentrantLock} can acquire it again
     * without blocking. The lock counts the acquisitions ("hold count") and only
     * becomes free when {@code unlock()} has been called the same number of times.
     *
     * <p>Why it matters: without reentrancy a method holding the lock could not
     * call another method that takes the same lock — it would deadlock itself.
     * {@code synchronized} is reentrant too; this is what makes both usable in
     * ordinary object-oriented code.
     *
     * <p>Complexity: O(1) per acquire/release.
     *
     * <p>Common gotcha: every {@code lock()} needs its own {@code unlock()}.
     * One missing unlock leaves the hold count above zero and the lock is never
     * free for other threads.
     */
    public static class HoldCountProbe {
        private final ReentrantLock lock = new ReentrantLock();

        /** Locks, calls {@link #inner()} and returns the hold count seen there. */
        public int outer() {
            lock.lock();
            try {
                return inner();
            } finally {
                lock.unlock();
            }
        }

        private int inner() {
            lock.lock();
            try {
                return lock.getHoldCount();
            } finally {
                lock.unlock();
            }
        }

        public int holdCountNow() {
            return lock.getHoldCount();
        }

        public boolean isLocked() {
            return lock.isLocked();
        }
    }

    static void reentrancyDemo() {
        System.out.println("== 2. Reentrancy and hold count ==");
        HoldCountProbe probe = new HoldCountProbe();
        System.out.println("hold count inside nested call: " + probe.outer());
        System.out.println("hold count afterwards: " + probe.holdCountNow());
        System.out.println("still locked? " + probe.isLocked());
    }

    // =====================================================================
    // 3. tryLock() — do not wait at all
    // =====================================================================

    /**
     * What: {@code tryLock()} acquires the lock only if it is free <i>right now</i>
     * and returns {@code true}; otherwise it returns {@code false} immediately.
     *
     * <p>Why it matters: {@code synchronized} can only block. {@code tryLock()}
     * lets a thread do something else (skip, retry later, report "busy") instead
     * of waiting. It is also the standard tool for avoiding deadlock.
     *
     * <p>Complexity: O(1), never parks.
     *
     * <p>Common gotcha: calling {@code unlock()} when {@code tryLock()} returned
     * {@code false}. Only unlock if you actually got the lock.
     *
     * @return {@code true} if the action ran
     */
    static boolean tryRun(ReentrantLock lock, Runnable action) {
        if (!lock.tryLock()) {
            return false;
        }
        try {
            action.run();
            return true;
        } finally {
            lock.unlock();
        }
    }

    static void tryLockDemo() throws InterruptedException {
        System.out.println("== 3. tryLock() ==");
        ReentrantLock lock = new ReentrantLock();
        System.out.println("free lock -> ran: " + tryRun(lock, () -> { }));

        LockHandoff gate = new LockHandoff();
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                gate.signalHeld();
                gate.awaitRelease();
            } finally {
                lock.unlock();
            }
        });
        holder.start();
        gate.awaitHeld();
        System.out.println("busy lock -> ran: " + tryRun(lock, () -> { }));
        gate.release();
        holder.join();
    }

    // =====================================================================
    // 4. tryLock(timeout) — wait, but not forever
    // =====================================================================

    /**
     * What: {@code tryLock(time, unit)} waits up to the timeout for the lock.
     * It returns {@code true} if acquired, {@code false} if the time ran out.
     * It also reacts to interruption (throws {@link InterruptedException}).
     *
     * <p>Why it matters: a thread blocked on {@code synchronized} waits
     * indefinitely. With a timeout you can give up, log, and recover — so a stuck
     * lock holder cannot freeze every other thread forever.
     *
     * <p>Complexity: O(1) work; the thread parks for at most the timeout.
     *
     * <p>Common gotcha: swallowing {@code InterruptedException}. Restore the
     * flag with {@code Thread.currentThread().interrupt()} when you cannot rethrow.
     */
    static boolean acquireWithin(ReentrantLock lock, long millis) {
        try {
            if (lock.tryLock(millis, TimeUnit.MILLISECONDS)) {
                lock.unlock();
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    static void tryLockTimeoutDemo() throws InterruptedException {
        System.out.println("== 4. tryLock(timeout) ==");
        ReentrantLock lock = new ReentrantLock();
        System.out.println("free lock, 50 ms -> " + acquireWithin(lock, 50));

        LockHandoff gate = new LockHandoff();
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                gate.signalHeld();
                gate.awaitRelease();
            } finally {
                lock.unlock();
            }
        });
        holder.start();
        gate.awaitHeld();
        System.out.println("busy lock, 50 ms -> " + acquireWithin(lock, 50));
        gate.release();
        holder.join();
    }

    // =====================================================================
    // 5. lockInterruptibly() — waiting that can be cancelled
    // =====================================================================

    /**
     * What: {@code lockInterruptibly()} blocks like {@code lock()}, but if the
     * waiting thread is interrupted it stops waiting and throws
     * {@link InterruptedException}.
     *
     * <p>Why it matters: a thread blocked in {@code synchronized} or in plain
     * {@code lock()} <b>cannot be cancelled</b> — {@code interrupt()} only sets a
     * flag. {@code lockInterruptibly()} makes lock waiting a proper cancellation
     * point, which is essential for clean shutdown.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: forgetting that plain {@code lock()} ignores interrupts
     * while waiting (the flag is set, but the thread stays blocked).
     *
     * @return {@code true} if the lock was obtained, {@code false} if cancelled
     */
    static boolean lockOrGiveUpWhenInterrupted(ReentrantLock lock) {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        lock.unlock();
        return true;
    }

    static void lockInterruptiblyDemo() throws InterruptedException {
        System.out.println("== 5. lockInterruptibly() ==");
        ReentrantLock lock = new ReentrantLock();
        lock.lock();                       // main holds the lock
        try {
            boolean[] result = new boolean[]{true};
            Thread waiter = new Thread(() -> result[0] = lockOrGiveUpWhenInterrupted(lock));
            waiter.start();
            while (!lock.hasQueuedThread(waiter)) {
                Thread.yield();            // wait until the waiter is really queued
            }
            waiter.interrupt();
            waiter.join();
            System.out.println("waiter obtained lock after interrupt? " + result[0]);
        } finally {
            lock.unlock();
        }
    }

    // =====================================================================
    // 6. Fair vs non-fair
    // =====================================================================

    /**
     * What: {@code new ReentrantLock(true)} creates a <i>fair</i> lock — the
     * longest-waiting thread gets it next. The default is non-fair, which allows
     * "barging": a newly arriving thread may grab the lock before queued ones.
     *
     * <p>Why it matters: fairness prevents starvation but costs throughput
     * (more context switches). Non-fair is usually several times faster, so it
     * is the default. Choose fair only when starvation is a real problem.
     *
     * <p>Complexity: same O(1) operations; fair locks pay extra park/unpark cost.
     *
     * <p>Common gotcha: the untimed {@code tryLock()} ignores fairness and may
     * barge even on a fair lock.
     */
    static void fairnessDemo() {
        System.out.println("== 6. Fair vs non-fair ==");
        System.out.println("default lock fair? " + new ReentrantLock().isFair());
        System.out.println("new ReentrantLock(true) fair? " + new ReentrantLock(true).isFair());
    }

    // =====================================================================
    // Tiny coordination helper (keeps demos deterministic, no sleeps)
    // =====================================================================

    /** Two-latch handshake: "I hold the lock" / "you may release it". */
    static class LockHandoff {
        private final CountDownLatch held = new CountDownLatch(1);
        private final CountDownLatch release = new CountDownLatch(1);

        void signalHeld() {
            held.countDown();
        }

        void awaitHeld() throws InterruptedException {
            held.await();
        }

        void awaitRelease() {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        void release() {
            release.countDown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        lockUnlockDemo();
        System.out.println("---");
        reentrancyDemo();
        System.out.println("---");
        tryLockDemo();
        System.out.println("---");
        tryLockTimeoutDemo();
        System.out.println("---");
        lockInterruptiblyDemo();
        System.out.println("---");
        fairnessDemo();
    }
}

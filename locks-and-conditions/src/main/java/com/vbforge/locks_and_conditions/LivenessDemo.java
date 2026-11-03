package com.vbforge.locks_and_conditions;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Liveness failures: deadlock, livelock and starvation.
 *
 * <p>Sections:
 * <ol>
 *   <li>Deadlock — a real one, detected with {@code ThreadMXBean}, then broken</li>
 *   <li>Fix 1: a global lock order</li>
 *   <li>Fix 2: {@code tryLock} with a timeout and back-off</li>
 *   <li>Livelock — everybody is busy, nobody progresses</li>
 *   <li>Starvation — and what a fair lock changes</li>
 * </ol>
 */
public class LivenessDemo {

    /** A bank account guarded by its own lock. Used by the fixes below and by the tasks. */
    public static class Account {
        private final long id;
        private final ReentrantLock lock = new ReentrantLock();
        private int balance;

        public Account(long id, int balance) {
            this.id = id;
            this.balance = balance;
        }

        public long getId() {
            return id;
        }

        /** The lock that guards {@link #balance}. */
        public ReentrantLock getLock() {
            return lock;
        }

        public int getBalance() {
            lock.lock();
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }

        /** Caller must hold {@link #getLock()}. */
        public void withdrawUnlocked(int amount) {
            balance -= amount;
        }

        /** Caller must hold {@link #getLock()}. */
        public void depositUnlocked(int amount) {
            balance += amount;
        }

        /** Caller must hold {@link #getLock()}. */
        public int balanceUnlocked() {
            return balance;
        }
    }

    // =====================================================================
    // 1. Deadlock
    // =====================================================================

    /**
     * What: a deadlock is a cycle of threads where each holds a lock the next one
     * needs: T1 holds A and waits for B, T2 holds B and waits for A. Nobody can
     * ever continue.
     *
     * <p>Why it matters: a deadlock does not throw and does not burn CPU — the
     * program just stops responding. All four Coffman conditions must hold:
     * mutual exclusion, hold-and-wait, no pre-emption, circular wait. Removing
     * <b>any one</b> prevents deadlock; the two fixes below remove circular wait
     * (lock ordering) and hold-and-wait (tryLock + back-off).
     *
     * <p>Complexity: detection with {@code ThreadMXBean} walks the wait-for graph,
     * roughly O(threads + locks).
     *
     * <p>Common gotcha: the deadlock only appears when the timing is unlucky, so it
     * passes in tests and fails in production. Here a latch forces the unlucky
     * interleaving every time. Threads use {@code lockInterruptibly()} so the
     * demo can break the cycle by interrupting them.
     */
    public static class DeadlockScenario {
        private final Thread first;
        private final Thread second;

        DeadlockScenario(Thread first, Thread second) {
            this.first = first;
            this.second = second;
        }

        public Thread getFirst() {
            return first;
        }

        public Thread getSecond() {
            return second;
        }

        /** Breaks the deadlock by interrupting both threads, then waits for them to finish. */
        public void resolve() throws InterruptedException {
            first.interrupt();
            second.interrupt();
            first.join();
            second.join();
        }
    }

    /**
     * Starts two threads that lock two locks in opposite order and returns once
     * the JVM reports them as deadlocked.
     *
     * @return the running (deadlocked) scenario — call {@link DeadlockScenario#resolve()}
     */
    public static DeadlockScenario startDeadlock() throws InterruptedException {
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();
        CountDownLatch holdsA = new CountDownLatch(1);
        CountDownLatch holdsB = new CountDownLatch(1);

        Thread t1 = new Thread(() -> lockInOrder(lockA, lockB, holdsA, holdsB), "deadlock-1");
        Thread t2 = new Thread(() -> lockInOrder(lockB, lockA, holdsB, holdsA), "deadlock-2");
        t1.setDaemon(true);
        t2.setDaemon(true);
        t1.start();
        t2.start();

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (!containsBoth(findDeadlockedThreadIds(), t1, t2)) {
            if (System.nanoTime() > deadline) {
                throw new IllegalStateException("deadlock was not detected in time");
            }
            Thread.sleep(5);
        }
        return new DeadlockScenario(t1, t2);
    }

    private static boolean containsBoth(long[] ids, Thread a, Thread b) {
        boolean foundA = false;
        boolean foundB = false;
        for (long id : ids) {
            foundA |= id == a.getId();
            foundB |= id == b.getId();
        }
        return foundA && foundB;
    }

    /** Takes {@code mine}, waits until the other thread holds its lock, then wants {@code other}. */
    private static void lockInOrder(ReentrantLock mine, ReentrantLock other,
                                    CountDownLatch iHoldMine, CountDownLatch theyHoldTheirs) {
        mine.lock();
        try {
            iHoldMine.countDown();
            theyHoldTheirs.await();           // both now hold one lock: the cycle is unavoidable
            other.lockInterruptibly();        // blocks forever — until interrupted
            other.unlock();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mine.unlock();
        }
    }

    /**
     * Asks the JVM for deadlocked threads — both monitors ({@code synchronized})
     * and ownable synchronizers ({@code ReentrantLock}).
     *
     * @return ids of deadlocked threads, empty if there are none
     */
    public static long[] findDeadlockedThreadIds() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] ids = bean.findDeadlockedThreads();
        return ids == null ? new long[0] : ids;
    }

    static void deadlockDemo() throws InterruptedException {
        System.out.println("== 1. Deadlock ==");
        System.out.println("deadlocked threads before: " + findDeadlockedThreadIds().length);
        DeadlockScenario scenario = startDeadlock();
        System.out.println("deadlocked threads detected: " + findDeadlockedThreadIds().length);
        scenario.resolve();
        System.out.println("deadlocked threads after interrupt: " + findDeadlockedThreadIds().length);
    }

    // =====================================================================
    // 2. Fix 1: lock ordering
    // =====================================================================

    /**
     * What: always acquire locks in one global order — here, by ascending account
     * id — no matter which direction the money flows.
     *
     * <p>Why it matters: with one order there can be no cycle in the wait-for
     * graph (circular wait is impossible), so no deadlock. It is the cheapest and
     * most reliable fix when the locks are known up front.
     *
     * <p>Complexity: O(1) extra work (one comparison).
     *
     * <p>Common gotcha: the order must be <b>total and stable</b>. Ordering by
     * {@code hashCode()} can tie; ordering by a mutable field can change mid-way.
     * Use a unique immutable id.
     *
     * @return {@code true} if the transfer happened, {@code false} if funds were insufficient
     */
    public static boolean transferOrdered(Account from, Account to, int amount) {
        Account firstLock = from.getId() < to.getId() ? from : to;
        Account secondLock = firstLock == from ? to : from;
        firstLock.getLock().lock();
        try {
            secondLock.getLock().lock();
            try {
                if (from.balanceUnlocked() < amount) {
                    return false;
                }
                from.withdrawUnlocked(amount);
                to.depositUnlocked(amount);
                return true;
            } finally {
                secondLock.getLock().unlock();
            }
        } finally {
            firstLock.getLock().unlock();
        }
    }

    static void lockOrderingDemo() throws InterruptedException {
        System.out.println("== 2. Lock ordering ==");
        Account a = new Account(1, 1_000);
        Account b = new Account(2, 1_000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5_000; i++) {
                transferOrdered(a, b, 1);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5_000; i++) {
                transferOrdered(b, a, 1);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("opposite transfers finished, total = " + (a.getBalance() + b.getBalance()));
    }

    // =====================================================================
    // 3. Fix 2: tryLock with timeout and back-off
    // =====================================================================

    /**
     * What: instead of blocking forever on the second lock, try it with a timeout.
     * If it fails, <b>release the first lock</b> and report failure so the caller
     * can retry later.
     *
     * <p>Why it matters: it removes hold-and-wait — a thread never sits on one lock
     * while indefinitely waiting for another. Useful when the lock order cannot be
     * known in advance (locks discovered at run time).
     *
     * <p>Complexity: O(1) per attempt; the wait is bounded by the timeout.
     *
     * <p>Common gotcha: two threads that both retry immediately in lock-step can
     * fail forever — that is livelock (next section). Add a growing, randomised
     * delay between attempts.
     *
     * @return {@code true} if the transfer happened, {@code false} if a lock could
     *         not be obtained in time or funds were insufficient
     */
    public static boolean transferWithTimeout(Account from, Account to, int amount, long timeoutMillis)
            throws InterruptedException {
        if (!from.getLock().tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
            return false;
        }
        try {
            if (!to.getLock().tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
                return false;                 // finally below releases 'from'
            }
            try {
                if (from.balanceUnlocked() < amount) {
                    return false;
                }
                from.withdrawUnlocked(amount);
                to.depositUnlocked(amount);
                return true;
            } finally {
                to.getLock().unlock();
            }
        } finally {
            from.getLock().unlock();
        }
    }

    static void tryLockDemo() throws InterruptedException {
        System.out.println("== 3. tryLock with timeout ==");
        Account a = new Account(1, 100);
        Account b = new Account(2, 100);
        System.out.println("free accounts -> " + transferWithTimeout(a, b, 40, 50));

        CountDownLatch held = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            b.getLock().lock();
            try {
                held.countDown();
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                b.getLock().unlock();
            }
        });
        holder.start();
        held.await();
        System.out.println("target locked by someone else -> " + transferWithTimeout(a, b, 10, 30));
        System.out.println("source released again? " + !a.getLock().isLocked());
        release.countDown();
        holder.join();
    }

    // =====================================================================
    // 4. Livelock
    // =====================================================================

    /**
     * What: a livelock is a deadlock's busy cousin. Threads are not blocked — they
     * keep reacting to each other and keep changing state — but no useful work gets
     * done. The classic picture: two people meet in a corridor, both step left, both
     * step right, forever.
     *
     * <p>Why it matters: unlike a deadlock it shows <b>100 % CPU and no progress</b>,
     * and {@code ThreadMXBean} reports nothing. Symmetric "polite" retry logic causes it.
     *
     * <p>Complexity: this simulation is O(rounds).
     *
     * <p>Common gotcha: retry-with-the-same-delay is symmetric. Break the symmetry:
     * exponential back-off (the delay grows each attempt, up to a cap) plus random
     * jitter, or a fixed priority so one side always wins.
     *
     * <p>The simulation is deterministic: each round both people choose a side.
     * They pass each other only when they choose <i>different</i> sides.
     *
     * @param sameRuleForBoth if {@code true} both always pick "left" (symmetry);
     *                        if {@code false} the second person picks "right" (priority rule)
     * @param maxRounds       give up after this many rounds
     * @return the round (starting at 1) in which they passed, or {@code -1} if they never did
     */
    static int roundsUntilPassed(boolean sameRuleForBoth, int maxRounds) {
        for (int round = 1; round <= maxRounds; round++) {
            boolean firstGoesLeft = true;
            boolean secondGoesLeft = sameRuleForBoth;
            if (firstGoesLeft != secondGoesLeft) {
                return round;
            }
        }
        return -1;
    }

    static void livelockDemo() {
        System.out.println("== 4. Livelock ==");
        System.out.println("symmetric rule, 1000 rounds -> " + roundsUntilPassed(true, 1_000));
        System.out.println("priority rule -> passed in round " + roundsUntilPassed(false, 1_000));
    }

    // =====================================================================
    // 5. Starvation and fairness
    // =====================================================================

    /**
     * What: starvation means a thread is ready to run but never gets the resource,
     * because others keep winning. A non-fair lock lets a newly arriving thread
     * "barge in" ahead of waiting ones. A <b>fair</b> lock
     * ({@code new ReentrantLock(true)}) hands the lock to the longest-waiting thread.
     *
     * <p>Why it matters: starvation is invisible in tests with light load and shows
     * up as unexplained latency spikes under heavy contention.
     *
     * <p>Complexity: O(1) per operation; fairness costs throughput (more parking).
     *
     * <p>Common gotcha: fairness only covers <i>waiting</i> threads. The untimed
     * {@code tryLock()} ignores the queue and may still barge. Thread priorities do
     * not fix starvation either.
     *
     * <p>Deterministic proof: the main thread holds a fair lock; {@code count}
     * threads queue up <i>one at a time</i>; when the lock is released they get it
     * in exactly the order they queued.
     *
     * @return the order (by queue position, starting at 0) in which the threads acquired the lock
     */
    static List<Integer> fairLockAcquisitionOrder(int count) throws InterruptedException {
        ReentrantLock fair = new ReentrantLock(true);
        List<Integer> order = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        fair.lock();
        try {
            for (int i = 0; i < count; i++) {
                final int position = i;
                Thread t = new Thread(() -> {
                    fair.lock();
                    try {
                        synchronized (order) {
                            order.add(position);
                        }
                    } finally {
                        fair.unlock();
                    }
                });
                threads.add(t);
                t.start();
                while (!fair.hasQueuedThread(t)) {
                    Thread.yield();           // wait until this thread is really queued
                }
            }
        } finally {
            fair.unlock();
        }
        for (Thread t : threads) {
            t.join();
        }
        return order;
    }

    static void starvationDemo() throws InterruptedException {
        System.out.println("== 5. Fair lock ==");
        System.out.println("fair lock acquisition order: " + fairLockAcquisitionOrder(5));
    }

    public static void main(String[] args) throws InterruptedException {
        deadlockDemo();
        System.out.println("---");
        lockOrderingDemo();
        System.out.println("---");
        tryLockDemo();
        System.out.println("---");
        livelockDemo();
        System.out.println("---");
        starvationDemo();
    }
}

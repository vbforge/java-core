package com.vbforge.locks_and_conditions;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link Condition} — {@code wait()/notify()} for explicit locks.
 *
 * <p>Sections:
 * <ol>
 *   <li>A bounded buffer with two conditions (notFull / notEmpty), waiting in a {@code while} loop</li>
 *   <li>{@code await(timeout)} — wait with a deadline</li>
 *   <li>{@code signal()} vs {@code signalAll()}</li>
 *   <li>A condition can only be used while holding its lock</li>
 * </ol>
 */
public class ConditionDemo {

    // =====================================================================
    // 1. Bounded buffer with two conditions, guarded waits
    // =====================================================================

    /**
     * What: a {@code Condition} is a wait-set that belongs to one {@code Lock}.
     * {@code await()} atomically releases the lock and parks the thread;
     * {@code signal()} wakes one waiter, which re-acquires the lock before
     * returning from {@code await()}.
     *
     * <p>Why it matters: {@code synchronized} has exactly <b>one</b> wait-set per
     * object, so producers and consumers must share it and {@code notifyAll()}
     * wakes everybody. One lock can create <b>many</b> conditions: here
     * {@code notFull} wakes only producers and {@code notEmpty} wakes only consumers.
     *
     * <p>Complexity:
     * <ul>
     *   <li>put/take: O(1) amortised</li>
     *   <li>Memory: O(capacity)</li>
     * </ul>
     *
     * <p>Common gotcha: wait in a <b>{@code while}</b> loop, never an {@code if}.
     * A thread can wake up although its condition is still false (spurious wakeup,
     * or another thread consumed the item first). The loop re-checks the
     * predicate every time the thread wakes.
     */
    public static class BoundedBuffer<T> {
        private final Deque<T> items = new ArrayDeque<>();
        private final int capacity;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public BoundedBuffer(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("capacity must be positive");
            }
            this.capacity = capacity;
        }

        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (items.size() == capacity) {   // while, not if
                    notFull.await();
                }
                items.addLast(item);
                notEmpty.signal();                   // wake a consumer only
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (items.isEmpty()) {
                    notEmpty.await();
                }
                T item = items.removeFirst();
                notFull.signal();                    // wake a producer only
                return item;
            } finally {
                lock.unlock();
            }
        }

        /** Non-blocking variant: returns {@code false} when the buffer is full. */
        public boolean offer(T item) {
            lock.lock();
            try {
                if (items.size() == capacity) {
                    return false;
                }
                items.addLast(item);
                notEmpty.signal();
                return true;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try {
                return items.size();
            } finally {
                lock.unlock();
            }
        }
    }

    static void boundedBufferDemo() throws InterruptedException {
        System.out.println("== 1. Bounded buffer, two conditions ==");
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(2);
        int[] consumed = new int[1];
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    consumed[0] += buffer.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        for (int i = 1; i <= 10; i++) {
            buffer.put(i);                   // blocks whenever 2 items are waiting
        }
        consumer.join();
        System.out.println("sum of 1..10 consumed through a 2-slot buffer = " + consumed[0]);
    }

    // =====================================================================
    // 2. await with a timeout
    // =====================================================================

    /**
     * What: {@code await(time, unit)} returns {@code false} if the time elapsed
     * without a signal, and {@code true} if it was signalled in time.
     *
     * <p>Why it matters: a plain {@code await()} can block forever if the
     * signalling thread died. A timeout turns "hung" into "failed with a reason".
     *
     * <p>Complexity: O(1); the thread parks for at most the timeout.
     *
     * <p>Common gotcha: a {@code false} result only says <i>time ran out</i>.
     * Still re-check the real predicate, because the state may have changed
     * without a signal reaching you.
     *
     * @return {@code true} if the wait ended by a signal
     */
    static boolean awaitWithTimeout(long millis) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition never = lock.newCondition();
        lock.lock();
        try {
            return never.await(millis, TimeUnit.MILLISECONDS);   // nobody signals
        } finally {
            lock.unlock();
        }
    }

    static void awaitTimeoutDemo() throws InterruptedException {
        System.out.println("== 2. await(timeout) ==");
        System.out.println("signalled within 20 ms? " + awaitWithTimeout(20));
    }

    // =====================================================================
    // 3. signal vs signalAll
    // =====================================================================

    /**
     * What: {@code signal()} wakes <b>one</b> waiter on that condition;
     * {@code signalAll()} wakes <b>all</b> of them. Each woken thread then
     * competes for the lock again.
     *
     * <p>Why it matters: {@code signal()} is cheaper but only correct when any
     * single waiter can proceed. For a "gate opens for everybody" event (such as
     * a shutdown flag) you need {@code signalAll()}.
     *
     * <p>Complexity: signal O(1); signalAll O(waiters).
     *
     * <p>Common gotcha: using {@code signal()} when waiters wait for <i>different</i>
     * predicates on one condition. The woken thread may not be able to proceed
     * and nobody else is told — a lost wakeup. Use one condition per predicate.
     */
    public static class Gate {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition opened = lock.newCondition();
        private boolean open;
        private int passed;

        public void awaitOpen() throws InterruptedException {
            lock.lock();
            try {
                while (!open) {
                    opened.await();
                }
                passed++;
            } finally {
                lock.unlock();
            }
        }

        public void openForAll() {
            lock.lock();
            try {
                open = true;
                opened.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void openForOne() {
            lock.lock();
            try {
                opened.signal();
            } finally {
                lock.unlock();
            }
        }

        public int passed() {
            lock.lock();
            try {
                return passed;
            } finally {
                lock.unlock();
            }
        }

        /** Number of threads currently parked on the condition. */
        public int waiting() {
            lock.lock();
            try {
                return lock.getWaitQueueLength(opened);
            } finally {
                lock.unlock();
            }
        }
    }

    static void signalAllDemo() throws InterruptedException {
        System.out.println("== 3. signalAll ==");
        Gate gate = new Gate();
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    gate.awaitOpen();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }
        while (gate.waiting() < 3) {
            Thread.yield();
        }
        gate.openForAll();
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("threads that passed the gate: " + gate.passed());
    }

    // =====================================================================
    // 4. Lock must be held
    // =====================================================================

    /**
     * What: calling {@code await()} or {@code signal()} without holding the
     * condition's lock throws {@link IllegalMonitorStateException} — the same
     * rule as {@code wait()/notify()} outside {@code synchronized}.
     *
     * <p>Why it matters: the check-then-wait must be atomic with respect to
     * state changes; otherwise a signal could arrive between "predicate false"
     * and "start waiting" and be lost forever.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: signalling a condition of a <i>different</i> lock than
     * the one you hold fails the same way.
     *
     * @return {@code true} if signal() without the lock was rejected
     */
    static boolean signalWithoutLockRejected() {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        try {
            condition.signal();
            return false;
        } catch (IllegalMonitorStateException expected) {
            return true;
        }
    }

    static void lockMustBeHeldDemo() {
        System.out.println("== 4. Lock must be held ==");
        System.out.println("signal() without lock rejected? " + signalWithoutLockRejected());
    }

    public static void main(String[] args) throws InterruptedException {
        boundedBufferDemo();
        System.out.println("---");
        awaitTimeoutDemo();
        System.out.println("---");
        signalAllDemo();
        System.out.println("---");
        lockMustBeHeldDemo();
    }
}

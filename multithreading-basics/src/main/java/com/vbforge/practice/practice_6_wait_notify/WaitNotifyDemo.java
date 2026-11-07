// IMPLEMENTATION
package com.vbforge.practice.practice_6_wait_notify;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * Demonstrates wait() / notify() / notifyAll() — Java's built-in condition mechanism.
 *
 * These methods are defined on Object and must be called from within a
 * synchronized block on the SAME object (the monitor). Breaking this rule
 * throws IllegalMonitorStateException at runtime.
 *
 * Core semantics:
 *   obj.wait()
 *     – Atomically RELEASES the monitor and suspends the calling thread.
 *     – When notified (or interrupted), the thread RE-ACQUIRES the monitor
 *       before wait() returns. This is crucial: no other code path bypasses locking.
 *
 *   obj.notify()
 *     – Wakes ONE waiting thread (chosen arbitrarily by JVM). That thread
 *       moves from WAITING → BLOCKED until it can re-acquire the monitor.
 *
 *   obj.notifyAll()
 *     – Wakes ALL waiting threads. Each one competes for the monitor.
 *     – Safer default: use notifyAll() unless you are certain only one
 *       thread needs to wake AND all waiting threads are equivalent.
 *
 * Spurious wakeups:
 *   The JVM is allowed to wake a waiting thread without a corresponding notify().
 *   ALWAYS wrap wait() in a loop that re-checks the condition:
 *
 *       synchronized (lock) {
 *           while (!conditionMet()) {   // ← loop, not if
 *               lock.wait();
 *           }
 *           // safe to proceed
 *       }
 *
 * Producer-Consumer is the canonical pattern:
 *   - Producer adds items; notifies when queue transitions empty → non-empty.
 *   - Consumer waits while queue is empty; notifies when queue transitions full → non-full.
 *
 * Modern alternative:
 *   java.util.concurrent.locks.Condition (paired with ReentrantLock) offers the
 *   same semantics with more power: multiple condition queues per lock, timed waits,
 *   and interruptible waits. Covered in locks_and_conditions/practice_1_explicit_locks.
 */
public class WaitNotifyDemo {

    // ── Bounded blocking queue (Producer-Consumer) ────────────────────────────

    /**
     * A minimal bounded queue that blocks producers when full and
     * consumers when empty — implemented purely with wait/notifyAll.
     *
     * This is essentially what ArrayBlockingQueue does internally.
     */
    static class BoundedQueue<T> {

        private final Queue<T> buffer;
        private final int      capacity;

        BoundedQueue(int capacity) {
            this.capacity = capacity;
            this.buffer   = new ArrayDeque<>(capacity);
        }

        /**
         * Adds an item, blocking if the queue is full.
         * notifyAll() wakes any waiting consumers.
         */
        public synchronized void put(T item) throws InterruptedException {
            // Loop — not if — to guard against spurious wakeups
            while (buffer.size() == capacity) {
                System.out.printf("[%s] Queue full (%d/%d), producer waiting...%n",
                        Thread.currentThread().getName(), buffer.size(), capacity);
                wait();   // releases the monitor; re-acquires before returning
            }
            buffer.add(item);
            System.out.printf("[%s] Produced: %-4s | size=%d%n",
                    Thread.currentThread().getName(), item, buffer.size());
            notifyAll();  // wake consumers that may be waiting on empty queue
        }

        /**
         * Removes and returns an item, blocking if the queue is empty.
         * notifyAll() wakes any waiting producers.
         */
        public synchronized T take() throws InterruptedException {
            while (buffer.isEmpty()) {
                System.out.printf("[%s] Queue empty, consumer waiting...%n",
                        Thread.currentThread().getName());
                wait();
            }
            T item = buffer.poll();
            System.out.printf("[%s] Consumed: %-4s | size=%d%n",
                    Thread.currentThread().getName(), item, buffer.size());
            notifyAll();  // wake producers that may be waiting on full queue
            return item;
        }

        public synchronized int size() { return buffer.size(); }
    }

    // ── One-shot handoff signal ───────────────────────────────────────────────

    /**
     * The simplest possible use of wait/notify: one thread waits for
     * another thread to complete some initialization before proceeding.
     *
     * Similar in spirit to CountDownLatch(1) but built from primitives.
     */
    static class ReadySignal {
        private boolean ready = false;

        public synchronized void markReady() {
            ready = true;
            notifyAll();   // wake all threads waiting in awaitReady()
        }

        public synchronized void awaitReady() throws InterruptedException {
            while (!ready) {   // spurious-wakeup-safe loop
                wait();
            }
        }

        public synchronized boolean isReady() { return ready; }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Wait / Notify Demo ===\n");

        // ── Scenario 1: Producer-Consumer with BoundedQueue ───────────────────
        System.out.println("-- Producer-Consumer (capacity=2) --");
        BoundedQueue<Integer> queue = new BoundedQueue<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    queue.take();
                    Thread.sleep(60);   // slower than producer → queue fills up
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Producer-Consumer done. Queue size: " + queue.size());

        // ── Scenario 2: One-shot ReadySignal ─────────────────────────────────
        System.out.println("\n-- ReadySignal handoff --");
        ReadySignal signal = new ReadySignal();

        Thread waiter = new Thread(() -> {
            try {
                System.out.println("[waiter] waiting for ready signal...");
                signal.awaitReady();
                System.out.println("[waiter] received ready signal, proceeding");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Waiter");

        waiter.start();
        Thread.sleep(100);   // let waiter block
        System.out.println("[main] marking ready");
        signal.markReady();
        waiter.join();
        System.out.println("ReadySignal done.");

        // ── Scenario 3: Multiple consumers — notifyAll is correct here ────────
        System.out.println("\n-- Multiple consumers (notifyAll) --");
        BoundedQueue<String> shared = new BoundedQueue<>(5);

        // Start 3 consumers, all waiting on empty queue
        List<Thread> consumers = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            Thread c = new Thread(() -> {
                try {
                    String item = shared.take();
                    System.out.printf("[consumer-%d] got: %s%n", id, item);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Consumer-" + i);
            consumers.add(c);
            c.start();
        }

        Thread.sleep(50);  // let consumers settle into wait()

        // Produce 3 items — notifyAll wakes all three consumers
        for (int i = 1; i <= 3; i++) {
            shared.put("item-" + i);
        }
        for (Thread c : consumers) c.join();
        System.out.println("All consumers done.");

        System.out.println("\nMain done.");
    }
}
// IMPLEMENTATION
package com.vbforge.practice.practice_5_thread_fundamentals;

/**
 * Demonstrates Java thread fundamentals — creation, lifecycle, and control.
 *
 * A thread is an independent path of execution within a process.
 * Every Java program starts with one thread: the main thread.
 * The JVM also runs background threads (GC, finalizer, etc.).
 *
 * Two ways to create a thread:
 *   1. Extend Thread      — tightly couples task and thread mechanism (less flexible)
 *   2. Implement Runnable — separates task from thread; preferred approach
 *
 * Thread lifecycle (simplified):
 *
 *   NEW ──start()──► RUNNABLE ──scheduler──► RUNNING
 *                       ▲                       │
 *                       │                  sleep/wait/IO
 *                       │                       │
 *                    notify()              BLOCKED/WAITING/TIMED_WAITING
 *                                               │
 *                                         TERMINATED (run() returns or throws)
 *
 * Key methods:
 *   start()          — schedules thread for execution (do NOT call run() directly)
 *   join()           — calling thread waits for this thread to terminate
 *   sleep(ms)        — pauses current thread; does NOT release monitors
 *   interrupt()      — sets interrupt flag; wakes sleeping/waiting threads
 *   isInterrupted()  — checks flag without clearing it
 *   Thread.interrupted() — checks AND clears flag (static)
 *   setDaemon(true)  — daemon threads die when all user threads finish
 *   setPriority(n)   — hint to scheduler (1–10); not a guarantee
 *   getName/setName  — useful for debugging and logging
 *
 * Important:
 *   - Calling run() directly executes on the CURRENT thread — no concurrency!
 *   - After a thread terminates, it cannot be restarted (start() throws IllegalThreadStateException).
 */
public class ThreadsDemo {

    // ── 1. Extend Thread ──────────────────────────────────────────────────────

    /**
     * Approach 1: extend Thread.
     * Simple but inflexible — your class cannot extend anything else.
     */
    static class CounterThread extends Thread {

        private final String label;
        private final int    count;

        CounterThread(String label, int count) {
            super(label);          // sets thread name
            this.label = label;
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 1; i <= count; i++) {
                System.out.printf("[%s] count = %d%n", label, i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // Restore interrupt flag — never swallow silently
                    Thread.currentThread().interrupt();
                    System.out.printf("[%s] interrupted at count=%d%n", label, i);
                    return;
                }
            }
        }
    }

    // ── 2. Implement Runnable ─────────────────────────────────────────────────

    /**
     * Approach 2: implement Runnable — preferred.
     * Task logic is decoupled from the Thread mechanism.
     * The same Runnable can be submitted to an ExecutorService later.
     */
    static class PrintTask implements Runnable {

        private final String message;
        private final int    repeat;

        PrintTask(String message, int repeat) {
            this.message = message;
            this.repeat  = repeat;
        }

        @Override
        public void run() {
            for (int i = 0; i < repeat; i++) {
                System.out.printf("[%s] %s (#%d)%n",
                        Thread.currentThread().getName(), message, i + 1);
            }
        }
    }

    // ── 3. Cooperative cancellation via interrupt ─────────────────────────────

    /**
     * A long-running task that checks the interrupt flag on each iteration.
     * This is the correct pattern for cancellable worker threads.
     */
    static class CancellableWorker implements Runnable {
        @Override
        public void run() {
            int iteration = 0;
            // Check interrupt flag — do NOT use Thread.interrupted() here
            // because it clears the flag; isInterrupted() is non-destructive.
            while (!Thread.currentThread().isInterrupted()) {
                iteration++;
                System.out.printf("[worker] iteration %d%n", iteration);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    // sleep() clears the flag on InterruptedException —
                    // re-set it so the while-condition check picks it up.
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("[worker] gracefully stopped after " + iteration + " iterations");
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Threads Demo ===\n");

        // --- Thread metadata ---
        Thread main = Thread.currentThread();
        System.out.println("Main thread name     : " + main.getName());
        System.out.println("Main thread id       : " + main.threadId());
        System.out.println("Main thread priority : " + main.getPriority());
        System.out.println("Main thread daemon   : " + main.isDaemon());
        System.out.println("Active thread count  : " + Thread.activeCount());

        // --- 1. Extend Thread ---
        System.out.println("\n-- CounterThread (extends Thread) --");
        CounterThread t1 = new CounterThread("Alpha", 3);
        CounterThread t2 = new CounterThread("Beta",  3);
        t1.start();
        t2.start();
        t1.join();   // main waits for Alpha to finish
        t2.join();   // main waits for Beta  to finish
        System.out.println("Both counter threads finished");

        // --- 2. Runnable + anonymous lambda ---
        System.out.println("\n-- Runnable (lambda) --");
        Thread lambdaThread = new Thread(() ->
                System.out.println("[lambda] running on " + Thread.currentThread().getName()),
                "LambdaThread"
        );
        lambdaThread.start();
        lambdaThread.join();

        // --- 3. PrintTask (implements Runnable) ---
        System.out.println("\n-- PrintTask (implements Runnable) --");
        Thread t3 = new Thread(new PrintTask("hello", 2), "Worker-1");
        Thread t4 = new Thread(new PrintTask("world", 2), "Worker-2");
        t3.start();
        t4.start();
        t3.join();
        t4.join();

        // --- 4. Daemon thread ---
        System.out.println("\n-- Daemon thread --");
        Thread daemon = new Thread(() -> {
            // Daemon threads are killed abruptly when all user threads finish.
            // They are suited for background housekeeping (cache eviction, etc.).
            while (true) {
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }
                System.out.println("[daemon] heartbeat");
            }
        }, "DaemonThread");
        daemon.setDaemon(true);   // MUST be set before start()
        daemon.start();
        Thread.sleep(250);        // let daemon print 2 heartbeats
        System.out.println("Main continues; daemon will die when main exits");

        // --- 5. Interrupt + cooperative cancellation ---
        System.out.println("\n-- Cooperative cancellation --");
        Thread worker = new Thread(new CancellableWorker(), "CancellableWorker");
        worker.start();
        Thread.sleep(150);        // let it run for ~5 iterations
        worker.interrupt();       // request cancellation
        worker.join();

        // --- 6. Thread state inspection ---
        System.out.println("\n-- Thread states --");
        Thread sleeper = new Thread(() -> {
            try { Thread.sleep(5_000); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Sleeper");
        System.out.println("Before start : " + sleeper.getState()); // NEW
        sleeper.start();
        Thread.sleep(20);
        System.out.println("While sleeping: " + sleeper.getState()); // TIMED_WAITING
        sleeper.interrupt();
        sleeper.join();
        System.out.println("After finish : " + sleeper.getState());  // TERMINATED

        System.out.println("\nMain thread done.");
    }
}
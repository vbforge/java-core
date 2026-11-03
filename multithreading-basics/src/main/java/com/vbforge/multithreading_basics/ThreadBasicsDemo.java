package com.vbforge.multithreading_basics;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread and Runnable basics: creating threads, start() vs run(), join(),
 * daemon threads, cooperative cancellation with interrupt(), and uncaught exceptions.
 *
 * Run: mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_basics.ThreadBasicsDemo"
 */
public class ThreadBasicsDemo {

    public static void main(String[] args) throws Exception {
        extendThreadDemo();
        System.out.println("---");
        runnableDemo();
        System.out.println("---");
        startVsRunDemo();
        System.out.println("---");
        joinDemo();
        System.out.println("---");
        daemonDemo();
        System.out.println("---");
        interruptDemo();
        System.out.println("---");
        uncaughtExceptionDemo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. Extending Thread
    //
    // What:  subclass Thread and override run().
    // Why:   it works, but it welds the TASK (what to run) to the MECHANISM
    //        (how it runs). You cannot reuse the task on a pool or another thread.
    // Gotcha: overriding start() instead of run() does not create a new thread.
    // ─────────────────────────────────────────────────────────────────────
    public static class HelloThread extends Thread {
        public HelloThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            System.out.println("HelloThread running on: " + Thread.currentThread().getName());
        }
    }

    static void extendThreadDemo() throws InterruptedException {
        Thread t = new HelloThread("hello-1");
        t.start();
        t.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Implementing Runnable (preferred)
    //
    // What:  Runnable is a single-method interface (a lambda fits), so the
    //        task is a plain object and the Thread is just a carrier.
    // Why:   the same Runnable can later be handed to an ExecutorService
    //        (see the multithreading-advanced module) without changes.
    // Gotcha: Runnable.run() cannot return a value or throw checked exceptions;
    //         Callable (later module) can.
    // ─────────────────────────────────────────────────────────────────────
    static void runnableDemo() throws InterruptedException {
        Runnable task = () -> System.out.println("Runnable on: " + Thread.currentThread().getName());

        Thread a = new Thread(task, "worker-A");
        Thread b = new Thread(task, "worker-B");
        a.start();
        b.start();
        a.join();
        b.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. start() vs run()
    //
    // What:  start() asks the JVM to create a new thread, which then calls run().
    //        Calling run() directly is an ordinary method call on the CURRENT thread.
    // Why:   the most common beginner bug — the code "works" but nothing is concurrent.
    // Gotcha: a Thread can be started only once; a second start() throws
    //         IllegalThreadStateException.
    // ─────────────────────────────────────────────────────────────────────
    static void startVsRunDemo() throws InterruptedException {
        Runnable printer = () -> System.out.println("  ran on: " + Thread.currentThread().getName());
        Thread t = new Thread(printer, "new-thread");

        System.out.println("run()   ->");
        t.run();
        System.out.println("start() ->");
        t.start();
        t.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. join()
    //
    // What:  join() blocks the caller until the target thread terminates.
    // Why:   it is the simplest way to wait for a result. It also creates a
    //        happens-before edge: everything the worker did is visible after join() returns.
    // Complexity: join() itself is O(1); it just waits. join(millis) gives up after a timeout.
    // ─────────────────────────────────────────────────────────────────────
    static void joinDemo() throws InterruptedException {
        int[] result = new int[1];
        Thread worker = new Thread(() -> result[0] = 6 * 7, "calculator");
        worker.start();
        worker.join();
        System.out.println("result after join = " + result[0]);
    }

    // ─────────────────────────────────────────────────────────────────────
    // 5. Daemon threads
    //
    // What:  the JVM exits when all NON-daemon threads finish; daemon threads
    //        are killed abruptly at that point.
    // Why:   use for background housekeeping (timers, cache cleaners) that must
    //        never keep the application alive.
    // Gotcha: setDaemon(true) must be called BEFORE start(); a thread inherits the
    //         daemon flag of the thread that creates it. Never do important I/O in a daemon.
    // ─────────────────────────────────────────────────────────────────────
    static void daemonDemo() throws InterruptedException {
        Thread t = new Thread(() -> { }, "background");
        t.setDaemon(true);
        System.out.println("isDaemon before start = " + t.isDaemon());
        t.start();
        t.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 6. Interruption — cooperative cancellation
    //
    // What:  interrupt() sets the target's interrupt flag. Blocking methods such as
    //        sleep()/wait()/join() react by throwing InterruptedException AND clearing the flag.
    // Why:   Thread.stop() is deprecated and unsafe; interrupt is the sanctioned way to
    //        ask a thread to finish. The thread decides how to stop.
    // Gotcha: swallowing InterruptedException silently loses the request. Either exit,
    //         or restore the flag with Thread.currentThread().interrupt().
    // ─────────────────────────────────────────────────────────────────────
    static void interruptDemo() throws InterruptedException {
        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                System.out.println("sleeper interrupted, flag now = "
                        + Thread.currentThread().isInterrupted());
            }
        }, "sleeper");
        sleeper.start();
        while (sleeper.getState() != Thread.State.TIMED_WAITING) {
            Thread.yield();
        }
        sleeper.interrupt();
        sleeper.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 7. Uncaught exceptions
    //
    // What:  an exception escaping run() kills only that thread; the caller of start()
    //        never sees it (no try/catch around start() can catch it).
    // Why:   install an UncaughtExceptionHandler to log or react to a dying thread.
    // ─────────────────────────────────────────────────────────────────────
    static void uncaughtExceptionDemo() throws InterruptedException {
        AtomicReference<Throwable> seen = new AtomicReference<>();
        Thread t = new Thread(() -> {
            throw new IllegalStateException("boom");
        }, "failing");
        t.setUncaughtExceptionHandler((thread, ex) -> seen.set(ex));
        t.start();
        t.join();
        System.out.println("handler saw: " + seen.get());
    }
}

package com.vbforge.virtual_threads_intro;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Virtual threads — the basics (Project Loom, final in Java 21 via JEP 444).
 *
 * <p>A <b>virtual thread</b> is still a {@link Thread}, but it is scheduled by the JVM
 * instead of the operating system. When it blocks (sleep, I/O, lock, queue …) the JVM
 * unmounts it from its <i>carrier thread</i> (a small pool of platform threads) and the
 * carrier immediately runs another virtual thread. Your code stays simple, blocking,
 * thread-per-task code — but it scales like async code.
 *
 * <p>Sections:
 * <ol>
 *   <li>Creating virtual threads</li>
 *   <li>What makes a virtual thread different (properties)</li>
 *   <li>One virtual thread per task: {@code newVirtualThreadPerTaskExecutor}</li>
 *   <li>Massive concurrency: thousands of threads blocked at once</li>
 * </ol>
 */
public class VirtualThreadBasicsDemo {

    public static void main(String[] args) throws Exception {
        creatingVirtualThreadsDemo();
        System.out.println("---");
        virtualThreadPropertiesDemo();
        System.out.println("---");
        executorPerTaskDemo();
        System.out.println("---");
        massiveConcurrencyDemo();
    }

    // =========================================================================
    // 1. CREATING VIRTUAL THREADS
    // =========================================================================
    /*
     * What:
     *   Four ways to get a virtual thread:
     *     - Thread.ofVirtual().name(..).start(r)   → builder, starts immediately
     *     - Thread.startVirtualThread(r)           → shortcut, unnamed, starts immediately
     *     - Thread.ofVirtual().unstarted(r)        → created in state NEW, you call start()
     *     - Thread.ofVirtual().factory()           → a ThreadFactory (plugs into any API
     *                                                that accepts one)
     *
     * Why it matters:
     *   Before Java 21 the only "real" thread was the platform thread: a thin wrapper over
     *   an OS thread. Creating one is expensive and the OS caps how many you can have.
     *   Virtual threads are cheap objects on the heap — you create one per task and throw
     *   it away. Same API, so existing Thread knowledge still applies.
     *
     * Complexity:
     *   - Creation / start: cheap, no OS call (a platform thread needs one)
     *   - Memory: the stack lives on the heap and grows/shrinks with use
     *
     * Common gotcha:
     *   Virtual threads are always DAEMON threads. If main() ends, the JVM exits even if
     *   virtual threads are still running — always join() them (or close the executor).
     */
    static void creatingVirtualThreadsDemo() throws InterruptedException {
        // 1. Builder: named, started immediately
        Thread greeter = Thread.ofVirtual()
                .name("greeter")
                .start(() -> System.out.println("Hello from '" + Thread.currentThread().getName() + "'"));
        greeter.join();

        // 2. Shortcut: no name → the name is the empty string
        Thread anonymous = Thread.startVirtualThread(
                () -> System.out.println("Hello from a thread named '" + Thread.currentThread().getName() + "'"));
        anonymous.join();

        // 3. Unstarted: you control when it starts
        Thread later = Thread.ofVirtual()
                .name("later")
                .unstarted(() -> System.out.println("Hello from '" + Thread.currentThread().getName() + "'"));
        System.out.println("state before start(): " + later.getState());
        later.start();
        later.join();
        System.out.println("state after join():   " + later.getState());

        // 4. Factory: name(prefix, start) numbers the threads it creates
        ThreadFactory factory = Thread.ofVirtual().name("worker-", 0).factory();
        Thread w0 = factory.newThread(() -> { });
        Thread w1 = factory.newThread(() -> { });
        System.out.println("factory names: " + w0.getName() + ", " + w1.getName());

        // For contrast: the platform-thread builder has the same shape
        Thread platform = Thread.ofPlatform().name("platform-1").unstarted(() -> { });
        System.out.println("platform.isVirtual() = " + platform.isVirtual());
    }

    // =========================================================================
    // 2. PROPERTIES — what is (and isn't) configurable
    // =========================================================================
    /*
     * What:
     *   Virtual threads are "normal" Threads with a few fixed properties:
     *     - isVirtual()            → true
     *     - isDaemon()             → always true; setDaemon(false) throws IllegalArgumentException
     *     - getPriority()          → always NORM_PRIORITY; setPriority(..) is silently ignored
     *     - getName()              → "" unless you set one
     *     - getThreadGroup()       → one shared group called "VirtualThreads"
     *
     * Why it matters:
     *   These fixed values are what let the JVM create millions of them cheaply.
     *   You do not tune priorities or daemon flags — you just create more threads.
     *
     * Common gotcha:
     *   Code that calls setDaemon(false) or relies on priorities will not behave the
     *   same on a virtual thread. Thread.currentThread() inside a virtual thread gives
     *   the VIRTUAL thread — the carrier thread underneath is deliberately invisible.
     */
    static void virtualThreadPropertiesDemo() {
        Thread virtual = Thread.ofVirtual().unstarted(() -> { });

        System.out.println("isVirtual:   " + virtual.isVirtual());
        System.out.println("isDaemon:    " + virtual.isDaemon());
        System.out.println("name:        '" + virtual.getName() + "'");
        System.out.println("group:       " + virtual.getThreadGroup().getName());

        virtual.setPriority(Thread.MAX_PRIORITY);
        System.out.println("priority after setPriority(MAX): " + virtual.getPriority());

        try {
            virtual.setDaemon(false);
        } catch (IllegalArgumentException e) {
            System.out.println("setDaemon(false) → IllegalArgumentException");
        }

        // The toString shows the carrier once the thread is running — informational only.
        Thread.startVirtualThread(() -> System.out.println("running: " + Thread.currentThread()));
        sleepQuietly(50);
    }

    // =========================================================================
    // 3. ONE VIRTUAL THREAD PER TASK
    // =========================================================================
    /*
     * What:
     *   Executors.newVirtualThreadPerTaskExecutor() starts a NEW virtual thread for every
     *   submitted task. Since Java 19 ExecutorService is AutoCloseable: close() waits
     *   until all submitted tasks have finished, so try-with-resources gives you a
     *   block that "owns" its tasks.
     *
     * Why it matters:
     *   Pool sizing (fixed pool of 200? 500?) existed because platform threads were
     *   scarce. Virtual threads are not scarce, so the pool is replaced by "one thread
     *   per task". There is nothing to size and nothing to reuse.
     *
     * Common gotcha:
     *   Do NOT pool virtual threads (e.g. a fixed pool backed by a virtual ThreadFactory).
     *   Pooling is a tool for expensive resources; if you need to limit concurrency,
     *   limit it with a Semaphore (see VirtualThreadPitfallsDemo).
     */
    static void executorPerTaskDemo() {
        AtomicInteger completed = new AtomicInteger();
        Set<Thread> threadsUsed = ConcurrentHashMap.newKeySet();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                executor.submit(() -> {
                    threadsUsed.add(Thread.currentThread());
                    Thread.sleep(20);
                    return completed.incrementAndGet();
                });
            }
        } // close() blocks here until all 5 tasks are done

        System.out.println("completed after the try block: " + completed.get());
        System.out.println("distinct threads used:         " + threadsUsed.size());
    }

    // =========================================================================
    // 4. MASSIVE CONCURRENCY
    // =========================================================================
    /*
     * What:
     *   Start N virtual threads that all block on the same latch, then check how many
     *   are alive at the same moment.
     *
     * Why it matters:
     *   This is the whole point of Loom. A blocked virtual thread costs only a small
     *   heap object (its stack), not an OS thread, so 10_000 simultaneously blocked
     *   threads are routine — a thread-per-request server can hold 10_000 open
     *   requests without async callbacks.
     *
     * Complexity:
     *   - Time: O(n) to start and join n threads
     *   - Space: O(n) small heap objects (no n OS threads)
     *
     * Common gotcha:
     *   Virtual threads make blocking cheap — they do NOT make CPU work faster. Work that
     *   keeps a CPU busy is still limited by the number of cores.
     */
    static void massiveConcurrencyDemo() throws InterruptedException {
        int alive = simultaneouslyBlocked(10_000);
        System.out.println("virtual threads alive at the same time: " + alive);
    }

    /**
     * Starts {@code n} virtual threads that all wait on one latch, counts how many are
     * alive while every one of them is blocked, then releases and joins them all.
     *
     * @param n number of virtual threads to start
     * @return how many threads were alive while all of them were blocked (equals {@code n})
     */
    public static int simultaneouslyBlocked(int n) throws InterruptedException {
        CountDownLatch allStarted = new CountDownLatch(n);
        CountDownLatch release = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            threads.add(Thread.ofVirtual().start(() -> {
                allStarted.countDown();
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        allStarted.await();
        int alive = (int) threads.stream().filter(Thread::isAlive).count();

        release.countDown();
        for (Thread t : threads) {
            t.join();
        }
        return alive;
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

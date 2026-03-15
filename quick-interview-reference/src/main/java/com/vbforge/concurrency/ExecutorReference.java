package com.vbforge.concurrency;

import java.util.List;
import java.util.concurrent.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  CONCURRENCY REFERENCE — Part 2: Executors & Virtual Threads
 *  Topics: ExecutorService · thread pools · Callable/Future ·
 *          CompletableFuture · CountDownLatch · Semaphore ·
 *          virtual threads (Project Loom)
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class ExecutorReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. THREAD POOL TYPES — comparison
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Executors factory          │ Thread count  │ Queue          │ Use case
     *  ───────────────────────────┼───────────────┼────────────────┼──────────────────────
     *  newFixedThreadPool(n)      │ Fixed n       │ Unbounded      │ CPU-bound, known load
     *  newCachedThreadPool()      │ 0 to MAX      │ None (SynchronousQueue)│ Short-lived async tasks
     *  newSingleThreadExecutor()  │ 1             │ Unbounded      │ Sequential processing
     *  newScheduledThreadPool(n)  │ Fixed n       │ DelayQueue     │ Scheduled/periodic tasks
     *  newVirtualThreadPerTaskExecutor()│ Virtual │ —              │ High-concurrency I/O (Java 21)
     *
     *  ⚠️ GOTCHA: newCachedThreadPool() can create thousands of threads under load → OOM
     *  ⚠️ GOTCHA: newFixedThreadPool() with unbounded queue → tasks pile up → OOM
     *  ✅ PRODUCTION: Use ThreadPoolExecutor with explicit bounds and rejection policy
     */
    static void threadPoolExamples() throws Exception {
        // ✅ Explicit ThreadPoolExecutor — production-grade configuration
        ExecutorService pool = new ThreadPoolExecutor(
            4,                              // corePoolSize
            8,                              // maximumPoolSize
            60L, TimeUnit.SECONDS,          // keepAlive for idle threads above core
            new ArrayBlockingQueue<>(100),  // bounded queue — backpressure!
            new ThreadPoolExecutor.CallerRunsPolicy()  // rejection: run in caller's thread
        );

        // Submit tasks
        Future<Integer> future = pool.submit(() -> 42);
        System.out.println(future.get());   // blocks until done

        // ✅ ALWAYS shutdown executor — otherwise JVM won't exit
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
            pool.shutdownNow();  // force stop if graceful shutdown times out
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. CALLABLE vs RUNNABLE — side-by-side
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Feature           │ Runnable            │ Callable<T>
     *  ──────────────────┼─────────────────────┼──────────────────────
     *  Return value      │ ❌ void              │ ✅ T
     *  Checked exception │ ❌ Cannot throw      │ ✅ Can throw Exception
     *  Submit to executor│ execute() / submit() │ submit() only
     *  Result holder     │ N/A                  │ Future<T>
     */
    static void callableFutureExamples() throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(2);

        // Callable returns a result
        Callable<String> task = () -> {
            Thread.sleep(100);
            return "result";
        };

        Future<String> future = exec.submit(task);

        // future.get() — BLOCKS until done
        // future.get(1, TimeUnit.SECONDS) — blocks with timeout → TimeoutException
        // future.cancel(true) — attempt to cancel; true = interrupt if running
        // future.isDone() — non-blocking check

        try {
            String result = future.get(2, TimeUnit.SECONDS);
            System.out.println(result);
        } catch (TimeoutException e) {
            future.cancel(true);
        } catch (ExecutionException e) {
            // Callable's exception is wrapped in ExecutionException
            Throwable cause = e.getCause();
        }

        exec.shutdown();
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. SYNCHRONIZATION AIDS — CountDownLatch, Semaphore, CyclicBarrier
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  CountDownLatch:  One-time gate. N threads count down, others await zero.
     *                   NOT reusable.
     *
     *  CyclicBarrier:   Reusable rendezvous point. N threads wait for each other.
     *                   Resets after all threads arrive.
     *
     *  Semaphore:       Controls access to N permits. acquire/release.
     *                   Use for rate limiting or resource pooling.
     */
    static void synchronizationAids() throws Exception {
        // CountDownLatch — "wait for N tasks to complete"
        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);
        ExecutorService exec = Executors.newFixedThreadPool(taskCount);

        for (int i = 0; i < taskCount; i++) {
            exec.submit(() -> {
                try {
                    Thread.sleep(100);
                    System.out.println(Thread.currentThread().getName() + " done");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();  // MUST be in finally — always count down
                }
            });
        }
        latch.await();  // blocks until count reaches 0
        System.out.println("All tasks finished");
        exec.shutdown();

        // Semaphore — "allow max 3 concurrent connections"
        Semaphore semaphore = new Semaphore(3);  // 3 permits
        semaphore.acquire();                      // blocks if 0 permits available
        try {
            // use resource
        } finally {
            semaphore.release();  // ALWAYS in finally
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. VIRTUAL THREADS (Java 21) — Project Loom ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Platform thread: 1-to-1 with OS thread. ~1MB stack. Expensive to create.
     *                   JVM typically supports ~thousands.
     *
     *  Virtual thread:  Managed by JVM scheduler (not OS). Tiny stack (~KB).
     *                   JVM supports MILLIONS. Mounted/unmounted from carrier threads.
     *
     *  HOW IT WORKS:
     *  Virtual thread blocks on I/O → JVM unmounts it from carrier thread →
     *  carrier thread picks up another virtual thread → I/O completes →
     *  virtual thread is remounted → execution continues.
     *
     *  ✅ PERFECT FOR: high-concurrency I/O bound workloads (REST calls, DB queries)
     *  ❌ NOT FOR: CPU-bound tasks (same number of carrier threads as CPU cores)
     *  ❌ AVOID: synchronized blocks in virtual threads → pins carrier thread
     *            Use ReentrantLock instead to allow unmounting during I/O.
     *
     *  Virtual threads vs platform threads — side-by-side:
     *  Feature              │ Platform Thread     │ Virtual Thread
     *  ─────────────────────┼─────────────────────┼──────────────────────
     *  Creation cost        │ High (~1ms, ~1MB)   │ Tiny (~µs, ~KB)
     *  Max count            │ ~Thousands          │ ~Millions
     *  Scheduler            │ OS                  │ JVM (ForkJoinPool)
     *  Blocking I/O         │ Blocks OS thread    │ Unmounts from carrier
     *  synchronized block   │ Fine                │ Pins carrier — avoid!
     *  ThreadLocal          │ ✅ Fine              │ ✅ Works but prefer ScopedValue
     *  Best use case        │ CPU-bound           │ I/O-bound, high concurrency
     */
    static void virtualThreadExamples() throws Exception {
        // Creating virtual threads
        Thread vt = Thread.ofVirtual().start(() -> System.out.println("virtual thread"));
        vt.join();

        // Named virtual threads (useful for debugging)
        Thread named = Thread.ofVirtual().name("worker").start(() -> {});

        // ExecutorService with one virtual thread per task — ideal for servers
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            // Submit 10,000 tasks — each gets its own virtual thread
            for (int i = 0; i < 10_000; i++) {
                final int taskId = i;
                exec.submit(() -> {
                    // simulate I/O — virtual thread unmounts during sleep, not blocking OS thread
                    try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            }
        }   // try-with-resources calls shutdown() and awaitTermination()

        // ⚠️ Do NOT pool virtual threads — create new ones per task
        // ❌ Executors.newFixedThreadPool(100) with virtual threads — defeats the purpose
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Not shutting down ExecutorService         → JVM never exits
     *  ❌ Unbounded queue in thread pool            → OOM under load
     *  ❌ Swallowing InterruptedException           → always restore interrupt flag
     *  ❌ Calling future.get() without timeout      → blocks forever on hanging task
     *  ❌ synchronized in virtual threads           → pins carrier thread (performance issue)
     *  ❌ Pooling virtual threads                   → defeats their purpose
     *  ❌ Not counting down latch in finally        → other threads wait forever
     *  ❌ Not releasing semaphore in finally        → permits leaked, system deadlocks
     */

    public static void main(String[] args) throws Exception {
        threadPoolExamples();
        callableFutureExamples();
        synchronizationAids();
        virtualThreadExamples();
    }
}

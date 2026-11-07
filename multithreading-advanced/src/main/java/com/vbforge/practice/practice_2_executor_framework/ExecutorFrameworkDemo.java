// IMPLEMENTATION
package com.vbforge.practice.practice_2_executor_framework;

import java.util.List;
import java.util.concurrent.*;

/**
 * Demonstrates the Executor Framework — Java's high-level thread management layer.
 *
 * The problem with raw threads:
 *   new Thread(task).start() creates and destroys an OS thread per task.
 *   Thread creation is expensive (~1 MB stack, OS syscall).
 *   Uncontrolled thread creation can exhaust memory and destabilize the JVM.
 *
 * The solution — thread pools:
 *   A pool of pre-created threads sits idle, picks up submitted tasks from a queue,
 *   executes them, then returns to the pool — thread reuse, bounded resource usage.
 *
 * Executor hierarchy:
 *
 *   Executor                        (single method: execute(Runnable))
 *     └─ ExecutorService            (submit, invokeAll, invokeAny, shutdown)
 *           └─ ScheduledExecutorService  (schedule, scheduleAtFixedRate, scheduleWithFixedDelay)
 *           └─ AbstractExecutorService   (base impl)
 *                 └─ ThreadPoolExecutor  (the real engine behind all factory methods)
 *                 └─ ForkJoinPool        (work-stealing; covered in practice_3_fork_join)
 *
 * Factory methods in Executors:
 *   newFixedThreadPool(n)        — n threads, unbounded LinkedBlockingQueue
 *   newCachedThreadPool()        — grows as needed, idle threads expire after 60s
 *   newSingleThreadExecutor()    — 1 thread, tasks serialized, respawns on failure
 *   newScheduledThreadPool(n)    — supports delayed and periodic tasks
 *   newVirtualThreadPerTaskExecutor() — one virtual thread per task (Java 21+)
 *
 * ThreadPoolExecutor constructor (full control):
 *   ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
 *                      workQueue, threadFactory, rejectedExecutionHandler)
 *
 * Rejection policies (when queue is full and max threads are busy):
 *   AbortPolicy         — throws RejectedExecutionException (default)
 *   CallerRunsPolicy    — submitter thread runs the task (natural back-pressure)
 *   DiscardPolicy       — silently drops the task
 *   DiscardOldestPolicy — drops oldest queued task, retries submission
 *
 * Shutdown lifecycle:
 *   shutdown()          — stops accepting new tasks; waits for running tasks to finish
 *   shutdownNow()       — interrupts running tasks; returns unstarted task list
 *   awaitTermination()  — blocks until all tasks finish or timeout expires
 *   isTerminated()      — true when fully shut down
 *
 * Always shut down executor services — they hold non-daemon threads
 * that will prevent JVM exit if left running.
 */
public class ExecutorFrameworkDemo {

    // ── helper task ───────────────────────────────────────────────────────────

    /** Simulates a task that takes some time and returns a labelled result. */
    static Callable<String> timedTask(String label, long millis) {
        return () -> {
            Thread.sleep(millis);
            String result = label + " done by " + Thread.currentThread().getName();
            System.out.println("  " + result);
            return result;
        };
    }

    // ── 1. Fixed thread pool ──────────────────────────────────────────────────

    static void fixedPoolDemo() throws InterruptedException, ExecutionException {
        System.out.println("\n-- newFixedThreadPool(3) --");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            // submit 6 tasks to a pool of 3 — second batch waits in queue
            List<Future<String>> futures = new java.util.ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                futures.add(pool.submit(timedTask("Task-" + i, 50)));
            }
            for (Future<String> f : futures) {
                System.out.println("  result: " + f.get());
            }
        } finally {
            pool.shutdown();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    // ── 2. Cached thread pool ─────────────────────────────────────────────────

    static void cachedPoolDemo() throws InterruptedException {
        System.out.println("\n-- newCachedThreadPool --");
        // Grows unboundedly — suitable for many SHORT-lived tasks.
        // WARNING: never use for long-running or unbounded task streams.
        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            List<Future<?>> fs = new java.util.ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                final int id = i;
                fs.add(pool.submit(() ->
                    System.out.println("  cached task " + id
                            + " on " + Thread.currentThread().getName())));
            }
            for (Future<?> f : fs) f.get();
        } catch (ExecutionException e) {
            System.err.println("Task failed: " + e.getCause());
        } finally {
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ── 3. Single thread executor ─────────────────────────────────────────────

    static void singleThreadDemo() throws InterruptedException, ExecutionException {
        System.out.println("\n-- newSingleThreadExecutor --");
        // Guarantees sequential execution and ordering — useful for serialised I/O.
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            for (int i = 1; i <= 4; i++) {
                final int id = i;
                pool.submit(() ->
                    System.out.println("  serial task " + id
                            + " on " + Thread.currentThread().getName())).get();
            }
        } finally {
            pool.shutdown();
        }
    }

    // ── 4. Scheduled executor ─────────────────────────────────────────────────

    static void scheduledDemo() throws InterruptedException {
        System.out.println("\n-- newScheduledThreadPool --");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        try {
            // One-shot delayed task
            ScheduledFuture<String> delayed = scheduler.schedule(
                    timedTask("Delayed", 0), 100, TimeUnit.MILLISECONDS);

            // Fixed-rate: first run at 50ms, then every 80ms
            // scheduleAtFixedRate — next run starts 80ms after PREVIOUS START
            java.util.concurrent.atomic.AtomicInteger fixedRateCount
                    = new java.util.concurrent.atomic.AtomicInteger(0);
            ScheduledFuture<?> fixedRate = scheduler.scheduleAtFixedRate(() -> {
                int n = fixedRateCount.incrementAndGet();
                System.out.println("  fixedRate tick " + n);
            }, 50, 80, TimeUnit.MILLISECONDS);

            // Fixed-delay: next run starts 80ms after PREVIOUS FINISH
            java.util.concurrent.atomic.AtomicInteger fixedDelayCount
                    = new java.util.concurrent.atomic.AtomicInteger(0);
            ScheduledFuture<?> fixedDelay = scheduler.scheduleWithFixedDelay(() -> {
                int n = fixedDelayCount.incrementAndGet();
                System.out.println("  fixedDelay tick " + n);
            }, 50, 80, TimeUnit.MILLISECONDS);

            Thread.sleep(400);  // let both repeat a few times
            fixedRate.cancel(false);
            fixedDelay.cancel(false);

            System.out.println("  fixedRate ran " + fixedRateCount.get() + " times");
            System.out.println("  fixedDelay ran " + fixedDelayCount.get() + " times");

        } finally {
            scheduler.shutdown();
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    // ── 5. Custom ThreadPoolExecutor ──────────────────────────────────────────

    static void customPoolDemo() throws InterruptedException {
        System.out.println("\n-- Custom ThreadPoolExecutor --");
        /*
         * Configuration:
         *   corePoolSize    = 2  — always keep 2 threads alive
         *   maximumPoolSize = 4  — burst up to 4
         *   keepAliveTime   = 30s — extra threads (>core) idle at most 30s
         *   workQueue       = ArrayBlockingQueue(10) — bounded queue
         *   CallerRunsPolicy — when queue+max threads exhausted, caller runs task
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2, 4,
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        try {
            List<Future<?>> futures = new java.util.ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                final int id = i;
                futures.add(executor.submit(() -> {
                    System.out.printf("  custom task %d | pool=%d active=%d queue=%d%n",
                            id,
                            executor.getPoolSize(),
                            executor.getActiveCount(),
                            executor.getQueue().size());
                    try { Thread.sleep(30); } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }));
            }
            for (Future<?> f : futures) {
                try { f.get(); } catch (ExecutionException e) {
                    System.err.println("Task error: " + e.getCause());
                }
            }
            System.out.println("  completedTaskCount: " + executor.getCompletedTaskCount());
        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    // ── 6. Graceful shutdown pattern ──────────────────────────────────────────

    /**
     * Production-grade shutdown helper.
     * Tries graceful shutdown first; forcefully kills after timeout.
     */
    static void shutdownGracefully(ExecutorService pool, long timeout, TimeUnit unit) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(timeout, unit)) {
                List<Runnable> pending = pool.shutdownNow();
                System.out.println("  forced shutdown; unstarted tasks: " + pending.size());
                if (!pool.awaitTermination(timeout, unit)) {
                    System.err.println("  pool did not terminate");
                }
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        System.out.println("=== Executor Framework Demo ===");
        fixedPoolDemo();
        cachedPoolDemo();
        singleThreadDemo();
        scheduledDemo();
        customPoolDemo();
        System.out.println("\nMain done.");
    }
}
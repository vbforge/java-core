package com.vbforge.java_memory_model;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The happens-before relation — the ONLY thing the Java Memory Model promises you.
 *
 * <p>If action A <i>happens-before</i> action B, then everything A wrote is visible to B
 * (and A is ordered before B). If there is no happens-before edge between a write and a
 * read of the same variable, the read may return the old value, the new value, or — for
 * non-final fields — a stale mix. There is no "usually works".</p>
 *
 * <p>The edges you will use every day:</p>
 * <ol>
 *   <li>Program order — inside one thread, earlier statements happen-before later ones</li>
 *   <li>Volatile — a write to a volatile field happens-before every later read of it</li>
 *   <li>Monitor — an unlock happens-before every later lock of the same monitor</li>
 *   <li>Thread start — {@code Thread.start()} happens-before everything in the started thread</li>
 *   <li>Thread join — everything in a thread happens-before {@code join()} returning</li>
 *   <li>Executors — submitting a task happens-before it runs; its actions happen-before {@code Future.get()}</li>
 *   <li>Transitivity — A hb B and B hb C implies A hb C ("piggybacking")</li>
 * </ol>
 *
 * <p>Every method below returns what the observing thread saw. Each value is guaranteed
 * by an edge listed above, so the results are deterministic.</p>
 */
public class HappensBeforeDemo {

    public static void main(String[] args) throws Exception {
        HappensBeforeDemo demo = new HappensBeforeDemo();

        System.out.println("=== 1. Program order ===");
        System.out.println("programOrderDemo()    = " + programOrderDemo());
        System.out.println("---");

        System.out.println("=== 2. Volatile write -> volatile read ===");
        System.out.println("volatileDemo()        = " + demo.volatileDemo());
        System.out.println("---");

        System.out.println("=== 3. Monitor unlock -> lock ===");
        System.out.println("monitorDemo()         = " + demo.monitorDemo());
        System.out.println("---");

        System.out.println("=== 4. Thread start / join ===");
        int[] startJoin = demo.startAndJoinDemo();
        System.out.println("seen by thread        = " + startJoin[0]);
        System.out.println("seen by main          = " + startJoin[1]);
        System.out.println("---");

        System.out.println("=== 5. Transitivity ===");
        int[] chain = demo.transitivityDemo();
        System.out.println("x = " + chain[0] + ", y = " + chain[1]);
        System.out.println("---");

        System.out.println("=== 6. Executor + Future ===");
        int[] exec = demo.executorDemo();
        System.out.println("task saw " + exec[0] + ", caller saw " + exec[1]);
    }

    // ═════════════════════════════════════════════════════════════════════
    // 1. PROGRAM ORDER
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Within ONE thread, every action happens-before every later action.
    // Why:    It is why single-threaded code never surprises you, even though the compiler
    //         and CPU reorder instructions: any reordering must be undetectable
    //         by that same thread ("as-if-serial").
    // Gotcha: Program order says nothing about what ANOTHER thread observes.

    /** @return 3 — the second statement always sees the first one's write */
    static int programOrderDemo() {
        int a = 1;
        int b = a + 1;      // depends on a: must observe a == 1
        return a + b;
    }

    // ═════════════════════════════════════════════════════════════════════
    // 2. VOLATILE
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   A volatile write happens-before every subsequent read of that field.
    // Why:    It publishes EVERYTHING the writer did before the volatile write — not just the
    //         volatile field itself. Plain writes made earlier "ride along".
    // Cost:   O(1); stops some compiler/CPU reordering around the access.
    // Gotcha: The guarantee starts only once the reader has SEEN the volatile write.
    //         Reading `data` before checking `ready` gives you no guarantee at all.

    private int data;                       // plain field, published by the volatile flag
    private volatile boolean ready;

    /** @return 42 — guaranteed, because the reader saw {@code ready == true} */
    int volatileDemo() {
        data = 0;
        ready = false;

        int[] observed = new int[1];
        Thread reader = new Thread(() -> {
            while (!ready) {
                Thread.yield();             // spin until the volatile write becomes visible
            }
            observed[0] = data;             // ready write hb this read, and data=42 hb ready write
        });
        reader.start();

        data = 42;                          // plain write ...
        ready = true;                       // ... published by this volatile write

        joinQuietly(reader);
        return observed[0];
    }

    // ═════════════════════════════════════════════════════════════════════
    // 3. MONITOR (synchronized)
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Releasing a monitor happens-before every later acquisition of the SAME monitor.
    // Why:    Locks give mutual exclusion AND visibility. Everything done inside the first
    //         critical section is visible inside the next one.
    // Gotcha: Different lock objects = no edge. Readers must lock the same monitor, and
    //         wait() must sit in a `while` loop (spurious wake-ups, missed conditions).

    private final Object lock = new Object();
    private int guardedValue;               // plain, guarded by `lock`
    private boolean guardedReady;           // plain, guarded by `lock`

    /** @return 7 — guaranteed by unlock -> lock ordering, even though both fields are plain */
    int monitorDemo() {
        guardedValue = 0;
        guardedReady = false;

        int[] observed = new int[1];
        Thread reader = new Thread(() -> {
            synchronized (lock) {
                while (!guardedReady) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                observed[0] = guardedValue;
            }
        });
        reader.start();

        synchronized (lock) {
            guardedValue = 7;
            guardedReady = true;
            lock.notifyAll();
        }                                   // unlock happens-before the reader's re-lock

        joinQuietly(reader);
        return observed[0];
    }

    // ═════════════════════════════════════════════════════════════════════
    // 4. THREAD START / JOIN
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   start() happens-before the first action of the new thread.
    //         The last action of a thread happens-before another thread's join() returns.
    // Why:    You can hand input to a thread through plain fields before start(), and
    //         read its result from plain fields after join() — no volatile needed.
    // Gotcha: Read the result BEFORE join() and you have a data race.

    private int input;
    private int output;

    /** @return {seenByThread, seenByMain} = {10, 20} */
    int[] startAndJoinDemo() {
        input = 10;                         // written before start()
        Thread worker = new Thread(() -> {
            int seen = input;               // start() hb this read: sees 10
            output = seen * 2;              // plain write inside the thread
        });
        worker.start();
        joinQuietly(worker);                // thread's actions hb join() returning
        return new int[]{input, output};    // output is guaranteed to be 20
    }

    // ═════════════════════════════════════════════════════════════════════
    // 5. TRANSITIVITY ("piggybacking")
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   If A hb B and B hb C then A hb C.
    // Why:    One volatile hand-off is enough to publish MANY plain writes, and edges can
    //         be chained across several threads.
    // Gotcha: Every link in the chain must really be an edge; one missing link breaks it.

    private int chainX;
    private int chainY;
    private volatile boolean flagA;
    private volatile boolean flagB;

    /** @return {x, y} = {5, 6}: thread A writes x, thread B reads x and writes y, main reads both */
    int[] transitivityDemo() {
        chainX = 0;
        chainY = 0;
        flagA = false;
        flagB = false;

        Thread a = new Thread(() -> {
            chainX = 5;                     // plain
            flagA = true;                   // volatile publish
        });
        Thread b = new Thread(() -> {
            while (!flagA) {
                Thread.yield();
            }
            chainY = chainX + 1;            // sees x == 5 because flagA write hb this read
            flagB = true;                   // volatile publish
        });
        b.setDaemon(true);
        b.start();
        a.start();

        while (!flagB) {
            Thread.yield();
        }
        // main sees flagB -> B's writes visible; B saw flagA -> A's writes visible; hence transitively x too
        int[] result = {chainX, chainY};
        joinQuietly(a);
        joinQuietly(b);
        return result;
    }

    // ═════════════════════════════════════════════════════════════════════
    // 6. EXECUTOR + FUTURE
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Submitting a task happens-before the task starts; the task's actions
    //         happen-before the result is returned by Future.get().
    // Why:    Thread pools "just work" with plain fields for input and output — as long as you
    //         hand data over through submit() and get() instead of sneaking it in some other way.
    // Gotcha: Mutating shared state from several tasks is NOT covered by these two edges.

    private int execInput;
    private int execOutput;

    /** @return {seenByTask, seenByCaller} = {3, 9} */
    int[] executorDemo() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            execInput = 3;                              // before submit()
            Callable<Integer> task = () -> {
                int seen = execInput;                   // submit() hb task start: sees 3
                execOutput = seen * 3;                  // plain write inside the task
                return seen;
            };
            Future<Integer> future = pool.submit(task);
            int seenByTask = future.get();              // task actions hb get() returning
            return new int[]{seenByTask, execOutput};   // execOutput is guaranteed to be 9
        } finally {
            pool.shutdown();
        }
    }

    private static void joinQuietly(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while joining " + thread.getName(), e);
        }
    }
}

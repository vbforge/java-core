package com.vbforge.java_memory_model;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Lazy initialisation and the double-checked locking (DCL) idiom.
 *
 * <p>Goal: create an expensive object at most once, only when first needed, from many threads,
 * without paying for a lock on every access.</p>
 *
 * <p>Options, from simplest to fastest:</p>
 * <ol>
 *   <li>{@code synchronized} getter — always correct, locks on every call</li>
 *   <li>Double-checked locking — check without lock, then check again under lock.
 *       Correct <b>only if the field is {@code volatile}</b></li>
 *   <li>Initialization-on-demand holder — a static nested class; the JVM's class-initialisation
 *       lock does the work, no volatile and no explicit lock in your code</li>
 * </ol>
 *
 * <p>The broken classic, shown here as text only because it can not be demonstrated reliably:</p>
 * <pre>
 * // BROKEN: 'instance' is not volatile
 * if (instance == null) {
 *     synchronized (this) {
 *         if (instance == null) {
 *             instance = new Expensive();   // publish may be visible before the constructor's writes
 *         }
 *     }
 * }
 * return instance;                          // fast path has NO happens-before edge
 * </pre>
 * <p>The fast-path read happens outside the lock, so nothing orders it after the constructor's
 * writes: it can see a non-null reference to a half-built object (see
 * {@code SafePublicationDemo}, section 1). The Java Memory Model permits this; whether it ever
 * shows up depends on JIT and hardware, which is exactly why it survives testing.
 * {@code volatile} adds the missing edge.</p>
 */
public class DoubleCheckedLockingDemo {

    public static void main(String[] args) {
        int threads = 16;

        System.out.println("=== 1. Synchronized lazy ===");
        System.out.println(describe(synchronizedLazyDemo(threads)));
        System.out.println("---");

        System.out.println("=== 2. Double-checked locking (volatile) ===");
        System.out.println(describe(doubleCheckedLockingDemo(threads)));
        System.out.println("---");

        System.out.println("=== 3. Initialization-on-demand holder ===");
        System.out.println(describe(holderIdiomDemo(threads)));
        System.out.println("---");

        System.out.println("=== 4. Laziness ===");
        int[] lazy = lazinessDemo();
        System.out.println("factory calls before first get: " + lazy[0]);
        System.out.println("factory calls after two gets  : " + lazy[1]);
    }

    private static String describe(int[] report) {
        return "creations = " + report[0] + ", distinct instances seen by threads = " + report[1];
    }

    // ═════════════════════════════════════════════════════════════════════
    // Helpers
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Releases {@code threads} threads at the same instant (via a start gate) and records
     * the object each one got from {@code lazy.get()}.
     */
    static Object[] raceGet(Supplier<?> lazy, int threads) {
        Object[] seen = new Object[threads];
        CountDownLatch startGate = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int slot = i;
            workers[i] = new Thread(() -> {
                try {
                    startGate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                seen[slot] = lazy.get();
            });
            workers[i].start();
        }
        startGate.countDown();
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while joining", e);
            }
        }
        return seen;
    }

    /** @return how many different objects (by identity) appear in {@code objects} */
    static int distinctInstances(Object[] objects) {
        Map<Object, Boolean> identities = new IdentityHashMap<>();
        for (Object o : objects) {
            identities.put(o, Boolean.TRUE);
        }
        return identities.size();
    }

    // ═════════════════════════════════════════════════════════════════════
    // 1. SYNCHRONIZED LAZY
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   The whole getter is synchronized; the check and the creation happen under one lock.
    // Why:    Simplest correct solution. The lock gives atomicity (create once) and
    //         visibility (every caller sees the fully built object).
    // Cost:   O(1), but every call pays for the monitor — uncontended it is cheap on modern
    //         JVMs, under contention callers queue up.
    // Gotcha: Do not optimise this until a profiler tells you to.

    /** Thread-safe lazy holder that locks on every call. */
    static class SynchronizedLazy<T> implements Supplier<T> {
        private final Supplier<T> factory;
        private T value;

        SynchronizedLazy(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override
        public synchronized T get() {
            if (value == null) {
                value = factory.get();
            }
            return value;
        }
    }

    /** @return {creations, distinctInstances} = {1, 1} */
    static int[] synchronizedLazyDemo(int threads) {
        AtomicInteger creations = new AtomicInteger();
        Supplier<Object> lazy = new SynchronizedLazy<>(() -> {
            creations.incrementAndGet();
            return new Object();
        });
        Object[] seen = raceGet(lazy, threads);
        return new int[]{creations.get(), distinctInstances(seen)};
    }

    // ═════════════════════════════════════════════════════════════════════
    // 2. DOUBLE-CHECKED LOCKING (with volatile)
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   First check without a lock (fast path), second check under the lock (slow path).
    // Why:    After initialisation, callers pay only for one volatile read, never for a lock.
    // Cost:   O(1); fast path = one volatile read. Slow path runs at most once per racing thread.
    // Gotcha: 1. The field MUST be volatile (see the class comment for the broken variant).
    //         2. Copy the volatile into a local variable: one volatile read on the fast path
    //            instead of two (Effective Java, Item 83).
    //         3. If the factory may return null, "value == null" can not mean "not initialised".

    /** Correct double-checked locking. */
    static class DoubleCheckedLazy<T> implements Supplier<T> {
        private final Supplier<T> factory;
        private volatile T value;          // volatile is what makes this correct

        DoubleCheckedLazy(Supplier<T> factory) {
            this.factory = factory;
        }

        @Override
        public T get() {
            T result = value;              // single volatile read on the fast path
            if (result == null) {
                synchronized (this) {
                    result = value;        // second check, now under the lock
                    if (result == null) {
                        result = factory.get();
                        value = result;    // volatile write: safe publication
                    }
                }
            }
            return result;
        }
    }

    /** @return {creations, distinctInstances} = {1, 1} */
    static int[] doubleCheckedLockingDemo(int threads) {
        AtomicInteger creations = new AtomicInteger();
        Supplier<Object> lazy = new DoubleCheckedLazy<>(() -> {
            creations.incrementAndGet();
            return new Object();
        });
        Object[] seen = raceGet(lazy, threads);
        return new int[]{creations.get(), distinctInstances(seen)};
    }

    // ═════════════════════════════════════════════════════════════════════
    // 3. INITIALIZATION-ON-DEMAND HOLDER
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   The instance lives in a static field of a private nested class. That class is
    //         initialised the first time getInstance() touches it — and not before.
    // Why:    The JVM guarantees class initialisation runs once and is visible to all
    //         threads (it takes an internal lock). You get laziness + thread-safety with no
    //         volatile, no synchronized, no double check.
    // Cost:   O(1); after initialisation a plain static final read.
    // Gotcha: Only for static singletons — it can not take a per-call factory argument.

    private static final AtomicInteger HOLDER_CREATIONS = new AtomicInteger();

    /** Singleton created by the holder idiom. */
    static final class HolderSingleton {
        private HolderSingleton() {
            HOLDER_CREATIONS.incrementAndGet();
        }

        private static final class Holder {
            // runs when Holder is first used, exactly once, under the JVM's class-init lock
            static final HolderSingleton INSTANCE = new HolderSingleton();
        }

        static HolderSingleton getInstance() {
            return Holder.INSTANCE;
        }
    }

    /**
     * @return {creations, distinctInstances} = {1, 1}. {@code creations} counts all constructions
     *         ever made in this JVM, so it is 1 no matter how many times this demo is called.
     */
    static int[] holderIdiomDemo(int threads) {
        Object[] seen = raceGet(HolderSingleton::getInstance, threads);
        return new int[]{HOLDER_CREATIONS.get(), distinctInstances(seen)};
    }

    // ═════════════════════════════════════════════════════════════════════
    // 4. LAZINESS
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Nothing is created until the first get(); later calls reuse the same object.
    // Why:    Saves startup time and memory for resources that might never be used.

    /** @return {factoryCallsBeforeFirstGet, factoryCallsAfterTwoGets} = {0, 1} */
    static int[] lazinessDemo() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<Object> lazy = new DoubleCheckedLazy<>(() -> {
            calls.incrementAndGet();
            return new Object();
        });
        int before = calls.get();
        lazy.get();
        lazy.get();
        return new int[]{before, calls.get()};
    }
}

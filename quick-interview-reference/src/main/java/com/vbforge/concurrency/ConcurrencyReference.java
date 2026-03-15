package com.vbforge.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  CONCURRENCY REFERENCE — Part 1: Core Threading
 *  Topics: Thread lifecycle · synchronized · volatile ·
 *          happens-before · locks · atomic · deadlock
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class ConcurrencyReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. THREAD LIFECYCLE
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  NEW → RUNNABLE → (RUNNING) → BLOCKED/WAITING/TIMED_WAITING → TERMINATED
     *
     *  NEW:           Thread created, not yet started
     *  RUNNABLE:      Eligible to run — may or may not be on CPU
     *  BLOCKED:       Waiting to acquire a monitor lock (synchronized)
     *  WAITING:       Waiting indefinitely: Object.wait(), Thread.join(), LockSupport.park()
     *  TIMED_WAITING: Waiting with timeout: sleep(), wait(ms), join(ms)
     *  TERMINATED:    run() finished or exception thrown
     *
     *  Note: JVM has no separate RUNNING state — it's part of RUNNABLE.
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. synchronized vs volatile vs AtomicInteger ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  volatile:
     *  ✅ Visibility — writes visible to all threads immediately
     *  ❌ NOT atomic — read-modify-write (i++) is still NOT thread-safe
     *  USE FOR: simple flags, stop signals between threads
     *
     *  synchronized:
     *  ✅ Mutual exclusion + visibility
     *  ✅ Atomic compound operations
     *  ❌ Only one thread at a time — can be a bottleneck
     *  USE FOR: protecting a block of code or compound operations
     *
     *  AtomicInteger (java.util.concurrent.atomic):
     *  ✅ Lock-free atomic operations (CAS — compare-and-swap)
     *  ✅ Better throughput than synchronized for single variables
     *  ❌ Only for single variable updates — not compound multi-variable ops
     *  USE FOR: counters, sequence generators
     */

    // ❌ Not thread-safe — read-modify-write is not atomic
    static int unsafeCounter = 0;
    static void unsafeIncrement() { unsafeCounter++; }  // RACE CONDITION

    // ✅ Thread-safe with synchronized
    static int syncCounter = 0;
    static synchronized void safeIncrement() { syncCounter++; }

    // ✅ Thread-safe with AtomicInteger — faster for simple counters
    static AtomicInteger atomicCounter = new AtomicInteger(0);
    static void atomicIncrement() { atomicCounter.incrementAndGet(); }

    // ✅ volatile for flags — visible across threads, no mutual exclusion needed
    static volatile boolean stopRequested = false;
    static void stopWorker() { stopRequested = true; }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. HAPPENS-BEFORE RELATIONSHIP ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Happens-before guarantees that one action's effects are visible to another.
     *
     *  HAPPENS-BEFORE rules (key ones):
     *  1. Program order:     within a thread, each action HB the next
     *  2. Monitor lock:      unlock of monitor HB every subsequent lock of same monitor
     *  3. volatile write:    write to volatile HB every subsequent read of that variable
     *  4. Thread start:      Thread.start() HB any action in started thread
     *  5. Thread join:       all actions in a thread HB Thread.join() returning
     *  6. Transitivity:      if A HB B and B HB C, then A HB C
     *
     *  WITHOUT happens-before: thread may see stale cached values (CPU cache / register).
     */
    static int sharedData   = 0;
    static volatile boolean ready = false;

    static void writerThread() {
        sharedData = 42;      // (1)
        ready = true;         // (2) volatile write — establishes HB
    }

    static void readerThread() {
        while (!ready) {}     // spin-wait on volatile read
        // guaranteed to see sharedData = 42 because:
        // (1) HB (2) via program order
        // (2) HB volatile read via volatile rule
        // (1) HB volatile read via transitivity
        System.out.println(sharedData);  // always 42
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. ReentrantLock vs synchronized — side-by-side
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Feature                  │ synchronized        │ ReentrantLock
     *  ─────────────────────────┼─────────────────────┼──────────────────────
     *  Syntax                   │ keyword             │ explicit lock/unlock
     *  Fairness                 │ ❌ No               │ ✅ Optional (new ReentrantLock(true))
     *  Try-lock (non-blocking)  │ ❌ No               │ ✅ tryLock()
     *  Timed lock               │ ❌ No               │ ✅ tryLock(time, unit)
     *  Interruptible lock       │ ❌ No               │ ✅ lockInterruptibly()
     *  Multiple conditions      │ ❌ Single wait/notify│ ✅ newCondition()
     *  Auto-unlock on exception │ ✅ Yes              │ ❌ Must use try/finally
     *
     *  RULE: Use synchronized for simple cases. Use ReentrantLock when you need
     *        tryLock(), fairness, or multiple condition variables.
     */
    static final ReentrantLock lock = new ReentrantLock();

    static void reentrantLockExample() throws InterruptedException {
        // ✅ Always unlock in finally — otherwise lock is never released on exception
        lock.lock();
        try {
            // critical section
            System.out.println("locked");
        } finally {
            lock.unlock();  // MUST be in finally
        }

        // ✅ tryLock — non-blocking attempt
        if (lock.tryLock()) {
            try {
                // got lock
            } finally {
                lock.unlock();
            }
        } else {
            // could not acquire lock — do something else
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. DEADLOCK — causes and prevention ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  FOUR CONDITIONS for deadlock (all must hold):
     *  1. Mutual exclusion    — resource held by only one thread
     *  2. Hold and wait       — thread holds one resource and waits for another
     *  3. No preemption       — resources cannot be forcibly taken
     *  4. Circular wait       — T1 waits for T2's resource, T2 waits for T1's resource
     *
     *  PREVENTION STRATEGIES:
     *  ✅ Lock ordering: always acquire locks in the SAME order across all threads
     *  ✅ tryLock() with timeout: give up and retry rather than block forever
     *  ✅ Lock hierarchy: assign a global ordering to all locks
     *  ✅ Avoid nested locks: don't hold lock A while acquiring lock B
     */
    static final Object lockA = new Object();
    static final Object lockB = new Object();

    // ❌ DEADLOCK: Thread1 holds A, waits for B. Thread2 holds B, waits for A.
    static void deadlockThread1() {
        synchronized (lockA) {
            synchronized (lockB) { /* work */ }
        }
    }
    static void deadlockThread2() {
        synchronized (lockB) {   // ❌ reversed order
            synchronized (lockA) { /* work */ }
        }
    }

    // ✅ FIXED: both threads acquire in same order A → B
    static void safeThread1() { synchronized (lockA) { synchronized (lockB) { /* work */ } } }
    static void safeThread2() { synchronized (lockA) { synchronized (lockB) { /* work */ } } }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Using volatile for i++ (compound op)      → still a race condition
     *  ❌ Acquiring locks in different orders        → deadlock
     *  ❌ Not releasing lock in finally              → lock never released on exception
     *  ❌ Calling wait() outside synchronized block  → IllegalMonitorStateException
     *  ❌ Ignoring InterruptedException              → masks thread interruption
     *  ❌ Using Thread.stop() / Thread.suspend()     → deprecated — leave threads in bad state
     *  ❌ Synchronized on non-final field            → reference can change, wrong monitor
     */

    public static void main(String[] args) throws Exception {
        reentrantLockExample();
        System.out.println("Atomic counter: " + atomicCounter.get());
    }
}

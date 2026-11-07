// IMPLEMENTATION
package com.vbforge.practice.practice_10_volatile;

import lombok.Getter;
import lombok.Setter;

/**
 * Demonstrates the volatile keyword — the lightest synchronization tool in Java.
 *
 * The problem volatile solves — memory visibility:
 *   Without any synchronization, the JVM and CPU are free to:
 *     - Cache variable values in CPU registers or L1/L2 cache per thread
 *     - Reorder reads and writes for performance (CPU out-of-order execution,
 *       JIT compiler optimisations, store buffers)
 *   This means one thread's write may NEVER become visible to another thread.
 *   A reader thread can loop forever on a stale cached value — even if the
 *   writer has updated the variable in its own cache.
 *
 * What volatile guarantees:
 *   1. VISIBILITY   — every write to a volatile variable is immediately flushed
 *                     to main memory; every read bypasses the cache and reads
 *                     from main memory directly.
 *   2. HAPPENS-BEFORE — a write to a volatile variable happens-before every
 *                     subsequent read of that same variable by any thread.
 *                     This prevents certain instruction reorderings around the
 *                     volatile access.
 *   3. NO CACHING   — the JIT will never hoist a volatile read out of a loop
 *                     or eliminate a "redundant" write.
 *
 * What volatile does NOT guarantee:
 *   - Atomicity of compound actions.
 *     volatile int count; count++ is still read-modify-write — three steps,
 *     not atomic. Use AtomicInteger or synchronized for that.
 *
 * Classic use cases:
 *   a) Stop/cancel flag                 — one thread writes, others read
 *   b) Status / state flag              — initialisation complete, connected, etc.
 *   c) Double-checked locking (DCL)     — safe publication of a singleton
 *                                         (requires volatile on the instance field)
 *   d) Lightweight published reference  — safely publishing an immutable object
 *
 * volatile vs synchronized vs Atomic:
 * ┌────────────────┬──────────────┬──────────────┬─────────────────────────────┐
 * │                │  volatile    │ synchronized │ AtomicXxx                   │
 * ├────────────────┼──────────────┼──────────────┼─────────────────────────────┤
 * │ Visibility     │ ✔            │ ✔            │ ✔                           │
 * │ Atomicity      │ ✘ (not CAS)  │ ✔            │ ✔ (CAS-based)               │
 * │ Mutual exclusion│ ✘           │ ✔            │ ✘ (lock-free, not mutex)    │
 * │ Blocking       │ never        │ may block    │ never (spins on CAS)        │
 * │ Overhead       │ lowest       │ highest      │ medium                      │
 * └────────────────┴──────────────┴──────────────┴─────────────────────────────┘
 *
 * Summary (from the Person/age example, scene 5):
 *   Visibility: Ensures that when one thread updates a volatile variable, all other threads see the updated value immediately.
 *   No Instruction Reordering: Prevents the JVM or CPU from reordering instructions in a way that could break the expected behavior.
 *   No Atomicity: Does not make compound operations (like age++) atomic. For atomicity, use AtomicInteger or synchronized.
 */
public class VolatileDemo {

    // ── 1. Visibility problem — without volatile ───────────────────────────────

    /**
     * Without volatile the JIT may cache `running` in a register.
     * The reader thread might loop forever even after the writer sets it to false.
     *
     * This class intentionally demonstrates the WRONG pattern — do not copy it.
     */
    static class UnsafeStopFlag {
        boolean running = true;       // NOT volatile — visibility NOT guaranteed

        void stop()           { running = false; }
        boolean isRunning()   { return running;  }
    }

    // ── 2. Correct stop flag — with volatile ──────────────────────────────────

    /**
     * volatile guarantees that the write in stop() is immediately visible
     * to the reader thread checking isRunning() in its loop.
     */
    static class VolatileStopFlag {
        private volatile boolean running = true;

        public void stop()          { running = false; }
        public boolean isRunning()  { return running;  }
    }

    // ── 3. Safe publication of initialisation result ──────────────────────────

    /**
     * One thread performs heavy initialisation and publishes the result.
     * Without volatile, other threads might see initialized=true but still
     * read a stale (null or partial) value for result — reordering hazard.
     *
     * volatile on `initialized` creates a happens-before edge:
     *   write(result)  →  write(initialized=true)  →  read(initialized)  →  read(result)
     * Readers that see initialized==true are guaranteed to see the correct result.
     */
    static class SafePublication {
        private String           result      = null;
        private volatile boolean initialized = false;    // the "guard" volatile

        /** Called by one writer thread. */
        public void initialize(String value) {
            result      = value;         // written BEFORE the volatile flag
            initialized = true;          // volatile write — flushes result too
        }

        /**
         * Called by any reader thread.
         * If initialized is true, result is guaranteed to be visible and correct.
         */
        public String getIfReady() {
            if (initialized) {           // volatile read — establishes happens-before
                return result;
            }
            return null;
        }

        public boolean isInitialized() { return initialized; }
    }

    // ── 4. Double-checked locking (DCL) singleton ─────────────────────────────

    /**
     * The classic DCL pattern — correct only when the instance field is volatile.
     *
     * Without volatile, the JVM may publish a partially-constructed object:
     *   The write to `instance` (the reference) could be reordered BEFORE the
     *   constructor body completes, letting another thread observe a non-null
     *   but uninitialised object.
     *
     * volatile prevents that reordering, making DCL safe from Java 5 onwards.
     *
     * Thread-safe lazy Singleton using Double-Checked Locking:
     *   - volatile on the instance field — ensures visibility of the instance across
     *     threads and prevents instruction reordering by the JVM/compiler.
     *   - first check outside the synchronized block (fast path),
     *     second check inside the block (safe path).
     *   This is one of the standard ways to implement a thread-safe lazy singleton in Java;
     *   it provides lazy initialization while minimizing the synchronization overhead.
     */
    static class DclSingleton {
        private static volatile DclSingleton instance;    // volatile is essential
        private final String value;

        /** Private constructor prevents instantiation from outside the class. */
        private DclSingleton() {
            this.value = "singleton-instance";
        }

        public static DclSingleton getInstance() {
            // First check (without synchronization) - fast path after initialization
            if (instance == null) {                       // first check — no lock
                synchronized (DclSingleton.class) {       // Acquire lock on the class object
                    // Second check (inside synchronized block) - safe path
                    if (instance == null) {               // second check — under lock
                        instance = new DclSingleton();
                    }
                }
            }
            return instance;
        }

        public String getValue() { return value; }
    }

    // ── 5. Showing volatile does NOT provide atomicity ────────────────────────

    /**
     * volatile int does NOT make counter++ atomic.
     * Two threads can still interleave the read-modify-write steps.
     * This class exists to demonstrate the pitfall — it is NOT correct.
     */
    static class VolatileCounter {
        volatile int count = 0;           // volatile — visibility yes, atomicity NO

        void increment() { count++; }    // still a race condition!
        int  get()       { return count; }
    }

    // ── 6. Volatile field on a domain object (Person.age) ─────────────────────

    static class Person {

        @Setter
        @Getter
        private volatile Integer age; // volatile ensures visibility

        public Person(String name, Long id, Integer age) {
            this.age = age;
        }
    }

    /**
     * Writer updates the age after 2 seconds; the reader spins on it.
     *
     * What if age were NOT volatile?
     * If remove the volatile keyword:
     *
     *     private Integer age; // Not volatile
     *
     * The reader thread might not see the updated value for a long time (or ever), due to thread-local caching.
     * This is called a visibility problem.
     */
    static void personAgeVisibilityScene() throws InterruptedException {

        Person person = new Person("Bob", 1L, 23);

        //Thread #1: updates the age
        Thread thread1_writer = new Thread(()->{
           try{
               Thread.sleep(2000);
               person.setAge(24);                   //updated
               System.out.println("Writer thread updated age to: " + person.getAge());
           } catch (InterruptedException e){
               Thread.currentThread().interrupt();
           }
        });

        //Thread #2: reads the age in a loop
        Thread thread2_reader = new Thread(()->{
            int lastSeenAge = person.getAge();
            while (lastSeenAge < 24) {
                if (lastSeenAge != person.getAge()) {
                    lastSeenAge = person.getAge();
                    System.out.println("Reader thread saw new age: " + lastSeenAge);
                }
            }
        });

        thread1_writer.start();
        thread2_reader.start();

        thread1_writer.join();
        thread2_reader.join();
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Volatile Demo ===\n");

        // --- 1. Volatile stop flag ---
        System.out.println("-- Volatile stop flag --");
        VolatileStopFlag flag = new VolatileStopFlag();

        Thread worker = new Thread(() -> {
            int iterations = 0;
            while (flag.isRunning()) {
                iterations++;
                // tight loop — no sleep; volatile ensures the flag is re-read each iteration
            }
            System.out.println("[worker] stopped after " + iterations + " iterations");
        }, "Worker");

        worker.start();
        Thread.sleep(20);
        flag.stop();          // volatile write — immediately visible to worker
        worker.join();
        System.out.println("Worker terminated: " + !worker.isAlive());

        // --- 2. Safe publication ---
        System.out.println("\n-- Safe publication --");
        SafePublication pub = new SafePublication();

        Thread reader = new Thread(() -> {
            // Spin until initialized is visible
            while (!pub.isInitialized()) { Thread.onSpinWait(); }
            System.out.println("[reader] result = " + pub.getIfReady());
        }, "Reader");

        reader.start();
        Thread.sleep(30);
        pub.initialize("ready-value");
        reader.join();

        // --- 3. DCL singleton ---
        System.out.println("\n-- DCL singleton --");
        DclSingleton s1 = DclSingleton.getInstance();
        DclSingleton s2 = DclSingleton.getInstance();
        System.out.println("same instance: " + (s1 == s2));
        System.out.println("value: " + s1.getValue());
        System.out.println("Singleton 1: " + s1.hashCode());
        System.out.println("Singleton 2: " + s2.hashCode());
        System.out.println("Are both instances the same? " + (s1 == s2));

        // --- 4. volatile does NOT guarantee atomicity ---
        System.out.println("\n-- VolatileCounter (atomicity pitfall) --");
        VolatileCounter vc = new VolatileCounter();
        int threads = 5, increments = 10_000;
        java.util.List<Thread> ts = new java.util.ArrayList<>();
        for (int i = 0; i < threads; i++) {
            ts.add(new Thread(() -> {
                for (int j = 0; j < increments; j++) vc.increment();
            }));
        }
        ts.forEach(Thread::start);
        for (Thread t : ts) t.join();
        System.out.printf("VolatileCounter expected %d, got %d ← likely wrong!%n",
                threads * increments, vc.get());

        // --- 5. Volatile field on a domain object (Person.age) ---
        System.out.println("\n-- Person.age (volatile visibility) --");
        personAgeVisibilityScene();

        System.out.println("\nMain done.");
    }
}
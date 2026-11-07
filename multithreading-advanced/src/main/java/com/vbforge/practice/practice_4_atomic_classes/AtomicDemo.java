// IMPLEMENTATION
package com.vbforge.practice.practice_4_atomic_classes;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * Demonstrates java.util.concurrent.atomic — lock-free thread-safe primitives.
 *
 * How they work — Compare-And-Swap (CAS):
 *   Atomic classes use CPU-level CAS instructions (e.g., CMPXCHG on x86).
 *   CAS takes three operands: memory address, expected value, new value.
 *   It atomically: IF memory == expected THEN memory = new; return true
 *                  ELSE return false (no change)
 *
 *   This is a hardware primitive — uninterruptible, no OS lock involved.
 *   Failed CAS loops (spin) and retries — this is "optimistic concurrency".
 *
 * Why CAS beats synchronized for simple operations:
 *   - No thread ever blocks — no context switch overhead
 *   - No deadlock possible
 *   - Scales better under moderate contention
 *   - Under HIGH contention, CAS spin loops waste CPU — LongAdder is better
 *
 * Key classes:
 * ┌──────────────────────┬──────────────────────────────────────────────────────┐
 * │ AtomicInteger        │ int with atomic inc/dec/CAS/getAndUpdate             │
 * │ AtomicLong           │ long equivalent                                      │
 * │ AtomicBoolean        │ boolean with atomic compareAndSet                    │
 * │ AtomicReference<V>   │ object reference with atomic compareAndSet           │
 * │ AtomicStampedReference│ reference + int stamp — solves ABA problem          │
 * │ LongAdder            │ high-throughput counter; sum() for final result      │
 * │ LongAccumulator      │ generalised LongAdder with custom accumulate function│
 * └──────────────────────┴──────────────────────────────────────────────────────┘
 *
 * ABA problem:
 *   Thread 1 reads value A.
 *   Thread 2 changes A → B → A.
 *   Thread 1's CAS succeeds (sees A) — but the value was changed and changed back.
 *   For counters this rarely matters, but for linked data structures (stacks, queues)
 *   it can cause corruption. AtomicStampedReference adds a version stamp to detect it.
 *
 * LongAdder vs AtomicLong:
 *   - AtomicLong: single cell, fast when contention is low
 *   - LongAdder:  multiple internal cells; threads update different cells,
 *                 reducing CAS collisions; sum() aggregates all cells.
 *   - Use LongAdder for high-frequency counters (metrics, hit counts).
 *   - Use AtomicLong when you need get-and-set / compareAndSet semantics.
 */
public class AtomicDemo {

    // ── 1. AtomicInteger ──────────────────────────────────────────────────────

    /**
     * Thread-safe counter using AtomicInteger.
     * No synchronized, no locks — CAS handles mutual exclusion at CPU level.
     */
    static class AtomicCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        public void increment()          { count.incrementAndGet();       }
        public void decrement()          { count.decrementAndGet();       }
        public void add(int delta)       { count.addAndGet(delta);        }
        public int  get()                { return count.get();            }

        /**
         * Increments only if the current value is below the threshold.
         * compareAndSet retries in a loop until it wins the CAS or the
         * condition fails — this is an "atomic conditional update" pattern.
         */
        public boolean incrementIfBelow(int threshold) {
            while (true) {
                int current = count.get();
                if (current >= threshold) return false;
                // CAS: if count is still `current`, set to current+1
                if (count.compareAndSet(current, current + 1)) return true;
                // else: another thread changed it — retry the loop
            }
        }

        /**
         * getAndUpdate / updateAndGet accept a UnaryOperator — functional style.
         * Both are implemented via CAS loop internally.
         */
        public int doubleAndGet() {
            return count.updateAndGet(v -> v * 2);
        }
    }

    // ── 2. AtomicBoolean — one-shot flag ──────────────────────────────────────

    /**
     * Guarantees that an action executes EXACTLY ONCE across any number of threads,
     * without synchronized.
     *
     * compareAndSet(false, true) atomically:
     *   - Checks whether the value is false
     *   - If yes: sets it to true and returns true (this thread "wins")
     *   - If no:  returns false (another thread already flipped it)
     */
    static class OnceAction {
        private final AtomicBoolean fired = new AtomicBoolean(false);

        /**
         * Returns true and runs action the first time; false on all subsequent calls.
         */
        public boolean fire(Runnable action) {
            if (fired.compareAndSet(false, true)) {
                action.run();
                return true;
            }
            return false;
        }

        public boolean hasFired() { return fired.get(); }
    }

    // ── 3. AtomicReference — lock-free object reference swap ─────────────────

    /**
     * Immutable snapshot of configuration.
     * Updating the config is atomic — readers always see a consistent snapshot,
     * never a half-written object.
     */
    static class Config {
        final String host;
        final int    port;

        Config(String host, int port) { this.host = host; this.port = port; }

        @Override public String toString() { return host + ":" + port; }
    }

    static class AtomicConfig {
        private final AtomicReference<Config> ref =
                new AtomicReference<>(new Config("localhost", 8080));

        public Config get()             { return ref.get(); }
        public void   set(Config c)     { ref.set(c); }

        /**
         * Atomically replaces config only if it currently matches expected.
         * Returns true if the swap succeeded.
         */
        public boolean replace(Config expected, Config updated) {
            return ref.compareAndSet(expected, updated);
        }
    }

    // ── 4. AtomicStampedReference — ABA-safe reference ────────────────────────

    /**
     * Demonstrates AtomicStampedReference to detect the ABA problem.
     * The stamp acts as a monotonically incrementing version number.
     * A CAS fails if either the reference OR the stamp has changed.
     */
    static class AbaDemo {
        // stamp starts at 0; each successful CAS bumps it by 1
        private final AtomicStampedReference<String> ref =
                new AtomicStampedReference<>("A", 0);

        public String getValue()  { return ref.getReference(); }
        public int    getStamp()  { return ref.getStamp();     }

        /**
         * Attempts CAS. Returns true only if BOTH value AND stamp match expected.
         */
        public boolean update(String expected, int expectedStamp,
                              String newValue) {
            return ref.compareAndSet(expected, newValue, expectedStamp, expectedStamp + 1);
        }
    }

    // ── 5. LongAdder — high-throughput counting ───────────────────────────────

    /**
     * Preferred over AtomicLong when many threads increment frequently.
     * Internally uses a cell array — threads update different cells, then
     * sum() adds them. This avoids the CAS contention hotspot of AtomicLong.
     *
     * Trade-off: sum() is not a real-time snapshot under concurrent modification.
     * Use it when you collect totals periodically (metrics, stats), not for
     * fine-grained coordination.
     */
    static class ThroughputCounter {
        private final LongAdder adder = new LongAdder();

        public void increment()    { adder.increment(); }
        public void add(long n)    { adder.add(n);      }
        public long sum()          { return adder.sum();}
        public void reset()        { adder.reset();     }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Atomic Demo ===\n");
        final int THREADS    = 10;
        final int INCREMENTS = 1_000;

        // --- 1. AtomicCounter ---
        System.out.println("-- AtomicInteger counter --");
        AtomicCounter counter = new AtomicCounter();
        java.util.List<Thread> threads = new java.util.ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) counter.increment();
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
        System.out.printf("Expected %d, got %d%n", THREADS * INCREMENTS, counter.get());

        // --- 2. incrementIfBelow ---
        System.out.println("\n-- incrementIfBelow --");
        AtomicCounter bounded = new AtomicCounter();
        System.out.println("increment to 3: " + bounded.incrementIfBelow(3)); // true
        System.out.println("increment to 3: " + bounded.incrementIfBelow(3)); // true
        System.out.println("increment to 3: " + bounded.incrementIfBelow(3)); // true
        System.out.println("increment to 3: " + bounded.incrementIfBelow(3)); // false (at 3)
        System.out.println("value: " + bounded.get());

        // --- 3. AtomicBoolean one-shot ---
        System.out.println("\n-- OnceAction --");
        OnceAction once = new OnceAction();
        System.out.println("fire 1: " + once.fire(() -> System.out.println("  action!")));
        System.out.println("fire 2: " + once.fire(() -> System.out.println("  action!")));
        System.out.println("fire 3: " + once.fire(() -> System.out.println("  action!")));

        // --- 4. AtomicReference ---
        System.out.println("\n-- AtomicConfig --");
        AtomicConfig cfg = new AtomicConfig();
        System.out.println("initial: " + cfg.get());
        Config old = cfg.get();
        Config updated = new Config("prod-server", 443);
        System.out.println("CAS replace: " + cfg.replace(old, updated));
        System.out.println("new config : " + cfg.get());
        System.out.println("CAS again (wrong expected): " + cfg.replace(old, updated)); // false

        // --- 5. AtomicStampedReference ---
        System.out.println("\n-- ABA demo --");
        AbaDemo aba = new AbaDemo();
        System.out.println("initial: " + aba.getValue() + " stamp=" + aba.getStamp());
        aba.update("A", 0, "B");
        System.out.println("after A→B: " + aba.getValue() + " stamp=" + aba.getStamp());
        aba.update("B", 1, "A");
        System.out.println("after B→A: " + aba.getValue() + " stamp=" + aba.getStamp());
        // stale CAS with old stamp fails even though value looks like "A"
        boolean stale = aba.update("A", 0, "C");
        System.out.println("stale CAS (stamp=0): " + stale + " ← correctly rejected");

        // --- 6. LongAdder ---
        System.out.println("\n-- LongAdder (high-throughput) --");
        ThroughputCounter adder = new ThroughputCounter();
        threads.clear();
        for (int i = 0; i < THREADS; i++) {
            threads.add(new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) adder.increment();
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) t.join();
        System.out.printf("LongAdder expected %d, got %d%n",
                (long) THREADS * INCREMENTS, adder.sum());

        System.out.println("\nMain done.");
    }
}
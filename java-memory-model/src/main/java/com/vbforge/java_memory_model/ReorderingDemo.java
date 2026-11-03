package com.vbforge.java_memory_model;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Reordering — why "the code runs top to bottom" is only true for the thread that wrote it.
 *
 * <p>Three different layers may change the order in which memory operations become visible
 * to OTHER threads:</p>
 * <ul>
 *   <li>the compiler (javac keeps source order, but the JIT does not)</li>
 *   <li>the CPU (out-of-order execution, store buffers)</li>
 *   <li>the memory hierarchy (caches reach other cores at different times)</li>
 * </ul>
 *
 * <p>The JMM allows all of it, as long as the thread doing the work cannot tell
 * ("as-if-serial"). Other threads <i>can</i> tell — unless a happens-before edge
 * forbids it.</p>
 *
 * <p>Two classic "litmus tests" are run below. Each iteration starts two fresh threads and
 * records the outcome. For plain fields the interesting outcome is <i>permitted</i> but whether you see
 * it depends on your hardware and JIT; for volatile fields and locks it is <i>forbidden</i> and
 * will never appear. The tests assert only the forbidden side.</p>
 */
public class ReorderingDemo {

    public static void main(String[] args) {
        int iterations = 20_000;

        System.out.println("=== 1. As-if-serial ===");
        System.out.println(Arrays.toString(asIfSerialDemo()));
        System.out.println("---");

        System.out.println("=== 2. Store buffering litmus (r1, r2) ===");
        System.out.println("index: 0=(0,0)  1=(0,1)  2=(1,0)  3=(1,1)");
        System.out.println("plain    fields: " + Arrays.toString(storeBuffering(false, iterations)));
        System.out.println("volatile fields: " + Arrays.toString(storeBuffering(true, iterations)) + "   <- index 0 is always 0");
        System.out.println("---");

        System.out.println("=== 3. Message passing litmus: flag seen, data stale? ===");
        System.out.println("plain    flag: " + countStaleReads(false, iterations) + " stale reads (permitted, hardware/JIT-dependent)");
        System.out.println("volatile flag: " + countStaleReads(true, iterations) + " stale reads (forbidden)");
        System.out.println("---");

        System.out.println("=== 4. Same test under a lock ===");
        System.out.println("synchronized : " + countStaleReadsSynchronized(iterations) + " stale reads (forbidden)");
    }

    // ═════════════════════════════════════════════════════════════════════
    // 1. AS-IF-SERIAL
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   The compiler/CPU may reorder independent statements, never dependent ones.
    // Why:    It is what keeps single-threaded programs simple while still allowing the
    //         optimisations that make them fast.
    // Gotcha: "Independent" is judged from ONE thread's point of view. Another thread
    //         reading these variables has no such protection.

    /** @return {1, 2, 10} — always, from the point of view of the executing thread */
    static int[] asIfSerialDemo() {
        int x = 1;
        int y = x + 1;      // depends on x: can never be moved before the x assignment
        int z = 10;         // independent: may be executed earlier or later, unobservably
        return new int[]{x, y, z};
    }

    // ═════════════════════════════════════════════════════════════════════
    // 2. STORE BUFFERING LITMUS TEST
    // ═════════════════════════════════════════════════════════════════════
    //
    //     Thread 1:  x = 1;  r1 = y;        Thread 2:  y = 1;  r2 = x;
    //
    // Intuition ("one of the writes must come first") says (r1, r2) = (0, 0) is impossible.
    // With plain fields the JMM does allow it: each thread may read before the other's
    // write has become visible (or the read is moved before the write).
    //
    // With volatile fields all accesses form ONE total order consistent with every thread's
    // program order. (0,0) would need r1=0 -> y=1 -> r2=0 -> x=1 -> r1=0: a cycle. Impossible.
    //
    // Cost:   volatile store -> load ordering is the most expensive barrier on most CPUs.
    // Gotcha: Volatile gives you sequential consistency for those fields only.

    /** One round of the store-buffering test. */
    interface StoreBufferingLitmus {
        void actor1();

        void actor2();

        int observed1();

        int observed2();
    }

    static class PlainStoreBuffering implements StoreBufferingLitmus {
        private int x;
        private int y;
        private int r1;
        private int r2;

        @Override
        public void actor1() {
            x = 1;
            r1 = y;
        }

        @Override
        public void actor2() {
            y = 1;
            r2 = x;
        }

        @Override
        public int observed1() {
            return r1;
        }

        @Override
        public int observed2() {
            return r2;
        }
    }

    static class VolatileStoreBuffering implements StoreBufferingLitmus {
        private volatile int x;
        private volatile int y;
        private int r1;     // written by one thread, read by main after join(): join gives the edge
        private int r2;

        @Override
        public void actor1() {
            x = 1;
            r1 = y;
        }

        @Override
        public void actor2() {
            y = 1;
            r2 = x;
        }

        @Override
        public int observed1() {
            return r1;
        }

        @Override
        public int observed2() {
            return r2;
        }
    }

    /**
     * Runs the store-buffering test {@code iterations} times.
     *
     * @return counts indexed by {@code (r1 << 1) | r2}: 0=(0,0), 1=(0,1), 2=(1,0), 3=(1,1);
     *         the counts always add up to {@code iterations}; index 0 is always 0 when
     *         {@code useVolatile} is true
     */
    static int[] storeBuffering(boolean useVolatile, int iterations) {
        int[] outcomes = new int[4];
        for (int i = 0; i < iterations; i++) {
            StoreBufferingLitmus litmus = useVolatile ? new VolatileStoreBuffering() : new PlainStoreBuffering();
            Thread t1 = new Thread(litmus::actor1);
            Thread t2 = new Thread(litmus::actor2);
            t1.start();
            t2.start();
            joinQuietly(t1);
            joinQuietly(t2);
            outcomes[(litmus.observed1() << 1) | litmus.observed2()]++;
        }
        return outcomes;
    }

    // ═════════════════════════════════════════════════════════════════════
    // 3 + 4. MESSAGE PASSING LITMUS TEST
    // ═════════════════════════════════════════════════════════════════════
    //
    //     Writer:  data = 1;  flag = 1;       Reader:  r1 = flag;  r2 = data;
    //
    // "Stale read" = (r1 == 1 && r2 == 0): the reader saw the flag but not the data.
    //  - plain fields : permitted — the two writes may become visible in either order
    //  - volatile flag: forbidden — data = 1 happens-before flag = 1, and the reader that
    //                   saw flag == 1 inherits that edge
    //  - same lock    : forbidden — the reader's critical section runs entirely before or
    //                   entirely after the writer's
    //
    // This is exactly the data/ready pattern from the happens-before demo, seen from the
    // other side: here we count how often it would have gone wrong without the edge.

    /** One round of the message-passing test. */
    interface MessagePassingLitmus {
        void writer();

        void reader();

        boolean sawStaleData();
    }

    private static final int READER_ATTEMPTS = 1_000;

    static class PlainMessagePassing implements MessagePassingLitmus {
        private int data;
        private int flag;
        private int r1;
        private int r2;

        @Override
        public void writer() {
            data = 1;
            flag = 1;
        }

        @Override
        public void reader() {
            for (int i = 0; i < READER_ATTEMPTS && r1 == 0; i++) {
                r1 = flag;
            }
            r2 = data;
        }

        @Override
        public boolean sawStaleData() {
            return r1 == 1 && r2 == 0;
        }
    }

    static class VolatileMessagePassing implements MessagePassingLitmus {
        private int data;
        private volatile int flag;
        private int r1;
        private int r2;

        @Override
        public void writer() {
            data = 1;
            flag = 1;       // volatile write publishes data = 1
        }

        @Override
        public void reader() {
            for (int i = 0; i < READER_ATTEMPTS && r1 == 0; i++) {
                r1 = flag;
            }
            r2 = data;
        }

        @Override
        public boolean sawStaleData() {
            return r1 == 1 && r2 == 0;
        }
    }

    static class SynchronizedMessagePassing implements MessagePassingLitmus {
        private final Object lock = new Object();
        private int data;
        private int flag;
        private int r1;
        private int r2;

        @Override
        public void writer() {
            synchronized (lock) {
                data = 1;
                flag = 1;
            }
        }

        @Override
        public void reader() {
            for (int i = 0; i < READER_ATTEMPTS && r1 == 0; i++) {
                synchronized (lock) {
                    r1 = flag;
                    r2 = data;      // both reads in ONE critical section
                }
            }
        }

        @Override
        public boolean sawStaleData() {
            return r1 == 1 && r2 == 0;
        }
    }

    private static int countStale(int iterations, Supplier<MessagePassingLitmus> factory) {
        int stale = 0;
        for (int i = 0; i < iterations; i++) {
            MessagePassingLitmus litmus = factory.get();
            Thread writer = new Thread(litmus::writer);
            Thread reader = new Thread(litmus::reader);
            reader.start();
            writer.start();
            joinQuietly(writer);
            joinQuietly(reader);
            if (litmus.sawStaleData()) {
                stale++;
            }
        }
        return stale;
    }

    /**
     * @return number of rounds (out of {@code iterations}) in which the reader saw the flag but
     *         stale data; always 0 when {@code useVolatile} is true, hardware/JIT-dependent otherwise
     */
    static int countStaleReads(boolean useVolatile, int iterations) {
        return countStale(iterations, useVolatile ? VolatileMessagePassing::new : PlainMessagePassing::new);
    }

    /** @return always 0 — a shared lock rules out the stale outcome */
    static int countStaleReadsSynchronized(int iterations) {
        return countStale(iterations, SynchronizedMessagePassing::new);
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

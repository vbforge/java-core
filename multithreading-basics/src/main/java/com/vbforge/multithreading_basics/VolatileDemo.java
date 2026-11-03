package com.vbforge.multithreading_basics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code volatile} keyword: visibility, happens-before publication,
 * why it does not make count++ atomic, and safe snapshot swapping.
 *
 * Run: mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_basics.VolatileDemo"
 */
public class VolatileDemo {

    public static void main(String[] args) throws Exception {
        stopFlagDemo();
        System.out.println("---");
        publicationDemo();
        System.out.println("---");
        notAtomicDemo();
        System.out.println("---");
        snapshotSwapDemo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. Visibility: a volatile stop flag
    //
    // What:  a write to a volatile field is guaranteed to become visible to every later
    //        read of that field by any thread.
    // Why:   without volatile the JVM may keep the value in a register or cache and the
    //        reader may never see the update. A plain `boolean running` can make the loop
    //        below run forever; we do NOT run that variant because "may hang" is not
    //        something a test can prove.
    // Complexity: a volatile read is cheap (no lock, no blocking); a write adds a memory
    //        barrier. Neither blocks other threads.
    // ─────────────────────────────────────────────────────────────────────
    public static class StoppableWorker implements Runnable {
        private volatile boolean running = true;
        private volatile long iterations;

        @Override
        public void run() {
            long n = 0;
            while (running) {
                n++;
            }
            iterations = n;
        }

        public void stop() {
            running = false;
        }

        public long getIterations() {
            return iterations;
        }
    }

    static void stopFlagDemo() throws InterruptedException {
        StoppableWorker worker = new StoppableWorker();
        Thread t = new Thread(worker);
        t.start();
        Thread.sleep(50);
        worker.stop();
        t.join();
        System.out.println("worker stopped, iterations > 0: " + (worker.getIterations() > 0));
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Safe publication through a volatile flag (happens-before)
    //
    // What:  everything the writer did BEFORE writing the volatile field is visible to a
    //        reader that sees the new value of that field.
    // Why:   lets you publish ordinary fields (payload) with a single volatile flag.
    // Gotcha: the order matters: write the data first, raise the flag last; read the
    //        flag first, read the data afterwards.
    // ─────────────────────────────────────────────────────────────────────
    public static class Publisher {
        private int payload;                // plain field, published through `ready`
        private volatile boolean ready;

        public void publish(int value) {
            payload = value;                // 1) write the data
            ready = true;                   // 2) then raise the flag
        }

        public int awaitPayload() {
            while (!ready) {                // 1) read the flag
                Thread.yield();
            }
            return payload;                 // 2) then read the data
        }
    }

    static void publicationDemo() throws InterruptedException {
        final Publisher publisher = new Publisher();
        final int[] received = new int[1];
        Thread reader = new Thread(() -> received[0] = publisher.awaitPayload());
        reader.start();
        publisher.publish(42);
        reader.join();
        System.out.println("reader received " + received[0]);
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. volatile does NOT make compound actions atomic
    //
    // What:  count++ is read → add → write. volatile guarantees each single read and
    //        each single write, not the sequence of them.
    // Why:   it is the classic misuse: "I made it volatile, so it is thread-safe."
    // Fix:   AtomicInteger.incrementAndGet() or a synchronized block (see the
    //        Synchronization pair).
    // ─────────────────────────────────────────────────────────────────────
    public static class VolatileCounter {
        private volatile int count;

        public void increment() {
            count++;                        // still a race!
        }

        public int get() {
            return count;
        }
    }

    static void notAtomicDemo() throws InterruptedException {
        VolatileCounter volatileCounter = new VolatileCounter();
        AtomicInteger atomicCounter = new AtomicInteger();
        SynchronizationDemo.hammer(4, 100_000, volatileCounter::increment);
        SynchronizationDemo.hammer(4, 100_000, atomicCounter::incrementAndGet);
        System.out.println("volatile int: " + volatileCounter.get() + " (expected 400000)");
        System.out.println("AtomicInteger: " + atomicCounter.get());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. volatile reference to an IMMUTABLE snapshot
    //
    // What:  instead of mutating shared state field by field, build a new immutable object
    //        and swap one volatile reference to it.
    // Why:   readers always see a complete, consistent snapshot — never "half old, half new".
    // Gotcha: volatile protects the REFERENCE only. If the object it points to is mutable,
    //        its contents are still unprotected.
    // ─────────────────────────────────────────────────────────────────────
    public static final class Range {
        private final int low;
        private final int high;

        public Range(int low, int high) {
            this.low = low;
            this.high = high;
        }

        public int getLow() {
            return low;
        }

        public int getHigh() {
            return high;
        }
    }

    public static class RangeHolder {
        private volatile Range current = new Range(0, 100);

        public void shift(int offset) {
            Range old = current;
            current = new Range(old.getLow() + offset, old.getHigh() + offset);
        }

        public Range get() {
            return current;
        }
    }

    static void snapshotSwapDemo() {
        RangeHolder holder = new RangeHolder();
        holder.shift(10);
        Range r = holder.get();
        System.out.println("range = [" + r.getLow() + ", " + r.getHigh() + "]");
    }
}

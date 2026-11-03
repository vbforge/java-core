package com.vbforge.java_memory_model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Safe publication — how to hand an object to another thread so that it sees the object fully built.
 *
 * <p>Creating an object and storing its reference in a shared field are two separate steps.
 * Without a happens-before edge between them, another thread may see the reference
 * <i>and</i> stale (default) field values — a "half-built" object.</p>
 *
 * <p>Safe publication idioms (each one creates an edge):</p>
 * <ol>
 *   <li>Store the reference in a {@code volatile} field or an {@code AtomicReference}</li>
 *   <li>Store it in a field guarded by a lock (reader uses the same lock)</li>
 *   <li>Put it in a concurrent collection ({@code ConcurrentHashMap}, {@code BlockingQueue}, …)</li>
 *   <li>Initialise it from a static initializer (class initialisation is lock-protected)</li>
 *   <li>Make its fields {@code final} (special rule — see section 2)</li>
 * </ol>
 *
 * <p><b>Honesty note:</b> the "unsafe" demo is permitted to misbehave, not required to. Whether
 * you see a half-built object depends on JIT and hardware. The tests assert only the guaranteed side.</p>
 */
public class SafePublicationDemo {

    public static void main(String[] args) {
        int iterations = 5_000;

        System.out.println("=== 1. Unsafe publication (plain field, mutable fields) ===");
        System.out.println("half-built objects seen: " + unsafePublicationDemo(iterations)
                + " of " + iterations + "  (permitted, not guaranteed)");
        System.out.println("---");

        System.out.println("=== 2. Final fields: safe even through a plain field ===");
        System.out.println("half-built objects seen: " + finalFieldsDemo(iterations));
        System.out.println("---");

        System.out.println("=== 3. Safe publication idioms ===");
        int[] idioms = safeIdiomsDemo(iterations);
        System.out.println("volatile       : " + idioms[0]);
        System.out.println("AtomicReference: " + idioms[1]);
        System.out.println("synchronized   : " + idioms[2]);
        System.out.println("ConcurrentMap  : " + idioms[3]);
        System.out.println("---");

        System.out.println("=== 4. 'this' escapes from a constructor ===");
        int[] leak = thisEscapeDemo(99);
        System.out.println("value seen during construction: " + leak[0]);
        System.out.println("value seen after  construction: " + leak[1]);
    }

    // ═════════════════════════════════════════════════════════════════════
    // Building blocks
    // ═════════════════════════════════════════════════════════════════════

    /** Read-only view of a point so the harness can treat mutable and immutable points alike. */
    interface Point {
        int x();

        int y();
    }

    /** Mutable point: NON-final fields, so a racy reader may see the defaults (0, 0). */
    static class MutablePoint implements Point {
        private int x;
        private int y;

        MutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }
    }

    /** Immutable point: FINAL fields get a special guarantee (JLS 17.5). */
    static class ImmutablePoint implements Point {
        private final int x;
        private final int y;

        ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int x() {
            return x;
        }

        @Override
        public int y() {
            return y;
        }
    }

    /** One-way mailbox: the writer {@code put}s a point, the reader {@code get}s it (or null if nothing yet). */
    interface Channel {
        void put(Point point);

        Point get();
    }

    /** NO synchronization: a data race on {@code slot}. */
    static class PlainChannel implements Channel {
        private Point slot;

        @Override
        public void put(Point point) {
            slot = point;
        }

        @Override
        public Point get() {
            return slot;
        }
    }

    static class VolatileChannel implements Channel {
        private volatile Point slot;

        @Override
        public void put(Point point) {
            slot = point;
        }

        @Override
        public Point get() {
            return slot;
        }
    }

    static class AtomicChannel implements Channel {
        private final AtomicReference<Point> slot = new AtomicReference<>();

        @Override
        public void put(Point point) {
            slot.set(point);
        }

        @Override
        public Point get() {
            return slot.get();
        }
    }

    static class LockedChannel implements Channel {
        private Point slot;     // guarded by "this"

        @Override
        public synchronized void put(Point point) {
            slot = point;
        }

        @Override
        public synchronized Point get() {
            return slot;
        }
    }

    static class MapChannel implements Channel {
        private final ConcurrentMap<String, Point> map = new ConcurrentHashMap<>();

        @Override
        public void put(Point point) {
            map.put("point", point);
        }

        @Override
        public Point get() {
            return map.get("point");
        }
    }

    private static final int READER_ATTEMPTS = 1_000;

    /**
     * Runs {@code iterations} rounds. In each round a writer thread publishes a point (1, 2)
     * through a fresh channel while a reader thread polls the channel (bounded retries).
     *
     * @return number of rounds in which the reader obtained a point whose coordinates were NOT (1, 2)
     */
    static int countHalfBuilt(Supplier<Channel> channelFactory, Supplier<Point> pointFactory, int iterations) {
        int halfBuilt = 0;
        for (int i = 0; i < iterations; i++) {
            Channel channel = channelFactory.get();
            boolean[] corrupted = new boolean[1];

            Thread writer = new Thread(() -> channel.put(pointFactory.get()));
            Thread reader = new Thread(() -> {
                Point seen = null;
                for (int attempt = 0; attempt < READER_ATTEMPTS && seen == null; attempt++) {
                    seen = channel.get();
                }
                if (seen != null && (seen.x() != 1 || seen.y() != 2)) {
                    corrupted[0] = true;
                }
            });
            reader.start();
            writer.start();
            joinQuietly(writer);
            joinQuietly(reader);
            if (corrupted[0]) {
                halfBuilt++;
            }
        }
        return halfBuilt;
    }

    // ═════════════════════════════════════════════════════════════════════
    // 1. UNSAFE PUBLICATION
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Publishing a mutable object through a plain shared field.
    // Why:    `new MutablePoint(1, 2)` is allocate -> write x -> write y -> publish reference.
    //         Nothing stops the publishing write from becoming visible BEFORE the field
    //         writes, so a reader can get the reference together with x == 0, y == 0.
    // Gotcha: This is the root cause of the broken double-checked locking idiom (see the
    //         DoubleCheckedLocking classes).

    /** @return rounds with a half-built object: between 0 and {@code iterations}, never asserted exactly */
    static int unsafePublicationDemo(int iterations) {
        return countHalfBuilt(PlainChannel::new, () -> new MutablePoint(1, 2), iterations);
    }

    // ═════════════════════════════════════════════════════════════════════
    // 2. FINAL FIELDS
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   When a constructor finishes, the JMM "freezes" its final fields. Any thread that
    //         sees the reference sees the final fields' correct values — even if the
    //         reference itself was published through a data race.
    // Why:    It makes immutable objects thread-safe "for free" and is why String, Integer,
    //         and every well-written value class can be shared without locks.
    // Gotcha: Only if `this` does not escape during construction (section 4), and only the
    //         final fields (and what they point to at freeze time) are covered.

    /** @return always 0 — guaranteed by the final-field rule */
    static int finalFieldsDemo(int iterations) {
        return countHalfBuilt(PlainChannel::new, () -> new ImmutablePoint(1, 2), iterations);
    }

    // ═════════════════════════════════════════════════════════════════════
    // 3. SAFE IDIOMS
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   The same MUTABLE point, but published through a channel that creates a
    //         happens-before edge: volatile, AtomicReference, lock, ConcurrentHashMap.
    // Why:    "Effectively immutable" objects (never modified after publication) are safe as
    //         long as they were SAFELY published.
    // Gotcha: Safe publication covers state at publication time. If the object is modified
    //         afterwards, every reader and writer still needs its own synchronization.

    /** @return {volatile, atomicReference, synchronized, concurrentMap} half-built counts — all 0, guaranteed */
    static int[] safeIdiomsDemo(int iterations) {
        Supplier<Point> mutable = () -> new MutablePoint(1, 2);
        return new int[]{
                countHalfBuilt(VolatileChannel::new, mutable, iterations),
                countHalfBuilt(AtomicChannel::new, mutable, iterations),
                countHalfBuilt(LockedChannel::new, mutable, iterations),
                countHalfBuilt(MapChannel::new, mutable, iterations)
        };
    }

    // ═════════════════════════════════════════════════════════════════════
    // 4. 'this' ESCAPE
    // ═════════════════════════════════════════════════════════════════════
    //
    // What:   Passing `this` (or an inner class / lambda that captures it) to other code from
    //         inside a constructor, before the constructor has finished.
    // Why:    The object is observable while still incomplete. The final-field guarantee does
    //         not apply, and even in ONE thread the final field still has its default value.
    // Fix:    Never leak `this` from a constructor. Use a static factory method that builds the
    //         object first and registers it afterwards.
    // Gotcha: Registering listeners or starting threads in a constructor is the usual culprit.

    /** Registers itself with a callback BEFORE its final field is assigned — deliberately wrong. */
    static class LeakyCounter {
        private final int value;

        LeakyCounter(int value, Consumer<LeakyCounter> registry) {
            registry.accept(this);      // 'this' escapes: value is still 0 here
            this.value = value;
        }

        int value() {
            return value;
        }
    }

    /** Registers itself only AFTER construction, via a factory — the fix. */
    static class SafeCounter {
        private final int value;

        private SafeCounter(int value) {
            this.value = value;
        }

        static SafeCounter createAndRegister(int value, Consumer<SafeCounter> registry) {
            SafeCounter counter = new SafeCounter(value);
            registry.accept(counter);   // fully constructed by now
            return counter;
        }

        int value() {
            return value;
        }
    }

    /**
     * @return {seenDuringConstruction, seenWithFactory}: {0, value} — deterministic, no threads involved
     */
    static int[] thisEscapeDemo(int value) {
        int[] seenByLeakyListener = new int[1];
        new LeakyCounter(value, leaked -> seenByLeakyListener[0] = leaked.value());

        int[] seenBySafeListener = new int[1];
        SafeCounter.createAndRegister(value, counter -> seenBySafeListener[0] = counter.value());

        return new int[]{seenByLeakyListener[0], seenBySafeListener[0]};
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

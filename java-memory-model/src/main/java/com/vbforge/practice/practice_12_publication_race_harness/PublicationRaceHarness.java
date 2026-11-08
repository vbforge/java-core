package com.vbforge.practice.practice_12_publication_race_harness;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Supplier;

/**
 * Shared fixture, not a concept: runs many rounds of "one thread publishes a point, another
 * thread tries to read it" through a {@link Slot}, and counts what the reader saw.
 *
 * <p>Every point is created as (EXPECTED_X, EXPECTED_Y). A reader that sees a non-null
 * reference but different coordinates has seen a half-built object.
 *
 * <p>The reader spins only a bounded number of times, so no run can hang, even if the JIT
 * hoists a plain read out of the loop.
 */
public final class PublicationRaceHarness {

    public static final int EXPECTED_X = 1;
    public static final int EXPECTED_Y = 2;
    private static final int SPINS = 200;

    private PublicationRaceHarness() {
    }

    /** One way of handing a reference from the writer thread to the reader thread. */
    public interface Slot {
        void publish(Point2D point);

        /** @return the published point, or null if nothing is visible (yet) */
        Point2D read();
    }

    public static final class Result {
        public final int rounds;
        public final int seen;        // rounds in which the reader found a non-null reference
        public final int halfBuilt;   // of those, rounds with wrong coordinates

        Result(int rounds, int seen, int halfBuilt) {
            this.rounds = rounds;
            this.seen = seen;
            this.halfBuilt = halfBuilt;
        }
    }

    public static Result run(int rounds, Supplier<Slot> slotFactory, Supplier<Point2D> pointFactory)
            throws InterruptedException {
        Slot[] slots = new Slot[rounds];
        for (int i = 0; i < rounds; i++) {
            slots[i] = slotFactory.get();       // created before the threads start: start edge
        }
        int[] seen = new int[1];
        int[] halfBuilt = new int[1];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread writer = new Thread(() -> {
            try {
                for (int i = 0; i < rounds; i++) {
                    start.await();
                    slots[i].publish(pointFactory.get());
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        Thread reader = new Thread(() -> {
            try {
                for (int i = 0; i < rounds; i++) {
                    start.await();
                    Point2D p = null;
                    for (int spin = 0; spin < SPINS && p == null; spin++) {
                        p = slots[i].read();
                    }
                    if (p != null) {
                        int x = p.x();
                        int y = p.y();
                        seen[0]++;
                        if (x != EXPECTED_X || y != EXPECTED_Y) {
                            halfBuilt[0]++;
                        }
                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        writer.start();
        reader.start();
        writer.join();
        reader.join();                          // join edge: counters are safe to read

        return new Result(rounds, seen[0], halfBuilt[0]);
    }
}

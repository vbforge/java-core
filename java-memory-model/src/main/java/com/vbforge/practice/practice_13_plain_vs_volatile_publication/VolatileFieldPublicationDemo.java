package com.vbforge.practice.practice_13_plain_vs_volatile_publication;

import com.vbforge.practice.practice_12_publication_race_harness.MutablePoint;
import com.vbforge.practice.practice_12_publication_race_harness.Point2D;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Result;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Slot;

/**
 * SAFE publication through a volatile field. The constructor's writes happen-before the
 * volatile write of the reference, so a reader that sees the reference sees a fully built
 * object. The same mutable class as in the plain demo, only the field differs.
 * Guaranteed, so asserted: half-built sightings are 0.
 */
public class VolatileFieldPublicationDemo {

    static class VolatileSlot implements Slot {
        private volatile Point2D point;              // the only change

        @Override
        public void publish(Point2D p) {
            point = p;
        }

        @Override
        public Point2D read() {
            return point;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Result r = PublicationRaceHarness.run(5_000, VolatileSlot::new,
                () -> new MutablePoint(PublicationRaceHarness.EXPECTED_X, PublicationRaceHarness.EXPECTED_Y));

        System.out.println("rounds:                  " + r.rounds);
        System.out.println("half-built sightings:    " + r.halfBuilt + "  (impossible with volatile)");
    }
}

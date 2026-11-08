package com.vbforge.practice.practice_13_plain_vs_volatile_publication;

import com.vbforge.practice.practice_12_publication_race_harness.MutablePoint;
import com.vbforge.practice.practice_12_publication_race_harness.Point2D;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Result;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Slot;

/**
 * UNSAFE publication: a mutable object handed over through a plain field. The reader may see
 * the reference before it sees the constructor's writes (x == 0, y == 0). That is allowed by
 * the memory model, but a given machine may never show it, so the count is not asserted or
 * printed. Only guaranteed facts are printed.
 */
public class PlainFieldPublicationDemo {

    static class PlainSlot implements Slot {
        private Point2D point;                       // plain field: no edge

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
        Result r = PublicationRaceHarness.run(5_000, PlainSlot::new,
                () -> new MutablePoint(PublicationRaceHarness.EXPECTED_X, PublicationRaceHarness.EXPECTED_Y));

        System.out.println("rounds:                  " + r.rounds);
        System.out.println("seen <= rounds:          " + (r.seen <= r.rounds));
        System.out.println("half-built sightings:    legal here, not counted");
    }
}

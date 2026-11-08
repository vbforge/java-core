package com.vbforge.practice.practice_15_final_fields_publication;

import com.vbforge.practice.practice_12_publication_race_harness.ImmutablePoint;
import com.vbforge.practice.practice_12_publication_race_harness.Point2D;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Result;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Slot;

/**
 * Final fields (JLS 17.5): an object whose fields are all final is seen fully built by every
 * thread that gets its reference, even through a PLAIN field (a data race). Same plain slot
 * as the unsafe demo, but with {@link ImmutablePoint}. Guaranteed, so asserted: 0 half-built.
 *
 * <p>Holds only while `this` does not escape the constructor (see practice_16) and covers only
 * the state at construction time.
 */
public class FinalFieldPublicationDemo {

    static class PlainSlot implements Slot {
        private Point2D point;                       // plain field, deliberately

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
                () -> new ImmutablePoint(PublicationRaceHarness.EXPECTED_X, PublicationRaceHarness.EXPECTED_Y));

        System.out.println("rounds:                  " + r.rounds);
        System.out.println("half-built sightings:    " + r.halfBuilt + "  (impossible with final fields)");
    }
}

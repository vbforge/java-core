package com.vbforge.practice.practice_14_safe_publication_idioms;

import com.vbforge.practice.practice_12_publication_race_harness.MutablePoint;
import com.vbforge.practice.practice_12_publication_race_harness.Point2D;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Result;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Slot;

/** Safe publication idiom: the reference is only touched under one lock (plain field, synchronized methods). */
public class LockedPublicationDemo {

    static class LockedSlot implements Slot {
        private Point2D point;                       // plain, guarded by this

        @Override
        public synchronized void publish(Point2D p) {
            point = p;
        }

        @Override
        public synchronized Point2D read() {
            return point;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Result r = PublicationRaceHarness.run(5_000, LockedSlot::new,
                () -> new MutablePoint(PublicationRaceHarness.EXPECTED_X, PublicationRaceHarness.EXPECTED_Y));

        System.out.println("half-built sightings: " + r.halfBuilt + "  (impossible under one lock)");
    }
}

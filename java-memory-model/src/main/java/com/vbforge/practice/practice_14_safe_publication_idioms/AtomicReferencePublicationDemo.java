package com.vbforge.practice.practice_14_safe_publication_idioms;

import com.vbforge.practice.practice_12_publication_race_harness.MutablePoint;
import com.vbforge.practice.practice_12_publication_race_harness.Point2D;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Result;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Slot;

import java.util.concurrent.atomic.AtomicReference;

/** Safe publication idiom: AtomicReference (set/get have volatile semantics). */
public class AtomicReferencePublicationDemo {

    static class AtomicSlot implements Slot {
        private final AtomicReference<Point2D> ref = new AtomicReference<>();

        @Override
        public void publish(Point2D p) {
            ref.set(p);
        }

        @Override
        public Point2D read() {
            return ref.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Result r = PublicationRaceHarness.run(5_000, AtomicSlot::new,
                () -> new MutablePoint(PublicationRaceHarness.EXPECTED_X, PublicationRaceHarness.EXPECTED_Y));

        System.out.println("half-built sightings: " + r.halfBuilt + "  (impossible with AtomicReference)");
    }
}

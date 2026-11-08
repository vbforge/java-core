package com.vbforge.practice.practice_14_safe_publication_idioms;

import com.vbforge.practice.practice_12_publication_race_harness.MutablePoint;
import com.vbforge.practice.practice_12_publication_race_harness.Point2D;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Result;
import com.vbforge.practice.practice_12_publication_race_harness.PublicationRaceHarness.Slot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Safe publication idiom: put into a ConcurrentHashMap. The put happens-before the get that
 * returns that value, so the point is fully built for the reader.
 */
public class ConcurrentMapPublicationDemo {

    static class MapSlot implements Slot {
        private final ConcurrentMap<String, Point2D> map = new ConcurrentHashMap<>();

        @Override
        public void publish(Point2D p) {
            map.put("point", p);
        }

        @Override
        public Point2D read() {
            return map.get("point");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Result r = PublicationRaceHarness.run(5_000, MapSlot::new,
                () -> new MutablePoint(PublicationRaceHarness.EXPECTED_X, PublicationRaceHarness.EXPECTED_Y));

        System.out.println("half-built sightings: " + r.halfBuilt + "  (impossible through a ConcurrentHashMap)");
    }
}

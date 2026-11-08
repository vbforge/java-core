package com.vbforge.practice.stamped.practice_10_stamped_optimistic_point;

import java.util.concurrent.locks.StampedLock;

/**
 * A 2D point protected by StampedLock.
 *
 * Optimistic read workflow:
 *   1. tryOptimisticRead() - get a stamp without acquiring any lock
 *   2. copy the fields into locals
 *   3. validate(stamp)     - check that no write happened meanwhile
 *   4. if invalid          - fall back to a real read lock
 *
 * Best for read-dominant data. StampedLock is NOT reentrant.
 */
public class StampedPointDemo {

    static class StampedPoint {
        private final StampedLock sl = new StampedLock();
        private double x;
        private double y;

        StampedPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void move(double deltaX, double deltaY) {
            long stamp = sl.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                sl.unlockWrite(stamp);
            }
        }

        /** Optimistic read - acquires no lock unless a write interfered. */
        public double distanceFromOrigin() {
            long stamp = sl.tryOptimisticRead();
            double currentX = x;
            double currentY = y;

            if (!sl.validate(stamp)) {
                stamp = sl.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    sl.unlockRead(stamp);
                }
            }
            return Math.hypot(currentX, currentY);
        }
    }

    public static void main(String[] args) {
        StampedPoint point = new StampedPoint(3.0, 4.0);
        System.out.printf("distance: %.1f (expected 5.0)%n", point.distanceFromOrigin());
        point.move(1.0, 0.0);
        System.out.printf("distance after move: %.2f%n", point.distanceFromOrigin());
    }
}

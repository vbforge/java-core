package com.vbforge.practice.practice_11_final_class;

import com.vbforge.practice.practice_11_final_class.ImmutablePoint;

/**
 * A final class cannot be subclassed.
 * // class ExtendedPoint extends ImmutablePoint {} // compile error: cannot inherit from final ImmutablePoint
 */
public class FinalClassDemo {

    public static void main(String[] args) {
        ImmutablePoint p1 = new ImmutablePoint(0, 0);
        ImmutablePoint p2 = new ImmutablePoint(3, 4);
        System.out.println("p1 = " + p1 + "  p2 = " + p2);
        System.out.printf("distance = %.2f%n", p1.distanceTo(p2));
    }
}

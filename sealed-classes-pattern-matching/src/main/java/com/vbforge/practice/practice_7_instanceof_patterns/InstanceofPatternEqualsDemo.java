package com.vbforge.practice.practice_7_instanceof_patterns;

import java.util.Objects;

/**
 * A compact equals: one expression with an instanceof pattern, then a field comparison.
 * The pattern is false for null, so no separate null check is needed.
 * (Point2D is final: with subclasses, instanceof-based equals needs more care.)
 */
public class InstanceofPatternEqualsDemo {

    static final class Point2D {
        private final int x;
        private final int y;

        Point2D(int x, int y) { this.x = x; this.y = y; }

        @Override public boolean equals(Object o) {
            return o instanceof Point2D p && x == p.x && y == p.y;
        }

        @Override public int hashCode() { return Objects.hash(x, y); }
    }

    public static void main(String[] args) {
        Point2D a = new Point2D(1, 2);
        System.out.println("equal point:    " + a.equals(new Point2D(1, 2)));
        System.out.println("other point:    " + a.equals(new Point2D(1, 3)));
        System.out.println("null:           " + a.equals(null));
        System.out.println("different type: " + a.equals("1,2"));
    }
}

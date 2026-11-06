package com.vbforge.practice.practice_14_records;

public record Point(double x, double y) {

    /** Compact constructor: validation without repeating the field assignments. */
    public Point {
        if (Double.isNaN(x) || Double.isNaN(y))
            throw new IllegalArgumentException("Coordinates cannot be NaN");
    }

    public double distanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Point origin() {
        return new Point(0.0, 0.0);
    }
}

package com.vbforge.practice.practice_11_final_class;

/** Final class with final fields - the two ingredients of an immutable value type. */
public final class ImmutablePoint {
    private final double x;
    private final double y;

    public ImmutablePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public final double distanceTo(ImmutablePoint other) {   // final is redundant inside a final class
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() { return x; }
    public double getY() { return y; }

    @Override public String toString() { return "(" + x + ", " + y + ")"; }
}

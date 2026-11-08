package com.vbforge.practice.practice_12_publication_race_harness;

/** Point with NON-final fields: a reader that gets the reference unsafely may see 0, 0. */
public class MutablePoint implements Point2D {

    private int x;
    private int y;

    public MutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }
}

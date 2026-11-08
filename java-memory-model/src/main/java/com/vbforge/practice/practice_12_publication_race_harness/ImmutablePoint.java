package com.vbforge.practice.practice_12_publication_race_harness;

/** Point with FINAL fields: safe even when published through a plain field (JLS 17.5). */
public class ImmutablePoint implements Point2D {

    private final int x;
    private final int y;

    public ImmutablePoint(int x, int y) {
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

package com.vbforge.practice.practice_10_when_not_to_mock;

/** Fake clock the test controls. */
public class FixedTimeSource implements TimeSource {

    private long now;

    public FixedTimeSource(long now) { this.now = now; }

    public void advance(long millis) { now += millis; }

    @Override
    public long nowMillis() { return now; }
}

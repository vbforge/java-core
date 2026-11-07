package com.vbforge.practice.practice_10_when_not_to_mock;

/** Production adapter: one line. */
public class SystemTimeSource implements TimeSource {
    @Override
    public long nowMillis() { return System.currentTimeMillis(); }
}

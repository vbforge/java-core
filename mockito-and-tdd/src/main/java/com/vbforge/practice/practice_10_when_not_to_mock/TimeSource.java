package com.vbforge.practice.practice_10_when_not_to_mock;

/** Our own boundary around the clock: a type we own, so it is safe to fake. */
public interface TimeSource {
    long nowMillis();
}

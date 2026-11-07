package com.vbforge.practice.practice_10_when_not_to_mock;

/** Wrap the clock in an interface you own, then fake it: the test becomes deterministic. */
public class OwnYourBoundariesDemo {

    public static void main(String[] args) {
        FixedTimeSource clock = new FixedTimeSource(1000);
        SessionToken token = new SessionToken(clock, 500);

        System.out.println("fresh            expired = " + token.isExpired());     // false
        clock.advance(400);
        System.out.println("after 400 ms     expired = " + token.isExpired());     // false
        clock.advance(200);
        System.out.println("after 600 ms     expired = " + token.isExpired());     // true

        System.out.println("production adapter works: " + (new SystemTimeSource().nowMillis() > 0));
    }
}

package com.vbforge.practice.practice_10_when_not_to_mock;

/** Expires after ttlMillis, measured on the injected TimeSource. */
public class SessionToken {

    private final TimeSource time;
    private final long issuedAt;
    private final long ttlMillis;

    public SessionToken(TimeSource time, long ttlMillis) {
        this.time = time;
        this.issuedAt = time.nowMillis();
        this.ttlMillis = ttlMillis;
    }

    public boolean isExpired() { return time.nowMillis() - issuedAt >= ttlMillis; }
}

package com.vbforge.practice.practice_4_exception_testing;

/** Unchecked business exception that carries data, so tests can assert on fields and not only on text. */
public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final long requested;
    private final long available;

    public InsufficientFundsException(long requested, long available) {
        super("requested " + requested + " but only " + available + " available");
        this.requested = requested;
        this.available = available;
    }

    public long getRequested() {
        return requested;
    }

    public long getAvailable() {
        return available;
    }
}

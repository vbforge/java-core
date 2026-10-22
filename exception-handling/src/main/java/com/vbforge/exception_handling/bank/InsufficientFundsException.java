package com.vbforge.exception_handling.bank;

/**
 * CHECKED custom exception: the caller can realistically recover (ask for a
 * smaller amount, top up the account), so the compiler forces them to decide.
 *
 * <p>Carries structured data ({@link #getShortfall()}) so callers do not have to
 * parse the message text.
 */
public class InsufficientFundsException extends Exception {

    private static final long serialVersionUID = 1L;

    private final long balance;
    private final long requested;

    public InsufficientFundsException(long balance, long requested) {
        super("Insufficient funds: balance=" + balance + ", requested=" + requested);
        this.balance = balance;
        this.requested = requested;
    }

    public long getBalance() {
        return balance;
    }

    public long getRequested() {
        return requested;
    }

    /** How much more money would have been needed. */
    public long getShortfall() {
        return requested - balance;
    }
}

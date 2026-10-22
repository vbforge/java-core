package com.vbforge.exception_handling.bank;

/**
 * UNCHECKED custom exception: passing a zero/negative amount is a programming
 * error, not something a caller recovers from at runtime.
 *
 * <p>Extends {@link IllegalArgumentException} so it fits the standard hierarchy —
 * code that already catches {@code IllegalArgumentException} keeps working.
 */
public class InvalidAmountException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final long amount;

    public InvalidAmountException(long amount) {
        super("Amount must be positive: " + amount);
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }
}

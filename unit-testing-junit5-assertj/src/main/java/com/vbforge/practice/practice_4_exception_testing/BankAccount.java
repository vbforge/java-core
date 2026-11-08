package com.vbforge.practice.practice_4_exception_testing;

/** Amounts are stored in cents (long) to stay away from floating point. */
public class BankAccount {

    private long balance;
    private boolean closed;

    public BankAccount(long openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("opening balance must not be negative");
        }
        this.balance = openingBalance;
    }

    public void deposit(long amount) {
        ensureOpen();
        requirePositive(amount);
        balance += amount;
    }

    public void withdraw(long amount) {
        ensureOpen();
        requirePositive(amount);
        if (amount > balance) {
            throw new InsufficientFundsException(amount, balance);
        }
        balance -= amount;
    }

    public void transferTo(BankAccount target, long amount) {
        if (target == null) {
            throw new NullPointerException("target");
        }
        withdraw(amount);          // may throw: then the target must stay untouched
        target.deposit(amount);
    }

    public void close() {
        closed = true;
    }

    public long balance() {
        return balance;
    }

    /** Parses "12.34" into cents; wraps the low-level failure and keeps it as the cause. */
    public static long parseToCents(String text) {
        try {
            return new java.math.BigDecimal(text.trim()).movePointRight(2).longValueExact();
        } catch (NumberFormatException | ArithmeticException e) {
            throw new IllegalArgumentException("not a valid amount: '" + text + "'", e);
        }
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("account is closed");
        }
    }

    private static void requirePositive(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive but was " + amount);
        }
    }
}

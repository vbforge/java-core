package com.vbforge.exception_handling.bank;

/**
 * Minimal bank account used by the custom-exception demo and tasks.
 *
 * <ul>
 *   <li>{@link #withdraw(long)} throws the CHECKED {@link InsufficientFundsException}</li>
 *   <li>{@link #deposit(long)} and {@link #withdraw(long)} throw the UNCHECKED
 *       {@link InvalidAmountException} for amounts that are not positive</li>
 * </ul>
 */
public class Account {

    private long balance;

    public Account(long initialBalance) {
        if (initialBalance < 0) {
            throw new InvalidAmountException(initialBalance);
        }
        this.balance = initialBalance;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(long amount) {
        requirePositive(amount);
        balance += amount;
    }

    public void withdraw(long amount) throws InsufficientFundsException {
        requirePositive(amount);
        if (amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance -= amount;
    }

    private static void requirePositive(long amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
    }
}

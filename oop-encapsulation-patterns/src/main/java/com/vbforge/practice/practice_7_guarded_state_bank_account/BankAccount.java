package com.vbforge.practice.practice_7_guarded_state_bank_account;

/**
 * Guarded state: private fields + controlled mutation, so the object can never
 * be in an invalid state.
 *
 * Benefits:
 *   1. Validation: constructor and mutators reject invalid values
 *   2. Flexibility: the internal representation can change without breaking callers
 *   3. Invariants: the object always stays valid
 */
public class BankAccount {

    private final String accountNumber;   // fixed after construction, no setter
    private String       ownerName;
    private double       balance;

    public BankAccount(String accountNumber, String ownerName, double initialBalance) {
        if (accountNumber == null || accountNumber.isBlank())
            throw new IllegalArgumentException("Account number cannot be blank");
        if (ownerName == null || ownerName.isBlank())
            throw new IllegalArgumentException("Owner name cannot be blank");
        if (initialBalance < 0)
            throw new IllegalArgumentException("Initial balance cannot be negative");

        this.accountNumber = accountNumber;
        this.ownerName     = ownerName;
        this.balance       = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive, got: " + amount);
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive, got: " + amount);
        if (amount > balance)
            throw new IllegalStateException("Insufficient funds: balance=" + balance + ", requested=" + amount);
        balance -= amount;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance()       { return balance; }
    public String getOwnerName()     { return ownerName; }

    public void setOwnerName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Owner name cannot be blank");
        this.ownerName = name;
    }

    @Override
    public String toString() {
        return "BankAccount{#" + accountNumber + ", owner=" + ownerName
               + ", balance=" + String.format("%.2f", balance) + "}";
    }
}

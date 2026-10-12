package com.vbforge.oop_inheritance_and_polymorphism;

import java.util.List;

/**
 * AbstractClassTasks — implement every method below to make {@code AbstractClassTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code AbstractClassDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=AbstractClassTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The abstract class {@link BankAccount} (a Template Method) is already written. Your job is to
 * fill in the two abstract steps in each concrete subclass, plus two reflection helpers.
 * Do not change method signatures or add external libraries — only the Java standard library
 * is needed.
 */
public class AbstractClassTasks {

    // ── Given: abstract class (do NOT modify) ──────────────────────────────────

    /**
     * Template Method: {@code withdraw} is final and fixed — it asks the subclass for the
     * {@code fee} and for permission ({@code canDebit}) before touching the balance.
     */
    public abstract static class BankAccount {
        private double balance;

        protected BankAccount(double initialBalance) {
            this.balance = initialBalance;
        }

        public double getBalance() {
            return balance;
        }

        public void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        /**
         * Withdraws {@code amount} plus the fee. Returns {@code false} (and changes nothing) if the
         * amount is not positive or if {@link #canDebit(double)} refuses the total.
         */
        public final boolean withdraw(double amount) {
            if (amount <= 0) {
                return false;
            }
            double totalDebit = amount + fee(amount);
            if (!canDebit(totalDebit)) {
                return false;
            }
            balance -= totalDebit;
            return true;
        }

        protected abstract double fee(double amount);

        protected abstract boolean canDebit(double totalDebit);
    }

    // ── Task 1 & 2: SavingsAccount ─────────────────────────────────────────────

    /** A savings account: free withdrawals, but it can never go below zero. */
    public static class SavingsAccount extends BankAccount {

        public SavingsAccount(double initialBalance) {
            super(initialBalance);
        }

        /**
         * Implements {@link BankAccount#fee(double)}: withdrawals from a savings account are free.
         *
         * <p>Examples:
         * <pre>
         *   new SavingsAccount(100).fee(50.0)    → 0.0
         *   new SavingsAccount(100).fee(1000.0)  → 0.0
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The amount is irrelevant for this account type.</li>
         * </ul>
         *
         * @param amount the requested withdrawal amount
         * @return the fee charged for withdrawing {@code amount}
         */
        @Override
        protected double fee(double amount) {
            throw new UnsupportedOperationException("TODO: implement SavingsAccount.fee");
        }

        /**
         * Implements {@link BankAccount#canDebit(double)}: allowed only if the total debit does
         * not exceed the current balance (the balance may reach exactly zero).
         *
         * <p>Examples:
         * <pre>
         *   new SavingsAccount(100).canDebit(100.0)   → true    (balance would become 0)
         *   new SavingsAccount(100).canDebit(100.01)  → false
         *   new SavingsAccount(100).canDebit(0.0)     → true
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The balance is {@code private} in the parent — use the parent's public getter.</li>
         * </ul>
         *
         * @param totalDebit the amount plus fee that would leave the account
         * @return {@code true} if the account can afford the debit
         */
        @Override
        protected boolean canDebit(double totalDebit) {
            throw new UnsupportedOperationException("TODO: implement SavingsAccount.canDebit");
        }
    }

    // ── Task 3 & 4: CheckingAccount ────────────────────────────────────────────

    /** A checking account: flat fee per withdrawal and an overdraft allowance. */
    public static class CheckingAccount extends BankAccount {
        private final double overdraftLimit;

        public CheckingAccount(double initialBalance, double overdraftLimit) {
            super(initialBalance);
            this.overdraftLimit = overdraftLimit;
        }

        /**
         * Implements {@link BankAccount#fee(double)}: every withdrawal costs a flat {@code 1.5},
         * regardless of the amount.
         *
         * <p>Examples:
         * <pre>
         *   new CheckingAccount(100, 50).fee(50.0)    → 1.5
         *   new CheckingAccount(100, 50).fee(1000.0)  → 1.5
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>No computation needed — just return the constant.</li>
         * </ul>
         *
         * @param amount the requested withdrawal amount
         * @return the fee charged for withdrawing {@code amount}
         */
        @Override
        protected double fee(double amount) {
            throw new UnsupportedOperationException("TODO: implement CheckingAccount.fee");
        }

        /**
         * Implements {@link BankAccount#canDebit(double)}: allowed as long as the balance after the
         * debit would not fall below {@code -overdraftLimit}. Ending exactly on the limit is allowed.
         *
         * <p>Examples:
         * <pre>
         *   new CheckingAccount(100, 50).canDebit(150.0)   → true    (balance would be -50, the limit)
         *   new CheckingAccount(100, 50).canDebit(150.5)   → false
         *   new CheckingAccount(100,  0).canDebit(100.0)   → true    (no overdraft, ends at 0)
         *   new CheckingAccount(100,  0).canDebit(100.5)   → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Compute the balance AFTER the debit, then compare it with the allowed minimum.</li>
         * </ul>
         *
         * @param totalDebit the amount plus fee that would leave the account
         * @return {@code true} if the resulting balance stays within the overdraft limit
         */
        @Override
        protected boolean canDebit(double totalDebit) {
            throw new UnsupportedOperationException("TODO: implement CheckingAccount.canDebit");
        }
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code type} carries the {@code abstract} modifier. Interfaces are
     * implicitly abstract, so they also report {@code true}.
     *
     * <p>Examples:
     * <pre>
     *   isAbstract(BankAccount.class)     → true
     *   isAbstract(SavingsAccount.class)  → false
     *   isAbstract(Number.class)          → true
     *   isAbstract(String.class)          → false
     *   isAbstract(Runnable.class)        → true    (interface)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>See {@link java.lang.reflect.Modifier} and {@link Class#getModifiers()}.</li>
     * </ul>
     *
     * @param type the class or interface to inspect (never {@code null})
     * @return {@code true} if the type is abstract
     */
    public boolean isAbstract(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement isAbstract");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the names of the abstract methods that {@code type} itself DECLARES (not the ones it
     * inherits), sorted alphabetically. A concrete class returns an empty list.
     *
     * <p>Examples:
     * <pre>
     *   abstractMethodNames(BankAccount.class)     → ["canDebit", "fee"]
     *   abstractMethodNames(SavingsAccount.class)  → []
     *   abstractMethodNames(Number.class)          → ["doubleValue", "floatValue", "intValue", "longValue"]
     *   abstractMethodNames(String.class)          → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getDeclaredMethods()} returns the methods declared by the class itself —
     *       but in NO guaranteed order, so sort the result.</li>
     *   <li>Skip compiler-generated (synthetic) methods.</li>
     * </ul>
     *
     * @param type the class or interface to inspect (never {@code null})
     * @return sorted names of the abstract methods declared directly in {@code type}
     */
    public List<String> abstractMethodNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement abstractMethodNames");
    }
}

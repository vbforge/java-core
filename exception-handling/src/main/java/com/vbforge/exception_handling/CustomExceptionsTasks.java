package com.vbforge.exception_handling;

import com.vbforge.exception_handling.bank.Account;

/**
 * 🎯 TASKS — Custom exceptions
 *
 * <p>Implement every method so that all tests in {@code CustomExceptionsTasksTest} pass.
 * Use the {@link Account} class and its exceptions from the {@code bank} package:
 * {@code InsufficientFundsException} (checked, has {@code getShortfall()}),
 * {@code InvalidAmountException} (unchecked) and {@code AccountServiceException}
 * (unchecked, constructor {@code (String message, Throwable cause)}).
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class CustomExceptionsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Try to withdraw {@code amount}. Return {@code 0} when the withdrawal succeeds
     * (the money IS taken from the account) or the missing amount when the balance is too low
     * (then the balance is unchanged).
     *
     * <p>Examples:
     * <pre>
     *   account balance 100, shortfall(account, 30)   → 0     (balance is now 70)
     *   account balance 100, shortfall(account, 250)  → 150   (balance is still 100)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The checked exception already carries the number you need</li>
     *   <li>Do not catch {@code InvalidAmountException} — a bad amount is the caller's bug</li>
     * </ul>
     *
     * @param account the account to withdraw from
     * @param amount  the amount to withdraw
     * @return 0 on success, otherwise the shortfall
     */
    public long shortfall(Account account, long amount) {
        throw new UnsupportedOperationException("TODO: implement shortfall");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Move {@code amount} from one account to another.
     * <ul>
     *   <li>When {@code from} lacks the money, throw an {@code AccountServiceException}
     *       with the message {@code "Transfer failed"} and the original exception as its cause.
     *       Neither account may change.</li>
     *   <li>When the amount is not positive, the {@code InvalidAmountException} must escape
     *       UNCHANGED (not wrapped).</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   from=100, to=0, transfer(from, to, 40)   → from=60, to=40
     *   from=10,  to=0, transfer(from, to, 40)   → throws AccountServiceException("Transfer failed"), cause = InsufficientFundsException
     *   transfer(from, to, -1)                   → throws InvalidAmountException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Think about the ORDER of the withdraw and the deposit</li>
     *   <li>Catch only the exception you want to translate</li>
     * </ul>
     *
     * @param from   the source account
     * @param to     the target account
     * @param amount the amount to move
     * @throws AccountServiceException if the source has insufficient funds
     */
    public void transfer(Account from, Account to, long amount) {
        throw new UnsupportedOperationException("TODO: implement transfer");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Deposit {@code amount} and report success. Return {@code false} (and leave the
     * balance unchanged) when the amount is rejected as invalid; {@code true} otherwise.
     *
     * <p>Examples:
     * <pre>
     *   balance 10, tryDeposit(account, 5)   → true   (balance 15)
     *   balance 10, tryDeposit(account, 0)   → false  (balance 10)
     *   balance 10, tryDeposit(account, -3)  → false  (balance 10)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Catch the most specific exception that describes the failure</li>
     * </ul>
     *
     * @param account the account to deposit into
     * @param amount  the amount to deposit
     * @return whether the deposit happened
     */
    public boolean tryDeposit(Account account, long amount) {
        throw new UnsupportedOperationException("TODO: implement tryDeposit");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return the ROOT cause of a throwable: follow {@code getCause()} until there is no
     * further cause. A throwable without a cause is its own root cause.
     *
     * <p>Examples:
     * <pre>
     *   new RuntimeException("a", new IllegalStateException("b", new java.io.IOException("c")))  → the IOException("c")
     *   new IllegalArgumentException("x")                                                        → that same exception
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A loop that stops when {@code getCause()} returns {@code null}</li>
     * </ul>
     *
     * @param throwable the throwable to inspect, never {@code null}
     * @return the deepest cause
     */
    public Throwable rootCause(Throwable throwable) {
        throw new UnsupportedOperationException("TODO: implement rootCause");
    }
}

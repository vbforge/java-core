package com.vbforge.exception_handling;

import com.vbforge.exception_handling.bank.Account;
import com.vbforge.exception_handling.bank.AccountServiceException;
import com.vbforge.exception_handling.bank.InsufficientFundsException;
import com.vbforge.exception_handling.bank.InvalidAmountException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates CUSTOM exceptions, using the tiny {@code bank} package:
 * <ul>
 *   <li>{@link InsufficientFundsException} — CHECKED, recoverable, carries data</li>
 *   <li>{@link InvalidAmountException} — UNCHECKED, extends {@link IllegalArgumentException}</li>
 *   <li>{@link AccountServiceException} — UNCHECKED wrapper that keeps the original cause</li>
 * </ul>
 *
 * <p>Why create your own: a named type says WHAT went wrong in domain language, can carry
 * structured data (no message parsing), and lets callers catch exactly this failure.
 *
 * <p>Rule of thumb: extend {@code Exception} when the caller can realistically recover,
 * extend {@code RuntimeException} (or a more specific one) for bugs and contract violations.
 * Every custom exception should offer a constructor taking a {@code cause}.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that
 * {@code CustomExceptionsDemoTest} can verify every claim.
 */
public class CustomExceptionsDemo {

    // ── 1. A checked exception carrying data ──────────────────────────────────
    /**
     * The caller reacts to the structured value {@code getShortfall()} instead of parsing text.
     */
    public static String withdrawDemo(Account account, long amount) {
        try {
            account.withdraw(amount);
            return "ok:" + account.getBalance();
        } catch (InsufficientFundsException e) {
            return "short by " + e.getShortfall();
        }
    }


    // ── 2. An unchecked exception for contract violations ─────────────────────
    /**
     * No {@code throws}, no {@code catch} needed: a non-positive amount is a caller bug.
     */
    public static long depositDemo(Account account, long amount) {
        account.deposit(amount);
        return account.getBalance();
    }


    // ── 3. Custom exceptions fit the standard hierarchy ───────────────────────
    /**
     * {@link InvalidAmountException} IS an {@link IllegalArgumentException}, so existing
     * handlers for the standard type also catch it.
     */
    public static String catchAsParentDemo(long amount) {
        try {
            new Account(10).deposit(amount);
            return "accepted";
        } catch (IllegalArgumentException e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }


    // ── 4. Wrapping with the cause preserved ──────────────────────────────────
    /**
     * A higher layer translates a low-level exception into its own type, but keeps the
     * original as the cause, so the full story survives in logs and stack traces.
     */
    public static long payDemo(Account account, long price) {
        try {
            account.withdraw(price);
            return account.getBalance();
        } catch (InsufficientFundsException e) {
            throw new AccountServiceException("Payment of " + price + " failed", e);
        }
    }


    // ── 5. Walking the cause chain ────────────────────────────────────────────
    /**
     * {@code getCause()} returns {@code null} at the end of the chain.
     * Returns the simple class names from the outermost exception down to the root cause.
     */
    public static List<String> causeChainDemo(Throwable throwable) {
        List<String> names = new ArrayList<String>();
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            names.add(t.getClass().getSimpleName());
        }
        return names;
    }


    // ── 6. Gotcha: losing the cause ───────────────────────────────────────────
    /**
     * <b>Gotcha:</b> copying only the MESSAGE into a new exception throws the original
     * type and stack trace away. The result has no cause.
     */
    public static Throwable lostCauseDemo() {
        IOException original = new IOException("disk unreachable");
        return new IllegalStateException(original.getMessage());
    }

    /** The fix: pass the original exception as the cause. */
    public static Throwable keptCauseDemo() {
        IOException original = new IOException("disk unreachable");
        return new IllegalStateException("storage failed", original);
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. checked, with data ===");
        System.out.println(withdrawDemo(new Account(100), 30));
        System.out.println(withdrawDemo(new Account(100), 250));

        System.out.println("---");
        System.out.println("=== 2. unchecked ===");
        System.out.println(depositDemo(new Account(100), 50));
        try {
            depositDemo(new Account(100), -5);
        } catch (InvalidAmountException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("---");
        System.out.println("=== 3. fits the standard hierarchy ===");
        System.out.println(catchAsParentDemo(-1));

        System.out.println("---");
        System.out.println("=== 4. wrapping ===");
        try {
            payDemo(new Account(10), 99);
        } catch (AccountServiceException e) {
            System.out.println(e.getMessage() + " <- " + e.getCause().getMessage());
        }

        System.out.println("---");
        System.out.println("=== 5. cause chain ===");
        try {
            payDemo(new Account(10), 99);
        } catch (AccountServiceException e) {
            System.out.println(causeChainDemo(e));
        }

        System.out.println("---");
        System.out.println("=== 6. losing the cause ===");
        System.out.println("lost cause:  " + lostCauseDemo().getCause());
        System.out.println("kept cause:  " + keptCauseDemo().getCause());
    }
}

package com.vbforge.best_practices_and_solid;

import java.util.List;

/**
 * 🎯 TASKS — Single Responsibility Principle
 *
 * <p>Each method below does <b>one</b> job and returns its result instead of
 * printing it. Implement them so that all tests in {@link SingleResponsibilityTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>No {@code System.out} — return values, do not print them</li>
 * </ul>
 */
public class SingleResponsibilityTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Validate a username and return <b>all</b> broken rules — validation only,
     * no storing, no printing.
     *
     * <p>Rules (checked on the raw string, it is <b>not</b> trimmed):
     * <ol>
     *   <li>{@code null} or only whitespace → {@code "username is blank"} and nothing else</li>
     *   <li>fewer than 3 characters → {@code "username is too short"}</li>
     *   <li>more than 12 characters → {@code "username is too long"}</li>
     *   <li>any character that is not a letter or digit → {@code "username has invalid characters"}</li>
     * </ol>
     * Messages appear in the order listed above. A valid name returns an empty list.
     *
     * <p>Examples:
     * <pre>
     *   validateUsername("alice")  → []
     *   validateUsername("   ")    → ["username is blank"]
     *   validateUsername("ab")     → ["username is too short"]
     *   validateUsername("a_b")    → ["username has invalid characters"]
     *   validateUsername("a!")     → ["username is too short", "username has invalid characters"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Collect errors in a list instead of returning at the first failure</li>
     *   <li>{@code Character.isLetterOrDigit} does the character check</li>
     * </ul>
     *
     * @param username the candidate username, may be {@code null}
     * @return the broken-rule messages, never {@code null}
     */
    public List<String> validateUsername(String username) {
        throw new UnsupportedOperationException("TODO: implement validateUsername");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Pure calculation: the sum of all prices plus tax, rounded to 2 decimals.
     * No formatting, no printing.
     *
     * <p>Examples:
     * <pre>
     *   calculateTotal([10.0, 20.0], 0.1)   → 33.0
     *   calculateTotal([19.99, 5.01], 0.2)  → 30.0
     *   calculateTotal([9.99], 0.0)         → 9.99
     *   calculateTotal([], 0.2)             → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Tax is a fraction: 0.1 means 10 %</li>
     *   <li>{@code Math.round(x * 100) / 100.0} rounds to two decimals</li>
     * </ul>
     *
     * @param prices  the item prices
     * @param taxRate the tax as a fraction (0.2 = 20 %)
     * @return the rounded gross total
     */
    public double calculateTotal(List<Double> prices, double taxRate) {
        throw new UnsupportedOperationException("TODO: implement calculateTotal");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Pure formatting: build the receipt text from <b>already computed</b> data.
     * Do not recompute the total — the caller passes it in.
     *
     * <p>Format: one line {@code "- <name>: <price>"} per item (price with exactly
     * 2 decimals), then {@code "TOTAL: <total>"} (2 decimals). Lines are separated by
     * {@code "\n"}, with no trailing newline.
     *
     * <p>Throws {@link IllegalArgumentException} when the two lists differ in size.
     *
     * <p>Examples:
     * <pre>
     *   renderReceipt(["Tea", "Cake"], [2.5, 4.0], 6.5)
     *       → "- Tea: 2.50\n- Cake: 4.00\nTOTAL: 6.50"
     *   renderReceipt([], [], 0.0)
     *       → "TOTAL: 0.00"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code String.format(Locale.ROOT, ...)} keeps the decimal point locale-independent</li>
     *   <li>{@code String.join} or a {@code StringBuilder} assembles the lines</li>
     * </ul>
     *
     * @param names  the item names
     * @param prices the item prices, same size as {@code names}
     * @param total  the total to print on the last line
     * @return the receipt text
     */
    public String renderReceipt(List<String> names, List<Double> prices, double total) {
        throw new UnsupportedOperationException("TODO: implement renderReceipt");
    }
}

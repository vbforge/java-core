package com.vbforge.best_practices_and_solid;

/**
 * 🎯 TASKS — Clean Code
 *
 * <p>Implement every method so that all tests in {@link CleanCodeTasksTest} pass —
 * <b>and</b> so that you would be happy to read the result in six months.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Style goals (the tests cannot check these, you can): guard clauses, named constants, no magic numbers</li>
 * </ul>
 */
public class CleanCodeTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Classify an age using <b>guard clauses</b> (early returns, no nested {@code else}).
     * <pre>
     *   0–12   → "child"
     *   13–19  → "teen"
     *   20–64  → "adult"
     *   65–150 → "senior"
     * </pre>
     * Throws {@link IllegalArgumentException} for an age below 0 or above 150.
     *
     * <p>Examples:
     * <pre>
     *   classifyAge(5)    → "child"
     *   classifyAge(13)   → "teen"
     *   classifyAge(64)   → "adult"
     *   classifyAge(65)   → "senior"
     *   classifyAge(-1)   → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reject the invalid range first, then test the categories from the smallest upwards</li>
     * </ul>
     *
     * @param age the age in years
     * @return the category name
     */
    public String classifyAge(int age) {
        throw new UnsupportedOperationException("TODO: implement classifyAge");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Compute weekly pay. The first 40 hours are paid at {@code hourlyRate}; every hour
     * beyond 40 is paid at 1.5 times the rate. Use <b>named constants</b>, not bare
     * {@code 40} and {@code 1.5}.
     *
     * <p>Throws {@link IllegalArgumentException} when {@code hoursWorked} or
     * {@code hourlyRate} is negative.
     *
     * <p>Examples:
     * <pre>
     *   overtimePay(40, 20.0)  → 800.0
     *   overtimePay(45, 20.0)  → 950.0   (40 * 20 + 5 * 30)
     *   overtimePay(10, 15.5)  → 155.0
     *   overtimePay(0, 20.0)   → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Split the hours into "regular" and "overtime" parts with {@code Math.min} / {@code Math.max}</li>
     * </ul>
     *
     * @param hoursWorked the hours worked this week
     * @param hourlyRate  the regular hourly rate
     * @return the total pay
     */
    public double overtimePay(int hoursWorked, double hourlyRate) {
        throw new UnsupportedOperationException("TODO: implement overtimePay");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * A password is strong when it has <b>at least 8 characters</b> and contains at
     * least one uppercase letter, one lowercase letter and one digit. {@code null}
     * is not strong.
     *
     * <p>Examples:
     * <pre>
     *   isStrongPassword("Passw0rd")   → true
     *   isStrongPassword("password1")  → false   (no uppercase)
     *   isStrongPassword("PASSWORD1")  → false   (no lowercase)
     *   isStrongPassword("Password")   → false   (no digit)
     *   isStrongPassword("Pass1")      → false   (too short)
     *   isStrongPassword(null)         → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Character.isUpperCase}, {@code isLowerCase}, {@code isDigit}</li>
     *   <li>Optional: extract one small, well-named private method per rule</li>
     * </ul>
     *
     * @param password the candidate password, may be {@code null}
     * @return {@code true} when all rules hold
     */
    public boolean isStrongPassword(String password) {
        throw new UnsupportedOperationException("TODO: implement isStrongPassword");
    }
}

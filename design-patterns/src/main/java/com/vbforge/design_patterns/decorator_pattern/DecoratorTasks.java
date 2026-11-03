package com.vbforge.design_patterns.decorator_pattern;

/**
 * DecoratorTasks — implement every method below to make {@code DecoratorTasksTest} go GREEN.
 */
public class DecoratorTasks {

    @FunctionalInterface
    public interface TextProcessor {
        String process(String input);
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the base "do nothing" processor — the identity function.
     *
     * <p>Examples:
     * <pre>
     *   base().process("hello")  → "hello"
     *   base().process("")       → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Return the input unchanged — a lambda {@code s -> s} is enough.</li>
     * </ul>
     *
     * @return an identity {@link TextProcessor}
     */
    public TextProcessor base() {
        throw new UnsupportedOperationException("TODO: implement base");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Wraps {@code wrapped} so its output is uppercased.
     *
     * <p>Examples:
     * <pre>
     *   uppercase(base()).process("hi")       → "HI"
     *   uppercase(base()).process("Hello")    → "HELLO"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Call {@code wrapped.process(input)} first, then {@code toUpperCase()}.</li>
     *   <li>Never call {@code toString}/{@code equals} on {@code wrapped} — just
     *       forward every call.</li>
     * </ul>
     *
     * @param wrapped the inner processor
     * @return a decorator that uppercases {@code wrapped}'s output
     */
    public TextProcessor uppercase(TextProcessor wrapped) {
        throw new UnsupportedOperationException("TODO: implement uppercase");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Wraps {@code wrapped} so its output is trimmed (leading/trailing
     * whitespace removed).
     *
     * <p>Examples:
     * <pre>
     *   trim(base()).process("  hi  ")     → "hi"
     *   trim(base()).process("  a b  ")    → "a b"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link String#trim()} is enough — no need to strip inner spaces.</li>
     * </ul>
     *
     * @param wrapped the inner processor
     * @return a decorator that trims {@code wrapped}'s output
     */
    public TextProcessor trim(TextProcessor wrapped) {
        throw new UnsupportedOperationException("TODO: implement trim");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Wraps {@code wrapped} so its output is reversed character-by-character.
     *
     * <p>Examples:
     * <pre>
     *   reverse(base()).process("abc")          → "cba"
     *   reverse(base()).process("hello world")  → "dlrow olleh"
     *   reverse(base()).process("")             → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code new StringBuilder(s).reverse().toString()} — reverse the
     *       output of {@code wrapped}, not the input.</li>
     * </ul>
     *
     * @param wrapped the inner processor
     * @return a decorator that reverses {@code wrapped}'s output
     */
    public TextProcessor reverse(TextProcessor wrapped) {
        throw new UnsupportedOperationException("TODO: implement reverse");
    }
}
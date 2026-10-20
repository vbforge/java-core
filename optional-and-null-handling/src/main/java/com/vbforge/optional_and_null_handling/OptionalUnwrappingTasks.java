package com.vbforge.optional_and_null_handling;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 🎯 TASKS — Getting values out of an Optional
 *
 * <p>Implement every method so that all tests in {@code OptionalUnwrappingTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class OptionalUnwrappingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the value when present; otherwise return whatever the {@code fallback}
     * supplier produces. The supplier must NOT be called when a value is present
     * (a test counts the calls).
     *
     * <p>Examples:
     * <pre>
     *   valueOrComputed(Optional.of("Ann"), () -> "guest")       → "Ann"   (supplier never called)
     *   valueOrComputed(Optional.empty(),   () -> "guest")       → "guest" (supplier called once)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Two {@code orElse...} methods exist — one of them evaluates its argument eagerly</li>
     * </ul>
     *
     * @param value    the optional value
     * @param fallback computes the default when {@code value} is empty
     * @return the value, or the computed fallback
     */
    public String valueOrComputed(Optional<String> value, Supplier<String> fallback) {
        throw new UnsupportedOperationException("TODO: implement valueOrComputed");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return the value when present. When empty, throw an
     * {@link IllegalArgumentException} whose message is {@code fieldName + " is required"}.
     *
     * <p>Examples:
     * <pre>
     *   requireValue(Optional.of("ann"), "username")  → "ann"
     *   requireValue(Optional.empty(),   "username")  → throws IllegalArgumentException("username is required")
     *   requireValue(Optional.empty(),   "email")     → throws IllegalArgumentException("email is required")
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Java 8 has no no-argument {@code orElseThrow()} — pass a supplier that builds the exception</li>
     * </ul>
     *
     * @param value     the optional value
     * @param fieldName the name used in the error message
     * @return the value
     * @throws IllegalArgumentException when the value is absent
     */
    public String requireValue(Optional<String> value, String fieldName) {
        throw new UnsupportedOperationException("TODO: implement requireValue");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Add the value to the end of {@code sink} when present; leave the list untouched
     * when the Optional is empty.
     *
     * <p>Examples:
     * <pre>
     *   sink = ["a"]
     *   collectIfPresent(Optional.of("x"), sink)   → sink is ["a", "x"]
     *   collectIfPresent(Optional.empty(), sink)   → sink is still ["a", "x"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You do not need {@code if} or {@code get()} — one Optional method takes an action</li>
     * </ul>
     *
     * @param value the optional value
     * @param sink  the list to append to, never {@code null}
     */
    public void collectIfPresent(Optional<String> value, List<String> sink) {
        throw new UnsupportedOperationException("TODO: implement collectIfPresent");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return the first Optional that has a value, checking in order
     * {@code first}, {@code second}, {@code third}. If all three are empty,
     * return an empty Optional. Java 9's {@code Optional.or} is NOT available.
     *
     * <p>Examples:
     * <pre>
     *   firstPresent(Optional.of("a"), Optional.of("b"), Optional.empty())    → Optional[a]
     *   firstPresent(Optional.empty(), Optional.of("b"), Optional.of("c"))    → Optional[b]
     *   firstPresent(Optional.empty(), Optional.empty(), Optional.of("c"))    → Optional[c]
     *   firstPresent(Optional.empty(), Optional.empty(), Optional.empty())    → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You are choosing between Optionals, not unwrapping them</li>
     * </ul>
     *
     * @param first  highest priority
     * @param second middle priority
     * @param third  lowest priority
     * @param <T>    the value type
     * @return the first non-empty Optional, or an empty one
     */
    public <T> Optional<T> firstPresent(Optional<T> first, Optional<T> second, Optional<T> third) {
        throw new UnsupportedOperationException("TODO: implement firstPresent");
    }
}

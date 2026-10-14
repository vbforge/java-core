package com.vbforge.oop_encapsulation_patterns;

import java.util.List;
import java.util.Objects;

/**
 * ImmutabilityTasks — implement every method below to make {@code ImmutabilityTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ImmutabilityDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ImmutabilityTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The immutable classes {@link Money} and {@link Profile} and the mutable {@link Counter} are
 * already written except for the stubs. Do not change method signatures or add external
 * libraries — only the Java standard library is needed.
 */
public class ImmutabilityTasks {

    // ── Tasks 1, 2 & 3: Money ──────────────────────────────────────────────────

    /** An immutable amount of money in cents. */
    public static final class Money {
        private final long cents;
        private final String currency;

        /** Given: rejects a {@code null} or empty currency with {@link IllegalArgumentException}. */
        public Money(long cents, String currency) {
            if (currency == null || currency.isEmpty()) {
                throw new IllegalArgumentException("currency must not be empty");
            }
            this.cents = cents;
            this.currency = currency;
        }

        /** Given. */
        public long cents() {
            return cents;
        }

        /** Given. */
        public String currency() {
            return currency;
        }

        /**
         * Returns a NEW {@code Money} holding the sum of this amount and {@code other}. Neither
         * operand is changed.
         *
         * <p>Examples:
         * <pre>
         *   new Money(1000, "EUR").plus(new Money(500, "EUR"))   → Money(1500, "EUR")
         *   new Money(1000, "EUR").plus(new Money(-300, "EUR"))  → Money(700, "EUR")
         *   new Money(1000, "EUR").plus(new Money(5, "USD"))     → throws IllegalArgumentException
         *   new Money(Long.MAX_VALUE, "EUR").plus(new Money(1, "EUR"))  → throws ArithmeticException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Plain {@code +} on a {@code long} silently wraps around on overflow — look at the
         *       {@code *Exact} methods of {@link Math}.</li>
         * </ul>
         *
         * @param other the amount to add (never {@code null})
         * @return a new {@code Money} with the sum
         * @throws IllegalArgumentException if the currencies differ
         * @throws ArithmeticException      if the sum overflows a {@code long}
         */
        public Money plus(Money other) {
            throw new UnsupportedOperationException("TODO: implement plus");
        }

        /**
         * Returns a NEW {@code Money} with the amount multiplied by {@code factor} (a negative
         * factor is allowed). This amount is not changed.
         *
         * <p>Examples:
         * <pre>
         *   new Money(250, "EUR").times(3)    → Money(750, "EUR")
         *   new Money(250, "EUR").times(0)    → Money(0, "EUR")
         *   new Money(250, "EUR").times(-2)   → Money(-500, "EUR")
         *   new Money(Long.MAX_VALUE, "EUR").times(2)   → throws ArithmeticException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Same overflow concern as {@code plus} — and watch the {@code int} → {@code long}
         *       conversion.</li>
         * </ul>
         *
         * @param factor the multiplier
         * @return a new {@code Money} with the product
         * @throws ArithmeticException if the product overflows a {@code long}
         */
        public Money times(int factor) {
            throw new UnsupportedOperationException("TODO: implement times");
        }

        /**
         * Adds up a list of amounts, all in {@code currency}. An empty list gives zero in that
         * currency.
         *
         * <p>Examples:
         * <pre>
         *   sum([Money(100,"EUR"), Money(250,"EUR")], "EUR")   → Money(350, "EUR")
         *   sum([], "EUR")                                     → Money(0, "EUR")
         *   sum([Money(100,"EUR"), Money(5,"USD")], "EUR")     → throws IllegalArgumentException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>With immutable values the running total is a variable you REASSIGN to each new
         *       result — nothing is modified in place.</li>
         *   <li>Reuse {@code plus}; it already checks the currency.</li>
         * </ul>
         *
         * @param values   the amounts to add (never {@code null})
         * @param currency the currency of the result
         * @return the total
         * @throws IllegalArgumentException if any amount is in a different currency
         */
        public static Money sum(List<Money> values, String currency) {
            throw new UnsupportedOperationException("TODO: implement sum");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Money)) {
                return false;
            }
            Money other = (Money) o;
            return cents == other.cents && currency.equals(other.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cents, currency);
        }

        @Override
        public String toString() {
            return currency + " " + cents + "c";
        }
    }

    // ── Task 4: Profile ────────────────────────────────────────────────────────

    /** An immutable profile: a name plus an unmodifiable list of tags. */
    public static final class Profile {
        private final String name;
        private final List<String> tags;

        /** Given: stores an immutable copy of {@code tags}. */
        public Profile(String name, List<String> tags) {
            this.name = name;
            this.tags = List.copyOf(tags);
        }

        /** Given. */
        public String name() {
            return name;
        }

        /** Given: the tags are already unmodifiable, so returning them is safe. */
        public List<String> tags() {
            return tags;
        }

        /**
         * Returns a NEW {@code Profile} with the same name and the same tags plus {@code tag}
         * appended at the end. This profile is not changed.
         *
         * <p>Examples:
         * <pre>
         *   new Profile("Ann", ["java"]).withTag("spring")   → Profile("Ann", ["java", "spring"])
         *   new Profile("Ann", []).withTag("java")           → Profile("Ann", ["java"])
         *   new Profile("Ann", ["java"]).withTag(" ")        → throws IllegalArgumentException
         *   new Profile("Ann", ["java"]).withTag(null)       → throws IllegalArgumentException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@code this.tags} is unmodifiable — build a fresh list, add to it, and pass it to
         *       the constructor.</li>
         *   <li>{@link String#isBlank()} exists since Java 11.</li>
         * </ul>
         *
         * @param tag the tag to add (not {@code null}, not blank)
         * @return a new profile
         * @throws IllegalArgumentException if {@code tag} is {@code null} or blank
         */
        public Profile withTag(String tag) {
            throw new UnsupportedOperationException("TODO: implement withTag");
        }
    }

    // ── Given: a deliberately mutable class (for the examples of Task 5) ───────

    /** Mutable on purpose: not final, and its field changes. */
    public static class Counter {
        private int value;

        public void increment() {
            value++;
        }

        public int value() {
            return value;
        }
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Checks whether a class has the SHAPE of an immutable class: it is {@code final} and every
     * INSTANCE field it declares is both {@code private} and {@code final}. Static fields are
     * ignored. (The check cannot see "deep" problems such as a {@code final} field holding a
     * mutable list — it only inspects the declaration.)
     *
     * <p>Examples:
     * <pre>
     *   isImmutableShape(Money.class)       → true
     *   isImmutableShape(Profile.class)     → true
     *   isImmutableShape(Counter.class)     → false    (not final, value changes)
     *   isImmutableShape(Integer.class)     → true
     *   isImmutableShape(ArrayList.class)   → false    (not final)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getModifiers()} for the class, {@link java.lang.reflect.Field#getModifiers()}
     *       for each field — both feed {@link java.lang.reflect.Modifier}.</li>
     *   <li>Skip compiler-generated (synthetic) fields.</li>
     *   <li>A record passes this check, too — that is one of the reasons records exist.</li>
     * </ul>
     *
     * @param type the class to inspect (never {@code null})
     * @return {@code true} if the class is final with only private final instance fields
     */
    public boolean isImmutableShape(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement isImmutableShape");
    }
}

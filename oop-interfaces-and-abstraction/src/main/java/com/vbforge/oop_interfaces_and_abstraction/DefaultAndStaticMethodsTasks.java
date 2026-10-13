package com.vbforge.oop_interfaces_and_abstraction;

import java.util.List;

/**
 * DefaultAndStaticMethodsTasks — implement every method below to make
 * {@code DefaultAndStaticMethodsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code DefaultAndStaticMethodsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=DefaultAndStaticMethodsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The interfaces {@link Taxable}, {@link Validator}, {@link Walker} and {@link Swimmer} are already
 * declared; the abstract methods are given and you fill in the default and static ones. Do not
 * change method signatures or add external libraries — only the Java standard library is needed.
 */
public class DefaultAndStaticMethodsTasks {

    // ── Task 1: Taxable.priceWithTax ───────────────────────────────────────────

    /** Something with a net price. Implementers only write {@code price()}. */
    public interface Taxable {

        double price();

        /**
         * Default method: the net price plus tax. {@code rate} is a fraction, so {@code 0.25} means 25%.
         *
         * <p>Examples:
         * <pre>
         *   price 100.0, priceWithTax(0.25)  → 125.0
         *   price 80.0,  priceWithTax(0.5)   → 120.0
         *   price 100.0, priceWithTax(0.0)   → 100.0
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>A default method may call the abstract methods of its own interface.</li>
         * </ul>
         *
         * @param rate the tax rate as a fraction (never negative)
         * @return the price including tax
         */
        default double priceWithTax(double rate) {
            throw new UnsupportedOperationException("TODO: implement Taxable.priceWithTax");
        }
    }

    // ── Task 2, 3 & 4: Validator ───────────────────────────────────────────────

    /** Decides whether a string is acceptable. Implementers only write {@code isValid}. */
    public interface Validator {

        boolean isValid(String value);

        /**
         * Default method: a validator that accepts a value only if BOTH this validator and
         * {@code other} accept it. If this validator already rejects the value, {@code other}
         * must NOT be consulted.
         *
         * <p>Examples:
         * <pre>
         *   longerThan3.and(startsWithA).isValid("Apple")   → true
         *   longerThan3.and(startsWithA).isValid("Ape")     → false   (too short)
         *   longerThan3.and(startsWithA).isValid("Banana")  → false   (wrong start)
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@code Validator} has a single abstract method, so the validator you return can be
         *       created with a lambda.</li>
         *   <li>The boolean operator {@code &&} already skips its right side when the left one is false.</li>
         * </ul>
         *
         * @param other the second condition (never {@code null})
         * @return a validator that requires both conditions
         */
        default Validator and(Validator other) {
            throw new UnsupportedOperationException("TODO: implement Validator.and");
        }

        /**
         * Default method: a validator that accepts exactly the values this validator rejects.
         *
         * <p>Examples:
         * <pre>
         *   longerThan3.negate().isValid("Ape")    → true
         *   longerThan3.negate().isValid("Apple")  → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>You can call {@code isValid} on {@code this} from inside the lambda you return.</li>
         * </ul>
         *
         * @return the opposite validator
         */
        default Validator negate() {
            throw new UnsupportedOperationException("TODO: implement Validator.negate");
        }

        /**
         * Static factory: a validator that accepts every value that is neither {@code null} nor empty.
         * A string containing only spaces counts as NOT empty.
         *
         * <p>Examples:
         * <pre>
         *   Validator.notEmpty().isValid("a")     → true
         *   Validator.notEmpty().isValid(" ")     → true
         *   Validator.notEmpty().isValid("")      → false
         *   Validator.notEmpty().isValid(null)    → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Check for {@code null} first so that the length check cannot fail.</li>
         * </ul>
         *
         * @return a validator rejecting {@code null} and the empty string
         */
        static Validator notEmpty() {
            throw new UnsupportedOperationException("TODO: implement Validator.notEmpty");
        }
    }

    // ── Task 5: AmphibiousRobot.move ───────────────────────────────────────────

    /** Can walk. */
    public interface Walker {
        default String move() {
            return "walk";
        }
    }

    /** Can swim. */
    public interface Swimmer {
        default String move() {
            return "swim";
        }
    }

    /** Both interfaces offer {@code move()}, so this class MUST decide what it does. */
    public static class AmphibiousRobot implements Walker, Swimmer {

        /**
         * Resolves the conflict between {@link Walker#move()} and {@link Swimmer#move()}: the robot
         * first walks, then swims. Reuse both inherited versions instead of typing the words yourself.
         *
         * <p>Examples:
         * <pre>
         *   new AmphibiousRobot().move()  → "walk then swim"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>A class may call the default of one of its DIRECT superinterfaces with a qualified
         *       {@code super} call.</li>
         * </ul>
         *
         * @return the walking result, then {@code " then "}, then the swimming result
         */
        @Override
        public String move() {
            throw new UnsupportedOperationException("TODO: implement AmphibiousRobot.move");
        }
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the names of the default methods that the interface {@code iface} itself DECLARES,
     * sorted alphabetically. Abstract methods, static methods and methods inherited from other
     * interfaces are not included.
     *
     * <p>Examples:
     * <pre>
     *   defaultMethodNames(Taxable.class)    → ["priceWithTax"]
     *   defaultMethodNames(Validator.class)  → ["and", "negate"]
     *   defaultMethodNames(Iterable.class)   → ["forEach", "spliterator"]
     *   defaultMethodNames(Runnable.class)   → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getDeclaredMethods()} returns the methods declared by the type itself — in NO
     *       guaranteed order, so sort the result.</li>
     *   <li>{@link java.lang.reflect.Method#isDefault()} answers the main question.</li>
     * </ul>
     *
     * @param iface the interface to inspect (never {@code null})
     * @return sorted names of the declared default methods
     */
    public List<String> defaultMethodNames(Class<?> iface) {
        throw new UnsupportedOperationException("TODO: implement defaultMethodNames");
    }
}

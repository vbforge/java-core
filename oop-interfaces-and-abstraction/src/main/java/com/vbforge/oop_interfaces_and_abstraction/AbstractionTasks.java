package com.vbforge.oop_interfaces_and_abstraction;

import java.util.List;

/**
 * AbstractionTasks — implement every method below to make {@code AbstractionTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code AbstractionDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=AbstractionTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The interfaces and the abstract class {@link AbstractCounter} are already written. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class AbstractionTasks {

    // ── Given: abstractions (do NOT modify) ────────────────────────────────────

    /** Computes the shipping cost of an order. */
    public interface ShippingPolicy {
        double cost(double orderTotal);
    }

    /** A device that can print. */
    public interface CanPrint {
        String print(String document);
    }

    /** A device that can scan. */
    public interface CanScan {
        String scan();
    }

    /** A counter clients depend on. */
    public interface Counter {
        void increment();

        int value();
    }

    /**
     * Skeletal implementation of {@link Counter}: it stores the count and defines HOW incrementing
     * works (add one {@code step()}); subclasses only decide how big the step is.
     */
    public abstract static class AbstractCounter implements Counter {
        private int count;

        protected AbstractCounter(int start) {
            this.count = start;
        }

        @Override
        public final int value() {
            return count;
        }

        @Override
        public final void increment() {
            count += step();
        }

        /** The amount added by the next {@link #increment()}. */
        protected abstract int step();
    }

    // ── Task 1 & 2: policy factories ───────────────────────────────────────────

    /**
     * Returns a policy that charges the same {@code rate} for every order, whatever its total.
     * The caller must only ever see the {@link ShippingPolicy} interface.
     *
     * <p>Examples:
     * <pre>
     *   flatRate(5.0).cost(10.0)    → 5.0
     *   flatRate(5.0).cost(500.0)   → 5.0
     *   flatRate(0.0).cost(10.0)    → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You do not need to write a named class: {@code ShippingPolicy} has one abstract method.</li>
     * </ul>
     *
     * @param rate the fixed cost per order
     * @return a policy that always returns {@code rate}
     */
    public ShippingPolicy flatRate(double rate) {
        throw new UnsupportedOperationException("TODO: implement flatRate");
    }

    /**
     * Returns a policy that is free for orders whose total reaches {@code threshold} (inclusive) and
     * costs {@code rate} for smaller orders.
     *
     * <p>Examples:
     * <pre>
     *   freeOver(50.0, 5.0).cost(49.99)   → 5.0
     *   freeOver(50.0, 5.0).cost(50.0)    → 0.0     (the threshold itself is free)
     *   freeOver(50.0, 5.0).cost(200.0)   → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The returned object may remember {@code threshold} and {@code rate} — lambdas can use
     *       variables of the enclosing method.</li>
     * </ul>
     *
     * @param threshold the order total from which shipping is free
     * @param rate      the cost for orders below the threshold
     * @return the free-over-threshold policy
     */
    public ShippingPolicy freeOver(double threshold, double rate) {
        throw new UnsupportedOperationException("TODO: implement freeOver");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the total shipping cost of several orders under one policy: the policy's cost for each
     * order total, added up. The method must work for ANY {@link ShippingPolicy}.
     *
     * <p>Examples:
     * <pre>
     *   totalShipping([10.0, 60.0, 20.0], flatRate(5.0))      → 15.0
     *   totalShipping([10.0, 60.0, 20.0], freeOver(50, 5))    → 10.0
     *   totalShipping([], flatRate(5.0))                      → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You know only the contract of the policy — no {@code instanceof}, no casts.</li>
     * </ul>
     *
     * @param orderTotals the totals of the orders (may be empty)
     * @param policy      the policy to apply to each order
     * @return the sum of the shipping costs
     */
    public double totalShipping(List<Double> orderTotals, ShippingPolicy policy) {
        throw new UnsupportedOperationException("TODO: implement totalShipping");
    }

    // ── Task 4 & 5: concrete counters (abstract step) ──────────────────────────

    /** Adds the same step on every increment. Constructor given. */
    public static class FixedCounter extends AbstractCounter {
        private final int stepSize;

        public FixedCounter(int start, int stepSize) {
            super(start);
            this.stepSize = stepSize;
        }

        /**
         * Implements {@link AbstractCounter#step()}: always the fixed step size given to the constructor.
         *
         * <p>Examples:
         * <pre>
         *   new FixedCounter(0, 5): increment(), increment()  → value() is 10
         *   new FixedCounter(100, 1): increment()             → value() is 101
         *   new FixedCounter(7, 0): increment()               → value() is 7
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The abstract parent already adds the step to the count for you — only return it.</li>
         * </ul>
         *
         * @return the step size
         */
        @Override
        protected int step() {
            throw new UnsupportedOperationException("TODO: implement FixedCounter.step");
        }
    }

    /** Doubles its value on every increment. Constructor given. */
    public static class DoublingCounter extends AbstractCounter {

        public DoublingCounter(int start) {
            super(start);
        }

        /**
         * Implements {@link AbstractCounter#step()} so that every increment DOUBLES the current value:
         * the step to add is the current value itself.
         *
         * <p>Examples:
         * <pre>
         *   new DoublingCounter(1): increment() ×3   → value() goes 2, 4, 8
         *   new DoublingCounter(5): increment()      → value() is 10
         *   new DoublingCounter(0): increment()      → value() stays 0
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The parent exposes the current count through a public method.</li>
         * </ul>
         *
         * @return the amount that makes the count double
         */
        @Override
        protected int step() {
            throw new UnsupportedOperationException("TODO: implement DoublingCounter.step");
        }
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns which capabilities {@code device} has, as a list in the fixed order
     * {@code "print"}, then {@code "scan"}: {@code "print"} if the device implements {@link CanPrint},
     * {@code "scan"} if it implements {@link CanScan}. A device with neither capability — or
     * {@code null} — gives an empty list.
     *
     * <p>Examples:
     * <pre>
     *   capabilities(printerOnly)    → ["print"]
     *   capabilities(scannerOnly)    → ["scan"]
     *   capabilities(allInOne)       → ["print", "scan"]
     *   capabilities("a string")     → []
     *   capabilities(null)           → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Ask the object about its TYPES, not its class name.</li>
     * </ul>
     *
     * @param device any object (may be {@code null})
     * @return the capability names, {@code "print"} first
     */
    public List<String> capabilities(Object device) {
        throw new UnsupportedOperationException("TODO: implement capabilities");
    }
}

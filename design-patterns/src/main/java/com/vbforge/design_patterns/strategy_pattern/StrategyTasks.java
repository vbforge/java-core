package com.vbforge.design_patterns.strategy_pattern;

/**
 * StrategyTasks — implement every method below to make {@code StrategyTasksTest} go GREEN.
 */
public class StrategyTasks {

    @FunctionalInterface
    public interface PricingStrategy {
        double price(double basePrice);
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a strategy that leaves the price unchanged.
     *
     * <p>Examples:
     * <pre>
     *   noDiscount().price(100)  → 100.0
     *   noDiscount().price(0)    → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Return the identity function — a lambda {@code base -> base}.</li>
     * </ul>
     *
     * @return a no-op pricing strategy
     */
    public PricingStrategy noDiscount() {
        throw new UnsupportedOperationException("TODO: implement noDiscount");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a strategy applying a flat <strong>10%</strong> discount:
     * {@code price = base * 0.9}.
     *
     * <p>Examples:
     * <pre>
     *   tenPercentOff().price(100)  → 90.0
     *   tenPercentOff().price(50)   → 45.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One multiplication. Do not round — return the raw double.</li>
     * </ul>
     *
     * @return a strategy that multiplies the base by 0.9
     */
    public PricingStrategy tenPercentOff() {
        throw new UnsupportedOperationException("TODO: implement tenPercentOff");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a strategy applying the given percentage discount.
     * {@code percentage(20).price(100) == 80.0}.
     *
     * <p>Examples:
     * <pre>
     *   percentage(0).price(100)    → 100.0
     *   percentage(50).price(100)   → 50.0
     *   percentage(100).price(100)  → 0.0
     *   percentage(150).price(100)  → -50.0   (no clamping — percentage is trusted)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Effective multiplier = {@code 1.0 - pct/100.0}.</li>
     *   <li>Capture {@code pct} in the lambda — the strategy is created once,
     *       called many times.</li>
     * </ul>
     *
     * @param pct discount percentage (0..100 typically)
     * @return a strategy that applies {@code pct} percent off
     */
    public PricingStrategy percentage(double pct) {
        throw new UnsupportedOperationException("TODO: implement percentage");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Applies the given strategy to a base price and returns the final price.
     *
     * <p>Examples:
     * <pre>
     *   checkout(100, base -> base)        → 100.0
     *   checkout(100, base -> base * 0.5)  → 50.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Delegate — do not reimplement discounting here.</li>
     * </ul>
     *
     * @param basePrice base amount before discount
     * @param strategy  pricing strategy to apply
     * @return {@code strategy.price(basePrice)}
     */
    public double checkout(double basePrice, PricingStrategy strategy) {
        throw new UnsupportedOperationException("TODO: implement checkout");
    }
}
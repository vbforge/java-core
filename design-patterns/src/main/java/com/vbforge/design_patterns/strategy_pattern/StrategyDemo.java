package com.vbforge.design_patterns.strategy_pattern;

import java.util.function.DoubleUnaryOperator;

/**
 * StrategyDemo — swap algorithms at runtime behind a common interface.
 *
 * <p>Topics:
 * <ol>
 *   <li>Two strategies for the same problem</li>
 *   <li>The context delegates to the strategy</li>
 *   <li>Lambda-based strategies (Java 8+)</li>
 * </ol>
 */
public class StrategyDemo {

    public static void main(String[] args) {
        strategiesDemo();
        System.out.println("---");
        contextDemo();
        System.out.println("---");
        lambdaStrategiesDemo();
    }

    /**
     * Two concrete strategies implement the same interface.
     *
     * <p><b>Why:</b> eliminates {@code if (type.equals("..."))} ladders in
     * the calling code — the algorithm lives in its own class.
     */
    static void strategiesDemo() {
        System.out.println("=== Two strategies ===");
        PricingStrategy noDiscount   = base -> base;
        PricingStrategy tenPercent   = base -> base * 0.9;
        System.out.println("100 @ none  : " + noDiscount.price(100));
        System.out.println("100 @ 10%   : " + tenPercent.price(100));
    }

    /**
     * Context delegates to the strategy it is given.
     */
    static void contextDemo() {
        System.out.println("=== Context delegates ===");
        Checkout c1 = new Checkout(base -> base);
        Checkout c2 = new Checkout(base -> base * 0.9);
        System.out.println("checkout none : " + c1.checkout(200));
        System.out.println("checkout 10%  : " + c2.checkout(200));
    }

    /**
     * Because {@link PricingStrategy} is a functional interface, lambdas
     * capture ad-hoc strategies without ceremony.
     */
    static void lambdaStrategiesDemo() {
        System.out.println("=== Lambda strategies ===");
        PricingStrategy fifty = base -> base * 0.5;
        Checkout c = new Checkout(fifty);
        System.out.println("checkout 50%  : " + c.checkout(100));
    }

    // ── Shared types ─────────────────────────────────────────────────────────

    @FunctionalInterface
    public interface PricingStrategy {
        double price(double basePrice);
    }

    public static class Checkout {
        private final PricingStrategy strategy;
        public Checkout(PricingStrategy strategy) { this.strategy = strategy; }
        public double checkout(double basePrice) { return strategy.price(basePrice); }
    }
}
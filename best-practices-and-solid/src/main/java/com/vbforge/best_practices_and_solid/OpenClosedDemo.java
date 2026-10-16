package com.vbforge.best_practices_and_solid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>O — Open/Closed Principle (OCP).</b>
 *
 * <p>"Software entities should be open for extension, closed for modification."
 * New behaviour arrives as <i>new code</i> (a new class, a new lambda, a new map entry),
 * not as an edit inside code that already works and is already tested.
 *
 * <p>Sections:
 * <ol>
 *   <li>An {@code if/else} chain that must be edited for every new customer type</li>
 *   <li>The same logic with a strategy interface and a registry</li>
 *   <li>{@code instanceof} chains vs polymorphism</li>
 * </ol>
 */
public class OpenClosedDemo {

    // ── 1. Violation: editing working code to add a case ──────────────────────
    /**
     * What it is: every new customer type forces an edit of {@code finalPrice}.
     *
     * <p>Why it hurts: each edit re-opens a tested method, and every {@code if} branch
     * is a place where a copy-paste mistake can hide. The same chain tends to be
     * copied into other methods (invoices, reports), so one new type means N edits.
     *
     * <p>Complexity: O(k) comparisons for k types.
     */
    public static class DiscountCalculatorBad {
        public double finalPrice(String customerType, double price) {
            if ("REGULAR".equals(customerType)) {
                return price;
            } else if ("SILVER".equals(customerType)) {
                return price - price * 0.05;
            } else if ("GOLD".equals(customerType)) {
                return price - price * 0.10;
            }
            throw new IllegalArgumentException("Unknown customer type: " + customerType);
        }
    }


    // ── 2. Strategy + registry: extend without touching the calculator ────────
    /**
     * What it is: each rule is a {@link DiscountPolicy}; the calculator only looks one up.
     *
     * <p>Why it matters: adding {@code PLATINUM} is one new map entry. {@link DiscountCalculator}
     * is never edited, so its tests never need to be re-trusted.
     *
     * <p>Complexity: O(1) average map lookup, independent of the number of types.
     *
     * <p>Gotcha: the constructor copies the map. Without the copy, a caller could
     * change the rules behind the calculator's back — "closed" would be a lie.
     */
    @FunctionalInterface
    public interface DiscountPolicy {
        /** @return the amount to subtract from {@code price} */
        double discountFor(double price);
    }

    public static class DiscountCalculator {
        private final Map<String, DiscountPolicy> policies;

        public DiscountCalculator(Map<String, DiscountPolicy> policies) {
            this.policies = new HashMap<>(policies);
        }

        public double finalPrice(String customerType, double price) {
            DiscountPolicy policy = policies.get(customerType);
            if (policy == null) {
                throw new IllegalArgumentException("Unknown customer type: " + customerType);
            }
            return price - policy.discountFor(price);
        }
    }

    public static Map<String, DiscountPolicy> standardPolicies() {
        Map<String, DiscountPolicy> policies = new HashMap<>();
        policies.put("REGULAR", price -> 0.0);
        policies.put("SILVER", price -> price * 0.05);
        policies.put("GOLD", price -> price * 0.10);
        return policies;
    }


    // ── 3. instanceof chain vs polymorphism ───────────────────────────────────
    /**
     * What it is: {@link AreaCalculatorBad} must learn about every shape; with
     * {@link Shape} each shape knows its own area.
     *
     * <p>Why it matters: {@code Triangle} below was "added later" — it works with
     * the polymorphic code immediately, while the bad calculator throws until someone
     * edits it.
     *
     * <p>Gotcha: OCP is not "never edit anything". Fix bugs freely; the principle is
     * about <i>new features</i> not requiring edits to stable code.
     */
    public interface Shape {
        double area();
    }

    public static class Circle implements Shape {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    public static class Rectangle implements Shape {
        private final double width;
        private final double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }
    }

    /** "Added later" — note that no existing class was edited to support it. */
    public static class Triangle implements Shape {
        private final double base;
        private final double height;

        public Triangle(double base, double height) {
            this.base = base;
            this.height = height;
        }

        @Override
        public double area() {
            return 0.5 * base * height;
        }
    }

    public static class AreaCalculatorBad {
        public double totalArea(List<?> shapes) {
            double total = 0;
            for (Object shape : shapes) {
                if (shape instanceof Circle) {
                    total += ((Circle) shape).area();
                } else if (shape instanceof Rectangle) {
                    total += ((Rectangle) shape).area();
                } else {
                    throw new IllegalArgumentException("Unsupported shape: " + shape.getClass().getSimpleName());
                }
            }
            return total;
        }
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void ifElseChainDemo() {
        DiscountCalculatorBad calculator = new DiscountCalculatorBad();
        System.out.println("GOLD 100 -> " + calculator.finalPrice("GOLD", 100.0));
        try {
            calculator.finalPrice("PLATINUM", 100.0);
        } catch (IllegalArgumentException e) {
            System.out.println("PLATINUM needs an edit: " + e.getMessage());
        }
    }

    static void strategyDemo() {
        Map<String, DiscountPolicy> policies = standardPolicies();
        policies.put("PLATINUM", price -> price * 0.20);          // extension: one new entry
        DiscountCalculator calculator = new DiscountCalculator(policies);
        System.out.println("PLATINUM 100 -> " + calculator.finalPrice("PLATINUM", 100.0));
    }

    static void polymorphismDemo() {
        List<Shape> shapes = java.util.Arrays.asList(new Rectangle(2, 3), new Triangle(4, 5));
        double total = 0;
        for (Shape shape : shapes) {
            total += shape.area();
        }
        System.out.println("total area with Triangle -> " + total);
        try {
            new AreaCalculatorBad().totalArea(shapes);
        } catch (IllegalArgumentException e) {
            System.out.println("bad calculator -> " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ifElseChainDemo();
        System.out.println("---");
        strategyDemo();
        System.out.println("---");
        polymorphismDemo();
    }
}

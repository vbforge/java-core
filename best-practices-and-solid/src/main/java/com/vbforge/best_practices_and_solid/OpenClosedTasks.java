package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.OpenClosedDemo.Shape;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

/**
 * 🎯 TASKS — Open/Closed Principle
 *
 * <p>Implement every method so that all tests in {@link OpenClosedTasksTest} pass.
 * Each method must keep working when callers pass <b>new</b> shapes, rules or
 * transformations — without you editing it again.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>No {@code instanceof} and no {@code if/else} chains on type names</li>
 * </ul>
 */
public class OpenClosedTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Sum the areas of all shapes. Works for any {@link Shape}, including ones
     * that do not exist yet.
     *
     * <p>Examples:
     * <pre>
     *   totalArea([Rectangle(2, 3), Triangle(4, 5)])  → 16.0   (6 + 10)
     *   totalArea([])                                  → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Ask each shape for its own area — the method must not know concrete types</li>
     * </ul>
     *
     * @param shapes the shapes to measure
     * @return the sum of {@code area()} over all shapes
     */
    public double totalArea(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement totalArea");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Apply a list of text transformations to {@code text}, in list order. Adding a new
     * kind of transformation must never require changing this method.
     *
     * <p>Examples:
     * <pre>
     *   applyTransformations("  hello ", [trim, toUpperCase])   → "HELLO"
     *   applyTransformations("abc", [s -> s + "!", s -> s + "?"]) → "abc!?"
     *   applyTransformations("abc", [])                          → "abc"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The output of one step is the input of the next</li>
     *   <li>{@link UnaryOperator#apply} runs one step</li>
     * </ul>
     *
     * @param text            the starting text
     * @param transformations the steps, applied first to last
     * @return the transformed text; {@code text} itself when the list is empty
     */
    public String applyTransformations(String text, List<UnaryOperator<String>> transformations) {
        throw new UnsupportedOperationException("TODO: implement applyTransformations");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Compute a shipping cost by looking the {@code method} up in {@code rules} and
     * applying that rule to the weight. A new shipping method is just a new map entry.
     *
     * <p>Throws {@link IllegalArgumentException} with the message
     * {@code "Unknown shipping method: <method>"} when no rule exists.
     *
     * <p>Examples (rules: STANDARD = 5 + kg, EXPRESS = 15 + 2.5 * kg):
     * <pre>
     *   shippingCost("STANDARD", 3.0, rules)  → 8.0
     *   shippingCost("EXPRESS", 2.0, rules)   → 20.0
     *   shippingCost("DRONE", 2.0, rules)     → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Map.get} returns {@code null} for a missing key</li>
     *   <li>{@link DoubleUnaryOperator#applyAsDouble} runs a rule</li>
     * </ul>
     *
     * @param method   the shipping method name
     * @param weightKg the parcel weight in kilograms
     * @param rules    the available rules by method name
     * @return the shipping cost
     */
    public double shippingCost(String method, double weightKg, Map<String, DoubleUnaryOperator> rules) {
        throw new UnsupportedOperationException("TODO: implement shippingCost");
    }
}

package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Eatable;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Workable;

import java.util.List;

/**
 * 🎯 TASKS — Interface Segregation Principle
 *
 * <p>Implement every method so that all tests in {@link InterfaceSegregationTasksTest} pass.
 * Each signature asks for exactly the roles the method needs — nothing more.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 * </ul>
 */
public class InterfaceSegregationTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Add up the work units produced by all workers. Works for humans, robots and
     * anything else that can {@code work()}.
     *
     * <p>Examples:
     * <pre>
     *   totalWorkUnits([Human(8 units), Robot(24 units)])  → 32
     *   totalWorkUnits([])                                  → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The wildcard {@code ? extends Workable} lets callers pass a list of any subtype</li>
     * </ul>
     *
     * @param workers anything that can work
     * @return the sum of {@code work()}
     */
    public int totalWorkUnits(List<? extends Workable> workers) {
        throw new UnsupportedOperationException("TODO: implement totalWorkUnits");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Collect what everyone eats during the lunch break, in list order.
     * A robot cannot be passed here — the signature does not allow it.
     *
     * <p>Examples:
     * <pre>
     *   lunchBreak([Human("Ann", "soup"), () -> "snack"])  → ["Ann eats soup", "snack"]
     *   lunchBreak([])                                      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Ask only for the role you need: {@code Eatable}</li>
     * </ul>
     *
     * @param beings anything that can eat
     * @return the results of {@code eat()}, in order
     */
    public List<String> lunchBreak(List<? extends Eatable> beings) {
        throw new UnsupportedOperationException("TODO: implement lunchBreak");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Describe a shift for something that can <b>both</b> work and eat: the work units,
     * a colon, then the meal text.
     *
     * <p>Examples:
     * <pre>
     *   shift(Human("Ann", 8 units, "soup"))  → "8:Ann eats soup"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code T extends Workable & Eatable} is an <i>intersection bound</i>: the
     *       parameter must play both roles, and a {@code Robot} no longer compiles</li>
     * </ul>
     *
     * @param worker something that can work and eat
     * @param <T>    the combined role type
     * @return {@code work() + ":" + eat()}
     */
    public <T extends Workable & Eatable> String shift(T worker) {
        throw new UnsupportedOperationException("TODO: implement shift");
    }
}

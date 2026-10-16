package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.MutableRectangle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Shape;

import java.util.List;

/**
 * 🎯 TASKS — Liskov Substitution Principle
 *
 * <p>Implement every method so that all tests in {@link LiskovSubstitutionTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 *   <li>Work only through the base type's contract — no {@code instanceof}, no {@code getClass()}</li>
 * </ul>
 */
public class LiskovSubstitutionTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the shape with the largest area. Must work for every {@link Shape}.
     * When several shapes tie for the largest area, return the one that comes first.
     *
     * <p>Throws {@link IllegalArgumentException} for an empty list.
     *
     * <p>Examples:
     * <pre>
     *   largestShape([Rectangle(2, 3), Square(3), Circle(1)])  → the Square (area 9.0)
     *   largestShape([Square(2), Rectangle(1, 4)])             → the Square (4.0 ties 4.0, first wins)
     *   largestShape([])                                       → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Track the best shape so far; replace it only when a <i>strictly</i> larger one shows up</li>
     * </ul>
     *
     * @param shapes the candidates
     * @return the shape with the greatest {@code area()}
     */
    public Shape largestShape(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement largestShape");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * A <b>contract probe</b>: decide whether {@code candidate} behaves like a real
     * rectangle. Set its width to 3 and its height to 7, then check that the area is 21.
     *
     * <p>Examples:
     * <pre>
     *   honoursRectangleContract(new MutableRectangle(1, 1))  → true
     *   honoursRectangleContract(new MutableSquare(1))        → false   (the area becomes 49)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This is how you <i>test</i> LSP: run the base-type contract against each subtype</li>
     * </ul>
     *
     * @param candidate a rectangle or any subtype of it
     * @return {@code true} when width and height behaved independently
     */
    public boolean honoursRectangleContract(MutableRectangle candidate) {
        throw new UnsupportedOperationException("TODO: implement honoursRectangleContract");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Add {@code items} to {@code target} when it allows modification and return
     * {@code target} itself. When {@code target} is read-only (it throws
     * {@link UnsupportedOperationException}), leave it untouched and return a
     * <b>new</b> modifiable list containing the old elements followed by {@code items}.
     *
     * <p>Examples:
     * <pre>
     *   target = new ArrayList [1, 2], items = [3]
     *       → returns target itself, now [1, 2, 3]
     *   target = Collections.unmodifiableList([1, 2]), items = [3]
     *       → returns a new list [1, 2, 3]; target is still [1, 2]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The JDK's read-only lists break the {@code List} contract — be defensive</li>
     *   <li>{@code addAll} signals "read-only" with an exception, not a return value</li>
     * </ul>
     *
     * @param target the list to extend if possible
     * @param items  the elements to append
     * @return {@code target} when modifiable, otherwise a new list with everything
     */
    public List<Integer> addAllOrCopy(List<Integer> target, List<Integer> items) {
        throw new UnsupportedOperationException("TODO: implement addAllOrCopy");
    }
}

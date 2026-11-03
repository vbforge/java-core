package com.vbforge.design_patterns.factory_pattern;

import java.util.List;
import java.util.function.Supplier;

/**
 * FactoryTasks — implement every method below to make {@code FactoryTasksTest} go GREEN.
 */
public class FactoryTasks {

    public interface Shape {
        String name();
        double area();
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a new {@link Shape} for the given type name (case-insensitive).
     * Supported types: {@code "circle"}, {@code "square"}, {@code "triangle"}.
     *
     * <p>Examples:
     * <pre>
     *   createShape("circle")   → a Shape with name "circle"   and area ≈ π
     *   createShape("SQUARE")   → a Shape with name "square"   and area 1.0
     *   createShape("hexagon")  → IllegalArgumentException
     *   createShape(null)       → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Implement the concrete shapes as anonymous classes returning the
     *       right name and area — the tests only check those two methods.</li>
     *   <li>Throw {@link IllegalArgumentException} for unknown / null types —
     *       never return {@code null}.</li>
     * </ul>
     *
     * @param type shape type name (may be mixed case)
     * @return a freshly constructed {@link Shape}
     */
    public Shape createShape(String type) {
        throw new UnsupportedOperationException("TODO: implement createShape");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a {@link Supplier} that produces fresh {@link Shape} instances
     * of the requested type every time {@code get()} is called.
     *
     * <p>Examples:
     * <pre>
     *   Supplier&lt;Shape&gt; s = shapeSupplier("circle");
     *   s.get() != s.get()          // fresh instance each call
     *   s.get().name()              // "circle"
     *   shapeSupplier("hexagon")    // IllegalArgumentException at factory creation
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Call {@code createShape(type)} once eagerly to validate — that way
     *       bad input fails immediately, not on the first {@code get()}.</li>
     *   <li>Return a lambda {@code () -> createShape(type)}.</li>
     * </ul>
     *
     * @param type shape type name
     * @return a supplier producing new shapes of the given type
     */
    public Supplier<Shape> shapeSupplier(String type) {
        throw new UnsupportedOperationException("TODO: implement shapeSupplier");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Maps each type name in {@code types} to a newly created {@link Shape},
     * preserving order. If any type is unknown, throws
     * {@link IllegalArgumentException} (do not partially apply — throw).
     *
     * <p>Examples:
     * <pre>
     *   createAll(["circle","square"])   → [Shape(circle), Shape(square)]
     *   createAll([])                    → []
     *   createAll(["circle","hexagon"])  → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A simple loop or {@code stream().map(...)} is fine.</li>
     *   <li>Do not catch and swallow the exception — let it propagate.</li>
     * </ul>
     *
     * @param types list of type names
     * @return list of shapes in the same order as {@code types}
     */
    public List<Shape> createAll(List<String> types) {
        throw new UnsupportedOperationException("TODO: implement createAll");
    }
}
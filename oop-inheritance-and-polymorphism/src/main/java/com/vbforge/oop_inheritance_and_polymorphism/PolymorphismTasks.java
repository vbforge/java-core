package com.vbforge.oop_inheritance_and_polymorphism;

import java.util.List;
import java.util.Map;

/**
 * PolymorphismTasks — implement every method below to make {@code PolymorphismTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code PolymorphismDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=PolymorphismTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The shape hierarchy {@link Shape} ← {@link Circle}, {@link Rectangle} ← {@link Square}
 * is already written for you — every task below works on those types. Do not change method
 * signatures or add external libraries — only the Java standard library is needed.
 */
public class PolymorphismTasks {

    // ── Given: shape hierarchy (do NOT modify) ─────────────────────────────────

    /** Base type. A plain {@code Shape} has no area. */
    public static class Shape {
        public double area() {
            return 0.0;
        }
    }

    public static class Circle extends Shape {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    public static class Rectangle extends Shape {
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

    /** A square IS-A rectangle whose sides are equal. */
    public static class Square extends Rectangle {
        public Square(double side) {
            super(side, side);
        }
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the sum of the areas of all shapes in {@code shapes}. The list may mix any
     * subtypes of {@link Shape}; each shape knows how to compute its own area.
     *
     * <p>Examples:
     * <pre>
     *   totalArea([Rectangle(2,3), Square(2)])   → 10.0   (6 + 4)
     *   totalArea([Circle(1), Square(2)])        → 7.14…  (π + 4)
     *   totalArea([])                            → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You should not need {@code instanceof} or any cast.</li>
     * </ul>
     *
     * @param shapes list of shapes (may be empty)
     * @return the sum of all areas
     */
    public double totalArea(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement totalArea");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the shape with the largest area. If several shapes tie for the largest area, the
     * one that appears FIRST in the list is returned. Returns {@code null} for an empty list.
     * The very same object from the list must be returned (not a copy).
     *
     * <p>Examples:
     * <pre>
     *   largest([Rectangle(1,1), Square(3), Rectangle(2,4)])  → Square(3)       (area 9 beats 8 and 1)
     *   largest([Rectangle(2,2), Square(2)])                  → Rectangle(2,2)  (tie → first wins)
     *   largest([])                                           → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Keep track of the best shape seen so far and replace it only when a candidate is
     *       strictly larger.</li>
     * </ul>
     *
     * @param shapes list of shapes (may be empty)
     * @return the shape with the largest area, or {@code null} if the list is empty
     */
    public Shape largest(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement largest");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Counts how many shapes of each RUNTIME class are in {@code shapes}. Keys are the simple
     * class names ({@code "Circle"}, {@code "Rectangle"}, {@code "Square"}); a {@code Square}
     * is counted as {@code "Square"}, not as {@code "Rectangle"}.
     *
     * <p>Examples:
     * <pre>
     *   countByType([Circle(1), Square(2), Circle(3), Rectangle(1,2)])
     *       → {"Circle"=2, "Square"=1, "Rectangle"=1}
     *
     *   countByType([])  → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The declared type of the list elements is always {@code Shape} — you need the type
     *       of the actual object. See {@link Object#getClass()}.</li>
     *   <li>{@code map.merge(key, 1, Integer::sum)} is a compact way to accumulate counts.</li>
     * </ul>
     *
     * @param shapes list of shapes (may be empty)
     * @return map from simple class name to number of shapes of exactly that class
     */
    public Map<String, Integer> countByType(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement countByType");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Counts the shapes that can be used as {@code type} (IS-A test): shapes of that exact class
     * AND of any of its subclasses.
     *
     * <p>Examples:
     * <pre>
     *   countInstancesOf([Rectangle(1,2), Square(2), Circle(1)], Rectangle.class)  → 2   (a Square IS-A Rectangle)
     *   countInstancesOf([Rectangle(1,2), Square(2), Circle(1)], Square.class)     → 1
     *   countInstancesOf([Rectangle(1,2), Square(2), Circle(1)], Circle.class)     → 1
     *   countInstancesOf([Rectangle(1,2), Square(2), Circle(1)], Shape.class)      → 3
     *   countInstancesOf([], Shape.class)                                          → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#isInstance(Object)} answers exactly this question for a given object.</li>
     * </ul>
     *
     * @param shapes list of shapes (may be empty)
     * @param type   the type to test against
     * @return number of shapes that are instances of {@code type}
     */
    public int countInstancesOf(List<Shape> shapes, Class<? extends Shape> type) {
        throw new UnsupportedOperationException("TODO: implement countInstancesOf");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a NEW list containing only the shapes that are rectangles (including squares),
     * typed as {@link Rectangle}, in their original order. The elements must be the same objects
     * as in the input.
     *
     * <p>Examples:
     * <pre>
     *   rectanglesOnly([Circle(1), Square(2), Rectangle(1,2)])  → [Square(2), Rectangle(1,2)]
     *   rectanglesOnly([Circle(1), Circle(2)])                  → []
     *   rectanglesOnly([])                                      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check the type first, then downcast — casting blindly would throw
     *       {@link ClassCastException} for a circle.</li>
     * </ul>
     *
     * @param shapes list of shapes (may be empty)
     * @return new list with every element that is a {@code Rectangle}
     */
    public List<Rectangle> rectanglesOnly(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement rectanglesOnly");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a NEW list with the same shapes sorted by area, largest first. The input list must
     * not be modified. Shapes with equal areas keep their original relative order.
     *
     * <p>Examples:
     * <pre>
     *   sortByAreaDescending([Rectangle(1,1), Square(3), Rectangle(2,4)])
     *       → [Square(3), Rectangle(2,4), Rectangle(1,1)]      (areas 9, 8, 1)
     *
     *   sortByAreaDescending([Rectangle(2,2), Square(2)])
     *       → [Rectangle(2,2), Square(2)]                      (equal areas → original order)
     *
     *   sortByAreaDescending([])  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Copy the list first, then sort the copy.</li>
     *   <li>{@link java.util.Comparator#comparingDouble} builds a comparator from a key extractor;
     *       {@link List#sort} is a stable sort.</li>
     * </ul>
     *
     * @param shapes list of shapes (may be empty; not modified)
     * @return a new list sorted by area descending
     */
    public List<Shape> sortByAreaDescending(List<Shape> shapes) {
        throw new UnsupportedOperationException("TODO: implement sortByAreaDescending");
    }
}

package com.vbforge.oop_inheritance_and_polymorphism;

import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismTasks.Circle;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismTasks.Rectangle;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismTasks.Shape;
import com.vbforge.oop_inheritance_and_polymorphism.PolymorphismTasks.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * PolymorphismTasksTest — tests for every method in {@link PolymorphismTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code PolymorphismTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=PolymorphismTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("PolymorphismTasks — your implementations")
class PolymorphismTasksTest {

    private PolymorphismTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PolymorphismTasks();
    }

    // ── Task 1: totalArea ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement totalArea()")
    @DisplayName("Task 1 — totalArea()")
    class TotalAreaTest {

        @Test
        @DisplayName("[Rectangle(2,3), Square(2)] → 10.0")
        void rectanglesAndSquares() {
            List<Shape> shapes = Arrays.<Shape>asList(new Rectangle(2, 3), new Square(2));
            assertThat(tasks.totalArea(shapes)).isEqualTo(10.0);
        }

        @Test
        @DisplayName("[Circle(1), Square(2)] → π + 4")
        void withCircle() {
            List<Shape> shapes = Arrays.<Shape>asList(new Circle(1), new Square(2));
            assertThat(tasks.totalArea(shapes)).isCloseTo(Math.PI + 4, within(1e-9));
        }

        @Test
        @DisplayName("empty list → 0.0")
        void emptyList() {
            assertThat(tasks.totalArea(new ArrayList<Shape>())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("single shape → its own area")
        void singleShape() {
            assertThat(tasks.totalArea(Collections.<Shape>singletonList(new Square(5)))).isEqualTo(25.0);
        }

        @Test
        @DisplayName("a plain Shape contributes 0.0")
        void plainShape() {
            List<Shape> shapes = Arrays.<Shape>asList(new Shape(), new Square(3));
            assertThat(tasks.totalArea(shapes)).isEqualTo(9.0);
        }
    }

    // ── Task 2: largest ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement largest()")
    @DisplayName("Task 2 — largest()")
    class LargestTest {

        @Test
        @DisplayName("[Rectangle(1,1), Square(3), Rectangle(2,4)] → the Square(3)")
        void basic() {
            Shape small = new Rectangle(1, 1);
            Shape biggest = new Square(3);
            Shape medium = new Rectangle(2, 4);
            assertThat(tasks.largest(Arrays.asList(small, biggest, medium))).isSameAs(biggest);
        }

        @Test
        @DisplayName("tie → the first one in the list wins")
        void tieFirstWins() {
            Shape first = new Rectangle(2, 2);
            Shape second = new Square(2);
            assertThat(tasks.largest(Arrays.asList(first, second))).isSameAs(first);
        }

        @Test
        @DisplayName("empty list → null")
        void emptyList() {
            assertThat(tasks.largest(new ArrayList<Shape>())).isNull();
        }

        @Test
        @DisplayName("single element → that element")
        void singleElement() {
            Shape only = new Circle(1);
            assertThat(tasks.largest(Collections.singletonList(only))).isSameAs(only);
        }

        @Test
        @DisplayName("a circle can beat a square")
        void circleWins() {
            Shape square = new Square(2);
            Shape circle = new Circle(2);        // area ≈ 12.57
            assertThat(tasks.largest(Arrays.asList(square, circle))).isSameAs(circle);
        }
    }

    // ── Task 3: countByType ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countByType()")
    @DisplayName("Task 3 — countByType()")
    class CountByTypeTest {

        @Test
        @DisplayName("counts by runtime class")
        void basic() {
            List<Shape> shapes = Arrays.<Shape>asList(
                    new Circle(1), new Square(2), new Circle(3), new Rectangle(1, 2));
            Map<String, Integer> counts = tasks.countByType(shapes);
            assertThat(counts).containsEntry("Circle", 2)
                              .containsEntry("Square", 1)
                              .containsEntry("Rectangle", 1)
                              .hasSize(3);
        }

        @Test
        @DisplayName("a Square is counted as Square, never as Rectangle")
        void squareIsNotRectangle() {
            Map<String, Integer> counts = tasks.countByType(
                    Arrays.<Shape>asList(new Square(1), new Square(2)));
            assertThat(counts).containsEntry("Square", 2).doesNotContainKey("Rectangle");
        }

        @Test
        @DisplayName("empty list → empty map")
        void emptyList() {
            assertThat(tasks.countByType(new ArrayList<Shape>())).isEmpty();
        }

        @Test
        @DisplayName("a plain Shape is counted under \"Shape\"")
        void plainShape() {
            assertThat(tasks.countByType(Collections.<Shape>singletonList(new Shape())))
                    .containsEntry("Shape", 1)
                    .hasSize(1);
        }
    }

    // ── Task 4: countInstancesOf ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countInstancesOf()")
    @DisplayName("Task 4 — countInstancesOf()")
    class CountInstancesOfTest {

        private final List<Shape> mixed = Arrays.<Shape>asList(
                new Rectangle(1, 2), new Square(2), new Circle(1));

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "Shape,     3",
                "Rectangle, 2",
                "Square,    1",
                "Circle,    1",
        })
        @DisplayName("parametrized: counts per type in [Rectangle, Square, Circle]")
        void perType(String simpleName, int expected) {
            assertThat(tasks.countInstancesOf(mixed, typeNamed(simpleName))).isEqualTo(expected);
        }

        @Test
        @DisplayName("empty list → 0")
        void emptyList() {
            assertThat(tasks.countInstancesOf(new ArrayList<Shape>(), Shape.class)).isZero();
        }

        @Test
        @DisplayName("no match → 0")
        void noMatch() {
            List<Shape> circles = Arrays.<Shape>asList(new Circle(1), new Circle(2));
            assertThat(tasks.countInstancesOf(circles, Rectangle.class)).isZero();
        }

        private Class<? extends Shape> typeNamed(String simpleName) {
            switch (simpleName) {
                case "Shape":     return Shape.class;
                case "Rectangle": return Rectangle.class;
                case "Square":    return Square.class;
                default:          return Circle.class;
            }
        }
    }

    // ── Task 5: rectanglesOnly ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement rectanglesOnly()")
    @DisplayName("Task 5 — rectanglesOnly()")
    class RectanglesOnlyTest {

        @Test
        @DisplayName("[Circle, Square, Rectangle] → [Square, Rectangle] (same objects)")
        void basic() {
            Shape circle = new Circle(1);
            Rectangle square = new Square(2);
            Rectangle rect = new Rectangle(1, 2);
            List<Rectangle> result = tasks.rectanglesOnly(Arrays.asList(circle, square, rect));
            assertThat(result).containsExactly(square, rect);
        }

        @Test
        @DisplayName("no rectangles → empty list")
        void noRectangles() {
            List<Shape> circles = Arrays.<Shape>asList(new Circle(1), new Circle(2));
            assertThat(tasks.rectanglesOnly(circles)).isEmpty();
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.rectanglesOnly(new ArrayList<Shape>())).isEmpty();
        }

        @Test
        @DisplayName("original order is preserved")
        void preservesOrder() {
            Rectangle a = new Rectangle(1, 1);
            Rectangle b = new Square(5);
            Rectangle c = new Rectangle(2, 2);
            assertThat(tasks.rectanglesOnly(Arrays.<Shape>asList(a, b, c))).containsExactly(a, b, c);
        }

        @Test
        @DisplayName("returns a NEW list, not the input")
        void returnsNewList() {
            List<Shape> input = new ArrayList<Shape>(Arrays.<Shape>asList(new Square(1)));
            List<Rectangle> result = tasks.rectanglesOnly(input);
            assertThat((Object) result).isNotSameAs(input);
        }
    }

    // ── Task 6: sortByAreaDescending ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sortByAreaDescending()")
    @DisplayName("Task 6 — sortByAreaDescending()")
    class SortByAreaDescendingTest {

        @Test
        @DisplayName("sorts largest first")
        void basic() {
            Shape small = new Rectangle(1, 1);      // 1
            Shape big = new Square(3);              // 9
            Shape medium = new Rectangle(2, 4);     // 8
            assertThat(tasks.sortByAreaDescending(Arrays.asList(small, big, medium)))
                    .containsExactly(big, medium, small);
        }

        @Test
        @DisplayName("equal areas keep their original relative order")
        void stableOnTies() {
            Shape first = new Rectangle(2, 2);
            Shape second = new Square(2);
            assertThat(tasks.sortByAreaDescending(Arrays.asList(first, second)))
                    .containsExactly(first, second);
        }

        @Test
        @DisplayName("empty list → empty list")
        void emptyList() {
            assertThat(tasks.sortByAreaDescending(new ArrayList<Shape>())).isEmpty();
        }

        @Test
        @DisplayName("the input list is not modified")
        void inputUntouched() {
            Shape small = new Square(1);
            Shape big = new Square(4);
            List<Shape> input = new ArrayList<Shape>(Arrays.asList(small, big));
            tasks.sortByAreaDescending(input);
            assertThat(input).containsExactly(small, big);
        }

        @Test
        @DisplayName("returns a NEW list")
        void returnsNewList() {
            List<Shape> input = new ArrayList<Shape>(Arrays.<Shape>asList(new Square(1), new Square(2)));
            assertThat(tasks.sortByAreaDescending(input)).isNotSameAs(input);
        }

        @Test
        @DisplayName("already sorted input stays in the same order")
        void alreadySorted() {
            Shape big = new Square(3);
            Shape small = new Square(1);
            assertThat(tasks.sortByAreaDescending(Arrays.asList(big, small))).containsExactly(big, small);
        }
    }
}

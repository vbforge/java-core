package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Circle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.MutableRectangle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.MutableSquare;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Rectangle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Shape;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Square;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LiskovSubstitutionTasks — your implementations")
class LiskovSubstitutionTasksTest {

    private LiskovSubstitutionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LiskovSubstitutionTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement largestShape()")
    @DisplayName("largestShape(shapes)")
    class LargestShapeTest {

        @Test
        @DisplayName("returns the shape with the greatest area")
        void picksLargest() {
            Shape square = new Square(3);

            assertThat(tasks.largestShape(Arrays.asList(new Rectangle(2, 3), square, new Circle(1)))).isSameAs(square);
        }

        @Test
        @DisplayName("on a tie the first shape wins")
        void tieFirstWins() {
            Shape first = new Square(2);
            Shape second = new Rectangle(1, 4);

            assertThat(tasks.largestShape(Arrays.asList(first, second))).isSameAs(first);
        }

        @Test
        @DisplayName("a single shape is returned as is")
        void singleShape() {
            Shape only = new Circle(2);

            assertThat(tasks.largestShape(Collections.singletonList(only))).isSameAs(only);
        }

        @Test
        @DisplayName("works for a shape type it has never seen")
        void unknownShape() {
            Shape huge = () -> 1000.0;

            assertThat(tasks.largestShape(Arrays.asList(new Square(5), huge))).isSameAs(huge);
        }

        @Test
        @DisplayName("an empty list is rejected")
        void emptyList() {
            assertThatThrownBy(() -> tasks.largestShape(Collections.<Shape>emptyList()))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement honoursRectangleContract()")
    @DisplayName("honoursRectangleContract(candidate)")
    class HonoursRectangleContractTest {

        @Test
        @DisplayName("a real rectangle honours the contract")
        void rectangleHonours() {
            assertThat(tasks.honoursRectangleContract(new MutableRectangle(1, 1))).isTrue();
        }

        @Test
        @DisplayName("a mutable square does not (width and height are coupled)")
        void squareViolates() {
            assertThat(tasks.honoursRectangleContract(new MutableSquare(1))).isFalse();
        }

        @Test
        @DisplayName("a well-behaved custom subclass honours the contract")
        void wellBehavedSubclass() {
            MutableRectangle fancy = new MutableRectangle(2, 2) {
                @Override
                public int area() {
                    return getWidth() * getHeight();
                }
            };

            assertThat(tasks.honoursRectangleContract(fancy)).isTrue();
        }

        @Test
        @DisplayName("a subclass that ignores setHeight is detected")
        void ignoringSetter() {
            MutableRectangle broken = new MutableRectangle(2, 2) {
                @Override
                public void setHeight(int height) {
                    // silently ignores the new height
                }
            };

            assertThat(tasks.honoursRectangleContract(broken)).isFalse();
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement addAllOrCopy()")
    @DisplayName("addAllOrCopy(target, items)")
    class AddAllOrCopyTest {

        @Test
        @DisplayName("a modifiable list is extended in place and returned")
        void modifiableList() {
            List<Integer> target = new ArrayList<>(Arrays.asList(1, 2));

            List<Integer> result = tasks.addAllOrCopy(target, Collections.singletonList(3));

            assertThat(result).isSameAs(target).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("a read-only list is left untouched and a new list is returned")
        void readOnlyList() {
            List<Integer> target = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(1, 2)));

            List<Integer> result = tasks.addAllOrCopy(target, Collections.singletonList(3));

            assertThat(result).isNotSameAs(target).containsExactly(1, 2, 3);
            assertThat(target).containsExactly(1, 2);
        }

        @Test
        @DisplayName("a fixed-size list from Arrays.asList is also left untouched")
        void fixedSizeList() {
            List<Integer> target = Arrays.asList(1, 2);

            List<Integer> result = tasks.addAllOrCopy(target, Arrays.asList(3, 4));

            assertThat(result).isNotSameAs(target).containsExactly(1, 2, 3, 4);
            assertThat(target).containsExactly(1, 2);
        }

        @Test
        @DisplayName("the returned copy can be modified further")
        void copyIsModifiable() {
            List<Integer> result = tasks.addAllOrCopy(Collections.unmodifiableList(new ArrayList<Integer>()), Collections.singletonList(1));

            result.add(2);

            assertThat(result).containsExactly(1, 2);
        }
    }
}

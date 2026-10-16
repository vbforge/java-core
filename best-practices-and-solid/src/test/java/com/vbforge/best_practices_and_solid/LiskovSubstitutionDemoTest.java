package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.BadBird;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.BadPenguin;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Circle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.FlyingBird;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.MutableRectangle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.MutableSquare;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Penguin;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Rectangle;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Shape;
import com.vbforge.best_practices_and_solid.LiskovSubstitutionDemo.Square;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("LiskovSubstitutionDemo — API contracts")
class LiskovSubstitutionDemoTest {

    @Nested
    @DisplayName("1. Square extends Rectangle")
    class SquareRectangleSection {

        @Test
        @DisplayName("a rectangle keeps width and height independent")
        void rectangleHonoursContract() {
            assertThat(LiskovSubstitutionDemo.resizeAndGetArea(new MutableRectangle(1, 1), 5, 4)).isEqualTo(20);
        }

        @Test
        @DisplayName("a square substituted for a rectangle silently returns the wrong area")
        void squareBreaksContract() {
            assertThat(LiskovSubstitutionDemo.resizeAndGetArea(new MutableSquare(1), 5, 4)).isEqualTo(16);
        }

        @Test
        @DisplayName("setting the width of a square also changes its height")
        void squareCouplesSides() {
            MutableSquare square = new MutableSquare(1);

            square.setWidth(5);

            assertThat(square.getHeight()).isEqualTo(5);
        }
    }


    @Nested
    @DisplayName("2. A shared abstraction instead of inheritance")
    class SharedAbstractionSection {

        @Test
        @DisplayName("every Shape implementation answers area()")
        void allShapesHaveAnArea() {
            List<Shape> shapes = Arrays.<Shape>asList(new Rectangle(2, 3), new Square(3), new Circle(1));

            assertThat(shapes.get(0).area()).isEqualTo(6.0);
            assertThat(shapes.get(1).area()).isEqualTo(9.0);
            assertThat(shapes.get(2).area()).isCloseTo(Math.PI, within(1e-9));
        }

        @Test
        @DisplayName("Square is not a Rectangle, so there is no setter contract to break")
        void squareIsNotARectangle() {
            assertThat(new Square(2)).isNotInstanceOf(Rectangle.class);
        }
    }


    @Nested
    @DisplayName("3. Subtypes that throw \"not supported\"")
    class NotSupportedSection {

        @Test
        @DisplayName("a base-class bird can fly")
        void badBirdFlies() {
            assertThat(new BadBird().fly()).isEqualTo("flap flap");
        }

        @Test
        @DisplayName("BadPenguin breaks code written against BadBird")
        void badPenguinThrows() {
            BadBird bird = new BadPenguin();

            assertThatThrownBy(bird::fly)
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("Penguins cannot fly");
        }

        @Test
        @DisplayName("flyAll works for every FlyingBird")
        void flyAllWorks() {
            List<String> results = LiskovSubstitutionDemo.flyAll(
                    Arrays.asList(new FlyingBird("Sparrow"), new FlyingBird("Eagle")));

            assertThat(results).containsExactly("Sparrow flies", "Eagle flies");
        }

        @Test
        @DisplayName("a Penguin is not a FlyingBird, so flyAll cannot even receive one")
        void penguinIsNotAFlyingBird() {
            assertThat(new Penguin("Pingu")).isNotInstanceOf(FlyingBird.class);
            assertThat(new Penguin("Pingu").swim()).isEqualTo("Pingu swims");
        }
    }


    @Nested
    @DisplayName("4. A violation inside the JDK")
    class JdkSection {

        @Test
        @DisplayName("an ArrayList is modifiable")
        void arrayListIsModifiable() {
            assertThat(LiskovSubstitutionDemo.isModifiable(new ArrayList<Integer>())).isTrue();
        }

        @Test
        @DisplayName("Collections.unmodifiableList is a List that refuses add")
        void unmodifiableListRefusesAdd() {
            List<Integer> readOnly = Collections.unmodifiableList(new ArrayList<Integer>());

            assertThat(LiskovSubstitutionDemo.isModifiable(readOnly)).isFalse();
        }

        @Test
        @DisplayName("Arrays.asList is fixed-size, so add fails as well")
        void arraysAsListIsFixedSize() {
            assertThat(LiskovSubstitutionDemo.isModifiable(Arrays.asList(1, 2, 3))).isFalse();
        }
    }
}

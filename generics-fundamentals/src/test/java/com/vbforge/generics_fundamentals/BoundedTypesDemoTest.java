package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.BoundedTypesDemo.NumberBox;
import com.vbforge.generics_fundamentals.BoundedTypesDemo.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BoundedTypesDemo — API contracts")
class BoundedTypesDemoTest {

    @Nested
    @DisplayName("1. Bounded type parameter on a class")
    class BoundedClass {

        @Test
        @DisplayName("asDouble converts an Integer through the Number bound")
        void asDoubleFromInteger() {
            assertThat(new NumberBox<>(42).asDouble()).isEqualTo(42.0);
        }

        @Test
        @DisplayName("asDouble works for Double too")
        void asDoubleFromDouble() {
            assertThat(new NumberBox<>(-1.5).asDouble()).isEqualTo(-1.5);
        }

        @ParameterizedTest(name = "isPositive({0}) = {1}")
        @CsvSource({"5, true", "1, true", "0, false", "-3, false"})
        @DisplayName("isPositive is true only above zero")
        void isPositive(int value, boolean expected) {
            assertThat(new NumberBox<>(value).isPositive()).isEqualTo(expected);
        }

        @Test
        @DisplayName("get returns the value with its exact type")
        void getKeepsType() {
            NumberBox<Integer> box = new NumberBox<>(7);

            Integer value = box.get();

            assertThat(value).isEqualTo(7);
        }

        @Test
        @DisplayName("null is rejected")
        void rejectsNull() {
            assertThatThrownBy(() -> new NumberBox<Integer>(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("a bounded T is erased to its bound (Number), not Object")
        void erasedToBound() {
            assertThat(BoundedTypesDemo.erasedValueType()).isEqualTo(Number.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BoundedTypesDemo::boundedClassDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Bounded generic methods and Comparable")
    class ComparableBound {

        @Test
        @DisplayName("maxOf picks the larger Integer")
        void maxOfIntegers() {
            assertThat(BoundedTypesDemo.maxOf(3, 7)).isEqualTo(7);
            assertThat(BoundedTypesDemo.maxOf(7, 3)).isEqualTo(7);
        }

        @Test
        @DisplayName("maxOf picks the alphabetically larger String")
        void maxOfStrings() {
            assertThat(BoundedTypesDemo.maxOf("pear", "apple")).isEqualTo("pear");
        }

        @Test
        @DisplayName("largest finds the maximum of an unsorted array")
        void largestUnsorted() {
            assertThat(BoundedTypesDemo.largest(new String[]{"b", "z", "a"})).isEqualTo("z");
        }

        @Test
        @DisplayName("largest of a single-element array is that element")
        void largestSingle() {
            assertThat(BoundedTypesDemo.largest(new Integer[]{5})).isEqualTo(5);
        }

        @Test
        @DisplayName("largest of an empty array throws IllegalArgumentException")
        void largestEmpty() {
            assertThatThrownBy(() -> BoundedTypesDemo.largest(new Integer[0]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BoundedTypesDemo::comparableBoundDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Multiple bounds")
    class MultipleBounds {

        @ParameterizedTest(name = "isBetween({0}, 1, 10) = {1}")
        @CsvSource({"5, true", "1, true", "10, true", "0, false", "11, false"})
        @DisplayName("isBetween is inclusive on both ends")
        void isBetweenIntegers(int value, boolean expected) {
            assertThat(BoundedTypesDemo.isBetween(value, 1, 10)).isEqualTo(expected);
        }

        @Test
        @DisplayName("isBetween works for Double")
        void isBetweenDoubles() {
            assertThat(BoundedTypesDemo.isBetween(2.5, 3.0, 4.0)).isFalse();
            assertThat(BoundedTypesDemo.isBetween(3.5, 3.0, 4.0)).isTrue();
        }

        @Test
        @DisplayName("BigDecimal satisfies both bounds (Number and Comparable<BigDecimal>)")
        void isBetweenBigDecimal() {
            assertThat(BoundedTypesDemo.isBetween(new BigDecimal("1.5"), new BigDecimal("1"), new BigDecimal("2")))
                    .isTrue();
        }

        @Test
        @DisplayName("largerAsDouble compares with Comparable and converts with Number")
        void largerAsDouble() {
            assertThat(BoundedTypesDemo.largerAsDouble(3, 8)).isEqualTo(8.0);
            assertThat(BoundedTypesDemo.largerAsDouble(2.5, 1.5)).isEqualTo(2.5);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BoundedTypesDemo::multipleBoundsDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. Your own types satisfy bounds too")
    class CustomComparable {

        @Test
        @DisplayName("Version orders by major, then by minor number (1.9 < 1.10)")
        void versionOrdering() {
            assertThat(new Version(1, 9).compareTo(new Version(1, 10))).isNegative();
            assertThat(new Version(2, 0).compareTo(new Version(1, 10))).isPositive();
            assertThat(new Version(1, 2).compareTo(new Version(1, 2))).isZero();
        }

        @Test
        @DisplayName("largest works with a custom Comparable type")
        void largestVersion() {
            Version[] versions = {new Version(1, 9), new Version(2, 0), new Version(1, 10)};

            assertThat(BoundedTypesDemo.largest(versions)).isEqualTo(new Version(2, 0));
        }

        @Test
        @DisplayName("maxOf works with a custom Comparable type")
        void maxOfVersions() {
            assertThat(BoundedTypesDemo.maxOf(new Version(1, 2), new Version(1, 10))).isEqualTo(new Version(1, 10));
        }

        @Test
        @DisplayName("Version equality, hash code and toString")
        void versionBasics() {
            assertThat(new Version(1, 2)).isEqualTo(new Version(1, 2));
            assertThat(new Version(1, 2).hashCode()).isEqualTo(new Version(1, 2).hashCode());
            assertThat(new Version(1, 2).toString()).isEqualTo("1.2");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(BoundedTypesDemo::customComparableDemo).doesNotThrowAnyException();
        }
    }
}

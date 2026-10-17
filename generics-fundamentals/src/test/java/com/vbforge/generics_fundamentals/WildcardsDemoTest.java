package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.WildcardsDemo.Apple;
import com.vbforge.generics_fundamentals.WildcardsDemo.Fruit;
import com.vbforge.generics_fundamentals.WildcardsDemo.Orange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("WildcardsDemo — API contracts")
class WildcardsDemoTest {

    @Nested
    @DisplayName("1. Invariance — why wildcards exist")
    class Invariance {

        @Test
        @DisplayName("arrays are covariant: storing an Orange in an Apple[] fails at runtime")
        void covariantArrayFailsAtRuntime() {
            assertThatThrownBy(WildcardsDemo::storeIntoCovariantArray).isInstanceOf(ArrayStoreException.class);
        }

        @Test
        @DisplayName("a List<? extends Fruit> view can point at a List<Apple>")
        void wildcardViewAcceptsSubtypeList() {
            List<Apple> apples = new ArrayList<>();
            apples.add(new Apple(100));

            List<? extends Fruit> view = apples;
            Fruit first = view.get(0);

            assertThat(first.getName()).isEqualTo("apple");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(WildcardsDemo::invarianceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Upper-bounded wildcard: ? extends T (producer)")
    class UpperBounded {

        @Test
        @DisplayName("sum accepts a List<Integer>")
        void sumIntegers() {
            assertThat(WildcardsDemo.sum(Arrays.asList(1, 2, 3))).isEqualTo(6.0);
        }

        @Test
        @DisplayName("sum accepts a List<Double>")
        void sumDoubles() {
            assertThat(WildcardsDemo.sum(Arrays.asList(0.5, 1.5))).isEqualTo(2.0);
        }

        @Test
        @DisplayName("sum accepts any Collection, such as a Set<Long>")
        void sumSetOfLongs() {
            assertThat(WildcardsDemo.sum(new HashSet<>(Arrays.asList(10L, 20L)))).isEqualTo(30.0);
        }

        @Test
        @DisplayName("sum accepts a List<Number> with mixed subtypes")
        void sumMixed() {
            List<Number> mixed = Arrays.<Number>asList(1, 2.5, 3L);

            assertThat(WildcardsDemo.sum(mixed)).isEqualTo(6.5);
        }

        @Test
        @DisplayName("sum of an empty collection is 0.0")
        void sumEmpty() {
            assertThat(WildcardsDemo.sum(Collections.<Integer>emptyList())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("only null can be added to a List<? extends Number>")
        void onlyNullCanBeAdded() {
            assertThat(WildcardsDemo.addNullToExtendsList()).isEqualTo(1);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(WildcardsDemo::extendsDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Lower-bounded wildcard: ? super T (consumer)")
    class LowerBounded {

        @Test
        @DisplayName("addApples fills a List<Apple>")
        void intoAppleList() {
            List<Apple> basket = new ArrayList<>();

            WildcardsDemo.addApples(basket, 3);

            assertThat(basket).hasSize(3);
        }

        @Test
        @DisplayName("addApples also fills a List<Fruit> and a List<Object>")
        void intoSupertypeLists() {
            List<Fruit> fruits = new ArrayList<>();
            List<Object> objects = new ArrayList<>();

            WildcardsDemo.addApples(fruits, 2);
            WildcardsDemo.addApples(objects, 1);

            assertThat(fruits).hasSize(2);
            assertThat(objects).hasSize(1);
        }

        @Test
        @DisplayName("added apples weigh 100g, 101g, ... in order")
        void applesInOrder() {
            List<Fruit> fruits = new ArrayList<>();

            WildcardsDemo.addApples(fruits, 3);

            assertThat(fruits.get(0).getGrams()).isEqualTo(100);
            assertThat(fruits.get(1).getGrams()).isEqualTo(101);
            assertThat(fruits.get(2).getGrams()).isEqualTo(102);
        }

        @Test
        @DisplayName("a count of zero adds nothing")
        void zeroCount() {
            List<Object> objects = new ArrayList<>();

            WildcardsDemo.addApples(objects, 0);

            assertThat(objects).isEmpty();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(WildcardsDemo::superDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. PECS: Producer Extends, Consumer Super")
    class Pecs {

        @Test
        @DisplayName("copyInto appends Apples to a List<Fruit> in order")
        void appleToFruit() {
            List<Apple> apples = Arrays.asList(new Apple(120), new Apple(130));
            List<Fruit> fruits = new ArrayList<>();
            fruits.add(new Orange(200));

            WildcardsDemo.copyInto(fruits, apples);

            assertThat(fruits).hasSize(3);
            assertThat(fruits.get(0)).isInstanceOf(Orange.class);
            assertThat(fruits.get(1)).isSameAs(apples.get(0));
            assertThat(fruits.get(2)).isSameAs(apples.get(1));
        }

        @Test
        @DisplayName("copyInto works from List<Integer> into List<Number>")
        void integerToNumber() {
            List<Number> numbers = new ArrayList<>();

            WildcardsDemo.copyInto(numbers, Arrays.asList(1, 2));

            assertThat(numbers).containsExactly(1, 2);
        }

        @Test
        @DisplayName("copyInto leaves the source unchanged")
        void sourceUnchanged() {
            List<String> source = new ArrayList<>(Arrays.asList("a", "b"));
            List<Object> destination = new ArrayList<>();

            WildcardsDemo.copyInto(destination, source);

            assertThat(source).containsExactly("a", "b");
            assertThat(destination).containsExactly("a", "b");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(WildcardsDemo::pecsDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("5. Unbounded wildcard ? and wildcard capture")
    class Unbounded {

        @Test
        @DisplayName("describe works for lists of any type")
        void describeAnyList() {
            assertThat(WildcardsDemo.describe(Arrays.asList("x", "y"))).isEqualTo("2 element(s), first: x");
            assertThat(WildcardsDemo.describe(Arrays.asList(1, 2, 3))).isEqualTo("3 element(s), first: 1");
        }

        @Test
        @DisplayName("describe handles an empty list")
        void describeEmpty() {
            assertThat(WildcardsDemo.describe(Collections.emptyList())).isEqualTo("0 element(s), first: -");
        }

        @Test
        @DisplayName("swapFirstAndLast swaps the ends of a List<String>")
        void swapStrings() {
            List<String> words = new ArrayList<>(Arrays.asList("a", "b", "c"));

            WildcardsDemo.swapFirstAndLast(words);

            assertThat(words).containsExactly("c", "b", "a");
        }

        @Test
        @DisplayName("swapFirstAndLast works for a two-element List<Integer>")
        void swapTwoIntegers() {
            List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2));

            WildcardsDemo.swapFirstAndLast(numbers);

            assertThat(numbers).containsExactly(2, 1);
        }

        @Test
        @DisplayName("swapFirstAndLast leaves empty and single-element lists alone")
        void swapTooShort() {
            List<String> empty = new ArrayList<>();
            List<String> single = new ArrayList<>(Collections.singletonList("only"));

            WildcardsDemo.swapFirstAndLast(empty);
            WildcardsDemo.swapFirstAndLast(single);

            assertThat(empty).isEmpty();
            assertThat(single).containsExactly("only");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(WildcardsDemo::unboundedDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("6. Comparable<? super T>")
    class ComparableSuper {

        @Test
        @DisplayName("maxOf accepts Apples although Apple is only Comparable<Fruit>")
        void maxOfApples() {
            Apple heavier = new Apple(180);

            Apple result = WildcardsDemo.maxOf(new Apple(120), heavier);

            assertThat(result).isSameAs(heavier);
        }

        @Test
        @DisplayName("maxOf still works for plain Comparable types")
        void maxOfIntegersAndStrings() {
            assertThat(WildcardsDemo.maxOf(3, 7)).isEqualTo(7);
            assertThat(WildcardsDemo.maxOf("b", "a")).isEqualTo("b");
        }

        @Test
        @DisplayName("Fruit compares by weight")
        void fruitComparesByWeight() {
            assertThat(new Apple(100).compareTo(new Orange(150))).isNegative();
            assertThat(new Orange(150).compareTo(new Apple(100))).isPositive();
        }

        @Test
        @DisplayName("Fruit toString shows name and weight")
        void fruitToString() {
            assertThat(new Apple(120).toString()).isEqualTo("apple(120g)");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(WildcardsDemo::comparableSuperDemo).doesNotThrowAnyException();
        }
    }
}

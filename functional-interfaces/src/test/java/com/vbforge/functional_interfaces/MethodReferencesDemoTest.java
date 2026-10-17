package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.MethodReferencesDemo.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MethodReferencesDemo — API contracts")
class MethodReferencesDemoTest {

    @Nested
    @DisplayName("0. mapAll helper")
    class MapAll {

        @Test
        @DisplayName("applies the function to every element, in order, into a new list")
        void mapsInOrder() {
            List<String> input = Arrays.asList("a", "bb", "ccc");

            List<Integer> lengths = MethodReferencesDemo.mapAll(input, String::length);

            assertThat(lengths).containsExactly(1, 2, 3);
            assertThat(input).containsExactly("a", "bb", "ccc");
        }

        @Test
        @DisplayName("an empty list gives an empty list")
        void emptyInput() {
            assertThat(MethodReferencesDemo.mapAll(Collections.<String>emptyList(), String::length)).isEmpty();
        }
    }

    @Nested
    @DisplayName("1. Static method reference")
    class StaticReference {

        @Test
        @DisplayName("Integer::parseInt converts every string")
        void parseAll() {
            assertThat(MethodReferencesDemo.parseAll(Arrays.asList("1", "22", "-3"))).containsExactly(1, 22, -3);
        }

        @Test
        @DisplayName("a reference to a user-defined static method behaves like the lambda")
        void ownStaticMethod() {
            Function<Integer, Boolean> reference = MethodReferencesDemo::isEven;
            Function<Integer, Boolean> lambda = n -> MethodReferencesDemo.isEven(n);

            assertThat(reference.apply(4)).isTrue();
            assertThat(reference.apply(7)).isFalse();
            assertThat(reference.apply(10)).isEqualTo(lambda.apply(10));
        }

        @Test
        @DisplayName("an invalid number still fails with NumberFormatException")
        void invalidNumber() {
            assertThatThrownBy(() -> MethodReferencesDemo.parseAll(Arrays.asList("1", "x")))
                    .isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(MethodReferencesDemo::staticReferenceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Bound instance reference")
    class BoundReference {

        @Test
        @DisplayName("prefix::concat prepends the captured prefix to every name")
        void greetAll() {
            assertThat(MethodReferencesDemo.greetAll("Hello, ", Arrays.asList("Ann", "Bob")))
                    .containsExactly("Hello, Ann", "Hello, Bob");
        }

        @Test
        @DisplayName("sink::add is bound to that exact list")
        void boundToSpecificObject() {
            List<String> sink = new ArrayList<>();
            Function<String, Boolean> adder = sink::add;

            adder.apply("first");
            adder.apply("second");

            assertThat(sink).containsExactly("first", "second");
        }

        @Test
        @DisplayName("a null receiver fails immediately when the reference is created")
        void nullReceiverFailsEarly() {
            String missing = null;

            assertThatThrownBy(() -> {
                Function<String, String> function = missing::concat;
            }).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(MethodReferencesDemo::boundReferenceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Unbound instance reference")
    class UnboundReference {

        @Test
        @DisplayName("String::toUpperCase uses each element as the receiver")
        void upperAll() {
            assertThat(MethodReferencesDemo.upperAll(Arrays.asList("a", "bc"))).containsExactly("A", "BC");
        }

        @Test
        @DisplayName("String::compareToIgnoreCase works as a two-argument comparator")
        void sortIgnoringCase() {
            List<String> sorted = MethodReferencesDemo.sortIgnoringCase(Arrays.asList("b", "A", "c"));

            assertThat(sorted).containsExactly("A", "b", "c");
        }

        @Test
        @DisplayName("sortIgnoringCase does not modify its input")
        void inputUnchanged() {
            List<String> input = Arrays.asList("b", "A", "c");

            MethodReferencesDemo.sortIgnoringCase(input);

            assertThat(input).containsExactly("b", "A", "c");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(MethodReferencesDemo::unboundReferenceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. Constructor reference")
    class ConstructorReference {

        @Test
        @DisplayName("Person::new creates one person per name")
        void toPersons() {
            assertThat(MethodReferencesDemo.toPersons(Arrays.asList("Ann", "Bob")))
                    .containsExactly(new Person("Ann"), new Person("Bob"));
        }

        @Test
        @DisplayName("ArrayList::new works as a Supplier of fresh lists")
        void supplierOfLists() {
            Supplier<List<String>> factory = ArrayList::new;

            List<String> first = factory.get();
            List<String> second = factory.get();

            assertThat(first).isEmpty();
            assertThat(first).isNotSameAs(second);
        }

        @Test
        @DisplayName("int[]::new creates an array of the requested length")
        void arrayConstructor() {
            assertThat(MethodReferencesDemo.newIntArray(4)).hasSize(4);
            IntFunction<int[]> factory = int[]::new;
            assertThat(factory.apply(0)).isEmpty();
        }

        @Test
        @DisplayName("Person equality, hash code and toString")
        void personBasics() {
            assertThat(new Person("Ann")).isEqualTo(new Person("Ann"));
            assertThat(new Person("Ann").hashCode()).isEqualTo(new Person("Ann").hashCode());
            assertThat(new Person("Ann")).isNotEqualTo(new Person("Bob"));
            assertThat(new Person("Ann").toString()).isEqualTo("Person(Ann)");
            assertThat(new Person("Ann").getName()).isEqualTo("Ann");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(MethodReferencesDemo::constructorReferenceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("5. Lambda or method reference?")
    class Equivalence {

        @Test
        @DisplayName("a reference and its equivalent lambda give the same result")
        void sameResult() {
            Function<String, Integer> asReference = Integer::parseInt;
            Function<String, Integer> asLambda = s -> Integer.parseInt(s);

            assertThat(asReference.apply("7")).isEqualTo(asLambda.apply("7"));
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(MethodReferencesDemo::equivalenceDemo).doesNotThrowAnyException();
        }
    }
}

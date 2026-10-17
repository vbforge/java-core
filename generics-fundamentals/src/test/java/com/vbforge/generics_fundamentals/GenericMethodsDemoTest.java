package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.GenericMethodsDemo.Wrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GenericMethodsDemo — API contracts")
class GenericMethodsDemoTest {

    @Nested
    @DisplayName("1. Declaring a generic method")
    class Declaring {

        @Test
        @DisplayName("identity returns the very same object, whatever its type")
        void identityReturnsSameObject() {
            String text = "hello";
            Integer number = 1000;

            assertThat(GenericMethodsDemo.identity(text)).isSameAs(text);
            assertThat(GenericMethodsDemo.identity(number)).isSameAs(number);
        }

        @Test
        @DisplayName("firstOrDefault returns the first element of a non-empty list")
        void firstOrDefaultNonEmpty() {
            assertThat(GenericMethodsDemo.firstOrDefault(Arrays.asList("x", "y"), "none")).isEqualTo("x");
        }

        @Test
        @DisplayName("firstOrDefault returns the default for an empty list")
        void firstOrDefaultEmpty() {
            assertThat(GenericMethodsDemo.firstOrDefault(Collections.<String>emptyList(), "none")).isEqualTo("none");
        }

        @Test
        @DisplayName("reverseInPlace reverses odd-length arrays")
        void reverseOddLength() {
            Integer[] numbers = {1, 2, 3};

            GenericMethodsDemo.reverseInPlace(numbers);

            assertThat(numbers).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("reverseInPlace reverses even-length arrays of another type")
        void reverseEvenLength() {
            String[] words = {"a", "b", "c", "d"};

            GenericMethodsDemo.reverseInPlace(words);

            assertThat(words).containsExactly("d", "c", "b", "a");
        }

        @Test
        @DisplayName("reverseInPlace leaves an empty array alone")
        void reverseEmpty() {
            String[] empty = {};

            GenericMethodsDemo.reverseInPlace(empty);

            assertThat(empty).isEmpty();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericMethodsDemo::declaringDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Type inference and explicit type arguments")
    class Inference {

        @Test
        @DisplayName("T is inferred from the arguments")
        void inferredFromArguments() {
            List<String> result = GenericMethodsDemo.listOf("a", "b");

            assertThat(result).containsExactly("a", "b");
        }

        @Test
        @DisplayName("T is inferred from the target type when arguments are mixed")
        void inferredFromTarget() {
            List<Object> result = GenericMethodsDemo.listOf("a", 1);

            assertThat(result).containsExactly("a", 1);
        }

        @Test
        @DisplayName("an explicit type witness fixes T")
        void explicitWitness() {
            List<Number> result = GenericMethodsDemo.<Number>listOf(1, 2.5);

            assertThat(result).containsExactly(1, 2.5);
        }

        @Test
        @DisplayName("Collections.emptyList is inferred from the assignment target")
        void inferredFromAssignment() {
            List<String> empty = Collections.emptyList();

            assertThat(empty).isEmpty();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericMethodsDemo::inferenceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Generic varargs and heap pollution")
    class Varargs {

        @Test
        @DisplayName("@SafeVarargs listOf only reads the array and is safe")
        void safeVarargs() {
            assertThat(GenericMethodsDemo.listOf(1, 2, 3)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("zero varargs give an empty list")
        void noVarargs() {
            List<String> result = GenericMethodsDemo.listOf();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returning a generic varargs array as T[] causes ClassCastException at the call site")
        void leakedVarargsArray() {
            assertThatThrownBy(GenericMethodsDemo::twoStrings).isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericMethodsDemo::varargsDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. Generic methods inside generic classes")
    class InsideGenericClass {

        @Test
        @DisplayName("map changes the element type: Wrapper<String> to Wrapper<Integer>")
        void mapChangesType() {
            Wrapper<String> text = new Wrapper<>("generics");

            Wrapper<Integer> length = text.map(String::length);

            assertThat(length.get()).isEqualTo(8);
        }

        @Test
        @DisplayName("map returns a new wrapper and keeps the original")
        void mapReturnsNewWrapper() {
            Wrapper<String> text = new Wrapper<>("abc");

            Wrapper<String> upper = text.map(String::toUpperCase);

            assertThat(upper).isNotSameAs(text);
            assertThat(text.get()).isEqualTo("abc");
            assertThat(upper.get()).isEqualTo("ABC");
        }

        @Test
        @DisplayName("sameValueAs compares wrappers of different type arguments")
        void sameValueAcrossTypes() {
            assertThat(new Wrapper<>(8).sameValueAs(new Wrapper<>(8))).isTrue();
            assertThat(new Wrapper<>(8).sameValueAs(new Wrapper<>("8"))).isFalse();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericMethodsDemo::genericMethodInGenericClassDemo).doesNotThrowAnyException();
        }
    }
}

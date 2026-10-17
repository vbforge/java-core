package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.GenericClassesDemo.Box;
import com.vbforge.generics_fundamentals.GenericClassesDemo.Converter;
import com.vbforge.generics_fundamentals.GenericClassesDemo.Pair;
import com.vbforge.generics_fundamentals.GenericClassesDemo.SimpleStack;
import com.vbforge.generics_fundamentals.GenericClassesDemo.StringLength;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GenericClassesDemo — API contracts")
class GenericClassesDemoTest {

    @Nested
    @DisplayName("1. Why generics?")
    class WhyGenerics {

        @Test
        @DisplayName("raw list accepts anything, so a wrong cast fails only at runtime")
        void rawListFailsAtRuntime() {
            assertThatThrownBy(GenericClassesDemo::readSecondAsStringFromRawList)
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("typed list needs no cast when reading")
        void typedListNeedsNoCast() {
            List<String> typed = new ArrayList<>();
            typed.add("text");

            String first = typed.get(0);

            assertThat(first).isEqualTo("text");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericClassesDemo::withoutGenericsDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Generic classes: Box<T> and Pair<A, B>")
    class BoxAndPair {

        @Test
        @DisplayName("Box.of stores the value and get returns it with its static type")
        void boxStoresValue() {
            Box<String> box = Box.of("Vlad");

            assertThat(box.isPresent()).isTrue();
            assertThat(box.get()).isEqualTo("Vlad");
        }

        @Test
        @DisplayName("empty box is not present and get throws NoSuchElementException")
        void emptyBox() {
            Box<Integer> empty = Box.empty();

            assertThat(empty.isPresent()).isFalse();
            assertThatThrownBy(empty::get).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("Box.of rejects null")
        void boxRejectsNull() {
            assertThatThrownBy(() -> Box.of(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("boxes with equal content are equal and share a hash code")
        void boxEquality() {
            assertThat(Box.of("a")).isEqualTo(Box.of("a"));
            assertThat(Box.of("a").hashCode()).isEqualTo(Box.of("a").hashCode());
            assertThat(Box.of("a")).isNotEqualTo(Box.of("b"));
            assertThat(Box.<String>empty()).isEqualTo(Box.<String>empty());
        }

        @Test
        @DisplayName("Box toString shows content or 'empty'")
        void boxToString() {
            assertThat(Box.of(5).toString()).isEqualTo("Box[5]");
            assertThat(Box.empty().toString()).isEqualTo("Box[empty]");
        }

        @Test
        @DisplayName("Pair holds two values of different types")
        void pairHoldsTwoTypes() {
            Pair<String, Integer> pair = new Pair<>("age", 30);

            String first = pair.getFirst();
            Integer second = pair.getSecond();

            assertThat(first).isEqualTo("age");
            assertThat(second).isEqualTo(30);
        }

        @Test
        @DisplayName("Pair equality, hash code and toString use both values")
        void pairEqualityAndToString() {
            assertThat(new Pair<>("a", 1)).isEqualTo(new Pair<>("a", 1));
            assertThat(new Pair<>("a", 1)).isNotEqualTo(new Pair<>("a", 2));
            assertThat(new Pair<>("a", 1).hashCode()).isEqualTo(new Pair<>("a", 1).hashCode());
            assertThat(new Pair<>("a", 1).toString()).isEqualTo("(a, 1)");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericClassesDemo::boxAndPairDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. A generic container with an internal array")
    class Stack {

        @Test
        @DisplayName("pop returns elements in last-in-first-out order")
        void lifoOrder() {
            SimpleStack<String> stack = new SimpleStack<>();
            stack.push("a");
            stack.push("b");
            stack.push("c");

            assertThat(stack.pop()).isEqualTo("c");
            assertThat(stack.pop()).isEqualTo("b");
            assertThat(stack.pop()).isEqualTo("a");
        }

        @Test
        @DisplayName("stack grows beyond its initial capacity")
        void growsBeyondInitialCapacity() {
            SimpleStack<Integer> stack = new SimpleStack<>();
            for (int i = 0; i < 20; i++) {
                stack.push(i);
            }

            assertThat(stack.size()).isEqualTo(20);
            assertThat(stack.pop()).isEqualTo(19);
            assertThat(stack.size()).isEqualTo(19);
        }

        @Test
        @DisplayName("peek returns the top element without removing it")
        void peekKeepsElement() {
            SimpleStack<String> stack = new SimpleStack<>();
            stack.push("top");

            assertThat(stack.peek()).isEqualTo("top");
            assertThat(stack.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("isEmpty reflects pushes and pops")
        void isEmptyTracksSize() {
            SimpleStack<String> stack = new SimpleStack<>();
            assertThat(stack.isEmpty()).isTrue();

            stack.push("x");
            assertThat(stack.isEmpty()).isFalse();

            stack.pop();
            assertThat(stack.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("pop and peek on an empty stack throw NoSuchElementException")
        void emptyStackThrows() {
            SimpleStack<String> stack = new SimpleStack<>();

            assertThatThrownBy(stack::pop).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(stack::peek).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericClassesDemo::stackDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. Generic interfaces")
    class GenericInterfaces {

        @Test
        @DisplayName("class implementing Converter<String, Integer> returns the length")
        void classImplementation() {
            Converter<String, Integer> converter = new StringLength();

            Integer length = converter.convert("generics");

            assertThat(length).isEqualTo(8);
        }

        @Test
        @DisplayName("a lambda can implement the single-method generic interface")
        void lambdaImplementation() {
            Converter<Integer, String> toText = n -> "#" + n;

            assertThat(toText.convert(7)).isEqualTo("#7");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericClassesDemo::genericInterfaceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("5. Type erasure and raw types")
    class TypeErasure {

        @Test
        @DisplayName("List<String> and List<Integer> have the same runtime class")
        void sameClassAtRuntime() {
            assertThat(GenericClassesDemo.sameRuntimeClass()).isTrue();
        }

        @Test
        @DisplayName("a String list polluted through a raw type fails with ClassCastException on read")
        void heapPollutionFailsOnRead() {
            assertThatThrownBy(GenericClassesDemo::readFromPollutedList)
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(GenericClassesDemo::typeErasureDemo).doesNotThrowAnyException();
        }
    }
}

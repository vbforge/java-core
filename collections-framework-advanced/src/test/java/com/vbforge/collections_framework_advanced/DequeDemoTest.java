package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DequeDemo — API contracts")
class DequeDemoTest {

    @Nested
    @DisplayName("1. Working at both ends")
    class BothEndsTest {

        @Test
        @DisplayName("addFirst/addLast build the order, pollFirst/pollLast take from the ends")
        void bothEnds() {
            assertThat(DequeDemo.bothEndsDemo()).containsExactly("a", "c", "[b]");
        }
    }

    @Nested
    @DisplayName("2. Deque as a stack (LIFO)")
    class StackTest {

        @Test
        @DisplayName("the last element pushed is the first one popped")
        void lastInFirstOut() {
            assertThat(DequeDemo.stackDemo(Arrays.asList(1, 2, 3))).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("an empty input gives an empty result")
        void emptyInput() {
            assertThat(DequeDemo.stackDemo(Collections.<Integer>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("a single element comes back unchanged")
        void singleElement() {
            assertThat(DequeDemo.stackDemo(Collections.singletonList(42))).containsExactly(42);
        }
    }

    @Nested
    @DisplayName("3. Iteration order: Deque vs legacy Stack")
    class IterationOrderTest {

        @Test
        @DisplayName("ArrayDeque used as a stack iterates from the top — reverse push order")
        void dequeIteratesFromTop() {
            assertThat(DequeDemo.dequeStackIterationDemo(Arrays.asList(1, 2, 3))).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("legacy Stack iterates from the bottom — push order")
        void legacyStackIteratesFromBottom() {
            assertThat(DequeDemo.legacyStackIterationDemo(Arrays.asList(1, 2, 3))).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("the two disagree for the same input")
        void theyDisagree() {
            assertThat(DequeDemo.dequeStackIterationDemo(Arrays.asList(1, 2, 3)))
                    .isNotEqualTo(DequeDemo.legacyStackIterationDemo(Arrays.asList(1, 2, 3)));
        }
    }

    @Nested
    @DisplayName("4. Empty deque behaviour")
    class EmptyDequeTest {

        @Test
        @DisplayName("peek() on an empty deque returns null")
        void peekReturnsNull() {
            assertThat(DequeDemo.peekEmptyDemo()).isNull();
        }

        @Test
        @DisplayName("pop() on an empty deque throws NoSuchElementException")
        void popThrows() {
            assertThatThrownBy(DequeDemo::popEmptyDemo).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("5. descendingIterator and removeFirstOccurrence")
    class DescendingAndRemoveTest {

        @Test
        @DisplayName("descendingIterator visits from tail to head")
        void descending() {
            assertThat(DequeDemo.descendingDemo(Arrays.asList(1, 2, 3))).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("removeFirstOccurrence removes only the first match")
        void removesOnlyFirst() {
            assertThat(DequeDemo.removeFirstOccurrenceDemo(Arrays.asList("a", "b", "a"), "a"))
                    .containsExactly("b", "a");
        }

        @Test
        @DisplayName("a missing element leaves the deque unchanged")
        void missingElement() {
            assertThat(DequeDemo.removeFirstOccurrenceDemo(Arrays.asList("a", "b"), "z"))
                    .containsExactly("a", "b");
        }
    }

    @Nested
    @DisplayName("6. Both ends at once: palindrome check")
    class PalindromeTest {

        @ParameterizedTest(name = "\"{0}\" is a palindrome")
        @ValueSource(strings = {"racecar", "A man, a plan, a canal: Panama", "", "x", "12321", "Noon"})
        @DisplayName("palindromes (letters and digits only, case ignored)")
        void palindromes(String text) {
            assertThat(DequeDemo.isPalindromeDemo(text)).isTrue();
        }

        @ParameterizedTest(name = "\"{0}\" is not a palindrome")
        @ValueSource(strings = {"deque", "ab", "12345"})
        @DisplayName("non-palindromes")
        void nonPalindromes(String text) {
            assertThat(DequeDemo.isPalindromeDemo(text)).isFalse();
        }
    }

    @Nested
    @DisplayName("7. Null elements")
    class NullTest {

        @Test
        @DisplayName("ArrayDeque rejects null with NullPointerException")
        void rejectsNull() {
            assertThatThrownBy(DequeDemo::pushNullDemo).isInstanceOf(NullPointerException.class);
        }
    }
}

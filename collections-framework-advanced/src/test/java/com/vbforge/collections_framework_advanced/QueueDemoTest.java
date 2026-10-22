package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QueueDemo — API contracts")
class QueueDemoTest {

    @Nested
    @DisplayName("1. FIFO order")
    class FifoTest {

        @Test
        @DisplayName("elements leave in the order they entered")
        void firstInFirstOut() {
            assertThat(QueueDemo.fifoDemo(Arrays.asList("first", "second", "third")))
                    .containsExactly("first", "second", "third");
        }

        @Test
        @DisplayName("an empty input gives an empty result")
        void emptyInput() {
            assertThat(QueueDemo.fifoDemo(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("duplicates are kept — a queue is not a set")
        void duplicatesKept() {
            assertThat(QueueDemo.fifoDemo(Arrays.asList("a", "a", "b"))).containsExactly("a", "a", "b");
        }
    }

    @Nested
    @DisplayName("2. Two method families: special value vs exception")
    class MethodFamiliesTest {

        @Test
        @DisplayName("peek() returns the head and does NOT remove it")
        void peekKeepsElement() {
            Queue<String> queue = new ArrayDeque<>(Arrays.asList("a", "b"));

            assertThat(QueueDemo.peekDemo(queue)).isEqualTo("a");
            assertThat(queue).hasSize(2);
        }

        @Test
        @DisplayName("poll() returns the head and removes it")
        void pollRemovesElement() {
            Queue<String> queue = new ArrayDeque<>(Arrays.asList("a", "b"));

            assertThat(QueueDemo.pollDemo(queue)).isEqualTo("a");
            assertThat(queue).containsExactly("b");
        }

        @Test
        @DisplayName("peek() and poll() return null on an empty queue")
        void emptyReturnsNull() {
            assertThat(QueueDemo.peekDemo(new ArrayDeque<String>())).isNull();
            assertThat(QueueDemo.pollDemo(new ArrayDeque<String>())).isNull();
        }

        @Test
        @DisplayName("element() and remove() return the head of a non-empty queue")
        void throwingFamilyWorks() {
            Queue<String> queue = new ArrayDeque<>(Arrays.asList("x", "y"));

            assertThat(QueueDemo.elementDemo(queue)).isEqualTo("x");
            assertThat(QueueDemo.removeDemo(queue)).isEqualTo("x");
            assertThat(queue).containsExactly("y");
        }

        @Test
        @DisplayName("element() on an empty queue throws NoSuchElementException")
        void elementThrowsOnEmpty() {
            assertThatThrownBy(() -> QueueDemo.elementDemo(new ArrayDeque<String>()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("remove() on an empty queue throws NoSuchElementException")
        void removeThrowsOnEmpty() {
            assertThatThrownBy(() -> QueueDemo.removeDemo(new ArrayDeque<String>()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("3. Bounded queues: offer vs add")
    class BoundedQueueTest {

        @Test
        @DisplayName("offer() on a full bounded queue returns false")
        void offerReturnsFalse() {
            assertThat(QueueDemo.offerToFullQueueDemo()).isFalse();
        }

        @Test
        @DisplayName("add() on a full bounded queue throws IllegalStateException")
        void addThrows() {
            assertThatThrownBy(QueueDemo::addToFullQueueDemo)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Queue full");
        }
    }

    @Nested
    @DisplayName("4. Null elements")
    class NullElementTest {

        @Test
        @DisplayName("ArrayDeque rejects null with NullPointerException")
        void arrayDequeRejectsNull() {
            assertThatThrownBy(() -> QueueDemo.offerNullDemo(new ArrayDeque<String>()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("LinkedList accepts null")
        void linkedListAcceptsNull() {
            Queue<String> queue = new LinkedList<>();

            assertThat(QueueDemo.offerNullDemo(queue)).isTrue();
            assertThat(queue).hasSize(1);
        }

        @Test
        @DisplayName("with a null inside, peek() == null no longer means the queue is empty")
        void ambiguousNull() {
            Queue<String> queue = new LinkedList<>();
            QueueDemo.offerNullDemo(queue);

            assertThat(queue.peek()).isNull();
            assertThat(queue.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("5. Round robin")
    class RoundRobinTest {

        @Test
        @DisplayName("everybody gets turns in the same repeating order")
        void repeatingOrder() {
            assertThat(QueueDemo.roundRobinDemo(Arrays.asList("A", "B", "C"), 7))
                    .containsExactly("A", "B", "C", "A", "B", "C", "A");
        }

        @Test
        @DisplayName("zero turns give an empty order")
        void zeroTurns() {
            assertThat(QueueDemo.roundRobinDemo(Arrays.asList("A", "B"), 0)).isEmpty();
        }

        @Test
        @DisplayName("a single participant keeps getting the turn")
        void singleParticipant() {
            assertThat(QueueDemo.roundRobinDemo(Collections.singletonList("solo"), 3))
                    .containsExactly("solo", "solo", "solo");
        }

        @Test
        @DisplayName("nobody to serve ends the loop early")
        void nobodyToServe() {
            assertThat(QueueDemo.roundRobinDemo(Collections.<String>emptyList(), 5)).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. Iterating vs draining")
    class IterateVsDrainTest {

        @Test
        @DisplayName("iterating goes from head to tail")
        void iterationOrder() {
            Queue<Integer> queue = new ArrayDeque<>(Arrays.asList(1, 2, 3));

            assertThat(QueueDemo.iterateDemo(queue)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("iterating does not consume the queue")
        void iterationKeepsElements() {
            Queue<Integer> queue = new ArrayDeque<>(Arrays.asList(1, 2, 3));

            QueueDemo.iterateDemo(queue);

            assertThat(queue).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("draining returns the same order and leaves the queue empty")
        void drainingEmptiesQueue() {
            Queue<Integer> queue = new ArrayDeque<>(Arrays.asList(1, 2, 3));

            assertThat(QueueDemo.drainDemo(queue)).containsExactly(1, 2, 3);
            assertThat(queue).isEmpty();
        }

        @Test
        @DisplayName("draining an empty queue gives an empty list")
        void drainEmpty() {
            assertThat(QueueDemo.drainDemo(new ArrayDeque<Integer>())).isEmpty();
        }
    }
}

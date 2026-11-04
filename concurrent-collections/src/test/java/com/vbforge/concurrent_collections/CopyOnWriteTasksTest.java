package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CopyOnWriteTasks — your implementations")
class CopyOnWriteTasksTest {

    private CopyOnWriteTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CopyOnWriteTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement iterateWhileAppending()")
    @DisplayName("iterateWhileAppending")
    class IterateWhileAppendingTest {

        @Test
        @DisplayName("visits the original elements and appends the suffixed copies (Javadoc example)")
        void example() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b"));

            List<String> visited = tasks.iterateWhileAppending(list, "!");

            assertThat(visited).containsExactly("a", "b");
            assertThat(list).containsExactly("a", "b", "a!", "b!");
        }

        @Test
        @DisplayName("an empty list stays empty")
        void emptyList() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

            assertThat(tasks.iterateWhileAppending(list, "!")).isEmpty();
            assertThat(list).isEmpty();
        }

        @Test
        @DisplayName("a single element")
        void singleElement() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Collections.singletonList("x"));

            assertThat(tasks.iterateWhileAppending(list, "-copy")).containsExactly("x");
            assertThat(list).containsExactly("x", "x-copy");
        }

        @Test
        @DisplayName("an empty suffix appends exact duplicates, and the loop still ends")
        void emptySuffix() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b"));

            assertThat(tasks.iterateWhileAppending(list, "")).containsExactly("a", "b");
            assertThat(list).containsExactly("a", "b", "a", "b");
        }

        @Test
        @DisplayName("returns a separate list, not the one it was given")
        void returnsNewList() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a"));

            assertThat(tasks.iterateWhileAppending(list, "!")).isNotSameAs(list);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement addUnique()")
    @DisplayName("addUnique")
    @Timeout(10)
    class AddUniqueTest {

        @Test
        @DisplayName("adds only the missing candidates and counts them (Javadoc example)")
        void example() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a"));

            int added = tasks.addUnique(list, Arrays.asList("a", "b", "b", "c"));

            assertThat(added).isEqualTo(2);
            assertThat(list).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("returns 0 and changes nothing when every candidate is present")
        void nothingNew() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b"));

            assertThat(tasks.addUnique(list, Arrays.asList("a", "b"))).isEqualTo(0);
            assertThat(list).containsExactly("a", "b");
        }

        @Test
        @DisplayName("no candidates")
        void noCandidates() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a"));

            assertThat(tasks.addUnique(list, Collections.<String>emptyList())).isEqualTo(0);
            assertThat(list).containsExactly("a");
        }

        @Test
        @DisplayName("duplicates among the candidates count once, even on an empty list")
        void duplicatesAmongCandidates() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

            assertThat(tasks.addUnique(list, Arrays.asList("x", "x", "x"))).isEqualTo(1);
            assertThat(list).containsExactly("x");
        }

        @Test
        @DisplayName("8 threads adding the same candidates: each element is added exactly once overall")
        void concurrentCalls() throws InterruptedException {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
            List<String> candidates = Arrays.asList("a", "b", "c", "d");
            AtomicInteger totalAdded = new AtomicInteger();

            runInParallel(8, i -> totalAdded.addAndGet(tasks.addUnique(list, candidates)));

            assertThat(totalAdded.get()).isEqualTo(4);
            assertThat(list).hasSize(4);
            assertThat(list).containsExactlyInAnyOrder("a", "b", "c", "d");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement notifyListeners()")
    @DisplayName("notifyListeners")
    class NotifyListenersTest {

        @Test
        @DisplayName("a listener that unregisters itself is still counted (Javadoc example)")
        void listenerRemovesItself() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
            List<String> log = new ArrayList<>();
            Consumer<String> middle = new Consumer<String>() {
                @Override
                public void accept(String event) {
                    log.add("middle:" + event);
                    listeners.remove(this);
                }
            };
            listeners.add(event -> log.add("first:" + event));
            listeners.add(middle);
            listeners.add(event -> log.add("last:" + event));

            int called = tasks.notifyListeners(listeners, "x");

            assertThat(called).isEqualTo(3);
            assertThat(log).containsExactly("first:x", "middle:x", "last:x");
            assertThat(listeners).hasSize(2);
            assertThat(listeners.contains(middle)).isFalse();
        }

        @Test
        @DisplayName("a listener registered during the notification is not called for this event")
        void listenerAddedDuringNotification() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
            List<String> log = new ArrayList<>();
            listeners.add(event -> {
                log.add("first:" + event);
                listeners.add(late -> log.add("late:" + late));
            });

            int called = tasks.notifyListeners(listeners, "x");

            assertThat(called).isEqualTo(1);
            assertThat(log).containsExactly("first:x");
            assertThat(listeners).hasSize(2);
        }

        @Test
        @DisplayName("no listeners")
        void noListeners() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

            assertThat(tasks.notifyListeners(listeners, "x")).isEqualTo(0);
        }

        @Test
        @DisplayName("every listener receives the event, in registration order")
        void everyListenerReceivesTheEvent() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
            List<String> log = new ArrayList<>();
            listeners.add(event -> log.add("A:" + event));
            listeners.add(event -> log.add("B:" + event));

            int called = tasks.notifyListeners(listeners, "ping");

            assertThat(called).isEqualTo(2);
            assertThat(log).containsExactly("A:ping", "B:ping");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement uniqueInOrder()")
    @DisplayName("uniqueInOrder")
    class UniqueInOrderTest {

        @Test
        @DisplayName("keeps the first occurrence of each value, in order (Javadoc example)")
        void example() {
            CopyOnWriteArraySet<Integer> result = tasks.uniqueInOrder(new int[]{3, 1, 3, 2, 1});

            assertThat(result).containsExactly(3, 1, 2);
        }

        @Test
        @DisplayName("all values equal")
        void allEqual() {
            assertThat(tasks.uniqueInOrder(new int[]{7, 7, 7})).containsExactly(7);
        }

        @Test
        @DisplayName("empty input")
        void emptyInput() {
            assertThat(tasks.uniqueInOrder(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("values that are already unique keep their order")
        void alreadyUnique() {
            assertThat(tasks.uniqueInOrder(new int[]{5, 4, 3, 2, 1})).containsExactly(5, 4, 3, 2, 1);
        }

        @Test
        @DisplayName("zero and negative values")
        void zeroAndNegative() {
            assertThat(tasks.uniqueInOrder(new int[]{0, -1, 0, -1, 2})).containsExactly(0, -1, 2);
        }
    }

    // -------------------------------------------------------------------------
    // Helper: start N threads together, wait for all of them
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface Body {
        void run(int threadIndex) throws Exception;
    }

    private static void runInParallel(int threads, Body body) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    body.run(index);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                }
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        if (failure.get() != null) {
            throw new AssertionError("a worker thread failed: " + failure.get(), failure.get());
        }
    }
}

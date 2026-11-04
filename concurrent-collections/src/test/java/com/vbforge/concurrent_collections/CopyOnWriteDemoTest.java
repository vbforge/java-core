package com.vbforge.concurrent_collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CopyOnWriteDemo — API contracts")
class CopyOnWriteDemoTest {

    @Nested
    @DisplayName("1. Snapshot iteration")
    class SnapshotIteration {

        @Test
        @DisplayName("ArrayList throws ConcurrentModificationException when added to while iterating")
        void arrayListIsFailFast() {
            List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

            assertThatThrownBy(() -> {
                for (String s : list) {
                    list.add(s + "'");
                }
            }).isInstanceOf(ConcurrentModificationException.class);
        }

        @Test
        @DisplayName("CopyOnWriteArrayList visits only the elements present when the loop started")
        void visitsOnlyTheSnapshot() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));
            List<String> visited = new ArrayList<>();

            for (String s : list) {
                list.add(s + "'");
                visited.add(s);
            }

            assertThat(visited).containsExactly("a", "b", "c");
            assertThat(list).containsExactly("a", "b", "c", "a'", "b'", "c'");
        }

        @Test
        @DisplayName("an iterator taken before clear() still sees the old contents")
        void iteratorSurvivesClear() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));
            Iterator<String> oldSnapshot = list.iterator();

            list.clear();

            List<String> seen = new ArrayList<>();
            while (oldSnapshot.hasNext()) {
                seen.add(oldSnapshot.next());
            }
            assertThat(seen).containsExactly("a", "b", "c");
            assertThat(list).isEmpty();
        }

        @Test
        @DisplayName("an iterator taken before set() still sees the old element")
        void iteratorSurvivesSet() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a", "b"));
            Iterator<String> oldSnapshot = list.iterator();

            list.set(0, "z");

            assertThat(oldSnapshot.next()).isEqualTo("a");
            assertThat(list.get(0)).isEqualTo("z");
        }

        @Test
        @DisplayName("a new iterator sees the latest contents")
        void newIteratorSeesLatest() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(Arrays.asList("a"));
            list.add("b");

            Iterator<String> fresh = list.iterator();

            assertThat(fresh.next()).isEqualTo("a");
            assertThat(fresh.next()).isEqualTo("b");
            assertThat(fresh.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("2. The iterator is read-only")
    class ReadOnlyIterator {

        @Test
        @DisplayName("Iterator.remove() throws UnsupportedOperationException")
        void iteratorRemoveUnsupported() {
            CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(Arrays.asList(1, 2, 3, 4));
            Iterator<Integer> it = numbers.iterator();
            it.next();

            assertThatThrownBy(it::remove).isInstanceOf(UnsupportedOperationException.class);
            assertThat(numbers).hasSize(4);
        }

        @Test
        @DisplayName("removeIf removes the matching elements and reports a change")
        void removeIf() {
            CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(Arrays.asList(1, 2, 3, 4));

            assertThat(numbers.removeIf(n -> n % 2 == 0)).isTrue();
            assertThat(numbers).containsExactly(1, 3);
        }

        @Test
        @DisplayName("removeIf reports false when nothing matched")
        void removeIfNothingMatched() {
            CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(Arrays.asList(1, 3));

            assertThat(numbers.removeIf(n -> n % 2 == 0)).isFalse();
            assertThat(numbers).containsExactly(1, 3);
        }
    }

    @Nested
    @DisplayName("3. addIfAbsent / addAllAbsent")
    class AddIfAbsent {

        @Test
        @DisplayName("addIfAbsent adds once and reports whether it added")
        void addIfAbsent() {
            CopyOnWriteArrayList<String> tags = new CopyOnWriteArrayList<>();

            assertThat(tags.addIfAbsent("java")).isTrue();
            assertThat(tags.addIfAbsent("java")).isFalse();
            assertThat(tags).containsExactly("java");
        }

        @Test
        @DisplayName("addAllAbsent returns how many were added and de-duplicates the candidates")
        void addAllAbsent() {
            CopyOnWriteArrayList<String> tags = new CopyOnWriteArrayList<>(Arrays.asList("java"));

            int added = tags.addAllAbsent(Arrays.asList("java", "maven", "maven", "junit"));

            assertThat(added).isEqualTo(2);
            assertThat(tags).containsExactly("java", "maven", "junit");
        }

        @Test
        @DisplayName("addAllAbsent returns 0 when every candidate is already present")
        void addAllAbsentNothingNew() {
            CopyOnWriteArrayList<String> tags = new CopyOnWriteArrayList<>(Arrays.asList("a", "b"));

            assertThat(tags.addAllAbsent(Arrays.asList("a", "b"))).isEqualTo(0);
            assertThat(tags).containsExactly("a", "b");
        }

        @Test
        @DisplayName("a plain add() happily creates a duplicate")
        void plainAddAllowsDuplicates() {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

            list.add("x");
            list.add("x");

            assertThat(list).containsExactly("x", "x");
        }
    }

    @Nested
    @DisplayName("4. Check-then-act race")
    @Timeout(10)
    class CheckThenAct {

        @Test
        @DisplayName("contains + add lets two threads both add the same element")
        void containsThenAddCreatesDuplicate() throws InterruptedException {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
            CyclicBarrier bothHaveChecked = new CyclicBarrier(2);

            runInParallel(2, i -> {
                if (!list.contains("x")) {
                    bothHaveChecked.await();
                    list.add("x");
                }
            });

            assertThat(list).containsExactly("x", "x");
        }

        @Test
        @DisplayName("addIfAbsent from 8 racing threads adds the element once")
        void addIfAbsentIsAtomic() throws InterruptedException {
            CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

            runInParallel(8, i -> list.addIfAbsent("x"));

            assertThat(list).containsExactly("x");
        }
    }

    @Nested
    @DisplayName("5. CopyOnWriteArraySet")
    class ArraySet {

        @Test
        @DisplayName("add returns false for a duplicate")
        void addReturnValue() {
            Set<String> handlers = new CopyOnWriteArraySet<>();

            assertThat(handlers.add("logger")).isTrue();
            assertThat(handlers.add("metrics")).isTrue();
            assertThat(handlers.add("logger")).isFalse();
        }

        @Test
        @DisplayName("iterates in insertion order and ignores duplicates")
        void insertionOrder() {
            Set<String> handlers = new CopyOnWriteArraySet<>();
            handlers.add("logger");
            handlers.add("metrics");
            handlers.add("logger");

            assertThat(handlers).containsExactly("logger", "metrics");
        }

        @Test
        @DisplayName("can be modified while being iterated")
        void snapshotIteration() {
            Set<Integer> numbers = new CopyOnWriteArraySet<>(Arrays.asList(1, 2, 3));
            List<Integer> visited = new ArrayList<>();

            for (Integer n : numbers) {
                numbers.add(n + 10);
                visited.add(n);
            }

            assertThat(visited).containsExactly(1, 2, 3);
            assertThat(numbers).containsExactly(1, 2, 3, 11, 12, 13);
        }
    }

    @Nested
    @DisplayName("6. Listener list")
    class ListenerList {

        @Test
        @DisplayName("a listener can unregister itself while being notified")
        void listenerRemovesItself() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
            List<String> log = new ArrayList<>();
            Consumer<String> oneShot = new Consumer<String>() {
                @Override
                public void accept(String event) {
                    log.add("oneShot got " + event);
                    listeners.remove(this);
                }
            };
            listeners.add(event -> log.add("logger got " + event));
            listeners.add(oneShot);

            for (Consumer<String> listener : listeners) {
                listener.accept("event-1");
            }
            for (Consumer<String> listener : listeners) {
                listener.accept("event-2");
            }

            assertThat(log).containsExactly("logger got event-1", "oneShot got event-1", "logger got event-2");
            assertThat(listeners).hasSize(1);
        }

        @Test
        @DisplayName("a listener removed during a round is still called later in that same round")
        void removedListenerStillCalledThisRound() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
            List<String> log = new ArrayList<>();
            Consumer<String> second = event -> log.add("second");
            listeners.add(event -> {
                log.add("first");
                listeners.remove(second);
            });
            listeners.add(second);

            for (Consumer<String> listener : listeners) {
                listener.accept("round-1");
            }
            assertThat(log).containsExactly("first", "second");

            log.clear();
            for (Consumer<String> listener : listeners) {
                listener.accept("round-2");
            }
            assertThat(log).containsExactly("first");
        }

        @Test
        @DisplayName("a listener added during a round is only called from the next round")
        void addedListenerCalledNextRound() {
            CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
            List<String> log = new ArrayList<>();
            listeners.add(event -> {
                log.add("first");
                listeners.add(late -> log.add("added"));
            });

            for (Consumer<String> listener : listeners) {
                listener.accept("round-1");
            }
            assertThat(log).containsExactly("first");

            for (Consumer<String> listener : listeners) {
                listener.accept("round-2");
            }
            assertThat(log).containsExactly("first", "first", "added");
        }
    }

    @Nested
    @DisplayName("7. main() runs every section")
    @Timeout(10)
    class MainMethod {

        @Test
        @DisplayName("prints six sections separated by ---")
        void mainPrintsAllSections() throws InterruptedException {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured, true));
            try {
                CopyOnWriteDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }

            String[] sections = captured.toString().split("(?m)^---$");
            assertThat(sections).hasSize(6);
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

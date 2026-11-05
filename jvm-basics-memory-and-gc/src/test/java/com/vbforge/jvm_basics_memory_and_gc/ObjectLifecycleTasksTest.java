package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ObjectLifecycleTasks — your implementations")
class ObjectLifecycleTasksTest {

    private ObjectLifecycleTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ObjectLifecycleTasks();
    }

    /** Builds a heap from entries like {"A", "B,C"} (empty string = no references). */
    private static Map<String, List<String>> heap(String... entries) {
        Map<String, List<String>> graph = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            List<String> refs = new ArrayList<>();
            if (!entries[i + 1].isEmpty()) {
                refs.addAll(Arrays.asList(entries[i + 1].split(",")));
            }
            graph.put(entries[i], refs);
        }
        return graph;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement findGarbage()")
    @DisplayName("findGarbage")
    class FindGarbageTest {

        @Test
        @DisplayName("cycle and object pointing into live data are garbage")
        void example() {
            Map<String, List<String>> graph = heap("A", "B", "B", "C", "C", "", "D", "E", "E", "D", "F", "A");
            assertThat(tasks.findGarbage(graph, Collections.singleton("A"))).containsExactlyInAnyOrder("D", "E", "F");
        }

        @Test
        @DisplayName("no roots → everything is garbage")
        void noRoots() {
            Map<String, List<String>> graph = heap("A", "B", "B", "");
            assertThat(tasks.findGarbage(graph, Collections.<String>emptySet())).containsExactlyInAnyOrder("A", "B");
        }

        @Test
        @DisplayName("everything reachable → no garbage")
        void allLive() {
            Map<String, List<String>> graph = heap("A", "B", "B", "");
            assertThat(tasks.findGarbage(graph, Collections.singleton("A"))).isEmpty();
        }

        @Test
        @DisplayName("a live cycle does not loop forever and is not garbage")
        void liveCycle() {
            Map<String, List<String>> graph = heap("A", "B", "B", "A", "X", "");
            assertThat(tasks.findGarbage(graph, Collections.singleton("A"))).containsExactly("X");
        }

        @Test
        @DisplayName("roots and references that are not in the heap are ignored")
        void unknownNames() {
            Map<String, List<String>> graph = heap("A", "Z", "B", "");
            assertThat(tasks.findGarbage(graph, new HashSet<>(Arrays.asList("A", "Q")))).containsExactly("B");
        }

        @Test
        @DisplayName("the graph is not modified")
        void graphUntouched() {
            Map<String, List<String>> graph = heap("A", "", "B", "");
            tasks.findGarbage(graph, Collections.singleton("A"));
            assertThat(graph.keySet()).containsExactly("A", "B");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement retainedSet()")
    @DisplayName("retainedSet")
    class RetainedSetTest {

        private final Map<String, List<String>> diamond =
                heap("A", "B,C", "B", "D", "C", "D,E", "D", "", "E", "");

        @Test
        @DisplayName("removing B frees only B — D is still held by C")
        void sharedChild() {
            assertThat(tasks.retainedSet(diamond, Collections.singleton("A"), "B")).containsExactly("B");
        }

        @Test
        @DisplayName("removing C frees C and E — D is still held by B")
        void partialRelease() {
            assertThat(tasks.retainedSet(diamond, Collections.singleton("A"), "C"))
                    .containsExactlyInAnyOrder("C", "E");
        }

        @Test
        @DisplayName("removing the root frees everything")
        void root() {
            assertThat(tasks.retainedSet(diamond, Collections.singleton("A"), "A"))
                    .containsExactlyInAnyOrder("A", "B", "C", "D", "E");
        }

        @Test
        @DisplayName("an object that is not in the heap retains nothing")
        void unknown() {
            assertThat(tasks.retainedSet(diamond, Collections.singleton("A"), "Z")).isEmpty();
        }

        @Test
        @DisplayName("an unreachable object retains nothing")
        void unreachable() {
            Map<String, List<String>> graph = heap("A", "", "G", "");
            assertThat(tasks.retainedSet(graph, Collections.singleton("A"), "G")).isEmpty();
        }

        @Test
        @DisplayName("the graph passed in is not modified")
        void graphUntouched() {
            tasks.retainedSet(diamond, Collections.singleton("A"), "B");
            assertThat(diamond.keySet()).containsExactly("A", "B", "C", "D", "E");
            assertThat(diamond.get("A")).containsExactly("B", "C");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lifecycle()")
    @DisplayName("lifecycle")
    class LifecycleTest {

        @Test
        @DisplayName("lifecycle(3, 2) → [EDEN, SURVIVOR, OLD, OLD]")
        void example() {
            assertThat(tasks.lifecycle(3, 2)).containsExactly("EDEN", "SURVIVOR", "OLD", "OLD");
        }

        @Test
        @DisplayName("lifecycle(0, 5) → [EDEN]")
        void zeroCollections() {
            assertThat(tasks.lifecycle(0, 5)).containsExactly("EDEN");
        }

        @Test
        @DisplayName("lifecycle(4, 1) → promoted on the first collection")
        void thresholdOne() {
            assertThat(tasks.lifecycle(4, 1)).containsExactly("EDEN", "OLD", "OLD", "OLD", "OLD");
        }

        @Test
        @DisplayName("lifecycle(3, 4) → never promoted")
        void neverPromoted() {
            assertThat(tasks.lifecycle(3, 4)).containsExactly("EDEN", "SURVIVOR", "SURVIVOR", "SURVIVOR");
        }

        @ParameterizedTest(name = "lifecycle({0}, {1}) has {2} entries")
        @CsvSource({"0, 1, 1", "5, 3, 6", "10, 15, 11"})
        @DisplayName("the list has collections + 1 entries")
        void size(int collections, int threshold, int expectedSize) {
            assertThat(tasks.lifecycle(collections, threshold)).hasSize(expectedSize);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement humongousRegionsNeeded()")
    @DisplayName("humongousRegionsNeeded")
    class HumongousRegionsNeededTest {

        @ParameterizedTest(name = "object={0} region={1} → {2}")
        @CsvSource({
                "400000, 1048576, 0",
                "524287, 1048576, 0",
                "524288, 1048576, 1",
                "1048576, 1048576, 1",
                "1048577, 1048576, 2",
                "3000000, 1048576, 3",
                "8388608, 4194304, 2"
        })
        @DisplayName("ceil(object / region) for humongous objects, 0 otherwise")
        void table(long objectBytes, long regionBytes, long expected) {
            assertThat(tasks.humongousRegionsNeeded(objectBytes, regionBytes)).isEqualTo(expected);
        }
    }
}

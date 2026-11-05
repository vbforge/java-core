package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ObjectLifecycleDemo — API contracts")
class ObjectLifecycleDemoTest {

    @Nested
    @DisplayName("1. Reachability from GC roots")
    class Reachability {

        @Test
        @DisplayName("everything on a path from the root is live")
        void livePath() {
            Set<String> live = ObjectLifecycleDemo.reachable(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("A"));
            assertThat(live).containsExactlyInAnyOrder("A", "B", "C");
        }

        @Test
        @DisplayName("an isolated cycle is unreachable — the JVM does not count references")
        void cycleIsGarbage() {
            Set<String> live = ObjectLifecycleDemo.reachable(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("A"));
            assertThat(live).doesNotContain("D", "E");
        }

        @Test
        @DisplayName("a reference from garbage does not keep anything alive, and does not make garbage live")
        void referenceFromGarbage() {
            Set<String> live = ObjectLifecycleDemo.reachable(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("A"));
            assertThat(live).doesNotContain("F");
        }

        @Test
        @DisplayName("with no roots nothing is reachable")
        void noRoots() {
            assertThat(ObjectLifecycleDemo.reachable(
                    ObjectLifecycleDemo.sampleGraph(), Collections.<String>emptySet())).isEmpty();
        }

        @Test
        @DisplayName("a cycle reachable from a root terminates and keeps both members")
        void cycleFromRoot() {
            Set<String> live = ObjectLifecycleDemo.reachable(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("D"));
            assertThat(live).containsExactlyInAnyOrder("D", "E");
        }

        @Test
        @DisplayName("unknown roots are ignored")
        void unknownRoot() {
            assertThat(ObjectLifecycleDemo.reachable(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("Z"))).isEmpty();
        }
    }

    @Nested
    @DisplayName("2. Mark-and-sweep")
    class MarkAndSweep {

        @Test
        @DisplayName("sweep keeps only the marked objects")
        void keepsLive() {
            Map<String, List<String>> after = ObjectLifecycleDemo.markAndSweep(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("A"));
            assertThat(after.keySet()).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("sweep returns a new heap and leaves the original intact")
        void originalUntouched() {
            Map<String, List<String>> before = ObjectLifecycleDemo.sampleGraph();
            Map<String, List<String>> after = ObjectLifecycleDemo.markAndSweep(
                    before, Collections.singleton("A"));
            assertThat(after).isNotSameAs(before);
            assertThat(before.keySet()).containsExactly("A", "B", "C", "D", "E", "F");
        }

        @Test
        @DisplayName("surviving objects keep their references")
        void referencesPreserved() {
            Map<String, List<String>> after = ObjectLifecycleDemo.markAndSweep(
                    ObjectLifecycleDemo.sampleGraph(), Collections.singleton("A"));
            assertThat(after.get("A")).containsExactly("B");
            assertThat(after.get("B")).containsExactly("C");
        }
    }

    @Nested
    @DisplayName("3. Generations and tenuring")
    class Generations {

        @ParameterizedTest(name = "survived {0} with threshold {1} -> {2}")
        @CsvSource({
                "0, 3, EDEN",
                "1, 3, SURVIVOR",
                "2, 3, SURVIVOR",
                "3, 3, OLD",
                "9, 3, OLD",
                "1, 1, OLD"
        })
        @DisplayName("generationFor maps survival count to a generation")
        void mapping(int survived, int threshold, String expected) {
            assertThat(ObjectLifecycleDemo.generationFor(survived, threshold)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("4. Humongous objects")
    class Humongous {

        @Test
        @DisplayName("below half a region is not humongous")
        void small() {
            assertThat(ObjectLifecycleDemo.isHumongous(524_287, 1_048_576)).isFalse();
        }

        @Test
        @DisplayName("exactly half a region IS humongous (>=, not >)")
        void boundary() {
            assertThat(ObjectLifecycleDemo.isHumongous(524_288, 1_048_576)).isTrue();
        }

        @Test
        @DisplayName("larger than a region is humongous")
        void large() {
            assertThat(ObjectLifecycleDemo.isHumongous(2_000_000, 1_048_576)).isTrue();
        }
    }

    @Test
    @DisplayName("sample graph has the documented six objects")
    void sampleGraphShape() {
        assertThat(ObjectLifecycleDemo.sampleGraph().keySet())
                .isEqualTo(new java.util.LinkedHashSet<>(Arrays.asList("A", "B", "C", "D", "E", "F")));
    }
}

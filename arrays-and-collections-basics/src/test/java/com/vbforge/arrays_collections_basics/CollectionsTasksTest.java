package com.vbforge.arrays_collections_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CollectionsTasksTest — tests for every method in {@link CollectionsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code CollectionsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=CollectionsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 7 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("CollectionsTasks — your implementations")
class CollectionsTasksTest {

    private CollectionsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CollectionsTasks();
    }

    // ── Task 1: rotateRight ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement rotateRight()")
    @DisplayName("Task 1 — rotateRight()")
    class RotateRightTest {

        @Test
        @DisplayName("rotates [1,2,3,4,5] by 2 → [4,5,1,2,3]")
        void basicRotation() {
            int[] arr = {1, 2, 3, 4, 5};
            tasks.rotateRight(arr, 2);
            assertThat(arr).containsExactly(4, 5, 1, 2, 3);
        }

        @Test
        @DisplayName("rotate by 1 moves last element to front")
        void rotateByOne() {
            int[] arr = {1, 2, 3};
            tasks.rotateRight(arr, 1);
            assertThat(arr).containsExactly(3, 1, 2);
        }

        @Test
        @DisplayName("rotate by length = no change")
        void rotateByLength() {
            int[] arr = {1, 2, 3};
            tasks.rotateRight(arr, 3);
            assertThat(arr).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("k > length is handled via modulo")
        void kGreaterThanLength() {
            int[] arr = {1, 2, 3};
            tasks.rotateRight(arr, 4);   // 4 % 3 = 1
            assertThat(arr).containsExactly(3, 1, 2);
        }

        @Test
        @DisplayName("empty array is unchanged")
        void emptyArray() {
            int[] arr = {};
            tasks.rotateRight(arr, 5);
            assertThat(arr).isEmpty();
        }

        @Test
        @DisplayName("single element is unchanged")
        void singleElement() {
            int[] arr = {42};
            tasks.rotateRight(arr, 7);
            assertThat(arr).containsExactly(42);
        }
    }

    // ── Task 2: removeDuplicates ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement removeDuplicates()")
    @DisplayName("Task 2 — removeDuplicates()")
    class RemoveDuplicatesTest {

        @Test
        @DisplayName("removes duplicates preserving first-occurrence order")
        void basicDedup() {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 2, 1, 4));
            tasks.removeDuplicates(list);
            assertThat(list).containsExactly(1, 2, 3, 4);
        }

        @Test
        @DisplayName("all-same list reduces to single element")
        void allSame() {
            List<Integer> list = new ArrayList<>(List.of(5, 5, 5, 5));
            tasks.removeDuplicates(list);
            assertThat(list).containsExactly(5);
        }

        @Test
        @DisplayName("no-duplicate list is unchanged")
        void noChange() {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
            tasks.removeDuplicates(list);
            assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("empty list stays empty")
        void emptyList() {
            List<Integer> list = new ArrayList<>();
            tasks.removeDuplicates(list);
            assertThat(list).isEmpty();
        }

        @Test
        @DisplayName("modifies the list in-place (same object)")
        void inPlace() {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 1));
            List<Integer> ref  = list;  // same reference
            tasks.removeDuplicates(list);
            assertThat(list).isSameAs(ref);
        }
    }

    // ── Task 3: wordFrequency ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement wordFrequency()")
    @DisplayName("Task 3 — wordFrequency()")
    class WordFrequencyTest {

        @Test
        @DisplayName("counts words correctly")
        void basicCount() {
            Map<String, Integer> freq = tasks.wordFrequency(
                    List.of("cat", "dog", "cat", "fish", "dog", "cat"));
            assertThat(freq).containsEntry("cat",  3)
                            .containsEntry("dog",  2)
                            .containsEntry("fish", 1);
        }

        @Test
        @DisplayName("keys are lower-cased")
        void caseInsensitive() {
            Map<String, Integer> freq = tasks.wordFrequency(
                    List.of("Cat", "DOG", "cat", "Dog"));
            assertThat(freq).containsEntry("cat", 2)
                            .containsEntry("dog", 2);
        }

        @Test
        @DisplayName("empty list returns empty map")
        void emptyInput() {
            assertThat(tasks.wordFrequency(List.of())).isEmpty();
        }

        @Test
        @DisplayName("single-word list returns count of 1")
        void singleWord() {
            assertThat(tasks.wordFrequency(List.of("hello")))
                    .containsEntry("hello", 1)
                    .hasSize(1);
        }
    }

    // ── Task 4: intersection ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement intersection()")
    @DisplayName("Task 4 — intersection()")
    class IntersectionTest {

        @Test
        @DisplayName("basic intersection of two lists")
        void basicIntersection() {
            List<Integer> result = tasks.intersection(
                    List.of(1, 2, 3, 4),
                    List.of(3, 4, 5, 6));
            assertThat(result).containsExactly(3, 4);
        }

        @Test
        @DisplayName("no common elements → empty list")
        void noCommon() {
            assertThat(tasks.intersection(List.of(1, 2), List.of(3, 4))).isEmpty();
        }

        @Test
        @DisplayName("duplicates in 'a' produce only one entry in result")
        void deduplicatesResult() {
            List<Integer> result = tasks.intersection(
                    List.of(1, 1, 2, 3),
                    List.of(1, 3, 5));
            assertThat(result).containsExactly(1, 3);
        }

        @Test
        @DisplayName("empty 'a' → empty result")
        void emptyA() {
            assertThat(tasks.intersection(List.of(), List.of(1, 2, 3))).isEmpty();
        }

        @Test
        @DisplayName("order follows first appearance in 'a'")
        void orderFromA() {
            List<Integer> result = tasks.intersection(
                    List.of(4, 2, 3, 1),
                    List.of(1, 2, 3));
            assertThat(result).containsExactly(2, 3, 1);
        }
    }

    // ── Task 5: groupByFirstChar ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement groupByFirstChar()")
    @DisplayName("Task 5 — groupByFirstChar()")
    class GroupByFirstCharTest {

        @Test
        @DisplayName("groups words by first character")
        void basicGrouping() {
            Map<Character, List<String>> groups = tasks.groupByFirstChar(
                    List.of("apple", "avocado", "banana", "blueberry", "cherry"));
            assertThat(groups.get('a')).containsExactly("apple", "avocado");
            assertThat(groups.get('b')).containsExactly("banana", "blueberry");
            assertThat(groups.get('c')).containsExactly("cherry");
        }

        @Test
        @DisplayName("first-char key is always lower-cased")
        void caseInsensitiveKey() {
            Map<Character, List<String>> groups = tasks.groupByFirstChar(
                    List.of("Apple", "Ant", "ant"));
            assertThat(groups).containsKey('a');
            assertThat(groups.get('a')).containsExactly("Apple", "Ant", "ant");
        }

        @Test
        @DisplayName("empty input returns empty map")
        void emptyInput() {
            assertThat(tasks.groupByFirstChar(List.of())).isEmpty();
        }

        @Test
        @DisplayName("insertion order within each group is preserved")
        void preservesOrder() {
            Map<Character, List<String>> groups = tasks.groupByFirstChar(
                    List.of("cat", "cobra", "crane"));
            assertThat(groups.get('c')).containsExactly("cat", "cobra", "crane");
        }
    }

    // ── Task 6: twoSum ────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement twoSum()")
    @DisplayName("Task 6 — twoSum()")
    class TwoSumTest {

        @Test
        @DisplayName("[2,7,11,15] target=9 → [0,1]")
        void classic() {
            assertThat(tasks.twoSum(List.of(2, 7, 11, 15), 9))
                    .containsExactly(0, 1);
        }

        @Test
        @DisplayName("[3,2,4] target=6 → [1,2]")
        void notFirstTwo() {
            assertThat(tasks.twoSum(List.of(3, 2, 4), 6))
                    .containsExactly(1, 2);
        }

        @Test
        @DisplayName("[3,3] target=6 → [0,1]")
        void sameValue() {
            assertThat(tasks.twoSum(List.of(3, 3), 6))
                    .containsExactly(0, 1);
        }

        @ParameterizedTest(name = "list={0} target={1} → [{2},{3}]")
        @CsvSource({
                "'1,5,3,7,9', 12, 2, 3",   // 3+7+? no — 3 at idx2, 9 at idx4 → 12
                "'0,4,3,0',    0, 0, 3",
        })
        @DisplayName("parametrized edge cases")
        void parametrized(String listStr, int target, int expectedI, int expectedJ) {
            List<Integer> list = new ArrayList<>();
            for (String s : listStr.split(",")) list.add(Integer.parseInt(s.trim()));
            int[] result = tasks.twoSum(list, target);
            assertThat(result[0]).isEqualTo(expectedI);
            assertThat(result[1]).isEqualTo(expectedJ);
        }

        @Test
        @DisplayName("result satisfies list.get(i) + list.get(j) == target")
        void resultSatisfiesContract() {
            List<Integer> list = List.of(5, 1, 8, 3, 4);
            int target = 9;
            int[] idx = tasks.twoSum(list, target);
            assertThat(idx[0]).isLessThan(idx[1]);
            assertThat(list.get(idx[0]) + list.get(idx[1])).isEqualTo(target);
        }
    }

    // ── Task 7: deduplicateSorted ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement deduplicateSorted()")
    @DisplayName("Task 7 — deduplicateSorted()")
    class DeduplicateSortedTest {

        @Test
        @DisplayName("[1,1,2,3,3,4] → [1,2,3,4]")
        void basicDedup() {
            assertThat(tasks.deduplicateSorted(new int[]{1, 1, 2, 3, 3, 4}))
                    .containsExactly(1, 2, 3, 4);
        }

        @Test
        @DisplayName("no duplicates → unchanged")
        void noChange() {
            assertThat(tasks.deduplicateSorted(new int[]{1, 2, 3}))
                    .containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("all same → single element")
        void allSame() {
            assertThat(tasks.deduplicateSorted(new int[]{5, 5, 5}))
                    .containsExactly(5);
        }

        @Test
        @DisplayName("empty array → empty array")
        void empty() {
            assertThat(tasks.deduplicateSorted(new int[]{})).isEmpty();
        }

        @Test
        @DisplayName("single element → same single element")
        void single() {
            assertThat(tasks.deduplicateSorted(new int[]{42}))
                    .containsExactly(42);
        }

        @Test
        @DisplayName("result is a NEW array, not the original")
        void returnsNewArray() {
            int[] input  = {1, 1, 2};
            int[] result = tasks.deduplicateSorted(input);
            assertThat(result).isNotSameAs(input);
        }

        @Test
        @DisplayName("result is sorted ascending")
        void resultSorted() {
            int[] result = tasks.deduplicateSorted(new int[]{1, 1, 2, 3, 3, 4, 4});
            int[] sorted = result.clone();
            Arrays.sort(sorted);
            assertThat(result).isEqualTo(sorted);
        }
    }
}

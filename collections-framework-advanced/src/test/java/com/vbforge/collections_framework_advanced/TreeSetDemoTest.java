package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TreeSetDemo — API contracts")
class TreeSetDemoTest {

    private static TreeSet<Integer> prices() {
        return new TreeSet<>(Arrays.asList(10, 20, 30, 40));
    }

    @Nested
    @DisplayName("1. Sorted and unique")
    class SortedUniqueTest {

        @Test
        @DisplayName("elements come out ascending and without duplicates")
        void sortedAndUnique() {
            assertThat(TreeSetDemo.sortedUniqueDemo(Arrays.asList(5, 1, 5, 3, 1))).containsExactly(1, 3, 5);
        }

        @Test
        @DisplayName("an empty input gives an empty result")
        void emptyInput() {
            assertThat(TreeSetDemo.sortedUniqueDemo(Collections.<Integer>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("negative numbers are ordered correctly")
        void negatives() {
            assertThat(TreeSetDemo.sortedUniqueDemo(Arrays.asList(0, -2, 2, -2))).containsExactly(-2, 0, 2);
        }
    }

    @Nested
    @DisplayName("2. Navigation: floor / ceiling / lower / higher")
    class NavigationTest {

        @Test
        @DisplayName("a value between two elements: lower=floor and higher=ceiling")
        void betweenElements() {
            Map<String, Integer> result = TreeSetDemo.navigationDemo(prices(), 25);

            assertThat(result).containsEntry("lower", 20).containsEntry("floor", 20)
                    .containsEntry("ceiling", 30).containsEntry("higher", 30);
        }

        @Test
        @DisplayName("an existing element: floor/ceiling return it, lower/higher skip it")
        void existingElement() {
            Map<String, Integer> result = TreeSetDemo.navigationDemo(prices(), 30);

            assertThat(result).containsEntry("lower", 20).containsEntry("floor", 30)
                    .containsEntry("ceiling", 30).containsEntry("higher", 40);
        }

        @Test
        @DisplayName("below the smallest element there is no lower or floor")
        void belowSmallest() {
            Map<String, Integer> result = TreeSetDemo.navigationDemo(prices(), 5);

            assertThat(result).containsEntry("lower", null).containsEntry("floor", null)
                    .containsEntry("ceiling", 10).containsEntry("higher", 10);
        }

        @Test
        @DisplayName("above the largest element there is no ceiling or higher")
        void aboveLargest() {
            Map<String, Integer> result = TreeSetDemo.navigationDemo(prices(), 99);

            assertThat(result).containsEntry("lower", 40).containsEntry("floor", 40)
                    .containsEntry("ceiling", null).containsEntry("higher", null);
        }

        @Test
        @DisplayName("an empty set has no neighbours at all")
        void emptySet() {
            Map<String, Integer> result = TreeSetDemo.navigationDemo(new TreeSet<Integer>(), 1);

            assertThat(result.values()).containsOnly((Integer) null);
        }
    }

    @Nested
    @DisplayName("3. Range views")
    class RangeViewTest {

        @Test
        @DisplayName("subSet with inclusive start and exclusive end")
        void halfOpenRange() {
            assertThat(TreeSetDemo.subSetDemo(prices(), 20, 40)).containsExactly(20, 30);
        }

        @Test
        @DisplayName("bounds do not have to be elements of the set")
        void boundsNeedNotExist() {
            assertThat(TreeSetDemo.subSetDemo(prices(), 15, 35)).containsExactly(20, 30);
        }

        @Test
        @DisplayName("an empty range gives an empty result")
        void emptyRange() {
            assertThat(TreeSetDemo.subSetDemo(prices(), 20, 20)).isEmpty();
        }

        @Test
        @DisplayName("a reversed range throws IllegalArgumentException")
        void reversedRange() {
            assertThatThrownBy(() -> TreeSetDemo.subSetDemo(prices(), 40, 10))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a view is LIVE: it reflects later changes of the original set")
        void viewIsLive() {
            assertThat(TreeSetDemo.viewIsLiveDemo()).containsExactly(3, 4, 5);
        }

        @Test
        @DisplayName("adding an element outside the view's range throws IllegalArgumentException")
        void outsideRange() {
            assertThatThrownBy(TreeSetDemo::addOutsideViewRangeDemo)
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("4. Descending order and polling the ends")
    class DescendingAndPollTest {

        @Test
        @DisplayName("descendingSet iterates from the largest to the smallest")
        void descending() {
            assertThat(TreeSetDemo.descendingDemo(Arrays.asList(3, 1, 2, 3))).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("pollFirst and pollLast return and REMOVE the smallest and largest")
        void pollEnds() {
            TreeSet<Integer> set = prices();

            assertThat(TreeSetDemo.pollEndsDemo(set)).containsExactly(10, 40);
            assertThat(set).containsExactly(20, 30);
        }

        @Test
        @DisplayName("on an empty set both polls return null")
        void pollEmpty() {
            assertThat(TreeSetDemo.pollEndsDemo(new TreeSet<Integer>())).containsExactly(null, null);
        }

        @Test
        @DisplayName("a single element is returned by pollFirst; pollLast then finds nothing")
        void singleElement() {
            assertThat(TreeSetDemo.pollEndsDemo(new TreeSet<>(Collections.singletonList(7))))
                    .containsExactly(7, null);
        }
    }

    @Nested
    @DisplayName("5. The comparator — not equals() — decides what is a duplicate")
    class ComparatorEqualityTest {

        @Test
        @DisplayName("words that differ only in case are duplicates; the first spelling wins")
        void duplicatesByComparator() {
            assertThat(TreeSetDemo.caseInsensitiveSetDemo(
                    Arrays.asList("Apple", "apple", "BANANA", "banana", "cherry")))
                    .containsExactly("Apple", "BANANA", "cherry");
        }

        @Test
        @DisplayName("with natural ordering the same words are all different")
        void naturalOrderKeepsAll() {
            assertThat(new TreeSet<>(Arrays.asList("Apple", "apple"))).hasSize(2);
        }

        @Test
        @DisplayName("contains() also uses the comparator")
        void containsUsesComparator() {
            assertThat(TreeSetDemo.caseInsensitiveContainsDemo(Arrays.asList("apple"), "APPLE")).isTrue();
            assertThat("apple".equals("APPLE")).isFalse();
        }

        @Test
        @DisplayName("a word that is genuinely absent is not found")
        void absentWord() {
            assertThat(TreeSetDemo.caseInsensitiveContainsDemo(Arrays.asList("apple"), "pear")).isFalse();
        }
    }

    @Nested
    @DisplayName("6. Null and non-comparable elements")
    class InvalidElementsTest {

        @Test
        @DisplayName("null is rejected with NullPointerException")
        void nullRejected() {
            assertThatThrownBy(TreeSetDemo::addNullDemo).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("elements that are not Comparable cause ClassCastException — even for the first one")
        void nonComparableRejected() {
            assertThatThrownBy(TreeSetDemo::addNonComparableDemo).isInstanceOf(ClassCastException.class);
        }
    }
}

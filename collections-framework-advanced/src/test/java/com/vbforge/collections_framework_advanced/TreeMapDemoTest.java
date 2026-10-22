package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TreeMapDemo — API contracts")
class TreeMapDemoTest {

    private static TreeMap<Integer, String> timeline() {
        TreeMap<Integer, String> timeline = new TreeMap<>();
        timeline.put(9, "standup");
        timeline.put(11, "review");
        timeline.put(14, "lunch");
        timeline.put(17, "deploy");
        return timeline;
    }

    private static TreeMap<String, Integer> stock() {
        TreeMap<String, Integer> stock = new TreeMap<>();
        stock.put("pear", 3);
        stock.put("apple", 5);
        stock.put("fig", 1);
        return stock;
    }

    @Nested
    @DisplayName("1. Keys are sorted")
    class SortedKeysTest {

        @Test
        @DisplayName("keys come out in ascending order regardless of insertion order")
        void ascendingKeys() {
            Map<String, Integer> input = new HashMap<>();
            input.put("pear", 3);
            input.put("apple", 5);
            input.put("fig", 1);

            assertThat(TreeMapDemo.sortedKeysDemo(input)).containsExactly("apple", "fig", "pear");
        }

        @Test
        @DisplayName("an empty map gives no keys")
        void emptyMap() {
            assertThat(TreeMapDemo.sortedKeysDemo(Collections.<String, Integer>emptyMap())).isEmpty();
        }

        @Test
        @DisplayName("toString lists entries in key order")
        void toStringInKeyOrder() {
            assertThat(stock().toString()).isEqualTo("{apple=5, fig=1, pear=3}");
        }
    }

    @Nested
    @DisplayName("2. Range lookup: floor / ceiling")
    class RangeLookupTest {

        @ParameterizedTest(name = "score {0} -> grade {1}")
        @CsvSource({
                "100, A",
                "95,  A",
                "90,  A",
                "89,  B",
                "80,  B",
                "79,  C",
                "70,  C",
                "69,  D",
                "60,  D",
                "59,  F",
                "0,   F"
        })
        @DisplayName("the greatest lower bound that is <= the score decides the grade")
        void grades(int score, String expected) {
            assertThat(TreeMapDemo.gradeDemo(score)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a score below the lowest boundary has no floor entry — invalid")
        void belowLowestBoundary() {
            assertThat(TreeMapDemo.gradeDemo(-1)).isEqualTo("invalid");
        }

        @Test
        @DisplayName("key navigation around an existing key")
        void navigationExistingKey() {
            TreeMap<Integer, String> map = new TreeMap<>();
            map.put(10, "x");
            map.put(20, "y");
            map.put(30, "z");

            assertThat(TreeMapDemo.navigationDemo(map, 20))
                    .containsEntry("lowerKey", 10).containsEntry("floorKey", 20)
                    .containsEntry("ceilingKey", 20).containsEntry("higherKey", 30);
        }

        @Test
        @DisplayName("missing neighbours are null")
        void navigationAtEdges() {
            TreeMap<Integer, String> map = new TreeMap<>();
            map.put(10, "x");

            assertThat(TreeMapDemo.navigationDemo(map, 10))
                    .containsEntry("lowerKey", null).containsEntry("floorKey", 10)
                    .containsEntry("ceilingKey", 10).containsEntry("higherKey", null);
        }
    }

    @Nested
    @DisplayName("3. First / last entries")
    class FirstLastTest {

        @Test
        @DisplayName("firstKey and lastKey are the smallest and largest keys")
        void firstAndLast() {
            assertThat(TreeMapDemo.firstAndLastDemo(stock())).isEqualTo("apple..pear");
        }

        @Test
        @DisplayName("firstKey() on an empty map throws NoSuchElementException")
        void firstKeyThrows() {
            assertThatThrownBy(TreeMapDemo::firstKeyOfEmptyDemo).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("firstEntry() on an empty map returns null instead of throwing")
        void firstEntryReturnsNull() {
            assertThat(TreeMapDemo.firstEntryOfEmptyIsNullDemo()).isTrue();
        }

        @Test
        @DisplayName("pollFirstEntry removes and returns the entry with the smallest key")
        void pollFirstEntry() {
            TreeMap<String, Integer> map = stock();

            assertThat(TreeMapDemo.pollFirstEntryDemo(map)).isEqualTo("apple=5");
            assertThat(map).containsOnlyKeys("fig", "pear");
        }

        @Test
        @DisplayName("entries returned by firstEntry() are immutable snapshots")
        void snapshotEntry() {
            assertThatThrownBy(TreeMapDemo::snapshotEntryDemo).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("4. Sub-map views")
    class SubMapViewTest {

        @Test
        @DisplayName("headMap: keys strictly below the bound")
        void headMap() {
            assertThat(TreeMapDemo.headMapDemo(timeline(), 14)).containsExactly("standup", "review");
        }

        @Test
        @DisplayName("tailMap: keys from the bound upwards, bound included")
        void tailMap() {
            assertThat(TreeMapDemo.tailMapDemo(timeline(), 14)).containsExactly("lunch", "deploy");
        }

        @Test
        @DisplayName("subMap: closed range on both ends")
        void subMap() {
            assertThat(TreeMapDemo.subMapDemo(timeline(), 11, 14)).containsExactly("review", "lunch");
        }

        @Test
        @DisplayName("bounds do not have to be existing keys")
        void boundsNeedNotExist() {
            assertThat(TreeMapDemo.subMapDemo(timeline(), 10, 16)).containsExactly("review", "lunch");
        }

        @Test
        @DisplayName("a reversed range throws IllegalArgumentException")
        void reversedRange() {
            assertThatThrownBy(() -> TreeMapDemo.subMapDemo(timeline(), 14, 11))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a view is LIVE: it reflects later changes of the original map")
        void viewIsLive() {
            Map<Integer, String> view = TreeMapDemo.viewIsLiveDemo();

            assertThat(view).containsOnlyKeys(2, 3);
            assertThat(view).containsEntry(2, "b").containsEntry(3, "c");
        }
    }

    @Nested
    @DisplayName("5. Descending order")
    class DescendingTest {

        @Test
        @DisplayName("descendingKeySet iterates from the largest key to the smallest")
        void descendingKeys() {
            Map<String, Integer> input = new HashMap<>();
            input.put("pear", 3);
            input.put("apple", 5);
            input.put("fig", 1);

            assertThat(TreeMapDemo.descendingKeysDemo(input)).containsExactly("pear", "fig", "apple");
        }

        @Test
        @DisplayName("an empty map gives no keys")
        void emptyMap() {
            assertThat(TreeMapDemo.descendingKeysDemo(Collections.<String, Integer>emptyMap())).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. The comparator decides key equality; nulls")
    class ComparatorEqualityTest {

        @Test
        @DisplayName("keys that differ only in case are the SAME key")
        void sameKey() {
            assertThat(TreeMapDemo.caseInsensitiveMapDemo()).hasSize(1);
        }

        @Test
        @DisplayName("the second put replaces the value but keeps the first key spelling")
        void firstSpellingKept() {
            TreeMap<String, Integer> map = TreeMapDemo.caseInsensitiveMapDemo();

            assertThat(map.firstKey()).isEqualTo("Key");
            assertThat(map.get("Key")).isEqualTo(2);
        }

        @Test
        @DisplayName("lookup ignores case too")
        void lookupIgnoresCase() {
            assertThat(TreeMapDemo.caseInsensitiveMapDemo().get("kEy")).isEqualTo(2);
        }

        @Test
        @DisplayName("a null key is rejected with NullPointerException")
        void nullKey() {
            assertThatThrownBy(TreeMapDemo::putNullKeyDemo).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("null values are allowed, and containsKey tells them from absent keys")
        void nullValue() {
            assertThat(TreeMapDemo.nullValueDemo()).isTrue();
        }

        @Test
        @DisplayName("TreeMap with natural ordering treats different-case keys as different")
        void naturalOrderDistinguishesCase() {
            TreeMap<String, Integer> map = new TreeMap<>();
            map.put("Key", 1);
            map.put("KEY", 2);

            assertThat(map).hasSize(2);
            assertThat(Arrays.asList(map.keySet().toArray())).containsExactly("KEY", "Key");
        }
    }
}

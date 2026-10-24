package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.mini.MiniHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HashMapInternalsTasks — your implementations")
class HashMapInternalsTasksTest {

    private HashMapInternalsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new HashMapInternalsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement spread()")
    @DisplayName("1. spread(hashCode)")
    class SpreadTest {

        @ParameterizedTest(name = "spread({0}) = {1}")
        @CsvSource({
                "0,      0",
                "1,      1",
                "65536,  65537",
                "-1,     -65536",
                "131072, 131074"
        })
        @DisplayName("examples from Javadoc and more")
        void spreadValues(int hashCode, int expected) {
            assertThat(tasks.spread(hashCode)).isEqualTo(expected);
        }

        @Test
        @DisplayName("agrees with the formula of the real HashMap for many values")
        void matchesFormula() {
            for (int h = -100_000; h <= 100_000; h += 97) {
                assertThat(tasks.spread(h)).isEqualTo(h ^ (h >>> 16));
            }
            assertThat(tasks.spread(Integer.MIN_VALUE)).isEqualTo(Integer.MIN_VALUE ^ 0x8000);
        }

        @Test
        @DisplayName("applying it to small hash codes changes nothing")
        void smallValuesUnchanged() {
            assertThat(tasks.spread(40000)).isEqualTo(40000);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement bucketIndex()")
    @DisplayName("2. bucketIndex(key, tableSize)")
    class BucketIndexTest {

        @ParameterizedTest(name = "bucketIndex({0}, {1}) = {2}")
        @CsvSource({
                "1,     16, 1",
                "17,    16, 1",
                "65536, 16, 1",
                "-1,    16, 0",
                "100,   64, 36",
                "0,     16, 0"
        })
        @DisplayName("examples from Javadoc and more (Integer keys)")
        void integerKeys(int key, int tableSize, int expected) {
            assertThat(tasks.bucketIndex(key, tableSize)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: a null key goes to bucket 0")
        void nullKey() {
            assertThat(tasks.bucketIndex(null, 16)).isZero();
            assertThat(tasks.bucketIndex(null, 1024)).isZero();
        }

        @Test
        @DisplayName("a table of size 1 puts everything into bucket 0")
        void tableOfOne() {
            assertThat(tasks.bucketIndex("anything", 1)).isZero();
            assertThat(tasks.bucketIndex(12345, 1)).isZero();
        }

        @Test
        @DisplayName("works for Strings and always returns a valid index")
        void stringKeys() {
            for (String key : new String[]{"apple", "banana", "Aa", "BB", "", "a much longer key than the others"}) {
                assertThat(tasks.bucketIndex(key, 32)).isBetween(0, 31);
            }
            assertThat(tasks.bucketIndex("Aa", 32)).isEqualTo(tasks.bucketIndex("BB", 32));
        }

        @Test
        @DisplayName("agrees with the bucket rule of MiniHashMap for many keys and table sizes")
        void agreesWithMiniMap() {
            for (int i = -500; i < 500; i += 7) {
                MiniHashMap<Object, String> mini = new MiniHashMap<>(16);
                assertThat(tasks.bucketIndex(i, 16)).isEqualTo(mini.bucketIndexOf(i));
                MiniHashMap<Object, String> bigger = new MiniHashMap<>(256);
                assertThat(tasks.bucketIndex("k" + i, 256)).isEqualTo(bigger.bucketIndexOf("k" + i));
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tableSizeFor()")
    @DisplayName("3. tableSizeFor(capacity)")
    class TableSizeForTest {

        @ParameterizedTest(name = "tableSizeFor({0}) = {1}")
        @CsvSource({
                "0,          1",
                "1,          1",
                "2,          2",
                "3,          4",
                "5,          8",
                "16,         16",
                "17,         32",
                "134,        256",
                "1000,       1024",
                "1073741824, 1073741824",
                "1073741825, 1073741824",
                "2000000000, 1073741824"
        })
        @DisplayName("examples from Javadoc and more")
        void sizes(int capacity, int expected) {
            assertThat(tasks.tableSizeFor(capacity)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a negative capacity is rejected")
        void negativeCapacity() {
            assertThatThrownBy(() -> tasks.tableSizeFor(-1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the result is always a power of two")
        void alwaysPowerOfTwo() {
            for (int capacity = 0; capacity < 3000; capacity += 13) {
                int result = tasks.tableSizeFor(capacity);

                assertThat(Integer.bitCount(result)).isEqualTo(1);
                assertThat(result).isGreaterThanOrEqualTo(capacity);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement resizeCount()")
    @DisplayName("4. resizeCount(entries)")
    class ResizeCountTest {

        @ParameterizedTest(name = "{0} entries -> {1} doublings")
        @CsvSource({
                "0,    0",
                "1,    0",
                "12,   0",
                "13,   1",
                "24,   1",
                "25,   2",
                "48,   2",
                "49,   3",
                "100,  4",
                "1000, 7"
        })
        @DisplayName("examples from Javadoc and more")
        void doublings(int entries, int expected) {
            assertThat(tasks.resizeCount(entries)).isEqualTo(expected);
        }

        @Test
        @DisplayName("agrees with MiniHashMap for every size up to 300")
        void agreesWithMiniMap() {
            for (int entries = 0; entries <= 300; entries++) {
                MiniHashMap<Integer, Integer> mini = new MiniHashMap<>();
                for (int i = 0; i < entries; i++) {
                    mini.put(i, i);
                }

                assertThat(tasks.resizeCount(entries)).as("entries=%d", entries).isEqualTo(mini.resizeCount());
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement iterationOrder()")
    @DisplayName("5. iterationOrder(keys, tableSize)")
    class IterationOrderTest {

        @Test
        @DisplayName("example from Javadoc: a collision chain keeps insertion order")
        void chainKeepsInsertionOrder() {
            assertThat(tasks.iterationOrder(new int[]{33, 1, 17, 2}, 16)).containsExactly(33, 1, 17, 2);
        }

        @Test
        @DisplayName("example from Javadoc: buckets are visited in ascending order")
        void bucketsAscending() {
            assertThat(tasks.iterationOrder(new int[]{5, 3, 4}, 16)).containsExactly(3, 4, 5);
        }

        @Test
        @DisplayName("example from Javadoc: a repeated key is stored once, at its first position")
        void duplicateKey() {
            assertThat(tasks.iterationOrder(new int[]{5, 5, 3}, 16)).containsExactly(3, 5);
        }

        @Test
        @DisplayName("example from Javadoc: 16 and 0 share bucket 0")
        void sharedBucketZero() {
            assertThat(tasks.iterationOrder(new int[]{16, 1, 0}, 16)).containsExactly(16, 0, 1);
        }

        @Test
        @DisplayName("example from Javadoc: no keys give no order")
        void noKeys() {
            assertThat(tasks.iterationOrder(new int[0], 16)).isEmpty();
        }

        @Test
        @DisplayName("matches the iteration order of the REAL HashMap (table of 16, up to 12 keys)")
        void matchesRealHashMapSize16() {
            int[] keys = {33, 1, 17, 2, 18, 4, 100, 36, 21, 7};

            assertThat(tasks.iterationOrder(keys, 16)).isEqualTo(realOrder(keys, 16));
        }

        @Test
        @DisplayName("matches the iteration order of the REAL HashMap (table of 64, up to 48 keys)")
        void matchesRealHashMapSize64() {
            int[] keys = new int[40];
            for (int i = 0; i < keys.length; i++) {
                keys[i] = i * 37 + 5;
            }

            assertThat(tasks.iterationOrder(keys, 64)).isEqualTo(realOrder(keys, 64));
        }

        private List<Integer> realOrder(int[] keys, int tableSize) {
            Map<Integer, String> real = new HashMap<>(tableSize);
            for (int key : keys) {
                real.put(key, "v");
            }
            return new ArrayList<>(real.keySet());
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement binAction()")
    @DisplayName("6. binAction(nodesInBinBeforeInsert, tableSize)")
    class BinActionTest {

        @ParameterizedTest(name = "{0} nodes, table {1} -> {2}")
        @CsvSource({
                "0,  16,  none",
                "3,  16,  none",
                "7,  16,  none",
                "7,  128, none",
                "8,  16,  resize",
                "8,  32,  resize",
                "8,  63,  resize",
                "8,  64,  treeify",
                "9,  64,  treeify",
                "20, 128, treeify"
        })
        @DisplayName("examples from Javadoc and more")
        void actions(int nodes, int tableSize, String expected) {
            assertThat(tasks.binAction(nodes, tableSize)).isEqualTo(expected);
        }
    }
}

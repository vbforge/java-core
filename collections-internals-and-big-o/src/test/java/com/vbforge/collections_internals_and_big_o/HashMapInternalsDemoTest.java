package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.HashMapInternalsDemo.ConstantHashKey;
import com.vbforge.collections_internals_and_big_o.mini.MiniHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HashMapInternalsDemo — API contracts")
class HashMapInternalsDemoTest {

    @Nested
    @DisplayName("1. From key to bucket: spread + mask")
    class BucketIndexTest {

        @Test
        @DisplayName("small integers go to the bucket equal to their value")
        void smallIntegers() {
            assertThat(HashMapInternalsDemo.bucketIndexDemo(5, 16)).isEqualTo(5);
        }

        @Test
        @DisplayName("keys that agree in their low 4 bits share a bucket in a table of 16")
        void lowBitsCollide() {
            assertThat(HashMapInternalsDemo.bucketIndexDemo(1, 16))
                    .isEqualTo(HashMapInternalsDemo.bucketIndexDemo(17, 16))
                    .isEqualTo(HashMapInternalsDemo.bucketIndexDemo(33, 16));
        }

        @Test
        @DisplayName("without spreading, 0 and 65536 collide because only the low bits count")
        void withoutSpreadCollides() {
            assertThat(HashMapInternalsDemo.bucketIndexWithoutSpreadDemo(0, 16)).isEqualTo(0);
            assertThat(HashMapInternalsDemo.bucketIndexWithoutSpreadDemo(65536, 16)).isEqualTo(0);
        }

        @Test
        @DisplayName("with spreading, the high bit of 65536 reaches the low bits and separates it from 0")
        void spreadSeparates() {
            assertThat(HashMapInternalsDemo.bucketIndexDemo(0, 16)).isEqualTo(0);
            assertThat(HashMapInternalsDemo.bucketIndexDemo(65536, 16)).isEqualTo(1);
        }

        @Test
        @DisplayName("a null key always lives in bucket 0")
        void nullKeyBucketZero() {
            assertThat(HashMapInternalsDemo.bucketIndexDemo(null, 16)).isZero();
            assertThat(HashMapInternalsDemo.bucketIndexDemo(null, 1024)).isZero();
        }

        @Test
        @DisplayName("negative hash codes still give a valid, non-negative index")
        void negativeHashCodes() {
            int index = HashMapInternalsDemo.bucketIndexDemo(-12345, 16);

            assertThat(index).isBetween(0, 15);
        }

        @Test
        @DisplayName("HashMap really accepts a null key")
        void nullKeyWorks() {
            assertThat(HashMapInternalsDemo.nullKeyDemo()).isEqualTo("stored under null");
        }

        @Test
        @DisplayName("the demo's bucket rule agrees with MiniHashMap.bucketIndexOf")
        void agreesWithMiniMap() {
            MiniHashMap<Object, String> map = new MiniHashMap<>();
            Object[] keys = {1, 17, 65536, -7, "hello", "Aa", null};

            for (Object key : keys) {
                assertThat(HashMapInternalsDemo.bucketIndexDemo(key, 16)).isEqualTo(map.bucketIndexOf(key));
            }
        }
    }

    @Nested
    @DisplayName("2. Collisions: a chain per bucket")
    class CollisionTest {

        @Test
        @DisplayName("1, 17 and 33 form a chain of length 3")
        void chainOfThree() {
            assertThat(HashMapInternalsDemo.longestChainDemo(1, 17, 33)).isEqualTo(3);
        }

        @Test
        @DisplayName("keys in different buckets form chains of length 1")
        void noCollisions() {
            assertThat(HashMapInternalsDemo.longestChainDemo(1, 2, 3)).isEqualTo(1);
        }

        @Test
        @DisplayName("no keys give no chain")
        void noKeys() {
            assertThat(HashMapInternalsDemo.longestChainDemo()).isZero();
        }

        @Test
        @DisplayName("\"Aa\" and \"BB\" are different Strings with the same hashCode")
        void sameHashCode() {
            assertThat(HashMapInternalsDemo.sameHashCodeDemo()).isTrue();
            assertThat("Aa".equals("BB")).isFalse();
        }

        @Test
        @DisplayName("equal hash codes do not merge keys: equals() tells them apart")
        void bothKeysStored() {
            assertThat(HashMapInternalsDemo.distinctKeysWithSameHashDemo()).containsExactly(2, 1, 2);
        }
    }

    @Nested
    @DisplayName("3. Load factor and resizing")
    class ResizeTest {

        @ParameterizedTest(name = "{0} entries -> table size {1}")
        @CsvSource({
                "0,   0",
                "1,   16",
                "12,  16",
                "13,  32",
                "24,  32",
                "25,  64",
                "48,  64",
                "49,  128"
        })
        @DisplayName("the table is lazy, starts at 16 and doubles when entries exceed 75 % of capacity")
        void tableSizes(int entries, int expected) {
            assertThat(HashMapInternalsDemo.tableSizeAfterDemo(entries)).isEqualTo(expected);
        }

        @Test
        @DisplayName("100 entries make a default map double 4 times (16 -> 256)")
        void defaultMapResizes() {
            assertThat(HashMapInternalsDemo.resizesWithoutPresizingDemo(100)).isEqualTo(4);
        }

        @Test
        @DisplayName("a presized map never resizes")
        void presizedMapDoesNotResize() {
            assertThat(HashMapInternalsDemo.resizesWithPresizingDemo(100)).isZero();
        }

        @Test
        @DisplayName("MiniHashMap.tableSizeFor rounds up to the next power of two")
        void tableSizeForRoundsUp() {
            assertThat(MiniHashMap.tableSizeFor(0)).isEqualTo(1);
            assertThat(MiniHashMap.tableSizeFor(16)).isEqualTo(16);
            assertThat(MiniHashMap.tableSizeFor(17)).isEqualTo(32);
            assertThat(MiniHashMap.tableSizeFor(134)).isEqualTo(256);
        }
    }

    @Nested
    @DisplayName("4. Iteration order reveals the buckets (real HashMap)")
    class IterationOrderTest {

        @Test
        @DisplayName("12 entries (no resize yet): the collision chain 33, 1, 17 comes first in insertion order")
        void beforeResize() {
            assertThat(HashMapInternalsDemo.orderBeforeResizeDemo())
                    .containsExactly(33, 1, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        }

        @Test
        @DisplayName("13 entries (table doubled): 17 gets its own bucket and moves to the very end")
        void afterResize() {
            assertThat(HashMapInternalsDemo.orderAfterResizeDemo())
                    .containsExactly(33, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 17);
        }

        @Test
        @DisplayName("the same keys appear in a different order — HashMap order is not a contract")
        void orderChangedBySize() {
            assertThat(HashMapInternalsDemo.orderBeforeResizeDemo()).startsWith(33, 1, 17);
            assertThat(HashMapInternalsDemo.orderAfterResizeDemo()).endsWith(17);
        }

        @Test
        @DisplayName("MiniHashMap reproduces the order of the real HashMap before the resize")
        void miniMatchesRealBefore() {
            assertThat(HashMapInternalsDemo.miniIterationOrderDemo(33, 1, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                    .isEqualTo(HashMapInternalsDemo.orderBeforeResizeDemo());
        }

        @Test
        @DisplayName("MiniHashMap reproduces the order of the real HashMap after the resize")
        void miniMatchesRealAfter() {
            assertThat(HashMapInternalsDemo.miniIterationOrderDemo(33, 1, 17, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                    .isEqualTo(HashMapInternalsDemo.orderAfterResizeDemo());
        }

        @Test
        @DisplayName("MiniHashMap matches the real HashMap for 30 String keys")
        void miniMatchesRealForStrings() {
            MiniHashMap<String, Integer> mini = new MiniHashMap<>();
            Map<String, Integer> real = new HashMap<>();
            for (int i = 0; i < 30; i++) {
                mini.put("key" + i, i);
                real.put("key" + i, i);
            }

            assertThat(mini.keysInIterationOrder()).isEqualTo(new ArrayList<>(real.keySet()));
        }

        @Test
        @DisplayName("MiniHashMap matches the real HashMap for 500 sequential Integer keys")
        void miniMatchesRealForIntegers() {
            MiniHashMap<Integer, Integer> mini = new MiniHashMap<>();
            Map<Integer, Integer> real = new HashMap<>();
            for (int i = 0; i < 500; i++) {
                mini.put(i * 7, i);
                real.put(i * 7, i);
            }

            assertThat(mini.keysInIterationOrder()).isEqualTo(new ArrayList<>(real.keySet()));
        }
    }

    @Nested
    @DisplayName("5. Treeification")
    class TreeifyTest {

        @ParameterizedTest(name = "{0} nodes in the bin, table {1} -> {2}")
        @CsvSource({
                "0,  16,  none",
                "7,  16,  none",
                "7,  64,  none",
                "8,  16,  resize",
                "8,  32,  resize",
                "8,  63,  resize",
                "8,  64,  treeify",
                "20, 128, treeify"
        })
        @DisplayName("short chains stay chains, long chains resize a small table or become trees")
        void decisions(int nodes, int tableSize, String expected) {
            assertThat(HashMapInternalsDemo.treeifyDecisionDemo(nodes, tableSize)).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("6. A bad hashCode destroys the O(1)")
    class BadHashTest {

        @Test
        @DisplayName("with a constant hashCode a lookup walks the whole chain: n probes")
        void constantHashIsLinear() {
            assertThat(HashMapInternalsDemo.probesWithConstantHashDemo(10)).isEqualTo(10);
            assertThat(HashMapInternalsDemo.probesWithConstantHashDemo(1_000)).isEqualTo(1_000);
        }

        @Test
        @DisplayName("with a well-spread hashCode a lookup examines one node, however many keys there are")
        void goodHashIsConstant() {
            assertThat(HashMapInternalsDemo.probesWithGoodHashDemo(10)).isEqualTo(1);
            assertThat(HashMapInternalsDemo.probesWithGoodHashDemo(1_000)).isEqualTo(1);
            assertThat(HashMapInternalsDemo.probesWithGoodHashDemo(100_000)).isEqualTo(1);
        }

        @Test
        @DisplayName("the constant-hash keys are still different keys (equals works), only slow")
        void stillCorrect() {
            MiniHashMap<ConstantHashKey, String> map = new MiniHashMap<>();
            map.put(new ConstantHashKey(1), "one");
            map.put(new ConstantHashKey(2), "two");

            assertThat(map.get(new ConstantHashKey(1))).isEqualTo("one");
            assertThat(map.get(new ConstantHashKey(2))).isEqualTo("two");
            assertThat(map.longestChain()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("7. Gotcha: mutating a key after insertion")
    class MutableKeyTest {

        @Test
        @DisplayName("after mutating the key the entry can no longer be found")
        void lostEntry() {
            assertThat(HashMapInternalsDemo.lookupAfterMutationDemo()).isNull();
        }

        @Test
        @DisplayName("the lost entry still occupies space: size() still counts it")
        void sizeStillOne() {
            assertThat(HashMapInternalsDemo.sizeAfterMutationDemo()).isEqualTo(1);
        }

        @Test
        @DisplayName("restoring the key's state makes the entry reachable again")
        void restoredEntry() {
            assertThat(HashMapInternalsDemo.lookupAfterRestoringDemo()).isEqualTo("value");
        }
    }
}

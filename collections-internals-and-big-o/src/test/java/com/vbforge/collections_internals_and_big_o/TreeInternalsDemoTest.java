package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.model.BstNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TreeInternalsDemo — API contracts")
class TreeInternalsDemoTest {

    private static int[] ascending(int n) {
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = i;
        }
        return values;
    }

    @Nested
    @DisplayName("1. Insert and in-order traversal")
    class InsertAndInOrderTest {

        @Test
        @DisplayName("in-order traversal visits the values in ascending order")
        void inOrderIsSorted() {
            assertThat(TreeInternalsDemo.inOrderDemo(TreeInternalsDemo.buildDemo(5, 3, 8, 1, 4)))
                    .containsExactly(1, 3, 4, 5, 8);
        }

        @Test
        @DisplayName("duplicates are ignored, like in a TreeSet")
        void duplicatesIgnored() {
            assertThat(TreeInternalsDemo.inOrderDemo(TreeInternalsDemo.buildDemo(5, 3, 5, 3, 8)))
                    .containsExactly(3, 5, 8);
        }

        @Test
        @DisplayName("no values give an empty tree (null root) and an empty traversal")
        void emptyTree() {
            assertThat(TreeInternalsDemo.buildDemo()).isNull();
            assertThat(TreeInternalsDemo.inOrderDemo(null)).isEmpty();
        }

        @Test
        @DisplayName("the first value becomes the root; smaller values go left, larger values right")
        void rootAndChildren() {
            BstNode root = TreeInternalsDemo.buildDemo(5, 3, 8);

            assertThat(root.value).isEqualTo(5);
            assertThat(root.left.value).isEqualTo(3);
            assertThat(root.right.value).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("2. The shape depends on the insertion order")
    class ShapeTest {

        @Test
        @DisplayName("sorted input forms a chain: depths 0, 1, 2, 3, ...")
        void sortedInputIsAChain() {
            assertThat(TreeInternalsDemo.insertDepthsDemo(1, 2, 3, 4, 5, 6, 7)).containsExactly(0, 1, 2, 3, 4, 5, 6);
        }

        @Test
        @DisplayName("reverse-sorted input forms a chain too")
        void reverseSortedIsAChain() {
            assertThat(TreeInternalsDemo.insertDepthsDemo(7, 6, 5, 4, 3, 2, 1)).containsExactly(0, 1, 2, 3, 4, 5, 6);
        }

        @Test
        @DisplayName("the same 7 values, median first, stay shallow: the tree has height 3")
        void medianFirstIsBushy() {
            assertThat(TreeInternalsDemo.insertDepthsDemo(4, 2, 6, 1, 3, 5, 7)).containsExactly(0, 1, 1, 2, 2, 2, 2);
        }

        @Test
        @DisplayName("an inserted duplicate costs the comparisons to find it but adds no node")
        void duplicateDepth() {
            assertThat(TreeInternalsDemo.insertDepthsDemo(5, 3, 3)).containsExactly(0, 1, 2);
        }

        @Test
        @DisplayName("no values give no depths")
        void noValues() {
            assertThat(TreeInternalsDemo.insertDepthsDemo()).isEmpty();
        }
    }

    @Nested
    @DisplayName("3. The cost of a degenerate tree")
    class DegenerateCostTest {

        @Test
        @DisplayName("n sorted values cost n(n-1)/2 comparisons — here 21 for 7 values")
        void smallSorted() {
            assertThat(TreeInternalsDemo.totalComparisonsDemo(1, 2, 3, 4, 5, 6, 7)).isEqualTo(21);
        }

        @Test
        @DisplayName("1000 sorted values cost 499500 comparisons: quadratic")
        void largeSorted() {
            assertThat(TreeInternalsDemo.totalComparisonsDemo(ascending(1_000))).isEqualTo(499_500);
        }

        @Test
        @DisplayName("the same 7 values in median-first order cost only 10")
        void balancedOrderIsCheap() {
            assertThat(TreeInternalsDemo.totalComparisonsDemo(4, 2, 6, 1, 3, 5, 7)).isEqualTo(10);
        }

        @Test
        @DisplayName("15 values in perfectly balanced order cost 34 instead of 105")
        void fifteenValues() {
            assertThat(TreeInternalsDemo.totalComparisonsDemo(8, 4, 12, 2, 6, 10, 14, 1, 3, 5, 7, 9, 11, 13, 15))
                    .isEqualTo(34);
            assertThat(TreeInternalsDemo.totalComparisonsDemo(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
                    .isEqualTo(105);
        }
    }

    @Nested
    @DisplayName("4. TreeMap stays balanced")
    class TreeMapBalanceTest {

        @Test
        @DisplayName("1000 sorted puts need far fewer comparisons than a plain BST (provable bound: below 20 000)")
        void totalIsNearNLogN() {
            long total = TreeInternalsDemo.treeMapTotalComparisonsDemo(1_000);

            assertThat(total).isLessThan(20_000);
            assertThat(total).isLessThan(TreeInternalsDemo.totalComparisonsDemo(ascending(1_000)) / 10);
        }

        @Test
        @DisplayName("every put needs at least one comparison per level, so the total is clearly above n")
        void totalIsAboveN() {
            assertThat(TreeInternalsDemo.treeMapTotalComparisonsDemo(1_000)).isGreaterThan(1_000);
        }

        @Test
        @DisplayName("no single put needs more comparisons than the guaranteed height 2*log2(n+1) = 19")
        void maxPutIsLogarithmic() {
            assertThat(TreeInternalsDemo.treeMapMaxComparisonsPerPutDemo(1_000)).isLessThanOrEqualTo(19);
        }

        @Test
        @DisplayName("with a million keys the worst single put is still at most 39 comparisons")
        void maxPutForAMillionKeys() {
            assertThat(TreeInternalsDemo.treeMapMaxComparisonsPerPutDemo(1_000_000)).isLessThanOrEqualTo(39);
        }
    }
}

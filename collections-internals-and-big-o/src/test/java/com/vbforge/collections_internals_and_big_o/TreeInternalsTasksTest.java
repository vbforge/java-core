package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.model.BstNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TreeInternalsTasks — your implementations")
class TreeInternalsTasksTest {

    private TreeInternalsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TreeInternalsTasks();
    }

    /** The reference tree 5 (3 (2, 4), 8 (7, 9)). */
    private static BstNode sample() {
        return TreeInternalsDemo.buildDemo(5, 3, 8, 2, 4, 7, 9);
    }

    /** A degenerate tree 0 -> 1 -> 2 -> ... built in O(n) (inserting sorted values one by one would be quadratic). */
    private static BstNode chain(int n) {
        BstNode root = new BstNode(0);
        BstNode last = root;
        for (int i = 1; i < n; i++) {
            last.right = new BstNode(i);
            last = last.right;
        }
        return root;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement height()")
    @DisplayName("1. height(root)")
    class HeightTest {

        @Test
        @DisplayName("example from Javadoc: an empty tree has height 0")
        void emptyTree() {
            assertThat(tasks.height(null)).isZero();
        }

        @Test
        @DisplayName("example from Javadoc: a single node has height 1")
        void singleNode() {
            assertThat(tasks.height(new BstNode(5))).isEqualTo(1);
        }

        @Test
        @DisplayName("example from Javadoc: a root with two children has height 2")
        void rootWithTwoChildren() {
            assertThat(tasks.height(TreeInternalsDemo.buildDemo(5, 3, 8))).isEqualTo(2);
        }

        @Test
        @DisplayName("example from Javadoc: a chain of 4 nodes has height 4")
        void chainOfFour() {
            assertThat(tasks.height(TreeInternalsDemo.buildDemo(1, 2, 3, 4))).isEqualTo(4);
            assertThat(tasks.height(TreeInternalsDemo.buildDemo(4, 3, 2, 1))).isEqualTo(4);
        }

        @Test
        @DisplayName("the same seven values: 3 when inserted median first, 7 when inserted in order")
        void sameValuesDifferentShapes() {
            assertThat(tasks.height(TreeInternalsDemo.buildDemo(4, 2, 6, 1, 3, 5, 7))).isEqualTo(3);
            assertThat(tasks.height(TreeInternalsDemo.buildDemo(1, 2, 3, 4, 5, 6, 7))).isEqualTo(7);
        }

        @Test
        @DisplayName("an unbalanced tree: the LONGER side decides")
        void longerSideDecides() {
            assertThat(tasks.height(TreeInternalsDemo.buildDemo(10, 5, 15, 3, 2, 1))).isEqualTo(5);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement contains()")
    @DisplayName("2. contains(root, value)")
    class ContainsTest {

        @ParameterizedTest(name = "{0} is in the sample tree")
        @CsvSource({"5", "3", "8", "2", "4", "7", "9"})
        @DisplayName("example from Javadoc: every stored value is found")
        void storedValues(int value) {
            assertThat(tasks.contains(sample(), value)).isTrue();
        }

        @ParameterizedTest(name = "{0} is NOT in the sample tree")
        @CsvSource({"1", "6", "10", "0", "-5"})
        @DisplayName("example from Javadoc: absent values are not found")
        void absentValues(int value) {
            assertThat(tasks.contains(sample(), value)).isFalse();
        }

        @Test
        @DisplayName("example from Javadoc: an empty tree contains nothing")
        void emptyTree() {
            assertThat(tasks.contains(null, 1)).isFalse();
        }

        @Test
        @DisplayName("extreme int values are handled")
        void extremeValues() {
            BstNode root = new BstNode(0, new BstNode(Integer.MIN_VALUE), new BstNode(Integer.MAX_VALUE));

            assertThat(tasks.contains(root, Integer.MIN_VALUE)).isTrue();
            assertThat(tasks.contains(root, Integer.MAX_VALUE)).isTrue();
            assertThat(tasks.contains(root, 1)).isFalse();
        }

        @Test
        @DisplayName("works on a degenerate tree of 100 000 nodes — use a loop, recursion as deep as the tree would overflow the stack")
        void degenerateTree() {
            BstNode root = chain(100_000);

            assertThat(tasks.contains(root, 99_999)).isTrue();
            assertThat(tasks.contains(root, 100_000)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement remove()")
    @DisplayName("3. remove(root, value)")
    class RemoveTest {

        @Test
        @DisplayName("removing a leaf cuts it off")
        void leaf() {
            BstNode root = tasks.remove(sample(), 2);

            assertThat(TreeInternalsDemo.inOrderDemo(root)).containsExactly(3, 4, 5, 7, 8, 9);
            assertThat(root.value).isEqualTo(5);
            assertThat(root.left.left).isNull();
        }

        @Test
        @DisplayName("a node with only a LEFT child is replaced by that child")
        void onlyLeftChild() {
            BstNode root = tasks.remove(TreeInternalsDemo.buildDemo(5, 3, 2), 3);

            assertThat(root.left.value).isEqualTo(2);
            assertThat(TreeInternalsDemo.inOrderDemo(root)).containsExactly(2, 5);
        }

        @Test
        @DisplayName("a node with only a RIGHT child is replaced by that child")
        void onlyRightChild() {
            BstNode root = tasks.remove(TreeInternalsDemo.buildDemo(5, 8, 9), 8);

            assertThat(root.right.value).isEqualTo(9);
            assertThat(TreeInternalsDemo.inOrderDemo(root)).containsExactly(5, 9);
        }

        @Test
        @DisplayName("a node with two children takes the value of its in-order successor")
        void twoChildren() {
            BstNode root = tasks.remove(sample(), 3);

            assertThat(root.left.value).isEqualTo(4);
            assertThat(root.left.left.value).isEqualTo(2);
            assertThat(root.left.right).isNull();
            assertThat(TreeInternalsDemo.inOrderDemo(root)).containsExactly(2, 4, 5, 7, 8, 9);
        }

        @Test
        @DisplayName("example from Javadoc: removing the root with two children — the new root is 7")
        void rootWithTwoChildren() {
            BstNode root = tasks.remove(sample(), 5);

            assertThat(root.value).isEqualTo(7);
            assertThat(root.left.value).isEqualTo(3);
            assertThat(root.right.value).isEqualTo(8);
            assertThat(root.right.left).isNull();
            assertThat(root.right.right.value).isEqualTo(9);
            assertThat(TreeInternalsDemo.inOrderDemo(root)).containsExactly(2, 3, 4, 7, 8, 9);
        }

        @Test
        @DisplayName("removing a root with a single child returns that child as the new root")
        void rootWithOneChild() {
            BstNode root = tasks.remove(TreeInternalsDemo.buildDemo(5, 3), 5);

            assertThat(root.value).isEqualTo(3);
            assertThat(root.left).isNull();
            assertThat(root.right).isNull();
        }

        @Test
        @DisplayName("removing the only node gives an empty tree")
        void onlyNode() {
            assertThat(tasks.remove(new BstNode(1), 1)).isNull();
        }

        @Test
        @DisplayName("removing from an empty tree gives an empty tree")
        void emptyTree() {
            assertThat(tasks.remove(null, 1)).isNull();
        }

        @Test
        @DisplayName("a value that is not in the tree leaves the tree unchanged")
        void absentValue() {
            BstNode root = tasks.remove(sample(), 6);

            assertThat(TreeInternalsDemo.inOrderDemo(root)).containsExactly(2, 3, 4, 5, 7, 8, 9);
        }

        @Test
        @DisplayName("removing every value one by one always leaves a valid, sorted tree")
        void removeEverything() {
            List<Integer> order = Arrays.asList(5, 2, 9, 3, 7, 4, 8);
            BstNode root = sample();
            int remaining = 7;

            for (int value : order) {
                root = tasks.remove(root, value);
                remaining--;

                List<Integer> values = TreeInternalsDemo.inOrderDemo(root);
                assertThat(values).hasSize(remaining).isSorted().doesNotContain(value);
            }
            assertThat(root).isNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isValidBst()")
    @DisplayName("4. isValidBst(root)")
    class IsValidBstTest {

        @Test
        @DisplayName("example from Javadoc: a correct small tree")
        void validSmallTree() {
            assertThat(tasks.isValidBst(TreeInternalsDemo.buildDemo(5, 3, 8))).isTrue();
        }

        @Test
        @DisplayName("a larger tree built by insertion is valid")
        void validLargerTree() {
            assertThat(tasks.isValidBst(sample())).isTrue();
        }

        @Test
        @DisplayName("example from Javadoc: an empty tree is valid")
        void emptyTree() {
            assertThat(tasks.isValidBst(null)).isTrue();
        }

        @Test
        @DisplayName("example from Javadoc: a left child that is larger than its parent")
        void leftChildTooLarge() {
            assertThat(tasks.isValidBst(new BstNode(5, new BstNode(6), new BstNode(8)))).isFalse();
        }

        @Test
        @DisplayName("a right child that is smaller than its parent")
        void rightChildTooSmall() {
            assertThat(tasks.isValidBst(new BstNode(5, new BstNode(3), new BstNode(4)))).isFalse();
        }

        @Test
        @DisplayName("example from Javadoc: 4 is locally fine under 8 but sits in the right subtree of 5")
        void violationOnlyVisibleFromTheGrandparent() {
            BstNode root = new BstNode(5, new BstNode(3), new BstNode(8, new BstNode(4), new BstNode(9)));

            assertThat(tasks.isValidBst(root)).isFalse();
        }

        @Test
        @DisplayName("a deep violation in the LEFT subtree: 12 under 5 under 10")
        void deepViolationLeft() {
            BstNode root = new BstNode(10, new BstNode(5, new BstNode(1), new BstNode(12)), new BstNode(20));

            assertThat(tasks.isValidBst(root)).isFalse();
        }

        @Test
        @DisplayName("example from Javadoc: a duplicate value is invalid")
        void duplicateValue() {
            assertThat(tasks.isValidBst(new BstNode(5, new BstNode(5), null))).isFalse();
            assertThat(tasks.isValidBst(new BstNode(5, null, new BstNode(5)))).isFalse();
        }

        @Test
        @DisplayName("Integer.MIN_VALUE and Integer.MAX_VALUE are legal values")
        void extremeValuesAreLegal() {
            assertThat(tasks.isValidBst(new BstNode(Integer.MIN_VALUE))).isTrue();
            assertThat(tasks.isValidBst(new BstNode(Integer.MAX_VALUE))).isTrue();
            assertThat(tasks.isValidBst(
                    new BstNode(0, new BstNode(Integer.MIN_VALUE), new BstNode(Integer.MAX_VALUE)))).isTrue();
            assertThat(tasks.isValidBst(new BstNode(Integer.MIN_VALUE, null, new BstNode(Integer.MAX_VALUE)))).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement kthSmallest()")
    @DisplayName("5. kthSmallest(root, k)")
    class KthSmallestTest {

        @ParameterizedTest(name = "k = {0} -> {1}")
        @CsvSource({
                "1, 2",
                "2, 3",
                "3, 4",
                "4, 5",
                "5, 7",
                "6, 8",
                "7, 9"
        })
        @DisplayName("examples from Javadoc and more: the sample tree in ascending order")
        void ranks(int k, int expected) {
            assertThat(tasks.kthSmallest(sample(), k)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: k larger than the number of nodes is rejected")
        void kTooLarge() {
            assertThatThrownBy(() -> tasks.kthSmallest(TreeInternalsDemo.buildDemo(2, 1, 3), 4))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("k below 1 is rejected")
        void kTooSmall() {
            assertThatThrownBy(() -> tasks.kthSmallest(sample(), 0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an empty tree has no k-th element")
        void emptyTree() {
            assertThatThrownBy(() -> tasks.kthSmallest(null, 1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("works on a degenerate tree: the 3rd smallest of 0..9999 is 2")
        void degenerateTree() {
            assertThat(tasks.kthSmallest(chain(10_000), 3)).isEqualTo(2);
        }
    }
}

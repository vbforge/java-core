package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.model.BstNode;

/**
 * 🎯 TASKS — Binary search tree (the idea behind TreeMap / TreeSet)
 *
 * <p>Implement every method so that all tests in {@code TreeInternalsTasksTest} pass.
 * A {@link BstNode} keeps smaller values in {@code left} and larger values in {@code right}; there are no duplicates.
 * An empty tree is represented by {@code null}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>You may add private helper methods</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class TreeInternalsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * The height of the tree: the number of NODES on the longest path from the root down to a leaf.
     * An empty tree has height 0, a single node has height 1.
     *
     * <p>Examples:
     * <pre>
     *   height(null)                          → 0
     *   height(node 5)                        → 1
     *   height(5 with left 3 and right 8)     → 2
     *   height(chain 1 → 2 → 3 → 4)           → 4     (a degenerate tree)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The height of a node is 1 plus the larger of its children's heights</li>
     * </ul>
     *
     * @param root the root of the tree, may be {@code null}
     * @return the height
     */
    public int height(BstNode root) {
        throw new UnsupportedOperationException("TODO: implement height");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Is the value in the tree? Follow ONE path from the root: go left when the value is smaller than the current
     * node, right when it is larger. Do not visit the whole tree.
     *
     * <p>Examples:
     * <pre>
     *   tree 5 (3, 8):  contains(root, 8)  → true
     *   tree 5 (3, 8):  contains(root, 4)  → false
     *   null tree:      contains(null, 1)  → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A loop is enough; recursion depth would equal the tree height, which can be n for a degenerate tree</li>
     * </ul>
     *
     * @param root  the root of the tree, may be {@code null}
     * @param value the value to look for
     * @return {@code true} if the value is stored in the tree
     */
    public boolean contains(BstNode root, int value) {
        throw new UnsupportedOperationException("TODO: implement contains");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Remove a value and return the root of the resulting tree (it changes when the root itself is removed).
     * A value that is not in the tree leaves the tree unchanged. Cases:
     * <ul>
     *   <li>a LEAF is simply cut off</li>
     *   <li>a node with ONE child is replaced by that child</li>
     *   <li>a node with TWO children takes the value of its IN-ORDER SUCCESSOR (the smallest value of its right
     *       subtree), and that successor node is then removed from the right subtree</li>
     * </ul>
     *
     * <p>Example: tree {@code 5 (3 (2, 4), 8 (7, 9))}, {@code remove(root, 5)} → new root 7; its left subtree is unchanged
     * and its right subtree is {@code 8 (-, 9)}.
     *
     * <p>Hints:
     * <ul>
     *   <li>Recursion works well: {@code node.left = remove(node.left, value)} re-attaches the (possibly new) subtree root</li>
     *   <li>BstNode.value is final: for two children build a NEW node with the successor's value instead of changing the old one</li>
     * </ul>
     *
     * @param root  the root of the tree, may be {@code null}
     * @param value the value to remove
     * @return the root of the tree without the value
     */
    public BstNode remove(BstNode root, int value) {
        throw new UnsupportedOperationException("TODO: implement remove");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Is this a valid binary search tree? EVERY value in a node's left subtree must be smaller than the node,
     * and EVERY value in its right subtree larger — not only the direct children. Duplicates are invalid.
     * An empty tree is valid.
     *
     * <p>Examples:
     * <pre>
     *   5 (3, 8)                       → true
     *   5 (6, 8)                       → false   (left child is larger)
     *   5 (3, 8 (4, 9))                → false   (4 is in the right subtree of 5 but smaller than 5)
     *   5 (5, -)                       → false   (duplicate)
     *   null                           → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Checking each node only against its direct children misses the third example</li>
     *   <li>Pass down the open interval (min, max) every value in a subtree must lie in</li>
     *   <li>{@code Integer.MIN_VALUE}/{@code MAX_VALUE} are valid values — use {@code long} bounds or {@code Integer} with null</li>
     * </ul>
     *
     * @param root the root of the tree, may be {@code null}
     * @return {@code true} if the tree satisfies the search-tree property everywhere
     */
    public boolean isValidBst(BstNode root) {
        throw new UnsupportedOperationException("TODO: implement isValidBst");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * The k-th smallest value of a valid BST ({@code k = 1} is the minimum). Visit the nodes IN ORDER and stop as soon as
     * the k-th one is reached.
     *
     * <p>Examples:
     * <pre>
     *   tree 5 (3 (2, 4), 8 (7, 9)):  kthSmallest(root, 1) → 2
     *   tree 5 (3 (2, 4), 8 (7, 9)):  kthSmallest(root, 4) → 5
     *   tree 5 (3 (2, 4), 8 (7, 9)):  kthSmallest(root, 7) → 9
     *   tree with 3 nodes:            kthSmallest(root, 4) → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>In-order = left subtree, node, right subtree; count each visited node</li>
     * </ul>
     *
     * @param root the root of a valid BST, may be {@code null}
     * @param k    the rank, at least 1
     * @return the k-th smallest value
     * @throws IllegalArgumentException if {@code k} is less than 1 or larger than the number of nodes
     */
    public int kthSmallest(BstNode root, int k) {
        throw new UnsupportedOperationException("TODO: implement kthSmallest");
    }
}

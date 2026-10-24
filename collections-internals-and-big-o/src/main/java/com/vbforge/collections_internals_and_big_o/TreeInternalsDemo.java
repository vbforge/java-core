package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.model.BstNode;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Demonstrates why {@link TreeMap}/{@code TreeSet} are built on a SELF-BALANCING tree (a red-black tree)
 * and not on a plain binary search tree.
 *
 * <p>A plain BST ({@link BstNode}) keeps smaller values on the left and larger values on the right, so a lookup
 * follows one path from the root. The cost of that path is the HEIGHT of the tree — and the height depends on the
 * INSERTION ORDER: shuffled input gives a bushy tree (height about log n), sorted input gives a chain (height n).
 *
 * <p>A red-black tree repairs its shape with rotations after every change and guarantees a height of at most
 * {@code 2 * log2(n + 1)}. The demos do not show the rotations; they MEASURE the effect by counting comparisons.
 *
 * <p>Complexity: plain BST — average O(log n), worst case O(n); {@code TreeMap}/{@code TreeSet} — always
 * O(log n) for {@code put}, {@code get}, {@code remove}, {@code floorKey}, {@code firstKey}.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code TreeInternalsDemoTest}
 * can verify every claim.
 */
public class TreeInternalsDemo {

    // ── 1. Insert and in-order traversal ──────────────────────────────────────
    /**
     * Inserts the values one by one into a plain BST and returns the root ({@code null} for no values).
     * A value that is already present is ignored — like in a {@code TreeSet}.
     */
    public static BstNode buildDemo(int... values) {
        BstNode root = null;
        for (int value : values) {
            if (root == null) {
                root = new BstNode(value);
            } else {
                insertCounting(root, value);
            }
        }
        return root;
    }

    /**
     * An in-order traversal (left subtree, node, right subtree) visits the values in ASCENDING order.
     * That is exactly how a {@code TreeSet} iterates.
     */
    public static List<Integer> inOrderDemo(BstNode root) {
        List<Integer> values = new ArrayList<>();
        collectInOrder(root, values);
        return values;
    }

    private static void collectInOrder(BstNode node, List<Integer> values) {
        if (node == null) {
            return;
        }
        collectInOrder(node.left, values);
        values.add(node.value);
        collectInOrder(node.right, values);
    }


    // ── 2. The shape depends on the insertion order ───────────────────────────
    /**
     * For every inserted value: how many comparisons did it take to find its place? That equals the depth of the new
     * node (the first value goes into the empty tree for free, depth 0). Sorted input: 0, 1, 2, 3, ... — a chain.
     * "Median first" input: the depths stay small — a bushy tree. Height = largest depth + 1.
     */
    public static List<Integer> insertDepthsDemo(int... values) {
        List<Integer> depths = new ArrayList<>();
        BstNode root = null;
        for (int value : values) {
            if (root == null) {
                root = new BstNode(value);
                depths.add(0);
            } else {
                depths.add(insertCounting(root, value));
            }
        }
        return depths;
    }

    /** Inserts into an existing tree and returns the number of comparisons performed. */
    private static int insertCounting(BstNode root, int value) {
        int comparisons = 0;
        BstNode current = root;
        while (true) {
            comparisons++;
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new BstNode(value);
                    break;
                }
                current = current.left;
            } else if (value > current.value) {
                if (current.right == null) {
                    current.right = new BstNode(value);
                    break;
                }
                current = current.right;
            } else {
                break;   // duplicate: nothing to insert
            }
        }
        return comparisons;
    }


    // ── 3. The cost of a degenerate tree ──────────────────────────────────────
    /**
     * The total number of comparisons needed to build the tree. For n SORTED values it is
     * {@code 0 + 1 + 2 + ... + (n - 1) = n (n - 1) / 2} — quadratic. A plain BST degrades into a linked list.
     */
    public static long totalComparisonsDemo(int... values) {
        long total = 0;
        for (int depth : insertDepthsDemo(values)) {
            total += depth;
        }
        return total;
    }


    // ── 4. TreeMap stays balanced ─────────────────────────────────────────────
    /**
     * The same worst-case input — keys 1..n in ascending order — put into a real {@code TreeMap} with a comparator that
     * COUNTS its calls. Because the red-black tree rebalances itself, the total stays near {@code n log n}
     * (provably below {@code n * 2 * log2(n + 1)}), not near {@code n²}.
     */
    public static long treeMapTotalComparisonsDemo(int n) {
        long[] count = {0};
        TreeMap<Integer, Integer> map = new TreeMap<>((a, b) -> {
            count[0]++;
            return Integer.compare(a, b);
        });
        for (int i = 1; i <= n; i++) {
            map.put(i, i);
        }
        return count[0];
    }

    /** The most comparisons a SINGLE {@code put} needed — bounded by the guaranteed height {@code 2 * log2(n + 1)}. */
    public static long treeMapMaxComparisonsPerPutDemo(int n) {
        long[] count = {0};
        TreeMap<Integer, Integer> map = new TreeMap<>((a, b) -> {
            count[0]++;
            return Integer.compare(a, b);
        });
        long max = 0;
        for (int i = 1; i <= n; i++) {
            long before = count[0];
            map.put(i, i);
            max = Math.max(max, count[0] - before);
        }
        return max;
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. insert + in-order ===");
        System.out.println(inOrderDemo(buildDemo(5, 3, 8, 1, 4, 5, 3)));

        System.out.println("---");
        System.out.println("=== 2. shape depends on insertion order ===");
        System.out.println("sorted 1..7      : depths " + insertDepthsDemo(1, 2, 3, 4, 5, 6, 7));
        System.out.println("median first     : depths " + insertDepthsDemo(4, 2, 6, 1, 3, 5, 7));

        System.out.println("---");
        System.out.println("=== 3. degenerate tree ===");
        int[] sorted = new int[1000];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = i;
        }
        System.out.println("plain BST, 1000 sorted keys: " + totalComparisonsDemo(sorted) + " comparisons");

        System.out.println("---");
        System.out.println("=== 4. TreeMap stays balanced ===");
        System.out.println("TreeMap,   1000 sorted keys: " + treeMapTotalComparisonsDemo(1000) + " comparisons");
        System.out.println("worst single put           : " + treeMapMaxComparisonsPerPutDemo(1000) + " comparisons");
    }
}

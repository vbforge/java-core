package com.vbforge.practice.map.practice_9_simple_bst;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Unbalanced binary search tree — recursive tree operations.
 *
 * BST property: left child < parent < right child (duplicates ignored here).
 *
 * Time complexity (average / worst — skewed tree):
 *   insert:  O(log n) / O(n)
 *   search:  O(log n) / O(n)
 *   delete:  O(log n) / O(n)   (delete not implemented in this minimal version)
 *
 * In-order traversal of a BST yields a sorted sequence.
 *
 * Balanced variants (TreeMap uses Red-Black): guaranteed O(log n) worst case.
 */
public class SimpleBSTDemo {

    public static void main(String[] args) {
        SimpleBST bst = new SimpleBST();
        for (int v : new int[]{5, 3, 7, 1, 4, 6, 9}) bst.insert(v);
        System.out.println("In-order (sorted): " + bst.inOrder());
        System.out.println("Min: " + bst.min() + ", Max: " + bst.max());
        System.out.println("Height: " + bst.height());
        System.out.println("contains(4): " + bst.contains(4));
        System.out.println("contains(8): " + bst.contains(8));
    }

    static class SimpleBST {

        private static class Node {
            int  value;
            Node left, right;

            Node(int value) { this.value = value; }
        }

        private Node root;

        public void insert(int value) {
            root = insertRec(root, value);
        }

        private Node insertRec(Node node, int value) {
            if (node == null) return new Node(value);
            if      (value < node.value) node.left  = insertRec(node.left,  value);
            else if (value > node.value) node.right = insertRec(node.right, value);
            // equal: ignore duplicate
            return node;
        }

        public boolean contains(int value) {
            return containsRec(root, value);
        }

        private boolean containsRec(Node node, int value) {
            if (node == null) return false;
            if (value == node.value) return true;
            return value < node.value
                ? containsRec(node.left,  value)
                : containsRec(node.right, value);
        }

        // In-order traversal: left -> root -> right -> yields sorted order
        public List<Integer> inOrder() {
            List<Integer> result = new ArrayList<>();
            inOrderRec(root, result);
            return result;
        }

        private void inOrderRec(Node node, List<Integer> result) {
            if (node == null) return;
            inOrderRec(node.left,  result);
            result.add(node.value);
            inOrderRec(node.right, result);
        }

        // Min value is the leftmost node
        public int min() {
            if (root == null) throw new NoSuchElementException("BST is empty");
            Node curr = root;
            while (curr.left != null) curr = curr.left;
            return curr.value;
        }

        // Max value is the rightmost node
        public int max() {
            if (root == null) throw new NoSuchElementException("BST is empty");
            Node curr = root;
            while (curr.right != null) curr = curr.right;
            return curr.value;
        }

        // Tree height (depth of deepest leaf), recursive
        public int height() {
            return heightRec(root);
        }

        private int heightRec(Node node) {
            if (node == null) return 0;
            return 1 + Math.max(heightRec(node.left), heightRec(node.right));
        }
    }
}

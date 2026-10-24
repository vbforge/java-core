package com.vbforge.collections_internals_and_big_o.model;

/**
 * A node of a plain (NOT self-balancing) binary search tree of ints.
 * Everything in the {@code left} subtree is smaller than {@code value}, everything in the
 * {@code right} subtree is larger. {@code left}/{@code right} are public and mutable on purpose, so
 * that demos, tasks and tests can wire trees together by hand.
 */
public final class BstNode {

    public final int value;
    public BstNode left;
    public BstNode right;

    public BstNode(int value) {
        this.value = value;
    }

    public BstNode(int value, BstNode left, BstNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}

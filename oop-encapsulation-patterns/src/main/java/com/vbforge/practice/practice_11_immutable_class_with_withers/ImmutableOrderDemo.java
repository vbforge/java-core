package com.vbforge.practice.practice_11_immutable_class_with_withers;

import java.util.ArrayList;
import java.util.List;

public class ImmutableOrderDemo {

    public static void main(String[] args) {
        System.out.println("=== Immutability: ImmutableOrder ===\n");

        List<String> itemList = new ArrayList<>();
        itemList.add("Keyboard");
        itemList.add("Mouse");

        ImmutableOrder order = new ImmutableOrder("ORD-001", "Alice", itemList, 125.49);
        System.out.println("Created: " + order);

        System.out.println("\n-- Defensive copy: changing the original list does not affect the order --");
        itemList.add("Monitor");
        System.out.println("Original list now: " + itemList);
        System.out.println("Order items still: " + order.getItems());

        System.out.println("\n-- The getter exposes only an unmodifiable list --");
        try {
            order.getItems().add("Webcam");
        } catch (UnsupportedOperationException e) {
            System.out.println("getItems().add(...) -> UnsupportedOperationException");
        }

        System.out.println("\n-- 'Wither' methods return new instances --");
        ImmutableOrder discounted = order.withTotalAmount(99.99);
        System.out.println("original:   " + order);
        System.out.println("discounted: " + discounted);
        System.out.println("Same object? " + (order == discounted));
    }
}

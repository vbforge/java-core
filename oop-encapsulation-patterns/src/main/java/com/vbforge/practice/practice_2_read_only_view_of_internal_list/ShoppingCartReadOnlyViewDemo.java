package com.vbforge.practice.practice_2_read_only_view_of_internal_list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exposing an internal list without giving callers write access.
 *
 * ShoppingCart keeps its items private and returns Collections.unmodifiableList(items):
 * callers can read and iterate, but add/remove throw UnsupportedOperationException.
 */
public class ShoppingCartReadOnlyViewDemo {

    static class OrderItem {
        private final String name;
        private final double price;

        public OrderItem(String name, double price) {
            this.name  = name;
            this.price = price;
        }

        public String getName()  { return name;  }
        public double getPrice() { return price; }
    }

    static class ShoppingCart {
        private final List<OrderItem> items = new ArrayList<>();

        public void addItem(OrderItem item) { items.add(item); }

        public double total() {
            return items.stream().mapToDouble(OrderItem::getPrice).sum();
        }

        public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
        public int size() { return items.size(); }
    }

    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new OrderItem("Java Book", 45.99));
        cart.addItem(new OrderItem("Keyboard",  89.00));
        cart.addItem(new OrderItem("Mouse",     35.50));
        System.out.println("Items in cart: " + cart.size());
        System.out.printf("Total: $%.2f%n", cart.total());

        try {
            cart.getItems().add(new OrderItem("Sneaky", 0.0));   // write through the view
        } catch (UnsupportedOperationException e) {
            System.out.println("getItems() is read-only: UnsupportedOperationException");
        }
        System.out.println("Items in cart after the attempt: " + cart.size());
    }
}

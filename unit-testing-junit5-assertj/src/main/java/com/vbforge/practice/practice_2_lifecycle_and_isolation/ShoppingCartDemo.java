package com.vbforge.practice.practice_2_lifecycle_and_isolation;

public class ShoppingCartDemo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.add("book");
        cart.add("pen");
        System.out.println(cart.items() + " size=" + cart.size());
    }
}

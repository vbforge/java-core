package com.vbforge.practice.practice_7_nested_tests_state_machine;

public class OrderDemo {
    public static void main(String[] args) {
        Order order = new Order();
        order.pay();
        order.ship();
        order.deliver();
        System.out.println(order.history());   // [NEW, PAID, SHIPPED, DELIVERED]
    }
}

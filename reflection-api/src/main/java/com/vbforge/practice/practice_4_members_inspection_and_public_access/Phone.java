package com.vbforge.practice.practice_4_members_inspection_and_public_access;

/** Target with a public field, a private field, a no-arg method and a method with a parameter. */
public class Phone {

    public String model;
    private int price;

    public void info() {
        System.out.println("model: " + model + "; price: " + price);
    }

    public int priceDiscount(int price) {
        if (price <= 3000) {
            price = price + 10;
        } else if (price >= 3000) {
            price = (int) ((double) price * 0.15);
        }
        return price;
    }
}

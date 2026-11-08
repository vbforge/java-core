package com.vbforge.practice.practice_6_boundary_value_analysis;

public class TicketPriceDemo {
    public static void main(String[] args) {
        for (int age : new int[] {2, 3, 12, 13, 64, 65}) {
            System.out.println(age + " -> " + TicketPriceCalculator.categoryFor(age)
                    + " " + TicketPriceCalculator.priceInCents(age));
        }
    }
}

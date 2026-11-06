package com.vbforge.practice.practice_14_records;

/** A record cannot extend a class, but it CAN implement interfaces. */
public class RecordImplementsInterfaceDemo {

    public static void main(String[] args) {
        Money price = new Money(99.99, "usd");
        Money tax   = new Money(8.50,  "USD");
        Money total = price.add(tax);

        Describable d = total;        // used through the interface type
        System.out.println("price: " + price.describe());
        System.out.println("tax:   " + tax.describe());
        System.out.println("total: " + d.describe());
    }
}

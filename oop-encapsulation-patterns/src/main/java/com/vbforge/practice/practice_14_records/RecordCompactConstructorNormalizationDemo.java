package com.vbforge.practice.practice_14_records;

/** A compact constructor can also NORMALISE: reassign the parameter before the fields are set. */
public class RecordCompactConstructorNormalizationDemo {

    public static void main(String[] args) {
        Money price = new Money(99.99, "usd");
        System.out.println("price: " + price);                       // currency stored as USD
        System.out.println("equals Money(99.99, \"USD\"): " + price.equals(new Money(99.99, "USD")));
    }
}

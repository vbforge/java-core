package com.vbforge.practice.practice_10_when_not_to_mock;

import java.util.Arrays;
import java.util.List;

/** A double of a value object adds nothing: you end up testing the double's canned data. */
public class ValueObjectsNotMockedDemo {

    /** "Mock" of a value object: every amount defaults to zero. */
    static class ZeroMoney extends Money {
        ZeroMoney() { super(0); }
        @Override public long cents() { return 0; }
    }

    static Money total(List<Money> items) {
        Money sum = new Money(0);
        for (Money item : items) {
            sum = sum.plus(item);
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("real values : " + total(Arrays.asList(new Money(100), new Money(250))));          // 350c
        System.out.println("doubled     : " + total(Arrays.<Money>asList(new ZeroMoney(), new ZeroMoney()))
                + "  <- the test would only prove what the double was told");
        System.out.println("equal by value: " + new Money(100).equals(new Money(100)));
    }
}

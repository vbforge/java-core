package com.vbforge.practice.practice_5_enum_as_field;

/** An enum used as a typed field: a FreshJuice can only be SMALL, MEDIUM or LARGE. */
public class FreshJuiceDemo {

    public static void main(String[] args) {
        FreshJuice juice = new FreshJuice(FreshJuice.FreshJuiceSize.LARGE);
        System.out.println(juice.getDescription());
    }
}

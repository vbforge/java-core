package com.vbforge.practice.practice_7_default_equals_is_identity;

/** A class that does not override equals() inherits Object.equals(), which is identity ({@code ==}). */
public class DefaultEqualsIsIdentityDemo {

    static final class Building {
        final int levels;
        Building(int levels) { this.levels = levels; }
        // no equals / hashCode override
    }

    public static void main(String[] args) {
        Building b1 = new Building(2);
        Building b2 = new Building(2);
        Building alias = b1;

        System.out.println("b1 == b2:       " + (b1 == b2));
        System.out.println("b1.equals(b2):  " + b1.equals(b2) + "  <- default equals -> identity");
        System.out.println("b1.equals(alias): " + b1.equals(alias) + "  <- same object");
    }
}

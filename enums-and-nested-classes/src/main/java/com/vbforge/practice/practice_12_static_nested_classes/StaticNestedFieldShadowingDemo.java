package com.vbforge.practice.practice_12_static_nested_classes;

/** A static nested class with its own field of the same name as the outer's instance field: the nested one wins, and the outer's x is unreachable without an outer object. */
public class StaticNestedFieldShadowingDemo {

    public static void main(String[] args) {
        ShadowOuter.CustomStatic objStatic = new ShadowOuter.CustomStatic();
        objStatic.printX();   // x is 8 (the outer x = 7 is not reachable here)
    }
}

class ShadowOuter {
    private int x = 7;

    static class CustomStatic {
        private int x = 8;

        public void printX() {
            System.out.println("x is " + x);
        }
    }
}

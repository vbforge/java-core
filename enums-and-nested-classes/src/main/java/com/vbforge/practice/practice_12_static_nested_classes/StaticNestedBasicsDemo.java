package com.vbforge.practice.practice_12_static_nested_classes;

/** A static nested class is created through the outer NAME, without any outer instance, even from another top-level class. */
public class StaticNestedBasicsDemo {

    public static void main(String[] args) {
        BasicOuter.Nested nested = new BasicOuter.Nested();
        nested.methodPrint();
    }
}

class BasicOuter {
    static class Nested {
        public void methodPrint() {
            System.out.println("Nested.methodPrint()");
        }
    }
}

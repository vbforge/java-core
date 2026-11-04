package com.vbforge.practice.practice_8_instanceof_operator;

/**
 * `x instanceof T` → boolean.
 *   - false if x is null (never throws NullPointerException)
 *   - compile error if the cast can never succeed (incompatible types)
 *
 * Pattern-matching instanceof (`if (x instanceof Foo f)`) is a language evolution
 * of this operator and belongs to oop-inheritance-and-polymorphism / sealed-classes-pattern-matching.
 */
public class InstanceofBasicsDemo {

    public static void main(String[] args) {
        Object name = "EPAM";
        System.out.println("name instanceof String  : " + (name instanceof String));
        System.out.println("name instanceof Object  : " + (name instanceof Object));
        System.out.println("name instanceof Integer : " + (name instanceof Integer));

        Object nothing = null;
        System.out.println("null instanceof Object  : " + (nothing instanceof Object)); // false — safe

        Object number = 42;
        System.out.println("42 instanceof Number    : " + (number instanceof Number));
    }
}

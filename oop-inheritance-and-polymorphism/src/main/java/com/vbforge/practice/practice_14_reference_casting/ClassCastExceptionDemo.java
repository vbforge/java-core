package com.vbforge.practice.practice_14_reference_casting;

/**
 * ClassCastException - an unguarded downcast to the wrong subtype.
 *
 * The code compiles because Cat and Dog are both Animals, but at runtime the
 * object is a Cat and cannot be treated as a Dog.
 */
public class ClassCastExceptionDemo {

    static class Animal {}
    static class Dog extends Animal {}
    static class Cat extends Animal {}

    public static void main(String[] args) {
        Animal actualCat = new Cat();
        try {
            Dog wrong = (Dog) actualCat;   // compiles, fails at runtime
            System.out.println("never reached: " + wrong);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: " + e.getMessage());
        }

        Animal plainAnimal = new Animal();
        try {
            Dog wrong = (Dog) plainAnimal;   // a plain Animal is not a Dog either
            System.out.println("never reached: " + wrong);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: a plain Animal cannot be cast to Dog");
        }
    }
}

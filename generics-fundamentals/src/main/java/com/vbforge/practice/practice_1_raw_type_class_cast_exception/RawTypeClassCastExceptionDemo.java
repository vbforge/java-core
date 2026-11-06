package com.vbforge.practice.practice_1_raw_type_class_cast_exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Why generics matter: a raw List accepts anything, so the mistake surfaces
 * only at runtime as a ClassCastException. A List<Animal> rejects the same
 * mistake at compile time.
 */
public class RawTypeClassCastExceptionDemo {

    static class Animal {
        public void makeSound() { System.out.println("Animal sound!"); }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {
        try {
            List rawList = new ArrayList();           // raw type, no generic information
            rawList.add("I'm a String");              // compiles fine

            for (Object obj : rawList) {
                Animal animal = (Animal) obj;         // ClassCastException: String is not an Animal
                animal.makeSound();
            }
        } catch (ClassCastException e) {
            System.out.println("Caught: " + e.getMessage());
            System.out.println("Lesson: List<Animal> would have caught this at COMPILE time.");
        }

        List<Animal> typed = new ArrayList<>();
        // typed.add("I'm a String");                 // compile error: String cannot be converted to Animal
        typed.add(new Animal());
        typed.get(0).makeSound();
    }
}

package com.vbforge.practice.practice_3_inheritance_basics;

/**
 * IS-A: a subclass IS-A superclass (Cat IS-A Animal, Dog IS-A Animal).
 *
 * A subclass object can be held by a superclass reference, and instanceof
 * confirms the IS-A relationship at runtime.
 */
public class IsADemo {

    static class Animal {}
    static class Cat extends Animal {}
    static class Dog extends Animal {}

    public static void main(String[] args) {
        Animal cat = new Cat();   // Cat IS-A Animal
        Animal dog = new Dog();   // Dog IS-A Animal

        System.out.println("cat is Animal: " + (cat instanceof Animal));
        System.out.println("dog is Animal: " + (dog instanceof Animal));
        System.out.println("cat is Cat:    " + (cat instanceof Cat));
        System.out.println("cat is Dog:    " + (cat instanceof Dog));
    }
}

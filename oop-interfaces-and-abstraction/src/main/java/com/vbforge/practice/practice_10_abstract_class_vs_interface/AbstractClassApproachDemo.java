package com.vbforge.practice.practice_10_abstract_class_vs_interface;

/**
 * Abstraction through an abstract class: one abstract method the subclass must write,
 * one concrete method it inherits as is (shared code).
 */
public class AbstractClassApproachDemo {

    static abstract class AbstractAnimal {
        abstract void sound();

        void sleep() {
            System.out.println("This animal sleeps.");
        }
    }

    static class AbstractDog extends AbstractAnimal {
        @Override void sound() { System.out.println("Dog barks."); }
    }

    public static void main(String[] args) {
        AbstractDog myDog = new AbstractDog();
        myDog.sound();
        myDog.sleep();
    }
}

package com.vbforge.practice.practice_10_abstract_class_vs_interface;

/**
 * Abstraction through an interface: a pure contract, the implementer writes every method.
 * Compare with AbstractClassApproachDemo, where sleep() was inherited.
 */
public class InterfaceApproachDemo {

    interface AnimalContract {
        void sound();
        void sleep();
    }

    static class InterfaceDog implements AnimalContract {
        @Override public void sound() { System.out.println("Dog barks.");  }
        @Override public void sleep() { System.out.println("Dog sleeps."); }
    }

    public static void main(String[] args) {
        InterfaceDog myDog = new InterfaceDog();
        myDog.sound();
        myDog.sleep();
    }
}

package com.vbforge.practice.practice_5_default_and_static_methods;

/**
 * A default method can orchestrate the interface's abstract methods (and other defaults).
 * makeTravel() calls blastOff(), fly() and landing(); the implementer only fills the abstract ones.
 */
public class DefaultCallsAbstractMethodDemo {

    interface FlyingMachine {
        void blastOff();
        void landing();

        default void makeTravel() {
            blastOff();
            fly();          // fly() is itself a default method, so it can be overridden
            landing();
        }

        default void fly() {
            System.out.println("Machine is flying!");
        }
    }

    static class Plane implements FlyingMachine {
        @Override public void blastOff() { System.out.println("The plane is blasting off"); }
        @Override public void landing()  { System.out.println("The plane is landing");      }
    }

    public static void main(String[] args) {
        FlyingMachine plane = new Plane();
        plane.makeTravel();
    }
}

package com.vbforge.practice.practice_7_anonymous_class_basics;

/** An anonymous subclass of an abstract class must implement every abstract method. */
public class AnonymousAbstractClassDemo {

    public static void main(String[] args) {
        Computer c = new Computer() {
            @Override
            public void powerOFF() {
                System.out.println("switch OFF");
            }

            @Override
            public void powerON() {
                System.out.println("switch On");
            }
        };
        c.powerON();
        c.powerOFF();
    }
}

abstract class Computer {
    public abstract void powerON();
    public abstract void powerOFF();
}

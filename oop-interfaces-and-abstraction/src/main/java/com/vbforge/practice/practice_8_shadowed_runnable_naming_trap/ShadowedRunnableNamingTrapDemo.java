package com.vbforge.practice.practice_8_shadowed_runnable_naming_trap;

/**
 * Naming trap: a locally declared interface called Runnable SHADOWS java.lang.Runnable
 * inside this class. A class implementing the local Runnable is not a java.lang.Runnable,
 * so it cannot be handed to new Thread(...).
 */
public class ShadowedRunnableNamingTrapDemo {

    /** Shadows java.lang.Runnable for every simple-name use of Runnable in this class. */
    interface Runnable {
        void run();
    }

    static class Person implements Runnable {
        @Override
        public void run() {
            System.out.println("Person runs");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Person person = new Person();
        person.run();

        Object o = person;
        System.out.println("instanceof local Runnable:      " + (o instanceof Runnable));
        System.out.println("instanceof java.lang.Runnable:  " + (o instanceof java.lang.Runnable));

        // new Thread(person);                          // compile error: Person is not a java.lang.Runnable
        // new Thread((java.lang.Runnable) o).start();  // compiles, ClassCastException at runtime
        Thread thread = new Thread(person::run);       // a method reference builds a real java.lang.Runnable
        thread.start();
        thread.join();
    }
}

package com.vbforge.practice.practice_3_object_tostring;

/**
 * Overriding {@code toString()}: concise, human-readable, the same text for the same state.
 * It is called implicitly by println / string concatenation / logging / debuggers.
 *
 * Pitfall: calling {@code obj.toString()} and discarding the result prints nothing.
 */
public class ToStringOverrideDemo {

    static final class Person {
        private final int age = 19;
        private final String name = "Michael";

        @Override
        public String toString() {
            return name + " is " + age + " years old";
        }
    }

    public static void main(String[] args) {
        Person p = new Person();

        System.out.println("-- override returns custom text --");
        System.out.println(p);
        System.out.println("explicit: " + p.toString());

        System.out.println("\n-- pitfall: calling toString() but discarding result --");
        p.toString();   // nothing printed
        System.out.println("(line above printed nothing - result discarded)");
    }
}

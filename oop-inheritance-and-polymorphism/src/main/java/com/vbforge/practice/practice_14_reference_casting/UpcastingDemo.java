package com.vbforge.practice.practice_14_reference_casting;

/**
 * Upcasting (implicit) - subtype -> supertype. Always safe.
 *
 *   - the object keeps its real type, so overridden methods still dispatch to it
 *   - the reference type limits which methods the compiler lets you call
 *   - the reverse direction (parent object -> child reference) is not implicit
 */
public class UpcastingDemo {

    static class Animal {
        final String name;
        Animal(String name) { this.name = name; }
        String speak() { return name + " makes a sound"; }
    }

    static class Dog extends Animal {
        Dog(String name) { super(name); }
        @Override String speak() { return name + " barks"; }
        String fetch() { return name + " fetches the ball"; }
    }

    static class Cat extends Animal {
        Cat(String name) { super(name); }
        @Override String speak() { return name + " meows"; }
    }

    public static void main(String[] args) {
        Animal a1 = new Dog("Rex");
        Animal a2 = new Cat("Whiskers");
        System.out.println(a1.speak());   // Dog's override
        System.out.println(a2.speak());   // Cat's override
        // a1.fetch();                    // compile error: Animal does not declare fetch()
        // Dog d = new Animal("Generic"); // compile error: a parent object cannot be assigned to a child reference
    }
}

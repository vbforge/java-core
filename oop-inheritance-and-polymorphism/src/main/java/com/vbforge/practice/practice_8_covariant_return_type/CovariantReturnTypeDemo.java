package com.vbforge.practice.practice_8_covariant_return_type;

/**
 * Covariant return type rule.
 *
 * An overriding method's return type must be the SAME as, or a SUBTYPE of,
 * the overridden method's return type.
 *
 *   Parent: void methodA()    -> child must return void (void has no subtype)
 *   Parent: Animal adopt()    -> child may return Dog (Dog IS-A Animal)
 *   Parent: Number methodA()  -> child may return Integer (Integer IS-A Number)
 */
public class CovariantReturnTypeDemo {

    // --- void cannot be made covariant ---
    static class ClassA {
        public void methodA() { System.out.println("methodA"); }
    }
    static class ClassB extends ClassA {
        // @Override public String methodA() { return "methodB"; }  // compile error: cannot override void with String
    }

    // --- a legal covariant override ---
    static class Animal {}
    static class Dog extends Animal {}

    static class Shelter {
        Animal adopt() { return new Animal(); }
    }
    static class DogShelter extends Shelter {
        @Override
        Dog adopt() { return new Dog(); }   // narrower return type: legal
    }

    public static void main(String[] args) {
        new ClassB().methodA();

        System.out.println("\n-- covariant override --");
        Shelter shelter = new DogShelter();
        Animal a = shelter.adopt();                          // static type: Animal
        System.out.println("via Shelter reference:    " + a.getClass().getSimpleName());

        DogShelter dogShelter = new DogShelter();
        Dog d = dogShelter.adopt();                          // static type: Dog - no cast needed
        System.out.println("via DogShelter reference: " + d.getClass().getSimpleName());
    }
}

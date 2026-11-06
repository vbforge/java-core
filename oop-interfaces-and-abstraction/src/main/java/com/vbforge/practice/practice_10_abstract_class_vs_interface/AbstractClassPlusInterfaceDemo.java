package com.vbforge.practice.practice_10_abstract_class_vs_interface;

/**
 * Abstract class + interface in one class.
 *   Abstract class = what something IS      (Cat IS-A Animal, Dog IS-A Animal)
 *   Interface      = what something CAN DO  (Dog CAN-DO Soundable, Cat cannot)
 * Each reference type shows only its own methods.
 */
public class AbstractClassPlusInterfaceDemo {

    interface Soundable {
        void makeSound();
    }

    static abstract class CombinedAnimal {
        private final String name;

        CombinedAnimal(String name) { this.name = name; }

        String getName() { return name; }

        void eat() { System.out.println(name + " is eating."); }

        abstract void speak();
    }

    static class CombinedCat extends CombinedAnimal {
        CombinedCat() { super("Cat"); }

        @Override void speak() { System.out.println("Cat: Meow!"); }
    }

    static class CombinedDog extends CombinedAnimal implements Soundable {
        CombinedDog() { super("Dog"); }

        @Override void speak() { System.out.println("Dog: Woof!"); }

        @Override public void makeSound() {
            System.out.println("Dog via Soundable interface: *bark bark*");
        }
    }

    public static void main(String[] args) {
        CombinedCat cat = new CombinedCat();
        cat.eat();
        cat.speak();
        // cat.makeSound();   // compile error: Cat doesn't implement Soundable

        CombinedDog dog = new CombinedDog();
        dog.eat();
        dog.speak();
        dog.makeSound();

        Soundable viaInterface = new CombinedDog();
        viaInterface.makeSound();
        // viaInterface.speak();   // compile error: not in Soundable

        CombinedAnimal viaAbstract = new CombinedDog();
        viaAbstract.eat();
        viaAbstract.speak();
        // viaAbstract.makeSound();   // compile error: not in CombinedAnimal
    }
}

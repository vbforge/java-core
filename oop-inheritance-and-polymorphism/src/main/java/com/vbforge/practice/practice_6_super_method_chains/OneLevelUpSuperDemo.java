package com.vbforge.practice.practice_6_super_method_chains;

/**
 * super.method() reaches exactly ONE level up.
 *
 * Hierarchy: Animal -> Cat -> BritishCat, and Animal -> Dog.
 *   - Dog.superEat()      calls super.eat() from a helper, so the overridden Animal.eat() is still reachable
 *   - BritishCat.eat()    calls super.eat() -> Cat.eat() (NOT Animal.eat(), because Cat overrides it)
 */
public class OneLevelUpSuperDemo {

    static class Animal {
        public void eat()   { System.out.println("All animals eat"); }
        public void sound() { System.out.println("All animals sound"); }
    }

    static class Dog extends Animal {
        @Override
        public void eat() { System.out.println("Dog eat"); }

        public void superEat() { super.eat(); }   // Animal.eat()
    }

    static class Cat extends Animal {
        public void jump() { System.out.println("cat can jumping"); }

        @Override
        public void eat() { System.out.println("cat eat special food"); }
    }

    static class BritishCat extends Cat {
        public void runFast() { System.out.println("British very fast running"); }

        @Override
        public void eat() {
            super.eat();   // Cat.eat() - one level up only
        }
    }

    public static void main(String[] args) {
        System.out.println("Dog methods: ");
        Dog dog = new Dog();
        dog.eat();        // Dog's own eat -> overrides Animal.eat()
        dog.superEat();   // helper -> super.eat() -> Animal.eat()
        dog.sound();      // inherited from Animal
        System.out.println();

        System.out.println("BritishCat methods: ");
        BritishCat britishCat = new BritishCat();
        britishCat.runFast();   // BritishCat-only method
        britishCat.eat();       // BritishCat.eat() -> super.eat() -> Cat.eat()
        britishCat.jump();      // inherited from Cat
        britishCat.sound();     // inherited from Animal
    }
}

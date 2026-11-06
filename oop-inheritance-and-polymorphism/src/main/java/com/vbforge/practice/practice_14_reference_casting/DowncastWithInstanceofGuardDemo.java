package com.vbforge.practice.practice_14_reference_casting;

/**
 * Downcasting (explicit) - supertype -> subtype.
 *
 *   - legal at runtime only when the actual object IS an instance of the target type
 *   - guard the cast with instanceof so a wrong type takes the else branch instead of throwing
 *   - a downcast to the real type unlocks the subtype-specific methods
 */
public class DowncastWithInstanceofGuardDemo {

    static class Cat {
        public void move() { System.out.println("Cat move"); }
    }

    static class BritishCat extends Cat {
        @Override
        public void move() { System.out.println("British cat move"); }
    }

    static class PersianCat extends Cat {
        @Override
        public void move() { System.out.println("Persian cat move"); }
    }

    public static void main(String[] args) {
        Cat cat = new BritishCat();   // upcast
        System.out.println("cat actual type: " + cat.getClass().getSimpleName());

        // Downcast that matches the actual type - legal
        BritishCat british = (BritishCat) cat;
        british.move();

        // Downcast that does NOT match - guard it with instanceof
        if (cat instanceof PersianCat) {
            PersianCat persian = (PersianCat) cat;
            persian.move();
        } else {
            System.out.println("Not a PersianCat (instanceof returned false)");
        }
    }
}

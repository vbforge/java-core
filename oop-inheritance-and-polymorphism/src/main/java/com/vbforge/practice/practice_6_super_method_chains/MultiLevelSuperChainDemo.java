package com.vbforge.practice.practice_6_super_method_chains;

/**
 * Multi-level inheritance: WildAnimal -> Bear -> PolarBear.
 *
 *   - every level overrides eat() and calls super.eat(), so one call runs the whole chain
 *   - the protected field hasEaten is set at the top level and visible through all subclasses
 */
public class MultiLevelSuperChainDemo {

    static class WildAnimal {
        protected boolean hasEaten;

        public WildAnimal() {
            hasEaten = false;
        }

        public void eat() {
            System.out.println("The animal is eating...");
            hasEaten = true;
        }
    }

    static class Bear extends WildAnimal {
        public String color;

        public Bear(String c) {
            super();
            color = c;
        }

        @Override
        public void eat() {
            super.eat();
            System.out.println("The bear went fishing before eating.");
        }
    }

    static class PolarBear extends Bear {
        public PolarBear() {
            super("white");
        }

        @Override
        public void eat() {
            super.eat();
        }
    }

    public static void main(String[] args) {
        WildAnimal animal = new WildAnimal();
        WildAnimal bear   = new PolarBear();

        System.out.println("Animal:     hasEaten before = " + animal.hasEaten);
        animal.eat();
        System.out.println("Animal:     hasEaten after  = " + animal.hasEaten);

        System.out.println();
        System.out.println("PolarBear:  hasEaten before = " + bear.hasEaten);
        bear.eat();   // PolarBear -> Bear -> WildAnimal, all three chain
        System.out.println("PolarBear:  hasEaten after  = " + bear.hasEaten);
    }
}

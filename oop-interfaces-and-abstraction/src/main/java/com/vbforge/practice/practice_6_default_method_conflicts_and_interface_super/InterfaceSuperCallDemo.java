package com.vbforge.practice.practice_6_default_method_conflicts_and_interface_super;

/**
 * Interface.super.method(): an override can reuse a DIRECT superinterface's default.
 *
 *   1. SwimmingWithSigns extends Swimming and adds constants + a default swim(String).
 *   2. HumanRunner extends Runner and supplies a default run().
 *   3. Human implements Speakable, HumanRunner and SwimmingWithSigns, overrides the defaults
 *      and delegates to them with SwimmingWithSigns.super / HumanRunner.super.
 *
 * Note that plain super.swim(x) would look in the superCLASS (Object), not in an interface.
 */
public class InterfaceSuperCallDemo {

    interface Swimming {
        void swim();
    }

    interface SwimmingWithSigns extends Swimming {
        String SWIM = "s";      // implicitly public static final
        String DIVE = "d";

        default void swim(String x) {
            if (SWIM.equals(x)) {
                System.out.println("People can swim in water");
            } else if (DIVE.equals(x)) {
                System.out.println("Diving - is a special underwater swim");
            } else {
                System.out.println("People using boat");
            }
        }
    }

    interface Runner {
        void run();
    }

    interface HumanRunner extends Runner {
        @Override
        default void run() {
            System.out.println("People running by humans style");
        }
    }

    interface Speakable {
        void speak();
    }

    static class Human implements Speakable, HumanRunner, SwimmingWithSigns {
        @Override
        public void swim() {
            System.out.println("Human swims");
        }

        @Override
        public void swim(String x) {
            SwimmingWithSigns.super.swim(x);     // explicit call of the interface default
        }

        @Override
        public void run() {
            HumanRunner.super.run();
        }

        @Override
        public void speak() {
            System.out.println("People speaking by many language.");
        }
    }

    public static void main(String[] args) {
        SwimmingWithSigns swimmer = new Human();
        swimmer.swim("s");
        swimmer.swim("d");
        swimmer.swim("boat");

        HumanRunner runner = new Human();
        runner.run();

        Human person = new Human();
        person.speak();
        person.run();
        person.swim();
        person.swim("d");
    }
}

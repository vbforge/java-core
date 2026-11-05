package com.vbforge.practice.practice_8_anonymous_class_members_and_state;

/**
 * Extra members of an anonymous subclass.
 * A: extra state (a private field) is usable because printInfo/isDrive are also overridden on the parent's API.
 * B: an extra method that is NOT on the parent's API cannot be called through a parent-typed reference.
 */
public class AnonymousExtraMembersDemo {

    public static void main(String[] args) {
        sceneExtraState();
        sceneExtraMethodHidden();
    }

    private static void sceneExtraState() {
        Audi obj = new Audi() {
            private boolean isWork;

            @Override
            public void isDrive() {
                this.isWork = true;
            }

            @Override
            public void printInfo() {
                System.out.println(isWork);
            }
        };
        obj.printInfo();   // false
        obj.isDrive();     // set true here
        obj.printInfo();   // true
    }

    private static void sceneExtraMethodHidden() {
        Auto car = new Auto() {
            void drive() {
                System.out.println("Auto.drive()");
            }
        };
        // car.drive();  // <-- drive() is not on Auto's API, so this would not compile

        // 'var' keeps the anonymous type, so the extra method IS reachable (Java 10+)
        var anonymousCar = new Auto() {
            void drive() {
                System.out.println("Auto.drive() via var");
            }
        };
        anonymousCar.drive();
    }
}

class Audi {
    public void printInfo() { }
    public void isDrive() { }
}

class Auto {
}

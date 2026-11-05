package com.vbforge.practice.practice_8_anonymous_class_members_and_state;

/**
 * An anonymous class has no constructor, so an instance initializer block does that job. This one
 * also has its own field z, and reaches the outer private field x and the local variable y.
 */
public class AnonymousInstanceInitializerDemo {

    public static void main(String[] args) {
        Ship ship = new Ship();
        ship.doJob();   // Init block10 10 20
        System.out.println();
    }
}

/**
 * - local variable y (effectively final)
 * - tst: a CustomTest implemented by an anonymous class with its own field z and an initializer block
 * test() reads the private field x of Ship and the local y of doJob(), although it is a method of an
 * anonymous class created inside a method of another class.
 */
class Ship {
    private int x = 10;

    void doJob() {
        final int y = 20;
        CustomTest tst = new CustomTest() {
            private int z = 10;
            {
                System.out.print("Init block");
            }

            @Override
            public void test() {
                System.out.print(x + " " + z + " " + y);
            }
        };
        tst.test();
    }
}

interface CustomTest {
    void test();
}

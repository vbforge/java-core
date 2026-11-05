package com.vbforge.practice.practice_11_inner_classes;

/**
 * The inner class Engine reads and modifies the private field x of its outer class InnerOwnerShip
 * directly. Created from a method of the outer class, or from outside through an outer instance.
 */
public class InnerClassMutatesOuterStateDemo {

    public static void main(String[] args) {
        InnerOwnerShip ship = new InnerOwnerShip();
        ship.testing();   // 21

        InnerOwnerShip.Engine engine = ship.new Engine();
        engine.test();    // 21 (x is set to 20 again, then incremented)
    }
}

class InnerOwnerShip {   // outer
    private int x = 10;

    protected class Engine {   // inner
        public void test() {
            x = 20;   // direct access to the outer field
            x++;
            System.out.println(x);
        }
    }

    public void testing() {   // outer method
        Engine eng = new Engine();
        eng.test();   // calling a method of the inner class through its instance
    }
}

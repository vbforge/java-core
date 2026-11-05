package com.vbforge.practice.practice_11_inner_classes;

/** An inner class instance created and held as a field of its own outer class. */
public class InnerClassFieldDemo {

    String model;

    class Engine {
        private boolean working = false;

        public void work() {
            working = true;
            System.out.println("Engine working");
        }
    }

    Engine engine = new Engine();

    public static void main(String[] args) {
        InnerClassFieldDemo auto = new InnerClassFieldDemo();
        auto.model = "BMW";
        auto.engine.work();
    }
}

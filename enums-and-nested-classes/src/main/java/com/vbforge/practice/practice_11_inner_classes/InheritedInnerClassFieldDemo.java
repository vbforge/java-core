package com.vbforge.practice.practice_11_inner_classes;

/**
 * An abstract base class owns an inner class instance as a field (the "engine"). A subclass inherits
 * the field, so the inner object is reachable through a subclass reference.
 */
public class InheritedInnerClassFieldDemo {

    public static void main(String[] args) {
        SimpleCar bmw1 = new SimpleBMW("x5", "black", 20000);
        bmw1.printAll();
        bmw1.engine.start();
        bmw1.engine.stop();
    }
}

abstract class SimpleCar {

    private String model;
    private String color;
    private int price;

    class Drive {
        private boolean isDrive;

        Drive(boolean isDrive) {
            this.isDrive = isDrive;
        }

        void start() {
            System.out.println("start: " + this.isDrive);
        }

        void stop() {
            System.out.println("stop: " + this.isDrive);
        }
    }

    Drive engine = new Drive(false);

    public SimpleCar(String model, String color, int price) {
        this.model = model;
        this.color = color;
        this.price = price;
    }

    public void printAll() {
        System.out.println(model + "; " + color + "; " + price);
    }
}

class SimpleBMW extends SimpleCar {
    public SimpleBMW(String model, String color, int price) {
        super(model, color, price);
    }
}

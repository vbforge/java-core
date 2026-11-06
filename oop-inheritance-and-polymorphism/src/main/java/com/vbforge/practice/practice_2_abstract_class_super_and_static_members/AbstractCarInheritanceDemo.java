package com.vbforge.practice.practice_2_abstract_class_super_and_static_members;

/**
 * Abstract class with several constructors, a subclass that calls super(...), super.method() and
 * assigns an inherited protected field, implements the abstract method, and adds a static field
 * and a static method.
 */
public class AbstractCarInheritanceDemo {

    public static void main(String[] args) {
        BMW m5 = new BMW("M5", 2702.5f, "grey", false, (short) 230, false);
        m5.printAll();
        m5.howToStartEngine();   // implements the abstract method

        BMW m6 = new BMW("M6", 3002.5f, "white", true, (short) 270, true);
        m6.printAll();

        System.out.println(BMW.number);   // static field, no object needed: 90

        BMW.printTest();   // static method: Test static function
    }
}

abstract class Car {

    private String model;
    protected short speed;
    private float weight;
    private String color;
    private boolean isWork;

    public Car(short speed) {
        this.speed = speed;
    }

    public Car(String model, float weight, String color, boolean isWork) {
        this.model = model;
        this.weight = weight;
        this.color = color;
        this.isWork = isWork;
    }

    public Car() {
        System.out.println("object created".toUpperCase() + "\n");
    }

    public void setAll(String model, float weight, String color, boolean isWork) {
        this.model = model;
        this.weight = weight;
        this.color = color;
        this.isWork = isWork;
    }

    public void printAll() {
        String isWork = this.isWork ? "started" : "not started";
        System.out.println("Car: " + this.model +
                " (speed: " + this.speed +
                "; weight: " + this.weight +
                "; color: " + this.color +
                ") // engine: " + isWork);
    }

    abstract void howToStartEngine();
}

class BMW extends Car {

    private boolean isMPerformance = false;

    public static int number = 90;   // belongs to the class, no object needed

    public BMW(String model, float weight, String color, boolean isWork, short speed, boolean isMPerformance) {
        super(model, weight, color, isWork);
        super.speed = speed;
        this.isMPerformance = isMPerformance;
    }

    @Override
    public void printAll() {
        super.printAll();
        String isMPerformance = this.isMPerformance ? "This car is " : "No, this car isn`t ";
        System.out.println("Car: " + isMPerformance + " M Performance".toUpperCase());
    }

    public static void printTest() {
        System.out.println("Test static function");
    }

    @Override
    void howToStartEngine() {
        System.out.println("Engine started by button");
    }
}

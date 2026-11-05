package com.vbforge.practice.practice_10_anonymous_to_lambda;

/**
 * void-method version: an anonymous subclass of a concrete class that implements the interface,
 * vs a lambda. The lambda's target type must be an interface (CarDriving), not the class Car.
 */
public class AnonymousVsLambdaVoidDemo {

    public static void main(String[] args) {
        Car carAnonymous = new Car() {
            @Override
            public void drive() {
                System.out.println("Car anonymous driving!");
            }
        };
        carAnonymous.drive();   // overridden method

        CarDriving carLambda = () -> System.out.println("Car lambda driving!");
        carLambda.drive();
    }
}

interface CarDriving {
    void drive();
}

class Car implements CarDriving {

    @Override
    public void drive() {
        System.out.println("Car driving...");
    }
}

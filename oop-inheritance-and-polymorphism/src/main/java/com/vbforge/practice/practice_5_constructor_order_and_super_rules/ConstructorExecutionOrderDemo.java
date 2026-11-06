package com.vbforge.practice.practice_5_constructor_order_and_super_rules;

/**
 * Constructor chaining order: the parent constructor body runs first, then the child's.
 * Vehicle and Car print from their constructors, so the order is visible.
 */
public class ConstructorExecutionOrderDemo {

    public static void main(String[] args) {
        Car car = new Car("Toyota", 2022, "Camry");   // "Vehicle ctor" first, then "Car ctor"
        car.showDetails();
    }
}

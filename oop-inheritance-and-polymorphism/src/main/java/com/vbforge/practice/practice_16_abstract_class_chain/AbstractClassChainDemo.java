package com.vbforge.practice.practice_16_abstract_class_chain;

/**
 * Multi-level abstract chain: Vehicle -> Car -> SportCar.
 * An abstract class can extend another abstract class; the unimplemented abstract method
 * (drive) travels down until a concrete class finally implements it.
 */
public class AbstractClassChainDemo {

    static abstract class Vehicle {
        private final String brand;

        Vehicle(String brand) { this.brand = brand; }

        String getBrand() { return brand; }

        public abstract void drive();
    }

    static abstract class Car extends Vehicle {
        Car(String brand) { super(brand); }

        public void refuel() {
            System.out.println(getBrand() + ": refuelling at the station.");
        }
        // drive() is still abstract: concrete subclasses must implement it
    }

    static class SportCar extends Car {
        SportCar(String brand) { super(brand); }

        @Override
        public void drive() {
            System.out.println(getBrand() + " SportCar: accelerating at full speed!");
        }
    }

    public static void main(String[] args) {
        SportCar sportCar = new SportCar("Ferrari");
        sportCar.drive();
        sportCar.refuel();    // concrete method inherited from Car

        System.out.println("-- Vehicle reference -> SportCar object --");
        Vehicle vehicle = new SportCar("Lamborghini");
        vehicle.drive();
        // vehicle.refuel();  // compile error: refuel() is not declared in Vehicle
    }
}

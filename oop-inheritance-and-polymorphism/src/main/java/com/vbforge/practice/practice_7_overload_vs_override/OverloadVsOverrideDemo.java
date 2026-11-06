package com.vbforge.practice.practice_7_overload_vs_override;

/**
 * Overload (compile-time, static dispatch) vs override (runtime, dynamic dispatch).
 * Plus: declared type vs runtime type and downcasting.
 */
public class OverloadVsOverrideDemo {

    static class Vehicle {
        private final String brand;
        private final int    year;
        Vehicle(String brand, int year) {
            this.brand = brand; this.year = year;
            System.out.println("Vehicle ctor: " + brand + " (" + year + ")");
        }
        String getBrand() { return brand; }
        int    getYear()  { return year; }
        void   describe() { System.out.println("Vehicle: " + brand + " " + year); }
    }

    static class Car extends Vehicle {
        private final String model;
        Car(String brand, int year, String model) {
            super(brand, year);
            this.model = model;
            System.out.println("Car ctor: " + model);
        }
        @Override void describe() { System.out.println("Car: " + getBrand() + " " + model); }
        void   describe(int n)     { System.out.println("Car (overload, n=" + n + "): " + model); }
    }

    // --- classic overload vs override dispatch demo ---
    static class A {
        public void job()          { System.out.println("A.job()"); }
    }
    static class B extends A {
        public void job(int i)     { System.out.println("B.job(int)"); }  // overload, NOT override
    }

    public static void main(String[] args) {
        System.out.println("=== overload vs override ===\n");

        System.out.println("-- override: runtime dispatch --");
        Vehicle v = new Car("Toyota", 2022, "Camry");
        v.describe();                         // calls Car.describe - override

        System.out.println("\n-- overload: a different signature adds a method, it does not replace --");
        B b = new B();
        b.job();                              // calls A.job() - job(int) is NOT an override
        b.job(7);                             // calls B.job(int)

        System.out.println("\n-- declared type limits visible methods --");
        Vehicle v2 = new Car("Honda", 2023, "Civic");
        System.out.println("v2.getBrand(): " + v2.getBrand());
        // v2.describe(1); <- does not compile (declared as Vehicle)
        if (v2 instanceof Car c) {
            c.describe(1);                    // safe downcast, Car-specific overload
        }
    }
}

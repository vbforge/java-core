package com.vbforge.practice.practice_4_getclass_and_runtime_type;

/**
 * Object.getClass() - runtime type info.
 *
 *   getClass()                         -> exact runtime Class<?>
 *   getClass() == X.class              -> exact-type check (rejects subclasses)
 *   instanceof X                       -> subtype check (accepts subclasses)
 *   X.class.isAssignableFrom(Y.class)  -> "can a Y be used as an X"
 */
public class ObjectGetClassDemo {

    static class Vehicle {
        String brand;
        Vehicle(String brand) { this.brand = brand; }
    }
    static class Car extends Vehicle {
        String model;
        Car(String brand, String model) { super(brand); this.model = model; }
    }

    public static void main(String[] args) {
        System.out.println("=== getClass() and runtime type ===\n");

        Object[] items = {
                "hello", 42, 3.14, new int[]{1, 2, 3},
                new Vehicle("Toyota"), new Car("Toyota", "Camry")
        };
        System.out.println("-- getClass().getName() --");
        for (Object item : items) {
            System.out.printf("  %-18s -> %s%n",
                    item.getClass().getSimpleName(), item.getClass().getName());
        }

        System.out.println("\n-- getClass() == exact class --");
        Vehicle v = new Car("Toyota", "Camry");
        System.out.println("v instanceof Vehicle:          " + (v instanceof Vehicle));
        System.out.println("v instanceof Car:              " + (v instanceof Car));
        System.out.println("v.getClass() == Vehicle.class: " + (v.getClass() == Vehicle.class) + "   <- false");
        System.out.println("v.getClass() == Car.class:     " + (v.getClass() == Car.class) + "   <- true");

        System.out.println("\n-- isAssignableFrom --");
        System.out.println("Vehicle.class.isAssignableFrom(Car.class): " + Vehicle.class.isAssignableFrom(Car.class));
        System.out.println("Car.class.isAssignableFrom(Vehicle.class): " + Car.class.isAssignableFrom(Vehicle.class));

        System.out.println("\n-- downcast requires runtime check --");
        Vehicle maybeCar = new Car("Honda", "Civic");
        if (maybeCar instanceof Car c) {
            System.out.println("safe cast: model = " + c.model);
        }

        System.out.println("\n-- getClass() on arrays --");
        int[] arr = {1, 2, 3};
        System.out.println("arr.getClass():       " + arr.getClass());
        System.out.println("isArray:              " + arr.getClass().isArray());
        System.out.println("componentType:        " + arr.getClass().getComponentType());

        System.out.println("\n-- primitives vs wrappers --");
        System.out.println("int.class:            " + int.class);
        System.out.println("Integer.class:        " + Integer.class);
        System.out.println("int.class == Integer.class: " + (int.class == Integer.class));
    }
}

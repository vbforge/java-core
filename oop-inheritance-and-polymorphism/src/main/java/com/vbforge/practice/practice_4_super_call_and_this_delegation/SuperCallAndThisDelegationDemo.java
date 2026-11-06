package com.vbforge.practice.practice_4_super_call_and_this_delegation;

/**
 * super(...) vs this(...) vs super.method().
 *
 *   super(...)      - first statement of a constructor, runs the parent constructor
 *   this(...)       - first statement of a constructor, delegates to another constructor of the same class
 *   super.method()  - inside an override, calls the parent version and extends it
 */
public class SuperCallAndThisDelegationDemo {

    public static void main(String[] args) {
        System.out.println("--- super() constructor call ---");
        ElectricCar tesla = new ElectricCar("Tesla", "Model 3", 2023, 82.0);
        System.out.println(tesla.describe());

        System.out.println("\n--- this(...) constructor delegation (battery defaults to 75.0) ---");
        ElectricCar leaf = new ElectricCar("Nissan", "Leaf", 2022);
        System.out.println(leaf.describe());

        System.out.println("\n--- super.method() inside an overridden method ---");
        System.out.println("Parent describe(): " + new Vehicle("BMW", 2020).describe());
        System.out.println("Child  describe(): " + tesla.describe());
    }
}

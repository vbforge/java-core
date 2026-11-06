package com.vbforge.practice.practice_5_constructor_order_and_super_rules;

import com.vbforge.practice.practice_5_constructor_order_and_super_rules.Vehicle;

public class Car extends Vehicle {
    private final String model;

    public Car(String brand, int year, String model) {
        super(brand, year);
        this.model = model;
        System.out.println("Car ctor: " + model);
    }
    public String getModel() { return model; }
    public void   showDetails() {
        System.out.println("Details: brand=" + getBrand() + ", year=" + getYear() + ", model=" + model);
    }
}

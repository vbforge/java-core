package com.vbforge.practice.practice_4_super_call_and_this_delegation;

public class Vehicle {
    protected final String brand;
    protected final int year;

    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }

    public String describe() {
        return brand + " (" + year + ")";
    }

    public String getBrand() {
        return brand;
    }
}

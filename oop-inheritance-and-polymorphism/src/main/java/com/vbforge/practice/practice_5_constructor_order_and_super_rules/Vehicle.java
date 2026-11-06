package com.vbforge.practice.practice_5_constructor_order_and_super_rules;

public class Vehicle {
    private final String brand;
    private final int year;

    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year  = year;
        System.out.println("Vehicle ctor: " + brand + " (" + year + ")");
    }
    public String getBrand() { return brand; }
    public int    getYear()  { return year; }
}

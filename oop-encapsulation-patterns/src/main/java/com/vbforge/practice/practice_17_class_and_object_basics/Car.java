package com.vbforge.practice.practice_17_class_and_object_basics;

/** Anatomy of a class: fields (state), a constructor (initialisation), instance methods (behaviour). */
public class Car {

    private final String brand;
    private final String model;
    private final int    year;

    public Car(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year  = year;
    }

    public String describe() { return year + " " + brand + " " + model; }

    public int getAge(int currentYear) { return currentYear - year; }
}

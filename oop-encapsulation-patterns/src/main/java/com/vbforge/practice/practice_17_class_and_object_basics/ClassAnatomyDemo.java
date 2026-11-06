package com.vbforge.practice.practice_17_class_and_object_basics;

/** 'new' creates an object and returns a reference; each object has its own state. */
public class ClassAnatomyDemo {

    public static void main(String[] args) {
        Car car1 = new Car("Toyota", "Camry", 2020);
        Car car2 = new Car("BMW",    "M3",    2022);

        System.out.println(car1.describe());
        System.out.println(car2.describe());
        System.out.println("age in 2024: " + car1.getAge(2024) + " and " + car2.getAge(2024));
    }
}

package com.vbforge.practice.practice_17_class_and_object_basics;

/** A reference that points to no object: calling a method on it throws NullPointerException. */
public class NullReferenceNpeDemo {

    public static void main(String[] args) {
        Car nullCar = null;
        try {
            nullCar.describe();
        } catch (NullPointerException e) {
            System.out.println("NPE: calling a method on a null reference");
        }
    }
}

package com.vbforge.practice.practice_13_local_classes;

/**
 * A local class (declared inside a method) can read the effectively-final locals of that method and
 * read AND modify the private fields of the outer instance.
 */
public class LocalClassCapturesDemo {

    public static void main(String[] args) {
        new Boat().floating();
    }
}

class Boat {
    private String color = "White";

    void floating() {
        int capacity = 1000;   // effectively final (never reassigned)
        class LocalRescueBoat {
            public void rescueBoatColor() {
                color = "Orange";   // modifies the outer class field
                System.out.println("Rescue Boat color = " + color + ", Boat capacity = " + capacity);   // reads the local variable
            }
        }
        LocalRescueBoat rescueBoat = new LocalRescueBoat();
        rescueBoat.rescueBoatColor();
    }
}

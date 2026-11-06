package com.vbforge.practice.practice_12_final_fields;

/**
 * Three ways to initialise a 'final' instance field:
 *   1. inline                       (speed)
 *   2. instance initializer block   (sizeWheel)
 *   3. constructor                  (type)
 */
public class Bike {
    private final int    speed = 22;
    private final int    sizeWheel;
    private final String type;

    {
        sizeWheel = 34;
    }

    public Bike(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Bike{speed=" + speed + ", sizeWheel=" + sizeWheel + ", type='" + type + "'}";
    }
}

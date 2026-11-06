package com.vbforge.practice.practice_6_reflection_bypasses_private;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * private is a design tool, not security: reflection with setAccessible(true)
 * reads a private static field of another class.
 * (Never hide secrets behind private.)
 */
public class ReflectionBypassesPrivateDemo {

    static class Car {
        private static int numOfCars;

        {
            numOfCars++;          // instance initializer: runs for every new Car
        }

        public Car() { }

        public Car(String model) { }
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        new Car("Car1");
        new Car("Car2");
        new Car("Car3");

        Field numOfCars = Car.class.getDeclaredField("numOfCars");
        System.out.println("field is private: " + Modifier.isPrivate(numOfCars.getModifiers()));

        numOfCars.setAccessible(true);

        // the field is static, so there is no instance to pass: null
        int value = (int) numOfCars.get(null);
        System.out.println("numOfCars = " + value);   // 3
    }
}

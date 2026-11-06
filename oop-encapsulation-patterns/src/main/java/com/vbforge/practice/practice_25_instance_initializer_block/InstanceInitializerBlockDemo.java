package com.vbforge.practice.practice_25_instance_initializer_block;

/** An instance initializer block runs before the constructor body. */
public class InstanceInitializerBlockDemo {

    static class Car {
        int speed;

        {
            speed = 60;
        }

        Car() {
            System.out.println("Speed of the car: " + speed);   // 60
        }
    }

    public static void main(String[] args) {
        new Car();
    }
}

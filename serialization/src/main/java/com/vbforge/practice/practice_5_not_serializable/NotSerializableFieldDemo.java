package com.vbforge.practice.practice_5_not_serializable;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.NotSerializableException;
import java.io.Serializable;

/** Every object reachable from the written one must be Serializable - checked at RUNTIME, not at compile time. */
public class NotSerializableFieldDemo {

    static class Engine { // does NOT implement Serializable
        int power = 100;
    }

    static class Car implements Serializable {
        private static final long serialVersionUID = 1L;
        String model = "T";
        Engine engine = new Engine();
    }

    public static void main(String[] args) throws Exception {
        try {
            SerializationHelper.serialize(new Car());
        } catch (NotSerializableException e) {
            System.out.println("NotSerializableException, offending class: " + e.getMessage());
        }
    }
}

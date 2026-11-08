package com.vbforge.practice.practice_6_serializable_inheritance;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.InvalidClassException;
import java.io.Serializable;

/** Non-serializable parent WITHOUT a no-arg constructor: writing works, reading fails with InvalidClassException. */
public class NonSerializableParentWithoutNoArgDemo {

    static class Vehicle { // not Serializable, no no-arg constructor
        final String maker;

        Vehicle(String maker) {
            this.maker = maker;
        }
    }

    static class Car extends Vehicle implements Serializable {
        private static final long serialVersionUID = 1L;

        Car() {
            super("ACME");
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] data = SerializationHelper.serialize(new Car());
        System.out.println("writing worked, " + data.length + " bytes");
        try {
            SerializationHelper.deserialize(data);
        } catch (InvalidClassException e) {
            System.out.println("reading failed: InvalidClassException (" + e.getMessage().replaceAll("^.*: ", "") + ")");
        }
    }
}

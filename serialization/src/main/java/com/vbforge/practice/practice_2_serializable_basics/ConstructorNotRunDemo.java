package com.vbforge.practice.practice_2_serializable_basics;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** Deserialization of a Serializable class does NOT run its constructor. */
public class ConstructorNotRunDemo {

    static class Counted implements Serializable {
        private static final long serialVersionUID = 1L;
        static int constructorCalls = 0;

        Counted() {
            constructorCalls++;
        }
    }

    public static void main(String[] args) throws Exception {
        Counted original = new Counted();
        System.out.println("after new: constructor calls = " + Counted.constructorCalls);

        Counted copy = SerializationHelper.roundTrip(original);
        System.out.println("after deserialization: constructor calls = " + Counted.constructorCalls);
        System.out.println("copy is a different object: " + (copy != original));
    }
}

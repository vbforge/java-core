package com.vbforge.practice.practice_2_serializable_basics;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/**
 * Serializable is a marker interface (no methods). The stream starts with the magic number 0xACED,
 * and reading it back gives a NEW object with equal field values.
 */
public class SerializableMarkerDemo {

    static class Person implements Serializable {
        private static final long serialVersionUID = 1L;
        final String name;
        final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static void main(String[] args) throws Exception {
        Person original = new Person("Ada", 36);
        byte[] data = SerializationHelper.serialize(original);
        Person copy = SerializationHelper.deserialize(data);

        System.out.println("stream starts with: " + SerializationHelper.hexPrefix(data, 4));
        System.out.println("copy name/age: " + copy.name + "/" + copy.age);
        System.out.println("same object: " + (copy == original));
    }
}

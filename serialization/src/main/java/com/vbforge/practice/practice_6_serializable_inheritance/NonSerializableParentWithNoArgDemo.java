package com.vbforge.practice.practice_6_serializable_inheritance;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/**
 * Non-serializable parent WITH an accessible no-arg constructor: the parent's fields are not written;
 * on reading the parent constructor runs and the parent fields get ITS initial values, not the original's.
 */
public class NonSerializableParentWithNoArgDemo {

    static class Animal { // not Serializable
        String species;

        Animal() {
            species = "unknown";
        }

        Animal(String species) {
            this.species = species;
        }
    }

    static class Dog extends Animal implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;

        Dog(String name) {
            super("canine");
            this.name = name;
        }
    }

    public static void main(String[] args) throws Exception {
        Dog dog = new Dog("Rex");
        System.out.println("original: " + dog.name + " / " + dog.species);

        Dog copy = SerializationHelper.roundTrip(dog);
        System.out.println("copy:     " + copy.name + " / " + copy.species);
    }
}

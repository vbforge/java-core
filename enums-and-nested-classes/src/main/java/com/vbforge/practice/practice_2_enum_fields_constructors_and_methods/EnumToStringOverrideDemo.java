package com.vbforge.practice.practice_2_enum_fields_constructors_and_methods;

/** Enum with a String field whose toString() returns the field instead of the constant name. */
public class EnumToStringOverrideDemo {

    public static void main(String[] args) {
        Animal animal = Animal.CAT;

        System.out.println(animal);                      // toString() overridden -> cat jumping high
        System.out.println("name() = " + animal.name()); // name() is unaffected -> CAT
        System.out.println("message = " + animal.getMessage());
    }
}

enum Animal {
    DOG("dog running fast"),
    CAT("cat jumping high"),
    BIRD("bird can fly");

    private final String message;

    Animal(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}

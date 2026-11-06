package com.vbforge.practice.practice_8_encapsulation_story_person;

/**
 * End point of the story in OOP-Encapsulation-Story.md:
 * private fields, validating constructor and setter (checks extracted, DRY),
 * getters, and an "aging protocol" (incrementAge) instead of a free setAge.
 */
public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        checkName(name);
        checkAge(age);
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        checkName(name);
        this.name = name;
    }

    public void incrementAge() {
        this.age++;
    }

    private void checkAge(final int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age is negative");
        }
    }

    private void checkName(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is null or empty");
        }
    }

    @Override
    public String toString() {
        return name + ", " + age;
    }
}

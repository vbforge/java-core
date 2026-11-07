package com.vbforge.practice.practice_1_person_sample_data;

/**
 * Shared fixture, not a concept: a small immutable value that the logging practice classes
 * (println baseline, java.util.logging console and file handlers) all describe and compare.
 * (immutable)
 */
public record PersonRecord(String name, int age) {

    @Override
    public String toString() {
        return String.format("PersonRecord [name: %s, age: %s]", this.name, this.age);
    }

    // Method to return a full description
    public String fullDescription() {
        return String.format("PersonRecord [name: %s, age: %d].", this.name, this.age);
    }

    // Method to check if the person is an adult
    public boolean isAdult() {
        return this.age >= 18;
    }

    // Static method to compare ages of two PersonRecord instances
    public static String compareAges(PersonRecord p1, PersonRecord p2) {
        if (p1.age < p2.age) {
            return String.format("%s is younger than %s.", p1.name, p2.name);
        } else if (p1.age > p2.age) {
            return String.format("%s is older than %s.", p1.name, p2.name);
        } else {
            return String.format("%s and %s are the same age.", p1.name, p2.name);
        }
    }
}

package com.vbforge.practice.practice_10_hash_map;

import java.util.Objects;

public class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = Objects.requireNonNull(name, "name");
        this.age = age;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        int rez = name.hashCode();
        rez = 31 * rez + age;
        return rez;
    }

    @Override
    public String toString() {
        return "Person: " + name + ", " + age;
    }
}

package com.vbforge.practice.practice_1_class_objects_and_metadata;

/** Plain model class used as the target of the Class-object demos. */
public class Person {

    private String name;
    private int age;

    public Person() {
        this("Unknown", 0);
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + '}';
    }
}

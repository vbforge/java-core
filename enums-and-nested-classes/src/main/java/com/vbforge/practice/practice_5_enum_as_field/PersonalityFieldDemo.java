package com.vbforge.practice.practice_5_enum_as_field;

import static com.vbforge.practice.practice_5_enum_as_field.Personality.*;

/** One enum type used as a data field by two different classes that implement the same interface (constants via static import). */
public class PersonalityFieldDemo {

    public static void main(String[] args) {
        Person bob = new Person(23, MALE);
        System.out.println(bob.getAge());
        bob.talk();

        UFO ufo = new UFO(3000, ALIEN);
        System.out.println(ufo.getAge());
        ufo.talk();
    }
}

interface HumanInterface {

    void talk();
    void walk();
    int getAge();
}

enum Personality {
    FEMALE,
    MALE,
    ALIEN
}

class Person implements HumanInterface {

    private final int age;
    private final Personality personality;

    public Person(int age, Personality personality) {
        this.age = age;
        this.personality = personality;
    }

    @Override
    public void talk() {
        System.out.println("I can talk");
        System.out.println("I`m " + this.personality);
    }

    @Override
    public void walk() {
        System.out.println("I can walk");
    }

    @Override
    public int getAge() {
        return this.age;
    }
}

class UFO implements HumanInterface {

    private final int age;
    private final Personality personality;

    public UFO(int age, Personality personality) {
        this.age = age;
        this.personality = personality;
    }

    @Override
    public void talk() {
        System.out.println("I can talk as UFO");
        System.out.println("I`m " + this.personality);
    }

    @Override
    public void walk() {
        System.out.println("I can walk as UFO");
    }

    @Override
    public int getAge() {
        return this.age;
    }
}

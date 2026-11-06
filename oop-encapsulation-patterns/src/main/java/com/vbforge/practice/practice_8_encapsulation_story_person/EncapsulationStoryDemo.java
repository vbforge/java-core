package com.vbforge.practice.practice_8_encapsulation_story_person;

/** Walks through the final Person from the story: valid use, rejected values, the aging protocol. */
public class EncapsulationStoryDemo {

    public static void main(String[] args) {
        Person john = new Person("John", 30);
        System.out.println("created:      " + john);

        try {
            new Person("  ", 30);
        } catch (IllegalArgumentException e) {
            System.out.println("blank name:   " + e.getMessage());
        }
        try {
            new Person("Jack", -1);
        } catch (IllegalArgumentException e) {
            System.out.println("negative age: " + e.getMessage());
        }

        john.setName("Johnny");
        System.out.println("renamed:      " + john);
        try {
            john.setName(null);
        } catch (IllegalArgumentException e) {
            System.out.println("null rename:  " + e.getMessage());
        }

        john.incrementAge();
        System.out.println("birthday:     " + john);   // there is no setAge on purpose
    }
}

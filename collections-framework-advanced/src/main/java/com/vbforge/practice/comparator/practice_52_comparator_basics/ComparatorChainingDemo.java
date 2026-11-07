package com.vbforge.practice.comparator.practice_52_comparator_basics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ComparatorChainingDemo {
    public static void main(String[] args) {

        List<Person> people = new ArrayList<>();
        people.add(new Person("John", 25));
        people.add(new Person("Bob", 35));
        people.add(new Person("Anna", 21));
        people.add(new Person("Nickolas", 20));
        people.add(new Person("Katy", 19));

        System.out.println("Sort by age:");
        people.sort(Comparator.comparingInt(Person::age));
        people.forEach(System.out::println);

        System.out.println("\nSort by name:");
        people.sort(Comparator.comparing(Person::name));
        people.forEach(System.out::println);

        System.out.println("\nSort by name reversed then by age:");
        people.sort(Comparator
                .comparing(Person::name).reversed()
                .thenComparingInt(Person::age));
        people.forEach(System.out::println);

    }

    record Person(String name, int age) {}

}

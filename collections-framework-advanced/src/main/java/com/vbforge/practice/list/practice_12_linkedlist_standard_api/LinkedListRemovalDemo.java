package com.vbforge.practice.list.practice_12_linkedlist_standard_api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class LinkedListRemovalDemo {
    public static void main(String[] args) {

        LinkedList<String> names = new LinkedList<>(List.of("John", "Mike", "Anna", "Mike"));
        System.out.println("Before remove: " + names);

        // Remove by value
        names.remove("Mike"); // removes first occurrence
        System.out.println("After remove(\"Mike\"): " + names);

        // Remove by index
        names.remove(1);
        System.out.println("After remove at index 1: " + names); //wanted to remove Anna

        // Safe removal with iterator
        LinkedList<String> countries = new LinkedList<>(List.of("US", "UK", "IT", "UK", "US"));
        Iterator<String> itr = countries.iterator();
        while (itr.hasNext()) {
            if (itr.next().equals("UK")) {
                itr.remove();
            }
        }
        //or using lambda expression
        countries.removeIf(country -> country.equals("US"));
        System.out.println("After safe iterator removal: " + countries);

        // Clear an entire list
        names.clear();
        System.out.println("After clear: " + names);

    }
}

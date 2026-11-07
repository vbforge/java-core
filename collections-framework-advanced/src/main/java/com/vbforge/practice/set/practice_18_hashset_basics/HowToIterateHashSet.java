package com.vbforge.practice.set.practice_18_hashset_basics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HowToIterateHashSet {
    public static void main(String[] args) {

        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        System.out.println("Iterating by enhanced for loop:");
        // Using enhanced for loop
        for (Integer s : set) {
            System.out.print(s + " ");
        }
        System.out.println();

        System.out.println("Iterating by iterator:");
        // Using iterator
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();

        System.out.println("Iterating by forEach (1):");
        // Using Java 8 forEach + lambda
        set.forEach(System.out::println);

        System.out.println("Iterating by forEach (2):");
        // Using Java 8 lambda
        set.forEach((integer -> System.out.print(integer + " ")));

    }
}

package com.vbforge.practice.set.practice_18_hashset_basics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * HashSet constructors and capacity behavior.
 *
 * Achievements of HashSet: access to elements is constant time, independent of size;
 * the same holds for add/remove.
 *
 * Candidates for new HashSet<>():
 *   HashSet()                                       — 16 buckets by default
 *   HashSet(Collection<? extends E> c)              — seeded from a collection, 16 buckets
 *   HashSet(int initialCapacity)                    — custom initial bucket count
 *   HashSet(int initialCapacity, float loadFactor)  — custom bucket count + load factor
 *   HashSet(int initialCapacity, float loadFactor, boolean dummy) — internal ctor (LinkedHashSet uses it)
 *
 * When size approaches `capacity * loadFactor` (default loadFactor=0.75), the set resizes.
 */
public class HashSetConstructionDemo {
    public static void main(String[] args) {

        Set<Integer> set = new HashSet<>(List.of(1,16,23,0,14,50,2,9,15,17));
        System.out.println(set + " size() = " + set.size());

        set.add(10);
        System.out.println(set + " size() = " + set.size());

        set.add(11);
        System.out.println(set + " size() = " + set.size());

        set.add(80);
        System.out.println(set + " size() = " + set.size());

        Set<Integer> set1 = new HashSet<>();
        // set1 is created with the no-arg constructor — 16 buckets by default
        System.out.println("set1 size() = " + set1.size());

    }
}

package com.vbforge.practice.set.practice_18_hashset_basics;

import java.util.HashSet;
import java.util.Set;

public class UnionIntersectionDifferenceHashSet {
    public static void main(String[] args) {

        Set<Integer> set1 = new HashSet<>();
        set1.add(1);
        set1.add(2);
        set1.add(3);
        set1.add(4);
        set1.add(5);

        Set<Integer> set2 = new HashSet<>();
        set2.add(2);
        set2.add(3);
        set2.add(4);
        set2.add(5);
        set2.add(6);
        set2.add(7);

        //union two sets in one:
        Set<Integer> unionSet = new HashSet<>(set1);
        unionSet.addAll(set2);
        System.out.println("Union Set: " + unionSet);

        //intersect two sets:
        Set<Integer> intersectionsSet = new HashSet<>(set1);
        intersectionsSet.retainAll(set2);
        System.out.println("Intersections Set: " + intersectionsSet);

        //difference between two sets: leave unique from set1
        Set<Integer> differenceSet1 = new HashSet<>(set1); //removing all elements from set1 which match from set2
        differenceSet1.removeAll(set2);
        System.out.println("Difference Set1: " + differenceSet1);//Difference Set1: [1]

        //difference between two sets: leave unique from set2
        Set<Integer> differenceSet2 = new HashSet<>(set2); //removing all elements from set1 which match from set2
        differenceSet2.removeAll(set1);
        System.out.println("Difference Set2: " + differenceSet2); //Difference Set2: [6, 7]

    }
}

package com.vbforge.practice.list.practice_6_arraylist_modification;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * subList(fromIndex, toIndex) Actually Does
 * It return a view, not a copy, of a portion of the original list.
 * The view is backed by the original list, meaning changes to the sublist reflect in the original list, and vice versa.
 * toIndex is exclusive.
 * The sublist has its own indices starting at 0, but it's referencing the original list's memory.
 * */
public class SubListViewDemo {
    public static void main(String[] args) {

        //Example 1: Basic View Behavior
        List<Integer> list1 = new ArrayList<>(List.of(1,2,3,4,5,6,7,8));
        List<Integer> list2 = list1.subList(1, 3);
        list2.set(0, 100); // modifies index 0 of the sublist, which is index 1 in list1
        System.out.println(list2); // [100, 3]
        System.out.println(list1); // [1, 100, 3, 4, 5, 6, 7, 8]


        //Example 2: Adding to sublist
        List<Integer> list3 = list1.subList(1, 3); // [15, 22]
        list3.add(0, 150);
        System.out.println(list3); // [150, 100, 3]
        System.out.println(list1); // [1, 150, 100, 3, 4, 5, 6, 7, 8]

        //Example 3: structural change of the parent invalidates the view -> ConcurrentModificationException
        List<Integer> listA = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        List<Integer> listB = listA.subList(1, 4);
        listA.add(10); // Structural modification outside the sublist
        try {
            listB.get(0);  // always throws: the view detects the parent's modCount change
        } catch (ConcurrentModificationException e) {
            System.out.println("sublist view after parent.add(): ConcurrentModificationException");
        }

    }
}

package com.vbforge.practice.iterator.practice_15_list_iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ListIteratorAddRemoveReplace {
    public static void main(String[] args) {

        List<String> list = new ArrayList<String>();
        list.add("apple");
        list.add("orange");
        list.add("banana");
        list.add("cherry");

        System.out.println("Before modifications: " + list); //[apple, orange, banana, cherry]


        ListIterator<String> it = list.listIterator();
        while (it.hasNext()) {
            String s = it.next();
            if (s.equals("apple")) {
                it.set("green apple"); // Replace
            } else if (s.equals("banana")) {
                it.remove(); // Remove
            } else if (s.equals("cherry")) {
                it.add("blueberry"); // Add before next()
            }
        }

        System.out.println("After modifications: " + list); //[green apple, orange, cherry, blueberry]

    }
}

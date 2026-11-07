package com.vbforge.practice.list.practice_8_arraylist_exercises;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vladproduction on 24-Mar-24
 */

public class ReverseListDemo {
    public static void main(String[] args) {

        System.out.println(" 10)========");
        /**Reverse the list:

         Reverse the order of elements in the numbers list.
         Use methods like Collections.reverse or a custom loop iterating from the end to the beginning.*/

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i * 2);
        }
        System.out.println("Original: " + list);

        //1) Collection.reverse:
        Collections.reverse(list);
        System.out.println("Reversed (used Collections.reverse):\n" + list);

        //2) Custom loop iterating (end -> beginning):
        reverseLoop(list);
    }

    private static void reverseLoop(List<Integer> list) {
        List<Integer> reversed = new ArrayList<>();
        for (int i = list.size()-1; i >= 0 ; i--) {
             reversed.add(list.get(i));
        }
        System.out.println("Custom loop (from end to beginning):\n" + reversed);
    }
}

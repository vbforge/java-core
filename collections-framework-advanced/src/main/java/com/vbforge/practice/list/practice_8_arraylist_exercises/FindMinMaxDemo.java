package com.vbforge.practice.list.practice_8_arraylist_exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladproduction on 24-Mar-24
 *
 * Extracted from {@code ArrayListPractice2_MergingLists} — the original
 * nested class was a distinct concept (find min/max) sharing no logic with
 * the merge two-pointer demo.
 */

public class FindMinMaxDemo {
    public static void main(String[] args) {

        System.out.println(" 9)========");
        /**Find the minimum and maximum elements:

         Write code to find the minimum and maximum elements in the numbers list without sorting.
         Iterate through the list and keep track of the current minimum and maximum values encountered.*/
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(50);
        list.add(-5);
        list.add(2);
        list.add(105);
        find_Min_Max(list);
    }

    private static void find_Min_Max(List<Integer> list) {
        int currentMin = list.get(0);
        int currentMax = list.get(0);

        for (Integer number : list) {
            if(number < currentMin){
                currentMin = number;
            }
            if (number > currentMax){
                currentMax = number;
            }
        }
        System.out.println("Min: " + currentMin);
        System.out.println("Max: " + currentMax);
    }
}

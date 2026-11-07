package com.vbforge.practice.list.practice_8_arraylist_exercises;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vladproduction on 24-Mar-24
 */

public class RemoveDuplicatesFromListDemo {
    public static void main(String[] args) {

        System.out.println(" 8)========");
        /**Remove duplicates:

         Given a potentially unsorted numbers list, remove duplicate elements from it while maintaining the order.
         Use a HashSet to keep track of unique elements seen so far and iterate through the list,
         adding only unseen elements to a new list.*/

        List<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(1);
        list.add(100);
        list.add(41);
        list.add(321);
        list.add(17);
        list.add(145);
        list.add(4);
        list.add(321);
        list.add(17);
        list.add(145);
        list.add(4);
        list.add(41);
        list.add(321);
        list.add(17);
        list.add(145);
        list.add(4);
        list.add(321);
        System.out.println(list);

        List<Integer> removedDuplicate = removeDuplicate(list);
        System.out.println(removedDuplicate);
    }

    private static List<Integer> removeDuplicate(List<Integer> list){
        Set<Integer> seen = new HashSet<>();
        List<Integer> uniqueList = new ArrayList<>();
        for (Integer number : list) {
            if(!seen.contains(number)){
                uniqueList.add(number);
                seen.add(number); //marked as seen
            }
        }
        return uniqueList;
    }
}

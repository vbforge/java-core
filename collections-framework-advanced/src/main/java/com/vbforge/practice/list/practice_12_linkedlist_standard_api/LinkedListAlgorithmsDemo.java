package com.vbforge.practice.list.practice_12_linkedlist_standard_api;

import java.util.*;

public class LinkedListAlgorithmsDemo {
    public static void main(String[] args) {

        reverseLinkedList();
        findMiddle();
        detectDuplicate();
        removeDuplicates();

    }

    private static void removeDuplicates() {
        LinkedList<Integer> list = new LinkedList<>(List.of(1, 2, 2, 3, 4, 3));
        System.out.println("Before: " + list);
        Set<Integer> seen = new HashSet<>();
        Iterator<Integer> itr = list.iterator();
        while (itr.hasNext()) {
            if(!seen.add(itr.next())){
                itr.remove();
            }
        }
        System.out.println("After: " + list);
    }

    private static void detectDuplicate() {
        List<Integer> list = List.of(1,2,3,4,2,5);
        //System.out.println("Duplicates: " + Collections.frequency(list, 2)); //if meet any element more than 2 times
        Set<Integer> seen = new HashSet<>();
        boolean hasDuplicates = false;

        //by stream function:
        //boolean hasDuplicates = list.stream().anyMatch(e -> !seen.add(e));

        //through loop:
        for (Integer item : list) {
            if(item != null && !seen.add(item)){
                hasDuplicates = true;
                break;
            }
        }

        System.out.println("Duplicates: " + hasDuplicates);
    }

    private static void findMiddle() {
        LinkedList<Integer> list = new LinkedList<>(List.of(1, 2, 3, 4, 5, 6));
        System.out.println("Middle: " + list.get(list.size()/2));
    }

    private static void reverseLinkedList() {
        LinkedList<Integer> list = new LinkedList<>(List.of(1, 2, 3, 4, 5));
        Collections.reverse(list);
        System.out.println("Reversed: " + list);
    }
}

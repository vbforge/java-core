package com.vbforge.practice.list.practice_6_arraylist_modification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemovingItemFromListDemo {

    public static void main(String[] args) {

        removeIteratingFromEnd();
        removeIteratingFromBeginning(); //not recommended
        removeByIterator();
        removeIfByIterator();
        removeItemByIndex();

    }

    private static void removeIteratingFromEnd() {
        System.out.println("\nremoveIteratingFromEnd:");
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        for (int i = list.size() - 1; i >= 0; i--) {
            if(list.get(i) % 2 == 0){
                list.remove(i);
            }
        }
        System.out.println(list);
    }

    //under suspicious
    private static void removeIteratingFromBeginning() {
        System.out.println("\nremoveIteratingFromBeginning (not recommended):");
        // not recommended: removing from a list while iterating forward by index is risky
        // because it shifts elements and might skip them unintentionally
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i) % 2 == 0){
                list.remove(i);
            }
        }
        System.out.println(list);
    }

    private static void removeByIterator() {
        System.out.println("\nremoveByIterator:");
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));
       Iterator<Integer> iterator = list.iterator();
            while (iterator.hasNext()) {
                Integer integer = iterator.next();
                if(integer % 2 == 0){
                    iterator.remove();
                }
            }
        System.out.println(list);
    }

    private static void removeIfByIterator() {
        System.out.println("\nremoveIfByIterator:");
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        list.removeIf(integer -> integer % 2 == 0);
        System.out.println(list);
    }

    private static void removeItemByIndex() {
        System.out.println("\nremoveItemByIndex:");
        ArrayList<Integer> list = new ArrayList<>();
        //add:
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        //remove:
        list.remove(5);
        System.out.println(list);
        /**remove in Array List is not effective*/
    }

}

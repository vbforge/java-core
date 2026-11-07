package com.vbforge.practice.list.practice_6_arraylist_modification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayListBaseMethodsExercisesDemo {
    public static void main(String[] args) {

        System.out.println(" 1)========");
        /**1)Create an ArrayList and add elements:

         Initialize an ArrayList<Integer> named numbers.
         Add elements 1, 5, 3, and 7 to the numbers list using add method.
         Print the entire list using a for loop or forEach loop with lambda.*/
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(5);
        list.add(3);
        list.add(7);
        list.forEach(integer -> System.out.print(integer + ", "));
        System.out.println();
        System.out.println(" 2)========");
        /**2)Check if an element exists:

         Given a number target, write code to check if it exists in the numbers list.
         Use the contains method of ArrayList to perform the check.*/
        int target = 3;
        System.out.println(list.contains(target));

        System.out.println(" 3)========");
        /**3)Find the index of an element:

         Find the index of the first occurrence of a number target in the numbers list.
         Use the indexOf method of ArrayList and handle the case where the element is not found.*/
        System.out.println(list.indexOf(target));

        System.out.println(" 4)========");
        /**4)Remove an element:

         Remove the first occurrence of a number target from the numbers list.
         Use the remove method of ArrayList and handle the case where the element is not found.*/
        int removingElement = 3;
        if(list.contains(removingElement)){
            list.remove((Integer) removingElement);
            System.out.println("removed: " + removingElement + "; updated list: " + list);
        }else {
            System.out.println("element is not found");
        }

        System.out.println(" 5)========");
        /**Sort the list:

         Sort the elements of the numbers list in ascending order.
         Use the Collections.sort method with the numbers list.*/
        List<Integer> listAsc = new ArrayList<>();
        listAsc.add(3);
        listAsc.add(76);
        listAsc.add(13);
        listAsc.add(1);
        listAsc.add(-2);
        System.out.println("before sort:");
        System.out.println(listAsc);
        System.out.println("after sorting (stream, joined with spaces):");
        String sorted = listAsc.stream().sorted().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println(sorted);
        System.out.println();

        System.out.println(" 6)========");
        /**Iterate and modify elements:

         Iterate through the numbers list and multiply each element by 2.
         Use a for loop or forEach loop with lambda to modify each element within the list.*/
        System.out.println("initialized list:");
        List<Integer> numbsForMulti = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            numbsForMulti.add(i);
        }
        String initList = numbsForMulti.stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println(initList);

        List<Integer> modifiedList = new ArrayList<>();
        for (Integer i : numbsForMulti) {
            modifiedList.add(i * 2);
        }
        System.out.println("modified list:");
        String modifyList = modifiedList.stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println(modifyList);
        //modify by lambda (x*3):
        System.out.println("modified by lambda( x3):");
        List<Integer> listForLambda = new ArrayList<>();
        listForLambda.add(2);
        listForLambda.add(4);
        listForLambda.add(6);
        System.out.println("Before modification: ");
        listForLambda.forEach(integer -> System.out.print(integer + " "));
        IntStream.range(0, listForLambda.size())
                .forEach(i -> listForLambda.set(i, listForLambda.get(i) * 3));
        String modifiedByLambda = listForLambda.stream().map(String::valueOf).collect(Collectors.joining(" "));
        System.out.println("\nAfter modification: " + modifiedByLambda); // 6 12 18

    }
}

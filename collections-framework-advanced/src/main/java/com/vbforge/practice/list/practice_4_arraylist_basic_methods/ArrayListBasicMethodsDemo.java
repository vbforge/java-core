package com.vbforge.practice.list.practice_4_arraylist_basic_methods;

import java.util.ArrayList;
import java.util.List;

public class ArrayListBasicMethodsDemo {
    public static void main(String[] args) {

        //create list:
        List<Integer> list = new ArrayList<>();

        //add items to the list:
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4000);
        list.add(5);

        //print content of the list:
        System.out.println(list);

        //check the size of the list:
        System.out.println("Size: " + list.size());

        //boolean isEmpty() --> true is size==0
        System.out.println("Is empty: " + list.isEmpty());

        //get item from the list:
        System.out.println("Item at index 2: " + list.get(2));

        //set(i, Object), replaces value on i position; new Value is Object; Also return Object with oldValue in this position
        Integer previousValue = list.set(2, 100);
        System.out.println("Previous value at index 2: " + previousValue);
        System.out.println("Item at index 2: " + list.get(2));

        //remove(i) - removes item in position i
        list.remove(2);
        System.out.println(list);

        //contains(Object) --> true if list contains Object
        System.out.println("Contains 4000: " + list.contains(4000));

        //clear() - remove all items
        list.clear();
        System.out.println(list);

        //boolean isEmpty() --> true is size==0
        System.out.println("Is empty: " + list.isEmpty());


        //add items to the list for iterating:
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4000);
        list.add(5);

        System.out.println("Is empty: " + list.isEmpty());

        //iterate through the list by using for loop:
        for (Integer integer : list) {
            System.out.print(integer + " ");
        }
        System.out.println();

        //iterate through the list by using enhanced for loop:
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }

        someIteratingMethods();

    }

    private static void someIteratingMethods() {
        //dynamic array
        ArrayList<Integer> integers = new ArrayList<>();

        //add:
        for (int i = 0; i < 10; i++) {
            integers.add(i);
        }

        System.out.println("\n============how to iterate:================");
        System.out.println("//1) loop for");
        for (int i = 0; i < integers.size(); i++) {
            System.out.print(integers.get(i));
        }
        System.out.println();

        System.out.println("//2) iter - for each");
        for (Integer integer : integers) {
            System.out.print(integer);
        }
        System.out.println();

        System.out.println("//3) method reference - forEach:");
        integers.forEach(System.out::print);
        System.out.println();

        System.out.println("//4) lambda - forEach:");
        integers.forEach((item)-> System.out.print(item));
        System.out.println();
    }
}

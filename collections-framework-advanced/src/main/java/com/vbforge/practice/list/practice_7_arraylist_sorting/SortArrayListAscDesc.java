package com.vbforge.practice.list.practice_7_arraylist_sorting;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Collections.sort;

public class SortArrayListAscDesc {
    public static void main(String[] args) {

        System.out.println("\tAscending sorting - from less to bigger:");
        //Ascending sorting - from less to bigger
        //String sorting by natural order (ascending order)
        ArrayList<String> cars =  new ArrayList<>();
        cars.add("Volkswagen");
        cars.add("Toyota");
        cars.add("Porsche");
        cars.add("Ferrari");
        cars.add("Mercedes-Benz");
        cars.add("Audi");
        cars.add("Rolls-Royce");
        cars.add("BMW");

        System.out.println("Before Sorting cars (in asc): " + cars);

        sort(cars);

        System.out.println("After Sorting cars (in asc): " + cars);

        System.out.println("---");

        //Integer sorting by natural order (ascending order)
        ArrayList<Integer> numbersList = new ArrayList<>();
        numbersList.add(10);
        numbersList.add(2);
        numbersList.add(68);
        numbersList.add(102);
        numbersList.add(-88);
        numbersList.add(5);
        numbersList.add(140);
        numbersList.add(21);
        System.out.println("Before sort Integer (in asc): " + numbersList);
        for(int items: numbersList){
            System.out.print(items + " ");
        }
        System.out.println();
        sort(numbersList);
        System.out.println("After sort Integer (in asc): " + numbersList);
        for(int items: numbersList){
            System.out.print(items + " ");
        }
        System.out.println();



        System.out.println("\n\tDescending order - from bigger to smallest:");

        //Descending order - from bigger to smallest
        System.out.println("String sorting desc example: ");
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("Data Science");
        stringList.add("Testing");
        stringList.add("C#");
        stringList.add("Basic Language");
        stringList.add("UML");
        stringList.add("Algorithms ");
        stringList.add("Computer Networks");
        stringList.add("Python");
        System.out.println("Before sort Strings (in desc): " + stringList);
        Collections.sort(stringList,Collections.reverseOrder());
        System.out.println("After sort Strings (in desc): " + stringList);

        System.out.println("---");

        System.out.println("Integer sorting desc example: ");
        ArrayList <Integer> integerList = new ArrayList<>();
        integerList.add(5);
        integerList.add(33);
        integerList.add(-9);
        integerList.add(0);
        integerList.add(15);
        integerList.add(20);
        integerList.add(105);
        System.out.println("Before sort Integer (in desc): " + integerList);
        Collections.sort(integerList, Collections.reverseOrder());
        System.out.println("After sort Integer (in desc): " + integerList);
    }


}

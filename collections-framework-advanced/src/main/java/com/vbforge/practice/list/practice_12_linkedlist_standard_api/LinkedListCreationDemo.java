package com.vbforge.practice.list.practice_12_linkedlist_standard_api;

import java.util.LinkedList;
import java.util.List;

public class LinkedListCreationDemo {
    public static void main(String[] args) {

        List<String> fruits = new LinkedList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");
        fruits.add("Pear");

        System.out.println("Fruits: " + fruits);
    }
}

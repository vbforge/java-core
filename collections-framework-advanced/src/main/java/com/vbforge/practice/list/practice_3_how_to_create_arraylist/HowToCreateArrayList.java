package com.vbforge.practice.list.practice_3_how_to_create_arraylist;

import java.util.*;

public class HowToCreateArrayList {
    public static void main(String[] args) {

        //create array list:

        //1. interface-based
        List<String> arrayList = new ArrayList<>();
        //2. concrete class implementation based:
        ArrayList<String> arrayList2 = new ArrayList<>();
        //3. abstract class AbstractList<E>
        AbstractList<Integer> abstractList = new ArrayList<>();
        //4. abstract class AbstractCollection<E>
        AbstractCollection<Integer> abstractCollection = new ArrayList<>();
        //5. implements Collection<E>
        Collection<Integer> collectionList = new ArrayList<>();
        //6. extends Iterable<E>
        Iterable<String> iterableList = new ArrayList<>();
        //7. we can define capacity
        List<Integer> listWithCapacity = new ArrayList<>(100);


        //how to use conventionally:

        /**By Convention of Collections Framework we're referring to the Interface during creating the object of Collection
         * for example:
         * ArrayList<Integer> integers = new ArrayList<>();
         * we do:
         * List<Integer> integers = new ArrayList<>();*/

        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        //remove:
        list.remove(5);
        System.out.println(list);

        //making new type of reference and add some data:
        list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        System.out.println(list);

        /** so we do not care about implementation, just using object of collection in level of Interface*/


    }
}

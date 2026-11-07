package com.vbforge.practice.list.practice_5_list_iteration_styles;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListIterationStylesDemo {

    private static final List<String> list = List.of("A", "B", "C", "D", "E");

    public static void main(String[] args) {

        enhancedForLoop(list);
        iteratorLoop(list);
        listIteratorLoop(list);
        forEachByReferenceLoop(list);
        whileIteratorLoop(list);

    }

    private static void enhancedForLoop(final List<String> list) {
        System.out.println("enhanced for loop: ");
        for (String s : list) {
            System.out.print(s + " ");
        }
        System.out.println("\n");
    }

    private static void iteratorLoop(final List<String> list) {
        System.out.println("iterator loop: ");
        for(Iterator<String> iterator = list.iterator(); iterator.hasNext();){
            System.out.print(iterator.next() + " ");
        }
        System.out.println("\n");
    }

    private static void listIteratorLoop(List<String> list) {
        System.out.println("list iterator loop: ");
        for(ListIterator<String> iterator = list.listIterator(); iterator.hasNext();){
            System.out.print(iterator.next() + " ");
        }
        System.out.println("\n");
    }

    private static void forEachByReferenceLoop(List<String> list) {
        System.out.println("forEach by reference loop: ");
        list.forEach(System.out::println);
    }

    // while-loop form with Iterator — same behaviour as iteratorLoop, different shape
    private static void whileIteratorLoop(List<String> list) {
        System.out.println("while-loop iterator: ");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.print(iterator.next() + " ");
        }
        System.out.println();
    }

}

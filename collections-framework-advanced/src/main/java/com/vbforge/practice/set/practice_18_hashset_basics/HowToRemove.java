package com.vbforge.practice.set.practice_18_hashset_basics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HowToRemove {
    public static void main(String[] args) {

        removeByValue();
        removeWhileIterator(); //safe removing while iteration
        removeIf(); //using lambda
        //doNotRemoveByForEachLoop(); //ConcurrentModificationException

    }

    private static void removeIf() {
        System.out.println("Remove if:");
        Set<String> set = new HashSet<>();
        set.add("A");
        set.add("B");
        set.add("C");
        set.add("D");
        set.add("E");

        System.out.println("Set: " + set);
        set.removeIf(s -> s.equals("C"));
        System.out.println("Set: " + set);
    }

    private static void removeWhileIterator() {
        System.out.println("Remove while iterator:");
        Set<String> set = new HashSet<>();
        set.add("A");
        set.add("B");
        set.add("C");
        set.add("D");
        set.add("E");

        System.out.println("Set: " + set);

        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.equals("C")) {
                iterator.remove();
            }
        }
        System.out.println("Set: " + set);
    }

    private static void removeByValue() {
        System.out.println("Remove by value:");
        Set<String> set = new HashSet<>();
        set.add("A");
        set.add("B");
        set.add("C");
        System.out.println("Set: " + set);
        set.remove("A");
        System.out.println("Set: " + set);
    }

    private static void doNotRemoveByForEachLoop() {
        System.out.println("Do not remove by forEach loop:");
        Set<String> set = new HashSet<>();
        set.add("A");
        set.add("B");
        set.add("C");
        set.add("D");
        set.add("E");

        for (String s : set) {
            if (s.equals("C")) {
                set.remove(s);
            }
        }
    }

}

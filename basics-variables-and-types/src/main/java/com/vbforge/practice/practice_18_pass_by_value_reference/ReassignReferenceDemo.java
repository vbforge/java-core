package com.vbforge.practice.practice_18_pass_by_value_reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Reassigning the parameter only changes the method's own copy of the reference;
 * the caller's variable still points to the original object.
 * That is why Java is never "pass-by-reference".
 */
public class ReassignReferenceDemo {

    static class Box {
        int value;
        Box(int v) { this.value = v; }
        @Override public String toString() { return "Box(" + value + ")"; }
    }

    private static void reassignBox(Box box) {
        box = new Box(0);
    }

    private static void replaceList(List<String> list) {
        list = new ArrayList<>();
        list.add("this-is-lost");
    }

    public static void main(String[] args) {
        Box box = new Box(77);
        reassignBox(box);
        System.out.println("after reassignBox:  " + box);

        List<String> items = new ArrayList<>(List.of("a", "b"));
        replaceList(items);
        System.out.println("after replaceList:  " + items);
    }
}

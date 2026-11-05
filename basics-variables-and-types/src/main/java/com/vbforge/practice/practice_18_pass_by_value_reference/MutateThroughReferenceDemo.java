package com.vbforge.practice.practice_18_pass_by_value_reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Java is always pass-by-value. For an object the value passed is a COPY of the reference,
 * so mutating the object through the parameter IS visible to the caller.
 */
public class MutateThroughReferenceDemo {

    static class Box {
        int value;
        Box(int v) { this.value = v; }
        @Override public String toString() { return "Box(" + value + ")"; }
    }

    private static void mutateBox(Box box) {
        box.value = 999;
    }

    private static void addToList(List<String> list) {
        list.add("added-by-method");
    }

    public static void main(String[] args) {
        Box box = new Box(42);
        mutateBox(box);
        System.out.println("after mutateBox: " + box);

        List<String> items = new ArrayList<>(List.of("a", "b"));
        addToList(items);
        System.out.println("after addToList: " + items);
    }
}

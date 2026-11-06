package com.vbforge.practice.practice_11_covariance_and_invariance;

import java.util.ArrayList;
import java.util.List;

/**
 * Generics are invariant: {@code List<Child1>} is NOT a {@code List<Parent>}, even though
 * Child1 extends Parent. The same mistake that arrays only catch at runtime is caught at
 * compile time. A wildcard describes a family of types when you need the flexibility.
 */
public class GenericInvarianceDemo {

    public static void main(String[] args) {

        // ================== Scene 1: elements may be subtypes of T ==================
        List<Parent> parents = new ArrayList<>();
        parents.add(new Parent());
        parents.add(new Child1());
        parents.add(new Child2());
        for (Parent item : parents) {
            System.out.println(item.getClass().getSimpleName());
        }

        // ================== Scene 2: the list types themselves are unrelated ==================
        List<Child1> children = new ArrayList<>();
        // List<Parent> asParents = children;   // compile error: List<Child1> is not a List<Parent>
        // (otherwise asParents.add(new Child2()) would put a Child2 into a list of Child1)

        List<Double> doubles = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        // doubles = integers;                  // compile error - incompatible types

        // ================== Scene 3: a wildcard view over the family ==================
        List<? extends Parent> view = children;   // OK: any list of Parent or its subtypes
        // view.add(new Child1());                // compile error: the exact element type is unknown
        System.out.println("view size = " + view.size() + ", doubles = " + doubles + ", integers = " + integers);
    }

    static class Parent {
    }

    static class Child1 extends Parent {
    }

    static class Child2 extends Parent {
    }
}

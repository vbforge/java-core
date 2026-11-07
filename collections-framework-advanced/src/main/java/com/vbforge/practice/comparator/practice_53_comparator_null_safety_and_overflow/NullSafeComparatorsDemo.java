package com.vbforge.practice.comparator.practice_53_comparator_null_safety_and_overflow;

import java.util.*;

/**
 * Comparator.nullsFirst / nullsLast. Extracted from the old ComparableComparatorDemo deep dive.
 */
public class NullSafeComparatorsDemo {

    // ─────────────────────────────────────────────────────────────
    // 1. Comparable — natural ordering
    // ─────────────────────────────────────────────────────────────

    /**
     * Student implements Comparable<Student> with natural order by GPA descending.
     * This means: higher GPA = "less than" in sort = appears first in ascending sort.
     */
    static class Student implements Comparable<Student> {
        final String name;
        final double gpa;
        final int    age;

        Student(String name, double gpa, int age) {
            this.name = name;
            this.gpa  = gpa;
            this.age  = age;
        }

        /**
         * Natural order: descending GPA (highest GPA sorts first).
         * Double.compare avoids subtraction precision issues.
         * Negated because we want descending: higher gpa → "less than" → first.
         */
        @Override
        public int compareTo(Student other) {
            return Double.compare(other.gpa, this.gpa);  // descending by GPA
        }

        @Override
        public String toString() {
            return name + "(gpa=" + gpa + ", age=" + age + ")";
        }
    }


    // ─────────────────────────────────────────────────────────────
    // 2. Comparator — external pluggable ordering
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 3. Null-safe Comparators
    // ─────────────────────────────────────────────────────────────

    /**
     * By default, Comparator.comparing() throws NPE on null elements.
     * Use Comparator.nullsFirst() or nullsLast() to handle nulls explicitly.
     */
    public static void nullSafeComparators() {
        System.out.println("\n=== Null-Safe Comparators ===");

        List<String> withNulls = new ArrayList<>(Arrays.asList("banana", null, "apple", null, "cherry"));

        // nullsFirst — nulls sort before all others
        withNulls.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
        System.out.println("nullsFirst: " + withNulls);  // [null, null, apple, banana, cherry]

        // nullsLast — nulls sort after all others
        withNulls.sort(Comparator.nullsLast(Comparator.naturalOrder()));
        System.out.println("nullsLast:  " + withNulls);  // [apple, banana, cherry, null, null]
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Comparator in TreeMap / TreeSet
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 5. Common pitfall: integer overflow in manual compareTo
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        nullSafeComparators();
    }
}

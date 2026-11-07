package com.vbforge.practice.comparator.practice_54_comparator_defines_equality;

import java.util.*;

/**
 * TreeMap/TreeSet use the comparator (never equals) to decide "same key": CASE_INSENSITIVE_ORDER merges "apple"/"Apple".
 * Extracted from the old ComparableComparatorDemo deep dive.
 */
public class ComparatorDefinesEqualityDemo {

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


    // ─────────────────────────────────────────────────────────────
    // 4. Comparator in TreeMap / TreeSet
    // ─────────────────────────────────────────────────────────────

    /**
     * Pass a Comparator to TreeSet/TreeMap constructor to override natural order.
     * The comparator also serves as the equality definition for the tree:
     *   compare(a, b) == 0 means a and b are treated as the SAME key/element.
     */
    public static void treeWithComparator() {
        System.out.println("\n=== Comparator in TreeMap & TreeSet ===");

        // Case-insensitive TreeMap
        TreeMap<String, Integer> caseInsensitive = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitive.put("banana", 1);
        caseInsensitive.put("Apple",  2);
        caseInsensitive.put("cherry", 3);
        System.out.println("Case-insensitive TreeMap: " + caseInsensitive);
        // {Apple=2, banana=1, cherry=3} — sorted case-insensitively

        // "apple" and "Apple" are the SAME key under case-insensitive order
        caseInsensitive.put("apple", 99);  // replaces "Apple" entry!
        System.out.println("After put 'apple': " + caseInsensitive.get("Apple")); // 99

        // TreeSet with reverse-order string comparator (longest first)
        TreeSet<String> byLength = new TreeSet<>(
            Comparator.comparingInt(String::length)
                      .reversed()
                      .thenComparing(Comparator.naturalOrder())
        );
        byLength.addAll(Arrays.asList("fig", "apple", "kiwi", "cherry", "date"));
        System.out.println("By length desc, then alpha: " + byLength);
    }

    // ─────────────────────────────────────────────────────────────
    // 5. Common pitfall: integer overflow in manual compareTo
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        treeWithComparator();
    }
}

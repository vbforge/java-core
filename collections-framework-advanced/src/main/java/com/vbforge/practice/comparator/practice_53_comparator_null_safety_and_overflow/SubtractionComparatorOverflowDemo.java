package com.vbforge.practice.comparator.practice_53_comparator_null_safety_and_overflow;

import java.util.*;

/**
 * The a - b comparator trap: integer overflow. Extracted from the old ComparableComparatorDemo deep dive.
 */
public class SubtractionComparatorOverflowDemo {

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


    // ─────────────────────────────────────────────────────────────
    // 5. Common pitfall: integer overflow in manual compareTo
    // ─────────────────────────────────────────────────────────────

    /**
     * WRONG: return a.value - b.value;
     *   Integer.MIN_VALUE - 1 = Integer.MAX_VALUE (overflow!) → wrong ordering
     *
     * CORRECT: return Integer.compare(a.value, b.value);
     *
     * This is a frequent interview gotcha. Always use Integer.compare(),
     * Double.compare(), etc. instead of subtraction.
     */
    public static void integerOverflowTrap() {
        System.out.println("\n=== Integer Overflow Trap in compareTo ===");

        int a = Integer.MIN_VALUE;  // -2147483648
        int b = 1;
        int subtraction = a - b;   // overflow: wraps to 2147483647 (positive!)
        System.out.println("MIN_VALUE - 1 = " + subtraction + "  (WRONG! overflow)");
        System.out.println("Integer.compare gives: " + Integer.compare(a, b) + "  (CORRECT: negative)");
    }

    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        integerOverflowTrap();
    }
}

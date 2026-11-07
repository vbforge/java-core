package com.vbforge.practice.comparator.practice_51_comparable_natural_order;

import java.util.*;

/**
 * compareTo defines the natural order, but compareTo == 0 does NOT imply equals(): a TreeSet would treat
 * such elements as duplicates. Extracted from the old ComparableComparatorDemo deep dive.
 */
public class ComparableConsistencyWithEqualsDemo {

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

    public static void comparableDemo() {
        System.out.println("=== Comparable — Natural Order ===");

        List<Student> students = new ArrayList<>(Arrays.asList(
            new Student("Alice",   3.9, 22),
            new Student("Bob",     3.5, 20),
            new Student("Carol",   3.9, 21),
            new Student("Dave",    2.8, 23)
        ));

        Collections.sort(students);  // uses compareTo
        System.out.println("Sorted by GPA desc (natural order):");
        students.forEach(s -> System.out.println("  " + s));

        // TreeSet uses natural order automatically
        TreeSet<Student> ts = new TreeSet<>(students);
        System.out.println("TreeSet first (highest GPA): " + ts.first());

        // Verify contract: compareTo == 0 does NOT mean equals unless overridden
        Student s1 = new Student("X", 3.5, 20);
        Student s2 = new Student("Y", 3.5, 25);
        System.out.println("\ns1.compareTo(s2) == 0: " + (s1.compareTo(s2) == 0));  // true (same GPA)
        System.out.println("s1.equals(s2):         " + s1.equals(s2));               // false (different objects)
        // This inconsistency means TreeSet would treat them as the same element!
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


    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        comparableDemo();
    }
}

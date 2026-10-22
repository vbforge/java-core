package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Demonstrates ORDERING objects with {@link Comparable} and {@link Comparator}.
 *
 * <ul>
 *   <li>{@code Comparable<T>} — the class itself defines ONE "natural" order ({@code compareTo}); {@code String}, {@code Integer} do</li>
 *   <li>{@code Comparator<T>} — an ordering supplied from OUTSIDE; you can have as many as you like</li>
 * </ul>
 *
 * <p>Why it matters: sorting, {@code TreeSet}/{@code TreeMap}, {@code PriorityQueue},
 * {@code Collections.max} all need to know "which one comes first?". Comparators answer that
 * for types you cannot or do not want to change.
 *
 * <p>Contract of {@code compare(a, b)}: negative if {@code a} comes first, zero if they are
 * equivalent for ordering, positive if {@code b} comes first. Only the SIGN matters.
 *
 * <p>Complexity: sorting a list is O(n log n); {@code List.sort} is a stable merge sort variant (TimSort).
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code ComparatorsDemoTest}
 * can verify every claim.
 */
public class ComparatorsDemo {

    // ── 1. Natural ordering ───────────────────────────────────────────────────
    /**
     * {@code String} is {@code Comparable}: natural order is lexicographic by UTF-16 value, so ALL upper-case
     * letters sort before ALL lower-case ones ({@code "Z" < "a"}).
     */
    public static List<String> naturalOrderDemo(List<String> words) {
        List<String> sorted = new ArrayList<>(words);
        Collections.sort(sorted);
        return sorted;
    }

    /** {@link String#CASE_INSENSITIVE_ORDER} is a ready-made Comparator for the human expectation. */
    public static List<String> caseInsensitiveDemo(List<String> words) {
        List<String> sorted = new ArrayList<>(words);
        sorted.sort(String.CASE_INSENSITIVE_ORDER);
        return sorted;
    }


    // ── 2. A custom Comparator, and stability ─────────────────────────────────
    /**
     * A lambda is enough for a one-off ordering. {@code List.sort} is STABLE: elements that compare
     * as equal (same length here) keep their original relative order.
     */
    public static List<String> byLengthStableDemo(List<String> words) {
        List<String> sorted = new ArrayList<>(words);
        sorted.sort((a, b) -> Integer.compare(a.length(), b.length()));
        return sorted;
    }


    // ── 3. comparing / thenComparing ──────────────────────────────────────────
    /**
     * Build multi-key orderings from small pieces instead of hand-written {@code if} chains:
     * department A→Z, then salary HIGH→LOW, then name A→Z.
     */
    public static List<Employee> sortEmployeesDemo(List<Employee> employees) {
        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort(Comparator.comparing(Employee::getDepartment)
                .thenComparing(Comparator.comparingInt(Employee::getSalary).reversed())
                .thenComparing(Employee::getName));
        return sorted;
    }


    // ── 4. reversed() — what exactly is reversed? ─────────────────────────────
    /**
     * <b>Gotcha:</b> {@code reversed()} flips the WHOLE comparator it is called on, including every
     * {@code thenComparing} added before it. Here department is reversed too: Z→A, then salary high→low.
     */
    public static List<Employee> reversedWholeChainDemo(List<Employee> employees) {
        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort(Comparator.comparing(Employee::getDepartment)
                .thenComparingInt(Employee::getSalary)
                .reversed());
        return sorted;
    }

    /**
     * To reverse only the second key, reverse that key's own comparator before attaching it:
     * department A→Z, then salary high→low.
     */
    public static List<Employee> reversedSecondKeyOnlyDemo(List<Employee> employees) {
        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort(Comparator.comparing(Employee::getDepartment)
                .thenComparing(Comparator.comparingInt(Employee::getSalary).reversed()));
        return sorted;
    }


    // ── 5. Null-safe comparators ──────────────────────────────────────────────
    /**
     * Natural order throws {@link NullPointerException} on {@code null}. Wrap the comparator
     * with {@code nullsFirst}/{@code nullsLast} to decide where nulls go.
     */
    public static List<String> nullsLastDemo(List<String> words) {
        List<String> sorted = new ArrayList<>(words);
        sorted.sort(Comparator.nullsLast(Comparator.<String>naturalOrder()));
        return sorted;
    }

    public static List<String> nullsFirstDemo(List<String> words) {
        List<String> sorted = new ArrayList<>(words);
        sorted.sort(Comparator.nullsFirst(Comparator.<String>naturalOrder()));
        return sorted;
    }


    // ── 6. The subtraction trap ───────────────────────────────────────────────
    /**
     * <b>Gotcha:</b> {@code a - b} looks like a comparator but OVERFLOWS for values that are far
     * apart: {@code Integer.MIN_VALUE - 1} wraps around to a huge positive number, claiming that
     * the smallest int is "greater". An inconsistent comparator makes sorts and trees misbehave.
     */
    public static int brokenCompareDemo(int a, int b) {
        return a - b;
    }

    /** Always use {@code Integer.compare} (or {@code Comparator.comparingInt}). */
    public static int safeCompareDemo(int a, int b) {
        return Integer.compare(a, b);
    }


    // ── 7. max / min with a Comparator ────────────────────────────────────────
    /**
     * {@code Collections.max/min} need no sorting: one pass, O(n). The Comparator defines
     * what "largest" means.
     */
    public static Employee topEarnerDemo(List<Employee> employees) {
        return Collections.max(employees, Comparator.comparingInt(Employee::getSalary));
    }

    public static Employee lowestEarnerDemo(List<Employee> employees) {
        return Collections.min(employees, Comparator.comparingInt(Employee::getSalary));
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        List<Employee> team = Arrays.asList(
                new Employee("Zoe", "QA", 4000),
                new Employee("Ann", "DEV", 5000),
                new Employee("Bob", "DEV", 7000),
                new Employee("Cid", "DEV", 5000),
                new Employee("Eve", "OPS", 4500));

        System.out.println("=== 1. natural order ===");
        System.out.println(naturalOrderDemo(Arrays.asList("b", "A", "a", "B")));
        System.out.println(caseInsensitiveDemo(Arrays.asList("b", "A", "a", "B")));

        System.out.println("---");
        System.out.println("=== 2. custom comparator, stable ===");
        System.out.println(byLengthStableDemo(Arrays.asList("ccc", "bb", "aa", "d")));

        System.out.println("---");
        System.out.println("=== 3. comparing / thenComparing ===");
        System.out.println(sortEmployeesDemo(team));

        System.out.println("---");
        System.out.println("=== 4. reversed() ===");
        System.out.println("whole chain reversed: " + reversedWholeChainDemo(team));
        System.out.println("second key reversed:  " + reversedSecondKeyOnlyDemo(team));

        System.out.println("---");
        System.out.println("=== 5. nulls ===");
        System.out.println(nullsLastDemo(Arrays.asList("b", null, "a")));
        System.out.println(nullsFirstDemo(Arrays.asList("b", null, "a")));

        System.out.println("---");
        System.out.println("=== 6. subtraction trap ===");
        System.out.println("a - b          : " + brokenCompareDemo(Integer.MIN_VALUE, 1));
        System.out.println("Integer.compare: " + safeCompareDemo(Integer.MIN_VALUE, 1));

        System.out.println("---");
        System.out.println("=== 7. max / min ===");
        System.out.println(topEarnerDemo(team) + " / " + lowestEarnerDemo(team));
    }
}

package com.vbforge.unit_testing_junit5_assertj;

/**
 * LifecycleDemo — reference material for JUnit 5's test lifecycle and structure.
 *
 * <p>Read {@code LifecycleDemoTest} alongside this file. The demo is intentionally
 * tiny — the <em>test</em> is the teaching artifact: it shows every lifecycle
 * annotation in action and how AAA (Arrange-Act-Assert) reads on the page.
 *
 * <p>Annotations covered:
 * <ul>
 *   <li>{@code @BeforeAll}  — static, runs once before the whole class</li>
 *   <li>{@code @BeforeEach} — runs before every test method (fresh state)</li>
 *   <li>{@code @AfterEach}  — runs after every test method (cleanup)</li>
 *   <li>{@code @AfterAll}   — static, runs once after the whole class</li>
 *   <li>{@code @DisplayName} — human-readable name for a class or method</li>
 *   <li>{@code @Nested}      — inner class grouping related tests</li>
 * </ul>
 *
 * <p>WHY lifecycle matters: without {@code @BeforeEach}, state leaks between
 * tests and you get order-dependent failures — the worst kind of flaky test.
 * A fresh setup per test keeps every test independent.
 */
public class LifecycleDemo {

    /**
     * Simple arithmetic — used by {@code LifecycleDemoTest} to show AAA.
     */
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("division by zero");
        }
        return a / b;
    }

    /**
     * Sign of an integer: -1, 0, or 1.
     */
    public int signum(int n) {
        return Integer.compare(n, 0);
    }

    public static void main(String[] args) {
        System.out.println("=== JUnit 5 lifecycle — see LifecycleDemoTest for the full picture ===");
        System.out.println();
        System.out.println("@BeforeAll  — static, once before the class");
        System.out.println("@BeforeEach — before EVERY test (fresh state)");
        System.out.println("@AfterEach  — after  EVERY test (cleanup)");
        System.out.println("@AfterAll   — static, once after the class");
        System.out.println();
        System.out.println("--- quick smoke check of the demo methods ---");
        LifecycleDemo d = new LifecycleDemo();
        System.out.println("add(2, 3)      = " + d.add(2, 3));
        System.out.println("subtract(5, 2) = " + d.subtract(5, 2));
        System.out.println("multiply(4, 5) = " + d.multiply(4, 5));
        System.out.println("divide(10, 2)  = " + d.divide(10, 2));
        System.out.println("signum(-42)    = " + d.signum(-42));
    }
}
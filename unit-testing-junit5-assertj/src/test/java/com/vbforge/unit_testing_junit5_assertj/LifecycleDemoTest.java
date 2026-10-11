package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LifecycleDemoTest — demonstrates every JUnit 5 lifecycle annotation in action.
 *
 * <p>Read this file alongside {@link LifecycleDemo} — the test is the
 * teaching artifact.
 */
@DisplayName("LifecycleDemo — structure & lifecycle")
class LifecycleDemoTest {

    /** Shared instance rebuilt for every test — a fresh SUT per test. */
    private LifecycleDemo demo;

    /** Runs ONCE for the whole class, before any test method. */
    @BeforeAll
    static void beforeAll() {
        // In real code this might open a DB connection or read a fixture file.
        // Here it just proves the annotation runs once by writing to stderr.
        System.err.println("[@BeforeAll] runs once before the whole class");
    }

    /** Runs after the whole class — pair of @BeforeAll. */
    @AfterAll
    static void afterAll() {
        System.err.println("[@AfterAll] runs once after the whole class");
    }

    /** Runs before EVERY test — ensures each test starts with a fresh object. */
    @BeforeEach
    void setUp() {
        demo = new LifecycleDemo();
    }

    /** Runs after EVERY test — cleanup. */
    @AfterEach
    void tearDown() {
        demo = null;
        System.err.println("[@AfterEach] cleanup after a test");
    }

    // ── 1. add ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. add — Arrange / Act / Assert")
    class AddTest {

        @Test
        @DisplayName("2 + 3 = 5")
        void addsTwoPositive() {
            // Arrange
            int a = 2, b = 3;
            // Act
            int result = demo.add(a, b);
            // Assert
            assertThat(result).isEqualTo(5);
        }

        @Test
        @DisplayName("negative operand is handled")
        void addsNegative() {
            assertThat(demo.add(-5, 3)).isEqualTo(-2);
        }

        @Test
        @DisplayName("identity: 0 + n = n")
        void zeroIdentity() {
            assertThat(demo.add(0, 42)).isEqualTo(42);
        }
    }

    // ── 2. subtract ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. subtract")
    class SubtractTest {

        @Test
        @DisplayName("5 - 2 = 3")
        void simpleSubtraction() {
            assertThat(demo.subtract(5, 2)).isEqualTo(3);
        }

        @Test
        @DisplayName("result can be negative")
        void negativeResult() {
            assertThat(demo.subtract(2, 5)).isEqualTo(-3);
        }
    }

    // ── 3. multiply ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. multiply")
    class MultiplyTest {

        @Test
        @DisplayName("4 * 5 = 20")
        void simpleMultiplication() {
            assertThat(demo.multiply(4, 5)).isEqualTo(20);
        }

        @Test
        @DisplayName("multiplying by zero yields zero")
        void zeroMultiplication() {
            assertThat(demo.multiply(999, 0)).isZero();
        }
    }

    // ── 4. divide ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. divide and exception assertions")
    class DivideTest {

        @Test
        @DisplayName("10 / 2 = 5")
        void simpleDivision() {
            assertThat(demo.divide(10, 2)).isEqualTo(5);
        }

        @Test
        @DisplayName("division by zero throws ArithmeticException")
        void divideByZeroThrows() {
            // AssertJ style — see AssertionsAndExceptionsDemoTest for more
            assertThatThrownBy(() -> demo.divide(5, 0))
                    .isInstanceOf(ArithmeticException.class);
        }
    }

    // ── 5. signum ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. signum")
    class SignumTest {

        @Test
        @DisplayName("positive → 1")
        void positive() {
            assertThat(demo.signum(42)).isEqualTo(1);
        }

        @Test
        @DisplayName("zero → 0")
        void zero() {
            assertThat(demo.signum(0)).isZero();
        }

        @Test
        @DisplayName("negative → -1")
        void negative() {
            assertThat(demo.signum(-1)).isEqualTo(-1);
        }
    }
}
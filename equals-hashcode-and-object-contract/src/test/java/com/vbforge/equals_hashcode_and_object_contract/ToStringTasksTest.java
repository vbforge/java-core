package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.ToStringTasks.Account;
import com.vbforge.equals_hashcode_and_object_contract.ToStringTasks.Money;
import com.vbforge.equals_hashcode_and_object_contract.ToStringTasks.Node;
import com.vbforge.equals_hashcode_and_object_contract.ToStringTasks.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ToStringTasksTest — tests for every method in {@link ToStringTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ToStringTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ToStringTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ToStringTasks — your implementations")
class ToStringTasksTest {

    private ToStringTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ToStringTasks();
    }

    // ── Task 1: Point.toString ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Point.toString()")
    @DisplayName("Task 1 — Point.toString()")
    class PointToStringTest {

        @ParameterizedTest(name = "Point({0}, {1}) → {2}")
        @CsvSource({
                "1,  2,  'Point(x=1, y=2)'",
                "-3, 0,  'Point(x=-3, y=0)'",
                "0,  0,  'Point(x=0, y=0)'",
        })
        @DisplayName("parametrized formats")
        void formats(int x, int y, String expected) {
            assertThat(new Point(x, y).toString()).isEqualTo(expected);
        }

        @Test
        @DisplayName("string concatenation uses it")
        void concatenation() {
            assertThat("p=" + new Point(1, 2)).isEqualTo("p=Point(x=1, y=2)");
        }
    }

    // ── Task 2: Account.toString ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Account.toString()")
    @DisplayName("Task 2 — Account.toString()")
    class AccountToStringTest {

        @Test
        @DisplayName("long IBAN → last four characters visible")
        void longIban() {
            assertThat(new Account("Ann", "DE8937040044").toString()).isEqualTo("Account[owner=Ann, iban=****0044]");
        }

        @Test
        @DisplayName("five characters → still masks all but the last four")
        void fiveCharacters() {
            assertThat(new Account("Ann", "12345").toString()).isEqualTo("Account[owner=Ann, iban=****2345]");
        }

        @Test
        @DisplayName("four characters or fewer → fully masked")
        void shortIban() {
            assertThat(new Account("Ann", "1234").toString()).isEqualTo("Account[owner=Ann, iban=****]");
            assertThat(new Account("Ann", "12").toString()).isEqualTo("Account[owner=Ann, iban=****]");
            assertThat(new Account("Ann", "").toString()).isEqualTo("Account[owner=Ann, iban=****]");
        }

        @Test
        @DisplayName("null IBAN → none")
        void nullIban() {
            assertThat(new Account("Ann", null).toString()).isEqualTo("Account[owner=Ann, iban=none]");
        }

        @Test
        @DisplayName("the full IBAN never appears")
        void neverFull() {
            assertThat(new Account("Ann", "DE8937040044").toString()).doesNotContain("DE8937040044");
        }
    }

    // ── Task 3: Node.toString ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Node.toString()")
    @DisplayName("Task 3 — Node.toString()")
    class NodeToStringTest {

        @Test
        @DisplayName("the root has no parent")
        void root() {
            assertThat(new Node("root", null).toString()).isEqualTo("Node[name=root, parent=none]");
        }

        @Test
        @DisplayName("a child shows its parent's name")
        void child() {
            Node root = new Node("root", null);
            assertThat(new Node("child", root).toString()).isEqualTo("Node[name=child, parent=root]");
        }

        @Test
        @DisplayName("only the parent's NAME is printed, not the whole ancestor chain")
        void noAncestorChain() {
            Node root = new Node("root", null);
            Node child = new Node("child", root);
            assertThat(new Node("leaf", child).toString()).isEqualTo("Node[name=leaf, parent=child]");
        }
    }

    // ── Task 4: defaultFormat ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement defaultFormat()")
    @DisplayName("Task 4 — defaultFormat()")
    class DefaultFormatTest {

        @Test
        @DisplayName("matches Object.toString() for a plain Object")
        void plainObject() {
            Object o = new Object();
            assertThat(tasks.defaultFormat(o)).isEqualTo(o.toString());
        }

        @Test
        @DisplayName("matches Object.toString() for a class that does not override toString")
        void noOverride() {
            Object o = new ToStringDemo.NoToString();
            assertThat(tasks.defaultFormat(o)).isEqualTo(o.toString());
        }

        @Test
        @DisplayName("String: java.lang.String@17862 (\"abc\".hashCode() = 96354 = 0x17862)")
        void stringExample() {
            assertThat(tasks.defaultFormat("abc")).isEqualTo("java.lang.String@17862");
        }

        @Test
        @DisplayName("ignores an overridden toString but uses the overridden hashCode")
        void ignoresToStringButUsesHashCode() {
            ToStringDemo.Point p = new ToStringDemo.Point(1, 2);   // overrides toString, inherits identity hashCode
            assertThat(tasks.defaultFormat(p))
                    .startsWith(ToStringDemo.Point.class.getName() + "@")
                    .endsWith(Integer.toHexString(p.hashCode()))
                    .isNotEqualTo(p.toString());
        }
    }

    // ── Task 5: describe ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement describe()")
    @DisplayName("Task 5 — describe()")
    class DescribeTest {

        @Test
        @DisplayName("null → \"null\"")
        void nullValue() {
            assertThat(tasks.describe(null)).isEqualTo("null");
        }

        @Test
        @DisplayName("non-arrays use toString()")
        void plainObjects() {
            assertThat(tasks.describe("x")).isEqualTo("x");
            assertThat(tasks.describe(42)).isEqualTo("42");
            assertThat(tasks.describe(Arrays.asList(1, 2))).isEqualTo("[1, 2]");
        }

        @Test
        @DisplayName("primitive arrays")
        void primitiveArrays() {
            assertThat(tasks.describe(new int[] {1, 2})).isEqualTo("[1, 2]");
            assertThat(tasks.describe(new double[] {1.5})).isEqualTo("[1.5]");
            assertThat(tasks.describe(new boolean[] {true, false})).isEqualTo("[true, false]");
            assertThat(tasks.describe(new char[] {'a', 'b'})).isEqualTo("[a, b]");
        }

        @Test
        @DisplayName("object arrays, including null elements")
        void objectArrays() {
            assertThat(tasks.describe(new String[] {"a", null})).isEqualTo("[a, null]");
        }

        @Test
        @DisplayName("an empty array → []")
        void emptyArray() {
            assertThat(tasks.describe(new int[0])).isEqualTo("[]");
        }

        @Test
        @DisplayName("a single element has no separator")
        void singleElement() {
            assertThat(tasks.describe(new int[] {7})).isEqualTo("[7]");
        }

        @Test
        @DisplayName("nested arrays are described recursively")
        void nestedArrays() {
            assertThat(tasks.describe(new Object[] {1, new int[] {2, 3}})).isEqualTo("[1, [2, 3]]");
            assertThat(tasks.describe(new int[][] {{1, 2}, {3}})).isEqualTo("[[1, 2], [3]]");
        }
    }

    // ── Task 6: Money.toString ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Money.toString()")
    @DisplayName("Task 6 — Money.toString()")
    class MoneyToStringTest {

        @ParameterizedTest(name = "{0} cents → {1}")
        @CsvSource({
                "1250,       '12.50 EUR'",
                "5,          '0.05 EUR'",
                "0,          '0.00 EUR'",
                "100,        '1.00 EUR'",
                "99,         '0.99 EUR'",
                "123456789,  '1234567.89 EUR'",
        })
        @DisplayName("parametrized: non-negative amounts")
        void nonNegative(long cents, String expected) {
            assertThat(new Money(cents, "EUR").toString()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0} cents → {1}")
        @CsvSource({
                "-50,    '-0.50 EUR'",
                "-1999,  '-19.99 EUR'",
                "-5,     '-0.05 EUR'",
                "-100,   '-1.00 EUR'",
        })
        @DisplayName("parametrized: negative amounts keep their sign")
        void negative(long cents, String expected) {
            assertThat(new Money(cents, "EUR").toString()).isEqualTo(expected);
        }

        @Test
        @DisplayName("the currency is printed as given")
        void currency() {
            assertThat(new Money(1, "USD").toString()).isEqualTo("0.01 USD");
        }
    }
}

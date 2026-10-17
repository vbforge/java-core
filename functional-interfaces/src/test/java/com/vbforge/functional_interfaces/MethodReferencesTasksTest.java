package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.MethodReferencesDemo.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MethodReferencesTasks — your implementations")
class MethodReferencesTasksTest {

    private MethodReferencesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new MethodReferencesTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement trimAndUpper()")
    @DisplayName("Task 1 — trimAndUpper(words)")
    class TrimAndUpperTest {

        @Test
        @DisplayName("example: trims and upper-cases")
        void example() {
            assertThat(tasks.trimAndUpper(Arrays.asList("  ann ", "bob"))).containsExactly("ANN", "BOB");
        }

        @Test
        @DisplayName("example: empty list gives empty list")
        void emptyList() {
            assertThat(tasks.trimAndUpper(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("returns a new list and leaves the input unchanged")
        void inputUnchanged() {
            List<String> input = Arrays.asList(" a ", "b");

            List<String> result = tasks.trimAndUpper(input);

            assertThat(result).isNotSameAs(input);
            assertThat(input).containsExactly(" a ", "b");
        }

        @Test
        @DisplayName("a blank word becomes an empty string")
        void blankWord() {
            assertThat(tasks.trimAndUpper(Collections.singletonList("   "))).containsExactly("");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement toPeople()")
    @DisplayName("Task 2 — toPeople(names)")
    class ToPeopleTest {

        @Test
        @DisplayName("example: one person per name, in order")
        void example() {
            assertThat(tasks.toPeople(Arrays.asList("Ann", "Bob")))
                    .containsExactly(new Person("Ann"), new Person("Bob"));
        }

        @Test
        @DisplayName("example: empty list gives empty list")
        void emptyList() {
            assertThat(tasks.toPeople(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("duplicate names create separate person objects")
        void duplicates() {
            List<Person> people = tasks.toPeople(Arrays.asList("Ann", "Ann"));

            assertThat(people).hasSize(2);
            assertThat(people.get(0)).isNotSameAs(people.get(1));
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement collectInto()")
    @DisplayName("Task 3 — collectInto(words, factory)")
    class CollectIntoTest {

        @Test
        @DisplayName("example: ArrayList keeps order and duplicates")
        void arrayList() {
            List<String> result = tasks.collectInto(Arrays.asList("b", "a", "b"), ArrayList::new);

            assertThat(result).containsExactly("b", "a", "b");
        }

        @Test
        @DisplayName("example: TreeSet sorts and removes duplicates")
        void treeSet() {
            TreeSet<String> result = tasks.collectInto(Arrays.asList("b", "a", "b"), TreeSet::new);

            assertThat(result).containsExactly("a", "b");
        }

        @Test
        @DisplayName("example: empty input gives an empty collection of the requested type")
        void emptyInput() {
            Set<String> result = tasks.collectInto(Collections.<String>emptyList(), HashSet::new);

            assertThat(result).isEmpty();
            assertThat(result).isInstanceOf(HashSet.class);
        }

        @Test
        @DisplayName("the factory is called exactly once")
        void factoryCalledOnce() {
            AtomicInteger calls = new AtomicInteger();

            tasks.collectInto(Arrays.asList("x", "y", "z"), () -> {
                calls.incrementAndGet();
                return new LinkedList<String>();
            });

            assertThat(calls.get()).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement byNameIgnoringCase()")
    @DisplayName("Task 4 — byNameIgnoringCase()")
    class ByNameTest {

        @Test
        @DisplayName("example: sorts alphabetically ignoring case")
        void example() {
            List<Person> people = new ArrayList<>(
                    Arrays.asList(new Person("bob"), new Person("Ann"), new Person("carl")));

            Collections.sort(people, tasks.byNameIgnoringCase());

            assertThat(people).containsExactly(new Person("Ann"), new Person("bob"), new Person("carl"));
        }

        @Test
        @DisplayName("names differing only in case compare as equal")
        void caseInsensitiveEquality() {
            assertThat(tasks.byNameIgnoringCase().compare(new Person("ann"), new Person("ANN"))).isEqualTo(0);
        }

        @Test
        @DisplayName("an earlier letter orders first regardless of case")
        void orderIgnoresCase() {
            assertThat(tasks.byNameIgnoringCase().compare(new Person("Zed"), new Person("adam"))).isPositive();
            assertThat(tasks.byNameIgnoringCase().compare(new Person("adam"), new Person("Zed"))).isNegative();
        }
    }
}

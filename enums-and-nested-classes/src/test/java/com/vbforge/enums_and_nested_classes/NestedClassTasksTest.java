package com.vbforge.enums_and_nested_classes;

import com.vbforge.enums_and_nested_classes.NestedClassTasks.Team;
import com.vbforge.enums_and_nested_classes.NestedClassTasks.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NestedClassTasks — your implementations")
class NestedClassTasksTest {

    private NestedClassTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new NestedClassTasks();
    }

    private static List<Integer> drain(Iterator<Integer> iterator) {
        List<Integer> values = new ArrayList<>();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return values;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countdown()")
    @DisplayName("Task 1 — countdown(from)")
    class CountdownTest {

        @Test
        @DisplayName("counts down to 1 — example from the Javadoc")
        void example() {
            assertThat(drain(tasks.countdown(3))).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("from 1 yields only 1")
        void fromOne() {
            assertThat(drain(tasks.countdown(1))).containsExactly(1);
        }

        @Test
        @DisplayName("zero and negative starts yield nothing")
        void nothingBelowOne() {
            assertThat(drain(tasks.countdown(0))).isEmpty();
            assertThat(drain(tasks.countdown(-4))).isEmpty();
        }

        @Test
        @DisplayName("hasNext() does not consume elements")
        void hasNextIsIdempotent() {
            Iterator<Integer> iterator = tasks.countdown(2);
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next()).isEqualTo(2);
            assertThat(iterator.next()).isEqualTo(1);
            assertThat(iterator.hasNext()).isFalse();
            assertThat(iterator.hasNext()).isFalse();
        }

        @Test
        @DisplayName("next() on an exhausted iterator throws NoSuchElementException")
        void nextWhenExhausted() {
            Iterator<Integer> iterator = tasks.countdown(1);
            iterator.next();
            assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("every call returns an independent iterator")
        void independentIterators() {
            Iterator<Integer> first = tasks.countdown(3);
            Iterator<Integer> second = tasks.countdown(3);
            first.next();
            first.next();
            assertThat(second.next()).isEqualTo(3);
            assertThat(first).isNotSameAs(second);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement build()")
    @DisplayName("Task 2 — Ticket.Builder.build()")
    class TicketBuilderBuildTest {

        @Test
        @DisplayName("builds a ticket with a trimmed title — example from the Javadoc")
        void example() {
            Ticket ticket = new Ticket.Builder().title("  Fix login ").priority(2).build();
            assertThat(ticket.title()).isEqualTo("Fix login");
            assertThat(ticket.priority()).isEqualTo(2);
        }

        @Test
        @DisplayName("priority defaults to 3")
        void defaultPriority() {
            Ticket ticket = new Ticket.Builder().title("Fix login").build();
            assertThat(ticket.priority()).isEqualTo(3);
        }

        @Test
        @DisplayName("a missing or blank title throws IllegalStateException")
        void invalidTitle() {
            assertThatThrownBy(() -> new Ticket.Builder().priority(2).build())
                    .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> new Ticket.Builder().title("").build())
                    .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> new Ticket.Builder().title("   ").build())
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest(name = "priority {0} is accepted")
        @ValueSource(ints = {1, 2, 3, 4, 5})
        @DisplayName("priorities 1 to 5 are valid")
        void validPriorities(int priority) {
            assertThat(new Ticket.Builder().title("x").priority(priority).build().priority())
                    .isEqualTo(priority);
        }

        @ParameterizedTest(name = "priority {0} is rejected")
        @ValueSource(ints = {-1, 0, 6, 100})
        @DisplayName("priorities outside 1..5 throw IllegalStateException")
        void invalidPriorities(int priority) {
            Ticket.Builder builder = new Ticket.Builder().title("x").priority(priority);
            assertThatThrownBy(builder::build).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("each build() call creates a new Ticket")
        void buildCreatesNewObjects() {
            Ticket.Builder builder = new Ticket.Builder().title("x");
            assertThat(builder.build()).isNotSameAs(builder.build());
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement addPoints()")
    @DisplayName("Task 3 — Team.Member.addPoints(points)")
    class MemberAddPointsTest {

        @Test
        @DisplayName("points count for the member and the team — example from the Javadoc")
        void example() {
            Team team = new Team();
            Team.Member ann = team.join("ann");
            Team.Member bob = team.join("bob");
            ann.addPoints(5);
            bob.addPoints(2);
            assertThat(ann.score()).isEqualTo(5);
            assertThat(bob.score()).isEqualTo(2);
            assertThat(team.totalScore()).isEqualTo(7);
        }

        @Test
        @DisplayName("repeated calls accumulate")
        void accumulates() {
            Team team = new Team();
            Team.Member ann = team.join("ann");
            ann.addPoints(3);
            ann.addPoints(4);
            assertThat(ann.score()).isEqualTo(7);
            assertThat(team.totalScore()).isEqualTo(7);
        }

        @Test
        @DisplayName("teams are independent of each other")
        void teamsAreIndependent() {
            Team red = new Team();
            Team blue = new Team();
            red.join("ann").addPoints(10);
            assertThat(red.totalScore()).isEqualTo(10);
            assertThat(blue.totalScore()).isEqualTo(0);
        }

        @Test
        @DisplayName("zero points change nothing")
        void zeroPoints() {
            Team team = new Team();
            Team.Member ann = team.join("ann");
            ann.addPoints(0);
            assertThat(ann.score()).isEqualTo(0);
            assertThat(team.totalScore()).isEqualTo(0);
        }

        @Test
        @DisplayName("negative points throw IllegalArgumentException and change nothing")
        void negativePoints() {
            Team team = new Team();
            Team.Member ann = team.join("ann");
            ann.addPoints(5);
            assertThatThrownBy(() -> ann.addPoints(-1)).isInstanceOf(IllegalArgumentException.class);
            assertThat(ann.score()).isEqualTo(5);
            assertThat(team.totalScore()).isEqualTo(5);
        }
    }
}

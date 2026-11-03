package com.vbforge.design_patterns.decorator_pattern;

import com.vbforge.design_patterns.decorator_pattern.DecoratorTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DecoratorTasks — your implementations")
class DecoratorTasksTest {

    private DecoratorTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DecoratorTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement base()")
    @DisplayName("Task 1 — base()")
    class BaseTest {

        @Test
        @DisplayName("returns input unchanged")
        void identity() {
            assertThat(tasks.base().process("hello")).isEqualTo("hello");
        }

        @Test
        @DisplayName("empty string stays empty")
        void empty() {
            assertThat(tasks.base().process("")).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement uppercase()")
    @DisplayName("Task 2 — uppercase()")
    class UppercaseTest {

        @Test
        @DisplayName("uppercases the wrapped output")
        void uppercases() {
            assertThat(tasks.uppercase(tasks.base()).process("hi")).isEqualTo("HI");
        }

        @Test
        @DisplayName("nested uppercase is idempotent")
        void idempotent() {
            DecoratorTasks.TextProcessor p = tasks.uppercase(tasks.uppercase(tasks.base()));
            assertThat(p.process("AbC")).isEqualTo("ABC");
        }

        @Test
        @DisplayName("wraps another decorator")
        void nested() {
            DecoratorTasks.TextProcessor p = tasks.uppercase(tasks.trim(tasks.base()));
            assertThat(p.process("  hi  ")).isEqualTo("HI");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement trim()")
    @DisplayName("Task 3 — trim()")
    class TrimTest {

        @Test
        @DisplayName("removes leading and trailing whitespace")
        void trims() {
            assertThat(tasks.trim(tasks.base()).process("  hi  ")).isEqualTo("hi");
        }

        @Test
        @DisplayName("inner spaces preserved")
        void innerSpaces() {
            assertThat(tasks.trim(tasks.base()).process("  a b  ")).isEqualTo("a b");
        }

        @Test
        @DisplayName("no whitespace, unchanged")
        void noWhitespace() {
            assertThat(tasks.trim(tasks.base()).process("hello")).isEqualTo("hello");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement reverse()")
    @DisplayName("Task 4 — reverse()")
    class ReverseTest {

        @Test
        @DisplayName("reverses characters")
        void reverses() {
            assertThat(tasks.reverse(tasks.base()).process("abc")).isEqualTo("cba");
        }

        @Test
        @DisplayName("palindrome is its own reverse")
        void palindrome() {
            assertThat(tasks.reverse(tasks.base()).process("level")).isEqualTo("level");
        }

        @Test
        @DisplayName("empty string stays empty")
        void empty() {
            assertThat(tasks.reverse(tasks.base()).process("")).isEmpty();
        }

        @Test
        @DisplayName("composition with uppercase — order matters")
        void composition() {
            DecoratorTasks.TextProcessor upperThenReverse =
                    tasks.reverse(tasks.uppercase(tasks.base()));
            assertThat(upperThenReverse.process("abc")).isEqualTo("CBA");
        }
    }
}
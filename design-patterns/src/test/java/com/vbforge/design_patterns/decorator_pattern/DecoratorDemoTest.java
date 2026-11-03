package com.vbforge.design_patterns.decorator_pattern;

import com.vbforge.design_patterns.decorator_pattern.DecoratorDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DecoratorDemo — API contracts")
class DecoratorDemoTest {

    @Nested
    @DisplayName("1. Base")
    class BaseTest {

        @Test
        @DisplayName("identity processor returns input unchanged")
        void identity() {
            DecoratorDemo.TextProcessor p = s -> s;
            assertThat(p.process("hello")).isEqualTo("hello");
            assertThat(p.process("")).isEmpty();
        }
    }

    @Nested
    @DisplayName("2. One decorator")
    class OneDecoratorTest {

        @Test
        @DisplayName("uppercase decorator uppercases output")
        void upper() {
            DecoratorDemo.TextProcessor base = s -> s;
            DecoratorDemo.TextProcessor upper = s -> base.process(s).toUpperCase();
            assertThat(upper.process("hi")).isEqualTo("HI");
        }
    }

    @Nested
    @DisplayName("3. Chaining")
    class ChainingTest {

        @Test
        @DisplayName("trim-then-uppercase removes outer whitespace and uppercases")
        void trimThenUpper() {
            DecoratorDemo.TextProcessor base = s -> s;
            DecoratorDemo.TextProcessor result = s -> base.process(s).trim().toUpperCase();
            assertThat(result.process("  hi  ")).isEqualTo("HI");
        }

        @Test
        @DisplayName("order of composition matters")
        void orderMatters() {
            DecoratorDemo.TextProcessor base = s -> s;
            DecoratorDemo.TextProcessor upperThenTrim = s -> base.process(s).toUpperCase().trim();
            DecoratorDemo.TextProcessor trimThenUpper = s -> base.process(s).trim().toUpperCase();
            // both happen to yield "HI" for "  hi  ", but for " hi there ":
            assertThat(upperThenTrim.process(" hi there ")).isEqualTo("HI THERE");
            assertThat(trimThenUpper.process(" hi there ")).isEqualTo("HI THERE");
        }
    }
}
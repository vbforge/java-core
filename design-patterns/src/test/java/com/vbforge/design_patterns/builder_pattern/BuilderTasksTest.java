package com.vbforge.design_patterns.builder_pattern;

import com.vbforge.design_patterns.builder_pattern.BuilderTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BuilderTasks — your implementations")
class BuilderTasksTest {

    private BuilderTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BuilderTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement buildMargherita() and PizzaBuilder")
    @DisplayName("Task 1 — buildMargherita()")
    class MargheritaTest {

        @Test
        @DisplayName("returns a Pizza with the correct fields")
        void fields() {
            BuilderTasks.Pizza p = tasks.buildMargherita();
            assertThat(p.size()).isEqualTo("M");
            assertThat(p.crust()).isEqualTo("thin");
            assertThat(p.cheese()).isTrue();
            assertThat(p.toppings()).containsExactly("tomato", "basil", "mozzarella");
        }

        @Test
        @DisplayName("returns a non-null Pizza")
        void notNull() {
            assertThat(tasks.buildMargherita()).isNotNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement buildPepperoni() and PizzaBuilder")
    @DisplayName("Task 2 — buildPepperoni()")
    class PepperoniTest {

        @Test
        @DisplayName("returns a Pizza with the correct fields")
        void fields() {
            BuilderTasks.Pizza p = tasks.buildPepperoni();
            assertThat(p.size()).isEqualTo("L");
            assertThat(p.crust()).isEqualTo("thick");
            assertThat(p.cheese()).isTrue();
            assertThat(p.toppings()).containsExactly("pepperoni");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement buildCustom() and PizzaBuilder")
    @DisplayName("Task 3 — buildCustom()")
    class CustomTest {

        @Test
        @DisplayName("uses the supplied size and toppings")
        void custom() {
            BuilderTasks.Pizza p = tasks.buildCustom("S", List.of("ham", "olive"));
            assertThat(p.size()).isEqualTo("S");
            assertThat(p.crust()).isEqualTo("thin");
            assertThat(p.cheese()).isTrue();
            assertThat(p.toppings()).containsExactly("ham", "olive");
        }

        @Test
        @DisplayName("empty toppings list is fine")
        void emptyToppings() {
            BuilderTasks.Pizza p = tasks.buildCustom("L", List.of());
            assertThat(p.toppings()).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement PizzaBuilder")
    @DisplayName("Task 4 — PizzaBuilder")
    class PizzaBuilderTest {

        @Test
        @DisplayName("defaults: M / thin / no cheese / no toppings")
        void defaults() {
            BuilderTasks.Pizza p = new BuilderTasks.PizzaBuilder().build();
            assertThat(p.size()).isEqualTo("M");
            assertThat(p.crust()).isEqualTo("thin");
            assertThat(p.cheese()).isFalse();
            assertThat(p.toppings()).isEmpty();
        }

        @Test
        @DisplayName("fluent setters return the builder for chaining")
        void fluent() {
            BuilderTasks.PizzaBuilder b = new BuilderTasks.PizzaBuilder();
            assertThat(b.size("L")).isSameAs(b);
            assertThat(b.crust("thick")).isSameAs(b);
            assertThat(b.cheese(true)).isSameAs(b);
            assertThat(b.toppings("a")).isSameAs(b);
        }

        @Test
        @DisplayName("build reflects every setter")
        void reflects() {
            BuilderTasks.Pizza p = new BuilderTasks.PizzaBuilder()
                    .size("S").crust("thick").cheese(true).toppings("ham", "olive").build();
            assertThat(p.size()).isEqualTo("S");
            assertThat(p.crust()).isEqualTo("thick");
            assertThat(p.cheese()).isTrue();
            assertThat(p.toppings()).containsExactly("ham", "olive");
        }

        @Test
        @DisplayName("built Pizza toppings are unmodifiable")
        void unmodifiable() {
            BuilderTasks.Pizza p = new BuilderTasks.PizzaBuilder().toppings("x").build();
            assertThatThrownBy(() -> p.toppings().add("y"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
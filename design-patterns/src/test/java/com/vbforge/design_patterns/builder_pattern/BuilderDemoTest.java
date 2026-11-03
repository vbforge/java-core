package com.vbforge.design_patterns.builder_pattern;

import com.vbforge.design_patterns.builder_pattern.BuilderDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BuilderDemo — API contracts")
class BuilderDemoTest {

    @Nested
    @DisplayName("1. Telescoping vs builder")
    class TelescopingVsBuilderTest {

        @Test
        @DisplayName("both paths build a Pizza with the same fields")
        void sameFields() {
            BuilderDemo.Pizza tel = new BuilderDemo.Pizza("L", "thin", true, false,
                    List.of("ham"), List.of());
            BuilderDemo.Pizza bld = BuilderDemo.Pizza.builder()
                    .size("L").crust("thin").cheese(true).toppings("ham").build();
            assertThat(bld.size()).isEqualTo(tel.size());
            assertThat(bld.crust()).isEqualTo(tel.crust());
            assertThat(bld.toppings()).isEqualTo(tel.toppings());
        }
    }

    @Nested
    @DisplayName("2. Fluent builder")
    class FluentTest {

        @Test
        @DisplayName("builder produces the configured Pizza")
        void producesPizza() {
            BuilderDemo.Pizza p = BuilderDemo.Pizza.builder()
                    .size("M").crust("thin").cheese(true)
                    .toppings("tomato", "basil", "mozzarella")
                    .build();
            assertThat(p.size()).isEqualTo("M");
            assertThat(p.cheese()).isTrue();
            assertThat(p.toppings()).containsExactly("tomato", "basil", "mozzarella");
        }

        @Test
        @DisplayName("defaults apply when not set")
        void defaults() {
            BuilderDemo.Pizza p = BuilderDemo.Pizza.builder().build();
            assertThat(p.size()).isEqualTo("M");
            assertThat(p.crust()).isEqualTo("thin");
            assertThat(p.cheese()).isFalse();
            assertThat(p.toppings()).isEmpty();
        }

        @Test
        @DisplayName("toppings accumulate across multiple calls")
        void toppingsAccumulate() {
            BuilderDemo.Pizza p = BuilderDemo.Pizza.builder().toppings("a").toppings("b", "c").build();
            assertThat(p.toppings()).containsExactly("a", "b", "c");
        }
    }

    @Nested
    @DisplayName("3. Immutability")
    class ImmutabilityTest {

        @Test
        @DisplayName("mutating the toppings list throws")
        void toppingsUnmodifiable() {
            BuilderDemo.Pizza p = BuilderDemo.Pizza.builder().toppings("x").build();
            assertThatThrownBy(() -> p.toppings().add("y"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("mutating the source list after build has no effect")
        void sourceListIsCopied() {
            List<String> src = new ArrayList<>();
            src.add("a");
            BuilderDemo.Pizza p = BuilderDemo.Pizza.builder().toppings(src.toArray(new String[0])).build();
            src.add("b");
            assertThat(p.toppings()).containsExactly("a");
        }
    }
}
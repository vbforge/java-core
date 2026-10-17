package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.LambdasDemo.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("LambdasDemo — API contracts")
class LambdasDemoTest {

    @Nested
    @DisplayName("1. Syntax forms")
    class Syntax {

        @Test
        @DisplayName("a lambda argument decides the operation")
        void lambdaAsArgument() {
            assertThat(LambdasDemo.applyBinary((a, b) -> a * b, 6, 7)).isEqualTo(42);
            assertThat(LambdasDemo.applyBinary((a, b) -> a - b, 6, 7)).isEqualTo(-1);
        }

        @Test
        @DisplayName("no-argument, one-argument and block-body lambdas")
        void syntaxForms() {
            Supplier<Integer> noParams = () -> 42;
            Function<Integer, Integer> oneParam = x -> x * 2;
            Function<Integer, Integer> block = x -> {
                int doubled = x * 2;
                return doubled + 1;
            };

            assertThat(noParams.get()).isEqualTo(42);
            assertThat(oneParam.apply(21)).isEqualTo(42);
            assertThat(block.apply(5)).isEqualTo(11);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(LambdasDemo::syntaxDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Target typing")
    class TargetTyping {

        @Test
        @DisplayName("the same lambda shape works as a Comparator")
        void asComparator() {
            List<String> words = new ArrayList<>(Arrays.asList("pear", "fig", "banana"));

            words.sort(LambdasDemo.byLength());

            assertThat(words).containsExactly("fig", "pear", "banana");
        }

        @Test
        @DisplayName("and as a BiFunction")
        void asBiFunction() {
            assertThat(LambdasDemo.larger().apply(3, 9)).isEqualTo(9);
            assertThat(LambdasDemo.larger().apply(9, 3)).isEqualTo(9);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(LambdasDemo::targetTypingDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Capturing variables")
    class Capturing {

        @Test
        @DisplayName("each supplier keeps its own copy of the loop value")
        void independentCopies() {
            List<Supplier<Integer>> suppliers = LambdasDemo.tenfoldSuppliers(3);

            assertThat(suppliers).hasSize(3);
            assertThat(suppliers.get(0).get()).isEqualTo(0);
            assertThat(suppliers.get(1).get()).isEqualTo(10);
            assertThat(suppliers.get(2).get()).isEqualTo(20);
        }

        @Test
        @DisplayName("zero suppliers for a count of zero")
        void zeroCount() {
            assertThat(LambdasDemo.tenfoldSuppliers(0)).isEmpty();
        }

        @Test
        @DisplayName("an AtomicInteger can be modified from inside a lambda")
        void atomicCounter() {
            assertThat(LambdasDemo.countWithAtomic(Arrays.asList("a", "b", "c"))).isEqualTo(3);
            assertThat(LambdasDemo.countWithAtomic(new ArrayList<String>())).isEqualTo(0);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(LambdasDemo::captureDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. \"this\" inside a lambda")
    class ThisKeyword {

        @Test
        @DisplayName("in a lambda, this is the enclosing object")
        void lambdaThis() {
            assertThat(new Owner().nameViaLambda()).isEqualTo("owner");
        }

        @Test
        @DisplayName("in an anonymous class, this is the anonymous object")
        void anonymousThis() {
            assertThat(new Owner().nameViaAnonymousClass()).isEqualTo("anonymous");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(LambdasDemo::thisDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("5. Deferred execution and closures")
    class Deferred {

        @Test
        @DisplayName("adder closes over its amount")
        void adderClosure() {
            assertThat(LambdasDemo.adder(5).apply(10)).isEqualTo(15);
            assertThat(LambdasDemo.adder(-1).apply(10)).isEqualTo(9);
        }

        @Test
        @DisplayName("two adders keep independent state")
        void independentAdders() {
            Function<Integer, Integer> addTwo = LambdasDemo.adder(2);
            Function<Integer, Integer> addTen = LambdasDemo.adder(10);

            assertThat(addTwo.apply(1)).isEqualTo(3);
            assertThat(addTen.apply(1)).isEqualTo(11);
        }

        @Test
        @DisplayName("a lambda body does not run until it is called")
        void bodyRunsOnCall() {
            int[] runs = LambdasDemo.deferredRuns();

            assertThat(runs[0]).isEqualTo(0);
            assertThat(runs[1]).isEqualTo(2);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(LambdasDemo::deferredDemo).doesNotThrowAnyException();
        }
    }
}

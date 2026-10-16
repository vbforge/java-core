package com.vbforge.sealed_classes_pattern_matching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.atomic.AtomicInteger;

import static com.vbforge.sealed_classes_pattern_matching.RecordPatternsDemo.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecordPatternsTasks — your implementations")
class RecordPatternsTasksTest {

    private RecordPatternsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new RecordPatternsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countNodes()")
    @DisplayName("countNodes")
    class CountNodesTest {

        @Test
        @DisplayName("a single number is one node")
        void single() {
            assertThat(tasks.countNodes(new Num(5))).isEqualTo(1);
        }

        @Test
        @DisplayName("Neg(Num) has two nodes")
        void neg() {
            assertThat(tasks.countNodes(new Neg(new Num(5)))).isEqualTo(2);
        }

        @Test
        @DisplayName("Add(1, Mul(2, 3)) has five nodes")
        void tree() {
            Expr e = new Add(new Num(1), new Mul(new Num(2), new Num(3)));

            assertThat(tasks.countNodes(e)).isEqualTo(5);
        }

        @Test
        @DisplayName("deep chain of negations")
        void deepChain() {
            Expr e = new Neg(new Neg(new Neg(new Num(1))));

            assertThat(tasks.countNodes(e)).isEqualTo(4);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement toPostfix()")
    @DisplayName("toPostfix")
    class ToPostfixTest {

        @Test
        @DisplayName("a number is written as itself")
        void number() {
            assertThat(tasks.toPostfix(new Num(4))).isEqualTo("4");
            assertThat(tasks.toPostfix(new Num(-4))).isEqualTo("-4");
        }

        @Test
        @DisplayName("negation comes after its operand")
        void neg() {
            assertThat(tasks.toPostfix(new Neg(new Num(4)))).isEqualTo("4 neg");
        }

        @Test
        @DisplayName("Add(1, Mul(2, 3)) → 1 2 3 * +")
        void precedenceByStructure() {
            Expr e = new Add(new Num(1), new Mul(new Num(2), new Num(3)));

            assertThat(tasks.toPostfix(e)).isEqualTo("1 2 3 * +");
        }

        @Test
        @DisplayName("Mul(Add(1, 2), 3) → 1 2 + 3 *")
        void leftSubtreeFirst() {
            Expr e = new Mul(new Add(new Num(1), new Num(2)), new Num(3));

            assertThat(tasks.toPostfix(e)).isEqualTo("1 2 + 3 *");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lineKind()")
    @DisplayName("lineKind")
    class LineKindTest {

        @ParameterizedTest(name = "({0},{1}) → ({2},{3}) is {4}")
        @CsvSource({
                "0, 2, 5, 2, horizontal",
                "1, 0, 1, 9, vertical",
                "0, 0, 3, 3, diagonal",
                "4, 4, 4, 4, point"
        })
        @DisplayName("classifies by comparing coordinates")
        void classifies(int x1, int y1, int x2, int y2, String expected) {
            assertThat(tasks.lineKind(new Line(new Point(x1, y1), new Point(x2, y2)))).isEqualTo(expected);
        }

        @Test
        @DisplayName("non-Line input is not a line")
        void notALine() {
            assertThat(tasks.lineKind("text")).isEqualTo("not a line");
            assertThat(tasks.lineKind(new Point(1, 1))).isEqualTo("not a line");
            assertThat(tasks.lineKind(null)).isEqualTo("not a line");
        }

        @Test
        @DisplayName("a Line with a null Point is not a line")
        void nullPoint() {
            assertThat(tasks.lineKind(new Line(null, new Point(1, 1)))).isEqualTo("not a line");
            assertThat(tasks.lineKind(new Line(new Point(1, 1), null))).isEqualTo("not a line");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement unwrapOr()")
    @DisplayName("unwrapOr")
    class UnwrapOrTest {

        @Test
        @DisplayName("success returns its value")
        void success() {
            assertThat(tasks.unwrapOr(new Success<>(10), 0)).isEqualTo(10);
        }

        @Test
        @DisplayName("failure returns the fallback")
        void failure() {
            assertThat(tasks.unwrapOr(new Failure<Integer>("boom"), 0)).isEqualTo(0);
        }

        @Test
        @DisplayName("works for any value type")
        void otherType() {
            assertThat(tasks.unwrapOr(new Success<>("ok"), "default")).isEqualTo("ok");
            assertThat(tasks.unwrapOr(new Failure<String>("x"), "default")).isEqualTo("default");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement mapResult()")
    @DisplayName("mapResult")
    class MapResultTest {

        @Test
        @DisplayName("success is mapped")
        void success() {
            assertThat(tasks.mapResult(new Success<>(5), x -> x * 2)).isEqualTo(new Success<>(10));
        }

        @Test
        @DisplayName("mapper may change the value type")
        void changesType() {
            assertThat(tasks.mapResult(new Success<>(5), x -> "n" + x)).isEqualTo(new Success<>("n5"));
        }

        @Test
        @DisplayName("failure keeps its message")
        void failure() {
            Result<Integer> failed = new Failure<>("boom");

            assertThat(tasks.mapResult(failed, x -> x * 2)).isEqualTo(new Failure<Integer>("boom"));
        }

        @Test
        @DisplayName("mapper is not called for a failure")
        void mapperNotCalled() {
            AtomicInteger calls = new AtomicInteger();
            Result<Integer> failed = new Failure<>("boom");

            tasks.mapResult(failed, x -> calls.incrementAndGet());

            assertThat(calls.get()).isZero();
        }
    }
}

package com.vbforge.sealed_classes_pattern_matching;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.vbforge.sealed_classes_pattern_matching.RecordPatternsDemo.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecordPatternsDemo — API contracts")
class RecordPatternsDemoTest {

    @Nested
    @DisplayName("1. Record pattern basics")
    class Basics {

        @Test
        @DisplayName("instanceof with a record pattern extracts components")
        void extractsComponents() {
            assertThat(describePoint(new Point(3, 4))).isEqualTo("Point at (3, 4)");
        }

        @Test
        @DisplayName("non-records and null do not match")
        void noMatch() {
            assertThat(describePoint("text")).isEqualTo("not a point");
            assertThat(describePoint(null)).isEqualTo("not a point");
        }
    }

    @Nested
    @DisplayName("2. Nested record patterns")
    class Nesting {

        @Test
        @DisplayName("nested pattern reaches all four coordinates")
        void horizontal() {
            assertThat(isHorizontal(new Line(new Point(0, 2), new Point(5, 2)))).isTrue();
            assertThat(isHorizontal(new Line(new Point(0, 2), new Point(5, 3)))).isFalse();
            assertThat(isHorizontal(new Line(new Point(1, 1), new Point(1, 1)))).isFalse();
        }

        @Test
        @DisplayName("non-Line input is simply false")
        void notALine() {
            assertThat(isHorizontal("line")).isFalse();
            assertThat(isHorizontal(null)).isFalse();
        }

        @Test
        @DisplayName("nested record pattern does not match a null component")
        void nullComponentSkipsNestedPattern() {
            assertThat(isHorizontal(new Line(null, new Point(5, 2)))).isFalse();
            assertThat(startOf(new Line(null, new Point(2, 2)))).isEqualTo("no start point");
        }

        @Test
        @DisplayName("non-null component matches the nested pattern")
        void nonNullComponent() {
            assertThat(startOf(new Line(new Point(1, 1), new Point(2, 2)))).isEqualTo("starts at (1, 1)");
        }
    }

    @Nested
    @DisplayName("3. Sealed interface + records")
    class SealedExpr {

        private final Expr sample = new Add(new Num(2), new Mul(new Num(3), new Num(4)));

        @Test
        @DisplayName("eval walks the tree")
        void evaluates() {
            assertThat(eval(sample)).isEqualTo(14);
            assertThat(eval(new Neg(new Num(5)))).isEqualTo(-5);
        }

        @Test
        @DisplayName("show prints a fully parenthesised expression")
        void shows() {
            assertThat(show(sample)).isEqualTo("(2 + (3 * 4))");
            assertThat(show(new Neg(new Num(5)))).isEqualTo("-5");
        }

        @Test
        @DisplayName("simplify folds constants")
        void foldsConstants() {
            assertThat(simplify(new Add(new Num(2), new Num(3)))).isEqualTo(new Num(5));
            assertThat(simplify(new Mul(new Num(2), new Num(3)))).isEqualTo(new Num(6));
        }

        @Test
        @DisplayName("simplify drops +0 and *1 and collapses double negation")
        void identities() {
            assertThat(simplify(new Add(new Num(0), new Mul(new Num(1), new Num(7))))).isEqualTo(new Num(7));
            assertThat(simplify(new Neg(new Neg(new Num(5))))).isEqualTo(new Num(5));
            assertThat(simplify(new Mul(new Add(new Num(1), new Num(2)), new Num(1)))).isEqualTo(new Num(3));
        }

        @Test
        @DisplayName("simplify turns multiplication by zero into zero")
        void multiplyByZero() {
            assertThat(simplify(new Mul(new Neg(new Num(9)), new Num(0)))).isEqualTo(new Num(0));
        }

        @Test
        @DisplayName("simplify keeps the structure when nothing applies")
        void leavesRest() {
            Expr e = new Add(new Neg(new Num(1)), new Neg(new Neg(new Num(2))));

            assertThat(simplify(e)).isEqualTo(new Add(new Num(-1), new Num(2)));
        }
    }

    @Nested
    @DisplayName("4. Generic records and Result<T>")
    class Results {

        @Test
        @DisplayName("parse returns Success or Failure")
        void parses() {
            assertThat(parse("42")).isEqualTo(new Success<>(42));
            assertThat(parse("abc")).isEqualTo(new Failure<Integer>("not a number: abc"));
        }

        @Test
        @DisplayName("render pattern-matches both variants")
        void renders() {
            assertThat(render(parse("42"))).isEqualTo("ok: 42");
            assertThat(render(parse("abc"))).isEqualTo("error: not a number: abc");
        }
    }
}

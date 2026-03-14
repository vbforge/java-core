package com.vbforge.basics.variables;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Variables and Types")
class VariablesDemoTest {

    // ── Primitive Types ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Primitive type ranges and literals")
    class PrimitiveTypes {

        @Test
        @DisplayName("byte holds values from -128 to 127")
        void byteRange() {
            byte min = -128;
            byte max = 127;
            assertThat(min).isEqualTo(Byte.MIN_VALUE);
            assertThat(max).isEqualTo(Byte.MAX_VALUE);
        }

        @Test
        @DisplayName("int max value equals 2^31 - 1")
        void intMaxValue() {
            assertThat(Integer.MAX_VALUE).isEqualTo(2_147_483_647);
        }

        @Test
        @DisplayName("long requires L suffix — different from int literal")
        void longLiteral() {
            long big = 9_223_372_036_854_775_807L;
            assertThat(big).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("Underscore separators in numeric literals are ignored by compiler")
        void underscoreLiterals() {
            int withUnderscores    = 1_000_000;
            int withoutUnderscores = 1000000;
            assertThat(withUnderscores).isEqualTo(withoutUnderscores);
        }

        @Test
        @DisplayName("Hex, binary and octal literals represent same value")
        void numericBases() {
            int decimal = 255;
            int hex     = 0xFF;
            int binary  = 0b11111111;
            int octal   = 0377;

            assertThat(hex).isEqualTo(decimal);
            assertThat(binary).isEqualTo(decimal);
            assertThat(octal).isEqualTo(decimal);
        }

        @Test
        @DisplayName("char is an unsigned 16-bit integer — holds Unicode code point")
        void charAsUnicode() {
            char letter  = 'A';
            int  codePoint = letter;         // widening: char → int
            char fromCode  = '\u0041';

            assertThat(codePoint).isEqualTo(65);
            assertThat(fromCode).isEqualTo('A');
        }

        @Test
        @DisplayName("double is the default floating-point type — float requires f suffix")
        void floatVsDouble() {
            float  f = 3.14f;
            double d = 3.14;

            // float has ~7 significant digits, double has ~15
            assertThat((double) f).isCloseTo(3.14, within(0.001));
            assertThat(d).isEqualTo(3.14);
        }
    }


    // ── Type Casting ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Type casting — widening and narrowing")
    class TypeCasting {

        @Test
        @DisplayName("Widening cast is implicit — no data loss")
        void wideningCast() {
            int    intVal    = 42;
            long   longVal   = intVal;       // implicit
            double doubleVal = longVal;      // implicit

            assertThat(longVal).isEqualTo(42L);
            assertThat(doubleVal).isEqualTo(42.0);
        }

        @Test
        @DisplayName("Narrowing cast truncates decimal part — does NOT round")
        void narrowingCastTruncates() {
            double value     = 3.99;
            int    truncated = (int) value;

            assertThat(truncated).isEqualTo(3);  // NOT 4 — truncation, not rounding!
        }

        @Test
        @DisplayName("Narrowing cast on negative double truncates toward zero")
        void narrowingNegativeTruncation() {
            double negative  = -3.99;
            int    truncated = (int) negative;

            assertThat(truncated).isEqualTo(-3); // toward zero, not -4
        }

        @ParameterizedTest(name = "double {0} cast to int = {1}")
        @CsvSource({
            "1.1,  1",
            "1.9,  1",
            "-1.1, -1",
            "-1.9, -1",
            "0.99,  0"
        })
        @DisplayName("Narrowing always truncates (toward zero) regardless of decimal")
        void narrowingParameterized(double input, int expected) {
            assertThat((int) input).isEqualTo(expected);
        }

        @Test
        @DisplayName("Casting char to int gives Unicode code point")
        void charToInt() {
            char ch    = 'A';
            int  ascii = ch;
            assertThat(ascii).isEqualTo(65);
        }

        @Test
        @DisplayName("int to char cast gives the character at that code point")
        void intToChar() {
            int  code = 66;
            char ch   = (char) code;
            assertThat(ch).isEqualTo('B');
        }
    }


    // ── Wrapper Types & Autoboxing ────────────────────────────────────────────

    @Nested
    @DisplayName("Wrapper types, autoboxing and Integer cache")
    class WrapperTypes {

        @Test
        @DisplayName("Autoboxing converts int to Integer implicitly")
        void autoboxing() {
            int     primitive = 42;
            Integer boxed     = primitive;   // autoboxing
            int     unboxed   = boxed;       // unboxing

            assertThat(boxed).isEqualTo(42);
            assertThat(unboxed).isEqualTo(primitive);
        }

        @Test
        @DisplayName("Integer cache: values -128 to 127 return same instance (== works)")
        void integerCacheHit() {
            Integer a = 127;
            Integer b = 127;
            // noinspection NumberEquality — intentional test of identity
            assertThat(a == b).isTrue();     // same cached instance
        }

        @Test
        @DisplayName("Integer cache miss: values outside -128..127 give different instances")
        void integerCacheMiss() {
            Integer x = 128;
            Integer y = 128;
            // noinspection NumberEquality — intentional test of identity
            assertThat(x == y).isFalse();   // different instances!
            assertThat(x).isEqualTo(y);     // but equals() still works correctly
        }

        @Test
        @DisplayName("Integer.parseInt converts String to int")
        void parseIntFromString() {
            assertThat(Integer.parseInt("42")).isEqualTo(42);
            assertThat(Integer.parseInt("-100")).isEqualTo(-100);
        }

        @Test
        @DisplayName("Integer wrapper provides useful constants")
        void wrapperConstants() {
            assertThat(Integer.MAX_VALUE).isEqualTo(2_147_483_647);
            assertThat(Integer.MIN_VALUE).isEqualTo(-2_147_483_648);
            assertThat(Integer.SIZE).isEqualTo(32);
            assertThat(Integer.BYTES).isEqualTo(4);
        }

        @ParameterizedTest(name = "parseInt(\"{0}\") = {1}")
        @CsvSource({"0, 0", "1, 1", "-1, -1", "2147483647, 2147483647"})
        @DisplayName("Integer.parseInt handles various valid string inputs")
        void parseIntParameterized(String input, int expected) {
            assertThat(Integer.parseInt(input)).isEqualTo(expected);
        }
    }


    // ── Constants ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Constants and final variables")
    class Constants {

        @Test
        @DisplayName("Static final constants are accessible without an instance")
        void staticFinalConstants() {
            assertThat(VariablesDemo.MAX_RETRY_COUNT).isEqualTo(3);
            assertThat(VariablesDemo.APP_NAME).isEqualTo("java-core");
            assertThat(VariablesDemo.PI_APPROXIMATION).isCloseTo(Math.PI, within(0.0000001));
        }

        @Test
        @DisplayName("final local variable cannot be reassigned — verified via value integrity")
        void finalLocalVariable() {
            final int value = 42;
            // value = 99; // ← would not compile
            assertThat(value).isEqualTo(42);
        }

        @Test
        @DisplayName("final reference cannot be reassigned but object content can change")
        void finalReferenceVsContent() {
            final java.util.List<String> list = new java.util.ArrayList<>();
            list.add("Java");                // ✓ modifying content is fine
            list.add("Core");

            // list = new ArrayList<>();    // ← would not compile
            assertThat(list).containsExactly("Java", "Core");
        }
    }


    // ── var type inference ────────────────────────────────────────────────────

    @Nested
    @DisplayName("var — local variable type inference (Java 10+)")
    class VarTypeInference {

        @Test
        @DisplayName("var infers String type — all String methods available")
        void varInfersString() {
            var message = "Hello, Java!";
            assertThat(message.toUpperCase()).isEqualTo("HELLO, JAVA!");
            assertThat(message.length()).isEqualTo(12);
        }

        @Test
        @DisplayName("var infers int — arithmetic works normally")
        void varInfersInt() {
            var count = 10;
            var result = count * 2;
            assertThat(result).isEqualTo(20);
        }

        @Test
        @DisplayName("var type is fixed at compile time — behaves like explicit type")
        void varIsStaticallyTyped() {
            var number = 42;                 // inferred as int
            // number = "hello";            // ← would not compile — type is fixed
            assertThat(number).isInstanceOf(Integer.class); // autoboxed for assertion
        }

        @Test
        @DisplayName("var works with collections — reduces boilerplate")
        void varWithCollections() {
            var list = new java.util.ArrayList<String>();
            list.add("Java");
            list.add("10");

            assertThat(list).hasSize(2);
            assertThat(list.get(0)).isEqualTo("Java");
        }

        @ParameterizedTest(name = "var infers correct type for value: {0}")
        @ValueSource(strings = {"hello", "world", "java"})
        @DisplayName("var string inference works for multiple values")
        void varWithParameterized(String input) {
            var value = input;
            assertThat(value).isNotNull().isNotBlank();
        }
    }
}

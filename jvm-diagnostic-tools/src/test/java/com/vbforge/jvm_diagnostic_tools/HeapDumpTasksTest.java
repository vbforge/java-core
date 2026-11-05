package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HeapDumpTasks — your implementations")
class HeapDumpTasksTest {

    private static final String HISTO = HeapDumpDemo.SAMPLE_HISTOGRAM;

    private HeapDumpTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new HeapDumpTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement bytesByClass()")
    @DisplayName("bytesByClass")
    class BytesByClass {

        @Test
        @DisplayName("maps class names to bytes (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.bytesByClass(HISTO))
                    .hasSize(5)
                    .containsEntry("[B", 5_000_000L)
                    .containsEntry("java.lang.String", 2_400_000L);
        }

        @Test
        @DisplayName("keeps the printed order")
        void keepsOrder() {
            assertThat(tasks.bytesByClass(HISTO).keySet()).containsExactly(
                    "[B", "java.lang.String", "[Ljava.lang.Object;", "java.lang.Integer", "[[I");
        }

        @Test
        @DisplayName("the Total line is not a class (Javadoc example: size 5)")
        void totalIgnored() {
            assertThat(tasks.bytesByClass(HISTO)).doesNotContainKey("Total");
        }

        @Test
        @DisplayName("empty text gives an empty map (Javadoc example)")
        void emptyText() {
            assertThat(tasks.bytesByClass("")).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement topClassesByBytes()")
    @DisplayName("topClassesByBytes")
    class TopClassesByBytes {

        @Test
        @DisplayName("returns the two biggest classes (Javadoc example)")
        void topTwo() {
            assertThat(tasks.topClassesByBytes(HISTO, 2)).containsExactly("[B", "java.lang.String");
        }

        @Test
        @DisplayName("n larger than the table returns every class, largest first (Javadoc example)")
        void nTooLarge() {
            assertThat(tasks.topClassesByBytes(HISTO, 10)).containsExactly(
                    "[B", "java.lang.String", "[Ljava.lang.Object;", "java.lang.Integer", "[[I");
        }

        @Test
        @DisplayName("n = 0 gives an empty list (Javadoc example)")
        void zero() {
            assertThat(tasks.topClassesByBytes(HISTO, 0)).isEmpty();
        }

        @Test
        @DisplayName("order follows bytes, not the printed order")
        void sortedByBytes() {
            String histogram = "   1:  1  10  Small\n   2:  1  500  Big\n   3:  1  50  Medium\n";
            assertThat(tasks.topClassesByBytes(histogram, 3)).containsExactly("Big", "Medium", "Small");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement averageObjectSize()")
    @DisplayName("averageObjectSize")
    class AverageObjectSize {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "java.lang.String, 120.0",
                "java.lang.Integer, 32.0",
                "'[B', 5000.0"
        })
        @DisplayName("divides bytes by instances (Javadoc examples)")
        void javadocExamples(String className, double expected) {
            assertThat(tasks.averageObjectSize(HISTO, className)).isEqualTo(expected);
        }

        @Test
        @DisplayName("keeps the fractional part")
        void fraction() {
            assertThat(tasks.averageObjectSize("   1:  3  10  Odd\n", "Odd")).isBetween(3.33, 3.34);
        }

        @Test
        @DisplayName("an unlisted class is rejected")
        void unknownClass() {
            assertThatThrownBy(() -> tasks.averageObjectSize(HISTO, "no.Such"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement decodeClassName()")
    @DisplayName("decodeClassName")
    class DecodeClassName {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "'[B', byte[]",
                "'[[I', int[][]",
                "'[Ljava.lang.Object;', java.lang.Object[]",
                "'[[Ljava.lang.String;', java.lang.String[][]",
                "java.lang.String, java.lang.String"
        })
        @DisplayName("turns JVM names into source names (Javadoc examples)")
        void javadocExamples(String jvmName, String expected) {
            assertThat(tasks.decodeClassName(jvmName)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "'[C', char[]",
                "'[D', double[]",
                "'[F', float[]",
                "'[J', long[]",
                "'[S', short[]",
                "'[Z', boolean[]"
        })
        @DisplayName("knows every primitive type code")
        void allPrimitives(String jvmName, String expected) {
            assertThat(tasks.decodeClassName(jvmName)).isEqualTo(expected);
        }
    }
}

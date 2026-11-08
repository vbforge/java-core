package com.vbforge.practice.practice_10_timeouts_and_conditional_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** The functional tests of the algorithm itself (the "what"). Performance and execution control are in TestControlTest. */
class EditDistanceTest {

    @ParameterizedTest(name = "distance(\"{0}\", \"{1}\") = {2}")
    @CsvSource(value = {
            "kitten, sitting, 3",
            "flaw,   lawn,    2",
            "same,   same,    0",
            "'',     abc,     3",     // empty vs text = insertions only
            "abc,    '',      3",
            "'',     '',      0",
            "a,      b,       1"
    })
    void distance_knownPairs(String a, String b, int expected) {
        assertThat(EditDistance.levenshtein(a, b)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"kitten, sitting", "abc, xyz", "hello, ''"})
    void distance_isSymmetric(String a, String b) {
        assertThat(EditDistance.levenshtein(a, b)).isEqualTo(EditDistance.levenshtein(b, a));
    }

    @ParameterizedTest
    @CsvSource({"abc, abd, abz", "kitten, sitting, mitten"})
    void distance_obeysTriangleInequality(String a, String b, String c) {
        assertThat(EditDistance.levenshtein(a, c))
                .isLessThanOrEqualTo(EditDistance.levenshtein(a, b) + EditDistance.levenshtein(b, c));
    }

    @org.junit.jupiter.api.Test
    void nullArgument_isRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> EditDistance.levenshtein(null, "a"));
        assertThatIllegalArgumentException().isThrownBy(() -> EditDistance.levenshtein("a", null));
    }
}

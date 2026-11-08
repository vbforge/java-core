package com.vbforge.practice.practice_5_parameterized_tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Technique: @ValueSource (one argument), @NullAndEmptySource (the inputs people forget). */
class IsbnValidatorTest {

    @ParameterizedTest(name = "[{index}] \"{0}\" is a valid ISBN-13")
    @ValueSource(strings = {"9780306406157", "978-3-16-148410-0", "978 1 86197 271 2"})
    void validIsbn_isAccepted(String isbn) {
        assertThat(IsbnValidator.isValid(isbn)).isTrue();
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" is rejected")
    @ValueSource(strings = {
            "9780306406158",      // wrong check digit
            "978030640615",       // 12 digits
            "97803064061577",     // 14 digits
            "97803064061AB",      // letters
            "   "                 // blank
    })
    void invalidIsbn_isRejected(String isbn) {
        assertThat(IsbnValidator.isValid(isbn)).isFalse();
    }

    @ParameterizedTest(name = "null/empty input #{index} is rejected without exception")
    @NullAndEmptySource
    void nullAndEmpty_areRejected(String isbn) {
        assertThat(IsbnValidator.isValid(isbn)).isFalse();
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" has a non-digit below '0'")
    @ValueSource(strings = {"97803064061!7", "978030640615/"})
    void characterJustBelowZero_isRejected(String isbn) {
        assertThat(IsbnValidator.isValid(isbn)).isFalse();
    }
}

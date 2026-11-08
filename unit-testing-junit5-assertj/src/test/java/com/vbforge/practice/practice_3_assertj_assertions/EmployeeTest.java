package com.vbforge.practice.practice_3_assertj_assertions;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Validation in a record's compact constructor: invalid objects must not exist at all. */
class EmployeeTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankName_isRejected(String name) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Employee(1, name, "a@b.c", "DEV", 100))
                .withMessage("name must not be blank");
    }

    @Test
    void negativeSalary_isRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Employee(1, "Alice", "a@b.c", "DEV", -1))
                .withMessage("salary must not be negative");
    }
}

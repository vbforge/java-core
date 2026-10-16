package com.vbforge.best_practices_and_solid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CleanCodeTasks — your implementations")
class CleanCodeTasksTest {

    private CleanCodeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CleanCodeTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement classifyAge()")
    @DisplayName("classifyAge(age)")
    class ClassifyAgeTest {

        @ParameterizedTest(name = "age {0} -> {1}")
        @CsvSource({
                "0,   child",
                "5,   child",
                "12,  child",
                "13,  teen",
                "19,  teen",
                "20,  adult",
                "64,  adult",
                "65,  senior",
                "150, senior"
        })
        @DisplayName("maps each age band, including both boundaries")
        void bands(int age, String expected) {
            assertThat(tasks.classifyAge(age)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "age {0} is rejected")
        @ValueSource(ints = {-1, 151, -100, 1000})
        @DisplayName("ages outside 0..150 are rejected")
        void invalidAges(int age) {
            assertThatThrownBy(() -> tasks.classifyAge(age)).isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement overtimePay()")
    @DisplayName("overtimePay(hoursWorked, hourlyRate)")
    class OvertimePayTest {

        @ParameterizedTest(name = "{0} h at {1} -> {2}")
        @CsvSource({
                "40, 20.0, 800.0",
                "45, 20.0, 950.0",
                "10, 15.5, 155.0",
                "0,  20.0, 0.0",
                "41, 10.0, 415.0",
                "60, 10.0, 700.0"
        })
        @DisplayName("regular hours up to 40, then 1.5 times the rate")
        void pay(int hours, double rate, double expected) {
            assertThat(tasks.overtimePay(hours, rate)).isEqualTo(expected);
        }

        @Test
        @DisplayName("negative hours are rejected")
        void negativeHours() {
            assertThatThrownBy(() -> tasks.overtimePay(-1, 20.0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a negative rate is rejected")
        void negativeRate() {
            assertThatThrownBy(() -> tasks.overtimePay(10, -0.5)).isInstanceOf(IllegalArgumentException.class);
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement isStrongPassword()")
    @DisplayName("isStrongPassword(password)")
    class IsStrongPasswordTest {

        @ParameterizedTest(name = "\"{0}\" is strong")
        @CsvSource({"Passw0rd", "Abcdefg1", "xY9xxxxxxxxxxxx", "LONGpassword123"})
        @DisplayName("8+ characters with upper, lower and a digit is strong")
        void strong(String password) {
            assertThat(tasks.isStrongPassword(password)).isTrue();
        }

        @ParameterizedTest(name = "\"{0}\" is weak")
        @CsvSource({"password1", "PASSWORD1", "Password", "Pass1", "Abcdef1"})
        @DisplayName("missing upper, lower, digit or length makes it weak")
        void weak(String password) {
            assertThat(tasks.isStrongPassword(password)).isFalse();
        }

        @Test
        @DisplayName("null is not strong")
        void nullPassword() {
            assertThat(tasks.isStrongPassword(null)).isFalse();
        }

        @Test
        @DisplayName("an empty password is not strong")
        void emptyPassword() {
            assertThat(tasks.isStrongPassword("")).isFalse();
        }
    }
}

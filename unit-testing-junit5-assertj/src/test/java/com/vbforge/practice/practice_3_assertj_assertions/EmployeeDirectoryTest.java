package com.vbforge.practice.practice_3_assertj_assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Technique: the AssertJ toolbox on realistic return types.
 * Rule of thumb: assert on the most specific type (assertThat(list) not assertThat(list.size())),
 * so failure messages show the actual content.
 */
class EmployeeDirectoryTest {

    private EmployeeDirectory directory;

    @BeforeEach
    void setUp() {
        directory = new EmployeeDirectory();
        directory.add(new Employee(1, "Alice", "alice@corp.com", "DEV", 5000));
        directory.add(new Employee(2, "Bob", "bob@corp.com", "QA", 3000));
        directory.add(new Employee(3, "Carol", "carol@corp.com", "DEV", 6000));
        directory.add(new Employee(4, "Dave", "dave@corp.com", "OPS", 4000));
    }

    // ----- lists: contains*, extracting, tuple, filteredOn -----

    @Test
    void inDepartment_returnsSortedByName() {
        List<Employee> dev = directory.inDepartment("DEV");

        assertThat(dev)
                .hasSize(2)
                .extracting(Employee::name)
                .containsExactly("Alice", "Carol");          // exact content AND order
    }

    @Test
    void topEarners_extractsSeveralFieldsAsTuples() {
        assertThat(directory.topEarners(2))
                .extracting(Employee::name, Employee::salary)
                .containsExactly(tuple("Carol", 6000), tuple("Alice", 5000));
    }

    @Test
    void containsExactlyInAnyOrder_ignoresOrder() {
        assertThat(directory.topEarners(4))
                .extracting(Employee::name)
                .containsExactlyInAnyOrder("Dave", "Bob", "Carol", "Alice");
    }

    @Test
    void filteredOn_andAllSatisfy_checkEveryElement() {
        List<Employee> top = directory.topEarners(4);

        assertThat(top).filteredOn(e -> e.salary() > 4500).extracting(Employee::name)
                .containsOnly("Alice", "Carol");
        assertThat(top).allSatisfy(e -> assertThat(e.email()).endsWith("@corp.com"));
        assertThat(top).noneMatch(e -> e.salary() < 0);
    }

    @Test
    void emptyResult_isEmptyNotNull() {
        assertThat(directory.inDepartment("HR")).isNotNull().isEmpty();
    }

    // ----- map -----

    @Test
    void headcountByDepartment_mapAssertions() {
        Map<String, Long> counts = directory.headcountByDepartment();

        assertThat(counts)
                .containsOnlyKeys("DEV", "OPS", "QA")
                .containsEntry("DEV", 2L)
                .doesNotContainKey("HR");
    }

    // ----- Optional / OptionalDouble -----

    @Test
    void findByEmail_isCaseInsensitive_andReturnsOptional() {
        assertThat(directory.findByEmail("ALICE@corp.com"))
                .isPresent()
                .get()
                .extracting(Employee::name)
                .isEqualTo("Alice");
        assertThat(directory.findByEmail("nobody@corp.com")).isEmpty();
    }

    @Test
    void averageSalary_numbersWithOffset() {
        double avg = directory.averageSalary("DEV").orElseThrow();

        assertThat(avg).isEqualTo(5500.0);
        assertThat(avg).isBetween(5000.0, 6000.0);
        assertThat(0.1 + 0.2).isCloseTo(0.3, org.assertj.core.api.Assertions.within(1e-9)); // never == on doubles
        assertThat(directory.averageSalary("HR")).isEmpty();
    }

    // ----- strings -----

    @Test
    void stringAssertions_chain() {
        String email = directory.findByEmail("bob@corp.com").orElseThrow().email();

        assertThat(email)
                .startsWith("bob")
                .contains("@")
                .endsWith(".com")
                .matches("[a-z]+@[a-z]+\\.com")
                .hasSize(12)
                .isEqualToIgnoringCase("BOB@CORP.COM");
    }

    // ----- object comparison -----

    @Test
    void recursiveComparison_comparesFieldByField_andCanIgnoreFields() {
        Employee expected = new Employee(99, "Alice", "alice@corp.com", "DEV", 5000);

        Employee actual = directory.findByEmail("alice@corp.com").orElseThrow();

        assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }

    // ----- failure messages, descriptions, soft assertions, conditions -----

    @Test
    void asDescription_isShownOnFailure_andSoftAssertionsReportEverything() {
        Employee carol = directory.findByEmail("carol@corp.com").orElseThrow();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(carol.name()).as("name").isEqualTo("Carol");
        softly.assertThat(carol.department()).as("department").isEqualTo("DEV");
        softly.assertThat(carol.salary()).as("salary").isEqualTo(6000);
        softly.assertAll();   // collects all failures instead of stopping at the first
    }

    @Test
    void softAssertions_reportAllFailuresAtOnce() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(1).as("first").isEqualTo(2);
        softly.assertThat("a").as("second").isEqualTo("b");

        assertThatThrownBy(softly::assertAll)
                .hasMessageContaining("first")
                .hasMessageContaining("second");
    }

    @Test
    void customCondition_readsLikeADomainRule() {
        Condition<Employee> wellPaid = new Condition<>(e -> e.salary() >= 5000, "paid at least 5000");

        assertThat(directory.topEarners(2)).are(wellPaid);
        assertThat(directory.findByEmail("bob@corp.com").orElseThrow()).is(
                org.assertj.core.api.Assertions.not(wellPaid));
    }

    @Test
    void add_duplicateEmail_isRejected() {
        assertThatThrownBy(() -> directory.add(new Employee(5, "Eve", "ALICE@corp.com", "QA", 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("duplicate email: ALICE@corp.com");
        assertThat(directory.size()).isEqualTo(4);   // nothing was added
    }
}

package com.vbforge.practice.practice_6_boundary_value_analysis;

import static com.vbforge.practice.practice_6_boundary_value_analysis.TicketPriceCalculator.Category.ADULT;
import static com.vbforge.practice.practice_6_boundary_value_analysis.TicketPriceCalculator.Category.CHILD;
import static com.vbforge.practice.practice_6_boundary_value_analysis.TicketPriceCalculator.Category.INFANT;
import static com.vbforge.practice.practice_6_boundary_value_analysis.TicketPriceCalculator.Category.SENIOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.vbforge.practice.practice_6_boundary_value_analysis.TicketPriceCalculator.Category;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Technique: equivalence partitioning + boundary value analysis.
 *
 * Partitions (inputs that must behave the same):  invalid-low | INFANT | CHILD | ADULT | SENIOR | invalid-high
 * Bugs live on the EDGES (< vs <=, off by one), so for every edge test the last value
 * of one partition and the first value of the next: (2, 3), (12, 13), (64, 65), and the outer limits (-1, 0), (130, 131).
 * A test with only "typical" ages (1, 8, 30, 80) passes even when every comparison is off by one.
 */
class TicketPriceBoundaryTest {

    @ParameterizedTest(name = "age {0} is {1}")
    @CsvSource({
            "0,   INFANT",   // lowest valid
            "2,   INFANT",   // last infant
            "3,   CHILD",    // first child
            "12,  CHILD",    // last child
            "13,  ADULT",    // first adult
            "64,  ADULT",    // last adult
            "65,  SENIOR",   // first senior
            "130, SENIOR"    // highest valid
    })
    void categoryFor_boundaries(int age, Category expected) {
        assertThat(TicketPriceCalculator.categoryFor(age)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "age {0} costs {1} cents")
    @CsvSource({
            "2,   0",
            "3,   500",
            "12,  500",
            "13,  1000",
            "64,  1000",
            "65,  700"
    })
    void priceInCents_boundaries(int age, int expected) {
        assertThat(TicketPriceCalculator.priceInCents(age)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "age {0} is outside the valid range")
    @ValueSource(ints = {-1, 131, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void invalidAges_areRejected(int age) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> TicketPriceCalculator.categoryFor(age))
                .withMessageContaining(String.valueOf(age));
    }

    @ParameterizedTest(name = "one typical value per partition: age {0}")
    @CsvSource({"1, INFANT", "8, CHILD", "30, ADULT", "80, SENIOR"})
    void oneRepresentativePerPartition(int age, Category expected) {
        // the cheap smoke layer; the boundary tests above are the ones that find real bugs
        assertThat(TicketPriceCalculator.categoryFor(age)).isEqualTo(expected);
    }
}

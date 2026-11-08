package com.vbforge.practice.practice_5_parameterized_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.vbforge.practice.practice_5_parameterized_tests.ShippingCalculator.Zone;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

/**
 * Technique: every JUnit 5 argument source on one class.
 * @CsvSource (inline table), @CsvFileSource (table in a resource file), @EnumSource,
 * @MethodSource (computed / typed arguments), @ArgumentsSource (reusable provider).
 */
class ShippingCalculatorTest {

    @ParameterizedTest(name = "{0} / {1} g -> {2} cents")
    @CsvSource({
            "DOMESTIC, 1000, 500",     // exactly the first kg: base only
            "DOMESTIC, 1001, 600",     // one gram over: second kg starts
            "EU,       2500, 1700",
            "WORLD,    1,    2500"
    })
    void cost_inlineTable(Zone zone, int grams, int expected) {
        assertThat(ShippingCalculator.costInCents(zone, grams)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "from file: {0} / {1} g -> {2} cents")
    @CsvFileSource(resources = "/practice_5_parameterized_tests/shipping.csv", numLinesToSkip = 1)
    void cost_tableFromResourceFile(Zone zone, int grams, int expected) {
        assertThat(ShippingCalculator.costInCents(zone, grams)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0}: the first kilogram costs the base rate and nothing more")
    @EnumSource(Zone.class)
    void cost_firstKilogram_isAlwaysCheaperThanTwoKilograms(Zone zone) {
        assertThat(ShippingCalculator.costInCents(zone, 1000))
                .isLessThan(ShippingCalculator.costInCents(zone, 2000));
    }

    @ParameterizedTest(name = "{0} is more expensive than DOMESTIC for 3 kg")
    @EnumSource(value = Zone.class, mode = EnumSource.Mode.EXCLUDE, names = "DOMESTIC")
    void cost_otherZones_costMoreThanDomestic(Zone zone) {
        assertThat(ShippingCalculator.costInCents(zone, 3000))
                .isGreaterThan(ShippingCalculator.costInCents(Zone.DOMESTIC, 3000));
    }

    static Stream<Arguments> priceTable() {
        return Stream.of(
                Arguments.of(Zone.DOMESTIC, 30_000, 3400),
                Arguments.of(Zone.EU, 1_500, 1450),
                Arguments.of(Zone.WORLD, 5_000, 4900));
    }

    @ParameterizedTest(name = "method source: {0}, {1} g")
    @MethodSource("priceTable")
    void cost_methodSource(Zone zone, int grams, int expected) {
        assertThat(ShippingCalculator.costInCents(zone, grams)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "weight {0} g is rejected")
    @ArgumentsSource(ShippingProviders.InvalidWeights.class)
    void cost_invalidWeight_throws(int grams) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ShippingCalculator.costInCents(Zone.EU, grams))
                .withMessageContaining(String.valueOf(grams));
    }

    @ParameterizedTest
    @NullSource
    void cost_nullZone_throws(Zone zone) {
        assertThatIllegalArgumentException().isThrownBy(() -> ShippingCalculator.costInCents(zone, 100));
    }
}

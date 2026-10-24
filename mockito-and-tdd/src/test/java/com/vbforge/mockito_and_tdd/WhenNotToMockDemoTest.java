package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.CachingPriceCalculator;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.FakeTaxService;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.FixedTimeSource;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.Money;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.PriceCalculator;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.TaxCalculator;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.TaxService;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.TimeSource;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.TokenPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.verification.TooFewActualInvocations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * WhenNotToMockDemoTest — verifies every claim made in {@link WhenNotToMockDemo}.
 *
 * <p>These tests are always GREEN. Several of them show what goes <em>wrong</em> when
 * you mock something you should not have — the "bad" style is demonstrated on purpose.
 */
@DisplayName("WhenNotToMockDemo — API contracts")
class WhenNotToMockDemoTest {

    // ── 1. Value objects ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Value objects and pure functions")
    class ValueObjectsTest {

        @Test
        @DisplayName("the real Money adds correctly")
        void realMoneyAdds() {
            assertThat(Money.of(250).plus(Money.of(175))).isEqualTo(Money.of(425));
        }

        @Test
        @DisplayName("a mocked Money cannot add — plus() returns null")
        void mockedMoneyIsUseless() {
            Money fake = mock(Money.class);

            assertThat(fake.plus(Money.of(1))).isNull();   // no real behaviour, just a recorder
            assertThat(fake.cents()).isZero();
        }

        @Test
        @DisplayName("value equality makes assertions trivial")
        void valueEquality() {
            assertThat(Money.of(5)).isEqualTo(Money.of(5)).hasSameHashCodeAs(Money.of(5));
        }
    }

    // ── 2. Types you don't own ────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Types you don't own")
    class TypesYouDontOwnTest {

        @Test
        @DisplayName("a fixed TimeSource makes expiry deterministic")
        void fixedTimeSource() {
            TokenPolicy policy = new TokenPolicy(new FixedTimeSource(10_000));

            assertThat(policy.isExpired(6_000, 5_000)).isFalse();   // age 4000 < ttl 5000
            assertThat(policy.isExpired(5_000, 5_000)).isTrue();    // age 5000 >= ttl 5000
            assertThat(policy.isExpired(0, 5_000)).isTrue();
        }

        @Test
        @DisplayName("mocking OUR interface is fine — and shows the time the test chooses")
        void mockOfOwnInterface() {
            TimeSource time = mock(TimeSource.class);
            when(time.nowMillis()).thenReturn(1_000L);

            assertThat(new TokenPolicy(time).isExpired(0, 2_000)).isFalse();
        }

        @Test
        @DisplayName("gotcha: a mocked List accepts add() but still reports size() == 0")
        void mockedListIsNotAList() {
            @SuppressWarnings("unchecked")
            List<String> fakeList = mock(List.class);

            fakeList.add("x");

            assertThat(fakeList.size()).isZero();
            assertThat(fakeList.contains("x")).isFalse();
        }
    }

    // ── 3. Assert outcomes, not call counts ───────────────────────────────────

    @Nested
    @DisplayName("3. Outcomes, not call counts")
    class OutcomesTest {

        @Test
        @DisplayName("plain and caching calculators give the same prices")
        void samePrices() {
            TaxCalculator plain = new PriceCalculator(new FakeTaxService().with("DE", 1900));
            TaxCalculator caching = new CachingPriceCalculator(new FakeTaxService().with("DE", 1900));

            assertThat(plain.withTax(Money.of(1000), "DE")).isEqualTo(Money.of(1190));
            assertThat(caching.withTax(Money.of(1000), "DE")).isEqualTo(Money.of(1190));
            assertThat(plain.withTax(Money.of(2000), "DE")).isEqualTo(caching.withTax(Money.of(2000), "DE"));
        }

        @Test
        @DisplayName("only the NUMBER OF LOOKUPS differs (2 vs 1)")
        void lookupCountsDiffer() {
            FakeTaxService plainRates = new FakeTaxService().with("DE", 1900);
            FakeTaxService cachedRates = new FakeTaxService().with("DE", 1900);
            TaxCalculator plain = new PriceCalculator(plainRates);
            TaxCalculator caching = new CachingPriceCalculator(cachedRates);

            plain.withTax(Money.of(1000), "DE");
            plain.withTax(Money.of(2000), "DE");
            caching.withTax(Money.of(1000), "DE");
            caching.withTax(Money.of(2000), "DE");

            assertThat(plainRates.lookups()).isEqualTo(2);
            assertThat(cachedRates.lookups()).isEqualTo(1);
        }

        @Test
        @DisplayName("an over-specified verify(times(2)) breaks when a harmless cache is introduced")
        void overSpecifiedVerifyBreaks() {
            TaxService mockTax = mock(TaxService.class);
            when(mockTax.basisPointsFor("DE")).thenReturn(1900);
            TaxCalculator caching = new CachingPriceCalculator(mockTax);

            // behaviour for callers is unchanged ...
            assertThat(caching.withTax(Money.of(1000), "DE")).isEqualTo(Money.of(1190));
            assertThat(caching.withTax(Money.of(2000), "DE")).isEqualTo(Money.of(2380));

            // ... but a test welded to "asks the service twice" now fails
            assertThatThrownBy(() -> verify(mockTax, times(2)).basisPointsFor("DE"))
                    .isInstanceOf(TooFewActualInvocations.class);
        }

        @Test
        @DisplayName("an outcome assertion survives the refactoring")
        void outcomeAssertionSurvives() {
            TaxService mockTax = mock(TaxService.class);
            when(mockTax.basisPointsFor("DE")).thenReturn(1900);

            assertThat(new PriceCalculator(mockTax).withTax(Money.of(2000), "DE")).isEqualTo(Money.of(2380));
            assertThat(new CachingPriceCalculator(mockTax).withTax(Money.of(2000), "DE")).isEqualTo(Money.of(2380));
        }
    }

    // ── 4. Fake vs mock ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Fake vs mock")
    class FakeVsMockTest {

        @Test
        @DisplayName("the fake fails realistically for an unknown region")
        void fakeFailsRealistically() {
            TaxCalculator calculator = new PriceCalculator(new FakeTaxService().with("DE", 1900));

            assertThatThrownBy(() -> calculator.withTax(Money.of(1000), "XX"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("unknown region: XX");
        }

        @Test
        @DisplayName("an unstubbed mock silently returns 0 → the wrong price, no error")
        void unstubbedMockHidesBugs() {
            TaxService mockTax = mock(TaxService.class);   // forgot to stub "XX"

            Money price = new PriceCalculator(mockTax).withTax(Money.of(1000), "XX");

            assertThat(price).isEqualTo(Money.of(1000));    // "no tax" — passes, and is wrong
        }

        @Test
        @DisplayName("the fake is reusable: configure once, query many regions")
        void fakeIsReusable() {
            TaxCalculator calculator = new PriceCalculator(
                    new FakeTaxService().with("DE", 1900).with("US", 0).with("HU", 2700));

            assertThat(calculator.withTax(Money.of(1000), "DE")).isEqualTo(Money.of(1190));
            assertThat(calculator.withTax(Money.of(1000), "US")).isEqualTo(Money.of(1000));
            assertThat(calculator.withTax(Money.of(1000), "HU")).isEqualTo(Money.of(1270));
        }
    }
}

package com.vbforge.practice.practice_10_when_not_to_mock;

import java.util.Arrays;
import java.util.List;

/**
 * Assert outcomes, not call counts. A cache is added to the calculator: the gross prices stay
 * the same (outcome check passes), but "the tax service is called once per item" breaks.
 */
public class OutcomeVsCallCountDemo {

    interface TaxService {
        int basisPointsFor(String region);
    }

    static class CountingTaxService implements TaxService {
        int calls;
        @Override
        public int basisPointsFor(String region) { calls++; return 1900; }
    }

    static class TaxCalculator {
        private final TaxService tax;
        TaxCalculator(TaxService tax) { this.tax = tax; }

        long gross(List<Long> netCents, String region) {
            long sum = 0;
            for (long net : netCents) {
                sum += net + net * tax.basisPointsFor(region) / 10000;
            }
            return sum;
        }
    }

    static class CachingTaxCalculator {
        private final TaxService tax;
        CachingTaxCalculator(TaxService tax) { this.tax = tax; }

        long gross(List<Long> netCents, String region) {
            int basisPoints = tax.basisPointsFor(region);          // asked once
            long sum = 0;
            for (long net : netCents) {
                sum += net + net * basisPoints / 10000;
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        List<Long> items = Arrays.asList(1000L, 2000L, 3000L);

        CountingTaxService first = new CountingTaxService();
        long grossBefore = new TaxCalculator(first).gross(items, "DE");
        System.out.println("before: gross = " + grossBefore + ", tax service calls = " + first.calls);

        CountingTaxService second = new CountingTaxService();
        long grossAfter = new CachingTaxCalculator(second).gross(items, "DE");
        System.out.println("after : gross = " + grossAfter + ", tax service calls = " + second.calls);

        System.out.println("outcome check  (gross == 7140): " + (grossAfter == 7140 ? "PASS" : "FAIL"));
        System.out.println("call-count check (3 calls)    : " + (second.calls == 3 ? "PASS" : "FAIL")
                + "  <- breaks on a harmless refactoring");
    }
}

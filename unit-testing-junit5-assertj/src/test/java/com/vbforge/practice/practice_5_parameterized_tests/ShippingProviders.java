package com.vbforge.practice.practice_5_parameterized_tests;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

/** Technique: custom ArgumentsProvider, reusable across test classes with @ArgumentsSource. */
public class ShippingProviders {

    /** Weights that must be rejected: 0, negative and just above the limit. */
    public static class InvalidWeights implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return IntStream.of(0, -1, ShippingCalculator.MAX_WEIGHT_GRAMS + 1).mapToObj(Arguments::of);
        }
    }
}

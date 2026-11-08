package com.vbforge.practice.practice_11_property_style_tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Technique: property-style (generative) testing without any library.
 * Instead of picking examples, state a RULE that must hold for ALL inputs and check it on many generated inputs.
 * Two rules to keep it deterministic and debuggable:
 *   1. use a FIXED seed, so a failure is reproducible;
 *   2. put the seed and the input into the failure message.
 * Libraries (jqwik, junit-quickcheck) add shrinking and smarter generators, the idea is the same.
 */
class RunLengthCodecPropertyTest {

    private static final long SEED = 20_260_509L;
    private static final int CASES = 1_000;
    private static final String ALPHABET = "abcxyzXYZ -_.!?\u00e9\u00fc";   // no digits: the codec rejects them

    /** Small alphabet and short strings on purpose: that makes long runs (the interesting case) likely. */
    private static String randomText(Random random) {
        int length = random.nextInt(40);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(ALPHABET.charAt(random.nextInt(random.nextBoolean() ? 3 : ALPHABET.length())));
        }
        return text.toString();
    }

    @Test
    void property_decodeOfEncode_isTheIdentity() {
        Random random = new Random(SEED);

        for (int i = 0; i < CASES; i++) {
            String text = randomText(random);

            assertThat(RunLengthCodec.decode(RunLengthCodec.encode(text)))
                    .as("round trip, seed=%d, case=%d, input='%s'", SEED, i, text)
                    .isEqualTo(text);
        }
    }

    @Test
    void property_encodingOfRepeatedChar_isCharPlusCount() {
        Random random = new Random(SEED);

        for (int i = 0; i < CASES; i++) {
            char c = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
            int n = 1 + random.nextInt(200);

            assertThat(RunLengthCodec.encode(String.valueOf(c).repeat(n)))
                    .as("char='%s' count=%d", c, n)
                    .isEqualTo(c + String.valueOf(n));
        }
    }

    @Test
    void property_encodedTextHasNoTwoEqualNeighbourLetters_inDifferentRuns() {
        // an invariant of the OUTPUT: consecutive runs always differ in their character
        Random random = new Random(SEED);

        for (int i = 0; i < CASES; i++) {
            String text = randomText(random);
            String encoded = RunLengthCodec.encode(text);
            String letters = encoded.replaceAll("[0-9]+", "");

            for (int k = 1; k < letters.length(); k++) {
                assertThat(letters.charAt(k))
                        .as("adjacent runs in encode('%s') = '%s'", text, encoded)
                        .isNotEqualTo(letters.charAt(k - 1));
            }
        }
    }

    @Test
    void property_concatenatingDifferentEndingAndStart_encodesAsConcatenation() {
        // encode(a + b) == encode(a) + encode(b) whenever the last char of a differs from the first char of b
        Random random = new Random(SEED);
        int checked = 0;

        for (int i = 0; i < CASES; i++) {
            String a = randomText(random);
            String b = randomText(random);
            if (a.isEmpty() || b.isEmpty() || a.charAt(a.length() - 1) == b.charAt(0)) {
                continue;
            }
            checked++;

            assertThat(RunLengthCodec.encode(a + b))
                    .as("a='%s' b='%s'", a, b)
                    .isEqualTo(RunLengthCodec.encode(a) + RunLengthCodec.encode(b));
        }
        assertThat(checked).as("the generator must produce enough usable cases").isGreaterThan(300);
    }
}

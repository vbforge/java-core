package com.vbforge.practice.practice_11_property_style_tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Example-based tests (the classic style): a few hand-picked inputs. They are the baseline, see the property test next to it. */
class RunLengthCodecExampleTest {

    @ParameterizedTest(name = "\"{0}\" encodes to \"{1}\"")
    @CsvSource({"aaabcc, a3b1c2", "a, a1", "abc, a1b1c1", "zzzzzzzzzzzz, z12", "'', ''"})
    void encode_examples(String plain, String encoded) {
        assertThat(RunLengthCodec.encode(plain)).isEqualTo(encoded);
        assertThat(RunLengthCodec.decode(encoded)).isEqualTo(plain);
    }

    @Test
    void encode_digitInInput_isRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> RunLengthCodec.encode("ab1"))
                .withMessageContaining("digits are not supported");
    }

    @ParameterizedTest(name = "decode(\"{0}\") is malformed")
    @ValueSource(strings = {"3a", "a", "a0", "a1b", "1"})
    void decode_malformedInput_isRejected(String malformed) {
        assertThatIllegalArgumentException().isThrownBy(() -> RunLengthCodec.decode(malformed));
    }

    @Test
    void nullInput_isRejectedByBothDirections() {
        assertThatIllegalArgumentException().isThrownBy(() -> RunLengthCodec.encode(null));
        assertThatIllegalArgumentException().isThrownBy(() -> RunLengthCodec.decode(null));
    }
}

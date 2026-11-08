package com.vbforge.practice.practice_11_property_style_tests;

/**
 * Run-length encoding: "aaabcc" -> "a3b1c2". Digits are not allowed in the input (they would make the
 * output ambiguous), which is the kind of rule that property-style tests discover quickly.
 */
public final class RunLengthCodec {

    private RunLengthCodec() {
    }

    public static String encode(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);
            if (Character.isDigit(c)) {
                throw new IllegalArgumentException("digits are not supported: '" + c + "' at " + i);
            }
            int j = i;
            while (j < text.length() && text.charAt(j) == c) {
                j++;
            }
            out.append(c).append(j - i);
            i = j;
        }
        return out.toString();
    }

    public static String decode(String encoded) {
        if (encoded == null) {
            throw new IllegalArgumentException("encoded must not be null");
        }
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < encoded.length()) {
            char c = encoded.charAt(i++);
            if (Character.isDigit(c)) {
                throw new IllegalArgumentException("expected a character but found a digit at " + (i - 1));
            }
            int start = i;
            while (i < encoded.length() && Character.isDigit(encoded.charAt(i))) {
                i++;
            }
            if (start == i) {
                throw new IllegalArgumentException("missing count after '" + c + "'");
            }
            int count = Integer.parseInt(encoded.substring(start, i));
            if (count == 0) {
                throw new IllegalArgumentException("count must be positive after '" + c + "'");
            }
            out.append(String.valueOf(c).repeat(count));
        }
        return out.toString();
    }
}

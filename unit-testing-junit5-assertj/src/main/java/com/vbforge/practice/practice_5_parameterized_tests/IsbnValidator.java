package com.vbforge.practice.practice_5_parameterized_tests;

/** ISBN-13 checksum validation: weights 1,3,1,3,... and the sum must be divisible by 10. */
public final class IsbnValidator {

    private IsbnValidator() {
    }

    public static boolean isValid(String isbn) {
        if (isbn == null) {
            return false;
        }
        String digits = isbn.replace("-", "").replace(" ", "");
        if (digits.length() != 13) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            char c = digits.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
            sum += (c - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }
}

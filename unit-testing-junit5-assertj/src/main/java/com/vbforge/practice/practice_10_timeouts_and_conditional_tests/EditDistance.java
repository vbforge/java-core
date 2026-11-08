package com.vbforge.practice.practice_10_timeouts_and_conditional_tests;

/** Levenshtein distance: the minimum number of single-character insertions, deletions and substitutions. O(n*m). */
public final class EditDistance {

    private EditDistance() {
    }

    public static int levenshtein(String a, String b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("arguments must not be null");
        }
        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int substitution = previous[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                current[j] = Math.min(substitution, Math.min(previous[j] + 1, current[j - 1] + 1));
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[b.length()];
    }
}

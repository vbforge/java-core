package com.vbforge.practice.practice_12_red_green_refactor;

import com.vbforge.practice.practice_11_tdd_check_helper.MiniCheck;
import java.util.regex.Pattern;

/**
 * Red -> green -> refactor on a tiny word counter, frozen as versions v0..v5.
 * Each step adds ONE test, sees it fail (red) for the right reason, writes the simplest code
 * that passes (green: fake it first), and only refactors while everything is green.
 */
public class RedGreenRefactorWordCountDemo {

    interface WordCounter {
        int count(String text);
    }

    private static final String[] INPUTS = {"", "hello", "hello world", "  a   b  "};
    private static final int[] EXPECTED = {0, 1, 2, 2};

    // v0: compiles, returns a wrong value on purpose
    static final WordCounter V0 = text -> -1;
    // v1: fake it
    static final WordCounter V1 = text -> 0;
    // v2: simplest thing for two examples
    static final WordCounter V2 = text -> text.isEmpty() ? 0 : 1;
    // v3: generalize
    static final WordCounter V3 = text -> text.isEmpty() ? 0 : text.split(" ").length;
    // v4: handle repeated / surrounding spaces
    static final WordCounter V4 = text -> text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;

    // v5: refactored on green: same behaviour, intention-revealing names
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    static final WordCounter V5 = text -> {
        String trimmed = text.trim();
        return trimmed.isEmpty() ? 0 : WHITESPACE.split(trimmed).length;
    };

    public static void main(String[] args) {
        System.out.println("STEP 1: empty text has 0 words");
        run("v0 skeleton", V0, 1);
        run("v1 fake it: return 0", V1, 1);

        System.out.println("STEP 2: \"hello\" has 1 word");
        run("v1", V1, 2);
        run("v2 empty ? 0 : 1", V2, 2);

        System.out.println("STEP 3: \"hello world\" has 2 words");
        run("v2", V2, 3);
        run("v3 split(\" \")", V3, 3);

        System.out.println("STEP 4: repeated and surrounding spaces");
        run("v3", V3, 4);
        run("v4 trim + split on \\s+", V4, 4);

        System.out.println("REFACTOR (only on green): same tests, same results");
        run("v5 named pattern + local variable", V5, 4);
    }

    private static void run(String label, WordCounter impl, int tests) {
        System.out.println("  " + label);
        MiniCheck check = new MiniCheck();
        for (int i = 0; i < tests; i++) {
            final String input = INPUTS[i];
            check.check("count(\"" + input + "\") == " + EXPECTED[i], EXPECTED[i], () -> impl.count(input));
        }
        System.out.println("    -> " + check.summary() + (check.isGreen() ? "  GREEN" : "  RED"));
    }
}

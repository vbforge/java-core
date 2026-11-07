package com.vbforge.practice.practice_11_tdd_check_helper;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Tiny stand-in for a test runner (JUnit is test-scope, not available in src/main).
 * PASS  = the value matched; FAIL = wrong value (a meaningful red);
 * ERROR = the check itself blew up (usually a wrong red: fix the test first).
 */
public class MiniCheck {

    private int passed;
    private int failed;
    private int errors;

    public void check(String name, Object expected, Supplier<?> actual) {
        try {
            Object value = actual.get();
            if (Objects.equals(expected, value)) {
                passed++;
                System.out.println("    PASS  " + name);
            } else {
                failed++;
                System.out.println("    FAIL  " + name + ": expected " + expected + " but was " + value);
            }
        } catch (RuntimeException e) {
            errors++;
            System.out.println("    ERROR " + name + ": " + e.getClass().getSimpleName());
        }
    }

    public boolean isGreen() { return failed == 0 && errors == 0; }

    public String summary() {
        return passed + " passed, " + failed + " failed, " + errors + " errors";
    }
}

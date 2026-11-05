package com.vbforge.practice.practice_15_default_values_and_final;

/**
 * Semantics of `final` on variables:
 *   - `final` means assigned exactly once
 *   - `static final` = constant (convention: UPPER_SNAKE_CASE)
 *   - works for fields (static / instance) and for locals
 */
public class FinalVariablesDemo {

    static final double PI              = 3.141592653589793;
    static final int    MAX_RETRY_COUNT = 3;

    private final String id;

    public FinalVariablesDemo(String id) {
        this.id = id;
        // this.id = "other"; // compile error: already assigned
    }

    public String getId() { return id; }

    public static void main(String[] args) {
        System.out.println("--- static final constants ---");
        System.out.println("PI:              " + PI);
        System.out.println("MAX_RETRY_COUNT: " + MAX_RETRY_COUNT);

        System.out.println("\n--- final instance field ---");
        FinalVariablesDemo obj = new FinalVariablesDemo("OBJ-001");
        System.out.println("id: " + obj.getId());

        System.out.println("\n--- final local variable ---");
        final int maxItems = 10;
        System.out.println("maxItems: " + maxItems);
        // maxItems = 20; // compile error
    }
}

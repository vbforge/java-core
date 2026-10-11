package com.vbforge.unit_testing_junit5_assertj;

/**
 * ParameterizedDemo — reference material for JUnit 5's {@code @ParameterizedTest}.
 *
 * <p>See {@code ParameterizedDemoTest} — it shows the four standard sources:
 * {@code @ValueSource}, {@code @CsvSource}, {@code @MethodSource}, and
 * {@code @EnumSource}.
 *
 * <p>WHY parameterized tests matter: they turn "test this method with 12 inputs"
 * into 12 separate results in your IDE, all sharing one body. Failures point at
 * the exact input. Duplicated test methods disappear.
 */
public class ParameterizedDemo {

    public int add(int a, int b) {
        return a + b;
    }

    public boolean isEven(int n) {
        return n % 2 == 0;
    }

    public int square(int n) {
        return n * n;
    }

    /**
     * Classifies a day name as {@code "weekday"} or {@code "weekend"}.
     * Case-insensitive.
     */
    public String dayType(String day) {
        if (day == null) {
            throw new IllegalArgumentException("day cannot be null");
        }
        String d = day.toLowerCase();
        switch (d) {
            case "saturday":
            case "sunday":
                return "weekend";
            case "monday":
            case "tuesday":
            case "wednesday":
            case "thursday":
            case "friday":
                return "weekday";
            default:
                throw new IllegalArgumentException("unknown day: " + day);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Parameterized tests — see ParameterizedDemoTest ===");
        System.out.println();
        System.out.println("@ValueSource   — one array of literals");
        System.out.println("@CsvSource     — comma-separated rows, mapped to parameters");
        System.out.println("@MethodSource  — a static method supplying test arguments");
        System.out.println("@EnumSource    — iterate an enum's constants");
        System.out.println();
        ParameterizedDemo d = new ParameterizedDemo();
        System.out.println("--- smoke check ---");
        System.out.println("add(2, 3)         = " + d.add(2, 3));
        System.out.println("isEven(4)         = " + d.isEven(4));
        System.out.println("square(5)         = " + d.square(5));
        System.out.println("dayType(\"Sat\")    = " + d.dayType("saturday"));
    }
}
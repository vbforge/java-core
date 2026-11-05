package com.vbforge.practice.practice_5_if_validation_guards;

/**
 * Parameter validation with if:
 *   - form A: return a sentinel value (-1) on bad input - quiet
 *   - form B: throw an exception - loud, preferred for "should never happen" cases
 *
 * Form B still uses if - validation is not a special keyword.
 */
public class PerimeterValidationDemo {

    public static void main(String[] args) {
        System.out.println("--- form A: return -1 on bad input ---");
        System.out.println("perimeter(2.4, 2.6)   = " + perimeter(2.4, 2.6));
        System.out.println("perimeter(0, 2.6)     = " + perimeter(0, 2.6));

        System.out.println("\n--- form B: throw on bad input ---");
        System.out.println("perimeterChecked(2.4, 2.6) = " + perimeterChecked(2.4, 2.6, "Rectangle"));
        try {
            perimeterChecked(1.5, 2.5, "");
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }
        try {
            perimeterChecked(0, -2.5, "Rectangle");
        } catch (IllegalArgumentException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }

    static double perimeter(double a, double b) {
        if (a > 0 && b > 0) {
            return 2 * (a + b);
        }
        return -1;
    }

    static double perimeterChecked(double a, double b, String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("shape name is missing");
        }
        if (a <= 0 || b <= 0) {
            throw new IllegalArgumentException("dimensions must be positive");
        }
        return 2 * (a + b);
    }
}

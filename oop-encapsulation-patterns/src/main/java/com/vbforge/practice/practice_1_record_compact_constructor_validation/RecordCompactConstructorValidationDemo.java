package com.vbforge.practice.practice_1_record_compact_constructor_validation;

/**
 * A compact canonical constructor validates the components before the record is created:
 * an invalid record can never exist.
 */
public class RecordCompactConstructorValidationDemo {

    record Circle(double radius) {
        Circle {
            if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
        }
    }

    record Triangle(double a, double b, double c) {
        Triangle {
            if (a + b <= c || a + c <= b || b + c <= a)
                throw new IllegalArgumentException("Invalid triangle sides");
        }
    }

    public static void main(String[] args) {
        try {
            new Circle(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("Circle(-1): " + e.getMessage());
        }
        try {
            new Triangle(1, 2, 10);
        } catch (IllegalArgumentException e) {
            System.out.println("Triangle(1,2,10): " + e.getMessage());
        }
    }
}

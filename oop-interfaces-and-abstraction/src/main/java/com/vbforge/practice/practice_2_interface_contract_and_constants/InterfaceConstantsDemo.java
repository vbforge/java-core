package com.vbforge.practice.practice_2_interface_contract_and_constants;

/**
 * Interface fields are implicitly public static final: constants shared by every implementer.
 */
public class InterfaceConstantsDemo {

    interface MathConstants {
        double PI = 3.141592653589793;     // = public static final double PI
        double EULER = 2.718281828459045;
    }

    interface Geometry {
        double PI = Math.PI;
        double getArea();
    }

    static class Circle implements Geometry {
        private final double radius;

        Circle(double radius) { this.radius = radius; }

        @Override
        public double getArea() { return PI * radius * radius; }   // inherited constant, no qualifier
    }

    public static void main(String[] args) {
        System.out.println("Geometry.PI         = " + Geometry.PI);
        System.out.println("MathConstants.PI    = " + MathConstants.PI);
        System.out.println("MathConstants.EULER = " + MathConstants.EULER);
        System.out.printf("Circle(2).getArea() = %.4f%n", new Circle(2).getArea());
        // MathConstants.PI = 3;   // compile error: cannot assign a value to final variable
    }
}

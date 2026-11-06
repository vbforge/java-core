package com.vbforge.practice.practice_2_interface_contract_and_constants;

/**
 * An interface is a contract: callers hold the interface type, the object decides the behavior.
 * Two implementations are used through the same Shape reference.
 */
public class InterfaceContractDemo {

    interface Shape {
        double getArea();
    }

    static class Circle implements Shape {
        private final double radius;

        Circle(double radius) { this.radius = radius; }

        @Override
        public double getArea() { return Math.PI * radius * radius; }
    }

    static class Rectangle implements Shape {
        private final double width;
        private final double height;

        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double getArea() { return width * height; }
    }

    public static void main(String[] args) {
        Shape[] shapes = { new Circle(7.01), new Rectangle(3, 4) };
        for (Shape shape : shapes) {
            System.out.printf("%s area via Shape ref: %.4f%n", shape.getClass().getSimpleName(), shape.getArea());
        }
        // Shape s = new Shape();   // compile error: an interface cannot be instantiated
    }
}

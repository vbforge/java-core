package com.vbforge.practice.practice_1_sealed_shapes;

public record Triangle(double a, double b, double c) implements Shape {
    public Triangle {
        if (a + b <= c || a + c <= b || b + c <= a)
            throw new IllegalArgumentException("Invalid triangle sides");
    }

    @Override public double area() {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    @Override public double perimeter() { return a + b + c; }
}

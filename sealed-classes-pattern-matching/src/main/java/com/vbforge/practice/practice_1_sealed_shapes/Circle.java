package com.vbforge.practice.practice_1_sealed_shapes;

/** Records are implicitly final, so they are valid permitted subtypes without extra modifiers. */
public record Circle(double radius) implements Shape {
    public Circle {
        if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
    }

    @Override public double area()      { return Math.PI * radius * radius; }
    @Override public double perimeter() { return 2 * Math.PI * radius; }
}

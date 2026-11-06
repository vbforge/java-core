package com.vbforge.practice.practice_10_final_methods;

public class Circle extends Shape {
    private final double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    // @Override public String getColor() { ... } // compile error: getColor() is final in Shape

    @Override
    public String describe() {
        return "Circle[color=" + getColor() + ", radius=" + radius + "]";
    }
}

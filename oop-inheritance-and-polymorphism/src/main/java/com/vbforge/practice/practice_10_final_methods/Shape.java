package com.vbforge.practice.practice_10_final_methods;

/** Base class with one final method (cannot be overridden) and one non-final (can be). */
public class Shape {
    private final String color;

    public Shape(String color) {
        this.color = color;
    }

    public final String getColor() {
        return color;
    }

    public String describe() {
        return "Shape[" + color + "]";
    }
}

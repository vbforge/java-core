package com.vbforge.practice.practice_1_sealed_shapes;

import java.util.Locale;

/**
 * Pattern-matching switch over a sealed interface is exhaustive: no default is needed,
 * and the build breaks if a permitted subtype is left out. Locale.ROOT keeps the number
 * format the same on every machine.
 */
public class ShapeDescribeDemo {

    static String describeShape(Shape shape) {
        return switch (shape) {
            case Circle c    -> String.format(Locale.ROOT, "Circle    r=%.1f  area=%.2f", c.radius(), c.area());
            case Rectangle r -> String.format(Locale.ROOT, "Rectangle %sx%s  area=%.2f", r.width(), r.height(), r.area());
            case Triangle t  -> String.format(Locale.ROOT, "Triangle  sides %.1f,%.1f,%.1f  area=%.2f",
                    t.a(), t.b(), t.c(), t.area());
            // no default: the compiler knows all permitted subtypes
        };
    }

    public static void main(String[] args) {
        Shape[] shapes = {new Circle(5), new Rectangle(4, 6), new Triangle(3, 4, 5)};
        for (Shape s : shapes) {
            System.out.println(describeShape(s));
        }
    }
}

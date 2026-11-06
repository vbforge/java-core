package com.vbforge.practice.practice_1_sealed_shapes;

import com.vbforge.practice.practice_1_sealed_shapes.Circle;
import com.vbforge.practice.practice_1_sealed_shapes.Rectangle;
import com.vbforge.practice.practice_1_sealed_shapes.Triangle;

/**
 * A sealed interface restricts which types may implement it.
 * The compiler therefore knows every possible Shape: Circle, Rectangle, Triangle.
 *
 * Keywords: sealed (closed hierarchy), permits (the allowed direct subtypes).
 */
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
    double perimeter();
}

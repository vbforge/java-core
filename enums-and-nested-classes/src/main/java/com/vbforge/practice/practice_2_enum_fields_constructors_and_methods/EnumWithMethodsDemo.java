package com.vbforge.practice.practice_2_enum_fields_constructors_and_methods;

/** Enum with final fields and methods computed from them (surface gravity / weight on each planet). */
public class EnumWithMethodsDemo {

    public static void main(String[] args) {
        double earthWeight = 75.0;
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values()) {
            System.out.printf("Weight on %-8s: %6.2f N%n", p, p.surfaceWeight(mass));
        }
    }
}

enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6),
    EARTH  (5.976e+24, 6.37814e6),
    MARS   (6.421e+23, 3.3972e6);

    private final double mass;    // kg
    private final double radius;  // meters
    static final double G = 6.67300E-11;

    // Enum constructor - always private (implicitly)
    Planet(double mass, double radius) {
        this.mass   = mass;
        this.radius = radius;
    }

    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }

    public double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

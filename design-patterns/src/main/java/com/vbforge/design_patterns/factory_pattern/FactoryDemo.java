package com.vbforge.design_patterns.factory_pattern;

import java.util.List;
import java.util.function.Supplier;

/**
 * FactoryDemo — create objects without exposing the construction logic to callers.
 *
 * <p>Topics:
 * <ol>
 *   <li>Factory method — a static method mapping a type name to a {@link Shape}</li>
 *   <li>Abstract factory — a {@code Supplier<Shape>} parameterised by type</li>
 *   <li>Batch — map type names to a list of shapes</li>
 * </ol>
 */
public class FactoryDemo {

    public static void main(String[] args) {
        factoryMethodDemo();
        System.out.println("---");
        abstractFactoryDemo();
        System.out.println("---");
        batchDemo();
    }

    /**
     * Factory method — the simplest factory form.
     *
     * <p><b>Why:</b> callers ask for {@code "circle"} and get a {@code Shape};
     * they don't need to know the concrete class name. Adding a new shape
     * touches exactly one place: this switch.
     *
     * <p><b>Gotcha:</b> return {@code null} on unknown type is a classic bug —
     * the caller NPEs far from the cause. Throw {@link IllegalArgumentException}.
     */
    static void factoryMethodDemo() {
        System.out.println("=== Factory method ===");
        Shape circle = createShape("circle");
        Shape square = createShape("square");
        System.out.println("circle name : " + circle.name());
        System.out.println("circle area : " + circle.area());
        System.out.println("square name : " + square.name());
        try {
            createShape("hexagon");
        } catch (IllegalArgumentException e) {
            System.out.println("unknown     : " + e.getMessage());
        }
    }

    /**
     * Abstract factory — parameterised creation returned as a {@link Supplier}.
     *
     * <p><b>Why:</b> handy when the caller wants to create many instances of
     * one type without repeating the string each time.
     *
     * <p><b>Gotcha:</b> the supplier is only as good as the factory method
     * backing it — validate eagerly.
     */
    static void abstractFactoryDemo() {
        System.out.println("=== Abstract factory ===");
        Supplier<Shape> circles = shapeSupplier("circle");
        Shape c1 = circles.get();
        Shape c2 = circles.get();
        System.out.println("c1 == c2  : " + (c1 == c2));
        System.out.println("c1 name   : " + c1.name());
    }

    /**
     * Batch creation — apply the factory to a list of type names.
     *
     * <p><b>Why:</b> very common in configuration-driven code (JSON, YAML, DB)
     * where the set of types is only known at runtime.
     */
    static void batchDemo() {
        System.out.println("=== Batch ===");
        List<Shape> shapes = createAll(List.of("circle", "square", "triangle"));
        for (Shape s : shapes) {
            System.out.println(s.name() + " area=" + s.area());
        }
    }

    // ── Shared types ─────────────────────────────────────────────────────────

    public interface Shape {
        String name();
        double area();
    }

    public static class Circle implements Shape {
        @Override public String name() { return "circle"; }
        @Override public double area() { return Math.PI * 1.0 * 1.0; }
    }

    public static class Square implements Shape {
        @Override public String name() { return "square"; }
        @Override public double area() { return 1.0; }
    }

    public static class Triangle implements Shape {
        @Override public String name() { return "triangle"; }
        @Override public double area() { return 0.5 * 1.0 * 1.0; }
    }

    public static Shape createShape(String type) {
        if (type == null) throw new IllegalArgumentException("type is null");
        switch (type.toLowerCase()) {
            case "circle":   return new Circle();
            case "square":   return new Square();
            case "triangle": return new Triangle();
            default: throw new IllegalArgumentException("Unknown shape: " + type);
        }
    }

    public static Supplier<Shape> shapeSupplier(String type) {
        createShape(type);          // validate now
        return () -> createShape(type);
    }

    public static List<Shape> createAll(List<String> types) {
        return types.stream().map(FactoryDemo::createShape).collect(java.util.stream.Collectors.toList());
    }
}
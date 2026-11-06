package com.vbforge.practice.practice_6_generic_interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * One generic contract, three implementations that each fix T to a different type:
 * Square -> Integer, Circle -> Double, Triangle -> Float.
 * A consumer that does not care about T takes {@code Shape<?>} and reads the result as Object.
 */
public class GenericInterfaceImplementationsDemo {

    public static void main(String[] args) {

        // ================== Scene 1: per-type binding ==================
        System.out.println("=== Scene 1: per-type binding ===");

        System.out.println("-----------square-------------");
        int[] sides = {10, 5, 2};
        for (int side : sides) {
            Shape<Integer> shape = new Square(side);
            System.out.println(shape.calculateArea());
        }

        System.out.println("-----------circle-------------");
        double[] radii = {10.0, 5.0, 2.0};
        for (double radius : radii) {
            Shape<Double> shape = new Circle(radius);
            System.out.printf("%.3f%n", shape.calculateArea());
        }

        System.out.println("-----------triangle-------------");
        float[][] baseAndHeight = {{10.0f, 5.0f}, {5.0f, 5.0f}, {2.0f, 5.0f}};
        for (float[] bh : baseAndHeight) {
            Shape<Float> shape = new Triangle(bh[0], bh[1]);
            System.out.printf("%.2f%n", shape.calculateArea());
        }

        // ================== Scene 2: heterogeneous list, T unknown ==================
        System.out.println("\n=== Scene 2: heterogeneous List<Shape<?>> ===");
        List<Shape<?>> shapeList = new ArrayList<>();
        shapeList.add(new Square(10));
        shapeList.add(new Square(5));
        shapeList.add(new Triangle(10.0f, 5.0f));
        shapeList.add(new Triangle(5.0f, 5.0f));
        shapeList.add(new Circle(10.0));
        shapeList.add(new Circle(5.0));
        printAreas(shapeList);
    }

    // Shape<?> (not the raw Shape): the compiler still checks, the result is read as Object
    private static void printAreas(List<? extends Shape<?>> list) {
        for (Shape<?> shape : list) {
            Object area = shape.calculateArea();
            System.out.println(area + " - " + shape.getClass().getSimpleName());
        }
    }

    // -------- interface + implementations --------
    public interface Shape<T> {

        T calculateArea();
    }

    public static class Square implements Shape<Integer> {

        private final int sideLength;

        public Square(int sideLength) {
            this.sideLength = sideLength;
        }

        @Override
        public Integer calculateArea() {
            return sideLength * sideLength;
        }
    }

    public static class Triangle implements Shape<Float> {

        private final float baseLength;
        private final float height;

        public Triangle(float baseLength, float height) {
            this.baseLength = baseLength;
            this.height = height;
        }

        /**
         * Area of a triangle: A = 1/2 * b * h, where b and h are the base and the height.
         */
        @Override
        public Float calculateArea() {
            return (baseLength * height) * 0.5F;
        }
    }

    public static class Circle implements Shape<Double> {

        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public Double calculateArea() {
            return Math.PI * radius * radius;
        }
    }
}

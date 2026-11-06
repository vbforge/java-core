package com.vbforge.practice.practice_9_abstract_class_implements_interface;

/**
 * Abstract class implementing an interface: the interface gives the contract, the abstract class
 * adds shared state and partial implementation, a concrete subclass finishes the job.
 *
 *   Shape (interface)         declares getArea()
 *       ^
 *   Rectangle (abstract)      implements Shape, holds width/height, has setDimensions(), leaves getArea() abstract
 *       ^
 *   Square (concrete)         constructor + getArea()
 *
 * (The same idea as List + AbstractList.)
 */
public class ShapeAbstractionDemo {

    interface Shape {
        double getArea();
    }

    static abstract class Rectangle implements Shape {
        int width;
        int height;

        public void setDimensions(int width, int height) {
            this.width  = width;
            this.height = height;
        }

        // getArea() stays abstract: subclasses must supply it
        @Override
        public abstract double getArea();
    }

    static class Square extends Rectangle {
        public Square(int side) {
            super.setDimensions(side, side);   // inherited shared method
        }

        @Override
        public double getArea() {
            return width * width;
        }
    }

    public static void main(String[] args) {
        Square square = new Square(5);
        System.out.println("Square area (concrete ref):  " + square.getArea());

        // Rectangle r = new Rectangle();   // compile error: Rectangle is abstract

        // Square IS-A Rectangle (abstract class) AND IS-A Shape (interface)
        Shape viaInterface    = square;
        Rectangle viaAbstract = square;
        System.out.println("Square area (Shape ref):     " + viaInterface.getArea());
        System.out.println("Square area (Rectangle ref): " + viaAbstract.getArea());
    }
}

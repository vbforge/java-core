package com.vbforge.practice.practice_13_polymorphic_dispatch;

/**
 * Late binding when the base class has a real (fallback) implementation.
 *
 * Figure.area() is concrete, not abstract. Rectangle and Triangle override it;
 * a Figure reference runs whichever version the actual object provides.
 */
public class DefaultBaseImplementationOverrideDemo {

    static class Figure {
        protected double dim1, dim2;
        Figure(double dim1, double dim2) { this.dim1 = dim1; this.dim2 = dim2; }
        public double area() {
            System.out.print("Area of the figure not determined ");
            return 0.0;
        }
    }
    static class Rectangle extends Figure {
        Rectangle(double dim1, double dim2) { super(dim1, dim2); }
        @Override public double area() {
            System.out.print("Area of the rectangle ");
            return dim1 * dim2;
        }
    }
    static class Triangle extends Figure {
        Triangle(double dim1, double dim2) { super(dim1, dim2); }
        @Override public double area() {
            System.out.print("Area of the triangle ");
            return dim1 * dim2 / 2;
        }
    }

    public static void main(String[] args) {
        Figure f = new Figure(10.0, 5.0);
        Rectangle r = new Rectangle(9.0, 5.0);
        Triangle t = new Triangle(10.0, 8.0);

        Figure figref;
        figref = r; System.out.println(figref.area());
        figref = t; System.out.println(figref.area());
        figref = f; System.out.println(figref.area());
    }
}

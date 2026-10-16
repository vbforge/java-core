package com.vbforge.best_practices_and_solid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <b>L — Liskov Substitution Principle (LSP).</b>
 *
 * <p>"Code that works with a base type must keep working, unchanged, with any subtype."
 * Compiling is not enough — a subtype must honour the base type's <i>behavioural
 * contract</i>: it may not demand more (stronger preconditions), promise less
 * (weaker postconditions) or surprise callers (new exceptions, broken invariants).
 *
 * <p>Sections:
 * <ol>
 *   <li>The classic {@code Square extends Rectangle} trap</li>
 *   <li>The fix: model what they share, not what they "are"</li>
 *   <li>Subtypes that throw "not supported" (the bird that cannot fly)</li>
 *   <li>A violation inside the JDK: read-only lists</li>
 * </ol>
 */
public class LiskovSubstitutionDemo {

    // ── 1. Square extends Rectangle ───────────────────────────────────────────
    /**
     * What it is: a mutable rectangle promises "setting the width does not change the
     * height". A mutable square cannot keep that promise, because its sides are tied.
     *
     * <p>Why it matters: {@link #resizeAndGetArea} is correct for every
     * {@code MutableRectangle} the author ever saw. Pass a {@code MutableSquare} and
     * it silently returns the wrong answer — no compiler error, no exception.
     *
     * <p>Gotcha: "a square IS-A rectangle" is true in maths and false in code that
     * has mutators. IS-A in OOP means "behaves like", not "is a kind of".
     */
    public static class MutableRectangle {
        protected int width;
        protected int height;

        public MutableRectangle(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int area() {
            return width * height;
        }
    }

    public static class MutableSquare extends MutableRectangle {
        public MutableSquare(int side) {
            super(side, side);
        }

        @Override
        public void setWidth(int width) {
            this.width = width;
            this.height = width;      // keeps the square a square… and breaks the contract
        }

        @Override
        public void setHeight(int height) {
            this.width = height;
            this.height = height;
        }
    }

    /** Client code written against the rectangle contract: width and height are independent. */
    public static int resizeAndGetArea(MutableRectangle rectangle, int width, int height) {
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        return rectangle.area();
    }


    // ── 2. The fix: a shared abstraction, immutable implementations ───────────
    /**
     * What it is: {@link Shape} only promises {@code area()}. {@link Rectangle} and
     * {@link Square} are separate, immutable classes — there is no mutator to break.
     *
     * <p>Why it matters: any code written against {@code Shape} works with every
     * implementation, including ones added next year.
     */
    public interface Shape {
        double area();
    }

    public static class Rectangle implements Shape {
        private final double width;
        private final double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public double area() {
            return width * height;
        }
    }

    public static class Square implements Shape {
        private final double side;

        public Square(double side) {
            this.side = side;
        }

        @Override
        public double area() {
            return side * side;
        }
    }

    public static class Circle implements Shape {
        private final double radius;

        public Circle(double radius) {
            this.radius = radius;
        }

        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }


    // ── 3. "Not supported" subtypes ───────────────────────────────────────────
    /**
     * What it is: {@link BadPenguin} extends a base class that promises {@code fly()},
     * then throws. Every caller that handles {@code BadBird} must now also handle
     * "except penguins".
     *
     * <p>Why it matters: the fix moves {@code fly()} down to {@link FlyingBird}. A
     * {@code Penguin} is simply not a {@code FlyingBird}, so {@link #flyAll} refuses to
     * compile when handed one — the mistake is caught at compile time, not in production.
     *
     * <p>Rule of thumb: an override that throws {@code UnsupportedOperationException}
     * is a design smell pointing at a hierarchy that is too wide.
     */
    public static class BadBird {
        public String fly() {
            return "flap flap";
        }
    }

    public static class BadPenguin extends BadBird {
        @Override
        public String fly() {
            throw new UnsupportedOperationException("Penguins cannot fly");
        }
    }

    public abstract static class Bird {
        private final String name;

        protected Bird(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class FlyingBird extends Bird {
        public FlyingBird(String name) {
            super(name);
        }

        public String fly() {
            return getName() + " flies";
        }
    }

    public static class Penguin extends Bird {
        public Penguin(String name) {
            super(name);
        }

        public String swim() {
            return getName() + " swims";
        }
    }

    public static List<String> flyAll(List<? extends FlyingBird> birds) {
        List<String> results = new ArrayList<>();
        for (FlyingBird bird : birds) {
            results.add(bird.fly());
        }
        return results;
    }


    // ── 4. A violation inside the JDK ─────────────────────────────────────────
    /**
     * What it is: {@code Collections.unmodifiableList(...)} and {@code Arrays.asList(...)}
     * are {@code List}s by type, but {@code add} throws {@code UnsupportedOperationException}.
     *
     * <p>Why it matters: {@code List} does not say "may be read-only", so code that
     * accepts any {@code List} and calls {@code add} can crash. The JDK accepted this
     * trade-off knowingly; in your own APIs, prefer a separate read-only type or
     * document the restriction.
     *
     * <p>Gotcha: the probe below adds and removes an element — use it for the demo, not
     * in production code.
     */
    public static boolean isModifiable(List<Integer> list) {
        try {
            list.add(0);
            list.remove(list.size() - 1);
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void rectangleSquareDemo() {
        System.out.println("Rectangle 5x4 -> " + resizeAndGetArea(new MutableRectangle(1, 1), 5, 4));
        System.out.println("Square    5x4 -> " + resizeAndGetArea(new MutableSquare(1), 5, 4) + "  (expected 20!)");
    }

    static void sharedAbstractionDemo() {
        List<Shape> shapes = java.util.Arrays.asList(new Rectangle(2, 3), new Square(2), new Circle(1));
        for (Shape shape : shapes) {
            System.out.println(shape.getClass().getSimpleName() + " area = " + shape.area());
        }
    }

    static void notSupportedDemo() {
        try {
            new BadPenguin().fly();
        } catch (UnsupportedOperationException e) {
            System.out.println("BadPenguin.fly() -> " + e.getMessage());
        }
        System.out.println(flyAll(Collections.singletonList(new FlyingBird("Sparrow"))));
    }

    static void readOnlyListDemo() {
        System.out.println("ArrayList modifiable?      " + isModifiable(new ArrayList<Integer>()));
        System.out.println("unmodifiableList modifiable? " + isModifiable(Collections.unmodifiableList(new ArrayList<Integer>())));
    }

    public static void main(String[] args) {
        rectangleSquareDemo();
        System.out.println("---");
        sharedAbstractionDemo();
        System.out.println("---");
        notSupportedDemo();
        System.out.println("---");
        readOnlyListDemo();
    }
}

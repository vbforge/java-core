package com.vbforge.practice.practice_5_method_references;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Constructor reference: {@code ClassName::new} is {@code (args) -> new ClassName(args)}.
 * The constructor is chosen by matching the functional interface's parameter list.
 */
public class ConstructorReferenceDemo {

    static class Point {
        final int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static void main(String[] args) {
        // No-arg constructor through a Supplier
        Supplier<ArrayList<String>> listFactory = ArrayList::new;
        ArrayList<String> list = listFactory.get();
        list.add("created via constructor ref");
        System.out.println("list: " + list);

        // One-arg constructor: StringBuilder(String)
        Function<String, StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.apply("initial content");
        System.out.println("sb: " + sb);

        // Two-arg constructor: Point(int, int)
        BiFunction<Integer, Integer, Point> pointFactory = Point::new;
        Point p = pointFactory.apply(3, 7);
        System.out.println("point: " + p);

        // Constructor reference as a factory: Collectors.toCollection takes a Supplier<C>
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        List<String> collected = names.stream()
            .filter(n -> n.length() > 3)
            .collect(Collectors.toCollection(ArrayList::new));
        System.out.println("collected: " + collected);
    }
}

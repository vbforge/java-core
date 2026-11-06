package com.vbforge.practice.practice_14_records;

import java.util.List;

/**
 * A record generates: private final fields, the canonical constructor,
 * accessors named like the components (x(), not getX()), and equals/hashCode/toString.
 * It can still have its own methods and static factories.
 */
public class RecordBasicsDemo {

    public static void main(String[] args) {
        System.out.println("-- Point record --");
        Point p1 = new Point(3.0, 4.0);
        Point origin = Point.origin();

        System.out.println("p1: " + p1);                                   // generated toString
        System.out.println("p1.x(): " + p1.x() + ", p1.y(): " + p1.y());   // accessors
        System.out.printf("distance p1 -> origin: %.2f%n", p1.distanceTo(origin));

        System.out.println("\n-- Generated equals / hashCode --");
        Point pa = new Point(1.0, 2.0);
        Point pb = new Point(1.0, 2.0);
        System.out.println("pa.equals(pb): " + pa.equals(pb) + "   <- value equality");
        System.out.println("pa == pb:      " + (pa == pb) + "  <- different objects");
        System.out.println("same hashCode: " + (pa.hashCode() == pb.hashCode()));

        System.out.println("\n-- Records in collections --");
        List<Point> points = List.of(new Point(0, 0), new Point(1, 1), new Point(3, 4));
        points.forEach(p -> System.out.printf("  %s distance from origin: %.2f%n",
                p, p.distanceTo(Point.origin())));
    }
}

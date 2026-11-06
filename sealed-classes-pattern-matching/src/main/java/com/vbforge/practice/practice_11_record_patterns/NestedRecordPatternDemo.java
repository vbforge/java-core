package com.vbforge.practice.practice_11_record_patterns;

/**
 * Nested record patterns deconstruct several levels at once. A nested record pattern never
 * matches a null component, but var (or a plain type pattern of the component type) does,
 * so the last case catches lines with a null endpoint.
 */
public class NestedRecordPatternDemo {

    record Point(int x, int y) {}
    record Line(Point from, Point to) {}

    static String kind(Line line) {
        return switch (line) {
            case Line(Point(int x1, int y1), Point(int x2, int y2)) when x1 == x2 -> "vertical";
            case Line(Point(int x1, int y1), Point(int x2, int y2)) when y1 == y2 -> "horizontal";
            case Line(Point(var x1, var y1), Point(var x2, var y2))               -> "slanted";
            case Line(var from, var to)                                           -> "has a null endpoint";
        };
    }

    public static void main(String[] args) {
        System.out.println(kind(new Line(new Point(1, 0), new Point(1, 9))));
        System.out.println(kind(new Line(new Point(0, 2), new Point(7, 2))));
        System.out.println(kind(new Line(new Point(0, 0), new Point(3, 4))));
        System.out.println(kind(new Line(null, new Point(3, 4))));
    }
}

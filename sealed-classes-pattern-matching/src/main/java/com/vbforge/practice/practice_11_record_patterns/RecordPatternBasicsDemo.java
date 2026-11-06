package com.vbforge.practice.practice_11_record_patterns;

/**
 * A record pattern tests the type AND deconstructs the components in one step:
 * obj instanceof Point(int x, int y). It works in instanceof and in switch, with guards,
 * and var can stand in for a component type.
 */
public class RecordPatternBasicsDemo {

    record Point(int x, int y) {}

    static String where(Object o) {
        return switch (o) {
            case Point(int x, int y) when x == 0 && y == 0 -> "origin";
            case Point(int x, int y) when x == y           -> "on the diagonal (" + x + ")";
            case Point(var x, var y)                       -> "point " + x + "," + y;
            default                                        -> "not a point";
        };
    }

    public static void main(String[] args) {
        Object obj = new Point(3, 4);
        if (obj instanceof Point(int x, int y)) {
            System.out.println("instanceof: x + y = " + (x + y));
        }

        System.out.println(where(new Point(0, 0)));
        System.out.println(where(new Point(2, 2)));
        System.out.println(where(new Point(1, 5)));
        System.out.println(where("text"));
    }
}

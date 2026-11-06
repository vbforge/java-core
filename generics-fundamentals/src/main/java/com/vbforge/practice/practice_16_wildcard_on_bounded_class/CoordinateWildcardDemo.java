package com.vbforge.practice.practice_16_wildcard_on_bounded_class;

/**
 * Wildcard depth-demo: read progressively deeper fields from a bounded
 * {@code CoordM<T extends Coord2>} using {@code ?}, {@code ? extends Coord3},
 * and {@code ? extends Coord4}. Even a plain {@code ?} still carries the class's own
 * bound (Coord2), so x and y are readable through it.
 */
public class CoordinateWildcardDemo {

    public static void showXY(CoordM<?> obj) {
        System.out.println("Array coordinate (x,y): ");
        for (int i = 0; i < obj.space.length; i++) {
            System.out.print("(" + obj.space[i].x + ", " + obj.space[i].y + "); ");
        }
        System.out.println();
    }

    public static void showXYZ(CoordM<? extends Coord3> obj) {
        System.out.println("Array coordinate (x,y,z): ");
        for (int i = 0; i < obj.space.length; i++) {
            System.out.print("(" + obj.space[i].x + ", "
                    + obj.space[i].y + "," + obj.space[i].z + "); ");
        }
        System.out.println();
    }

    public static void showAll(CoordM<? extends Coord4> obj) {
        System.out.println("Array coordinate (x, y, z, t):");
        for (int i = 0; i < obj.space.length; i++) {
            System.out.print("(" + obj.space[i].x + ", " + obj.space[i].y
                    + ", " + obj.space[i].z + ", " + obj.space[i].t + ");");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Coord2[] sp_1 = {new Coord2(0, 0), new Coord2(1, 1),
                new Coord2(5, 5), new Coord2(-1, -1)};
        CoordM<Coord2> ss = new CoordM<>(sp_1);
        System.out.println("Object ss");
        showXY(ss);
        // showXYZ(ss);  // COMPILE ERROR: Coord2 is not a Coord3

        Coord4[] sp_2 = {new Coord4(1, 2, 3, 4), new Coord4(0, 0, 2, 6),
                new Coord4(2, 4, 2, 4)};
        CoordM<Coord4> cc = new CoordM<>(sp_2);
        System.out.println("\nObject cc");
        showXY(cc);
        showXYZ(cc);
        showAll(cc);
    }

    static class CoordM<T extends Coord2> {
        T[] space;

        CoordM(T[] array) {
            space = array;
        }
    }

    static class Coord2 {
        int x, y;

        Coord2(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Coord3 extends Coord2 {
        int z;

        Coord3(int x, int y, int z) {
            super(x, y);
            this.z = z;
        }
    }

    static class Coord4 extends Coord3 {
        int t;

        Coord4(int x, int y, int z, int t) {
            super(x, y, z);
            this.t = t;
        }
    }
}

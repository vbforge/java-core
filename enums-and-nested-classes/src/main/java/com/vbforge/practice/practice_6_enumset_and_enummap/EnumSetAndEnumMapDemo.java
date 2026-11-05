package com.vbforge.practice.practice_6_enumset_and_enummap;

import java.util.EnumMap;
import java.util.EnumSet;

/** EnumSet and EnumMap: compact, declaration-ordered collections for enum keys. */
public class EnumSetAndEnumMapDemo {

    enum Direction { NORTH, SOUTH, EAST, WEST }

    public static void main(String[] args) {
        EnumSet<Direction> horizontals = EnumSet.of(Direction.EAST, Direction.WEST);
        System.out.println("Horizontal directions: " + horizontals);

        EnumSet<Direction> all = EnumSet.allOf(Direction.class);
        EnumSet<Direction> verticals = EnumSet.complementOf(horizontals);
        System.out.println("All directions:        " + all);
        System.out.println("Vertical directions:   " + verticals);

        EnumMap<Direction, String> labels = new EnumMap<>(Direction.class);
        labels.put(Direction.NORTH, "↑");
        labels.put(Direction.SOUTH, "↓");
        labels.put(Direction.EAST,  "→");
        labels.put(Direction.WEST,  "←");
        System.out.println("EnumMap labels: " + labels);
    }
}

package com.vbforge.practice.practice_1_enum_basics;

import java.util.Arrays;

/**
 * Built-in Enum methods: name(), ordinal(), values(), valueOf(), compareTo(), equals, ==,
 * and a custom toString().
 */
public class EnumBuiltInMethodsDemo {

    public static void main(String[] args) {
        // name and ordinal:
        System.out.println(Season.WINTER.name());      // WINTER
        System.out.println(Season.WINTER.ordinal());   // 0

        // values, valueOf:
        Season[] seasons = Season.values();
        System.out.println(Arrays.toString(seasons));  // [Season: WINTER, Season: SPRING, ...] (custom toString)
        System.out.println(Season.valueOf("SPRING"));  // Season: SPRING

        // compareTo (difference of ordinals):
        System.out.println(Season.SPRING.compareTo(Season.WINTER));  // 1
        System.out.println(Season.SPRING.compareTo(Season.SPRING));  // 0
        System.out.println(Season.SPRING.compareTo(Season.SUMMER));  // -1
        System.out.println(Season.WINTER.compareTo(Season.SUMMER));  // -2

        // equals and ==:
        boolean isEqualToItself = Season.WINTER.equals(Season.WINTER);
        boolean isEqualToDifferentSeason = Season.WINTER.equals(Season.SUMMER);
        System.out.println(isEqualToItself);            // true
        System.out.println(isEqualToDifferentSeason);   // false
        Season season = Season.WINTER;
        System.out.println(season == Season.WINTER);    // true
        System.out.println(season == Season.SUMMER);    // false

        // toString:
        System.out.println(Season.SPRING);   // Season: SPRING
        System.out.println(Season.SUMMER);   // Season: SUMMER
        System.out.println(Season.WINTER);   // Season: WINTER
        System.out.println(Season.AUTUMN);   // Season: AUTUMN
    }
}

enum Season {
    WINTER, SPRING, SUMMER, AUTUMN;

    @Override
    public String toString() {
        return "Season: " + this.name();
    }
}

package com.vbforge.practice.practice_1_enum_basics;

/** Every enum is an Enum and an Object: Object --> Enum --> Season. */
public class EnumTypeHierarchyDemo {

    enum Season { SUMMER, AUTUMN, WINTER, SPRING }

    public static void main(String[] args) {
        Season season = Season.SUMMER;

        System.out.println(season instanceof Object);    // true
        System.out.println(season instanceof Enum<?>);   // true
        System.out.println(season instanceof Season);    // true
        System.out.println(season.getClass().getSimpleName());   // Season
    }
}

package com.vbforge.practice.practice_16_enum_map_usage;

import java.util.EnumMap;

public class EnumMapRemove {
    public static void main(String[] args) {
        EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
        schedule.put(Day.MONDAY, "Math");
        schedule.put(Day.TUESDAY, "Physics");

        schedule.remove(Day.TUESDAY);
        System.out.println("After removing TUESDAY: " + schedule);
    }

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    }

}

package com.vbforge.practice.practice_16_enum_map_usage;

import java.util.EnumMap;
import java.util.Map;

public class EnumMapIteration {
    public static void main(String[] args) {
        EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
        schedule.put(Day.MONDAY, "Math");
        schedule.put(Day.TUESDAY, "Physics");
        schedule.put(Day.WEDNESDAY, "Chemistry");

        for (Map.Entry<Day, String> entry : schedule.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    }

}

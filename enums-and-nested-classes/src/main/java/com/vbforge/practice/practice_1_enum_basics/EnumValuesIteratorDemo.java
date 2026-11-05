package com.vbforge.practice.practice_1_enum_basics;

import java.util.Iterator;

/** A hand-written Iterator (anonymous class) over an enum's values(); ordinal() + 1 gives the month number. */
public class EnumValuesIteratorDemo {

    enum Month {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST,
        SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }

    public static void main(String[] args) {
        Iterator<Month> iterator = new Iterator<>() {
            private final Month[] months = Month.values();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < months.length;
            }

            @Override
            public Month next() {
                return months[index++];
            }
        };

        System.out.println("All months:");
        while (iterator.hasNext()) {
            Month month = iterator.next();
            System.out.println(month.name() + " (Month Number: " + (month.ordinal() + 1) + ")");
        }
    }
}

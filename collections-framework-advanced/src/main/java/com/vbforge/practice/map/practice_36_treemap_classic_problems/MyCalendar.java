package com.vbforge.practice.map.practice_36_treemap_classic_problems;

import java.util.TreeMap;

/**
 * Problem 2: My Calendar I (Non-overlapping Intervals)
 * Goal: Design a class to book events that do not overlap. Each event is a pair [start, end).
 *
 * Solution using TreeMap:
 * We store existing bookings in a TreeMap<Integer, Integer>, where key is start time and value is end time.
 * Logic:
 * For each booking, check:
 * the floor entry (event that starts before or at the same time): does its end time overlap?
 * the ceiling entry (event that starts right after): does this start before the current event ends?
 * */
public class MyCalendar {

    private final TreeMap<Integer, Integer> calendar;

    public MyCalendar() {
        calendar = new TreeMap<>();
    }

    public boolean book(int start, int end) {
        // Find the previous and next event
        Integer prev = calendar.floorKey(start);
        Integer next = calendar.ceilingKey(start);

        // Check for overlap with previous event
        if (prev != null && calendar.get(prev) > start) {
            return false;
        }

        // Check for overlap with next event
        if (next != null && next < end) {
            return false;
        }

        // No overlap, book the event
        calendar.put(start, end);
        return true;
    }

    public static void main(String[] args) {

        MyCalendar cal = new MyCalendar();
        System.out.println(cal.book(10, 20)); // true
        System.out.println(cal.book(15, 25)); // false, overlaps with [10,20)
        System.out.println(cal.book(20, 30)); // true

    }


}

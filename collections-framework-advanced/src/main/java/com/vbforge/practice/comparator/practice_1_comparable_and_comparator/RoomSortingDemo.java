package com.vbforge.practice.comparator.practice_1_comparable_and_comparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Natural ordering ({@code Comparable}) vs an external {@code Comparator}
 * on the same list of rooms.
 */
public class RoomSortingDemo {

    public static void main(String[] args) {
        List<Room> rooms = new ArrayList<>(List.of(
                new Room("London", "Presentation hall", 200, 500.0),
                new Room("Manchester", "Living room", 5, 200.0),
                new Room("Chesterfield", "Living room", 7, 250.0),
                new Room("Oxford", "Dining room", 17, 350.0)));

        Collections.sort(rooms);                       // natural order: city, then type
        System.out.println("--- natural order (city, type) ---");
        rooms.forEach(System.out::println);

        rooms.sort(Room.PRICE_RATE);                   // comparator: price, city, type
        System.out.println("\n--- PRICE_RATE (price, city, type) ---");
        rooms.forEach(System.out::println);
    }
}

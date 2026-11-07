package com.vbforge.practice.comparator.practice_1_comparable_and_comparator;

import java.util.Comparator;

/**
 * Domain object for {@link RoomSortingDemo}.
 *
 * <ul>
 *   <li>natural order ({@link Comparable}): city, then type</li>
 *   <li>{@link #PRICE_RATE} comparator: price, then city, then type</li>
 * </ul>
 */
public class Room implements Comparable<Room> {

    public static final Comparator<Room> PRICE_RATE =
            Comparator.comparingDouble(Room::getPrice)
                    .thenComparing(Room::getCity)
                    .thenComparing(Room::getType);

    private final String city;
    private final String type;
    private final int capacity;
    private final double price;

    public Room(String city, String type, int capacity, double price) {
        this.city = city;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

    public String getCity()  { return city; }
    public String getType()  { return type; }
    public int getCapacity() { return capacity; }
    public double getPrice() { return price; }

    @Override
    public int compareTo(Room other) {
        int r = city.compareTo(other.city);
        return r != 0 ? r : type.compareTo(other.type);
    }

    @Override
    public String toString() {
        return String.format("Room[%s, %s, cap=%d, %.2f]", city, type, capacity, price);
    }
}

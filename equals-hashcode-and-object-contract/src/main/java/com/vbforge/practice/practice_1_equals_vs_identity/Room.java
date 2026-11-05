package com.vbforge.practice.practice_1_equals_vs_identity;

import java.util.Objects;

/**
 * Domain object for {@link EqualsVsIdentityDemo}: value-based {@code equals}
 * and a {@code hashCode} built from the same fields.
 */
public class Room {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return capacity == room.capacity
                && Double.compare(price, room.price) == 0
                && Objects.equals(city, room.city)
                && Objects.equals(type, room.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, type, capacity, price);
    }

    @Override
    public String toString() {
        return String.format("Room[%s, %s, cap=%d, %.2f]", city, type, capacity, price);
    }
}

package com.vbforge.practice.collection.practice_56_room_inventory_equals_design;

import java.util.Objects;

/*I’d use a normal Java class here because Room is a mutable domain entity.
Records are immutable and better suited for DTOs or value objects.
Since price changes over time and the object is stored in a Set, I need controlled mutability and a carefully designed equals/hashCode.*/

public class Room {

    private final String city;
    private final String type;
    private final int capacity;
    private double price;

    public Room(String city, String type, int capacity, double price) {
        this.city = city;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

    // getters
    public String getCity() {
        return city;
    }

    public String getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPrice() {
        return price;
    }

    // setter ONLY for mutable field
    public void setPrice(double price) {
        this.price = price;
    }

    // equality based on identity, not price
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return capacity == room.capacity &&
                Objects.equals(city, room.city) &&
                Objects.equals(type, room.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, type, capacity);
    }
}

package com.vbforge.practice.practice_7_why_collections;

/**
 * Small immutable domain object used by {@link WhyCollectionsDemo}.
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

    public String getCity()   { return city; }
    public String getType()   { return type; }
    public int getCapacity()  { return capacity; }
    public double getPrice()  { return price; }

    @Override
    public String toString() {
        return String.format("Room[%s, %s, cap=%d, %.2f]", city, type, capacity, price);
    }
}

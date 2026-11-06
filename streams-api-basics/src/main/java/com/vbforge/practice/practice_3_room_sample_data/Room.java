package com.vbforge.practice.practice_3_room_sample_data;

/** Small domain class used by the Room based stream practice (city, type, capacity, price, breakfast flag). */
public class Room {

    private final String city;
    private final String type;
    private final int capacity;
    private final double price;
    private boolean breakfast;

    public Room(String city, String type, int capacity, double price) {
        this.city = city;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

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

    public boolean isBreakfast() {
        return breakfast;
    }

    public void setBreakfast(boolean breakfast) {
        this.breakfast = breakfast;
    }

    @Override
    public String toString() {
        return String.format("Room: %s %s %s %s", city, type, capacity, price);
    }
}

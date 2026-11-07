package com.vbforge.practice.common.practice_2_hotel_sample_data;

import java.util.Comparator;
import java.util.Objects;

public class Room implements Comparable<Room>{

    public static Comparator<Room> PRICE_RATE = Comparator.comparing(Room::getPrice)     //by price first
                                                          .thenComparing(Room::getCity)  //by city name second parameter
                                                          .thenComparing(Room::getType); //by type third parameter
    private String city;
    private String type;
    private int capacity;
    private double price;
    public boolean breakfast;

    public Room(String city, String type, int capacity, double price) {
        this.city = city;
        this.type = type;
        this.capacity = capacity;
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isBreakfast() {
        return breakfast;
    }

    public void setBreakfast(boolean breakfast) {
        this.breakfast = breakfast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return capacity == room.capacity && Double.compare(price, room.price) == 0 && Objects.equals(city, room.city) && Objects.equals(type, room.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, type, capacity, price);
    }

    @Override
    public String toString() {
        return String.format("Room: %s %s %s %s", this.city, this.type, this.capacity, this.price);
    }

    @Override
    public int compareTo(Room o) {
        int result = this.getCity().compareTo(o.getCity());
        return result != 0 ? result : this.getType().compareTo(o.getType());
    }
}

package com.vbforge.practice.collection.practice_56_room_inventory_equals_design;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class RoomService {

    //1) declare a Collection to store Room Inventory
    private Collection<Room> inventory;

    public RoomService() {
        //2) initialize Collection and assign it the Room Inventory
        this.inventory = new LinkedHashSet<>(); //LinkedHashSet - we achieve the order for elements were put
    }

    public Collection<Room> getInventory() {
        //3)return the Room Inventory

        return new HashSet<>(this.inventory);
    }

    public void createRoom(String city, String type, int capacity, double price) {
        //4) add a new Room to the Room Inventory using the provided parameters
        this.inventory.add(new Room(city, type, capacity, price));

    }

    public void createRooms(Room[] rooms) {
        //5) add the rooms provided in the array into Room Inventory
        //example of bulk operation to add many objects at one time
        this.inventory.addAll(Arrays.asList(rooms));
    }

    public void removeRoom(Room room) {
        //6) remove the provided Room from the Room Inventory
        this.inventory.remove(room);
    }

    public boolean hasRoom(Room room) {
        //7) return the boolean that indicates if inventory contains the room
        return this.inventory.contains(room);
    }

    public Room[] asArray() {
        //8)return all rooms as an array of rooms in **order** they were added
        return this.inventory.toArray(new Room[0]);
    }

    public Collection<Room> getByType(String type) {
        //9) return nwe collection of rooms where room type matches the provided String;
        //the original collection must not be modified;
        Collection<Room> copy = new HashSet<>(this.inventory);
        copy.removeIf(room -> !room.getType().equals(type));
        return copy;
    }

    public void applyDiscount(final double discount) {
        //10)Reduces the rate of each room by the provided discount
        this.inventory.forEach(room -> room.setPrice(room.getPrice() * (1 - discount)));
    }

    public Collection<Room> getRoomsByCapacity(final int requiredCapacity) {
        //11)Returns a new collection of rooms that meet or exceed the provided capacity
        Collection<Room> matchers = new HashSet<>();
        for (Room room : inventory) {
            if (room.getCapacity() >= requiredCapacity) {
                matchers.add(room);
            }
        }
        return matchers;

    }

    public Collection<Room> getRoomByRateAndType(final double price, final String type) {
        //12)Returns a new collection of rooms with a rate below the provided rate and that match the provided type
        return this.inventory.stream()
                .filter(r->r.getPrice() < price)
                .filter(r ->r.getType().equals(type))
                .collect(Collectors.toList());
    }

}

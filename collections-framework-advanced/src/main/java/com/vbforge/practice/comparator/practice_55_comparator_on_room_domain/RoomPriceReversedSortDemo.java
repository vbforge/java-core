package com.vbforge.practice.comparator.practice_55_comparator_on_room_domain;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomPriceReversedSortDemo {
    public static void main(String[] args) {

        Room piccadilly = new Room("Piccadilly", "Guest Room", 3, 175.00);
        Room piccadilly2 = new Room("Piccadilly", "Guest Room", 3, 175.00);
        Room oxford = new Room("Oxford", "Suite", 5, 725.0);
        Room oxfordGuest = new Room("Oxford", "Guest Room", 5, 225.0);
        Room oxfordGuest2 = new Room("Oxford", "Guest Room", 5, 225.0);
        Room manchester = new Room("Manchester", "Premiere", 7, 1125.0);
        Room liverpool = new Room("Liverpool", "Suite", 9, 525.0);

        List<Room> rooms = new ArrayList<>(List.of(piccadilly, piccadilly2, oxford, oxfordGuest, oxfordGuest2, manchester, liverpool));

        //sorting by prices, city, type
//        rooms.sort(Room.PRICE_RATE);

        //by the way we can sort collection in reversed order
        rooms.sort(Room.PRICE_RATE.reversed());

        rooms.forEach(r -> System.out.format("%-15s %-15s %-10f %n", r.getCity(), r.getType(), r.getPrice()));
    }
}

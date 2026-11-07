package com.vbforge.practice.map.practice_30_hashmap_with_room_keys;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Guest;
import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;

import java.util.HashMap;
import java.util.Map;

public class BookingService {

    private Map<Room, Guest> bookings = new HashMap<>();

    public boolean book(Room room, Guest guest) {

        /*
         * 1. The provided Guest is placed in the bookings Map and
         * associated with the provided room, only if no other guest
         * is associated with the room.
         *
         * Returns a boolean that indicates if the Guest was
         * successfully placed in the room.
         */

        return this.bookings.putIfAbsent(room, guest) == null;
    }

    public double totalRevenue() {

        /*
         * 2. Returns a double that totals the price of each Room booked
         * in the bookings Map.
         */
        return this.bookings.keySet().stream()
                .mapToDouble(Room::getPrice)
                .sum();
    }

    public Map<Room, Guest> getBookings() {
        return this.bookings;
    }

}

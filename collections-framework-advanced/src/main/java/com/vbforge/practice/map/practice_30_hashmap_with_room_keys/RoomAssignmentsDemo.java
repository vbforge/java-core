package com.vbforge.practice.map.practice_30_hashmap_with_room_keys;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Guest;
import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;

import java.util.HashMap;
import java.util.Map;

public class RoomAssignmentsDemo {
    public static void main(String[] args) {
        Room piccadilly = new Room("Piccadilly", "Guest Room", 3, 125.00);
        Room oxford = new Room("Oxford", "Suite", 5, 225.0);

        Guest john = new Guest("John", "Doe", false);
        Guest maria = new Guest("Maria", "Doe", true);

        Map<Room, Guest> assignments = new HashMap<>();
        assignments.put(oxford, maria);
        assignments.put(piccadilly, john);

        System.out.println("Oxford: " + assignments.get(new Room("Oxford", "Suite", 5, 225.0)));
        System.out.println("Piccadilly: " + assignments.get(piccadilly));

        /*  equals() and hashCode() is overridden
        Oxford: Maria Doe
        Piccadilly: John Doe*/

        //in case not override, we got (Oxford: null)
    }
}

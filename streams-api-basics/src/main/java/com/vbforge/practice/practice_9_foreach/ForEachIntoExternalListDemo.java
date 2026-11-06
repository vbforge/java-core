package com.vbforge.practice.practice_9_foreach;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.System.out;

/**
 * forEach adds matching rooms to an external collection.
 * The lambda may mutate the collection (the reference is effectively final), but it is a side effect:
 * it works for a sequential stream and is unsafe for a parallel one. Prefer collect (see practice_10).
 */
public class ForEachIntoExternalListDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        Collection<Room> breakfastRooms = new ArrayList<>();
        rooms.stream()
                .filter(Room::isBreakfast)
                .forEach(breakfastRooms::add); // method reference (same as room -> breakfastRooms.add(room))

        breakfastRooms.forEach(room -> out.println(room.getCity()));
        // Manchester
        // London
    }
}

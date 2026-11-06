package com.vbforge.practice.practice_10_collect_and_to_array;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;
import java.util.stream.Collectors;

/** The result of a pipeline goes into a new collection with collect(Collectors.toList()) - the Java 8 way. */
public class CollectToListDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        Collection<Room> breakfastRooms = rooms.stream()
                .filter(Room::isBreakfast)
                .collect(Collectors.toList());

        breakfastRooms.forEach(room -> System.out.println(room.getCity()));
        // Manchester
        // London
    }
}

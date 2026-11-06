package com.vbforge.practice.practice_5_filter_and_map;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.System.out;

/** filter, collect to a new collection, then map each Room to the value that should be printed (its city). */
public class MapRoomToCityDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        Collection<Room> breakfastRooms = rooms.stream()
                .filter(Room::isBreakfast)
                .collect(Collectors.toList());

        breakfastRooms.stream()
                .map(Room::getCity)
                .forEach(out::println);
        // Manchester
        // London
    }
}

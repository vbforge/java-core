package com.vbforge.practice.practice_9_foreach;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.System.out;

/** stream().forEach(...) and Collection.forEach(...) do the same here; the shorter form skips the stream. */
public class StreamForEachVsCollectionForEachDemo {

    public static void main(String[] args) {
        Collection<Room> breakfastRooms = RoomSampleData.rooms().stream()
                .filter(Room::isBreakfast)
                .collect(Collectors.toList());

        breakfastRooms.stream()
                .forEach(room -> out.println(room.getCity()));

        breakfastRooms.forEach(room -> out.println(room.getCity()));
        // Manchester, London, Manchester, London
    }
}

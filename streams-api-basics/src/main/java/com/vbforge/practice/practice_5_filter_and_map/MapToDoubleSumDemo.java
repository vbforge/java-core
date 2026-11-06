package com.vbforge.practice.practice_5_filter_and_map;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.System.out;

/** mapToDouble switches to a DoubleStream, which has sum(): breakfast rooms cost 500.0 + 200.0. */
public class MapToDoubleSumDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        Collection<Room> breakfastRooms = rooms.stream()
                .filter(Room::isBreakfast)
                .collect(Collectors.toList());

        double sum = breakfastRooms.stream()
                .mapToDouble(Room::getPrice)
                .sum();
        out.println("sum = " + sum);
        // sum = 700.0
    }
}

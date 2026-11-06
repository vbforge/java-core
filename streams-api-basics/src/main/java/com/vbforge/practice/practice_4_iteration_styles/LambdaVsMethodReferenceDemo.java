package com.vbforge.practice.practice_4_iteration_styles;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;

/** The same stream pipeline written with a lambda and with a method reference. */
public class LambdaVsMethodReferenceDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        System.out.println("\tlambda:");
        rooms.stream()
                .filter(room -> room.isBreakfast())
                .forEach(room -> System.out.println(room.getCity()));

        System.out.println("\tmethod reference:");
        rooms.stream()
                .filter(Room::isBreakfast)
                .forEach(room -> System.out.println(room.getCity()));
        // both print: Manchester, London
    }
}

package com.vbforge.practice.practice_8_laziness;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;

/**
 * Elements flow through the pipeline one at a time: the filter's "Testing ..." line and
 * the forEach line for the same room appear together, not all filter lines first.
 */
public class ElementByElementFlowDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        rooms.stream()
                .filter(room -> {
                    System.out.format("Testing %s with result %b%n", room.getCity(), room.isBreakfast());
                    return room.isBreakfast();
                })
                .forEach(room -> System.out.println(room.getCity()));

        /*Testing Manchester with result true
        Manchester
        Testing London with result true
        London
        Testing Oxford with result false
        Testing Liverpool with result false
        Testing Newcastle with result false*/
    }
}

package com.vbforge.practice.iterator.practice_14_iterable_and_iterator_basics;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ForEachOverCollectionDemo {
    public static void main(String[] args) {

        Room room1 = new Room("Manchester", "Presentation", 200, 500.0);
        Room room2 = new Room("London", "Hotel", 5, 200.0);
        Room room3 = new Room("Oxford", "Motel", 3, 100.0);
        Room room4 = new Room("Liverpool", "Hotel", 7, 300.0);
        Room room5 = new Room("Newcastle", "Motel", 5, 400.0);

        Collection<Room> rooms = new ArrayList<>(Arrays.asList(room1, room2, room3, room4, room5));

        for (Room room : rooms) {
            System.out.println(room.getCity());
        }

    }
}

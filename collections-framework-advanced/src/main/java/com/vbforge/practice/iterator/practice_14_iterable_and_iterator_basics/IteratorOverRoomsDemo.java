package com.vbforge.practice.iterator.practice_14_iterable_and_iterator_basics;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class IteratorOverRoomsDemo {
    public static void main(String[] args) {

        Room room1 = new Room("Manchester", "Presentation", 200, 500.0);
        Room room2 = new Room("London", "Hotel", 5, 200.0);
        Room room3 = new Room("Oxford", "Motel", 3, 100.0);

        Collection<Room> rooms = new ArrayList<>(Arrays.asList(room1, room2, room3));

        //iterate through one instance of iterator:
        Iterator<Room> iterator = rooms.iterator();
        System.out.println("\niterator:");
        System.out.println(iterator.next());
        System.out.println(iterator.next());
        System.out.println(iterator.next());

        //iterating trough separate instance of iterator:
        //tracks it`s own process through collection:
        Iterator<Room> iterator2 = rooms.iterator();
        System.out.println("\niterator2:");
        System.out.println(iterator2.next());

        //iterating by while loop:
        //need to create a new instance of iterator:
        Iterator<Room> iterator3 = rooms.iterator();
        System.out.println("\niterator3(while loop):");
        while (iterator3.hasNext()){
            Room room = iterator3.next();
            System.out.println(room.getType() + ", " + room.getCapacity());
        }

    }
}

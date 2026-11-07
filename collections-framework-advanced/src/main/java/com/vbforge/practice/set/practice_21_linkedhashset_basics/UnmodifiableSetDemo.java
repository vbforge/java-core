package com.vbforge.practice.set.practice_21_linkedhashset_basics;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;
import com.vbforge.practice.common.practice_2_hotel_sample_data.Guest;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Immutable Set factories — {@code Set.of(...)} and {@code Set.copyOf(...)}.
 *
 *   Set.of(e1, e2, ...)  — truly immutable, fixed content; no nulls.
 *   Set.copyOf(collection) — unmodifiable snapshot; iteration order NOT guaranteed
 *                            (even if the source was a LinkedHashSet).
 */
public class UnmodifiableSetDemo {
    public static void main(String[] args) {

        Room manchester = new Room("Manchester", "Presentation", 100, 850.0);
        Room london = new Room("London", "Guest", 10, 250.0);
        Room londonDuplicate = new Room("London", "Guest", 10, 250.0);
        Room oxford = new Room("Oxford", "Community", 5, 250.0);
        Room liverpool = new Room("Liverpool", "Community", 5, 250.0); //new element created

        Set<Room> roomSet = new LinkedHashSet<>();
        roomSet.add(manchester);
        roomSet.add(london);
        roomSet.add(londonDuplicate);
        roomSet.add(oxford);
        roomSet.add(liverpool); //added new element

        /** Set.of(...) - set is not modified (not allowed to add elements):*/
        Set<Room> rooms = Set.of(manchester, london);
//        rooms.add(oxford);  //UnsupportedOperationException

        Set<Room> moreRooms = Set.copyOf(roomSet); //set is backed of all elements from source collection
        //but in that case (after copy and streaming) **order** does not support

        moreRooms.stream()
                .map(Room::getCity)
                .forEach(System.out::println);
        /* before adding liverpool
        Manchester
        Oxford
        London*/

        /* after adding liverpool
        Manchester
        Oxford
        London
        Liverpool*/
    }
}

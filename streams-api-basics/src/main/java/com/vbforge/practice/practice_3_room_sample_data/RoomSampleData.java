package com.vbforge.practice.practice_3_room_sample_data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Five rooms; Manchester and London include breakfast. One place for the setup the Room demos share. */
public final class RoomSampleData {

    private RoomSampleData() {
    }

    public static List<Room> rooms() {
        Room manchester = new Room("Manchester", "Presentation", 200, 500.0);
        Room london = new Room("London", "Hotel", 5, 200.0);
        Room oxford = new Room("Oxford", "Motel", 3, 100.0);
        Room liverpool = new Room("Liverpool", "Hotel", 7, 300.0);
        Room newcastle = new Room("Newcastle", "Motel", 5, 400.0);

        manchester.setBreakfast(true);
        london.setBreakfast(true);

        return new ArrayList<>(Arrays.asList(manchester, london, oxford, liverpool, newcastle));
    }
}

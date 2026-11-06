package com.vbforge.practice.practice_4_iteration_styles;

import com.vbforge.practice.practice_3_room_sample_data.Room;
import com.vbforge.practice.practice_3_room_sample_data.RoomSampleData;

import java.util.Collection;

/** Imperative style: a for-each loop with an if. The baseline the stream versions are compared with. */
public class ImperativeLoopDemo {

    public static void main(String[] args) {
        Collection<Room> rooms = RoomSampleData.rooms();

        for (Room room : rooms) {
            if (room.isBreakfast()) {
                System.out.println(room.getCity());
            }
        }
        // Manchester
        // London
    }
}

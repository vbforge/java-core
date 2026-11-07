package com.vbforge.practice.list.practice_10_guest_service_with_list;

import java.util.ArrayList;
import com.vbforge.practice.common.practice_2_hotel_sample_data.Guest;
import java.util.List;

public class RoomGuestDemo {

    public static void main(String[] args) {

        Guest john = new Guest("John", "Doe", false);
        Guest maria = new Guest("Maria", "Doe", false);
        Guest sonia = new Guest("Sonia", "Doe", true);
        Guest siri = new Guest("Siri", "Doe", true);

        List<Guest> checkinList = new ArrayList<>(100);
        checkinList.add(john);
        checkinList.add(maria);

        print(checkinList);

        checkinList.add(0, sonia);

        print(checkinList);

        //set status for maria for loyalty to true
        checkinList.get(2).setLoyaltyProgramMember(true);

        //more members going to add to the collection
        checkinList.addAll(1, List.of(maria, siri));
        print(checkinList);

        checkinList.remove(checkinList.size() - 1);
        print(checkinList);

        System.out.println(checkinList.indexOf(john)); //3


    }

    public static void print(List<Guest> list) {

        System.out.format("%n--List Contents--%n");

        for (int x = 0; x < list.size(); x++) {
            Guest guest = list.get(x);
            System.out.format("%x: %s %n", x, guest.toString());
        }

    }

}

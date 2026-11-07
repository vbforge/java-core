package com.vbforge.practice.practice_4_members_inspection_and_public_access;

import java.lang.reflect.Field;

/** Reading and writing a PUBLIC field: no setAccessible needed. getInt/setInt avoid boxing. */
public class PublicFieldAccessDemo {

    public static void main(String[] args) throws Exception {
        Player player = Player.class.getDeclaredConstructor().newInstance();

        Field numberField = Player.class.getDeclaredField("number");
        numberField.set(player, 10);
        int currentValue = numberField.getInt(player);
        System.out.println("currentValue: " + currentValue);
    }
}

package com.vbforge.practice.practice_4_members_inspection_and_public_access;

/** Target with one private and one public field and a no-arg method. */
public class Player {
    private String name;
    public int number;

    public void doAction() {
        System.out.println(name + "; " + number);
    }
}

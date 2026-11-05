package com.vbforge.practice.practice_4_enum_implements_interface;

/** An enum can implement an interface (but never extend a class); each constant inherits the behavior. */
public class EnumImplementsInterfaceDemo {

    public static void main(String[] args) {
        Weekday x = Weekday.TUESDAY;
        x.play();
    }
}

interface Playable {
    void play();
}

enum Weekday implements Playable {

    MONDAY, TUESDAY, WEDNESDAY;

    @Override
    public void play() {
        System.out.println("play");
    }
}

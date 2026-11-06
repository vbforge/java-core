package com.vbforge.practice.practice_6_exhaustive_switch;

import com.vbforge.practice.practice_6_exhaustive_switch.Animal.Bird;
import com.vbforge.practice.practice_6_exhaustive_switch.Animal.Cat;
import com.vbforge.practice.practice_6_exhaustive_switch.Animal.Dog;

/**
 * No default over a sealed type: the compiler proves every subtype is handled.
 * Try it: delete the Bird case below and the file no longer compiles - on purpose.
 * The same happens when a new subtype is added to Animal's permits list.
 */
public class ExhaustiveSwitchNoDefaultDemo {

    static String sound(Animal a) {
        return switch (a) {
            case Dog d  -> "woof";
            case Cat c  -> "meow";
            case Bird b -> "tweet";
        };
    }

    public static void main(String[] args) {
        System.out.println(sound(new Dog()));
        System.out.println(sound(new Cat()));
        System.out.println(sound(new Bird()));
    }
}

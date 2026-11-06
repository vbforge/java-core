package com.vbforge.practice.practice_6_exhaustive_switch;

import com.vbforge.practice.practice_6_exhaustive_switch.Animal.Bird;
import com.vbforge.practice.practice_6_exhaustive_switch.Animal.Cat;
import com.vbforge.practice.practice_6_exhaustive_switch.Animal.Dog;

/**
 * A "defensive" default hides the error when a subtype is added.
 * Imagine Bird was added to Animal later: soundWithDefault still compiles and Bird silently
 * takes the default branch, while soundExhaustive would have failed the build until fixed.
 */
public class DefensiveDefaultTrapDemo {

    static String soundWithDefault(Animal a) {
        return switch (a) {
            case Dog d -> "woof";
            case Cat c -> "meow";
            default    -> "???";          // Bird lands here without any warning
        };
    }

    static String soundExhaustive(Animal a) {
        return switch (a) {
            case Dog d  -> "woof";
            case Cat c  -> "meow";
            case Bird b -> "tweet";
        };
    }

    public static void main(String[] args) {
        Animal bird = new Bird();
        System.out.println("with default: " + soundWithDefault(bird));
        System.out.println("exhaustive:   " + soundExhaustive(bird));
    }
}

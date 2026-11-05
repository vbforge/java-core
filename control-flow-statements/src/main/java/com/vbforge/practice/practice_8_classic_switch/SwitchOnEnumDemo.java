package com.vbforge.practice.practice_8_classic_switch;

/**
 * Classic switch on an enum: case labels are the bare constant names (TOMATO, not Vegetable.TOMATO).
 *
 * All constants are listed, so the trailing return is unreachable for a non-null argument;
 * it only matters if a new constant is added later. A null argument throws NullPointerException.
 */
public class SwitchOnEnumDemo {

    enum Vegetable { TOMATO, POTATO, CUCUMBER }

    public static void main(String[] args) {
        System.out.println("potato -> " + vegetableKind(Vegetable.POTATO));
        System.out.println("tomato -> " + vegetableKind(Vegetable.TOMATO));
    }

    static String vegetableKind(Vegetable v) {
        switch (v) {
            case TOMATO:
            case POTATO:
            case CUCUMBER:
                return "vegetable";
        }
        return "unknown";
    }
}

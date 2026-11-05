package com.vbforge.practice.practice_1_enum_basics;

/** The pre-enum alternative: plain int constants carry no type, so any int is accepted where a season is expected. */
public class IntConstantsVsEnumDemo {

    public static void main(String[] args) {
        System.out.println("Old style: " + SeasonConstants.WINTER + ", " + SeasonConstants.SPRING);
    }
}

final class SeasonConstants {
    public static final int WINTER = 0;
    public static final int SPRING = 1;
    public static final int SUMMER = 2;
    public static final int AUTUMN = 3;
}

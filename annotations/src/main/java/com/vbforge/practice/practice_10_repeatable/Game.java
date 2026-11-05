package com.vbforge.practice.practice_10_repeatable;

import java.lang.annotation.Repeatable;

@Repeatable(Games.class)
public @interface Game {
    String name() default "free practice you like";
    String day();
}

package com.vbforge.practice.practice_6_exhaustive_switch;

public sealed interface Animal permits Animal.Dog, Animal.Cat, Animal.Bird {
    record Dog() implements Animal {}
    record Cat() implements Animal {}
    record Bird() implements Animal {}
}

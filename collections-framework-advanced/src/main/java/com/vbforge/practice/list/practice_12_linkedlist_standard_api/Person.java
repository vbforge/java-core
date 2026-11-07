package com.vbforge.practice.list.practice_12_linkedlist_standard_api;

public record Person(String name, int age) {

    @Override
    public String toString() {
        return String.format("(%s: %d)", name, age);
    }
}

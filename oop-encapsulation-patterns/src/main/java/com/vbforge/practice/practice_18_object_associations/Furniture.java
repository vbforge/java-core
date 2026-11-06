package com.vbforge.practice.practice_18_object_associations;

public class Furniture {

    public String name;
    public int cost;

    public Furniture(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Furniture{name='" + name + "', cost=" + cost + "}";
    }
}

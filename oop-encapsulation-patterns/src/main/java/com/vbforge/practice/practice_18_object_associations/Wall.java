package com.vbforge.practice.practice_18_object_associations;

public class Wall {

    public String material;
    public int height;

    public Wall(String material, int height) {
        this.material = material;
        this.height = height;
    }

    @Override
    public String toString() {
        return "Wall{material='" + material + "', height=" + height + "}";
    }
}

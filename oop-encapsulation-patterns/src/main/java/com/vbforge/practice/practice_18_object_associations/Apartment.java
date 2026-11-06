package com.vbforge.practice.practice_18_object_associations;

import com.vbforge.practice.practice_18_object_associations.Furniture;

import java.util.Arrays;

/**
 * Two kinds of "has-a":
 *   - Wall is created INSIDE the constructor: composition, the apartment owns it
 *   - Furniture is passed in from OUTSIDE: aggregation, it exists independently
 */
public class Apartment {

    public String street;
    public int apartmentNo;
    private Wall walls;
    private Furniture[] furniture;

    public Apartment(String street, int apartmentNo, String material, int high, Furniture[] furniture) {
        this.street = street;
        this.apartmentNo = apartmentNo;
        this.walls = new Wall(material, high);
        this.furniture = furniture;
    }

    @Override
    public String toString() {
        return "The address is '" + street + " St' " + apartmentNo
                + ", walls " + walls
                + ", furniture=" + Arrays.toString(furniture);
    }

    public int getTotalFurnitureCost() {
        int totalAmount = 0;
        for (Furniture unit : furniture) {
            totalAmount += unit.cost;
        }
        return totalAmount;
    }
}

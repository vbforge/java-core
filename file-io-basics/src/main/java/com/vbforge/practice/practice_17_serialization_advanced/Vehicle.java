package com.vbforge.practice.practice_17_serialization_advanced;

import java.io.Serial;
import java.io.Serializable;

public class Vehicle implements Serializable {

    @Serial
    private static final long serialVersionUID = 4184663242797836018L;
    private int id;
    private String brand;
    private double price;

    public Vehicle(int id, String brand, double price) {
        this.id = id;
        this.brand = brand;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                '}';
    }
}
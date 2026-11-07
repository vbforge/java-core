package com.vbforge.practice.collection.practice_56_room_inventory_equals_design;

import java.util.Objects;

public class Car {

    private String make;
    private String model;
    private int mileage;

    public Car(String make, String model, int mileage) {
        this.make = make;
        this.model = model;
        this.mileage = mileage;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    @Override
    public boolean equals(Object o) {

        Car c = (Car) o;
        System.out.format("Comparing %s %s %s with %s %s %s %n",
                this.make, this.model, this.mileage, c.make, c.model, c.mileage);

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return mileage == car.mileage && Objects.equals(make, car.make) && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(make, model, mileage);
    }

    @Override
    public String toString() {
        return String.format("Car: %s, %s, %s", this.make, this.model, this.mileage);
    }
}

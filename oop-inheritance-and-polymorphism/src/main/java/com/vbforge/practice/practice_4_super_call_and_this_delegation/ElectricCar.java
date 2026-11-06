package com.vbforge.practice.practice_4_super_call_and_this_delegation;

public class ElectricCar extends Vehicle {
    private final String model;
    private final double batteryKwh;

    public ElectricCar(String brand, String model, int year, double batteryKwh) {
        super(brand, year);           // must be first
        this.model = model;
        this.batteryKwh = batteryKwh;
    }

    public ElectricCar(String brand, String model, int year) {
        this(brand, model, year, 75.0);   // constructor delegation via this(...)
    }

    @Override
    public String describe() {
        return super.describe() + " | Model: " + model + " | Battery: " + batteryKwh + " kWh";
    }

    public double getBatteryKwh() { return batteryKwh; }
    public String getModel()      { return model; }
}

package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** Reports a fixed temperature in degrees Celsius. */
public class TemperatureSensor extends AbstractSensor {

    private final double celsius;

    public TemperatureSensor(String id, double celsius) {
        super(id);
        this.celsius = celsius;
    }

    @Override
    public double read() {
        return celsius;
    }

    @Override
    protected String unit() {
        return " C";
    }
}

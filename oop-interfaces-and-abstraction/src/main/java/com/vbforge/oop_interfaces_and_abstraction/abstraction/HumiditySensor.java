package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** Reports a fixed relative humidity in percent. */
public class HumiditySensor extends AbstractSensor {

    private final double percent;

    public HumiditySensor(String id, double percent) {
        super(id);
        this.percent = percent;
    }

    @Override
    public double read() {
        return percent;
    }

    @Override
    protected String unit() {
        return " %";
    }
}

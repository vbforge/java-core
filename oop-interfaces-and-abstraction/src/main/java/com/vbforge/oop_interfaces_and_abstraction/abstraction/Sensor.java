package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/** The contract clients depend on. */
public interface Sensor {

    String id();

    double read();

    String describe();
}

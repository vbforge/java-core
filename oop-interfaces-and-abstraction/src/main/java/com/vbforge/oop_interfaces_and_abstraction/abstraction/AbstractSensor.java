package com.vbforge.oop_interfaces_and_abstraction.abstraction;

/**
 * A SKELETAL implementation: an abstract class that implements the interface partially.
 * It supplies the shared state ({@code id}) and shared behaviour ({@code describe}) and leaves
 * {@code read()} — and the new {@code unit()} step — to subclasses.
 *
 * <p>The JDK uses the same pairing: {@code List} + {@code AbstractList}, {@code Map} + {@code AbstractMap}.
 */
public abstract class AbstractSensor implements Sensor {

    private final String id;

    protected AbstractSensor(String id) {
        this.id = id;
    }

    @Override
    public final String id() {
        return id;
    }

    @Override
    public String describe() {
        return id + " = " + read() + unit();
    }

    /** Unit suffix appended to the reading, for example {@code " C"}. */
    protected abstract String unit();
}

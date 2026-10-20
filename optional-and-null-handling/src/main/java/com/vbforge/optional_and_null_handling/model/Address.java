package com.vbforge.optional_and_null_handling.model;

import java.util.Optional;

/**
 * Tiny immutable model used by the chaining demo and tasks.
 *
 * <p>It deliberately mixes the two API styles you meet in real code:
 * <ul>
 *   <li>{@link #getCity()} — <b>legacy style</b>: returns {@code null} when unknown</li>
 *   <li>{@link #getZip()}  — <b>modern style</b>: absence is visible in the return type</li>
 * </ul>
 */
public final class Address {

    private final String city;   // may be null
    private final String zip;    // may be null

    public Address(String city, String zip) {
        this.city = city;
        this.zip = zip;
    }

    /** Legacy style — returns {@code null} when the city is unknown. */
    public String getCity() {
        return city;
    }

    /** Modern style — empty when the zip code is unknown. */
    public Optional<String> getZip() {
        return Optional.ofNullable(zip);
    }
}

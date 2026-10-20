package com.vbforge.optional_and_null_handling.model;

import java.util.Optional;

/**
 * Tiny immutable model used by the chaining demo and tasks.
 * A user always has a name, but may have no {@link Address}.
 */
public final class User {

    private final String name;
    private final Address address;   // may be null

    public User(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    /** Modern style — empty when the user has no address on file. */
    public Optional<Address> getAddress() {
        return Optional.ofNullable(address);
    }
}

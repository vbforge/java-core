package com.vbforge.oop_interfaces_and_abstraction.basics;

/**
 * A "major.minor" version number that knows how to order itself by implementing
 * {@link Comparable} — the JDK's contract for "natural ordering".
 *
 * <p>{@code equals}/{@code hashCode} are written to stay CONSISTENT with {@code compareTo}.
 */
public final class Version implements Comparable<Version> {

    private final int major;
    private final int minor;

    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    @Override
    public int compareTo(Version other) {
        int byMajor = Integer.compare(major, other.major);
        return byMajor != 0 ? byMajor : Integer.compare(minor, other.minor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }
        Version other = (Version) o;
        return major == other.major && minor == other.minor;
    }

    @Override
    public int hashCode() {
        return 31 * major + minor;
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }
}

package com.vbforge.practice.practice_9_equals_hashcode_contract;

import java.util.HashSet;
import java.util.Objects;

/**
 * hashCode() without equals(): the hashes match, but equals is still identity, so a HashSet keeps
 * both objects. Not a contract violation (unequal objects may share a hash), but almost never what
 * was intended for a value class.
 */
public class HashCodeWithoutEqualsDemo {

    static class HashCodeOnlyEmployee {
        private final String id;
        HashCodeOnlyEmployee(String id) { this.id = id; }

        @Override public int hashCode() { return Objects.hash(id); }
        // equals NOT overridden -> identity equality
        @Override public String toString() { return "HashCodeOnlyEmployee(" + id + ")"; }
    }

    public static void main(String[] args) {
        HashCodeOnlyEmployee h1 = new HashCodeOnlyEmployee("H001");
        HashCodeOnlyEmployee h2 = new HashCodeOnlyEmployee("H001");
        System.out.println("h1.equals(h2):  " + h1.equals(h2) + "  <- identity equality");
        System.out.println("h1.hashCode == h2.hashCode: " + (h1.hashCode() == h2.hashCode()));
        HashSet<HashCodeOnlyEmployee> set = new HashSet<>();
        set.add(h1);
        set.add(h2);
        System.out.println("set size: " + set.size()
                + "  <- 2: same bucket, but equals (identity) keeps them apart; 1 would need an equals override");
    }
}

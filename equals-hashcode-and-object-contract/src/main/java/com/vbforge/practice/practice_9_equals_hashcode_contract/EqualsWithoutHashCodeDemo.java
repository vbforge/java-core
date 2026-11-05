package com.vbforge.practice.practice_9_equals_hashcode_contract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * equals() without hashCode() silently breaks HashSet / HashMap: two "equal" objects have different
 * identity hashes, land in different buckets and are never compared. Identity hash values vary
 * between runs, so only the comparison results are printed.
 */
public class EqualsWithoutHashCodeDemo {

    // --- Correct: equals and hashCode use the SAME field ---
    static class GoodEmployee {
        private final String id;
        GoodEmployee(String id) { this.id = id; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof GoodEmployee e)) return false;
            return Objects.equals(id, e.id);
        }

        @Override
        public int hashCode() { return Objects.hash(id); }
    }

    // --- Broken: equals without hashCode ---
    static class BrokenEmployee {
        private final String id;
        BrokenEmployee(String id) { this.id = id; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BrokenEmployee b)) return false;
            return Objects.equals(id, b.id);
        }
        // hashCode NOT overridden -> identity hash -> HashMap/HashSet break
    }

    public static void main(String[] args) {
        System.out.println("-- correct: equals + hashCode --");
        GoodEmployee g1 = new GoodEmployee("E001");
        GoodEmployee g2 = new GoodEmployee("E001");
        HashSet<GoodEmployee> goodSet = new HashSet<>();
        goodSet.add(g1);
        goodSet.add(g2);
        System.out.println("set size after g1 and g2 (same id): " + goodSet.size() + "  <- 1");
        HashMap<GoodEmployee, String> goodMap = new HashMap<>();
        goodMap.put(g1, "Senior Dev");
        System.out.println("lookup by g2 (same id as g1): " + goodMap.get(g2) + "  <- found");

        System.out.println("\n-- BROKEN: equals without hashCode --");
        BrokenEmployee b1 = new BrokenEmployee("B001");
        BrokenEmployee b2 = new BrokenEmployee("B001");
        System.out.println("b1.equals(b2):          " + b1.equals(b2));
        System.out.println("hash codes are equal:   " + (b1.hashCode() == b2.hashCode()) + "  <- identity hashes differ");
        HashSet<BrokenEmployee> brokenSet = new HashSet<>();
        brokenSet.add(b1);
        brokenSet.add(b2);
        System.out.println("broken set size: " + brokenSet.size()
                + "  <- WRONG, expected 1 (different buckets)");
        HashMap<BrokenEmployee, String> brokenMap = new HashMap<>();
        brokenMap.put(b1, "Developer");
        System.out.println("lookup b2: " + brokenMap.get(b2)
                + "  <- null, cannot find despite equals returning true");
    }
}

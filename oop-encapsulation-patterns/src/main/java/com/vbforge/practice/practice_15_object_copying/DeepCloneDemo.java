package com.vbforge.practice.practice_15_object_copying;

import java.util.ArrayList;
import java.util.List;

/** A deep clone copies the mutable parts too, so the copies are independent. */
public class DeepCloneDemo {

    static class DeepProfile implements Cloneable {
        String username;
        List<String> tags;

        DeepProfile(String u, List<String> t) { username = u; tags = new ArrayList<>(t); }

        @Override
        public DeepProfile clone() {
            try {
                DeepProfile c = (DeepProfile) super.clone();
                c.tags = new ArrayList<>(this.tags);      // copy the mutable field
                return c;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public String toString() { return "Deep{" + username + ", tags=" + tags + "}"; }
    }

    public static void main(String[] args) {
        DeepProfile dp1 = new DeepProfile("bob", List.of("java", "oop"));
        DeepProfile dp2 = dp1.clone();

        dp2.tags.add("safe");
        System.out.println("dp1: " + dp1);
        System.out.println("dp2: " + dp2);
    }
}

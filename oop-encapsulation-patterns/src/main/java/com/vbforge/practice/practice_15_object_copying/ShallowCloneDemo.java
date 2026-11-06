package com.vbforge.practice.practice_15_object_copying;

import java.util.ArrayList;
import java.util.List;

/** Cloneable + super.clone() is a SHALLOW copy: mutable fields are shared. */
public class ShallowCloneDemo {

    static class ShallowProfile implements Cloneable {
        String username;
        List<String> tags;

        ShallowProfile(String u, List<String> t) { username = u; tags = t; }

        @Override
        public ShallowProfile clone() {
            try {
                return (ShallowProfile) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public String toString() { return "Shallow{" + username + ", tags=" + tags + "}"; }
    }

    public static void main(String[] args) {
        List<String> shared = new ArrayList<>(List.of("java", "oop"));
        ShallowProfile sp1 = new ShallowProfile("alice", shared);
        ShallowProfile sp2 = sp1.clone();

        sp2.tags.add("hacked");
        System.out.println("sp1: " + sp1 + "   <- also changed");
        System.out.println("sp2: " + sp2);
    }
}

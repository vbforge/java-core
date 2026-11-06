package com.vbforge.practice.practice_15_object_copying;

import java.util.ArrayList;
import java.util.List;

/**
 * Preferred over clone() (Effective Java, Item 13): a copy constructor or a static copy factory.
 */
public class CopyConstructorAndFactoryDemo {

    static final class UserProfile {
        private final String username;
        private final List<String> tags;
        private final int score;

        UserProfile(String u, List<String> t, int s) { username = u; tags = new ArrayList<>(t); score = s; }

        UserProfile(UserProfile other) { this(other.username, other.tags, other.score); }   // copy constructor

        static UserProfile copyOf(UserProfile other) { return new UserProfile(other); }       // copy factory

        List<String> getTags() { return List.copyOf(tags); }

        @Override
        public String toString() { return "User{" + username + ", tags=" + tags + ", score=" + score + "}"; }
    }

    public static void main(String[] args) {
        UserProfile up1 = new UserProfile("carol", List.of("java", "clean-code"), 95);
        UserProfile up2 = new UserProfile(up1);
        UserProfile up3 = UserProfile.copyOf(up1);

        System.out.println("up1: " + up1);
        System.out.println("up2: " + up2);
        System.out.println("up3: " + up3);
        System.out.println("up1 == up2: " + (up1 == up2));
    }
}

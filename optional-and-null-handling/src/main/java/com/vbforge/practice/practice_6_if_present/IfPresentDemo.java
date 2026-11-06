package com.vbforge.practice.practice_6_if_present;

import com.vbforge.practice.practice_5_user_sample_data.UserSampleData;

import java.util.Optional;

/** ifPresent(Consumer): run an action only when a value exists (functional style, Java 8). */
public class IfPresentDemo {

    public static void main(String[] args) {
        Optional<String> name = Optional.of("John");
        Optional<String> empty = Optional.empty();

        name.ifPresent(System.out::println);
        name.ifPresent(v -> System.out.println("ifPresent: " + v));
        empty.ifPresent(v -> System.out.println("this won't print"));
        System.out.println("empty: ifPresent body skipped");

        UserSampleData.findUserById("U001")
                .ifPresent(u -> System.out.println("Found user: " + u.name()));
        UserSampleData.findUserById("U999")
                .ifPresent(u -> System.out.println("this won't print"));
        System.out.println("U999 not found: ifPresent body skipped");
    }
}

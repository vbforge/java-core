package com.vbforge.practice.practice_8_flat_map;

import com.vbforge.practice.practice_5_user_sample_data.User;
import com.vbforge.practice.practice_5_user_sample_data.UserSampleData;

import java.util.Optional;

/**
 * map vs flatMap when the function already returns an Optional.
 *
 * map(f)     with f returning Optional<U> gives Optional<Optional<U>> (nested).
 * flatMap(f) flattens one level and gives Optional<U>.
 */
public class FlatMapVsMapDemo {

    public static void main(String[] args) {
        Optional<User> alice = UserSampleData.findUserById("U001");

        Optional<Optional<String>> nested = alice.map(UserSampleData::emailOf);
        Optional<String> flat = alice.flatMap(UserSampleData::emailOf);

        System.out.println("map gives nested:   " + nested);
        System.out.println("flatMap gives flat: " + flat);
    }
}

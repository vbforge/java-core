package com.vbforge.practice.practice_7_map;

import com.vbforge.practice.practice_5_user_sample_data.User;
import com.vbforge.practice.practice_5_user_sample_data.UserSampleData;

/** Real pipeline: find a user, map to the name, fall back to a default. */
public class MapWithDefaultPipelineDemo {

    public static void main(String[] args) {
        for (String id : UserSampleData.IDS) {
            String name = UserSampleData.findUserById(id)
                    .map(User::name)
                    .orElse("Unknown user");
            System.out.println("  " + id + " -> " + name);
        }
    }
}

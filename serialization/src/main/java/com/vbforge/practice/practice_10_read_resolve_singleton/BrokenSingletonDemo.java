package com.vbforge.practice.practice_10_read_resolve_singleton;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** A Serializable singleton WITHOUT readResolve is no longer a singleton: deserialization creates a second instance. */
public class BrokenSingletonDemo {

    static class AppConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        static final AppConfig INSTANCE = new AppConfig();

        private AppConfig() {
        }
    }

    public static void main(String[] args) throws Exception {
        AppConfig copy = SerializationHelper.roundTrip(AppConfig.INSTANCE);
        System.out.println("copy == INSTANCE: " + (copy == AppConfig.INSTANCE));
    }
}

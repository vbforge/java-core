package com.vbforge.practice.practice_10_read_resolve_singleton;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

/** Enum constants keep their identity automatically (they are serialized by name): no readResolve needed. */
public class EnumSingletonDemo {

    enum Registry {
        INSTANCE
    }

    public static void main(String[] args) throws Exception {
        Registry copy = SerializationHelper.roundTrip(Registry.INSTANCE);
        System.out.println("copy == INSTANCE: " + (copy == Registry.INSTANCE));
    }
}

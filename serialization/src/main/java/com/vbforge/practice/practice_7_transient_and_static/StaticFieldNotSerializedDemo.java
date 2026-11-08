package com.vbforge.practice.practice_7_transient_and_static;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** static fields belong to the class, not to the object: they are never serialized, reading shows the CURRENT value. */
public class StaticFieldNotSerializedDemo {

    static class Settings implements Serializable {
        private static final long serialVersionUID = 1L;
        static String theme = "light";
        String owner = "ann";
    }

    public static void main(String[] args) throws Exception {
        byte[] data = SerializationHelper.serialize(new Settings());

        Settings.theme = "dark"; // changed AFTER writing
        Settings copy = SerializationHelper.deserialize(data);

        System.out.println("owner (instance field): " + copy.owner);
        System.out.println("theme (static field):   " + Settings.theme + "  <- not restored from the stream");
    }
}

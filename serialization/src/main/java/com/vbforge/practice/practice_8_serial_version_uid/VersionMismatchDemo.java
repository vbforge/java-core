package com.vbforge.practice.practice_8_serial_version_uid;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.InvalidClassException;
import java.io.Serializable;

/**
 * A stream written by another version of the class is rejected with InvalidClassException.
 * We simulate "another version" by patching the 8 version bytes (7L -> 8L) inside the written stream.
 */
public class VersionMismatchDemo {

    static class Document implements Serializable {
        private static final long serialVersionUID = 7L;
        String title = "draft";
    }

    private static int indexOf(byte[] data, byte[] pattern) {
        for (int i = 0; i <= data.length - pattern.length; i++) {
            boolean match = true;
            for (int j = 0; j < pattern.length && match; j++) {
                match = data[i + j] == pattern[j];
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) throws Exception {
        byte[] data = SerializationHelper.serialize(new Document());

        byte[] uid7 = {0, 0, 0, 0, 0, 0, 0, 7};
        int at = indexOf(data, uid7);
        System.out.println("version bytes found in the stream: " + (at >= 0));
        data[at + 7] = 8; // pretend the writer had serialVersionUID = 8L

        try {
            SerializationHelper.deserialize(data);
        } catch (InvalidClassException e) {
            System.out.println("InvalidClassException: " + e.getMessage().replaceAll("^.*Document; ", ""));
        }
    }
}

package com.vbforge.practice.practice_1_serialization_helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Shared fixture, not a concept: in-memory round trip helpers used by the other practice packages.
 * Streams are closed BEFORE the bytes are taken, so buffered data is never lost.
 */
public final class SerializationHelper {

    private SerializationHelper() {
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(obj);
        }
        return bytes.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) in.readObject();
        }
    }

    public static <T> T roundTrip(T obj) throws IOException, ClassNotFoundException {
        return deserialize(serialize(obj));
    }

    /** First n bytes as hex, e.g. "AC ED 00 05". */
    public static String hexPrefix(byte[] data, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n && i < data.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(String.format("%02X", data[i] & 0xFF));
        }
        return sb.toString();
    }
}

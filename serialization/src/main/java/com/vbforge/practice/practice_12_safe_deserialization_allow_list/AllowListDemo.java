package com.vbforge.practice.practice_12_safe_deserialization_allow_list;

import com.vbforge.practice.practice_12_safe_deserialization_allow_list.AllowListObjectInputStream;
import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.ByteArrayInputStream;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * Never deserialize bytes you do not trust: the stream chooses which classes get instantiated.
 * An allow-list stops unexpected classes before any object is created.
 * (For data exchange with other systems prefer a data format such as JSON.)
 */
public class AllowListDemo {

    static class Message implements Serializable {
        private static final long serialVersionUID = 1L;
        String text = "hello";
    }

    static class Unexpected implements Serializable {
        private static final long serialVersionUID = 1L;
        String payload = "surprise";
    }

    private static Object readAllowed(byte[] data, Set<String> allowed) throws Exception {
        try (AllowListObjectInputStream in = new AllowListObjectInputStream(new ByteArrayInputStream(data), allowed)) {
            return in.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        Set<String> allowed = Collections.singleton(Message.class.getName());

        Object ok = readAllowed(SerializationHelper.serialize(new Message()), allowed);
        System.out.println("allowed class read: " + ((Message) ok).text);

        try {
            readAllowed(SerializationHelper.serialize(new Unexpected()), allowed);
            System.out.println("unexpected class read (should not happen)");
        } catch (InvalidClassException e) {
            System.out.println("blocked: " + e.getMessage());
        }
    }
}

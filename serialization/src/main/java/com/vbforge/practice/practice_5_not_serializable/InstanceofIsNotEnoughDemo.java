package com.vbforge.practice.practice_5_not_serializable;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** "x instanceof Serializable" says nothing about the CONTENT: an ArrayList is Serializable, its elements may not be. */
public class InstanceofIsNotEnoughDemo {

    public static void main(String[] args) throws Exception {
        List<Object> list = new ArrayList<>(Arrays.asList("ok", new Object()));
        System.out.println("list instanceof Serializable: " + (list instanceof Serializable));

        try {
            SerializationHelper.serialize(list);
            System.out.println("serialized (unexpected)");
        } catch (NotSerializableException e) {
            System.out.println("writeObject failed anyway: NotSerializableException " + e.getMessage());
        }

        List<Object> good = new ArrayList<>(Arrays.asList("ok", 1, 2.5));
        System.out.println("list of Strings/numbers serializes: " + (SerializationHelper.serialize(good).length > 0));
    }
}
